package com.example.reseau_social.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.reseau_social.models.SeConnecte;
import com.example.reseau_social.models.SeConnecte.StatutConnexion;

import jakarta.transaction.Transactional;

@Repository
public interface SeConnecteRepository extends JpaRepository<SeConnecte, Integer> {
    
    List<SeConnecte> findByDemandeurIdUtilisateur(Integer demandeurId);
    
    List<SeConnecte> findByDestinataireIdUtilisateur(Integer destinataireId);
    
    long countByDemandeurIdUtilisateur(Integer demandeurId);
    
    long countByDestinataireIdUtilisateur(Integer destinataireId);
    
    List<SeConnecte> findByDemandeurIdUtilisateurAndStatut(Integer demandeurId, StatutConnexion statut);
    
    List<SeConnecte> findByDestinataireIdUtilisateurAndStatut(Integer destinataireId, StatutConnexion statut);
    
    long countByDestinataireIdUtilisateurAndStatut(Integer destinataireId, StatutConnexion statut);
    
    @Query("SELECT sc FROM SeConnecte sc WHERE " +
           "(sc.demandeur.idUtilisateur = :utilisateur1 AND sc.destinataire.idUtilisateur = :utilisateur2) OR " +
           "(sc.demandeur.idUtilisateur = :utilisateur2 AND sc.destinataire.idUtilisateur = :utilisateur1)")
    Optional<SeConnecte> findConnectionBetween(@Param("utilisateur1") Integer utilisateur1, 
                                                 @Param("utilisateur2") Integer utilisateur2);
    
    @Query("SELECT sc FROM SeConnecte sc WHERE sc.demandeur.idUtilisateur = :demandeurId AND " +
           "sc.destinataire.idUtilisateur = :destinataireId AND sc.statut = :statut")
    Optional<SeConnecte> findByDemandeurAndDestinataireAndStatut(
            @Param("demandeurId") Integer demandeurId,
            @Param("destinataireId") Integer destinataireId,
            @Param("statut") StatutConnexion statut);
    
    @Query(value = "SELECT COUNT(*) FROM se_connecte WHERE (id_demandeur = :utilisateurId OR id_destinataire = :utilisateurId) AND statut = 'ACCEPTEE'", nativeQuery = true)
    long countAcceptedConnectionsForUtilisateur(@Param("utilisateurId") Integer utilisateurId);
    
    @Query("SELECT sc FROM SeConnecte sc WHERE (sc.demandeur.idUtilisateur = :utilisateurId OR sc.destinataire.idUtilisateur = :utilisateurId) AND sc.statut = 'ACCEPTEE'")
    List<SeConnecte> findAllAcceptedConnectionsForUtilisateur(@Param("utilisateurId") Integer utilisateurId);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM SeConnecte sc WHERE sc.demandeur.idUtilisateur = :utilisateurId OR sc.destinataire.idUtilisateur = :utilisateurId")
    void deleteAllConnectionsForUtilisateur(@Param("utilisateurId") Integer utilisateurId);
}
