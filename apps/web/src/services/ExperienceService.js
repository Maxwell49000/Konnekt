import apiClient from './api';

const ExperienceService = {
    async createExperience(utilisateurId, data) {
        const response = await apiClient.post(`/experiences/utilisateur/${utilisateurId}`, data);
        return response.data;
    },

    async getExperiencesByUtilisateur(utilisateurId) {
        const response = await apiClient.get(`/experiences/utilisateur/${utilisateurId}`);
        return response.data;
    },

    async updateExperience(id, data) {
        const response = await apiClient.put(`/experiences/${id}`, data);
        return response.data;
    },

    async deleteExperience(id) {
        await apiClient.delete(`/experiences/${id}`);
    },
};

export default ExperienceService;
