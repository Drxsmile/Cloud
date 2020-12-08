package com.snx.ImageProcess.service;

import graphql.schema.*;
import lombok.SneakyThrows;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GraphQLDateScalar extends GraphQLScalarType {
    public static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public GraphQLDateScalar() {
        super("Date", "GraphQLDate type", new Coercing<Date, String>() {
            @Override
            public String serialize(Object dataFetcherResult) throws CoercingSerializeException {
                try {
                    Date date = (Date) dataFetcherResult;
                    return FORMAT.format(date);
                } catch (CoercingSerializeException e) {
                    throw new CoercingSerializeException("Serialize failed: " + String.valueOf(dataFetcherResult));
                }

            }

            @SneakyThrows
            @Override
            public Date parseValue(Object input) throws CoercingParseValueException {
                String value = String.valueOf(input);
                if ("null".equalsIgnoreCase(value) || "".equalsIgnoreCase(value)) {
                    return null;
                }
                try {
                    return FORMAT.parse(value);
                } catch (ParseException e) {
                    throw e;
                }
            }

            @SneakyThrows
            @Override
            public Date parseLiteral(Object input) throws CoercingParseLiteralException {
                String value = String.valueOf(input);
                if ("null".equalsIgnoreCase(value) || "".equalsIgnoreCase(value)) {
                    return null;
                }
                try {
                    return FORMAT.parse(value);
                } catch (ParseException e) {
                    throw e;
                }
            }
        });

    }
}
