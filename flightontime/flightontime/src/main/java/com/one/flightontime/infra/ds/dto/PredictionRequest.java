package com.one.flightontime.infra.ds.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import java.time.OffsetDateTime;

@Builder
@Schema(description = "Objeto contendo os dados do voo para análise de previsão")
public record PredictionRequest(

        @Schema(description = "Código da companhia aérea (ICAO ou IATA",example = "GLO" )
        @NotBlank(message = "Campo Obrigatório")
        @Size(min = 3,max = 3,message = "A sigla deve conter 3 caracteres")
        @Pattern(regexp = "^[A-Za-z]{3}$")
        @JsonProperty("cod_companhia")
        String codCompanhia,

        @Schema(description = "Código ICAO do aeroporto de Origem", example = "SBGR")
        @NotBlank(message = "Campo Obrigatório")
        @Size(min = 4,max = 4,message = "A sigla deve conter 4 caracteres")
        @Pattern(regexp = "^[A-Za-z]{4}$")
        @JsonProperty("cod_aeroporto_origem")
        String codAeroportoOrigem,

        @Schema(description = "Código ICAO do aeroporto de destino", example = "SBGL")
        @NotBlank(message = "Campo Obrigatório")
        @Size(min = 4,max = 4,message = "A sigla deve conter 4 caracteres")
        @Pattern(regexp = "^[A-Za-z]{4}$")
        @JsonProperty("cod_aeroporto_destino")
        String codAeroportoDestino,

        @Schema(description = "Data e hora prevista para a partida (ISO 8601)", example = "2026-02-05T16:30:00Z")
        @Future(message = "A data deve ser no futuro")
        @NotNull(message = "A data é obrigatória")
        @JsonProperty("data_hora_partida")
        OffsetDateTime dataHoraPartida
) {}