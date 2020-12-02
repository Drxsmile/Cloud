package com.snx.ImageProcess.service;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.snx.ImageProcess.dao.DaoRepository;
import com.snx.ImageProcess.object.Image;
import com.snx.ImageProcess.object.UpdateImageInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Component
public class MutationResolver implements GraphQLMutationResolver {
    @Autowired
    private DaoRepository dao;

    public Image saveOriginImage(String name, String imagePath) throws IOException {
        String key = dao.s3UploadImage(imagePath);
        Image image = new Image();
        image.setS3Key(key);
        image.setName(name);
        image.setFilterName("origin");
        dao.saveImage(image);
        return image;
    }

    public Image updateImage(UpdateImageInput input) throws IOException {
        Image image = dao.getImage(input.getId(), input.getTime());
        String filterName = input.getFilterName();
        String name = input.getName();
        if(!filterName.equals(null)){
            String key = image.getS3Key();
            image.setFilterName(filterName);
            BufferedImage filteredImage = dao.applyFilter(dao.s3download(key), filterName);
            File file = new File("temp.png");
            ImageIO.write(filteredImage, "png", file);
            String newKey = dao.s3UploadImage("temp.png");
            image.setS3Key(newKey);
            file.delete();
            if(!input.getSaveAnother())  dao.s3DeleteImage(key);
        }
        if(!name.equals(null)) image.setName(name);
        if (input.getSaveAnother())  image.setTime(new Date());
        dao.saveImage(image);
        return image;
    }

    public Boolean deleteImage(String id, String time) {
        Image image = dao.getImage(id, time);
        dao.s3DeleteImage(image.getS3Key());
        dao.deleteImage(image);
        return true;
    }

    public Boolean deleteImages(String id) {
        //TODO
//        List<Image> images = dao.getImages(id);
        return true;
    }
}


