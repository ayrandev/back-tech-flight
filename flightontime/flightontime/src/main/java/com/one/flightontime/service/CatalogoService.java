package com.one.flightontime.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class CatalogoService {

    private final Set<String> companhias = new HashSet<>();
    private final Set<String> aeroportos = new HashSet<>();

    @PostConstruct
    public void carregarCatalogos() {
        carregarArquivo("catalogos/companhias.txt", companhias);
        log.info("Companhias carregadas");
        carregarArquivo("catalogos/aeroportos.txt", aeroportos);
        log.info("Aeroportos carregados");
    }

    private void carregarArquivo(String caminho, Set<String> destino) {
        try (InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream(caminho)) {
            assert is != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

                reader.lines()
                        .map(String::trim)
                        .filter(linha -> !linha.isEmpty())
                        .map(String::toUpperCase)
                        .forEach(destino::add);

            }
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao carregar arquivo: " + caminho, e);
        }
    }

    public boolean companhiaExiste(String codigo) {
        return companhias.contains(codigo.toUpperCase());
    }

    public boolean aeroportoExiste(String codigo) {
        return aeroportos.contains(codigo.toUpperCase());
    }
}

