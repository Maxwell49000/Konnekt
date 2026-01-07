package com.example.reseau_social.dtos;


public class CreateCommentDTO {
    private String contenu;
    private Integer auteurId;

    // Constructors
    public CreateCommentDTO() {
    }

    public CreateCommentDTO(String contenu, Integer auteurId) {
        this.contenu = contenu;
        this.auteurId = auteurId;
    }

    // Getters and Setters
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
}
