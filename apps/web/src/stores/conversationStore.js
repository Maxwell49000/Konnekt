import { defineStore } from 'pinia';
import { ref } from 'vue';
import ConversationService from '../services/ConversationService';
import WebSocketService from '../services/WebSocketService';

export const useConversationStore = defineStore('conversation', () => {
    const conversations = ref([]);
    const currentConversation = ref(null);
    const isLoading = ref(false);
    const error = ref(null);
    const wsConnected = ref(false);

    const fetchConversations = async () => {
        isLoading.value = true;
        error.value = null;
        try {
            conversations.value = await ConversationService.getConversations();
        } catch (err) {
            error.value = err.message || 'Erreur lors du chargement des conversations';
        } finally {
            isLoading.value = false;
        }
    };

    const selectConversation = async (conversationId) => {
        try {
            currentConversation.value = await ConversationService.getConversation(conversationId);
        } catch (err) {
            error.value = err.message || 'Erreur lors du chargement de la conversation';
            throw err;
        }
    };

    const createConversation = async (participants, titre) => {
        try {
            const newConversation = await ConversationService.createConversation({
                participants,
                titre,
            });
            conversations.value.push(newConversation);
            currentConversation.value = newConversation;
            return newConversation;
        } catch (err) {
            error.value = err.message || 'Erreur lors de la création de la conversation';
            throw err;
        }
    };

    const sendMessage = async (contenu, auteurId, conversationId = null) => {
        const convId = conversationId || currentConversation.value?.id;
        if (!convId) {
            throw new Error('Aucune conversation sélectionnée');
        }

        try {
            // Essayer d'envoyer via WebSocket d'abord
            if (WebSocketService.isConnected()) {
                WebSocketService.sendMessage(convId, auteurId, contenu);
                // Ne pas attendre la réponse, elle viendra via WebSocket
            } else {
                // Fallback: HTTP classique
                const message = await ConversationService.sendMessage(convId, {
                    contenu,
                    auteurId,
                });
                if (currentConversation.value?.id === convId) {
                    currentConversation.value.messages.push(message);
                }
            }
        } catch (err) {
            error.value = err.message || 'Erreur lors de l\'envoi du message';
            throw err;
        }
    };

    const initWebSocket = async () => {
        try {
            await WebSocketService.connect();
            wsConnected.value = true;

            // Écouter les nouveaux messages
            WebSocketService.onMessage((message) => {
                if (message.action === 'NEW_MESSAGE' && currentConversation.value) {
                    if (message.conversationId === currentConversation.value.id) {
                        // Ajouter le message à la conversation actuelle
                        if (!currentConversation.value.messages) {
                            currentConversation.value.messages = [];
                        }
                        currentConversation.value.messages.push({
                            id: Math.random().toString(),
                            senderId: message.userId,
                            text: message.content,
                            createdAt: new Date(message.timestamp).toISOString()
                        });
                    }
                }
            });

            console.log('✅ WebSocket initialisé');
        } catch (err) {
            console.error('Erreur initialisation WebSocket:', err);
            wsConnected.value = false;
        }
    };

    const joinConversation = (conversationId, userId) => {
        if (WebSocketService.isConnected()) {
            WebSocketService.joinConversation(conversationId, userId);
            console.log('✅ Rejoind la conversation:', conversationId);
        }
    };

    const leaveConversation = (conversationId, userId) => {
        if (WebSocketService.isConnected()) {
            WebSocketService.leaveConversation(conversationId, userId);
            console.log('✅ Quitte la conversation:', conversationId);
        }
    };

    const disconnectWebSocket = () => {
        WebSocketService.disconnect();
        wsConnected.value = false;
    };

    return {
        conversations,
        currentConversation,
        isLoading,
        error,
        wsConnected,
        fetchConversations,
        selectConversation,
        createConversation,
        sendMessage,
        initWebSocket,
        joinConversation,
        leaveConversation,
        disconnectWebSocket,
    };
});
