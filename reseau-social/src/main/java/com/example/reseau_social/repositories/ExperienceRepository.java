package com.example.reseau_social.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.reseau_social.models.Experience;

import jakarta.transaction.Transactional;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Integer> {
    
    List<Experience> findByUtilisateurIdUtilisateur(Integer utilisateurId);
    
    long countByUtilisateurIdUtilisateur(Integer utilisateurId);
    
    List<Experience> findByEntrepriseContainingIgnoreCase(String entreprise);
    
    List<Experience> findByPosteContainingIgnoreCase(String poste);
    
    @Query("SELECT e FROM Experience e WHERE e.utilisateur.idUtilisateur = :utilisateurId ORDER BY e.dateDebut DESC")
    List<Experience> findByUtilisateurOrderByDateDebut(@Param("utilisateurId") Integer utilisateurId);
    
    @Query("SELECT e FROM Experience e WHERE e.dateFin IS NULL AND e.utilisateur.idUtilisateur = :utilisateurId")
    List<Experience> findCurrentExperiencesByUtilisateur(@Param("utilisateurId") Integer utilisateurId);
    
    @Query("SELECT e FROM Experience e WHERE LOWER(e.entreprise) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(e.poste) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Experience> searchByEntrepriseOrPoste(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT e FROM Experience e WHERE e.utilisateur.idUtilisateur = :utilisateurId AND e.dateDebut <= CURRENT_DATE AND (e.dateFin IS NULL OR e.dateFin >= CURRENT_DATE)")
    List<Experience> findActiveExperiencesByUtilisateur(@Param("utilisateurId") Integer utilisateurId);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM Experience e WHERE e.utilisateur.idUtilisateur = :utilisateurId")
    void deleteAllByUtilisateur(@Param("utilisateurId") Integer utilisateurId);
}
