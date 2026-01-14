package com.one.flightontime.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = CatalogoService.class)
class CatalogoServiceTest {

    @Autowired
    private CatalogoService catalogoService;

    @Test
    void deveRetornarTrueParaCompanhiaExistente() {
        assertTrue(catalogoService.companhiaExiste("AAL"));
    }

    @Test
    void deveRetornarFalseParaCompanhiaInexistente() {
        assertFalse(catalogoService.companhiaExiste("XXX"));
    }

    @Test
    void deveRetornarTrueParaAeroportoExistente() {
        assertTrue(catalogoService.aeroportoExiste("KMIA"));
    }

    @Test
    void deveRetornarFalseParaAeroportoInexistente() {
        assertFalse(catalogoService.aeroportoExiste("ZZZZ"));
    }
}