package com.snx.ImageProcess.service;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.coxautodev.graphql.tools.SchemaParser;
import com.snx.ImageProcess.object.Filter;
import com.snx.ImageProcess.object.Image;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class QueryResolver implements GraphQLQueryResolver {
    private static List<Image> images = Arrays.asList(
            new Image("1", "image1", "Beijing"),
            new Image("2", "image2", "Shanghai")
    );
    public Image getImageById(String id){
        return images
                .stream()
                .filter(image -> image.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    public List<Image> findAllImages(){
        return images;
    }
}
