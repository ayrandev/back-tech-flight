package com.one.flightontime.infra.ds.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@Schema(description = "Resposta da API com o resultado da previsão de atraso")
public record PredictionResponse (

    @Schema(description = "Resposta do modelo (Ex: ATRASADO, PONTUAL", example = "ATRASADO")
    @NotBlank(message = "Campo Obrigatório")
    @JsonProperty("status_predicao")
    String status_predicao,

    @Schema(description = "Probabilidade calculada pelo modelo (0.0 a 1.0)", example = "0.85")
    @NotNull(message = "Probabilidade obrigatória")
    @JsonProperty("probabilidade")
    Double probabilidade,

    @Schema(description = "Mensagem explicativa do resultado", example = "O voo tem grandes chances de atraso devido ao histórico da rota.")
    @JsonProperty("mensagem")
    String mensagem
) {}
