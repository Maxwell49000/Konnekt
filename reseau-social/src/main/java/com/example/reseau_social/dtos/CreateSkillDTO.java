package com.example.reseau_social.dtos;

import jakarta.validation.constraints.NotBlank;

public class CreateSkillDTO {
    @NotBlank(message = "Le libellé est requis")
    private String libelle;

    // Constructors
    public CreateSkillDTO() {
    }

    public CreateSkillDTO(String libelle) {
        this.libelle = libelle;
    }

    // Getters and Setters
    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }
}
