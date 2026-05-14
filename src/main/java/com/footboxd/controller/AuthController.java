package com.footboxd.controller;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.footboxd.dto.LoginDTO;
import com.footboxd.model.Usuario;
import com.footboxd.service.UsuarioService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService service;
    private final PasswordEncoder encoder;

    public AuthController(UsuarioService service, PasswordEncoder encoder) {
        this.service = service;
        this.encoder = encoder;
    }

    @PostMapping("/register")
    public Usuario register(@RequestBody Usuario usuario) {
        return service.salvar(usuario);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginDTO loginDTO) {

        Optional<Usuario> user = service.buscarPorLogin(loginDTO.login);

        if (user.isPresent() && encoder.matches(loginDTO.senha, user.get().getSenha())) {
            return "Login OK";
        }

        throw new RuntimeException("Credenciais inválidas");
    }
}