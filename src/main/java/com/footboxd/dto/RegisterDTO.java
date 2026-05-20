package com.footboxd.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterDTO {
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 255, message = "Nome deve ter entre 3 e 255 caracteres")
    public String nome;
    
    @NotBlank(message = "Login/e-mail é obrigatório")
    @Email(message = "Login deve ser um e-mail válido")
    public String login;
    
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 4, max = 255, message = "Senha deve ter entre 4 e 255 caracteres")
    public String senha;
    
    public String role;
}