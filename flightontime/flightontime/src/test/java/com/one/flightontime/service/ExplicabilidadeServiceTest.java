package com.one.flightontime.service;

import com.one.flightontime.infra.ds.dto.PredictionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExplicabilidadeServiceTest {

    @BeforeEach
    void setup() {
        Map<String, Double> aeroportosMenosPontuais = Map.of("SBGR", 45.0);
        Map<String, Double> aeroportosMaisPontuais = Map.of("KMIA", 10.0);
        Map<String, Double> ciaMenosPontuais = Map.of("AZU", 50.0);
        Map<Integer, Double> horariosMaisAtrasados = Map.of(18, 60.5);

        when(dataLoader.loadStringDoubleMap("explicabilidade/aeroportos_menos_pontuais.json"))
                .thenReturn(aeroportosMenosPontuais);

        when(dataLoader.loadStringDoubleMap("explicabilidade/aeroportos_mais_pontuais.json"))
                .thenReturn(aeroportosMaisPontuais);

        when(dataLoader.loadStringDoubleMap("explicabilidade/cias_menos_pontuais.json"))
                .thenReturn(ciaMenosPontuais);

        when(dataLoader.loadIntegerDoubleMap("explicabilidade/horarios_mais_atrasados.json"))
                .thenReturn(horariosMaisAtrasados);

        explicabilidadeService = new ExplicabilidadeService(dataLoader);
    }

    @Mock
    private ExplicabilidadeDataLoader dataLoader;

    @InjectMocks
    private ExplicabilidadeService explicabilidadeService;

    // TESTE PARA RETORNAR MENSAGEM DE COMPANHIA AEREA MENOS PONTUAL
    @Test
    void deveRetornarMensagemCiaMenosPontual() {
        PredictionResponse response = explicabilidadeService.returnExplicabilidade("SDCG", "AZU", 14, "ATRASADO", 0.30);
        assertTrue(response.mensagem().contains("A companhia aérea AZU está entre as menos pontuais (50,0% de atraso médio)."));
    }
    // TESTE PARA RETORNAR MENSAGEM DE AEROPORTO DE ORIGEM MENOS PONTUAL
    @Test
    void deveRetornarMensagemAeroportoMenosPontual() {
        PredictionResponse response = explicabilidadeService.returnExplicabilidade("SBGR", "TAM", 10, "ATRASADO", 0.30);
        assertTrue(response.mensagem().contains("O aeroporto de origem SBGR está entre os menos pontuais (45,0% de atraso médio)."));
    }

    // TESTE PARA RETORNAR MENSAGEM DE HORARIO MAIS ATRASADO
    @Test
    void deveRetornarMensagemHorarioMaisAtrasado() {
        PredictionResponse response = explicabilidadeService.returnExplicabilidade("SDCG", "TAM", 18, "ATRASADO", 0.30);
        assertTrue(response.mensagem().contains("O horário de partida às 18:00 tem maior risco de atraso (60,50% de atraso médio)."));
    }

    // TESTE PARA RETORNAR MENSAGEM DE AEROPORTO DE ORIGEM MAIS PONTUAL
    @Test
    void deveRetornarMensagemAeroportoMaisPontual() {
        PredictionResponse response = explicabilidadeService.returnExplicabilidade("KMIA", "TAM", 10, "PONTUAL", 0.30);
        assertTrue(response.mensagem().contains("O aeroporto de origem KMIA está entre os mais pontuais (10,0% de atraso médio)."));
    }

    // TESTE PARA RETORNAR MENSAGEM DE AEROPORTO DE ORIGEM MAIS PONTUAL
    @Test
    void deveRetornarMensagemAeroportoMaisPontualEHorarioMaisAtrasado() {
        PredictionResponse response = explicabilidadeService.returnExplicabilidade("KMIA", "TAM", 15, "PONTUAL", 0.30);
        assertTrue(response.mensagem().contains("O aeroporto de origem KMIA está entre os mais pontuais (10,0% de atraso médio)."));
    }

    // TESTE PARA RETORNAR MENSAGEM DE PROBABILIDADE ACIMA DO LIMIAR
    @Test
    void deveRetornarMensagemProbabilidadeAcimaLimiar() {
        PredictionResponse response = explicabilidadeService.returnExplicabilidade("KMIA", "TAM", 10, "ATRASADO", 0.45);
        assertTrue(response.mensagem().contains("Consideramos que probabilidade acima de 40,0% tem maior tendência a atrasos."));
    }

    // TESTE PARA NÃO RETORNAR MENSAGEM QUANDO NENHUMA CONDIÇÃO APLICAR
    @Test
    void naoDeveAdicionarMensagemQuandoNenhumaCondicaoAplicar() {
        PredictionResponse response = explicabilidadeService.returnExplicabilidade(
                "XXX", "YYY", 5, "PONTUAL", 0.20
        );

        assertEquals("", response.mensagem());
    }

    // TESTE PARA RETORNAR STATUS E PROBABILIDADE CORRETAMENTE
    @Test
    void deveRetornarStatusEProbabilidadeCorretamente() {
        PredictionResponse response = explicabilidadeService.returnExplicabilidade(
                "SBGR", "AZU", 18, "ATRASADO", 0.55
        );

        assertEquals("ATRASADO", response.status_predicao());
        assertEquals(0.55, response.probabilidade());
    }
}