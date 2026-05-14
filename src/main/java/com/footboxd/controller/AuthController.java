package com.footboxd.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.footboxd.dto.LoginDTO;
import com.footboxd.dto.TokenDTO;
import com.footboxd.model.Usuario;
import com.footboxd.service.TokenService;
import com.footboxd.service.UsuarioService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService service;
    private final PasswordEncoder encoder;
    private final TokenService tokenService;

    public AuthController(UsuarioService service, PasswordEncoder encoder, TokenService tokenService) {
        this.service = service;
        this.encoder = encoder;
        this.tokenService = tokenService;
    }

    @PostMapping("/register")
    public Usuario register(@RequestBody Usuario usuario) {
        // impede duplicidade de login
        if (service.buscarPorLogin(usuario.getLogin()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Login já cadastrado");
        }
        return service.salvar(usuario);
    }

    @PostMapping("/login")
    public TokenDTO login(@RequestBody LoginDTO loginDTO) {
        Optional<Usuario> user = service.buscarPorLogin(loginDTO.login);

        if (user.isPresent() && encoder.matches(loginDTO.senha, user.get().getSenha())) {
            String token = tokenService.gerarToken(user.get().getLogin());
            return new TokenDTO(token);
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
    }
}