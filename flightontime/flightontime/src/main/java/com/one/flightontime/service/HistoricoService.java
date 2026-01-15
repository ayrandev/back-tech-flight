package com.one.flightontime.service;

import com.one.flightontime.domain.HistoricoPrevisao;
import com.one.flightontime.domain.enums.StatusPredicao;
import com.one.flightontime.infra.ds.client.DsClient;
import com.one.flightontime.infra.ds.dto.PredictionRequest;
import com.one.flightontime.infra.ds.dto.PredictionResponse;
import com.one.flightontime.repository.HistoricoRepository;
import com.one.flightontime.service.validations.ValidationPrediction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class HistoricoService {

    private final DsClient dsClient;
    private final HistoricoRepository repository;
    private final ValidationPrediction validation;
    private final ExplicabilidadeService explicabilidadeService;

    public PredictionResponse prediction(PredictionRequest request) {
        log.info("Predição recebida para companhia {} de {} para {} em {}", request.codCompanhia(),
                request.codAeroportoOrigem(), request.codAeroportoDestino(), request.dataHoraPartida());

        validation.validation(request);
        log.info("Request de predição validado com sucesso");
        PredictionResponse response;

        response = dsClient.predict(request);
        log.info("Probabilidade - {}", response.probabilidade());
        Double probabilidade = response.probabilidade();
        probabilidade = formatarProbabilidade(probabilidade);
        StatusPredicao status = pontualOrAtrasado(probabilidade);
        log.info("Probabilidade - {}, Status - {}", probabilidade, status);

        HistoricoPrevisao historico = criarHistorico(request, status, probabilidade);
        repository.save(historico);
        log.info("Histórico de predição salvo com sucesso: {}", historico.getIdHistorico());
        log.debug("Data hora partida: {}", request.dataHoraPartida());

        return explicabilidadeService.returnExplicabilidade(request.codAeroportoOrigem(), request.codCompanhia(),
                request.dataHoraPartida().getHour(), status.name(), probabilidade);
    }

    private Double formatarProbabilidade(Double probabilidade) {
        return BigDecimal.valueOf(probabilidade)
                .setScale(2, RoundingMode.DOWN)
                .doubleValue();
    }

    private StatusPredicao pontualOrAtrasado(Double probabilidade){
        return probabilidade >= 0.40 ? StatusPredicao.ATRASADO : StatusPredicao.PONTUAL;
    }

    private HistoricoPrevisao criarHistorico(PredictionRequest request, StatusPredicao status, Double probabilidade) {
        HistoricoPrevisao historico = new HistoricoPrevisao();
        historico.setCodCompanhia(request.codCompanhia());
        historico.setCodAeroportoOrigem(request.codAeroportoOrigem());
        historico.setCodAeroportoDestino(request.codAeroportoDestino());
        historico.setDataHoraPartida(request.dataHoraPartida());
        historico.setStatusPredicao(status);
        historico.setProbabilidade(probabilidade);
        return historico;
    }
}
