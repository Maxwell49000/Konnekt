import apiClient from './api';

const ConversationService = {
    async createConversation(data) {
        // Le backend attend utilisateur1Id et utilisateur2Id, pas participants
        const payload = {
            utilisateur1Id: data.participants?.[0] || data.utilisateur1Id,
            utilisateur2Id: data.participants?.[1] || data.utilisateur2Id,
        };
        const response = await apiClient.post('/conversations', payload);
        return response.data;
    },

    async getConversation(id) {
        const response = await apiClient.get(`/conversations/${id}`);
        return response.data;
    },

    async getConversations() {
        const response = await apiClient.get('/conversations');
        return response.data;
    },

    async sendMessage(conversationId, data) {
        const response = await apiClient.post(
            `/conversations/${conversationId}/messages`,
            data
        );
        return response.data;
    },

    async getMessages(conversationId) {
        const response = await apiClient.get(`/conversations/${conversationId}/messages`);
        return response.data;
    },

    async deleteConversation(id) {
        await apiClient.delete(`/conversations/${id}`);
    },

    async getConversationsByParticipant(userId) {
        const response = await apiClient.get(`/conversations/participant/${userId}`);
        return response.data;
    },
};

export default ConversationService;
