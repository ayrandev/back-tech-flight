package com.one.flightontime.controllers;

import com.one.flightontime.infra.ds.dto.PredictionRequest;
import com.one.flightontime.infra.ds.dto.PredictionResponse;
import com.one.flightontime.service.HistoricoService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@RequestMapping("/predict")
@Validated
@Slf4j

@Tag(name = "Predição de atrasos", description = "Endpoint para análise de risco de voos")
public class RequestController {

    private final HistoricoService historicoService;

    @Operation(summary = "Calcular probabilidade de atraso",
            description = "Recebe os dados do voo e realiza uma requisição interna para " +
                    "a API Python(Data Science), que processa o modelo de Machine Learning e retorna a previsão. ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Predição realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos(Data no passado, sigla errada ou campos vazios"),
            @ApiResponse(responseCode = "500", description = "Erro de comunicação com o serviço Python")
    })
    @PostMapping
    public ResponseEntity<@NonNull PredictionResponse> receberDadosApi(@Valid @RequestBody PredictionRequest dados){
        PredictionResponse enviarDados = historicoService.prediction(dados);
        log.debug("Dados recebidos na API: companhia {}, origem {}, destino {}, data-hora {}",
                dados.codCompanhia(), dados.codAeroportoOrigem(), dados.codAeroportoDestino(), dados.dataHoraPartida()
        );
        return ResponseEntity.ok(enviarDados);
    }
}
