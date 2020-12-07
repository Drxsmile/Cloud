package com.snx.ImageProcess.service;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.snx.ImageProcess.dao.DaoRepository;
import com.snx.ImageProcess.object.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QueryResolver implements GraphQLQueryResolver {

    @Autowired
    private DaoRepository dao;

    public Image getImageByPrimaryKey(String id, String name) {
        return dao.getImage(id, name);
    }

    public List<Image> getImagesByOriginImage(String id) {
        return dao.getImages(id);
    }

    public Image findImageByFilteredImage(String id) {
        return dao.getImageByGSI("filterName-id-index", id, "origin");
    }

    public List<Image> findImagesByFilterType(String filterName) {
        return dao.getImagesByGSI("filterName-id-index", filterName);
    }

}
