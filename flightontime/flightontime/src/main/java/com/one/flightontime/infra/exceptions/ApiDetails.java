package com.one.flightontime.infra.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@Schema(description = "Estrutura padrão para retorno de erros da API")
public class ApiDetails {

    @Schema(description = "Título resumido do erro",example = "Dados inválidos")
    private String title;

    @Schema(description = "Mensagem detalhada do problema", example = "A sigla do aeroporto deve ter 4 caracteres")
    private String message;

    @Schema(description = "Código HTTP do status", example = "400")
    private int status;

    @Schema(description = "Data e hora exata da ocorrência")
    private LocalDateTime timestamp;
}
