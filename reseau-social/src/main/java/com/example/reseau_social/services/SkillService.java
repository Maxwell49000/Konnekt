package com.example.reseau_social.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.reseau_social.models.Skill;
import com.example.reseau_social.repositories.SkillRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class SkillService {

    @Autowired
    private SkillRepository skillRepository;

    // CREATE
    public Skill createSkill(Skill skill) {
        if (skillRepository.existsByLibelle(skill.getLibelle())) {
            throw new IllegalArgumentException("Un skill avec ce libellé existe déjà");
        }
        return skillRepository.save(skill);
    }

    // READ
    public Optional<Skill> getSkillById(Integer id) {
        return skillRepository.findById(id);
    }

    public Optional<Skill> getSkillByLibelle(String libelle) {
        return skillRepository.findByLibelle(libelle);
    }

    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    public List<Skill> getMostUsedSkills() {
        return skillRepository.findMostUsedSkills();
    }

    public List<Skill> findByLibelle(String libelle) {
        return skillRepository.findByLibelleContainingIgnoreCase(libelle);
    }

    public List<Skill> findSkillsWithMinUtilisateurs(int minCount) {
        return skillRepository.findSkillsWithMinUtilisateurs(minCount);
    }

    // UPDATE
    public Skill updateSkill(Integer id, Skill skillDetails) {
        return skillRepository.findById(id)
                .map(skill -> {
                    skill.setLibelle(skillDetails.getLibelle());
                    return skillRepository.save(skill);
                })
                .orElseThrow(() -> new IllegalArgumentException("Skill non trouvé avec l'ID: " + id));
    }

    // DELETE
    public void deleteSkill(Integer id) {
        skillRepository.deleteById(id);
    }

    // STATISTICS
    public long countAllSkills() {
        return skillRepository.count();
    }

    public boolean skilleExists(String libelle) {
        return skillRepository.existsByLibelle(libelle);
    }
}
