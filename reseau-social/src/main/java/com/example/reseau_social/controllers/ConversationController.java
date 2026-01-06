package com.example.reseau_social.controllers;

import java.net.URI;
import java.time.Instant;
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

import com.example.reseau_social.models.Conversation;
import com.example.reseau_social.models.Message;
import com.example.reseau_social.services.ConversationService;

@RestController
@RequestMapping("/api/conversations")
@CrossOrigin(origins = "*")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @PostMapping
    public ResponseEntity<Conversation> create(@RequestBody Conversation conv) {
        conv.setCreatedAt(conv.getCreatedAt() == null ? Instant.now() : conv.getCreatedAt());
        Conversation created = conversationService.createConversation(conv);
        return ResponseEntity.created(URI.create("/api/conversations/" + created.getId())).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Conversation>> list() {
        return ResponseEntity.ok(conversationService.getAllConversations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Conversation> get(@PathVariable String id) {
        Optional<Conversation> c = conversationService.getById(id);
        return c.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/participant/{userId}")
    public ResponseEntity<List<Conversation>> byParticipant(@PathVariable Integer userId) {
        return ResponseEntity.ok(conversationService.findByParticipant(userId));
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<Conversation> addMessage(@PathVariable String id, @RequestBody Message message) {
        message.setCreatedAt(message.getCreatedAt() == null ? Instant.now() : message.getCreatedAt());
        try {
            Conversation updated = conversationService.addMessage(id, message);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Conversation> update(@PathVariable String id, @RequestBody Conversation details) {
        try {
            Conversation updated = conversationService.updateConversation(id, details);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        conversationService.deleteConversation(id);
        return ResponseEntity.noContent().build();
    }
}
