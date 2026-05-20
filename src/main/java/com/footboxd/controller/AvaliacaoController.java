package com.footboxd.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import com.footboxd.dto.AvaliacaoRequestDTO;
import com.footboxd.dto.AvaliacaoResponseDTO;
import com.footboxd.service.AvaliacaoService;

/**
 * Controller para gerenciar avaliações de fixture (JOGO) e referências (JOGADOR/TECNICO).
 * Todos os erros são tratados automaticamente pelo GlobalExceptionHandler.
 */
@RestController
@RequestMapping("/avaliacoes")
@Slf4j
public class AvaliacaoController {

    private final AvaliacaoService service;

    public AvaliacaoController(AvaliacaoService service) {
        this.service = service;
    }

    /**
     * Lista as avaliações de uma fixture específica para o usuário autenticado.
     */
    @GetMapping
    public List<AvaliacaoResponseDTO> listarPorFixture(@RequestParam Long fixtureId, Principal principal) {
        String login = principal.getName();
        log.debug("Listando avaliações da fixture {} para usuário {}", fixtureId, login);
        
        return service.listarPorFixture(login, fixtureId)
            .stream()
            .map(a -> new AvaliacaoResponseDTO(
                a.getId(), a.getFixtureId(), a.getTipo(),
                a.getReferenciaId(), a.getNota(), a.getCriadoEm()
            ))
            .toList();
    }

    /**
     * Lista todas as avaliações do usuário autenticado.
     */
    @GetMapping("/minhas")
    public List<AvaliacaoResponseDTO> minhas(Principal principal) {
        String login = principal.getName();
        log.debug("Listando todas as avaliações do usuário {}", login);
        
        return service.listarMinhas(login)
            .stream()
            .map(a -> new AvaliacaoResponseDTO(
                a.getId(), a.getFixtureId(), a.getTipo(),
                a.getReferenciaId(), a.getNota(), a.getCriadoEm()
            ))
            .toList();
    }

    /**
     * Salva ou atualiza uma avaliação.
     * Validação é feita automaticamente pelo Bean Validation.
     */
    @PostMapping
    public AvaliacaoResponseDTO upsert(@Valid @RequestBody AvaliacaoRequestDTO dto, Principal principal) {
        String login = principal.getName();
        log.debug("Salvando/atualizando avaliação para fixture {} tipo {}", dto.fixtureId, dto.tipo);
        
        var a = service.salvarOuAtualizar(login, dto);
        return new AvaliacaoResponseDTO(
            a.getId(), a.getFixtureId(), a.getTipo(),
            a.getReferenciaId(), a.getNota(), a.getCriadoEm()
        );
    }

    /**
     * Salva múltiplas avaliações em lote.
     */
    @PostMapping("/batch")
    public List<AvaliacaoResponseDTO> salvarTodos(@Valid @RequestBody List<AvaliacaoRequestDTO> dtos, Principal principal) {
        String login = principal.getName();
        log.debug("Salvando {} avaliações em batch para usuário {}", dtos.size(), login);
        
        return service.salvarTodos(login, dtos)
            .stream()
            .map(a -> new AvaliacaoResponseDTO(
                a.getId(), a.getFixtureId(), a.getTipo(),
                a.getReferenciaId(), a.getNota(), a.getCriadoEm()
            ))
            .toList();
    }

    /**
     * Deleta uma avaliação específica.
     */
    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id, Principal principal) {
        String login = principal.getName();
        log.debug("Deletando avaliação ID {} do usuário {}", id, login);
        
        service.remover(login, id);
    }
}