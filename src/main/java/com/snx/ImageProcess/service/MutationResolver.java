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
import java.util.UUID;

@Component
public class MutationResolver implements GraphQLMutationResolver {
    @Autowired
    private DaoRepository dao;

    public Image saveOriginImage(String name, String imagePath) throws IOException {
        //        DefaultGraphQLServletContext context = env.getContext();
        UUID id = UUID.randomUUID();
        Image image = Image.builder()
                .id(id.toString())
                .filterName("origin")
                .name(name)
                .time(new Date())
                .build();
        try {
            String key = id.toString() + name + image.getFilterName();
            dao.s3UploadImage(key, imagePath);
            image.setS3Key(key);
        } catch (IOException e) {
            throw e;
        }
        try {
            dao.saveImage(image);
        } catch (Exception e) {
            dao.s3DeleteImage(image.getS3Key());
            throw e;
        }
        return image;
    }

    public Image updateImage(UpdateImageInput input) throws IOException {
        String id = input.getId();
        Image image = dao.getImage(id, input.getName());
        String filterName = input.getFilterName();
        String newName = input.getNewName();
        Image newImage = Image.builder()
                .id(id)
                .time(new Date())
                .filterName(filterName)
                .name(newName)
                .build();
        String des = id + newName + filterName;
        if (filterName.equals(image.getFilterName())) {
            String key = image.getS3Key();
            dao.s3CopyImage(key, des);
        } else {
            String key = id + input.getName() + "origin";
            BufferedImage filteredImage = dao.applyFilter(dao.s3download(key), filterName);
            File file = new File("temp.png");
            ImageIO.write(filteredImage, "png", file);
            dao.s3UploadImage(des, "temp.png");
            file.delete();
        }
        newImage.setS3Key(des);
        dao.saveImage(newImage);
        return newImage;
    }

    public Boolean deleteImage(String id, String name) {
        Image image = dao.getImage(id, name);
        String key = image.getS3Key();
        if (dao.s3DeleteImage(key)) {
            dao.deleteImage(image);
        } else {
            return false;
        }
        return true;
    }

    public Boolean deleteImages(String id) {
        boolean isDeleted = true;
        List<Image> images = dao.getImages(id);
        for (Image i : images) {
            if (dao.s3DeleteImage(i.getS3Key())) {
                dao.deleteImage(i);
            } else {
                System.out.println(i.getId());
                isDeleted = false;
                continue;
            }
        }
        return isDeleted;
    }
}


