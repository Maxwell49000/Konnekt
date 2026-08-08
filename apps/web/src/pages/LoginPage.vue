<template>
  <q-page class="auth-page">
    <section class="auth-story">
      <router-link to="/login" class="brand brand--light"><span class="brand__word">konnekt</span></router-link>
      <div class="auth-story__content">
        <span class="eyebrow eyebrow--light">Réseau professionnel</span>
        <h1>Les bonnes rencontres font avancer les idées.</h1>
        <p>Partagez vos projets, développez votre réseau et échangez avec des profils qui parlent votre métier.</p>
      </div>
      <div class="auth-story__proof"><q-icon name="hub" /><span>Une expérience pensée pour les échanges utiles.</span></div>
    </section>
    <section class="auth-form-panel">
      <div class="auth-form-wrap">
        <span class="eyebrow">Espace de démonstration</span>
        <h2>Bienvenue sur Konnekt</h2>
        <p class="auth-intro">Entrez l’adresse d’un profil existant pour explorer l’application.</p>
        <q-form class="auth-form" @submit.prevent="onSubmit">
          <q-input v-model="formData.email" label="Adresse e-mail" type="email" outlined autocomplete="email" :rules="[(val) => !!val || 'Adresse e-mail requise', (val) => validateEmail(val) || 'Adresse e-mail invalide']" />
          <q-btn label="Accéder à la démo" type="submit" color="primary" unelevated no-caps size="lg" class="full-width auth-submit" :loading="isLoading" />
          <div class="demo-notice"><q-icon name="info_outline" /><span>Ce projet portfolio utilise une session de démonstration, sans mot de passe.</span></div>
          <p class="auth-switch">Pas encore de profil ? <router-link to="/register">Créer un profil</router-link></p>
        </q-form>
        <q-banner v-if="error" rounded class="error-banner"><template #avatar><q-icon name="error_outline" color="negative" /></template>{{ error }}</q-banner>
      </div>
    </section>
  </q-page>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/authStore';
import UtilisateurService from '../services/UtilisateurService';
const router = useRouter();
const authStore = useAuthStore();
const formData = ref({ email: '' });
const isLoading = ref(false);
const error = ref(null);
const validateEmail = (email) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
const onSubmit = async () => {
  isLoading.value = true;
  error.value = null;
  try {
    const users = await UtilisateurService.getAllUtilisateurs();
    const user = users.find((candidate) => candidate.email?.toLowerCase() === formData.value.email.toLowerCase());
    if (!user) throw new Error('Aucun profil ne correspond à cette adresse.');
    authStore.startDemoSession(user);
    await router.push('/dashboard');
  } catch (err) {
    error.value = err.response?.data?.message || err.message || 'Impossible d’ouvrir la session de démonstration.';
  } finally { isLoading.value = false; }
};
</script>
