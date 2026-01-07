package com.example.reseau_social.dtos;

import java.time.Instant;

public class NotificationResponseDTO {
    private String id;
    private Integer utilisateurId;
    private String type;
    private String message;
    private Boolean lue;
    private Instant dateCreation;

    public NotificationResponseDTO() {
    }

    public NotificationResponseDTO(String id, Integer utilisateurId, String type, String message, Boolean lue, Instant dateCreation) {
        this.id = id;
        this.utilisateurId = utilisateurId;
        this.type = type;
        this.message = message;
        this.lue = lue;
        this.dateCreation = dateCreation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Boolean getLue() {
        return lue;
    }

    public void setLue(Boolean lue) {
        this.lue = lue;
    }

    public Instant getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Instant dateCreation) {
        this.dateCreation = dateCreation;
    }
}
