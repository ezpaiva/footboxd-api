package com.footboxd.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginDTO {
    
    @NotBlank(message = "Login é obrigatório")
    public String login;
    
    @NotBlank(message = "Senha é obrigatória")
    public String senha;
}
