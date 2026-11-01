package br.com.unijui.cambio.infrastructure.client;

import br.com.unijui.cambio.infrastructure.client.dto.CotacaoExternaDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AwesomeApiClient {

    private final RestClient restClient;

    public AwesomeApiClient() {
        // Inicializa o RestClient diretamente sem depender da injeção do Spring
        this.restClient = RestClient.builder()
                .baseUrl("https://economia.awesomeapi.com.br/json/last")
                .build();
    }

    // Aplica o padrão Retry e Circuit Breaker para tolerância a falhas (Req 4)
    @Retry(name = "awesomeApi", fallbackMethod = "fallbackCotacao")
    @CircuitBreaker(name = "awesomeApi", fallbackMethod = "fallbackCotacao")
    public CotacaoExternaDto buscarCotacao(String moeda) {
        // O parâmetro 'moeda' será passado no formato "USD-BRL"
        return restClient.get()
                .uri("/{moeda}", moeda)
                .retrieve()
                .body(CotacaoExternaDto.class);
    }

    // Método executado automaticamente se a API falhar ou demorar a responder
    public CotacaoExternaDto fallbackCotacao(String moeda, Throwable ex) {
        throw new RuntimeException("O serviço de câmbio encontra-se temporariamente indisponível. Tente mais tarde.");
    }
}