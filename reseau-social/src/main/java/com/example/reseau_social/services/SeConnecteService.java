package com.example.reseau_social.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.reseau_social.models.SeConnecte;
import com.example.reseau_social.models.SeConnecte.StatutConnexion;
import com.example.reseau_social.models.Utilisateur;
import com.example.reseau_social.repositories.SeConnecteRepository;
import com.example.reseau_social.repositories.UtilisateurRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class SeConnecteService {

    @Autowired
    private SeConnecteRepository seConnecteRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    // CREATE - Envoyer une demande de connexion
    public SeConnecte sendConnectionRequest(Integer demandeurId, Integer destinataireId) {
        if (demandeurId.equals(destinataireId)) {
            throw new IllegalArgumentException("Impossible d'envoyer une demande à soi-même");
        }

        Utilisateur demandeur = utilisateurRepository.findById(demandeurId)
                .orElseThrow(() -> new IllegalArgumentException("Demandeur non trouvé avec l'ID: " + demandeurId));
        Utilisateur destinataire = utilisateurRepository.findById(destinataireId)
                .orElseThrow(() -> new IllegalArgumentException("Destinataire non trouvé avec l'ID: " + destinataireId));

        // Vérifier s'il existe déjà une connexion
        Optional<SeConnecte> existingConnection = seConnecteRepository.findConnectionBetween(demandeurId, destinataireId);
        if (existingConnection.isPresent()) {
            throw new IllegalArgumentException("Une connexion existe déjà entre ces utilisateurs");
        }

        SeConnecte seConnecte = new SeConnecte(demandeur, destinataire);
        return seConnecteRepository.save(seConnecte);
    }

    // READ
    public Optional<SeConnecte> getSeConnecteById(Integer id) {
        return seConnecteRepository.findById(id);
    }

    public List<SeConnecte> getAllSeConnecte() {
        return seConnecteRepository.findAll();
    }

    public List<SeConnecte> getDemandesEnvoyees(Integer utilisateurId) {
        return seConnecteRepository.findByDemandeurIdUtilisateur(utilisateurId);
    }

    public List<SeConnecte> getDemandesRecues(Integer utilisateurId) {
        return seConnecteRepository.findByDestinataireIdUtilisateur(utilisateurId);
    }

    public List<SeConnecte> getDemandesEnvoyeesEnAttente(Integer utilisateurId) {
        return seConnecteRepository.findByDemandeurIdUtilisateurAndStatut(utilisateurId, StatutConnexion.EN_ATTENTE);
    }

    public List<SeConnecte> getDemandesRecuesEnAttente(Integer utilisateurId) {
        return seConnecteRepository.findByDestinataireIdUtilisateurAndStatut(utilisateurId, StatutConnexion.EN_ATTENTE);
    }

    public Optional<SeConnecte> getConnectionBetween(Integer utilisateur1, Integer utilisateur2) {
        return seConnecteRepository.findConnectionBetween(utilisateur1, utilisateur2);
    }

    public List<SeConnecte> getAcceptedConnections(Integer utilisateurId) {
        return seConnecteRepository.findAllAcceptedConnectionsForUtilisateur(utilisateurId);
    }

    // UPDATE - Accepter une demande
    public SeConnecte acceptConnection(Integer seConnecteId) {
        return seConnecteRepository.findById(seConnecteId)
                .map(seConnecte -> {
                    seConnecte.setStatut(StatutConnexion.ACCEPTEE);
                    return seConnecteRepository.save(seConnecte);
                })
                .orElseThrow(() -> new IllegalArgumentException("Connexion non trouvée avec l'ID: " + seConnecteId));
    }

    // UPDATE - Bloquer une demande
    public SeConnecte blockConnection(Integer seConnecteId) {
        return seConnecteRepository.findById(seConnecteId)
                .map(seConnecte -> {
                    seConnecte.setStatut(StatutConnexion.BLOQUEE);
                    return seConnecteRepository.save(seConnecte);
                })
                .orElseThrow(() -> new IllegalArgumentException("Connexion non trouvée avec l'ID: " + seConnecteId));
    }

    // UPDATE - Changer le statut
    public SeConnecte updateStatus(Integer seConnecteId, StatutConnexion statut) {
        return seConnecteRepository.findById(seConnecteId)
                .map(seConnecte -> {
                    seConnecte.setStatut(statut);
                    return seConnecteRepository.save(seConnecte);
                })
                .orElseThrow(() -> new IllegalArgumentException("Connexion non trouvée avec l'ID: " + seConnecteId));
    }

    // DELETE
    public void deleteSeConnecte(Integer id) {
        seConnecteRepository.deleteById(id);
    }

    public void deleteAllConnectionsForUtilisateur(Integer utilisateurId) {
        seConnecteRepository.deleteAllConnectionsForUtilisateur(utilisateurId);
    }

    // STATISTICS
    public long countDemandesEnvoyees(Integer utilisateurId) {
        return seConnecteRepository.countByDemandeurIdUtilisateur(utilisateurId);
    }

    public long countDemandesRecues(Integer utilisateurId) {
        return seConnecteRepository.countByDestinataireIdUtilisateur(utilisateurId);
    }

    public long countDemandesRecuesEnAttente(Integer utilisateurId) {
        return seConnecteRepository.countByDestinataireIdUtilisateurAndStatut(utilisateurId, StatutConnexion.EN_ATTENTE);
    }

    public long countAcceptedConnections(Integer utilisateurId) {
        return seConnecteRepository.countAcceptedConnectionsForUtilisateur(utilisateurId);
    }

    public boolean isConnected(Integer utilisateur1, Integer utilisateur2) {
        Optional<SeConnecte> connection = seConnecteRepository.findConnectionBetween(utilisateur1, utilisateur2);
        return connection.isPresent() && connection.get().getStatut() == StatutConnexion.ACCEPTEE;
    }
}
