package com.example.reseau_social.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.reseau_social.models.Experience;
import com.example.reseau_social.models.Utilisateur;
import com.example.reseau_social.repositories.ExperienceRepository;
import com.example.reseau_social.repositories.UtilisateurRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ExperienceService {

    @Autowired
    private ExperienceRepository experienceRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    // CREATE
    public Experience createExperience(Integer utilisateurId, Experience experience) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID: " + utilisateurId));
        experience.setUtilisateur(utilisateur);
        return experienceRepository.save(experience);
    }

    // READ
    public Optional<Experience> getExperienceById(Integer id) {
        return experienceRepository.findById(id);
    }

    public List<Experience> getAllExperiences() {
        return experienceRepository.findAll();
    }

    public List<Experience> getExperiencesByUtilisateur(Integer utilisateurId) {
        return experienceRepository.findByUtilisateurIdUtilisateur(utilisateurId);
    }

    public List<Experience> getExperiencesByUtilisateurOrderByDateDebut(Integer utilisateurId) {
        return experienceRepository.findByUtilisateurOrderByDateDebut(utilisateurId);
    }

    public List<Experience> getCurrentExperiencesByUtilisateur(Integer utilisateurId) {
        return experienceRepository.findCurrentExperiencesByUtilisateur(utilisateurId);
    }

    public List<Experience> getActiveExperiencesByUtilisateur(Integer utilisateurId) {
        return experienceRepository.findActiveExperiencesByUtilisateur(utilisateurId);
    }

    public List<Experience> findByEntreprise(String entreprise) {
        return experienceRepository.findByEntrepriseContainingIgnoreCase(entreprise);
    }

    public List<Experience> findByPoste(String poste) {
        return experienceRepository.findByPosteContainingIgnoreCase(poste);
    }

    public List<Experience> searchByEntrepriseOrPoste(String searchTerm) {
        return experienceRepository.searchByEntrepriseOrPoste(searchTerm);
    }

    // UPDATE
    public Experience updateExperience(Integer id, Experience experienceDetails) {
        return experienceRepository.findById(id)
                .map(experience -> {
                    experience.setPoste(experienceDetails.getPoste());
                    experience.setEntreprise(experienceDetails.getEntreprise());
                    experience.setDateDebut(experienceDetails.getDateDebut());
                    experience.setDateFin(experienceDetails.getDateFin());
                    experience.setDescription(experienceDetails.getDescription());
                    return experienceRepository.save(experience);
                })
                .orElseThrow(() -> new IllegalArgumentException("Experience non trouvée avec l'ID: " + id));
    }

    // DELETE
    public void deleteExperience(Integer id) {
        experienceRepository.deleteById(id);
    }

    public void deleteAllExperiencesByUtilisateur(Integer utilisateurId) {
        experienceRepository.deleteAllByUtilisateur(utilisateurId);
    }

    // STATISTICS
    public long countExperiencesByUtilisateur(Integer utilisateurId) {
        return experienceRepository.countByUtilisateurIdUtilisateur(utilisateurId);
    }

    public long countAllExperiences() {
        return experienceRepository.count();
    }
}
