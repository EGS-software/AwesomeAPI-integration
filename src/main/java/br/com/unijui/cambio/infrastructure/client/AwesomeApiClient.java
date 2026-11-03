package br.com.unijui.cambio.infrastructure.client;

import br.com.unijui.cambio.infrastructure.client.dto.CotacaoExternaDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class AwesomeApiClient {

    private final RestClient restClient;

    public AwesomeApiClient(@Value("${api.awesome.url:https://economia.awesomeapi.com.br/json/last}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    // Aplica o padrão Retry e Circuit Breaker para tolerância a falhas (Req 4)
    @Retry(name = "awesomeApi", fallbackMethod = "fallbackCotacao")
    @CircuitBreaker(name = "awesomeApi", fallbackMethod = "fallbackCotacao")
    public CotacaoExternaDto buscarCotacao(String moeda) {

        java.util.Map<String, CotacaoExternaDto> resposta = restClient.get()
                .uri("/{moeda}", moeda)
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<java.util.Map<String, CotacaoExternaDto>>() {});

        return resposta.values().iterator().next();
    }

    // Método executado automaticamente se a API falhar ou demorar a responder
    public CotacaoExternaDto fallbackCotacao(String moeda, Throwable ex) {
        throw new RuntimeException("O serviço de câmbio encontra-se temporariamente indisponível. Tente mais tarde.");
    }
}