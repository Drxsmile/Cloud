package com.snx.ImageProcess.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestQuery {
    private String operationName;
    private String query;
    private Map<String, Object> variables;

}
