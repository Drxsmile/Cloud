package com.snx.ImageProcess.service;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.snx.ImageProcess.dao.DaoRepository;
import com.snx.ImageProcess.object.Image;
import com.snx.ImageProcess.object.UpdateImageInput;
import graphql.schema.DataFetchingEnvironment;
import graphql.servlet.GraphQLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.servlet.http.Part;
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

    public Image saveOriginImage(String name, Part image, DataFetchingEnvironment env) throws IOException {
        Part imagePart = env.getArgument("image");
        BufferedImage bi = ImageIO.read(imagePart.getInputStream());
        UUID id = UUID.randomUUID();
        Image origin = Image.builder()
                .id(id.toString())
                .filterName("origin")
                .name(name)
                .time(new Date())
                .build();
        try {
            String key = id.toString() + name + origin.getFilterName();
            dao.s3UploadImage(key, bi);
            origin.setS3Key(key);
        } catch (IOException e) {
            throw e;
        }
        try {
            dao.saveImage(origin);
        } catch (Exception e) {
            dao.s3DeleteImage(origin.getS3Key());
            throw e;
        }
        return origin;
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
            try {
                dao.s3CopyImage(key, des);
            } catch (Exception e) {
                throw e;
            }
        } else {
            String key = id + input.getName() + "origin";
            File file = null;
            try {
                BufferedImage filteredImage = dao.applyFilter(dao.s3download(key), filterName);
                dao.s3UploadImage(des, filteredImage);
            } catch (Exception e) {
                throw e;
            }
        }
        newImage.setS3Key(des);
        try {
            dao.saveImage(newImage);
        } catch (Exception e) {
            dao.s3DeleteImage(des);
            throw e;
        }
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


