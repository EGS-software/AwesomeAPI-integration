package br.com.unijui.cambio.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CotacaoExternaDto(
        String code,
        String codein,
        BigDecimal bid, // Valor bruto da moeda
        String create_date
) {}