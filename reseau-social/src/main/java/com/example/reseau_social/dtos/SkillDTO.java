package com.example.reseau_social.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;

public class SkillDTO {
    @JsonProperty("idSkill")
    private Integer id;

    @NotBlank(message = "Le libellé est requis")
    private String libelle;

    // Constructors
    public SkillDTO() {
    }

    public SkillDTO(String libelle) {
        this.libelle = libelle;
    }

    public SkillDTO(Integer id, String libelle) {
        this.id = id;
        this.libelle = libelle;
    }

    // Getters and Setters
    @JsonIgnore
    public Integer getId() {
        return id;
    }

    public Integer getIdSkill() {
        return id;
    }

    @JsonIgnore
    public void setId(Integer id) {
        this.id = id;
    }

    public void setIdSkill(Integer idSkill) {
        this.id = idSkill;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }
}
