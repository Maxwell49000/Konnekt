package com.example.reseau_social.controllers;

import java.net.URI;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.reseau_social.dtos.CreateNotificationDTO;
import com.example.reseau_social.dtos.NotificationResponseDTO;
import com.example.reseau_social.models.Notification;
import com.example.reseau_social.services.NotificationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationResponseDTO> create(@Valid @RequestBody CreateNotificationDTO dto) {
        Notification n = notificationService.createNotificationFromDTO(dto);
        Notification created = notificationService.createNotification(n);
        NotificationResponseDTO response = notificationService.notificationToResponseDTO(created);
        return ResponseEntity.created(URI.create("/api/notifications/" + created.getId())).body(response);
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> list() {
        List<Notification> notifications = notificationService.getAll();
        List<NotificationResponseDTO> responses = notificationService.notificationsToResponseDTOList(notifications);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDTO> get(@PathVariable String id) {
        Optional<Notification> n = notificationService.getById(id);
        return n.map(notif -> ResponseEntity.ok(notificationService.notificationToResponseDTO(notif)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponseDTO>> byUser(@PathVariable Integer userId) {
        List<Notification> notifications = notificationService.findByUser(userId);
        List<NotificationResponseDTO> responses = notificationService.notificationsToResponseDTOList(notifications);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificationResponseDTO> update(@PathVariable String id, @RequestBody Notification details) {
        try {
            Notification updated = notificationService.updateNotification(id, details);
            NotificationResponseDTO response = notificationService.notificationToResponseDTO(updated);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}
