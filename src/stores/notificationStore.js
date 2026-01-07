import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import NotificationService from '../services/NotificationService';

export const useNotificationStore = defineStore('notification', () => {
    const notifications = ref([]);
    const isLoading = ref(false);
    const error = ref(null);

    // Notifications non lues
    const unreadNotifications = computed(() =>
        notifications.value.filter(n => {
            // Support both 'read' (camelCase) and 'lue' (French) property names
            const isRead = n.read !== undefined ? n.read : n.lue;
            return !isRead;
        })
    );

    // Nombre de notifications non lues
    const unreadCount = computed(() => unreadNotifications.value.length);

    const fetchNotificationsByUser = async (userId) => {
        isLoading.value = true;
        error.value = null;
        try {
            notifications.value = await NotificationService.getNotificationsByUser(userId);
            console.log('[notificationStore] Loaded notifications:', notifications.value);
        } catch (err) {
            error.value = err.message || 'Erreur lors du chargement des notifications';
            console.error('[notificationStore] Error:', err);
        } finally {
            isLoading.value = false;
        }
    };

    const markAsRead = async (notificationId) => {
        try {
            await NotificationService.markAsRead(notificationId);
            const notif = notifications.value.find(n => n.id === notificationId);
            if (notif) {
                // Support both property names
                notif.read = true;
                notif.lue = true;
            }
        } catch (err) {
            error.value = err.message || 'Erreur lors de la mise à jour';
            console.error('[notificationStore] Error marking as read:', err);
        }
    };

    const markAllAsRead = async (userId) => {
        try {
            await NotificationService.markAllAsRead(userId);
            notifications.value.forEach(n => {
                n.read = true;
            });
        } catch (err) {
            error.value = err.message || 'Erreur lors de la mise à jour';
            console.error('[notificationStore] Error marking all as read:', err);
        }
    };

    const deleteNotification = async (notificationId) => {
        try {
            await NotificationService.deleteNotification(notificationId);
            notifications.value = notifications.value.filter(n => n.id !== notificationId);
        } catch (err) {
            error.value = err.message || 'Erreur lors de la suppression';
            console.error('[notificationStore] Error deleting:', err);
        }
    };

    const addNotification = (notification) => {
        // Ajouter une notification localement (pour les mises à jour en temps réel)
        notifications.value.unshift(notification);
    };

    return {
        notifications,
        isLoading,
        error,
        unreadNotifications,
        unreadCount,
        fetchNotificationsByUser,
        markAsRead,
        markAllAsRead,
        deleteNotification,
        addNotification,
    };
});
