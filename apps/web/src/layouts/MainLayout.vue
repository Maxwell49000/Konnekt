<template>
  <q-layout view="hHh Lpr fFf" class="app-shell">
    <template v-if="!isAuthPage">
      <q-header class="app-header">
        <q-toolbar class="app-toolbar">
          <q-btn flat round dense icon="menu" class="lt-md" aria-label="Ouvrir la navigation" @click="drawerOpen = !drawerOpen" />
          <router-link to="/dashboard" class="brand brand--header">
            <span class="brand__mark" aria-hidden="true">K</span><span>Konnekt</span>
          </router-link>
          <q-space />
          <q-input v-model="searchQuery" dense borderless placeholder="Rechercher une personne, une compétence…" class="header-search gt-sm" aria-label="Rechercher" @keyup.enter="goToSearch">
            <template #prepend><q-icon name="search" size="20px" /></template>
            <template v-if="searchQuery" #append><q-icon name="close" class="cursor-pointer" @click="searchQuery = ''" /></template>
          </q-input>
          <q-space />
          <q-btn flat round icon="notifications_none" aria-label="Notifications" @click="goTo('/notifications')">
            <q-badge v-if="unreadCount > 0" color="negative" floating rounded>{{ unreadCount }}</q-badge>
          </q-btn>
          <q-btn-dropdown flat round dropdown-icon="none" aria-label="Menu du profil">
            <template #label><q-avatar size="34px" color="primary" text-color="white" class="text-weight-bold">{{ userInitials }}</q-avatar></template>
            <q-list class="account-menu">
              <q-item-label header>{{ fullName }}</q-item-label>
              <q-item clickable v-close-popup @click="goToProfile"><q-item-section avatar><q-icon name="person_outline" /></q-item-section><q-item-section>Mon profil</q-item-section></q-item>
              <q-separator />
              <q-item clickable v-close-popup @click="logout"><q-item-section avatar><q-icon name="logout" /></q-item-section><q-item-section>Quitter la démo</q-item-section></q-item>
            </q-list>
          </q-btn-dropdown>
        </q-toolbar>
      </q-header>

      <q-drawer v-model="drawerOpen" show-if-above :width="248" :breakpoint="1024" class="app-drawer">
        <div class="drawer-content">
          <nav aria-label="Navigation principale">
            <q-list padding>
              <q-item-label header class="nav-eyebrow">Espace</q-item-label>
              <q-item v-for="item in navItems" :key="item.to" clickable :to="item.to" exact-active-class="nav-item--active" class="nav-item">
                <q-item-section avatar><q-icon :name="item.icon" /></q-item-section>
                <q-item-section>{{ item.label }}</q-item-section>
                <q-item-section v-if="item.badge" side><q-badge color="negative" rounded>{{ item.badge }}</q-badge></q-item-section>
              </q-item>
            </q-list>
          </nav>
          <div class="drawer-profile">
            <q-avatar size="40px" color="primary" text-color="white">{{ userInitials }}</q-avatar>
            <div class="drawer-profile__copy"><strong>{{ fullName }}</strong><span>{{ authStore.user?.titreProfessionnel || 'Membre Konnekt' }}</span></div>
          </div>
        </div>
      </q-drawer>

      <q-footer class="mobile-nav lt-md">
        <q-tabs no-caps active-color="primary" indicator-color="transparent">
          <q-route-tab v-for="item in mobileNavItems" :key="item.to" :to="item.to" :icon="item.icon" :label="item.label" />
        </q-tabs>
      </q-footer>
    </template>

    <q-page-container :class="{ 'auth-container': isAuthPage }"><router-view /></q-page-container>
  </q-layout>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '../stores/authStore';
import { useNotificationStore } from '../stores/notificationStore';
import { useSearchStore } from '../stores/searchStore';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const notificationStore = useNotificationStore();
const searchStore = useSearchStore();
const drawerOpen = ref(false);
const searchQuery = ref('');
let notificationTimer;

const isAuthPage = computed(() => ['/login', '/register'].includes(route.path));
const unreadCount = computed(() => notificationStore.unreadCount);
const fullName = computed(() => [authStore.user?.prenom, authStore.user?.nom].filter(Boolean).join(' ') || 'Profil démo');
const userInitials = computed(() => `${authStore.user?.prenom?.[0] || ''}${authStore.user?.nom?.[0] || ''}`.toUpperCase() || 'K');
const navItems = computed(() => [
  { label: 'Fil d’actualité', icon: 'home', to: '/dashboard' },
  { label: 'Mon réseau', icon: 'group', to: '/connections' },
  { label: 'Messages', icon: 'chat_bubble_outline', to: '/messages' },
  { label: 'Notifications', icon: 'notifications_none', to: '/notifications', badge: unreadCount.value || null },
]);
const mobileNavItems = computed(() => navItems.value.slice(0, 4));
const goTo = (path) => router.push(path);
const goToSearch = () => {
  if (!searchQuery.value.trim()) return;
  searchStore.updateSearchQuery(searchQuery.value.trim());
  router.push('/search');
};
const goToProfile = () => {
  const userId = authStore.user?.idUtilisateur || authStore.user?.id;
  if (userId) router.push(`/profile/${userId}`);
};
const logout = () => { authStore.logout(); router.push('/login'); };
const loadNotifications = async () => {
  const userId = authStore.user?.idUtilisateur || authStore.user?.id;
  if (userId) await notificationStore.fetchNotificationsByUser(userId);
};
onMounted(() => { loadNotifications(); notificationTimer = window.setInterval(loadNotifications, 30000); });
onUnmounted(() => window.clearInterval(notificationTimer));
</script>

