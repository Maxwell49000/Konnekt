package com.example.reseau_social.controllers;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.reseau_social.dtos.NotificationDTO;
import com.example.reseau_social.models.Notification;
import com.example.reseau_social.services.NotificationService;

import jakarta.validation.Valid;

// Controller class for managing notifications
@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationDTO> create(@Valid @RequestBody NotificationDTO dto) {
        Notification n = new Notification();
        n.setUserId(dto.getUtilisateurId());
        n.setType(dto.getType());
        n.setContent(dto.getMessage());
        n.setRead(false);
        n.setCreatedAt(java.time.Instant.now());
        
        Notification created = notificationService.createNotification(n);
        NotificationDTO response = toDTO(created);
        return ResponseEntity.created(java.net.URI.create("/api/notifications/" + created.getId())).body(response);
    }

    @GetMapping
    public ResponseEntity<java.util.List<NotificationDTO>> list() {
        java.util.List<Notification> notifications = notificationService.getAll();
        java.util.List<NotificationDTO> responses = notifications.stream().map(this::toDTO).toList();
        return ResponseEntity.ok(responses);
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<NotificationDTO> get(@PathVariable String id) {
        Optional<Notification> n = notificationService.getById(id);
        return n.map(notif -> ResponseEntity.ok(toDTO(notif)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<java.util.List<NotificationDTO>> byUser(@PathVariable Integer userId) {
        java.util.List<Notification> notifications = notificationService.findByUser(userId);
        java.util.List<NotificationDTO> responses = notifications.stream().map(this::toDTO).toList();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificationDTO> update(@PathVariable String id, @RequestBody NotificationDTO dto) {
        try {
            Notification updated = notificationService.updateNotification(id, dto);
            NotificationDTO response = toDTO(updated);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/user/{userId}/mark-all-read")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Integer userId) {
        try {
            notificationService.markAllAsRead(userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    // Helper mapper
    private NotificationDTO toDTO(Notification n) {
        if (n == null) return null;
        NotificationDTO dto = new NotificationDTO();
        dto.setId(n.getId());
        dto.setUtilisateurId(n.getUserId());
        dto.setType(n.getType());
        dto.setMessage(n.getContent());
        dto.setLue(n.getRead());
        dto.setDateCreation(n.getCreatedAt());
        return dto;
    }
}
