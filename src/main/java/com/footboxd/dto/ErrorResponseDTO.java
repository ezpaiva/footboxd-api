package com.footboxd.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO padronizado para respostas de erro.
 * Mantém compatibilidade com o frontend existente.
 */
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseDTO {
    private String message;
    private LocalDateTime timestamp;
    private int status;
}
