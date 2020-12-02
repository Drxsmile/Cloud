package com.snx.ImageProcess.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.amazonaws.services.dynamodbv2.xspec.L;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.google.common.collect.ImmutableMap;
import com.jhlabs.image.BlockFilter;
import com.jhlabs.image.GaussianFilter;
import com.jhlabs.image.GrayscaleFilter;
import com.snx.ImageProcess.object.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class DaoRepository {
    @Autowired
    private AwsConfig awsConfig;
    @Autowired
    private AmazonDynamoDB dbClient;
    @Autowired
    private AmazonS3 s3Client;
    @Autowired
    private DynamoDBMapper dbMapper;
    // 图片的格式
    private static final String[] IMAGE_TYPE = new String[]{".bmp", ".jpg", ".jpeg", ".gif", ".png"};
    private static final String[] FILTER_NAME = new String[]{"GaussianBlur", "Grayscale", "Mosaic"};


    // aws s3 operations
    public String s3UploadImage(String imagePath) throws IOException {
        File image = new File(imagePath);
        Boolean isLegal = false;
        for (String type : IMAGE_TYPE) {
            if (StringUtils.endsWithIgnoreCase(image.getName(), type)) {
                isLegal = true;
                break;
            }
        }
        if (isLegal) {
            UUID uuid = UUID.randomUUID();
            String key = uuid.toString();
            s3Client.putObject(awsConfig.getBucketName(), key, image);
            return key;
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
            e.printStackTrace();
        }
    }

    public Image getImage(String id, String time) {
        return dbMapper.load(Image.class, id, time);
    }

    public void getImages(String id) {
        ArrayList<Image> itemsToGet = new ArrayList<>();
        Image image = new Image();
        image.setId(id);
        itemsToGet.add(image);
        Map<String, List<Object>> load = dbMapper.batchLoad(itemsToGet);
        System.out.println(load);
//        List<Image> res = load.get(id);
        //todo
    }

    public void updateImage(Image image) {

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
        } else {
            throw new IOException("No such Filter yet");
        }
    }
}
