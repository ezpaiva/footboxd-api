package com.footboxd.config;

import com.footboxd.dto.ErrorResponseDTO;
import com.footboxd.exception.BadRequestException;
import com.footboxd.exception.ResourceNotFoundException;
import com.footboxd.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Handler global de exceções.
 * Centraliza o tratamento de erros da API.
 * Mantém compatibilidade total com o frontend.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Tratamento de recurso não encontrado
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("Recurso não encontrado: {}", ex.getMessage());
        
        ErrorResponseDTO error = new ErrorResponseDTO(
            ex.getMessage(),
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Tratamento de requisição inválida
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadRequest(BadRequestException ex) {
        log.warn("Requisição inválida: {}", ex.getMessage());
        
        ErrorResponseDTO error = new ErrorResponseDTO(
            ex.getMessage(),
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Tratamento de não autorizado (token inválido)
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponseDTO> handleUnauthorized(UnauthorizedException ex) {
        log.warn("Acesso não autorizado: {}", ex.getMessage());
        
        ErrorResponseDTO error = new ErrorResponseDTO(
            ex.getMessage(),
            LocalDateTime.now(),
            HttpStatus.UNAUTHORIZED.value()
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Tratamento de erros de validação do Bean Validation
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationError(MethodArgumentNotValidException ex) {
        String messages = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
        
        log.warn("Erro de validação: {}", messages);
        
        ErrorResponseDTO error = new ErrorResponseDTO(
            messages.isEmpty() ? "Dados inválidos" : messages,
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Tratamento de ResponseStatusException (legado do código antigo)
     * Converte para formato padronizado
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponseDTO> handleResponseStatusException(ResponseStatusException ex) {
        log.warn("Status exception: {} - {}", ex.getStatusCode(), ex.getReason());
        
        ErrorResponseDTO error = new ErrorResponseDTO(
            ex.getReason() != null ? ex.getReason() : "Erro na requisição",
            LocalDateTime.now(),
            ex.getStatusCode().value()
        );
        
        return ResponseEntity.status(ex.getStatusCode()).body(error);
    }

    /**
     * Tratamento genérico para exceções não previstas
     * Não expõe stacktrace ao cliente
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
        log.error("Erro interno não tratado", ex);
        
        ErrorResponseDTO error = new ErrorResponseDTO(
            "Erro interno do servidor",
            LocalDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
