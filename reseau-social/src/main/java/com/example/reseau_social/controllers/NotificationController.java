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

import com.example.reseau_social.models.Notification;
import com.example.reseau_social.services.NotificationService;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public ResponseEntity<Notification> create(@RequestBody Notification n) {
        Notification created = notificationService.createNotification(n);
        return ResponseEntity.created(URI.create("/api/notifications/" + created.getId())).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Notification>> list() {
        return ResponseEntity.ok(notificationService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notification> get(@PathVariable String id) {
        Optional<Notification> n = notificationService.getById(id);
        return n.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> byUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(notificationService.findByUser(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Notification> update(@PathVariable String id, @RequestBody Notification details) {
        try {
            Notification updated = notificationService.updateNotification(id, details);
            return ResponseEntity.ok(updated);
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
