import apiClient from './api';

const UtilisateurService = {
    async createUtilisateur(data) {
        const response = await apiClient.post('/utilisateurs', data);
        return response.data;
    },

    async getUtilisateur(id) {
        const response = await apiClient.get(`/utilisateurs/${id}`);
        return response.data;
    },

    async getAllUtilisateurs() {
        const response = await apiClient.get('/utilisateurs');
        return response.data;
    },

    async updateUtilisateur(id, data) {
        const response = await apiClient.put(`/utilisateurs/${id}`, data);
        return response.data;
    },

    async deleteUtilisateur(id) {
        await apiClient.delete(`/utilisateurs/${id}`);
    },

    async searchUtilisateurs(query) {
        const response = await apiClient.get('/utilisateurs/search', {
            params: { q: query },
        });
        return response.data;
    },

    async updateSkills(id, skillIds) {
        const response = await apiClient.put(`/utilisateurs/${id}/skills`, skillIds);
        return response.data;
    },
};

export default UtilisateurService;;
