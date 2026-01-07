import { defineStore } from 'pinia';
import { ref } from 'vue';
import ConversationService from '../services/ConversationService';

export const useConversationStore = defineStore('conversation', () => {
    const conversations = ref([]);
    const currentConversation = ref(null);
    const isLoading = ref(false);
    const error = ref(null);

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

    const sendMessage = async (contenu, auteurId) => {
        if (!currentConversation.value) {
            throw new Error('Aucune conversation sélectionnée');
        }

        try {
            const message = await ConversationService.sendMessage(currentConversation.value.id, {
                contenu,
                auteurId,
            });
            currentConversation.value.messages.push(message);
            return message;
        } catch (err) {
            error.value = err.message || 'Erreur lors de l\'envoi du message';
            throw err;
        }
    };

    return {
        conversations,
        currentConversation,
        isLoading,
        error,
        fetchConversations,
        selectConversation,
        createConversation,
        sendMessage,
    };
});
