package com.example.reseau_social.websocket;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.example.reseau_social.models.Conversation;
import com.example.reseau_social.models.Message;
import com.example.reseau_social.services.ConversationService;
import com.fasterxml.jackson.databind.ObjectMapper;

// DTO for WebSocket messages
@Component
public class ConversationWebSocketHandler extends TextWebSocketHandler {
    
    private static final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    private ConversationService conversationService;
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = session.getId();
        sessions.put(sessionId, session);
        System.out.println("WebSocket connexion établie: " + sessionId + " (" + sessions.size() + " au total)");
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        
        try {
            // Parser le message JSON
            WebSocketMessageDto wsMessage = objectMapper.readValue(payload, WebSocketMessageDto.class);
            
            // Traiter selon le type d'action
            switch (wsMessage.getAction()) {
                case "JOIN_CONVERSATION":
                    handleJoinConversation(session, wsMessage);
                    break;
                case "SEND_MESSAGE":
                    handleSendMessage(session, wsMessage);
                    break;
                case "LEAVE_CONVERSATION":
                    handleLeaveConversation(session, wsMessage);
                    break;
                default:
                    System.out.println("Action inconnue: " + wsMessage.getAction());
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du traitement du message WebSocket: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = session.getId();
        sessions.remove(sessionId);
        System.out.println("WebSocket connexion fermée: " + sessionId + " (" + sessions.size() + " au total)");
    }
    
    private void handleJoinConversation(WebSocketSession session, WebSocketMessageDto message) {
        String conversationId = message.getConversationId();
        session.getAttributes().put("conversationId", conversationId);
        session.getAttributes().put("userId", message.getUserId());
        System.out.println("Utilisateur " + message.getUserId() + " a rejoint la conversation " + conversationId);
    }
    
    private void handleSendMessage(WebSocketSession session, WebSocketMessageDto message) throws Exception {
        String conversationId = (String) session.getAttributes().get("conversationId");
        Integer userId = (Integer) session.getAttributes().get("userId");
        
        if (conversationId == null || userId == null) {
            session.sendMessage(new TextMessage("{\"error\": \"Not joined to a conversation\"}"));
            return;
        }
        
        // Sauvegarder le message en base de données
        try {
            Message msg = new Message();
            msg.setId(UUID.randomUUID().toString());
            msg.setSenderId(userId);
            msg.setText(message.getContent());
            msg.setCreatedAt(Instant.now());
            
            Optional<Conversation> convOpt = conversationService.getById(conversationId);
            if (convOpt.isPresent()) {
                Conversation conv = convOpt.get();
                conversationService.addMessage(conversationId, msg);
                System.out.println("Message sauvegardé en base de données pour la conversation: " + conversationId);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde du message: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Créer la réponse avec le message reçu
        Map<String, Object> response = new HashMap<>();
        response.put("action", "NEW_MESSAGE");
        response.put("conversationId", conversationId);
        response.put("userId", userId);
        response.put("content", message.getContent());
        response.put("timestamp", System.currentTimeMillis());
        
        String responseJson = objectMapper.writeValueAsString(response);
        
        // Envoyer à tous les clients connectés à cette conversation
        for (WebSocketSession s : sessions.values()) {
            if (s.isOpen()) {
                String convId = (String) s.getAttributes().get("conversationId");
                if (convId != null && convId.equals(conversationId)) {
                    s.sendMessage(new TextMessage(responseJson));
                }
            }
        }
    }
    
    private void handleLeaveConversation(WebSocketSession session, WebSocketMessageDto message) {
        session.getAttributes().remove("conversationId");
        System.out.println("Utilisateur " + message.getUserId() + " a quitté la conversation");
    }
}
