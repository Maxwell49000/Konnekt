package com.example.reseau_social.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.reseau_social.dtos.ExperienceDTO;
import com.example.reseau_social.dtos.SearchResultDTO;
import com.example.reseau_social.dtos.SkillDTO;
import com.example.reseau_social.dtos.UtilisateurDTO;
import com.example.reseau_social.models.Experience;
import com.example.reseau_social.models.Skill;
import com.example.reseau_social.models.Utilisateur;
import com.example.reseau_social.repositories.ExperienceRepository;
import com.example.reseau_social.repositories.SkillRepository;
import com.example.reseau_social.repositories.UtilisateurRepository;

import lombok.RequiredArgsConstructor;

// Service class for advanced search functionality
@Service
@RequiredArgsConstructor
public class SearchService {
    
    private final UtilisateurRepository utilisateurRepository;
    private final SkillRepository skillRepository;
    private final ExperienceRepository experienceRepository;
    
    public SearchResultDTO advancedSearch(String query, List<String> categories) {
        SearchResultDTO.SearchResultDTOBuilder result = SearchResultDTO.builder();
        int totalResults = 0;
        
        // Si pas de catégories spécifiées, chercher partout
        boolean searchAll = categories == null || categories.isEmpty();
        
        // Recherche utilisateurs
        List<UtilisateurDTO> utilisateurs = new ArrayList<>();
        if (searchAll || categories.contains("utilisateurs")) {
            List<Utilisateur> users = utilisateurRepository.searchByNomOrPrenom(query);
            utilisateurs = users.stream()
                    .map(this::toUtilisateurDTO)
                    .collect(Collectors.toList());
            totalResults += utilisateurs.size();
        }
        result.utilisateurs(utilisateurs);
        
        // Recherche compétences
        List<SkillDTO> skills = new ArrayList<>();
        if (searchAll || categories.contains("skills")) {
            List<Skill> skillList = skillRepository.findByLibelleContainingIgnoreCase(query);
            skills = skillList.stream()
                    .map(s -> new SkillDTO(s.getIdSkill(), s.getLibelle()))
                    .collect(Collectors.toList());
            totalResults += skills.size();
        }
        result.skills(skills);
        
        // Recherche expériences
        List<ExperienceDTO> experiences = new ArrayList<>();
        if (searchAll || categories.contains("experiences")) {
            List<Experience> experienceList = experienceRepository.searchByEntrepriseOrPoste(query);
            experiences = experienceList.stream()
                    .map(this::toExperienceDTO)
                    .collect(Collectors.toList());
            totalResults += experiences.size();
        }
        result.experiences(experiences);
        
        return result.totalResults(totalResults).build();
    }
    
    private UtilisateurDTO toUtilisateurDTO(Utilisateur u) {
        if (u == null) return null;
        UtilisateurDTO dto = new UtilisateurDTO();
        dto.setId(u.getIdUtilisateur());
        dto.setEmail(u.getEmail());
        dto.setNom(u.getNom());
        dto.setPrenom(u.getPrenom());
        dto.setTitreProfessionnel(u.getTitreProfessionnel());
        dto.setResume(u.getResume());
        dto.setVisibiliteProfil(u.getVisibiliteProfil());
        if (u.getSkills() != null) {
            List<SkillDTO> skills = u.getSkills().stream()
                    .map(s -> new SkillDTO(s.getIdSkill(), s.getLibelle()))
                    .collect(Collectors.toList());
            dto.setSkills(skills);
        }
        return dto;
    }
    
    private ExperienceDTO toExperienceDTO(Experience e) {
        ExperienceDTO dto = new ExperienceDTO();
        dto.setIdExperience(e.getIdExperience());
        dto.setPoste(e.getPoste());
        dto.setEntreprise(e.getEntreprise());
        dto.setDateDebut(e.getDateDebut());
        dto.setDateFin(e.getDateFin());
        dto.setDescription(e.getDescription());
        return dto;
    }
}