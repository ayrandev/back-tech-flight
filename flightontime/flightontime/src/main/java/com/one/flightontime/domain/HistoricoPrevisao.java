package com.one.flightontime.domain;

import com.one.flightontime.domain.enums.StatusPredicao;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "HistoricoPrevisao")
public class HistoricoPrevisao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHistorico;

    @NotBlank
    @Column(name = "codCompanhia",nullable = false,length = 3)
    @Pattern(regexp = "^[A-Za-z]{3}$")
    @Size(min = 3,max = 3,message = "A sigla deve conter 3 caracteres")
    private String codCompanhia;

    @NotBlank
    @Pattern(regexp = "^[A-Za-z]{4}$")
    @Size(min = 4,max = 4,message = "A sigla deve conter 4 caracteres")
    @Column(name = "codAeroportoOrigem",nullable = false,length = 4)
    private String codAeroportoOrigem;

    @NotBlank
    @Pattern(regexp = "^[A-Za-z]{4}$")
    @Size(min = 4,max = 4,message = "A sigla deve conter 4 caracteres")
    @Column(name = "codAeroportoDestino",nullable = false,length = 4)
    private String codAeroportoDestino;

    @NotNull
    @Column(name = "dataHoraPartida")
    @Future
    private OffsetDateTime dataHoraPartida;

    @Enumerated(EnumType.STRING)
    @Column(name = "statusPredicao")
    private StatusPredicao statusPredicao;

    @Column(name = "probabilidade")
    @NotNull
    private Double probabilidade;

    @Column(name = "dataConsulta")
    private LocalDateTime dataConsulta;

    @PrePersist
    private void preencherData(){
        this.dataConsulta = LocalDateTime.now();
    }

}
