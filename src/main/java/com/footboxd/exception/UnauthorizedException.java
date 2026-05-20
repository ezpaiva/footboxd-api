package com.footboxd.exception;

/**
 * Exception lançada quando token é inválido ou expirado.
 * Retorna HTTP 401.
 */
public class UnauthorizedException extends RuntimeException {
    
    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
