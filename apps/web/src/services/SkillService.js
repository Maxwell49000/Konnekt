import apiClient from './api';

const SkillService = {
    async getAllSkills() {
        const res = await apiClient.get('/skills');
        return res.data;
    },

    async getSkill(id) {
        const res = await apiClient.get(`/skills/${id}`);
        return res.data;
    },

    async getByLibelle(libelle) {
        try {
            const res = await apiClient.get(`/skills/by-libelle?libelle=${encodeURIComponent(libelle)}`);
            return res.data;
        } catch (err) {
            // return null on 404
            if (err.response && err.response.status === 404) return null;
            throw err;
        }
    },

    async createSkill(libelle) {
        const res = await apiClient.post('/skills', { libelle });
        return res.data;
    },

    async updateSkill(id, data) {
        const res = await apiClient.put(`/skills/${id}`, data);
        return res.data;
    },

    async deleteSkill(id) {
        await apiClient.delete(`/skills/${id}`);
    },

    async search(query) {
        const res = await apiClient.get('/skills/search', { params: { query } });
        return res.data;
    },

    async mostUsed() {
        const res = await apiClient.get('/skills/mostUsed');
        return res.data;
    },

    async checkLibelle(libelle) {
        const res = await apiClient.get('/skills/check-libelle', { params: { libelle } });
        return res.data;
    }
};

export default SkillService;
