package com.one.flightontime.service;

import com.one.flightontime.domain.HistoricoPrevisao;
import com.one.flightontime.infra.ds.client.DsClient;
import com.one.flightontime.infra.ds.dto.PredictionRequest;
import com.one.flightontime.infra.ds.dto.PredictionResponse;
import com.one.flightontime.infra.exceptions.CodigoInvalidoException;
import com.one.flightontime.repository.HistoricoRepository;
import com.one.flightontime.service.validations.ValidationPrediction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.OffsetDateTime;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoricoServiceTest {

    @Mock
    private HistoricoRepository historicoRepository;

    @Mock
    private DsClient dsClient;

    @Mock
    private ValidationPrediction validationPrediction;

    @Mock
    private ExplicabilidadeService explicabilidadeService;

    @InjectMocks
    private HistoricoService historicoService;

    @Test // TESTE PARA RETORNAR PONTUAL QUANDO PROBABILIDADE FOR MENOR QUE 40
    void deveRetornarPontualQuandoProbabilidadeMenorQue40() {
        PredictionRequest request = request();
        when(dsClient.predict(request))
                .thenReturn(PredictionResponse.builder().probabilidade(0.35).build());

        doNothing().when(validationPrediction).validation(request);

        when(explicabilidadeService.returnExplicabilidade(
                anyString(), anyString(), anyInt(), eq("PONTUAL"), eq(0.35)
        )).thenReturn(PredictionResponse.builder()
                .status_predicao("PONTUAL")
                .probabilidade(0.35)
                .mensagem("msg")
                .build());

        PredictionResponse response = historicoService.prediction(request);

        assertEquals("PONTUAL", response.status_predicao());
        assertEquals(0.35, response.probabilidade());

        verify(historicoRepository).save(any(HistoricoPrevisao.class));
    }

    @Test // TESTE PARA RETORNAR ATRASADO QUANDO PROBABILIDADE FOR MAIOR QUE 40
    void deveRetornarAtrasadoQuandoProbabilidadeMaiorQue40() {
        PredictionRequest request = request();

        when(dsClient.predict(request))
                .thenReturn(PredictionResponse.builder().probabilidade(0.45).build());

        doNothing().when(validationPrediction).validation(request);

        when(explicabilidadeService.returnExplicabilidade(
                anyString(), anyString(), anyInt(), eq("ATRASADO"), eq(0.45)
        )).thenReturn(PredictionResponse.builder()
                .status_predicao("ATRASADO")
                .probabilidade(0.45)
                .build());

        PredictionResponse response = historicoService.prediction(request);

        assertEquals("ATRASADO", response.status_predicao());
        assertEquals(0.45, response.probabilidade());

        verify(historicoRepository).save(any(HistoricoPrevisao.class));
    }

    @Test // TESTE PARA ARREDONDAR A PROBABILIDADE CORRETAMENTE EM DUAS CASAS DECIMAIS
    void deveArredondarAProbabilidadeCorretamente() {
        PredictionRequest request = request();

        when(dsClient.predict(request))
                .thenReturn(PredictionResponse.builder().probabilidade(0.45999).build());

        doNothing().when(validationPrediction).validation(request);

        when(explicabilidadeService.returnExplicabilidade(
                anyString(), anyString(), anyInt(), eq("ATRASADO"), eq(0.45)
        )).thenReturn(PredictionResponse.builder()
                .status_predicao("ATRASADO")
                .probabilidade(0.45)
                .build());

        PredictionResponse response = historicoService.prediction(request);

        assertEquals(0.45, response.probabilidade());
        verify(historicoRepository).save(any(HistoricoPrevisao.class));
    }

    @Test // TESTE PARA LANÇAR EXCEÇÃO QUANDO A VALIDAÇÃO FALHAR
    void deveLancarExcecaoQuandoValidacaoFalhar() {
        PredictionRequest request = request();

        doThrow(new CodigoInvalidoException("Dados inválidos"))
                .when(validationPrediction).validation(request);

        CodigoInvalidoException ex = assertThrows(
                CodigoInvalidoException.class,
                () -> historicoService.prediction(request)
        );

        assertEquals("Dados inválidos", ex.getMessage());

        verify(historicoRepository, never()).save(any());
        verifyNoInteractions(dsClient, explicabilidadeService);
    }

    @Test // TESTE PARA LANÇAR EXCEÇÃO QUANDO O DS CLIENT FALHAR
    void deveLancarExcecaoQuandoDsClientFalhar() {
        PredictionRequest request = request();

        doNothing().when(validationPrediction).validation(request);

        when(dsClient.predict(request))
                .thenThrow(new RuntimeException("Erro no DS Client"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> historicoService.prediction(request)
        );

        assertEquals("Erro no DS Client", ex.getMessage());

        verify(historicoRepository, never()).save(any());
        verifyNoInteractions(explicabilidadeService);
    }

    private PredictionRequest request(){
        OffsetDateTime data = dataHoraPartidaFutura();
        return PredictionRequest.builder()
                .codCompanhia("AZU")
                .codAeroportoOrigem("KMIA")
                .codAeroportoDestino("SBGR")
                .dataHoraPartida(data)
                .build();
    }

    private OffsetDateTime dataHoraPartidaFutura(){
        return OffsetDateTime.parse("2026-01-30T10:00:00Z");
    }
}