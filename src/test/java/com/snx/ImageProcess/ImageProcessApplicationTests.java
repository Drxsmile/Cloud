package com.snx.ImageProcess;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.google.common.collect.ImmutableMap;
import com.snx.ImageProcess.dao.AwsConfig;
import com.snx.ImageProcess.dao.DaoRepository;
import com.snx.ImageProcess.object.Image;
import com.snx.ImageProcess.service.MutationResolver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

@SpringBootTest
class ImageProcessApplicationTests {

    @Test
    void contextLoads() {

    }


//    @Autowired
//    AwsConfig awsConfig;
//    @Autowired
//    private DaoRepository daoRepository;
//
//    @Test
//    void testS3UploadImage() throws IOException {
//        String path = "/Users/s/Desktop/腹肌小孩/timg.jpeg";
//        System.out.println(daoRepository.s3UploadImage(path));
//		System.out.println(awsConfig.s3Client().getRegion());
//    }

//    @Test
//    void testSaveImage() {
//        Date date = new Date();
//        Image image = new Image("4", "i1", "234", date, "origin");
//        daoRepository.saveImage(image);
//    }

//    @Test
//    void myTest() {
//        String accessKey = awsConfig.getAccessKey();
//        String secretKey = "QAy5peDdO/uv+1jk614KsXArsjJp2hsiZdrKrLvE";
//		System.out.println(accessKey);
//		System.out.println(secretKey);

//		System.out.println(dbClient.listTables());
//        DynamoDBMapper dbMapper = new DynamoDBMapper(awsConfig.dbClient());
//        DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression();
//        saveExpression.setExpected(new ImmutableMap.Builder()
//                .put("id", new ExpectedAttributeValue(false))
//                .put("name", new ExpectedAttributeValue(false))
//                .put("s3Key", new ExpectedAttributeValue(false))
//                .put("time", new ExpectedAttributeValue(false))
//                .put("filterName", new ExpectedAttributeValue(false))
//                .build());
//        if (daoRepository.getImage("12", "i2") != null) {
//            System.out.println("ss");
//            return;
//        }
//        Image image = new Image("12", "i2", "234", new Date(), "origin");
//        dbMapper.save(image, saveExpression);
//		DynamoDBQueryExpression queryExpression = new DynamoDBQueryExpression();
//		dbMapper.query(image, queryExpression);
//    }

//    @Test
//    void testImageFilter() throws IOException {
//        String path = "/Users/s/Desktop/腹肌小孩/timg.jpeg";
//        BufferedImage image = ImageIO.read(new FileInputStream(path));
//        BufferedImage dst = daoRepository.applyFilter(image, "MyGray");
//        File outputfile = new File("/Users/s/Desktop/腹肌小孩/save2.jpeg");
//        ImageIO.write(dst, "jpeg", outputfile);
//    }
//
//    @Test
//    void testGetImages() throws ParseException {
//        List<Image> images = daoRepository.getImages("12");
//        for (Image i : images) {
//            System.out.println(i);
//        }
//        System.out.println(images.get(0).getTime());
//    }
//
//    @Autowired
//    private MutationResolver mutationResolver;

//    @Test
//    void testUpdateImage() throws ParseException, IOException {
//        String path = "/Users/s/Desktop/腹肌小孩/timg.jpeg";
////        Image image = mutationResolver.saveOriginImage("sd", path);
//        UpdateImageInput input = UpdateImageInput.builder()
//                .filterName("Grayscale")
//                .id("b7f703a9-84f0-41d7-83a4-78ce78f98f4f")
//                .name("sd")
//                .newName("dpd").build();
////        mutationResolver.updateImage(input);
//        mutationResolver.deleteImage("b7f703a9-84f0-41d7-83a4-78ce78f98f4f", "dwd");
//    }
}
