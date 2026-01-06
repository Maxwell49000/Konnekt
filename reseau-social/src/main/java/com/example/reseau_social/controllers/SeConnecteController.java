package com.example.reseau_social.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.reseau_social.models.SeConnecte;
import com.example.reseau_social.models.SeConnecte.StatutConnexion;
import com.example.reseau_social.services.SeConnecteService;

@RestController
@RequestMapping("/api/connexions")
@CrossOrigin(origins = "*")
public class SeConnecteController {

    @Autowired
    private SeConnecteService seConnecteService;

    // CREATE - Send connection request
    @PostMapping("/request/{demandeurId}/{destinataireId}")
    public ResponseEntity<SeConnecte> sendConnectionRequest(@PathVariable Integer demandeurId, @PathVariable Integer destinataireId) {
        try {
            SeConnecte created = seConnecteService.sendConnectionRequest(demandeurId, destinataireId);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // READ - Get all
    @GetMapping
    public ResponseEntity<List<SeConnecte>> getAllSeConnecte() {
        List<SeConnecte> connexions = seConnecteService.getAllSeConnecte();
        return ResponseEntity.ok(connexions);
    }

    // READ - Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<SeConnecte> getSeConnecteById(@PathVariable Integer id) {
        Optional<SeConnecte> seConnecte = seConnecteService.getSeConnecteById(id);
        return seConnecte.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // READ - Get demandes envoyees
    @GetMapping("/demandes-envoyees/{utilisateurId}")
    public ResponseEntity<List<SeConnecte>> getDemandesEnvoyees(@PathVariable Integer utilisateurId) {
        List<SeConnecte> demandes = seConnecteService.getDemandesEnvoyees(utilisateurId);
        return ResponseEntity.ok(demandes);
    }

    // READ - Get demandes recues
    @GetMapping("/demandes-recues/{utilisateurId}")
    public ResponseEntity<List<SeConnecte>> getDemandesRecues(@PathVariable Integer utilisateurId) {
        List<SeConnecte> demandes = seConnecteService.getDemandesRecues(utilisateurId);
        return ResponseEntity.ok(demandes);
    }

    // READ - Get demandes en attente envoyees
    @GetMapping("/demandes-envoyees/{utilisateurId}/attente")
    public ResponseEntity<List<SeConnecte>> getDemandesEnvoyeesEnAttente(@PathVariable Integer utilisateurId) {
        List<SeConnecte> demandes = seConnecteService.getDemandesEnvoyeesEnAttente(utilisateurId);
        return ResponseEntity.ok(demandes);
    }

    // READ - Get demandes en attente recues
    @GetMapping("/demandes-recues/{utilisateurId}/attente")
    public ResponseEntity<List<SeConnecte>> getDemandesRecuesEnAttente(@PathVariable Integer utilisateurId) {
        List<SeConnecte> demandes = seConnecteService.getDemandesRecuesEnAttente(utilisateurId);
        return ResponseEntity.ok(demandes);
    }

    // READ - Get connection between two users
    @GetMapping("/between/{utilisateur1}/{utilisateur2}")
    public ResponseEntity<SeConnecte> getConnectionBetween(@PathVariable Integer utilisateur1, @PathVariable Integer utilisateur2) {
        Optional<SeConnecte> connexion = seConnecteService.getConnectionBetween(utilisateur1, utilisateur2);
        return connexion.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // READ - Get accepted connections
    @GetMapping("/accepted/{utilisateurId}")
    public ResponseEntity<List<SeConnecte>> getAcceptedConnections(@PathVariable Integer utilisateurId) {
        List<SeConnecte> connexions = seConnecteService.getAcceptedConnections(utilisateurId);
        return ResponseEntity.ok(connexions);
    }

    // UPDATE - Accept connection
    @PutMapping("/{id}/accept")
    public ResponseEntity<SeConnecte> acceptConnection(@PathVariable Integer id) {
        try {
            SeConnecte updated = seConnecteService.acceptConnection(id);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // UPDATE - Block connection
    @PutMapping("/{id}/block")
    public ResponseEntity<SeConnecte> blockConnection(@PathVariable Integer id) {
        try {
            SeConnecte updated = seConnecteService.blockConnection(id);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // UPDATE - Change status
    @PutMapping("/{id}/status")
    public ResponseEntity<SeConnecte> updateStatus(@PathVariable Integer id, @RequestParam StatutConnexion statut) {
        try {
            SeConnecte updated = seConnecteService.updateStatus(id, statut);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeConnecte(@PathVariable Integer id) {
        seConnecteService.deleteSeConnecte(id);
        return ResponseEntity.noContent().build();
    }

    // DELETE - All connections for utilisateur
    @DeleteMapping("/utilisateur/{utilisateurId}/all")
    public ResponseEntity<Void> deleteAllConnectionsForUtilisateur(@PathVariable Integer utilisateurId) {
        seConnecteService.deleteAllConnectionsForUtilisateur(utilisateurId);
        return ResponseEntity.noContent().build();
    }

    // STATISTICS
    @GetMapping("/stats/demandes-envoyees/{utilisateurId}")
    public ResponseEntity<Long> countDemandesEnvoyees(@PathVariable Integer utilisateurId) {
        long count = seConnecteService.countDemandesEnvoyees(utilisateurId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/demandes-recues/{utilisateurId}")
    public ResponseEntity<Long> countDemandesRecues(@PathVariable Integer utilisateurId) {
        long count = seConnecteService.countDemandesRecues(utilisateurId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/demandes-recues/{utilisateurId}/attente")
    public ResponseEntity<Long> countDemandesRecuesEnAttente(@PathVariable Integer utilisateurId) {
        long count = seConnecteService.countDemandesRecuesEnAttente(utilisateurId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/accepted/{utilisateurId}")
    public ResponseEntity<Long> countAcceptedConnections(@PathVariable Integer utilisateurId) {
        long count = seConnecteService.countAcceptedConnections(utilisateurId);
        return ResponseEntity.ok(count);
    }

    // Check if connected
    @GetMapping("/isConnected/{utilisateur1}/{utilisateur2}")
    public ResponseEntity<Boolean> isConnected(@PathVariable Integer utilisateur1, @PathVariable Integer utilisateur2) {
        boolean connected = seConnecteService.isConnected(utilisateur1, utilisateur2);
        return ResponseEntity.ok(connected);
    }
}
