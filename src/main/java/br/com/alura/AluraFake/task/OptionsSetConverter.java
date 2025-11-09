package br.com.alura.AluraFake.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

@Converter
public class OptionsSetConverter implements AttributeConverter<Set<Options>, String> {

    private static final Logger logger = LoggerFactory.getLogger(OptionsSetConverter.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Set<Options> options) {
        try {
            return objectMapper.writeValueAsString(options);
        } catch (JsonProcessingException e) {
            logger.error("Error converting options to JSON", e);
            return "[]";
        }
    }

    @Override
    public Set<Options> convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.isEmpty()) {
                return new HashSet<>();
            }
            return objectMapper.readValue(dbData, new TypeReference<Set<Options>>() {});
        } catch (JsonProcessingException e) {
            logger.error("Error converting JSON to options", e);
            return new HashSet<>();
        }
    }
}
