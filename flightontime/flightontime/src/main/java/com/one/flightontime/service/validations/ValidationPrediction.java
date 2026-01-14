package com.one.flightontime.service.validations;

import com.one.flightontime.infra.ds.dto.PredictionRequest;
import com.one.flightontime.infra.exceptions.CodigoInvalidoException;
import com.one.flightontime.infra.exceptions.DataHoraPartidaInvalidaException;
import com.one.flightontime.infra.exceptions.OrigemDestinoException;
import com.one.flightontime.service.CatalogoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class ValidationPrediction {

    private final CatalogoService service;

    public void validation(PredictionRequest aeroporto1){
        log.info("Iniciando validação do request de predição");
        if(!service.companhiaExiste(aeroporto1.codCompanhia())){
            throw new CodigoInvalidoException("O código da companhia aérea é inválido");
        }
        log.info("Código da companhia aérea validado com sucesso: {}", aeroporto1.codCompanhia());

        if(!service.aeroportoExiste(aeroporto1.codAeroportoOrigem())){
            throw new CodigoInvalidoException("O código do aeroporto de origem é inválido");
        }
        log.info("Código do aeroporto de origem validado com sucesso: {}", aeroporto1.codAeroportoOrigem());

        if(!service.aeroportoExiste(aeroporto1.codAeroportoDestino())){
            throw new CodigoInvalidoException("O código do aeroporto de destino é inválido");
        }
        log.info("Código do aeroporto de destino validado com sucesso: {}", aeroporto1.codAeroportoDestino());

        if(aeroporto1.codAeroportoOrigem().equals(aeroporto1.codAeroportoDestino())){
           throw new OrigemDestinoException("O aeroporto de origem não pode ser igual ao de destino");
        }
        log.info("Aeroporto de origem e destino são diferentes");

        if(aeroporto1.dataHoraPartida().isAfter(OffsetDateTime.now().plusDays(365))){
            throw new DataHoraPartidaInvalidaException(
                    "A data e hora de partida não pode ser maior que um ano a partir da data atual"
            );
        }
        log.info("Data e hora de partida está dentro do limite permitido");
    }
}
