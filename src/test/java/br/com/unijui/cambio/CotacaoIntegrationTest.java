package br.com.unijui.cambio;

import br.com.unijui.cambio.infrastructure.repository.CotacaoRepository;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CotacaoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CotacaoRepository repository;

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(8081);
        wireMockServer.start();
        WireMock.configureFor("localhost", 8081);
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }

    // Sobrescreve a URL da API apenas durante a execução dos testes
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("api.awesome.url", () -> "http://localhost:8081");
    }

    @BeforeEach
    void limparBanco() {
        repository.deleteAll();
    }

    @Test
    public void deveProcessarEGravarCotacaoComSucesso() throws Exception {
        // 1. Simula a resposta da API Externa com um valor redondo (5.00)
        String jsonResponse = "{\"USDBRL\": {\"bid\": \"5.0000\"}}";
        stubFor(WireMock.get(urlEqualTo("/USD-BRL"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonResponse)));

        // 2. Dispara a requisição para o nosso Controller (Req 1 e 2)
        // O valor final deve ser 5.00 * 1.05144 (Spread + IOF) = 5.2572
        mockMvc.perform(get("/api/cotacoes/USD/BRL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valorBruto").value(5.0000))
                .andExpect(jsonPath("$.valorFinalCalculado").value(5.2572));

        // 3. Valida se a cotação foi salva no Banco de Dados (Req 3)
        boolean existeRegistro = repository.count() == 1;
        assertTrue(existeRegistro, "O registro deveria ter sido salvo no banco de dados.");
    }
}