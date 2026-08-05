import apiClient from './api';

const FeedService = {
    async getAllFeeds() {
        const response = await apiClient.get('/feeds');
        return response.data;
    },

    async getFeedByUser(userId) {
        const response = await apiClient.get(`/feeds/user/${userId}`);
        return response.data;
    },

    async getPostsByUser(userId) {
        const response = await apiClient.get(`/feeds/user/${userId}/posts`);
        return response.data;
    },

    async createFeed(data) {
        const response = await apiClient.post('/feeds', data);
        return response.data;
    },

    async updateFeed(id, data) {
        const response = await apiClient.put(`/feeds/${id}`, data);
        return response.data;
    },

    async deleteFeed(id) {
        await apiClient.delete(`/feeds/${id}`);
    },
};

export default FeedService;
