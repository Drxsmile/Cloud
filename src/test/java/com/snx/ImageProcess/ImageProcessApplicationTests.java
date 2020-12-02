package com.snx.ImageProcess;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.google.common.collect.ImmutableMap;
import com.jhlabs.image.GrayFilter;
import com.snx.ImageProcess.dao.AwsConfig;
import com.snx.ImageProcess.dao.DaoRepository;
import com.snx.ImageProcess.object.Image;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

@SpringBootTest
class ImageProcessApplicationTests {

    @Test
    void contextLoads() {

    }


    @Autowired
    AwsConfig awsConfig;
    @Autowired
    private DaoRepository daoRepository;

    @Test
    void testS3UploadImage() throws IOException {
        String path = "/Users/s/Desktop/腹肌小孩/timg.jpeg";
        System.out.println(daoRepository.s3UploadImage(path));
//		System.out.println(awsConfig.s3Client().getRegion());
    }

    @Test
    void testSaveImage() {
        Date date = new Date();
        Image image = new Image("4", "i1", "234", date, "origin");
        daoRepository.saveImage(image);
    }

    @Test
    void myTest() {
        String accessKey = awsConfig.getAccessKey();
        String secretKey = "QAy5peDdO/uv+1jk614KsXArsjJp2hsiZdrKrLvE";
//		System.out.println(accessKey);
//		System.out.println(secretKey);

//		System.out.println(dbClient.listTables());
        DynamoDBMapper dbMapper = new DynamoDBMapper(awsConfig.dbClient());
        DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression();
        saveExpression.setExpected(new ImmutableMap.Builder()
                .put("id", new ExpectedAttributeValue(false))
                .put("name", new ExpectedAttributeValue(false))
                .put("s3Key", new ExpectedAttributeValue(false))
                .put("time", new ExpectedAttributeValue(false))
                .put("filterName", new ExpectedAttributeValue(false))
                .build());
        Image image = new Image("12", "i1", "234", new Date(), "origin");
        dbMapper.save(image, saveExpression);
//		DynamoDBQueryExpression queryExpression = new DynamoDBQueryExpression();
//		dbMapper.query(image, queryExpression);
    }

    @Test
    void testImageFilter() throws IOException {
        String path = "/Users/s/Desktop/腹肌小孩/timg.jpeg";
        BufferedImage image = ImageIO.read(new FileInputStream(path));
        GrayFilter grayFilter = new GrayFilter();
        BufferedImage dst = grayFilter.filter(image, null);
        File outputfile = new File("/Users/s/Desktop/腹肌小孩/saeve.png");
        ImageIO.write(dst, "png", outputfile);
    }
    @Test
    void testGetImages(){
        daoRepository.getImages("1");
    }
    @Test
    void testUpdateImage(){

    }
}
