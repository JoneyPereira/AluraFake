package br.com.alura.AluraFake.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import jakarta.persistence.AttributeConverter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class OptionsSetConverter implements AttributeConverter<Set<Options>, String> {
    private final ObjectMapper objectMapper;

    public OptionsSetConverter() {
        this.objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public String convertToDatabaseColumn(Set<Options> options) {
        try {
            return objectMapper.writeValueAsString(options);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao converter opções para JSON", e);
        }
    }

    @Override
    public Set<Options> convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null) {
                return new HashSet<>();
            }
            Options[] optionsArray = objectMapper.readValue(dbData, Options[].class);
            Set<Options> optionsSet = new HashSet<>();
            if (optionsArray != null) {
                optionsSet.addAll(Arrays.asList(optionsArray));
            }
            return optionsSet;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao converter JSON para opções", e);
        }
    }
}
