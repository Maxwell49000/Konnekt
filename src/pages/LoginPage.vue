<template>
  <q-page class="row items-center justify-center">
    <div class="col-12 col-md-5">
      <div class="text-center q-mb-lg">
        <h1 class="text-h3 q-ma-none">LinkDong</h1>
        <p class="text-grey">Réseau social professionnel</p>
      </div>

      <q-card class="q-pa-lg">
        <q-form @submit.prevent="onSubmit" class="q-gutter-md">
          <q-input
            v-model="formData.email"
            label="Email"
            type="email"
            outlined
            :rules="[(val) => !!val || 'Email requis', (val) => validateEmail(val) || 'Email invalide']"
          />

          <!-- No password in backend: login by email only -->

          <div class="text-center">
            <q-btn
              label="Se connecter"
              type="submit"
              @click="onSubmit"
              color="primary"
              size="lg"
              unelevated
              class="full-width"
              :loading="isLoading"
            />
          </div>

          <div class="text-center q-mt-md" v-if="!authStore.isAuthenticated">
            <p class="q-mb-none">
              Pas encore inscrit?
              <router-link to="/register" class="text-primary">S'inscrire</router-link>
            </p>
          </div>
        </q-form>

        <q-linear-progress
          v-if="error"
          query
          color="negative"
          class="q-mt-md"
        />
        <p v-if="error" class="text-negative text-center q-mt-md">{{ error }}</p>
      </q-card>
    </div>
  </q-page>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/authStore';
import UtilisateurService from '../services/UtilisateurService';

const router = useRouter();
const authStore = useAuthStore();

const formData = ref({
  email: '',
  password: '',
});

const isLoading = ref(false);
const error = ref(null);

const validateEmail = (email) => {
  const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return re.test(email);
};

const onSubmit = async () => {
  console.log('Login submit clicked', formData.value);
  isLoading.value = true;
  error.value = null;

  try {
    const users = await UtilisateurService.getAllUtilisateurs();
    console.log('Fetched users:', users && users.length);
    const user = users.find((u) => u.email === formData.value.email);

    if (!user) {
      throw new Error('Utilisateur non trouvé');
    }

    // Simuler le login
    authStore.setUser(user);
    const userId = user.idUtilisateur || user.id;
    authStore.setToken(`token_${userId}`);

    // Redirect to dashboard after login
    router.push('/dashboard');
  } catch (err) {
    console.error('Login error:', err);
    const errorMsg = err.response?.data?.message || err.message || 'Erreur lors de la connexion';
    error.value = errorMsg;
  } finally {
    isLoading.value = false;
  }
};
</script>
