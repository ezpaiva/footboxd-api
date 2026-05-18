package com.footboxd.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.footboxd.dto.AvaliacaoRequestDTO;
import com.footboxd.model.Avaliacao;
import com.footboxd.model.TipoAvaliacao;
import com.footboxd.repository.AvaliacaoRepository;

@Service
public class AvaliacaoService {

    private final AvaliacaoRepository repo;
    private final UsuarioService usuarioService;

    public AvaliacaoService(AvaliacaoRepository repo, UsuarioService usuarioService) {
        this.repo = repo;
        this.usuarioService = usuarioService;
    }

    public List<Avaliacao> listarPorFixture(String login, Long fixtureId) {
        return repo.findByUsuarioLoginAndFixtureId(login, fixtureId);
    }

    public List<Avaliacao> listarMinhas(String login) {
        return repo.findByUsuarioLoginOrderByCriadoEmDesc(login);
    }

    public Avaliacao salvarOuAtualizar(String login, AvaliacaoRequestDTO dto) {
        validar(dto);

        Long referenciaIdNormalizada = normalizarReferencia(dto.tipo, dto.referenciaId);

        BigDecimal nota1casa = dto.nota.setScale(1, RoundingMode.HALF_UP);

        var existente = repo.findByUsuarioLoginAndFixtureIdAndTipoAndReferenciaId(
            login, dto.fixtureId, dto.tipo, referenciaIdNormalizada
        );

        Avaliacao a = existente.orElseGet(Avaliacao::new);

        a.setFixtureId(dto.fixtureId);
        a.setTipo(dto.tipo);
        a.setReferenciaId(referenciaIdNormalizada);
        a.setNota(nota1casa);

        if (a.getUsuario() == null) {
            var usuario = usuarioService.buscarPorLogin(login)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado"));
            a.setUsuario(usuario);
        }

        return repo.save(a);
    }

    public List<Avaliacao> salvarTodos(String login, List<AvaliacaoRequestDTO> dtos) {
        return dtos.stream()
            .map(dto -> salvarOuAtualizar(login, dto))
            .toList();
    }

    public void remover(String login, Long id) {
        var avaliacao = repo.findByIdAndUsuarioLogin(id, login)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Avaliação não encontrada"));
        repo.delete(avaliacao);
    }

    private void validar(AvaliacaoRequestDTO dto) {
        if (dto.fixtureId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fixtureId obrigatório");
        if (dto.tipo == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tipo obrigatório");
        if (dto.nota == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "nota obrigatória");

        BigDecimal min = new BigDecimal("0.0");
        BigDecimal max = new BigDecimal("10.0");

        if (dto.nota.compareTo(min) < 0 || dto.nota.compareTo(max) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "nota deve ser entre 0.0 e 10.0");
        }
    }

    private Long normalizarReferencia(TipoAvaliacao tipo, Long referenciaId) {
        if (tipo == TipoAvaliacao.JOGO) return 0L;

        if (referenciaId == null || referenciaId <= 0) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "referenciaId (player.id/coach.id) obrigatório para JOGADOR/TECNICO"
            );
        }
        return referenciaId;
    }
}