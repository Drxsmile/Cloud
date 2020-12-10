package com.snx.ImageProcess;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.boot.jackson.JsonComponent;

import javax.servlet.http.Part;

@JsonComponent
//自动将Jackson的序列化和反序列化手动加入ObjectMapper
public class PartDeserializer extends JsonDeserializer<Part> {
    @Override
    public Part deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        return null;
    }
}
