package com.example.reseau_social.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateMessageDTO {
    @NotBlank(message = "Le contenu du message ne peut pas être vide")
    private String contenu;

    @NotNull(message = "L'ID de l'auteur est requis")
    private Integer auteurId;

    public CreateMessageDTO() {
    }

    public CreateMessageDTO(String contenu, Integer auteurId) {
        this.contenu = contenu;
        this.auteurId = auteurId;
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
}
