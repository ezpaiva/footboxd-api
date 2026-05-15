package com.footboxd.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.footboxd.dto.LoginDTO;
import com.footboxd.dto.RegisterDTO;
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
    public void register(@RequestBody RegisterDTO dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados invalidos");
        }

        String nome = dto.nome == null ? "" : dto.nome.trim();
        String login = dto.login == null ? "" : dto.login.trim().toLowerCase();

        if (nome.length() < 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome inválido");
        }
        if (!login.contains("@")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Login/e-mail inválido");
        }
        if (dto.senha == null || dto.senha.length() < 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha inválida");
        }

        if (service.buscarPorLogin(login).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Login já cadastrado");
        }

        Usuario u = new Usuario();
        u.setNome(nome);
        u.setLogin(login);
        u.setSenha(dto.senha);
        u.setRole((dto.role == null || dto.role.isBlank()) ? "USER" : dto.role);

        service.salvar(u);
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginDTO loginDTO) {
        if (loginDTO == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados invalidos");
        }

        String login = loginDTO.login == null ? "" : loginDTO.login.trim().toLowerCase();
        String senha = loginDTO.senha == null ? "" : loginDTO.senha;

        var userOpt = service.buscarPorLogin(login);

        if (userOpt.isPresent() && encoder.matches(senha, userOpt.get().getSenha())) {
            var user = userOpt.get();
            String token = tokenService.gerarToken(user.getLogin());
            return Map.of(
                    "token", token,
                    "nome", user.getNome(),
                    "login", user.getLogin());
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
    }

}
