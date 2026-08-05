import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import SearchService from '../services/SearchService';

export const useSearchStore = defineStore('search', () => {
    const searchQuery = ref('');
    const searchResults = ref({
        utilisateurs: [],
        skills: [],
        experiences: [],
        totalResults: 0
    });
    const selectedCategories = ref(['utilisateurs', 'skills', 'experiences']);
    const isSearching = ref(false);
    const hasSearched = ref(false);
    const searchError = ref(null);

    // Computed properties
    const resultsByCategory = computed(() => ({
        utilisateurs: searchResults.value.utilisateurs || [],
        skills: searchResults.value.skills || [],
        experiences: searchResults.value.experiences || []
    }));

    const categoryCount = computed(() => ({
        utilisateurs: resultsByCategory.value.utilisateurs.length,
        skills: resultsByCategory.value.skills.length,
        experiences: resultsByCategory.value.experiences.length
    }));

    // Actions
    const performSearch = async (query, categories = null) => {
        if (!query || query.trim().length === 0) {
            searchResults.value = {
                utilisateurs: [],
                skills: [],
                experiences: [],
                totalResults: 0
            };
            hasSearched.value = false;
            return;
        }

        isSearching.value = true;
        searchError.value = null;

        try {
            const cats = categories || selectedCategories.value;
            searchResults.value = await SearchService.advancedSearch(query, cats);
            hasSearched.value = true;
            console.log('✅ Recherche réussie:', searchResults.value);
        } catch (error) {
            searchError.value = error.message;
            console.error('❌ Erreur recherche:', error);
        } finally {
            isSearching.value = false;
        }
    };

    const updateSearchQuery = (query) => {
        searchQuery.value = query;
    };

    const toggleCategory = (category) => {
        const index = selectedCategories.value.indexOf(category);
        if (index > -1) {
            selectedCategories.value.splice(index, 1);
        } else {
            selectedCategories.value.push(category);
        }
    };

    const setCategories = (categories) => {
        selectedCategories.value = categories;
    };

    const clearSearch = () => {
        searchQuery.value = '';
        searchResults.value = {
            utilisateurs: [],
            skills: [],
            experiences: [],
            totalResults: 0
        };
        hasSearched.value = false;
        searchError.value = null;
    };

    return {
        // State
        searchQuery,
        searchResults,
        selectedCategories,
        isSearching,
        hasSearched,
        searchError,

        // Computed
        resultsByCategory,
        categoryCount,

        // Actions
        performSearch,
        updateSearchQuery,
        toggleCategory,
        setCategories,
        clearSearch
    };
});
