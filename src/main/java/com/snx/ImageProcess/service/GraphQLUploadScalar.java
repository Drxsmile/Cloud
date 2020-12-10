package com.snx.ImageProcess.service;

import graphql.schema.*;
import org.springframework.web.multipart.MultipartFile;

public class GraphQLUploadScalar extends GraphQLScalarType {
    public GraphQLUploadScalar() {
        super("Upload",
                "A file part in a multipart request",
                new Coercing<byte[], Void>() {
                    @Override
                    public Void serialize(Object dataFetcherResult) {
                        throw new CoercingSerializeException("Upload is an input-only type");
                    }

                    @Override
                    public byte[] parseValue(Object input) {
                        if (input instanceof byte[]) {
                            return (byte[]) input;
                        } else if (null == input) {
                            return null;
                        } else {
                            throw new CoercingParseValueException("Expected type " +
                                    MultipartFile.class.getName() +
                                    " but was " +
                                    input.getClass().getName());
                        }
                    }

                    @Override
                    public byte[] parseLiteral(Object input) {
                        throw new CoercingParseLiteralException(
                                "Must use variables to specify Upload values");
                    }
                });
    }
}
