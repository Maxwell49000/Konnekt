package com.example.reseau_social.models;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "skill")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSkill;

    @Column(name = "libelle", length = 50, nullable = false, unique = true)
    private String libelle;

    @ManyToMany(mappedBy = "skills")
    private Set<Utilisateur> utilisateurs = new HashSet<>();

    // Constructors
    public Skill() {
    }

    public Skill(String libelle) {
        this.libelle = libelle;
    }

    // Getters and Setters
    public Integer getIdSkill() {
        return idSkill;
    }

    public void setIdSkill(Integer idSkill) {
        this.idSkill = idSkill;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Set<Utilisateur> getUtilisateurs() {
        return utilisateurs;
    }

    public void setUtilisateurs(Set<Utilisateur> utilisateurs) {
        this.utilisateurs = utilisateurs;
    }
}
