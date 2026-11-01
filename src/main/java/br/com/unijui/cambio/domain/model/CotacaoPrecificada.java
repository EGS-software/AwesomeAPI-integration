package br.com.unijui.cambio.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_cotacao_precificada")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CotacaoPrecificada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String moedaOrigem;
    private String moedaDestino;

    @Column(precision = 10, scale = 4)
    private BigDecimal valorBruto;

    @Column(precision = 10, scale = 4)
    private BigDecimal valorFinalCalculado;

    private LocalDateTime dataConsulta;
}