package com.snx.ImageProcess.service;

import lombok.Data;

import java.util.Map;

@Data
public class RequestQuery {
    private String operationName;
    private String query;
    private Map<String, Object> variables;

}
