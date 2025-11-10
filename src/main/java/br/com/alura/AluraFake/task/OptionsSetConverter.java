package br.com.alura.AluraFake.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;

import java.util.HashSet;
import java.util.Set;

public class OptionsSetConverter implements AttributeConverter<Set<Options>, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

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
                for (Options option : optionsArray) {
                    optionsSet.add(option);
                }
            }
            return optionsSet;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao converter JSON para opções", e);
        }
    }
}
