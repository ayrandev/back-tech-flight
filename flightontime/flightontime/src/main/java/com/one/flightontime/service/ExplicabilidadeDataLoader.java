package com.one.flightontime.service;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ExplicabilidadeDataLoader {

    private final ObjectMapper objectMapper;

    public Map<String, Double> loadStringDoubleMap(String path) {
        try (InputStream is = new ClassPathResource(path).getInputStream()) {
            return objectMapper.readValue(
                    is,
                    new TypeReference<Map<String, Double>>() {}
            );
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao carregar arquivo: " + path, e);
        }
    }

    public Map<Integer, Double> loadIntegerDoubleMap(String path) {
        Map<String, Double> raw = loadStringDoubleMap(path);
        return raw.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> Integer.valueOf(e.getKey()),
                        Map.Entry::getValue
                ));
    }
}
