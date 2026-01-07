package com.example.reseau_social.services;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.reseau_social.models.Notification;
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

    @Autowired
    private NotificationService notificationService;

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
            SeConnecte existing = existingConnection.get();
            // Si la connexion est acceptée, on ne peut pas renvoyer de demande
            if (existing.getStatut() == StatutConnexion.ACCEPTEE) {
                throw new IllegalArgumentException("Vous êtes déjà connecté à cet utilisateur");
            }
            // Si la connexion est en attente ou refusée, réactiver la demande
            existing.setStatut(StatutConnexion.EN_ATTENTE);
            existing.setDemandeur(demandeur);
            existing.setDestinataire(destinataire);
            SeConnecte saved = seConnecteRepository.save(existing);
            
            // Create notification for the recipient
            Notification notification = new Notification();
            notification.setUserId(destinataireId);
            notification.setType("connection_request");
            notification.setContent(demandeur.getPrenom() + " " + demandeur.getNom() + " a envoyé une demande de connexion");
            notification.setRead(false);
            notification.setCreatedAt(Instant.now());
            notificationService.createNotification(notification);
            
            return saved;
        }

        SeConnecte seConnecte = new SeConnecte(demandeur, destinataire);
        SeConnecte saved = seConnecteRepository.save(seConnecte);
        
        // Create notification for the recipient
        Notification notification = new Notification();
        notification.setUserId(destinataireId);
        notification.setType("connection_request");
        notification.setContent(demandeur.getPrenom() + " " + demandeur.getNom() + " a envoyé une demande de connexion");
        notification.setRead(false);
        notification.setCreatedAt(Instant.now());
        notificationService.createNotification(notification);
        
        return saved;
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

    public List<SeConnecte> getAllConnectionsForUtilisateur(Integer utilisateurId) {
        // Retourne toutes les connexions (peu importe le statut) pour un utilisateur
        List<SeConnecte> asRequester = seConnecteRepository.findByDemandeurIdUtilisateur(utilisateurId);
        List<SeConnecte> asRecipient = seConnecteRepository.findByDestinataireIdUtilisateur(utilisateurId);
        asRequester.addAll(asRecipient);
        return asRequester;
    }

    // UPDATE - Accepter une demande
    public SeConnecte acceptConnection(Integer seConnecteId) {
        return seConnecteRepository.findById(seConnecteId)
                .map(seConnecte -> {
                    seConnecte.setStatut(StatutConnexion.ACCEPTEE);
                    SeConnecte saved = seConnecteRepository.save(seConnecte);
                    
                    // Create notification for the requester
                    Utilisateur acceptor = seConnecte.getDestinataire();
                    Integer requesterId = seConnecte.getDemandeur().getIdUtilisateur();
                    
                    Notification notification = new Notification();
                    notification.setUserId(requesterId);
                    notification.setType("connection_accepted");
                    notification.setContent(acceptor.getPrenom() + " " + acceptor.getNom() + " a accepté votre demande de connexion");
                    notification.setRead(false);
                    notification.setCreatedAt(Instant.now());
                    notificationService.createNotification(notification);
                    
                    return saved;
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
