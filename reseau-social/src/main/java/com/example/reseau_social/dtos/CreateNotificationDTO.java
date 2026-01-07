package com.example.reseau_social.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateNotificationDTO {
    @NotNull(message = "L'ID de l'utilisateur destinataire est requis")
    private Integer utilisateurId;

    @NotBlank(message = "Le type de notification ne peut pas être vide")
    private String type;

    @NotBlank(message = "Le message de notification ne peut pas être vide")
    private String message;

    public CreateNotificationDTO() {
    }

    public CreateNotificationDTO(Integer utilisateurId, String type, String message) {
        this.utilisateurId = utilisateurId;
        this.type = type;
        this.message = message;
    }

    public Integer getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(Integer utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
