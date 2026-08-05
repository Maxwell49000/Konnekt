package com.example.reseau_social.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.reseau_social.models.Utilisateur;

// Repository interface for Utilisateur model
@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {

    @Override
    @EntityGraph(attributePaths = "skills")
    List<Utilisateur> findAll();

    @EntityGraph(attributePaths = "skills")
    Optional<Utilisateur> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    @EntityGraph(attributePaths = "skills")
    List<Utilisateur> findByNomContainingIgnoreCase(String nom);

    @EntityGraph(attributePaths = "skills")
    List<Utilisateur> findByPrenomContainingIgnoreCase(String prenom);
    
    long countByVisibiliteProfil(Boolean visibilite);
    
    @EntityGraph(attributePaths = "skills")
    @Query("SELECT u FROM Utilisateur u WHERE u.visibiliteProfil = true")
    List<Utilisateur> findAllVisibleProfiles();

    @EntityGraph(attributePaths = "skills")
    @Query("SELECT u FROM Utilisateur u JOIN u.skills s WHERE s.idSkill = :skillId")
    List<Utilisateur> findUtilisateursBySkill(@Param("skillId") Integer skillId);
    
    @Query("SELECT DISTINCT u FROM Utilisateur u LEFT JOIN FETCH u.skills WHERE u.idUtilisateur = :id")
    Optional<Utilisateur> findByIdWithSkills(@Param("id") Integer id);
    
    @EntityGraph(attributePaths = "skills")
    @Query("SELECT u FROM Utilisateur u WHERE LOWER(u.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(u.prenom) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Utilisateur> searchByNomOrPrenom(@Param("searchTerm") String searchTerm);

    @EntityGraph(attributePaths = "skills")
    @Query("SELECT u FROM Utilisateur u WHERE SIZE(u.skills) >= :minSkills")
    List<Utilisateur> findUtilisateursByMinSkills(@Param("minSkills") int minSkills);

    @EntityGraph(attributePaths = "skills")
    @Query(value = "SELECT u.* FROM utilisateur u WHERE (SELECT COUNT(*) FROM se_connecte WHERE (id_demandeur = u.id_utilisateur OR id_destinataire = u.id_utilisateur) AND statut = 'ACCEPTEE') >= :minConnections", nativeQuery = true)
    List<Utilisateur> findUtilisateursByMinConnections(@Param("minConnections") int minConnections);
}
