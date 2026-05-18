package com.footboxd.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.footboxd.dto.AvaliacaoRequestDTO;
import com.footboxd.dto.AvaliacaoResponseDTO;
import com.footboxd.service.AvaliacaoService;

@RestController
@RequestMapping("/avaliacoes")
public class AvaliacaoController {

    private final AvaliacaoService service;

    public AvaliacaoController(AvaliacaoService service) {
        this.service = service;
    }

    @GetMapping
    public List<AvaliacaoResponseDTO> listarPorFixture(@RequestParam Long fixtureId, Principal principal) {
        String login = principal.getName();
        return service.listarPorFixture(login, fixtureId)
            .stream()
            .map(a -> new AvaliacaoResponseDTO(
                a.getId(), a.getFixtureId(), a.getTipo(),
                a.getReferenciaId(), a.getNota(), a.getCriadoEm()
            ))
            .toList();
    }

    @GetMapping("/minhas")
    public List<AvaliacaoResponseDTO> minhas(Principal principal) {
        String login = principal.getName();
        return service.listarMinhas(login)
            .stream()
            .map(a -> new AvaliacaoResponseDTO(
                a.getId(), a.getFixtureId(), a.getTipo(),
                a.getReferenciaId(), a.getNota(), a.getCriadoEm()
            ))
            .toList();
    }

    @PostMapping
    public AvaliacaoResponseDTO upsert(@RequestBody AvaliacaoRequestDTO dto, Principal principal) {
        String login = principal.getName();
        var a = service.salvarOuAtualizar(login, dto);
        return new AvaliacaoResponseDTO(
            a.getId(), a.getFixtureId(), a.getTipo(),
            a.getReferenciaId(), a.getNota(), a.getCriadoEm()
        );
    }

    @PostMapping("/batch")
    public List<AvaliacaoResponseDTO> salvarTodos(@RequestBody List<AvaliacaoRequestDTO> dtos, Principal principal) {
        String login = principal.getName();
        return service.salvarTodos(login, dtos)
            .stream()
            .map(a -> new AvaliacaoResponseDTO(
                a.getId(), a.getFixtureId(), a.getTipo(),
                a.getReferenciaId(), a.getNota(), a.getCriadoEm()
            ))
            .toList();
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id, Principal principal) {
        String login = principal.getName();
        service.remover(login, id);
    }
}