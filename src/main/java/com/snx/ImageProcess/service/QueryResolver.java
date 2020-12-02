package com.snx.ImageProcess.service;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.snx.ImageProcess.object.Image;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class QueryResolver implements GraphQLQueryResolver {

    //    public Image getImageById(String id) {
////        return images
////                .stream()
////                .filter(image -> image.getId().equals(id))
////                .findFirst()
////                .orElse(null);
//
//    return new Image();
//    }
    public List<Image> getImageByPrimaryKey(String id, String time) {
        //TODO
        return new ArrayList<>();
    }

    public List<Image> getImagesByOriginImage(String id) {
        //TODO
        return new ArrayList<>();
    }

    public Image findImageByFilteredImage(String id) {
        //TODO
        return new Image();
    }

    public List<Image> findImagesByFilterType(String filterName) {
        //TODO
        return new ArrayList<>();
    }

}
