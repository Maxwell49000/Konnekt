package com.example.reseau_social.dtos;

import java.time.Instant;
import java.util.List;

public class PostResponseDTO {
    private String id;
    private String contenu;
    private Integer auteurId;
    private List<Integer> likes;
    private Instant dateCreation;
    private List<CommentDTO> comments;

    public PostResponseDTO() {
    }

    public PostResponseDTO(String id, String contenu, Integer auteurId, List<Integer> likes, Instant dateCreation, List<CommentDTO> comments) {
        this.id = id;
        this.contenu = contenu;
        this.auteurId = auteurId;
        this.likes = likes;
        this.dateCreation = dateCreation;
        this.comments = comments;
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
}
