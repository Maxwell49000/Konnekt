package com.example.reseau_social.services;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.reseau_social.dtos.ConversationDTO;
import com.example.reseau_social.dtos.MessageDTO;
import com.example.reseau_social.models.Conversation;
import com.example.reseau_social.models.Message;
import com.example.reseau_social.models.Notification;
import com.example.reseau_social.models.Utilisateur;
import com.example.reseau_social.repositories.ConversationRepository;
import com.example.reseau_social.repositories.UtilisateurRepository;

import jakarta.transaction.Transactional;

// Service class for managing conversations
@Service
@Transactional
public class ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private NotificationService notificationService;

    public Conversation createConversation(Conversation conv) {
        return conversationRepository.save(conv);
    }

    public List<Conversation> getAllConversations() {
        return conversationRepository.findAll();
    }

    public Optional<Conversation> getById(String id) {
        return conversationRepository.findById(id);
    }

    public List<Conversation> findByParticipant(Integer participantId) {
        return conversationRepository.findByParticipantsContaining(participantId);
    }

    public Conversation addMessage(String conversationId, Message message) {
        return conversationRepository.findById(conversationId).map(c -> {
            c.getMessages().add(message);
            Conversation saved = conversationRepository.save(c);
            
            // Create notification for the other participant
            Integer senderId = message.getSenderId();
            Integer recipientId = c.getParticipants().stream()
                    .filter(p -> !p.equals(senderId))
                    .findFirst()
                    .orElse(null);
            
            if (recipientId != null) {
                Optional<Utilisateur> senderOpt = utilisateurRepository.findById(senderId);
                if (senderOpt.isPresent()) {
                    Utilisateur sender = senderOpt.get();
                    Notification notification = new Notification();
                    notification.setUserId(recipientId);
                    notification.setType("message");
                    notification.setContent(sender.getPrenom() + " " + sender.getNom() + " vous a envoyé un message");
                    notification.setRead(false);
                    notification.setCreatedAt(Instant.now());
                    notificationService.createNotification(notification);
                }
            }
            
            return saved;
        }).orElseThrow(() -> new IllegalArgumentException("Conversation not found: " + conversationId));
    }

    public Conversation updateConversation(String id, Conversation details) {
        return conversationRepository.findById(id).map(c -> {
            c.setParticipants(details.getParticipants());
            return conversationRepository.save(c);
        }).orElseThrow(() -> new IllegalArgumentException("Conversation not found: " + id));
    }

    public void deleteConversation(String id) {
        conversationRepository.deleteById(id);
    }

    // Mappers
    public Conversation createConversationFromDTO(ConversationDTO dto) {
        Conversation conversation = new Conversation();
        conversation.setParticipants(Arrays.asList(dto.getUtilisateur1Id(), dto.getUtilisateur2Id()));
        conversation.setCreatedAt(Instant.now());
        conversation.setMessages(new java.util.ArrayList<>());
        return conversation;
    }

    public ConversationDTO conversationToResponseDTO(Conversation conversation) {
        List<MessageDTO> messageDTOs = conversation.getMessages().stream()
                .map(m -> new MessageDTO(m.getId(), m.getText(), m.getSenderId(), conversation.getId(), m.getCreatedAt()))
                .collect(Collectors.toList());
        
        ConversationDTO dto = new ConversationDTO();
        dto.setId(conversation.getId());
        dto.setUtilisateur1Id(conversation.getParticipants().get(0));
        dto.setUtilisateur2Id(conversation.getParticipants().get(1));
        dto.setDateCreation(conversation.getCreatedAt());
        dto.setMessages(messageDTOs);
        return dto;
    }

    public List<ConversationDTO> conversationsToResponseDTOList(List<Conversation> conversations) {
        return conversations.stream().map(this::conversationToResponseDTO).collect(Collectors.toList());
    }
}
