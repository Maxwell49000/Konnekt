package com.example.reseau_social.exceptions;

/**
 * Exception levée quand une ressource n'est pas trouvée (HTTP 404)
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
