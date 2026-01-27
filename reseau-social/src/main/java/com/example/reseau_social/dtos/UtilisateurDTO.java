package com.example.reseau_social.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;

public class UtilisateurDTO {
    @JsonProperty("idUtilisateur")
    private Integer id;

    private String nom;

    private String prenom;

    @Email(message = "L'email doit être valide")
    private String email;

    private String titreProfessionnel;
    private String resume;
    private Boolean visibiliteProfil;
    private List<SkillDTO> skills;

    // Constructors
    public UtilisateurDTO() {
    }

    public UtilisateurDTO(String nom, String prenom, String email) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.visibiliteProfil = true;
    }

    public UtilisateurDTO(Integer id, String nom, String prenom, String email, String titreProfessionnel,
            String resume, Boolean visibiliteProfil, List<SkillDTO> skills) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.titreProfessionnel = titreProfessionnel;
        this.resume = resume;
        this.visibiliteProfil = visibiliteProfil;
        this.skills = skills;
    }

    // Getters and Setters
    @JsonIgnore
    public Integer getId() {
        return id;
    }

    public Integer getIdUtilisateur() {
        return id;
    }

    @JsonIgnore
    public void setId(Integer id) {
        this.id = id;
    }

    public void setIdUtilisateur(Integer idUtilisateur) {
        this.id = idUtilisateur;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitreProfessionnel() {
        return titreProfessionnel;
    }

    public void setTitreProfessionnel(String titreProfessionnel) {
        this.titreProfessionnel = titreProfessionnel;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public Boolean getVisibiliteProfil() {
        return visibiliteProfil;
    }

    public void setVisibiliteProfil(Boolean visibiliteProfil) {
        this.visibiliteProfil = visibiliteProfil;
    }

    public List<SkillDTO> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillDTO> skills) {
        this.skills = skills;
    }
}
