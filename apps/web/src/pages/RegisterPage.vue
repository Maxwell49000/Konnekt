<template>
  <q-page class="row items-center justify-center">
    <div class="col-12 col-md-5">
      <div class="text-center q-mb-lg">
        <h1 class="text-h3 q-ma-none">Créer un compte</h1>
        <p class="text-grey">Rejoignez LinkDong</p>
      </div>

      <q-card class="q-pa-lg">
        <q-form @submit="onSubmit" class="q-gutter-md">
          <div class="row q-col-gutter-md">
            <div class="col-6">
              <q-input
                v-model="formData.prenom"
                label="Prénom"
                outlined
                :rules="[(val) => !!val || 'Prénom requis']"
              />
            </div>
            <div class="col-6">
              <q-input
                v-model="formData.nom"
                label="Nom"
                outlined
                :rules="[(val) => !!val || 'Nom requis']"
              />
            </div>
          </div>

          <q-input
            v-model="formData.email"
            label="Email"
            type="email"
            outlined
            :rules="[(val) => !!val || 'Email requis', (val) => validateEmail(val) || 'Email invalide']"
          />

          <q-input
            v-model="formData.motDePasse"
            label="Mot de passe"
            type="password"
            outlined
            :rules="[(val) => !!val || 'Mot de passe requis', (val) => val.length >= 6 || 'Au minimum 6 caractères']"
          />

          <q-input
            v-model="formData.confirmPassword"
            label="Confirmer le mot de passe"
            type="password"
            outlined
            :rules="[(val) => val === formData.motDePasse || 'Les mots de passe ne correspondent pas']"
          />

          <div class="text-center">
            <q-btn
              label="S'inscrire"
              type="submit"
              color="primary"
              size="lg"
              unelevated
              class="full-width"
              :loading="isLoading"
            />
          </div>

          <div class="text-center q-mt-md">
            <p class="q-mb-none">
              Déjà inscrit?
              <router-link to="/login" class="text-primary">Se connecter</router-link>
            </p>
          </div>
        </q-form>

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
  prenom: '',
  nom: '',
  email: '',
  motDePasse: '',
  confirmPassword: '',
});

const isLoading = ref(false);
const error = ref(null);

const validateEmail = (email) => {
  const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return re.test(email);
};

const onSubmit = async () => {
  isLoading.value = true;
  error.value = null;

  try {
    const newUser = await UtilisateurService.createUtilisateur({
      nom: formData.value.nom,
      prenom: formData.value.prenom,
      email: formData.value.email,
      motDePasse: formData.value.motDePasse,
    });

    authStore.setUser(newUser);
    const newId = newUser.idUtilisateur || newUser.id;
    authStore.setToken(`token_${newId}`);

    router.push('/');
  } catch (err) {
    const errorMsg = err.message || 'Erreur lors de l\'inscription';
    error.value = errorMsg;
  } finally {
    isLoading.value = false;
  }
};
</script>
