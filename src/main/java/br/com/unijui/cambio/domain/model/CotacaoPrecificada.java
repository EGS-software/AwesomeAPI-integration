package br.com.unijui.cambio.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_cotacao_precificada")
public class CotacaoPrecificada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String moedaOrigem;
    private String moedaDestino;

    @Column(precision = 10, scale = 4)
    private BigDecimal valorBruto;

    @Column(precision = 10, scale = 4)
    private BigDecimal valorFinalCalculado; // Valor após taxas

    private LocalDateTime dataConsulta;

    // Construtores, Getters e Setters omitidos para brevidade
}