import apiClient from './api';

export const SearchService = {
    /**
     * Recherche avancée globale avec filtrage par catégorie
     * @param {string} query - Terme de recherche
     * @param {string[]} categories - Catégories à chercher: utilisateurs, skills, experiences
     * @returns {Promise} Résultats groupés par catégorie
     */
    async advancedSearch(query, categories = []) {
        try {
            const params = new URLSearchParams({ query });

            if (categories && categories.length > 0) {
                categories.forEach(cat => params.append('categories', cat));
            }

            const response = await apiClient.get(`/search/advanced?${params.toString()}`);
            return response.data;
        } catch (error) {
            console.error('❌ Erreur recherche:', error);
            throw error;
        }
    },

    /**
     * Recherche dans une catégorie spécifique
     * @param {string} query - Terme de recherche
     * @param {string} category - Catégorie: utilisateurs, skills ou experiences
     * @returns {Promise} Résultats de la catégorie
     */
    async searchByCategory(query, category) {
        try {
            const response = await apiClient.get(`/search/advanced?query=${query}&categories=${category}`);
            return response.data;
        } catch (error) {
            console.error(`❌ Erreur recherche ${category}:`, error);
            throw error;
        }
    }
};

export default SearchService;
