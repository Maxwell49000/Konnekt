<template>
  <q-page class="auth-page auth-page--register">
    <section class="auth-story">
      <router-link to="/login" class="brand brand--light"><span class="brand__mark brand__mark--light" aria-hidden="true">K</span><span>Konnekt</span></router-link>
      <div class="auth-story__content"><span class="eyebrow eyebrow--light">Votre espace professionnel</span><h1>Un profil clair. Des échanges qui comptent.</h1><p>Présentez votre parcours et retrouvez une communauté centrée sur les projets et les compétences.</p></div>
      <div class="auth-story__proof"><q-icon name="verified" /><span>Profil visible uniquement selon vos préférences.</span></div>
    </section>
    <section class="auth-form-panel">
      <div class="auth-form-wrap">
        <span class="eyebrow">Nouveau profil</span><h2>Rejoindre Konnekt</h2><p class="auth-intro">Quelques informations suffisent pour commencer la démonstration.</p>
        <q-form class="auth-form" @submit.prevent="onSubmit">
          <div class="row q-col-gutter-md"><q-input v-model="formData.prenom" class="col-12 col-sm-6" label="Prénom" outlined :rules="[(val) => !!val || 'Prénom requis']" /><q-input v-model="formData.nom" class="col-12 col-sm-6" label="Nom" outlined :rules="[(val) => !!val || 'Nom requis']" /></div>
          <q-input v-model="formData.email" label="Adresse e-mail" type="email" outlined :rules="[(val) => !!val || 'Adresse e-mail requise', (val) => validateEmail(val) || 'Adresse e-mail invalide']" />
          <q-input v-model="formData.titreProfessionnel" label="Intitulé professionnel" outlined hint="Ex. Développeuse frontend" />
          <q-btn label="Créer mon profil" type="submit" color="primary" unelevated no-caps size="lg" class="full-width auth-submit" :loading="isLoading" />
          <p class="auth-switch">Déjà un profil ? <router-link to="/login">Accéder à la démo</router-link></p>
        </q-form>
        <q-banner v-if="error" rounded class="error-banner">{{ error }}</q-banner>
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
const formData = ref({ prenom: '', nom: '', email: '', titreProfessionnel: '' });
const isLoading = ref(false);
const error = ref(null);
const validateEmail = (email) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
const onSubmit = async () => {
  isLoading.value = true;
  error.value = null;
  try {
    const newUser = await UtilisateurService.createUtilisateur({ ...formData.value, visibiliteProfil: true });
    authStore.startDemoSession(newUser);
    await router.push('/dashboard');
  } catch (err) {
    error.value = err.response?.data?.message || err.message || 'Impossible de créer ce profil.';
  } finally { isLoading.value = false; }
};
</script>
