<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated class="bg-primary text-white">
      <q-toolbar>
        <q-toolbar-title class="text-h6">
          <router-link to="/dashboard" class="text-white no-underline">LinkDong</router-link>
        </q-toolbar-title>

        <div class="row items-center q-gutter-md">
          <q-input
            v-model="searchQuery"
            dense
            outlined
            dark
            placeholder="Rechercher..."
            style="width: 200px"
            class="q-px-md"
          >
            <template #prepend>
              <q-icon name="search" />
            </template>
          </q-input>

          <q-btn flat dense round icon="notifications" @click="goTo('/notifications')">
            <q-badge v-if="unreadCount > 0" color="negative" floating>{{ unreadCount }}</q-badge>
          </q-btn>

          <q-btn-dropdown flat dense round icon="account_circle">
            <q-list style="min-width: 200px">
              <q-item clickable @click="goToProfile">
                <q-item-section>Mon profil</q-item-section>
              </q-item>
              <q-separator />
              <q-item clickable @click="logout">
                <q-item-section>Déconnexion</q-item-section>
              </q-item>
            </q-list>
          </q-btn-dropdown>
        </div>
      </q-toolbar>
    </q-header>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/authStore';
import { useNotificationStore } from '../stores/notificationStore';

const router = useRouter();
const authStore = useAuthStore();
const notificationStore = useNotificationStore();

const searchQuery = ref('');

// Use computed property to always get the latest unread count
const unreadCount = computed(() => notificationStore.unreadCount);

const goTo = (path) => {
  router.push(path);
};

const goToProfile = () => {
  const userId = authStore.user?.idUtilisateur || authStore.user?.id;
  router.push(`/profile/${userId}`);
};

const logout = () => {
  authStore.logout();
  router.push('/login');
};

const loadNotifications = async () => {
  const userId = authStore.user?.idUtilisateur || authStore.user?.id;
  if (userId) {
    await notificationStore.fetchNotificationsByUser(userId);
  }
};

onMounted(() => {
  loadNotifications();
  
  // Recharger les notifications toutes les 30 secondes
  const interval = setInterval(() => {
    loadNotifications();
  }, 30000);
  
  // Cleanup
  return () => clearInterval(interval);
});
</script>

<style scoped>
a {
  text-decoration: none;
  color: inherit;
}

a:hover {
  opacity: 0.8;
}
</style>

