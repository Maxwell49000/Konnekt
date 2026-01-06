package com.example.reseau_social.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.reseau_social.models.Skill;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Integer> {
    
    Optional<Skill> findByLibelle(String libelle);
    
    boolean existsByLibelle(String libelle);
    
    List<Skill> findByLibelleContainingIgnoreCase(String libelle);
    
    @Query("SELECT s FROM Skill s ORDER BY SIZE(s.utilisateurs) DESC")
    List<Skill> findMostUsedSkills();
    
    @Query("SELECT s FROM Skill s WHERE SIZE(s.utilisateurs) >= :minCount")
    List<Skill> findSkillsWithMinUtilisateurs(@Param("minCount") int minCount);
}
