import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import PostService from '../services/PostService';

export const usePostStore = defineStore('post', () => {
    const posts = ref([]);
    const isLoading = ref(false);
    const error = ref(null);

    const postCount = computed(() => posts.value.length);

    const fetchPosts = async () => {
        isLoading.value = true;
        error.value = null;
        try {
            posts.value = await PostService.getAllPosts();
        } catch (err) {
            error.value = err.message || 'Erreur lors du chargement des posts';
        } finally {
            isLoading.value = false;
        }
    };

    const addPost = async (contenu, auteurId) => {
        try {
            const newPost = await PostService.createPost({ contenu, auteurId });
            posts.value.unshift(newPost);
            return newPost;
        } catch (err) {
            error.value = err.message || 'Erreur lors de la création du post';
            throw err;
        }
    };

    const deletePost = async (postId) => {
        try {
            await PostService.deletePost(postId);
            posts.value = posts.value.filter((p) => p.id !== postId);
        } catch (err) {
            error.value = err.message || 'Erreur lors de la suppression du post';
            throw err;
        }
    };

    const likePost = async (postId, utilisateurId) => {
        try {
            await PostService.likePost(postId, utilisateurId);
            const post = posts.value.find((p) => p.id === postId);
            if (post) {
                post.likes = (post.likes || 0) + 1;
            }
        } catch (err) {
            error.value = err.message || 'Erreur lors du like du post';
            throw err;
        }
    };

    const unlikePost = async (postId, utilisateurId) => {
        try {
            await PostService.unlikePost(postId, utilisateurId);
            const post = posts.value.find((p) => p.id === postId);
            if (post && post.likes > 0) {
                post.likes -= 1;
            }
        } catch (err) {
            error.value = err.message || 'Erreur lors du unlike du post';
            throw err;
        }
    };

    return {
        posts,
        isLoading,
        error,
        postCount,
        fetchPosts,
        addPost,
        deletePost,
        likePost,
        unlikePost,
    };
});
