package com.one.flightontime.controllers;

import com.one.flightontime.domain.HistoricoPrevisao;
import com.one.flightontime.infra.ds.client.DsClient;
import com.one.flightontime.infra.ds.dto.PredictionRequest;
import com.one.flightontime.infra.ds.dto.PredictionResponse;
import com.one.flightontime.repository.HistoricoRepository;
import com.one.flightontime.service.ExplicabilidadeService;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import java.util.Map;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RequestControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DsClient dsClient;

    @MockitoBean
    private HistoricoRepository repository;

    @MockitoBean
    private ExplicabilidadeService explicabilidadeService;

    @Test
    void deveRealizarPredicaoComSucesso() throws Exception {
        PredictionRequest request = request();

        when(dsClient.predict(any(PredictionRequest.class)))
                .thenReturn(PredictionResponse.builder()
                        .probabilidade(0.25)
                        .build());

        when(explicabilidadeService.returnExplicabilidade(
                anyString(), anyString(), anyInt(), eq("PONTUAL"), eq(0.25)
        )).thenReturn(PredictionResponse.builder()
                .status_predicao("PONTUAL")
                .probabilidade(0.25)
                .mensagem("Predição realizada com sucesso")
                .build());

        mockMvc.perform(post("/predict")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_predicao").value("PONTUAL"))
                .andExpect(jsonPath("$.probabilidade").value(0.25))
                .andExpect(jsonPath("$.mensagem")
                        .value("Predição realizada com sucesso"));

        verify(repository).save(any(HistoricoPrevisao.class));
    }

    @Test
    void deveRetornarErroOrigemIgualDestino() throws Exception {
        PredictionRequest request = PredictionRequest.builder()
                .codCompanhia("AZU")
                .codAeroportoOrigem("SBGR")
                .codAeroportoDestino("SBGR")
                .dataHoraPartida(dataHoraPartidaFutura())
                .build();

        mockMvc.perform(post("/predict")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("O aeroporto de origem não pode ser igual ao de destino"));

        verifyNoInteractions(dsClient, explicabilidadeService, repository);
    }

    @Test
    void devePersistirObjetoNoBancoAposPredicao() throws Exception {
        PredictionRequest request = request();

        when(dsClient.predict(any()))
                .thenReturn(PredictionResponse.builder()
                        .probabilidade(0.55)
                        .build());

        when(explicabilidadeService.returnExplicabilidade(
                anyString(), anyString(), anyInt(), eq("ATRASADO"), eq(0.55)
        )).thenReturn(PredictionResponse.builder()
                .status_predicao("ATRASADO")
                .probabilidade(0.55)
                .mensagem("Predição realizada com sucesso")
                .build());

        mockMvc.perform(post("/predict")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_predicao").value("ATRASADO"))
                .andExpect(jsonPath("$.probabilidade").value(0.55));

        verify(repository, times(1)).save(any(HistoricoPrevisao.class));
    }

    @Test
    void deveRetornarErroQuandoDSFalhar() throws Exception {
        PredictionRequest request = request();

        FeignException ex = FeignException.errorStatus(
                "predict",
                feign.Response.builder()
                        .status(500)
                        .reason("Internal Server Error")
                        .request(Request.create(
                                Request.HttpMethod.POST,
                                "/predict",
                                Map.of(),
                                null,
                                null,
                                null))
                        .build()
        );

        when(dsClient.predict(any())).thenThrow(ex);

        mockMvc.perform(post("/predict")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message")
                        .value("Ocorreu um erro interno no serviço de predição. Por favor, tente novamente mais tarde."));

        verify(repository, never()).save(any());
        verifyNoInteractions(explicabilidadeService);
    }

    @Test
    void deveRetornarErroQuandoCampoObrigatorioFaltar() throws Exception {
        PredictionRequest request = PredictionRequest.builder()
                .codCompanhia(null)
                .codAeroportoOrigem("KMIA")
                .codAeroportoDestino("SBGR")
                .dataHoraPartida(dataHoraPartidaFutura())
                .build();

        mockMvc.perform(post("/predict")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(dsClient, explicabilidadeService, repository);
    }

    @Test
    void deveRetornarErroQuandoDataHoraPartidaNoPassado() throws Exception {
        PredictionRequest request = PredictionRequest.builder()
                .codCompanhia("AZU")
                .codAeroportoOrigem("KMIA")
                .codAeroportoDestino("SBGR")
                .dataHoraPartida(OffsetDateTime.parse("2025-12-25T10:00:00Z"))
                .build();

        mockMvc.perform(post("/predict")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("dataHoraPartida: A data deve ser no futuro"));

        verifyNoInteractions(dsClient, explicabilidadeService, repository);
    }

    private PredictionRequest request() {
        return PredictionRequest.builder()
                .codCompanhia("AZU")
                .codAeroportoOrigem("KMIA")
                .codAeroportoDestino("SBGR")
                .dataHoraPartida(dataHoraPartidaFutura())
                .build();
    }

    private OffsetDateTime dataHoraPartidaFutura() {
        return OffsetDateTime.parse("2026-01-30T10:00:00Z");
    }
}
