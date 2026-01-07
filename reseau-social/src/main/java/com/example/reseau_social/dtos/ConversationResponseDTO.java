package com.example.reseau_social.dtos;

import java.time.Instant;
import java.util.List;

public class ConversationResponseDTO {
    private String id;
    private Integer utilisateur1Id;
    private Integer utilisateur2Id;
    private Instant dateCreation;
    private List<MessageDTO> messages;

    public ConversationResponseDTO() {
    }

    public ConversationResponseDTO(String id, Integer utilisateur1Id, Integer utilisateur2Id, Instant dateCreation, List<MessageDTO> messages) {
        this.id = id;
        this.utilisateur1Id = utilisateur1Id;
        this.utilisateur2Id = utilisateur2Id;
        this.dateCreation = dateCreation;
        this.messages = messages;
    }

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
