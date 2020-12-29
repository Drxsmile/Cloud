package com.snx.ImageProcess;

import com.snx.ImageProcess.dao.DaoRepository;
import com.snx.ImageProcess.object.Image;
import com.snx.ImageProcess.object.UpdateImageInput;
import com.snx.ImageProcess.service.MutationResolver;
import graphql.schema.DataFetchingEnvironment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest
class ImageProcessApplicationTests {

    @Mock
    private DaoRepository daoRepository;
    @Mock
    private DataFetchingEnvironment environment;
    @Spy
    @InjectMocks
    private MutationResolver mutationResolver;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testDeleteImageWithS3DeleteSuccess() {
        Image image = Image.builder()
                .id("1223")
                .name("sdds")
                .time(new Date())
                .filterName("origin")
                .build();
        image.setS3Key(image.getId() + image.getName() + image.getFilterName());
        doReturn(image).when(daoRepository).getImage(anyString(), anyString());
        doReturn(true).when(daoRepository).s3DeleteImage(anyString());
        Boolean res = mutationResolver.deleteImage("1223", "sdds");
        Assertions.assertEquals(true, res);
        verify(daoRepository, times(1)).deleteImage(image);
    }

    @Test
    public void testDeleteImageWithS3DeleteFailed() {
        Image image = Image.builder()
                .id("1223")
                .name("sdds")
                .time(new Date())
                .filterName("origin")
                .build();
        image.setS3Key(image.getId() + image.getName() + image.getFilterName());
        doReturn(image).when(daoRepository).getImage(anyString(), anyString());
        doReturn(false).when(daoRepository).s3DeleteImage(anyString());
        Boolean res = mutationResolver.deleteImage("1223", "sdds");
        Assertions.assertEquals(false, res);
        verify(daoRepository, times(0)).deleteImage(image);
    }

    @Test
    public void testDeleteImagesWithS3DeleteSuccess() {
        List<Image> list = new ArrayList<>();
        Image image1 = Image.builder()
                .id("1234").time(new Date()).name("WOEJI").filterName("origin").build();
        image1.setS3Key(image1.getId() + image1.getName() + image1.getFilterName());
        list.add(image1);
        Image image2 = Image.builder()
                .id("1234").time(new Date()).name("Wsd").filterName("MyGray").build();
        image2.setS3Key(image2.getId() + image2.getName() + image2.getFilterName());
        list.add(image2);
        doReturn(list).when(daoRepository).getImages(anyString());
        doReturn(true).when(daoRepository).s3DeleteImage(anyString());
        Boolean isDeleted = mutationResolver.deleteImages("1234");
        Assertions.assertEquals(isDeleted, true);
        verify(daoRepository, times(list.size())).deleteImage(any());
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(mutationResolver).deleteImages(argument.capture());
        Assertions.assertEquals("1234", argument.getValue());
    }

    @Test
    public void testDeleteImagesWithS3DeleteFailed() {
        List<Image> list = new ArrayList<>();
        Image image1 = Image.builder()
                .id("1234").time(new Date()).name("WOEJI").filterName("origin").build();
        image1.setS3Key(image1.getId() + image1.getName() + image1.getFilterName());
        list.add(image1);
        Image image2 = Image.builder()
                .id("1234").time(new Date()).name("Wsd").filterName("MyGray").build();
        image2.setS3Key(image2.getId() + image2.getName() + image2.getFilterName());
        list.add(image2);
        doReturn(list).when(daoRepository).getImages(anyString());
        doReturn(false).when(daoRepository).s3DeleteImage(anyString());
        Boolean isDeleted = mutationResolver.deleteImages("1234");
        Assertions.assertEquals(isDeleted, false);
        verify(daoRepository, times(0)).deleteImage(any());
    }

    @Test
    public void testSaveOriginImageWithoutException() throws IOException {
        doNothing().when(daoRepository).s3UploadImage(anyString(), any());
        doNothing().when(daoRepository).saveImage(any());
        byte[] bytes = new byte[100];
        for (int i = 0; i < 99; i++) {
            bytes[i] = 1;
        }
        doReturn(bytes).when(environment).getArgument("image");
        Image img = mutationResolver.saveOriginImage("sjdi", bytes, environment);
        verify(daoRepository, times(1)).s3UploadImage(any(), any());
        verify(daoRepository, times(1)).saveImage(any());
        verify(daoRepository, times(0)).s3DeleteImage(any());
        Assertions.assertEquals("origin", img.getFilterName());
    }

    @Test
    public void testSaveOriginImageWithS3UploadException() throws IOException {
        doThrow(new IOException("io")).when(daoRepository).s3UploadImage(anyString(), any());
        byte[] bytes = new byte[100];
        for (int i = 0; i < 99; i++) {
            bytes[i] = 1;
        }
        Image img = Image.builder().id("3333").build();
        doReturn(bytes).when(environment).getArgument("image");
        try {
            img = mutationResolver.saveOriginImage("sjdi", bytes, environment);
        } catch (IOException e) {
            Assertions.assertEquals("io", e.getMessage());
        } finally {
            Assertions.assertEquals(null, img);
            verify(daoRepository, times(1)).s3UploadImage(any(), any());
            verify(daoRepository, times(0)).saveImage(any());
            verify(daoRepository, times(0)).s3DeleteImage(any());
        }
    }
    @Test
    public void testSaveOriginImageWithDBSaveException() throws IOException {
        doNothing().when(daoRepository).s3UploadImage(anyString(), any());
        byte[] bytes = new byte[100];
        for (int i = 0; i < 99; i++) {
            bytes[i] = 1;
        }
        doReturn(bytes).when(environment).getArgument("image");
        doThrow(new RuntimeException("DB")).when(daoRepository).saveImage(any());
        Image img = Image.builder().id("3333").build();
        try {
            img = mutationResolver.saveOriginImage("sjdi", bytes, environment);
        } catch (RuntimeException e) {
            Assertions.assertEquals("DB", e.getMessage());
        } finally {
            Assertions.assertEquals(null, img);
            verify(daoRepository, times(1)).s3UploadImage(any(), any());
            verify(daoRepository, times(1)).saveImage(any());
            verify(daoRepository, times(1)).s3DeleteImage(any());
        }
    }

    @Test
    public void testUpdateImageWithS3CopyException() throws IOException {
        doThrow(new RuntimeException("s3")).when(daoRepository).s3CopyImage(any(), any());
        UpdateImageInput input = UpdateImageInput.builder()
                .filterName("Grayscale")
                .id("b7f703a9-84f0-41d7-83a4-78ce78f98f4f")
                .name("sd")
                .newName("dpd").build();
        Image image = Image.builder()
                .id("b7f703a9-84f0-41d7-83a4-78ce78f98f4f")
                .build();
        doReturn(image).when(daoRepository).getImage(anyString(), anyString());
        image.setFilterName("Grayscale");
        Image img = Image.builder().id("3333").build();
        try {
            img = mutationResolver.updateImage(input);
        } catch (RuntimeException | IOException e) {
            Assertions.assertEquals("s3", e.getMessage());
        } finally {
            Assertions.assertEquals(null, img);
            verify(daoRepository, times(1)).s3CopyImage(any(), any());
            verify(daoRepository, times(0)).applyFilter(any(), any());
            verify(daoRepository, times(0)).s3download(any());
            verify(daoRepository, times(0)).s3UploadImage(any(), any());
            verify(daoRepository, times(0)).saveImage(any());
            verify(daoRepository, times(0)).s3DeleteImage(any());
        }
    }

    @Test
    public void testUpdateImageWithS3DownloadException() throws IOException {
        UpdateImageInput input = UpdateImageInput.builder()
                .filterName("Grayscale")
                .id("b7f703a9-84f0-41d7-83a4-78ce78f98f4f")
                .name("sd")
                .newName("dpd").build();
        Image image = Image.builder()
                .id("b7f703a9-84f0-41d7-83a4-78ce78f98f4f")
                .build();
        doReturn(image).when(daoRepository).getImage(anyString(), anyString());
        image.setFilterName("MyGray");
        Image img = Image.builder().id("3333").build();
        doThrow(new RuntimeException("s3")).when(daoRepository).s3download(any());
        try {
            img = mutationResolver.updateImage(input);
        } catch (RuntimeException | IOException e) {
            Assertions.assertEquals("s3", e.getMessage());
        } finally {
            Assertions.assertEquals(null, img);
            verify(daoRepository, times(0)).s3CopyImage(any(), any());
            verify(daoRepository, times(0)).applyFilter(any(), any());
            verify(daoRepository, times(1)).s3download(any());
            verify(daoRepository, times(0)).s3UploadImage(any(), any());
            verify(daoRepository, times(0)).saveImage(any());
            verify(daoRepository, times(0)).s3DeleteImage(any());
        }
    }
    @Test
    public void testUpdateImageWithDBSaveException() throws IOException {
        UpdateImageInput input = UpdateImageInput.builder()
                .filterName("Grayscale")
                .id("b7f703a9-84f0-41d7-83a4-78ce78f98f4f")
                .name("sd")
                .newName("dpd").build();
        Image image = Image.builder()
                .id("b7f703a9-84f0-41d7-83a4-78ce78f98f4f")
                .build();
        doReturn(image).when(daoRepository).getImage(anyString(), anyString());
        image.setFilterName("MyGray");
        Image img = Image.builder().id("3333").build();
        doThrow(new RuntimeException("s3")).when(daoRepository).saveImage(any());
        try {
            img = mutationResolver.updateImage(input);
        } catch (RuntimeException | IOException e) {
            Assertions.assertEquals("s3", e.getMessage());
        } finally {
            Assertions.assertEquals(null, img);
            verify(daoRepository, times(0)).s3CopyImage(any(), any());
            verify(daoRepository, times(1)).applyFilter(any(), any());
            verify(daoRepository, times(1)).s3download(any());
            verify(daoRepository, times(1)).s3UploadImage(any(), any());
            verify(daoRepository, times(1)).saveImage(any());
            verify(daoRepository, times(1)).s3DeleteImage(any());
        }
    }

}
