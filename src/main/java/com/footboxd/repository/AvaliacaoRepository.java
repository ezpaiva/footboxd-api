package com.footboxd.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.footboxd.model.Avaliacao;
import com.footboxd.model.TipoAvaliacao;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    List<Avaliacao> findByUsuarioLoginAndFixtureId(String login, Long fixtureId);

    List<Avaliacao> findByUsuarioLoginOrderByCriadoEmDesc(String login);

    Optional<Avaliacao> findByUsuarioLoginAndFixtureIdAndTipoAndReferenciaId(
        String login, Long fixtureId, TipoAvaliacao tipo, Long referenciaId
    );

    Optional<Avaliacao> findByIdAndUsuarioLogin(Long id, String login);
}