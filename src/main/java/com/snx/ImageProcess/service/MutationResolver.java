package com.snx.ImageProcess.service;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.snx.ImageProcess.object.Image;
import graphql.GraphQL;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MutationResolver implements GraphQLMutationResolver {
    private static Map<String, Image> images = new HashMap<>();
        static {
            images.put("1", new Image("1", "image1", "Beijing"));
            images.put("2", new Image("2", "image2", "Shanghai"));
        }

    public Image updateFilter(String imageId, String filterName){
        if(images.containsKey(imageId)){
            Image newImage = images.get(imageId);
            newImage.setFilter(filterName);
            images.put(imageId, newImage);
            return newImage;
        }
        throw new RuntimeException("Wrong Id");
    }
    public Image updateImageInfo(String imageId){
        if(images.containsKey(imageId)){
            return images.get(imageId);
        }
        throw new RuntimeException("Wrong Id");
    }
    public Image updateImageInfo(String imageId, String name){
        if(images.containsKey(imageId)){
            Image newImage = images.get(imageId);
            newImage.setName(name);
            images.put(imageId, newImage);
            return newImage;
        }
        throw new RuntimeException("Wrong Id");
    }
    public Image updateImageInfo(String imageId, String name, String loc){
        if(images.containsKey(imageId)){
            Image newImage = images.get(imageId);
            newImage.setName(name);
            newImage.setLocation(loc);
            images.put(imageId, newImage);
            return newImage;
        }
        throw new RuntimeException("Wrong Id");
    }
}
