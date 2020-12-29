package com.snx.ImageProcess.service;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.snx.ImageProcess.dao.DaoRepository;
import com.snx.ImageProcess.object.Image;
import com.snx.ImageProcess.object.UpdateImageInput;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class MutationResolver implements GraphQLMutationResolver {
    @Autowired
    private DaoRepository dao;

    public Image saveOriginImage(String name, byte[] image, DataFetchingEnvironment env) throws IOException {
        String realName = env.getArgument("name");
        InputStream inputStream = new ByteArrayInputStream(env.getArgument("image"));
        BufferedImage bi = ImageIO.read(inputStream);
        UUID id = UUID.randomUUID();
        Image origin = Image.builder()
                .id(id.toString())
                .filterName("origin")
                .name(realName)
                .time(new Date())
                .build();
        try {
            String key = id.toString() + realName + origin.getFilterName();
            dao.s3UploadImage(key, bi);
            origin.setS3Key(key);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            dao.saveImage(origin);
        } catch (Exception e) {
            dao.s3DeleteImage(origin.getS3Key());
            e.printStackTrace();
            return null;
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
                e.printStackTrace();
                return null;
            }
        } else {
            String key = id + input.getName() + "origin";
            try {
                BufferedImage filteredImage = dao.applyFilter(dao.s3download(key), filterName);
                dao.s3UploadImage(des, filteredImage);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        newImage.setS3Key(des);
        try {
            dao.saveImage(newImage);
        } catch (Exception e) {
            dao.s3DeleteImage(des);
            e.printStackTrace();
            return null;
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


