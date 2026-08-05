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

import com.example.reseau_social.dtos.ConversationDTO;
import com.example.reseau_social.dtos.MessageDTO;
import com.example.reseau_social.models.Conversation;
import com.example.reseau_social.models.Message;
import com.example.reseau_social.services.ConversationService;

import jakarta.validation.Valid;

// Controller class for managing conversations
@RestController
@RequestMapping("/api/conversations")
@CrossOrigin(origins = "*")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @PostMapping
    public ResponseEntity<ConversationDTO> create(@Valid @RequestBody ConversationDTO dto) {
        Conversation conv = new Conversation();
        conv.setParticipants(java.util.Arrays.asList(dto.getUtilisateur1Id(), dto.getUtilisateur2Id()));
        conv.setCreatedAt(java.time.Instant.now());
        conv.setMessages(new java.util.ArrayList<>());
        
        Conversation created = conversationService.createConversation(conv);
        ConversationDTO response = toDTO(created);
        return ResponseEntity.created(java.net.URI.create("/api/conversations/" + created.getId())).body(response);
    }

    @GetMapping
    public ResponseEntity<java.util.List<ConversationDTO>> list() {
        java.util.List<Conversation> conversations = conversationService.getAllConversations();
        java.util.List<ConversationDTO> responses = conversations.stream().map(this::toDTO).toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConversationDTO> get(@PathVariable String id) {
        Optional<Conversation> c = conversationService.getById(id);
        return c.map(conv -> ResponseEntity.ok(toDTO(conv)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/participant/{userId}")
    public ResponseEntity<java.util.List<ConversationDTO>> byParticipant(@PathVariable Integer userId) {
        java.util.List<Conversation> conversations = conversationService.findByParticipant(userId);
        java.util.List<ConversationDTO> responses = conversations.stream().map(this::toDTO).toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<ConversationDTO> addMessage(@PathVariable String id, @Valid @RequestBody MessageDTO dto) {
        try {
            Message message = new Message();
            message.setText(dto.getContenu());
            message.setSenderId(dto.getAuteurId());
            message.setCreatedAt(java.time.Instant.now());
            
            Conversation updated = conversationService.addMessage(id, message);
            ConversationDTO response = toDTO(updated);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConversationDTO> update(@PathVariable String id, @RequestBody Conversation details) {
        try {
            Conversation updated = conversationService.updateConversation(id, details);
            ConversationDTO response = toDTO(updated);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        conversationService.deleteConversation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<java.util.List<MessageDTO>> getMessages(@PathVariable String conversationId) {
        Optional<Conversation> c = conversationService.getById(conversationId);
        if (c.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        java.util.List<MessageDTO> messages = c.get().getMessages().stream()
                .map(m -> new MessageDTO(m.getId(), m.getText(), m.getSenderId(), conversationId, m.getCreatedAt()))
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(messages);
    }

    // Helper mapper
    private ConversationDTO toDTO(Conversation c) {
        if (c == null) return null;
        java.util.List<MessageDTO> messageDTOs = c.getMessages().stream()
                .map(m -> new MessageDTO(m.getId(), m.getText(), m.getSenderId(), c.getId(), m.getCreatedAt()))
                .collect(java.util.stream.Collectors.toList());
        
        ConversationDTO dto = new ConversationDTO();
        dto.setId(c.getId());
        dto.setUtilisateur1Id(c.getParticipants().get(0));
        dto.setUtilisateur2Id(c.getParticipants().get(1));
        dto.setDateCreation(c.getCreatedAt());
        dto.setMessages(messageDTOs);
        return dto;
    }
}