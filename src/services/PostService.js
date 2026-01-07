import apiClient from './api';

const PostService = {
    async createPost(data) {
        const response = await apiClient.post('/posts', data);
        return response.data;
    },

    async getPost(id) {
        const response = await apiClient.get(`/posts/${id}`);
        return response.data;
    },

    async getAllPosts() {
        const response = await apiClient.get('/posts');
        return response.data;
    },

    async updatePost(id, data) {
        const response = await apiClient.put(`/posts/${id}`, data);
        return response.data;
    },

    async deletePost(id) {
        await apiClient.delete(`/posts/${id}`);
    },

    async likePost(postId, userId) {
        const response = await apiClient.post(`/posts/${postId}/like`, null, {
            params: { userId }
        });
        return response.data;
    },

    async unlikePost(postId, userId) {
        const response = await apiClient.post(`/posts/${postId}/unlike`, null, {
            params: { userId }
        });
        return response.data;
    },

    async addComment(postId, data) {
        const response = await apiClient.post(`/posts/${postId}/comments`, data);
        return response.data;
    },

    async updateComment(postId, commentId, data) {
        const response = await apiClient.put(`/posts/${postId}/comments/${commentId}`, data);
        return response.data;
    },

    async deleteComment(postId, commentId) {
        const response = await apiClient.delete(`/posts/${postId}/comments/${commentId}`);
        return response.data;
    },

    async getComments(postId) {
        const response = await apiClient.get(`/posts/${postId}/comments`);
        return response.data;
    },

    async getPostsByAuthor(authorId) {
        const response = await apiClient.get(`/posts/author/${authorId}`);
        return response.data;
    },
};

export default PostService;
