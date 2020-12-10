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
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Collections;
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
            WebRequest webRequest, MultipartHttpServletRequest multipartHttpServletRequest,
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "operationName", required = false) String operationName,
            @RequestParam(value = "variables", required = false) String variablesJson
    ) throws IOException {
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
            String name = webRequest.getParameter("name");
            MultipartFile file = multipartHttpServletRequest.getFile("image");
            RequestQuery request = objectMapper.readValue(webRequest.getParameter("operations"), RequestQuery.class);
            if (request.getQuery() != null) {
                request.getVariables().put("name", name);
                request.getVariables().put("image", file.getBytes());
            } else {
                request.setQuery("");
            }
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
