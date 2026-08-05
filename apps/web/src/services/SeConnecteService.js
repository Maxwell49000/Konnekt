import apiClient from './api';

const SeConnecteService = {
    async createConnection(demandeurId, destinataireId) {
        const response = await apiClient.post(`/connexions/request/${demandeurId}/${destinataireId}`);
        return response.data;
    },

    async getAcceptedConnections(utilisateurId) {
        const response = await apiClient.get(`/connexions/accepted/${utilisateurId}`);
        return response.data;
    },

    async getAllConnections(utilisateurId) {
        const response = await apiClient.get(`/connexions/all/${utilisateurId}`);
        return response.data;
    },

    async getPendingConnections(utilisateurId) {
        const response = await apiClient.get(`/connexions/demandes-recues/${utilisateurId}/attente`);
        return response.data;
    },

    async acceptConnection(connectionId) {
        const response = await apiClient.put(`/connexions/${connectionId}/accept`);
        return response.data;
    },

    async refuseConnection(connectionId) {
        const response = await apiClient.put(`/connexions/${connectionId}/refuse`);
        return response.data;
    },

    async blockConnection(connectionId) {
        const response = await apiClient.put(`/connexions/${connectionId}/block`);
        return response.data;
    },

    async deleteConnection(connectionId) {
        await apiClient.delete(`/connexions/${connectionId}`);
    },

    async checkConnectionStatus(utilisateur1, utilisateur2) {
        try {
            const response = await apiClient.get(`/connexions/isConnected/${utilisateur1}/${utilisateur2}`);
            return response.data;
        } catch {
            return false;
        }
    },
};

export default SeConnecteService;
