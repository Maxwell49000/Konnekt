package com.example.reseau_social.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.reseau_social.models.Conversation;

@Repository
public interface ConversationRepository extends MongoRepository<Conversation, String> {
    List<Conversation> findByParticipantsContaining(Integer participantId);
}
