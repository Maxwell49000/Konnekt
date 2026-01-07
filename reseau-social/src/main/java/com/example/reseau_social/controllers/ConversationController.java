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

import com.example.reseau_social.dtos.ConversationResponseDTO;
import com.example.reseau_social.dtos.CreateConversationDTO;
import com.example.reseau_social.dtos.CreateMessageDTO;
import com.example.reseau_social.models.Conversation;
import com.example.reseau_social.models.Message;
import com.example.reseau_social.services.ConversationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/conversations")
@CrossOrigin(origins = "*")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @PostMapping
    public ResponseEntity<ConversationResponseDTO> create(@Valid @RequestBody CreateConversationDTO dto) {
        Conversation conv = conversationService.createConversationFromDTO(dto);
        Conversation created = conversationService.createConversation(conv);
        ConversationResponseDTO response = conversationService.conversationToResponseDTO(created);
        return ResponseEntity.created(URI.create("/api/conversations/" + created.getId())).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ConversationResponseDTO>> list() {
        List<Conversation> conversations = conversationService.getAllConversations();
        List<ConversationResponseDTO> responses = conversationService.conversationsToResponseDTOList(conversations);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConversationResponseDTO> get(@PathVariable String id) {
        Optional<Conversation> c = conversationService.getById(id);
        return c.map(conv -> ResponseEntity.ok(conversationService.conversationToResponseDTO(conv)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/participant/{userId}")
    public ResponseEntity<List<ConversationResponseDTO>> byParticipant(@PathVariable Integer userId) {
        List<Conversation> conversations = conversationService.findByParticipant(userId);
        List<ConversationResponseDTO> responses = conversationService.conversationsToResponseDTOList(conversations);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<ConversationResponseDTO> addMessage(@PathVariable String id, @Valid @RequestBody CreateMessageDTO dto) {
        try {
            Message message = new Message();
            message.setText(dto.getContenu());
            message.setSenderId(dto.getAuteurId());
            message.setCreatedAt(Instant.now());
            
            Conversation updated = conversationService.addMessage(id, message);
            ConversationResponseDTO response = conversationService.conversationToResponseDTO(updated);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConversationResponseDTO> update(@PathVariable String id, @RequestBody Conversation details) {
        try {
            Conversation updated = conversationService.updateConversation(id, details);
            ConversationResponseDTO response = conversationService.conversationToResponseDTO(updated);
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
}
