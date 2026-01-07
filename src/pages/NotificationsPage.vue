<template>
  <q-page class="q-pa-md">
    <div class="text-h6 q-mb-md">Notifications</div>
    
    <div class="row q-col-gutter-md">
      <!-- Notifications List -->
      <div class="col-12">
        <q-card>
          <q-card-section class="row items-center justify-between border-bottom">
            <div>
              <span class="text-subtitle2">{{ notifications.length }} notification(s)</span>
              <span class="text-caption text-grey q-ml-md">{{ unreadCount }} non lu(e)s</span>
            </div>
            <q-btn 
              v-if="unreadCount > 0" 
              flat 
              label="Tout marquer comme lu" 
              color="primary" 
              size="sm"
              @click="markAllAsRead"
            />
          </q-card-section>

          <q-scroll-area style="height: 600px">
            <q-list v-if="notifications.length > 0" separator>
              <q-item 
                v-for="notif in notifications" 
                :key="notif.id"
                :class="!isNotificationRead(notif) ? 'bg-blue-1' : ''"
                clickable
                @click="handleNotificationClick(notif)"
              >
                <q-item-section avatar>
                  <q-avatar 
                    :color="getNotificationColor(notif.type)" 
                    text-color="white"
                    :icon="getNotificationIcon(notif.type)"
                  />
                </q-item-section>

                <q-item-section>
                  <q-item-label>{{ notif.content || notif.message || notif.contenu }}</q-item-label>
                  <q-item-label caption>{{ formatDate(notif.createdAt || notif.dateCreation) }}</q-item-label>
                </q-item-section>

                <q-item-section side>
                  <div class="text-grey q-gutter-xs">
                    <q-btn 
                      v-if="!isNotificationRead(notif)"
                      flat
                      round
                      dense
                      size="sm"
                      icon="done"
                      color="primary"
                      @click.stop="markAsRead(notif.id)"
                      title="Marquer comme lu"
                    />
                    <q-btn 
                      flat
                      round
                      dense
                      size="sm"
                      icon="close"
                      color="negative"
                      @click.stop="deleteNotification(notif.id)"
                      title="Supprimer"
                    />
                  </div>
                </q-item-section>
              </q-item>
            </q-list>

            <div v-else class="q-pa-lg text-center text-grey">
              <p>Aucune notification</p>
            </div>
          </q-scroll-area>
        </q-card>
      </div>
    </div>
  </q-page>
</template>

<script setup>
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/authStore';
import { useNotificationStore } from '../stores/notificationStore';

const authStore = useAuthStore();
const notificationStore = useNotificationStore();
const router = useRouter();

const notifications = notificationStore.notifications;
const unreadCount = notificationStore.unreadCount;

const formatDate = (dateString) => {
  if (!dateString) return '';
  return new Date(dateString).toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
};

const getNotificationIcon = (type) => {
  const icons = {
    connection_request: 'person_add',
    connection_accepted: 'check_circle',
    like: 'favorite',
    comment: 'comment',
    message: 'mail',
    post: 'article',
  };
  return icons[type] || 'notifications';
};

const getNotificationColor = (type) => {
  const colors = {
    connection_request: 'primary',
    connection_accepted: 'positive',
    like: 'red',
    comment: 'orange',
    message: 'blue',
    post: 'purple',
  };
  return colors[type] || 'grey';
};

const isNotificationRead = (notif) => {
  // Support both 'read' (camelCase) and 'lue' (French) property names
  return notif.read !== undefined ? notif.read : notif.lue;
};

const markAsRead = async (id) => {
  await notificationStore.markAsRead(id);
};

const markAllAsRead = async () => {
  const userId = authStore.user?.idUtilisateur || authStore.user?.id;
  await notificationStore.markAllAsRead(userId);
};

const deleteNotification = async (id) => {
  await notificationStore.deleteNotification(id);
};

const handleNotificationClick = (notif) => {
  // Marquer comme lu
  if (!notif.read && !notif.lue) {
    markAsRead(notif.id);
  }

  // Rediriger en fonction du type de notification
  if (notif.type === 'connection_request') {
    router.push('/connections');
  } else if (notif.type === 'like' || notif.type === 'comment' || notif.type === 'post') {
    router.push('/dashboard');
  } else if (notif.type === 'message') {
    router.push('/messages');
  }
};

onMounted(async () => {
  const userId = authStore.user?.idUtilisateur || authStore.user?.id;
  if (userId) {
    await notificationStore.fetchNotificationsByUser(userId);
  }
});
</script>

<style scoped>
.border-bottom {
  border-bottom: 1px solid #e0e0e0;
}
</style>
