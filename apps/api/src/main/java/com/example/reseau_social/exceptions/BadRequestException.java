package com.example.reseau_social.exceptions;

/**
 * Exception levée quand la requête est invalide (HTTP 400)
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
