package br.com.unijui.cambio.presentation;

import br.com.unijui.cambio.application.CotacaoService;
import br.com.unijui.cambio.domain.model.CotacaoPrecificada;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cotacoes")
public class CotacaoController {

    private final CotacaoService cotacaoService;

    public CotacaoController(CotacaoService cotacaoService) {
        this.cotacaoService = cotacaoService;
    }

    @GetMapping("/{moedaOrigem}/{moedaDestino}")
    public ResponseEntity<CotacaoPrecificada> gerarCotacao(
            @PathVariable String moedaOrigem,
            @PathVariable String moedaDestino) {

        CotacaoPrecificada cotacao = cotacaoService.processarEGravarCotacao(moedaOrigem, moedaDestino);
        return ResponseEntity.ok(cotacao);
    }
}