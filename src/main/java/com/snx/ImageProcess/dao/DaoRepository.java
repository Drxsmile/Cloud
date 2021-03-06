package com.snx.ImageProcess.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.amazonaws.services.s3.AmazonS3;
import com.google.common.collect.ImmutableMap;
import com.jhlabs.image.GrayscaleFilter;
import com.snx.ImageProcess.object.Image;
import com.snx.ImageProcess.object.filter.BlackWhite;
import com.snx.ImageProcess.object.filter.Film;
import com.snx.ImageProcess.object.filter.MyFilter;
import com.snx.ImageProcess.object.filter.MyGray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DaoRepository {
    @Autowired
    private AwsConfig awsConfig;
    @Autowired
    private AmazonS3 s3Client;
    @Autowired
    private DynamoDBMapper dbMapper;
    // 图片的格式
    private static final Map<String, MyFilter> FILTER_MAP = new HashMap<String, MyFilter>() {{
        put("MyGray", new MyGray());
        put("BlackWhite", new BlackWhite());
        put("Film", new Film());
    }};

    // aws s3 operations
    public void s3UploadImage(String key, BufferedImage bi) throws IOException {
        File image = new File("temp.jpeg");
        ImageIO.write(bi, "jpeg", image);
        s3Client.putObject(awsConfig.getBucketName(), key, image);
        image.delete();
    }

    public boolean s3DeleteImage(String key) {
        if (s3Client.doesObjectExist(awsConfig.getBucketName(), key)) {
            s3Client.deleteObject(awsConfig.getBucketName(), key);
            return true;
        } else {
            return false;
        }
    }

    public BufferedImage s3download(String key) throws IOException {
        if (s3Client.doesObjectExist(awsConfig.getBucketName(), key)) {
            URL url = s3Client.getUrl(awsConfig.getBucketName(), key);
            return ImageIO.read(url);
        } else {
            throw new IOException("Wrong s3 key");
        }
    }

    public void s3CopyImage(String key, String des) {
        s3Client.copyObject(awsConfig.getBucketName(), key, awsConfig.getBucketName(), des);
    }

    // aws dynamodb operations
    public void saveImage(Image image) {

        DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression();
        saveExpression.setExpected(new ImmutableMap.Builder()
                .put("id", new ExpectedAttributeValue(false))
                .put("name", new ExpectedAttributeValue(false))
                .put("s3Key", new ExpectedAttributeValue(false))
                .put("time", new ExpectedAttributeValue(false))
                .put("filterName", new ExpectedAttributeValue(false))
                .build());
        try {
            dbMapper.save(image, saveExpression);
        } catch (Exception e) {
            throw e;
        }
    }

    public Image getImage(String id, String name) {
        return dbMapper.load(Image.class, id, name);
    }

    public List<Image> getImages(String id) {
        Image image = new Image();
        image.setId(id);
        DynamoDBQueryExpression<Image> queryExpression = new DynamoDBQueryExpression()
                .withHashKeyValues(image);
        List<Image> latestReplies = dbMapper.query(Image.class, queryExpression);
        return latestReplies;
    }

    public Image getImageByGSI(String gsi, String gsiRangeKey, String gsiHashKey) {
        HashMap<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":v1", new AttributeValue().withS(gsiHashKey));
        eav.put(":v2", new AttributeValue().withS(gsiRangeKey));
        DynamoDBQueryExpression<Image> queryExpression = new DynamoDBQueryExpression()
                .withIndexName(gsi)
                .withConsistentRead(false)
                .withKeyConditionExpression("filterName = :v1 and id = :v2")
                .withExpressionAttributeValues(eav);
        List<Image> latestReplies = dbMapper.query(Image.class, queryExpression);
        return latestReplies.get(0);
    }

    public List<Image> getImagesByGSI(String gsi, String gsiHashKey) {
        HashMap<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":v1", new AttributeValue().withS(gsiHashKey));
        DynamoDBQueryExpression<Image> queryExpression = new DynamoDBQueryExpression()
                .withIndexName(gsi)
                .withConsistentRead(false)
                .withKeyConditionExpression("filterName = :v1")
                .withExpressionAttributeValues(eav);
        List<Image> latestReplies = dbMapper.query(Image.class, queryExpression);
        return latestReplies;
    }


    public void deleteImage(Image image) {
        dbMapper.delete(image);
    }

    public BufferedImage applyFilter(BufferedImage image, String filterName) throws IOException {
        if (filterName.equals("Grayscale")) {
            GrayscaleFilter grayscaleFilter = new GrayscaleFilter();
            return grayscaleFilter.filter(image, null);
        } else {
            if (!FILTER_MAP.containsKey(filterName)) {
                throw new IOException("No such Filter yet");
            } else {
                return FILTER_MAP.get(filterName).applyMyFilter(image);
            }
        }
    }

}
