package com.snx.ImageProcess.service;

import com.coxautodev.graphql.tools.SchemaParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import javax.naming.OperationNotSupportedException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/graphql", consumes = {"application/json", "multipart/form-data"})
public class GraphQLController {
    @Autowired
    private QueryResolver queryResolver;
    @Autowired
    private MutationResolver mutationResolver;
    @Autowired
    private ObjectMapper objectMapper;

    private GraphQL graphQL;

    @PostConstruct
    public void init() {
        GraphQLSchema graphQLSchema = SchemaParser.newParser()
                .file("schema.graphqls")
                .resolvers(queryResolver, mutationResolver)
                .scalars(GraphQLScalarType.newScalar(new GraphQLDateScalar()).build())
                .scalars(GraphQLScalarType.newScalar(new GraphQLUploadScalar()).build())
                .build()
                .makeExecutableSchema();
        graphQL = GraphQL.newGraphQL(graphQLSchema).build();

    }

    @PostMapping(consumes = {"application/json", "multipart/form-data"})

    public Map<String, Object> myGraphql(
            @RequestHeader(value = HttpHeaders.CONTENT_TYPE, required = false) String contentType,
            @RequestBody(required = false) String body,
            WebRequest webRequest,
            @RequestParam(required = false) MultipartHttpServletRequest multipartHttpServletRequest,
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "operationName", required = false) String operationName,
            @RequestParam(value = "variables", required = false) String variablesJson
    ) throws IOException, OperationNotSupportedException {
        if (body == null) {
            body = "";
        }
        if (MediaType.APPLICATION_JSON_VALUE.equals(contentType)) {
            RequestQuery request = objectMapper.readValue(body, RequestQuery.class);
            if (request.getQuery() == null) {
                request.setQuery("");
            }
            return execute(request.getQuery(), request.getOperationName(), request.getVariables());
        } else if (contentType != null && contentType.startsWith(MediaType.MULTIPART_FORM_DATA_VALUE)) {
            LinkedHashMap<String, ArrayList<String>> multipartFileKeyVariablePathMap = objectMapper.readValue(webRequest.getParameter("map"), LinkedHashMap.class);
            System.out.println(multipartFileKeyVariablePathMap);
            String name = webRequest.getParameter("name");
            MultipartFile file = multipartHttpServletRequest.getFile("image");
            RequestQuery request = objectMapper.readValue(webRequest.getParameter("operations"), RequestQuery.class);
            if (request.getQuery() != null) {
                request.getVariables().put("name", name);
                request.getVariables().put("image", file.getBytes());
            } else {
                request.setQuery("");
            }
//            if(request.getQuery() != null) {
//                LinkedHashMap<String, ArrayList<String>> multipartFileKeyVariablePathMap = objectMapper.readValue(webRequest.getParameter("map"), LinkedHashMap.class);
//                System.out.println(multipartFileKeyVariablePathMap);
//                MultipartHttpServletRequest multiPartRequest = (MultipartHttpServletRequest) ((ServletWebRequest) webRequest).getNativeRequest();
//                Map<String, MultipartFile> multipartFileMap = multiPartRequest.getFileMap();
//                System.out.println(multipartFileMap);
//                for(Map.Entry<String,MultipartFile> e: multipartFileMap.entrySet()){
//                    String pathString = multipartFileKeyVariablePathMap.get(e.getKey()).get(0); /*i.e. "variables.files" or "variables.fileList.NUMBER*/
//                    if (pathString.matches("variables\\.[a-zA-Z0-9]*?\\.\\d")) {
//                        String[] splittedPath = pathString.split("\\.", 3);
//                        final Object variablesArray = request.getVariables().get(splittedPath[1]);
//                        if (variablesArray instanceof ArrayList)
//                            ((ArrayList) variablesArray).set(Integer.parseInt(splittedPath[2]), e.getValue().getBytes());
//                        else
//                            throw new OperationNotSupportedException("Array of files represented by not supported collection");
//                    } else if (pathString.startsWith("variables.")) {
//                        String[] splittedPath = pathString.split("\\.",2);
//                        request.getVariables().put(splittedPath[1],e.getValue().getBytes());
//                    }
//                }
//            }else {
//                request.setQuery("");
//            }

            return execute(request.getQuery(), request.getOperationName(), request.getVariables());
        }
        if (query != null) {
            return execute(query, operationName, convertVariablesJson(variablesJson));
        }
        if ("application/graphql".equals(contentType)) {
            return execute(body, null, null);
        }

        throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Could not process GraphQL request");

    }

    @GetMapping
    public Map<String, Object> myGraphql(@RequestBody(required = false) String body) throws JsonProcessingException {
        RequestQuery request = objectMapper.readValue(body, RequestQuery.class);
        if (request.getQuery() == null) {
            request.setQuery("");
        }
        return execute(request.getQuery(), request.getOperationName(), request.getVariables());
    }

    public Map<String, Object> execute(String query, String operationName, Map<String, Object> variables) {
        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(query)
                .variables(variables)
                .operationName(operationName)
                .build();
        ExecutionResult executionResult = graphQL.execute(executionInput);
        return executionResult.toSpecification();
    }

    public Map<String, Object> convertVariablesJson(String jsonMap) throws JsonProcessingException {
        if (jsonMap == null) {
            return Collections.emptyMap();
        }
        return objectMapper.readValue(jsonMap, Map.class);
    }

}
