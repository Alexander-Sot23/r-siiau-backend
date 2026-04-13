package com.alexander.springboot.r_siiau_backend.convert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Converter
public class MapToJsonConverter implements AttributeConverter<Map<String,Object>,String> {

    private final ObjectMapper objectMapper;

    public MapToJsonConverter(){
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModules(new JavaTimeModule());
        this.objectMapper.findAndRegisterModules();
        this.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        if(attribute == null || attribute.isEmpty()){
            return "{}";
        }
        try{
            return objectMapper.writeValueAsString(attribute);
        }catch (JsonProcessingException ex){
            throw new IllegalArgumentException("Error converting map to JSON: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        if(dbData == null || dbData.isEmpty() || dbData.equals("{}")){
            return new HashMap<>();
        }
        try{
            return objectMapper.readValue(dbData, new TypeReference<Map<String, Object>>() {});
        }catch (IOException ex){
            throw new IllegalArgumentException("Error converting JSON to map: " + ex.getMessage(), ex);
        }
    }
}
