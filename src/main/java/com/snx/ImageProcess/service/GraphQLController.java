package com.snx.ImageProcess.service;

import com.coxautodev.graphql.tools.SchemaParser;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping(path = "/graphql", produces = "application/json")
public class GraphQLController {
    @Autowired
    private QueryResolver queryResolver;
    @Autowired
    private MutationResolver mutationResolver;

    private GraphQL graphQL;

    @PostConstruct
    public void init() throws IOException {
        GraphQLSchema graphQLSchema = SchemaParser.newParser()
                .file("schema.graphqls")
                .resolvers(queryResolver, mutationResolver)
                .scalars(GraphQLDataScalar.DATE)
                .build()
                .makeExecutableSchema();
        graphQL = GraphQL.newGraphQL(graphQLSchema).build();

    }

    @PostMapping
    public Map<String, Object> myGraphql(@RequestBody RequestQuery query){
        Object context;
        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(query.getQuery())
                .variables(query.getVariables())
                .operationName(query.getOperationName())
                .build();
        ExecutionResult executionResult = graphQL.execute(executionInput);
        return executionResult.toSpecification();
    }

}
