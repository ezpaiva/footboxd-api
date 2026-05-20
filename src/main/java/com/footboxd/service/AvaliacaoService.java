package com.footboxd.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;

import com.footboxd.dto.AvaliacaoRequestDTO;
import com.footboxd.exception.BadRequestException;
import com.footboxd.exception.ResourceNotFoundException;
import com.footboxd.exception.UnauthorizedException;
import com.footboxd.model.Avaliacao;
import com.footboxd.model.TipoAvaliacao;
import com.footboxd.repository.AvaliacaoRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AvaliacaoService {

    private final AvaliacaoRepository repo;
    private final UsuarioService usuarioService;

    public AvaliacaoService(AvaliacaoRepository repo, UsuarioService usuarioService) {
        this.repo = repo;
        this.usuarioService = usuarioService;
    }

    public List<Avaliacao> listarPorFixture(String login, Long fixtureId) {
        log.debug("Listando avaliações para fixture {} do usuário {}", fixtureId, login);
        return repo.findByUsuarioLoginAndFixtureId(login, fixtureId);
    }

    public List<Avaliacao> listarMinhas(String login) {
        log.debug("Listando minhas avaliações do usuário {}", login);
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
                .orElseThrow(() -> {
                    log.warn("Usuário não encontrado durante salvar avaliação: {}", login);
                    return new UnauthorizedException("Usuário não encontrado");
                });
            a.setUsuario(usuario);
        }

        var resultado = repo.save(a);
        log.info("Avaliação salva/atualizada com ID: {}", resultado.getId());
        return resultado;
    }

    public List<Avaliacao> salvarTodos(String login, List<AvaliacaoRequestDTO> dtos) {
        log.debug("Salvando {} avaliações em batch para usuário {}", dtos.size(), login);
        return dtos.stream()
            .map(dto -> salvarOuAtualizar(login, dto))
            .toList();
    }

    public void remover(String login, Long id) {
        log.debug("Removendo avaliação ID {} do usuário {}", id, login);
        
        var avaliacao = repo.findByIdAndUsuarioLogin(id, login)
            .orElseThrow(() -> {
                log.warn("Avaliação não encontrada: ID {} para usuário {}", id, login);
                return new ResourceNotFoundException("Avaliação não encontrada");
            });
        
        repo.delete(avaliacao);
        log.info("Avaliação ID {} removida", id);
    }

    private void validar(AvaliacaoRequestDTO dto) {
        if (dto.fixtureId == null) {
            throw new BadRequestException("fixtureId obrigatório");
        }
        if (dto.tipo == null) {
            throw new BadRequestException("tipo obrigatório");
        }
        if (dto.nota == null) {
            throw new BadRequestException("nota obrigatória");
        }

        BigDecimal min = new BigDecimal("0.0");
        BigDecimal max = new BigDecimal("10.0");

        if (dto.nota.compareTo(min) < 0 || dto.nota.compareTo(max) > 0) {
            throw new BadRequestException("nota deve ser entre 0.0 e 10.0");
        }
    }

    private Long normalizarReferencia(TipoAvaliacao tipo, Long referenciaId) {
        if (tipo == TipoAvaliacao.JOGO) {
            return 0L;
        }

        if (referenciaId == null || referenciaId <= 0) {
            throw new BadRequestException(
                "referenciaId (player.id/coach.id) obrigatório para JOGADOR/TECNICO"
            );
        }
        return referenciaId;
    }
}