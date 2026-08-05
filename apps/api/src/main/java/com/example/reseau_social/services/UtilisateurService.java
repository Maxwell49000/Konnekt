package com.example.reseau_social.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.reseau_social.models.Skill;
import com.example.reseau_social.models.Utilisateur;
import com.example.reseau_social.repositories.SkillRepository;
import com.example.reseau_social.repositories.UtilisateurRepository;

import jakarta.transaction.Transactional;

// Service class for managing Utilisateur entities
@Service
@Transactional
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private SkillRepository skillRepository;

    // CREATE
    public Utilisateur createUtilisateur(Utilisateur utilisateur) {
        if (utilisateurRepository.existsByEmail(utilisateur.getEmail())) {
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà");
        }
        return utilisateurRepository.save(utilisateur);
    }

    // READ
    public Optional<Utilisateur> getUtilisateurById(Integer id) {
        return utilisateurRepository.findByIdWithSkills(id);
    }

    public Optional<Utilisateur> getUtilisateurByEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    public List<Utilisateur> getAllVisibleProfiles() {
        return utilisateurRepository.findAllVisibleProfiles();
    }

    public List<Utilisateur> searchByNomOrPrenom(String searchTerm) {
        return utilisateurRepository.searchByNomOrPrenom(searchTerm);
    }

    public List<Utilisateur> findByNom(String nom) {
        return utilisateurRepository.findByNomContainingIgnoreCase(nom);
    }

    public List<Utilisateur> findByPrenom(String prenom) {
        return utilisateurRepository.findByPrenomContainingIgnoreCase(prenom);
    }

    public List<Utilisateur> findBySkill(Integer skillId) {
        return utilisateurRepository.findUtilisateursBySkill(skillId);
    }

    public List<Utilisateur> findByMinSkills(int minSkills) {
        return utilisateurRepository.findUtilisateursByMinSkills(minSkills);
    }

    public List<Utilisateur> findByMinConnections(int minConnections) {
        return utilisateurRepository.findUtilisateursByMinConnections(minConnections);
    }

    // UPDATE
    public Utilisateur updateUtilisateur(Integer id, Utilisateur utilisateurDetails) {
        return utilisateurRepository.findById(id)
                .map(utilisateur -> {
                    if (utilisateurDetails.getNom() != null) {
                        utilisateur.setNom(utilisateurDetails.getNom());
                    }
                    if (utilisateurDetails.getPrenom() != null) {
                        utilisateur.setPrenom(utilisateurDetails.getPrenom());
                    }
                    // Update email if provided and different
                    String newEmail = utilisateurDetails.getEmail();
                    if (newEmail != null && !newEmail.equals(utilisateur.getEmail())) {
                        if (utilisateurRepository.existsByEmail(newEmail)) {
                            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà");
                        }
                        utilisateur.setEmail(newEmail);
                    }
                    if (utilisateurDetails.getTitreProfessionnel() != null) {
                        utilisateur.setTitreProfessionnel(utilisateurDetails.getTitreProfessionnel());
                    }
                    if (utilisateurDetails.getResume() != null) {
                        utilisateur.setResume(utilisateurDetails.getResume());
                    }
                    if (utilisateurDetails.getVisibiliteProfil() != null) {
                        utilisateur.setVisibiliteProfil(utilisateurDetails.getVisibiliteProfil());
                    }
                    return utilisateurRepository.save(utilisateur);
                })
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID: " + id));
    }

    public Utilisateur updateVisibility(Integer id, Boolean visible) {
        return utilisateurRepository.findById(id)
                .map(utilisateur -> {
                    utilisateur.setVisibiliteProfil(visible);
                    return utilisateurRepository.save(utilisateur);
                })
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID: " + id));
    }

    // UPDATE SKILLS
    public Utilisateur updateSkills(Integer id, Set<Skill> skills) {
        return utilisateurRepository.findById(id)
                .map(utilisateur -> {
                    utilisateur.setSkills(skills);
                    return utilisateurRepository.save(utilisateur);
                })
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID: " + id));
    }

    // DELETE
    public void deleteUtilisateur(Integer id) {
        utilisateurRepository.deleteById(id);
    }

    // STATISTICS
    public long countVisibleProfiles() {
        return utilisateurRepository.countByVisibiliteProfil(true);
    }

    public long countAllUtilisateurs() {
        return utilisateurRepository.count();
    }

    public boolean emailExists(String email) {
        return utilisateurRepository.existsByEmail(email);
    }
}
