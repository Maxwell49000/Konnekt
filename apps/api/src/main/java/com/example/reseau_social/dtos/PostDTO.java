package com.example.reseau_social.dtos;

import java.time.Instant;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PostDTO {
    private String id;

    @NotBlank(message = "Le contenu du post ne peut pas être vide")
    private String contenu;

    @NotNull(message = "L'ID de l'auteur est requis")
    private Integer auteurId;

    private List<Integer> likes;
    private Instant dateCreation;
    private List<CommentDTO> comments;
    private List<String> media;
    private String visibility;

    // Constructors
    public PostDTO() {
    }

    public PostDTO(String contenu, Integer auteurId) {
        this.contenu = contenu;
        this.auteurId = auteurId;
    }

    public PostDTO(String id, String contenu, Integer auteurId, List<Integer> likes, Instant dateCreation, List<CommentDTO> comments) {
        this.id = id;
        this.contenu = contenu;
        this.auteurId = auteurId;
        this.likes = likes;
        this.dateCreation = dateCreation;
        this.comments = comments;
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

    public List<Integer> getLikes() {
        return likes;
    }

    public void setLikes(List<Integer> likes) {
        this.likes = likes;
    }

    public Instant getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Instant dateCreation) {
        this.dateCreation = dateCreation;
    }

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }

    public List<String> getMedia() {
        return media;
    }

    public void setMedia(List<String> media) {
        this.media = media;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }
}
