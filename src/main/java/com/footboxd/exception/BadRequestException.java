package com.footboxd.exception;

/**
 * Exception lançada quando os dados da requisição são inválidos.
 * Retorna HTTP 400.
 */
public class BadRequestException extends RuntimeException {
    
    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
