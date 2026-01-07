package com.example.reseau_social.dtos;

import java.time.Instant;

public class CommentDTO {
    private String id;
    private String contenu;
    private Integer auteurId;
    private Instant dateCreation;

    public CommentDTO() {
    }

    public CommentDTO(String id, String contenu, Integer auteurId, Instant dateCreation) {
        this.id = id;
        this.contenu = contenu;
        this.auteurId = auteurId;
        this.dateCreation = dateCreation;
    }

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
