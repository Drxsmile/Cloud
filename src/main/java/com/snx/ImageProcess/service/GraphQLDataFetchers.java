package com.snx.ImageProcess.service;

import com.snx.ImageProcess.object.Filter;
import com.snx.ImageProcess.object.Image;
import graphql.schema.DataFetcher;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class GraphQLDataFetchers {

    private static List<Image> images = Arrays.asList(
            new Image("1", "image1", "Beijing"),
            new Image("2", "image2", "Shanghai")
    );

    public DataFetcher getImageByIdDataFetcher() {
        return dataFetchingEnvironment -> {
            String imageId = dataFetchingEnvironment.getArgument("id");
            return images
                    .stream()
                    .filter(image -> image.getId().equals(imageId))
                    .findFirst()
                    .orElse(null);
        };
    }

}
