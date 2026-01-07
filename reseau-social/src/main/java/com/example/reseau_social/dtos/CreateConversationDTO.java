package com.example.reseau_social.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateConversationDTO {
    @NotNull(message = "L'ID de l'utilisateur 1 est requis")
    private Integer utilisateur1Id;

    @NotNull(message = "L'ID de l'utilisateur 2 est requis")
    private Integer utilisateur2Id;

    public CreateConversationDTO() {
    }

    public CreateConversationDTO(Integer utilisateur1Id, Integer utilisateur2Id) {
        this.utilisateur1Id = utilisateur1Id;
        this.utilisateur2Id = utilisateur2Id;
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
}
