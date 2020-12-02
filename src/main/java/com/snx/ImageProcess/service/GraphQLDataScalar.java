package com.snx.ImageProcess.service;

import graphql.schema.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class GraphQLDataScalar {
    public static final GraphQLScalarType DATE = new GraphQLScalarType(
            "Date", "A custom scalar that handles time",
            new Coercing() {
                @Override
                public Object serialize(Object dataFetcherResult) {
                    return serializeDate(dataFetcherResult);
                }

                @Override
                public Object parseValue(Object input) {
                    return parseDateFromVariable(input);
                }

                @Override
                public Object parseLiteral(Object input) {
                    return parseDateFromAstLiteral(input);
                }
            });

    private static Object parseDateFromAstLiteral(Object input) {
        if (input instanceof String) {
            return parseDate(input, "Unable to parse ");
        }
        throw new CoercingParseLiteralException(String.valueOf(input) + " is not any date");
    }

    private static Object parseDateFromVariable(Object input) {
        if (input instanceof String) {
            return parseDate(input, "Unable to parse ");
        }
        throw new CoercingParseValueException(String.valueOf(input) + "can not be parsed as a date");
    }

    private static Object serializeDate(Object dataFetcherResult) {
        return parseDate(dataFetcherResult, "Unable to serialize ");
    }

    private static Object parseDate(Object o, String excInfo) {
        String possibleDateVal = String.valueOf(o);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            return format.parse(possibleDateVal);
        } catch (ParseException e) {
            throw new CoercingSerializeException(
                    excInfo + possibleDateVal + " as a date");
        }
    }

}
