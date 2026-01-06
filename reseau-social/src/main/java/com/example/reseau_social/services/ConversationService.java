package com.example.reseau_social.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.reseau_social.models.Conversation;
import com.example.reseau_social.models.Message;
import com.example.reseau_social.repositories.ConversationRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

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
            return conversationRepository.save(c);
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
}
