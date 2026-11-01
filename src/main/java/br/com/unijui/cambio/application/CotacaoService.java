package br.com.unijui.cambio.application;

import br.com.unijui.cambio.domain.model.CotacaoPrecificada;
import br.com.unijui.cambio.infrastructure.client.AwesomeApiClient;
import br.com.unijui.cambio.infrastructure.client.dto.CotacaoExternaDto;
import br.com.unijui.cambio.infrastructure.repository.CotacaoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class CotacaoService {

    private final AwesomeApiClient apiClient;
    private final CotacaoRepository repository;

    public CotacaoService(AwesomeApiClient apiClient, CotacaoRepository repository) {
        this.apiClient = apiClient;
        this.repository = repository;
    }

    public CotacaoPrecificada processarEGravarCotacao(String moedaOrigem, String moedaDestino) {
        String parMoedas = moedaOrigem + "-" + moedaDestino;

        // 1. Consome API externa (Req 1)
        CotacaoExternaDto dto = apiClient.buscarCotacao(parMoedas);

        // 2. Processa os dados recebidos (Req 2)
        // Cálculo: Valor Bruto * 1.04 (Spread) * 1.011 (IOF)
        BigDecimal valorBruto = dto.bid();
        BigDecimal fatorMultiplicador = new BigDecimal("1.05144"); // 1.04 * 1.011
        BigDecimal valorFinal = valorBruto.multiply(fatorMultiplicador).setScale(4, RoundingMode.HALF_UP);

        // 3. Persiste os dados (Req 3)
        CotacaoPrecificada cotacao = new CotacaoPrecificada();
        cotacao.setMoedaOrigem(moedaOrigem);
        cotacao.setMoedaDestino(moedaDestino);
        cotacao.setValorBruto(valorBruto);
        cotacao.setValorFinalCalculado(valorFinal);
        cotacao.setDataConsulta(LocalDateTime.now());

        return repository.save(cotacao);
    }
}