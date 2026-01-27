package com.example.reseau_social.dtos;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CommentDTO {
    private String id;

    @NotBlank(message = "Le contenu du commentaire ne peut pas être vide")
    private String contenu;

    @NotNull(message = "L'ID de l'auteur est requis")
    private Integer auteurId;

    private Instant dateCreation;

    // Constructors
    public CommentDTO() {
    }

    public CommentDTO(String contenu, Integer auteurId) {
        this.contenu = contenu;
        this.auteurId = auteurId;
    }

    public CommentDTO(String id, String contenu, Integer auteurId, Instant dateCreation) {
        this.id = id;
        this.contenu = contenu;
        this.auteurId = auteurId;
        this.dateCreation = dateCreation;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Integer getAuteurId() {
        return auteurId;
    }

    public void setAuteurId(Integer auteurId) {
        this.auteurId = auteurId;
    }

    public Instant getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Instant dateCreation) {
        this.dateCreation = dateCreation;
    }
}
