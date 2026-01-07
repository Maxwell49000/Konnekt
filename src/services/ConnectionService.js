import apiClient from './api';

const ConnectionService = {
    async sendConnectionRequest(demandeurId, destinataireId) {
        const response = await apiClient.post(`/connexions/request/${demandeurId}/${destinataireId}`);
        return response.data;
    },

    // Helper to find the connexion entity between two users
    async getBetween(utilisateur1, utilisateur2) {
        const response = await apiClient.get(`/connexions/between/${utilisateur1}/${utilisateur2}`);
        return response.data;
    },

    async acceptConnectionRequest(demandeurId, destinataireId) {
        const between = await this.getBetween(demandeurId, destinataireId);
        const id = between?.id || between?.seConnecteId || between?.idSeConnecte;
        if (!id) throw new Error('Connexion introuvable');
        const response = await apiClient.put(`/connexions/${id}/accept`);
        return response.data;
    },

    async refuseConnectionRequest(demandeurId, destinataireId) {
        const between = await this.getBetween(demandeurId, destinataireId);
        const id = between?.id || between?.seConnecteId || between?.idSeConnecte;
        if (!id) throw new Error('Connexion introuvable');
        const response = await apiClient.put(`/connexions/${id}/refuse`);
        return response.data;
    },

    async blockUser(demandeurId, destinataireId) {
        const between = await this.getBetween(demandeurId, destinataireId);
        const id = between?.id || between?.seConnecteId || between?.idSeConnecte;
        if (!id) throw new Error('Connexion introuvable');
        const response = await apiClient.put(`/connexions/${id}/block`);
        return response.data;
    },

    // Pending received requests for utilisateur
    async getPendingConnections(utilisateurId) {
        const response = await apiClient.get(`/connexions/demandes-recues/${utilisateurId}/attente`);
        return response.data;
    },

    // Accepted connections for utilisateur
    async getConnections(utilisateurId) {
        const response = await apiClient.get(`/connexions/accepted/${utilisateurId}`);
        return response.data;
    },

    // Other helpers
    async getAllConnections() {
        const response = await apiClient.get('/connexions');
        return response.data;
    },
    async deleteConnectionById(id) {
        const response = await apiClient.delete(`/connexions/${id}`);
        return response.data;
    },

    async deleteConnectionBetween(utilisateur1, utilisateur2) {
        const between = await this.getBetween(utilisateur1, utilisateur2);
        const id = between?.id || between?.seConnecteId || between?.idSeConnecte;
        if (!id) throw new Error('Connexion introuvable');
        return this.deleteConnectionById(id);
    },
};

export default ConnectionService;
