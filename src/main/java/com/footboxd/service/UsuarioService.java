package com.footboxd.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.footboxd.exception.BadRequestException;
import com.footboxd.model.Usuario;
import com.footboxd.repository.UsuarioRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    public Usuario salvar(Usuario usuario) {
        if (usuario == null) {
            throw new BadRequestException("Usuário não pode ser nulo");
        }
        
        if (usuario.getNome() == null || usuario.getNome().trim().length() < 3) {
            throw new BadRequestException("Nome inválido: deve ter no mínimo 3 caracteres");
        }
        
        if (usuario.getLogin() == null || !usuario.getLogin().contains("@")) {
            throw new BadRequestException("Login/e-mail inválido");
        }
        
        if (usuario.getSenha() == null || usuario.getSenha().length() < 4) {
            throw new BadRequestException("Senha inválida: deve ter no mínimo 4 caracteres");
        }

        // Verificar se usuário já existe
        if (repository.findByLogin(usuario.getLogin()).isPresent()) {
            log.warn("Tentativa de registrar login duplicado: {}", usuario.getLogin());
            throw new BadRequestException("Login já cadastrado");
        }
        
        usuario.setSenha(encoder.encode(usuario.getSenha()));
        var usuarioSalvo = repository.save(usuario);
        log.info("Novo usuário registrado: {}", usuario.getLogin());
        return usuarioSalvo;
    }

    public Optional<Usuario> buscarPorLogin(String login) {
        return repository.findByLogin(login);
    }
}