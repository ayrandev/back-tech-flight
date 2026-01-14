package com.one.flightontime.service.validations;

import com.one.flightontime.infra.ds.dto.PredictionRequest;
import com.one.flightontime.infra.exceptions.CodigoInvalidoException;
import com.one.flightontime.infra.exceptions.DataHoraPartidaInvalidaException;
import com.one.flightontime.infra.exceptions.OrigemDestinoException;
import com.one.flightontime.service.CatalogoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.OffsetDateTime;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // ATIVA O MOCKITO PARA QUE POSSA SER USADO O @MOCK E @INJECTMOCKS
class ValidationPredictionTest {

    @Mock
    private CatalogoService catalogoService; // MOCK DO SERVIÇO DE CATÁLOGO

    @InjectMocks
    private ValidationPrediction validationPrediction; // INJEÇÃO DO MOCK NO SERVIÇO DE VALIDAÇÃO
    // É COMO SE O CATALOGOSERVICE FOSSE INJETADO DENTRO DO VALIDATIONPREDICTION -> new ValidationPrediction(catalogoService);

    @Test // TESTE PARA VALIDAR UMA REQUEST VÁLIDA
    void deveValidarRequestComSucesso() {
        // ARRANGE
        PredictionRequest request = PredictionRequest.builder()
                .codCompanhia("AZU")
                .codAeroportoOrigem("KMIA")
                .codAeroportoDestino("SBAC")
                .dataHoraPartida(OffsetDateTime.now().plusHours(2))
                .build();

        when(catalogoService.companhiaExiste("AZU")).thenReturn(true);
        when(catalogoService.aeroportoExiste("KMIA")).thenReturn(true);
        when(catalogoService.aeroportoExiste("SBAC")).thenReturn(true);

        // ACT & ASSERT
        assertDoesNotThrow(() -> validationPrediction.validation(request));
    }

    @Test // TESTE PARA VALIDAR UMA REQUEST COM CÓDIGO DE COMPANHIA INVÁLIDO
    void deveLancarExcecaoParaCompanhiaInvalida() {
        PredictionRequest request = PredictionRequest.builder()
                .codCompanhia("XXX")
                .codAeroportoOrigem("KMIA")
                .codAeroportoDestino("SBAC")
                .dataHoraPartida(OffsetDateTime.now().plusHours(2))
                .build();

        when(catalogoService.companhiaExiste("XXX")).thenReturn(false);

        CodigoInvalidoException exception = assertThrows(
                CodigoInvalidoException.class,
                () -> validationPrediction.validation(request)
        );
    }

    @Test // TESTE PARA VALIDAR UMA REQUEST COM CÓDIGO DE AEROPORTO DE ORIGEM INVÁLIDO
    void deveLancarExcecaoParaAeroportoOrigemInvalido() {
        PredictionRequest request = PredictionRequest.builder()
                .codCompanhia("AZU")
                .codAeroportoOrigem("XXXX")
                .codAeroportoDestino("SBAC")
                .dataHoraPartida(OffsetDateTime.now().plusHours(2))
                .build();

        when(catalogoService.companhiaExiste("AZU")).thenReturn(true);
        when(catalogoService.aeroportoExiste("XXXX")).thenReturn(false);

        CodigoInvalidoException exception = assertThrows(
                CodigoInvalidoException.class,
                () -> validationPrediction.validation(request)
        );
    }

    @Test // TESTE PARA VALIDAR UMA REQUEST COM CÓDIGO DE AEROPORTO DE DESTINO INVÁLIDO
    void deveLancarExcecaoParaAeroportoDestinoInvalido() {
        PredictionRequest request = PredictionRequest.builder()
                .codCompanhia("AZU")
                .codAeroportoOrigem("KMIA")
                .codAeroportoDestino("XXXX")
                .dataHoraPartida(OffsetDateTime.now().plusHours(2))
                .build();

        when(catalogoService.companhiaExiste("AZU")).thenReturn(true);
        when(catalogoService.aeroportoExiste("KMIA")).thenReturn(true);
        when(catalogoService.aeroportoExiste("XXXX")).thenReturn(false);

        CodigoInvalidoException exception = assertThrows(
                CodigoInvalidoException.class,
                () -> validationPrediction.validation(request)
        );
    }

    @Test // TESTE PARA VALIDAR UMA REQUEST COM AEROPORTO DE ORIGEM IGUAL AO DE DESTINO
    void deveLancarExcecaoParaAeroportoOrigemIgualDestino() {
        PredictionRequest request = PredictionRequest.builder()
                .codCompanhia("AZU")
                .codAeroportoOrigem("KMIA")
                .codAeroportoDestino("KMIA")
                .dataHoraPartida(OffsetDateTime.now().plusHours(2))
                .build();

        when(catalogoService.companhiaExiste("AZU")).thenReturn(true);
        when(catalogoService.aeroportoExiste("KMIA")).thenReturn(true);

        OrigemDestinoException exception = assertThrows(
                OrigemDestinoException.class,
                () -> validationPrediction.validation(request)
        );
    }

    @Test // TESTE PARA VALIDAR UMA REQUEST COM DATA DE PARTIDA MAIOR QUE UM ANO A PARTIR DA DATA ATUAL
    void deveLancarExcecaoParaDataHoraPartidaMaiorQueUmAno() {
        PredictionRequest request = PredictionRequest.builder()
                .codCompanhia("AZU")
                .codAeroportoOrigem("KMIA")
                .codAeroportoDestino("SBAC")
                .dataHoraPartida(OffsetDateTime.now().plusDays(366))
                .build();

        when(catalogoService.companhiaExiste("AZU")).thenReturn(true);
        when(catalogoService.aeroportoExiste("KMIA")).thenReturn(true);
        when(catalogoService.aeroportoExiste("SBAC")).thenReturn(true);

        DataHoraPartidaInvalidaException exception = assertThrows(
                DataHoraPartidaInvalidaException.class,
                () -> validationPrediction.validation(request)
        );
    }
}