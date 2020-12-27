package com.snx.ImageProcess;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.amazonaws.services.s3.AmazonS3;
import com.google.common.collect.ImmutableMap;
import com.snx.ImageProcess.dao.AwsConfig;
import com.snx.ImageProcess.dao.DaoRepository;
import com.snx.ImageProcess.object.Image;
import com.snx.ImageProcess.object.UpdateImageInput;
import com.snx.ImageProcess.service.MutationResolver;
import com.snx.ImageProcess.service.QueryResolver;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingEnvironmentBuilder;
import graphql.schema.DataFetchingEnvironmentImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.*;

import static org.mockito.Mockito.*;

@SpringBootTest
class ImageProcessApplicationTests {

    @Mock
    private DaoRepository daoRepository;
    @Mock
    private DataFetchingEnvironment environment;
    @Mock
    private MultipartFile file;
    @Spy
    @InjectMocks
    private MutationResolver mutationResolver;
    @Spy
    @InjectMocks
    private QueryResolver queryResolver;
    @Mock
    private AwsConfig awsConfig;
    @Mock
    private AmazonS3 s3Client;
    @Mock
    private DynamoDBMapper dbMapper;
    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testDeleteImage(){
        Image image = new Image();
        when(daoRepository.getImage(anyString(), anyString())).thenReturn(image);
        when(daoRepository.s3DeleteImage(anyString())).thenReturn(true);
        doNothing().when(daoRepository).deleteImage(image);
        when(mutationResolver.deleteImage("1223", "sdds")).thenCallRealMethod();
        verify(mutationResolver, times(2)).deleteImage("1223", "sdds");
    }
    @Test
    public void testDeleteImages(){
        List<Image> list = new ArrayList<>();
        Image image1 = Image.builder()
                .id("1234").time(new Date()).name("WOEJI").filterName("origin").build();
        image1.setS3Key(image1.getId()+image1.getName()+image1.getFilterName());
        list.add(image1);
        Image image2 = Image.builder()
                .id("1234").time(new Date()).name("Wsd").filterName("MyGray").build();
        image2.setS3Key(image2.getId()+image2.getName()+image2.getFilterName());
        list.add(image2);
        when(daoRepository.getImages(anyString())).thenReturn(list);
        when(daoRepository.s3DeleteImage(anyString())).thenReturn(true);
        doNothing().when(daoRepository).deleteImage(new Image());
        when(mutationResolver.deleteImages("1234")).thenCallRealMethod();
        verify(mutationResolver).deleteImages("1234");
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(mutationResolver).deleteImages(argument.capture());
        Assertions.assertEquals("1234", argument.getValue());
    }
    @Test
    public void testSaveOriginImage() throws IOException {
        doThrow(new IOException("io")).when(daoRepository).s3UploadImage(anyString(), any());
        byte[] bytes = new byte[100];
        for (int i = 0; i < 99; i++) {
            bytes[i] = 1;
        }
        when(environment.getArgument("image")).thenReturn(bytes);
        try{
            when(mutationResolver.saveOriginImage("sjdi", bytes, environment)).thenCallRealMethod();
        }catch (IOException e){
            Assertions.assertEquals("io", e.getMessage());
            verify(mutationResolver).saveOriginImage("sjdi", bytes, environment);
        }
    }
    @Test
    public void testUpdateImage(){
        doThrow(new RuntimeException("s3")).when(daoRepository).s3CopyImage(anyString(), anyString());
        UpdateImageInput input = UpdateImageInput.builder()
                .filterName("Grayscale")
                .id("b7f703a9-84f0-41d7-83a4-78ce78f98f4f")
                .name("sd")
                .newName("dpd").build();
        Image image = Image.builder().filterName("Grayscale")
                .id("b7f703a9-84f0-41d7-83a4-78ce78f98f4f")
                .build();
        when(daoRepository.getImage(anyString(), anyString())).thenReturn(image);
        try {
            when(mutationResolver.updateImage(input)).thenCallRealMethod();
        } catch (RuntimeException | IOException e) {
            Assertions.assertEquals("s3", e.getMessage());
        }
    }

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

}
