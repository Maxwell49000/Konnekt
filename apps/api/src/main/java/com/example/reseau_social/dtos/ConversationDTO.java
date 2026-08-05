package com.example.reseau_social.dtos;

import java.time.Instant;
import java.util.List;

import jakarta.validation.constraints.NotNull;

public class ConversationDTO {
    private String id;

    @NotNull(message = "L'ID de l'utilisateur 1 est requis")
    private Integer utilisateur1Id;

    @NotNull(message = "L'ID de l'utilisateur 2 est requis")
    private Integer utilisateur2Id;

    private Instant dateCreation;
    private List<MessageDTO> messages;

    // Constructors
    public ConversationDTO() {
    }

    public ConversationDTO(Integer utilisateur1Id, Integer utilisateur2Id) {
        this.utilisateur1Id = utilisateur1Id;
        this.utilisateur2Id = utilisateur2Id;
    }

    public ConversationDTO(String id, Integer utilisateur1Id, Integer utilisateur2Id, Instant dateCreation, List<MessageDTO> messages) {
        this.id = id;
        this.utilisateur1Id = utilisateur1Id;
        this.utilisateur2Id = utilisateur2Id;
        this.dateCreation = dateCreation;
        this.messages = messages;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getUtilisateur1Id() {
        return utilisateur1Id;
    }

    public void setUtilisateur1Id(Integer utilisateur1Id) {
        this.utilisateur1Id = utilisateur1Id;
    }

    public Integer getUtilisateur2Id() {
        return utilisateur2Id;
    }

    public void setUtilisateur2Id(Integer utilisateur2Id) {
        this.utilisateur2Id = utilisateur2Id;
    }

    public Instant getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Instant dateCreation) {
        this.dateCreation = dateCreation;
    }

    public List<MessageDTO> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageDTO> messages) {
        this.messages = messages;
    }
}
