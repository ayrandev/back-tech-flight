package com.one.flightontime.service;

import com.one.flightontime.infra.ds.dto.PredictionResponse;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class ExplicabilidadeService {

    private static final Double LIMIAR_PROB = 0.40;
    private final Map<String, Double> aeroportosMenosPontuais;
    private final Map<String, Double> aeroportosMaisPontuais;
    private final Map<String, Double> ciaMenosPontuais;
    private final Map<Integer, Double> horariosMaisAtrasados;

    public ExplicabilidadeService(ExplicabilidadeDataLoader dataLoader) {
        this.aeroportosMenosPontuais = dataLoader.loadStringDoubleMap("explicabilidade/aeroportos_menos_pontuais.json");
        this.aeroportosMaisPontuais = dataLoader.loadStringDoubleMap("explicabilidade/aeroportos_mais_pontuais.json");
        this.ciaMenosPontuais = dataLoader.loadStringDoubleMap("explicabilidade/cias_menos_pontuais.json");
        this.horariosMaisAtrasados = dataLoader.loadIntegerDoubleMap("explicabilidade/horarios_mais_atrasados.json");
    }

    public PredictionResponse returnExplicabilidade(String codAeroportoOrigem, String codCia, int horaPartida,
                                                    String status, Double probabilidade){
        StringBuilder mensagem = new StringBuilder();

        appendIfPresent(
                ciaMenosPontuais,
                codCia,
                "A companhia aérea %s está entre as menos pontuais (%.1f%% de atraso médio).",
                mensagem
        );

        appendIfPresent(
                aeroportosMenosPontuais,
                codAeroportoOrigem,
                "O aeroporto de origem %s está entre os menos pontuais (%.1f%% de atraso médio). ",
                mensagem
        );

        appendIfPresent(
                horariosMaisAtrasados,
                horaPartida,
                "O horário de partida às %02d:00 tem maior risco de atraso (%.2f%% de atraso médio). ",
                mensagem
        );

        appendIfPresent(
                aeroportosMaisPontuais,
                codAeroportoOrigem,
                "O aeroporto de origem %s está entre os mais pontuais (%.1f%% de atraso médio). ",
                mensagem
        );

        if(probabilidade > LIMIAR_PROB){
            double prob = LIMIAR_PROB * 100;
            mensagem.append(String.format(
                    "Consideramos que probabilidade acima de %.1f%% tem maior tendência a atrasos.", prob));
        }
        return PredictionResponse.builder()
                .status_predicao(status)
                .probabilidade(probabilidade)
                .mensagem(mensagem.toString())
                .build();
    }

    private <K> void appendIfPresent(
            Map<K, Double> ranking,
            K chave,
            String template,
            StringBuilder mensagem) {

        Double valor = ranking.get(chave);
        if (valor != null) {
            mensagem.append(String.format(template, chave, valor));
        }
    }
}
