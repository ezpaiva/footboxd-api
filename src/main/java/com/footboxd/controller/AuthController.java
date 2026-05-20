package com.footboxd.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import com.footboxd.dto.LoginDTO;
import com.footboxd.dto.RegisterDTO;
import com.footboxd.exception.BadRequestException;
import com.footboxd.model.Usuario;
import com.footboxd.service.TokenService;
import com.footboxd.service.UsuarioService;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final UsuarioService service;
    private final PasswordEncoder encoder;
    private final TokenService tokenService;

    public AuthController(UsuarioService service, PasswordEncoder encoder, TokenService tokenService) {
        this.service = service;
        this.encoder = encoder;
        this.tokenService = tokenService;
    }

    /**
     * Registra um novo usuário.
     * Validação é feita automaticamente pelo Bean Validation.
     * Erros retornam HTTP 400 via GlobalExceptionHandler.
     */
    @PostMapping("/register")
    public void register(@Valid @RequestBody RegisterDTO dto) {
        log.info("Tentativa de registro para login: {}", dto.login);
        
        Usuario u = new Usuario();
        u.setNome(dto.nome.trim());
        u.setLogin(dto.login.trim().toLowerCase());
        u.setSenha(dto.senha);
        u.setRole((dto.role == null || dto.role.isBlank()) ? "USER" : dto.role);

        service.salvar(u);
        
        log.info("Usuário registrado com sucesso: {}", u.getLogin());
    }

    /**
     * Autentica um usuário e retorna um JWT.
     * Validação é feita automaticamente pelo Bean Validation.
     * Retorna { "token": "...", "nome": "...", "login": "..." }
     */
    @PostMapping("/login")
    public Map<String, String> login(@Valid @RequestBody LoginDTO loginDTO) {
        log.debug("Tentativa de login para: {}", loginDTO.login);
        
        String login = loginDTO.login.trim().toLowerCase();
        String senha = loginDTO.senha;

        var userOpt = service.buscarPorLogin(login);

        if (userOpt.isEmpty() || !encoder.matches(senha, userOpt.get().getSenha())) {
            log.warn("Falha de autenticação para: {}", login);
            throw new BadRequestException("Credenciais inválidas");
        }

        var user = userOpt.get();
        String token = tokenService.gerarToken(user.getLogin());
        
        log.info("Login bem-sucedido: {}", login);
        
        return Map.of(
                "token", token,
                "nome", user.getNome(),
                "login", user.getLogin());
    }
}
