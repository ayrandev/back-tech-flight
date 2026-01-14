package com.one.flightontime.infra.exceptions;

import feign.FeignException;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GerenciadorErros {

    @ExceptionHandler(OrigemDestinoException.class)
    public ResponseEntity<@NonNull ApiDetails> tratarOrigemDestino(OrigemDestinoException ex){
        ApiDetails apiDetails = ApiDetails.builder()
                .title("O destino deve ser diferente da origem")
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(java.time.LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiDetails);
    }

    @ExceptionHandler(CodigoInvalidoException.class)
    public ResponseEntity<@NonNull ApiDetails> tratarCodigoInvalido(CodigoInvalidoException ex){
        ApiDetails apiDetails = ApiDetails.builder()
                .title("Código inválido")
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(java.time.LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiDetails);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<@NonNull ApiDetails> tratarValidacaoBean(MethodArgumentNotValidException ex) {

        String mensagem = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .findFirst()
                .orElse("Erro de validação");

        ApiDetails apiDetails = ApiDetails.builder()
                .title("Erro de validação")
                .message(mensagem)
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiDetails);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<@NonNull ApiDetails> tratarErroFeign(FeignException ex) {
        int status = ex.status();
        if(status == 500){
            ApiDetails apiDetails = ApiDetails.builder()
                    .title("Erro no serviço de predição")
                    .message("Ocorreu um erro interno no serviço de predição. Por favor, tente novamente mais tarde.")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiDetails);
        } if(status == 404) {
            ApiDetails apiDetails = ApiDetails.builder()
                    .title("Não encontrado")
                    .message("O serviço de predição não foi encontrado. Verifique a URL e tente")
                    .status(HttpStatus.NOT_FOUND.value())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(apiDetails);
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body(ApiDetails.builder()
                            .title("Erro na comunicação com o serviço de predição")
                            .message("Não foi possível conectar à API externa. Por favor, tente novamente mais tarde.")
                            .status(HttpStatus.BAD_GATEWAY.value())
                            .timestamp(LocalDateTime.now())
                            .build());
        }
    }
}
