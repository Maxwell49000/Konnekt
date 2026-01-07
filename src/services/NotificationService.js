import apiClient from './api';

// Helper function to normalize notification data from backend
const normalizeNotification = (notif) => {
    return {
        id: notif.id,
        utilisateurId: notif.utilisateurId,
        userId: notif.userId || notif.utilisateurId,
        type: notif.type,
        content: notif.content || notif.message,
        message: notif.message || notif.content,
        read: notif.read !== undefined ? notif.read : notif.lue,
        lue: notif.lue !== undefined ? notif.lue : notif.read,
        createdAt: notif.createdAt || notif.dateCreation,
        dateCreation: notif.dateCreation || notif.createdAt,
        relatedPostId: notif.relatedPostId,
    };
};

const NotificationService = {
    async getNotification(id) {
        const response = await apiClient.get(`/notifications/${id}`);
        return normalizeNotification(response.data);
    },

    async getAllNotifications() {
        const response = await apiClient.get('/notifications');
        const data = Array.isArray(response.data) ? response.data : (response.data.value || []);
        return data.map(normalizeNotification);
    },

    async getNotificationsByUser(userId) {
        const response = await apiClient.get(`/notifications/user/${userId}`);
        // Backend retourne une liste directement ou un objet avec 'value'
        const data = Array.isArray(response.data) ? response.data : (response.data.value || []);
        return data.map(normalizeNotification);
    },

    async createNotification(data) {
        const response = await apiClient.post('/notifications', data);
        return normalizeNotification(response.data);
    },

    async updateNotification(id, data) {
        const response = await apiClient.put(`/notifications/${id}`, data);
        return normalizeNotification(response.data);
    },

    async markAsRead(id) {
        const response = await apiClient.put(`/notifications/${id}`, {
            read: true,
        });
        return normalizeNotification(response.data);
    },

    async deleteNotification(id) {
        await apiClient.delete(`/notifications/${id}`);
    },

    // Backward compatibility - anciennes méthodes
    async getNotifications(utilisateurId) {
        return this.getNotificationsByUser(utilisateurId);
    },

    async markAllAsRead(utilisateurId) {
        const notifs = await this.getNotificationsByUser(utilisateurId);
        for (const notif of notifs) {
            await this.markAsRead(notif.id);
        }
    },
};

export default NotificationService;
