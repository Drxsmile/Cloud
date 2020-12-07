package com.snx.ImageProcess.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.amazonaws.services.s3.AmazonS3;
import com.google.common.collect.ImmutableMap;
import com.jhlabs.image.BlockFilter;
import com.jhlabs.image.GaussianFilter;
import com.jhlabs.image.GrayscaleFilter;
import com.snx.ImageProcess.object.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

@Component
public class DaoRepository {
    @Autowired
    private AwsConfig awsConfig;
    @Autowired
    private AmazonS3 s3Client;
    @Autowired
    private DynamoDBMapper dbMapper;
    // 图片的格式
    private static final String[] IMAGE_TYPE = new String[]{".bmp", ".jpg", ".jpeg", ".gif", ".png"};
    private static final String[] FILTER_NAME = new String[]{"GaussianBlur", "Grayscale", "Mosaic", "MyGray"};


    // aws s3 operations
    public void s3UploadImage(String key, String imagePath) throws IOException {
        File image = new File(imagePath);
        Boolean isLegal = false;
        for (String type : IMAGE_TYPE) {
            if (StringUtils.endsWithIgnoreCase(image.getName(), type)) {
                isLegal = true;
                break;
            }
        }
        if (isLegal) {
            s3Client.putObject(awsConfig.getBucketName(), key, image);
        } else {
            throw new IOException("The file is not a image");
        }
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
                .withKeyConditionExpression("filterName = :v1")
                .withExpressionAttributeValues(eav);
        List<Image> latestReplies = dbMapper.query(Image.class, queryExpression);
        return latestReplies;
    }


    public void deleteImage(Image image) {
        dbMapper.delete(image);
    }


    public BufferedImage applyFilter(BufferedImage image, String filterName) throws IOException {
        if (filterName.equals(FILTER_NAME[0])) {
            GaussianFilter gaussianFilter = new GaussianFilter();
            return gaussianFilter.filter(image, null);
        } else if (filterName.equals(FILTER_NAME[1])) {
            GrayscaleFilter grayscaleFilter = new GrayscaleFilter();
            return grayscaleFilter.filter(image, null);
        } else if (filterName.equals(FILTER_NAME[2])) {
            BlockFilter blockFilter = new BlockFilter();
            return blockFilter.filter(image, null);
        } else if (filterName.equals(FILTER_NAME[3])) {
            //todo: mygray


            return image;
        } else {
            throw new IOException("No such Filter yet");
        }
    }

    public BufferedImage myGray(BufferedImage image) {
        int height = image.getHeight();
        int width = image.getWidth();
        BufferedImage dst = new BufferedImage(width, height, image.getType());
        Graphics graphics = dst.getGraphics();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pixel = image.getRGB(j, i);
                Color color = new Color(pixel);
                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();
//                int ave = (int)(r * 0.3 + g * 0.59 + b * 0.11);
                int ave = (int) (r + g + b) / 3;
                System.out.println(ave);
                Color newColor = new Color(ave, ave, ave);
                graphics.setColor(newColor);
                graphics.fillOval(j, i, 1, 1);
            }
        }
        graphics.dispose();
        return dst;
    }
}
