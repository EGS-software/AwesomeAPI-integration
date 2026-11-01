package br.com.unijui.cambio.infrastructure.repository;

import br.com.unijui.cambio.domain.model.CotacaoPrecificada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CotacaoRepository extends JpaRepository<CotacaoPrecificada, Long> {
}