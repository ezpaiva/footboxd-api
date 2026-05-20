package com.footboxd.exception;

/**
 * Exception lançada quando um recurso requisitado não é encontrado.
 * Retorna HTTP 404.
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
