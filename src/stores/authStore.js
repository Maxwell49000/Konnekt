import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import UtilisateurService from '../services/UtilisateurService';

export const useAuthStore = defineStore('auth', () => {
    const user = ref(null);
    const token = ref(localStorage.getItem('authToken'));
    const isLoading = ref(false);
    const error = ref(null);

    const isAuthenticated = computed(() => !!user.value && !!token.value);

    const setUser = (userData) => {
        user.value = userData;
    };

    const setToken = (newToken) => {
        token.value = newToken;
        localStorage.setItem('authToken', newToken);
    };

    const logout = () => {
        user.value = null;
        token.value = null;
        localStorage.removeItem('authToken');
    };

    const fetchUser = async (id) => {
        isLoading.value = true;
        error.value = null;
        try {
            const userData = await UtilisateurService.getUtilisateur(id);
            setUser(userData);
        } catch (err) {
            error.value = err.message || 'Erreur lors du chargement de l\'utilisateur';
        } finally {
            isLoading.value = false;
        }
    };

    // If a token exists from previous session, try to recover the user.
    if (token.value) {
        const m = String(token.value).match(/^token_(\d+)$/);
        if (m) {
            // don't await — fire and forget to populate user
            fetchUser(m[1]).catch(() => { });
        }
    }

    return {
        user,
        token,
        isLoading,
        error,
        isAuthenticated,
        setUser,
        setToken,
        logout,
        fetchUser,
    };
});
