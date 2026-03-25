package br.com.alura.screenmatch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class ConverterDados implements IConverteDados {

    private final ObjectMapper mapper;

    public ConverterDados(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    @Override
    public <T> T obterDados(String json, Class<T> classe) {
        try {
            return mapper.readValue(json, classe);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter JSON para " + classe.getSimpleName(), e);
        }
    }
}