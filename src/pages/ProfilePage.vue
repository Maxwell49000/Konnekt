<template>
  <q-page class="q-pa-md">
    <div class="text-h6 q-mb-md">Profil</div>
    <q-card class="q-pa-md">
      <div class="text-center q-mb-md">
        <q-avatar size="100px" color="primary" text-color="white">{{ user?.prenom?.charAt(0) ?? 'U' }}</q-avatar>
      </div>
      <div class="text-center q-mb-lg">
          <div class="row items-center justify-between">
            <div>
              <h5 class="q-ma-none">{{ user?.prenom }} {{ user?.nom }}</h5>
              <p class="text-grey">{{ user?.titreProfessionnel || 'Profil utilisateur' }}</p>
            </div>
            <div class="q-gutter-sm">
              <q-btn v-if="isOwner && !isEditing" dense label="Modifier" color="primary" @click="startEdit" />
              <q-btn v-if="isOwner && isEditing" dense flat label="Annuler" color="negative" @click="cancelEdit" />
              
              <!-- Bouton de connexion pour les autres profils -->
              <q-btn 
                v-if="!isOwner && !isConnected && !connectionPending" 
                dense 
                label="Demander une connexion" 
                color="positive"
                icon="person_add"
                @click="sendConnectionRequest"
                :loading="isLoadingConnection"
              />
              <q-btn 
                v-if="!isOwner && connectionPending" 
                dense 
                label="Demande en attente" 
                color="warning"
                icon="schedule"
                disable
              />
              <q-btn 
                v-if="!isOwner && isConnected" 
                dense 
                label="Connecté" 
                color="primary"
                icon="check_circle"
                disable
              />
            </div>
          </div>
      </div>

      <div class="q-mb-md">
        <q-form v-if="isEditing" @submit.prevent="saveEdit">
          <q-input v-model="editModel.prenom" label="Prénom" outlined dense />
          <q-input v-model="editModel.nom" label="Nom" outlined dense class="q-mt-md" />
          <q-input v-model="editModel.titreProfessionnel" label="Titre professionnel" outlined dense class="q-mt-md" />
          <q-input v-model="editModel.email" label="Email" outlined dense class="q-mt-md" />
          <q-input v-model="editModel.resume" type="textarea" label="Résumé" outlined dense class="q-mt-md" />

          <q-card class="q-mt-lg q-pa-md bg-grey-1">
            <div class="text-h6 q-mb-md">
              <q-icon name="school" color="primary" class="q-mr-sm" />
              Compétences
            </div>

            <div v-if="editModel.skills && editModel.skills.length > 0" class="q-mb-lg">
              <div class="text-subtitle2 q-mb-md text-grey-8">Compétences actuelles</div>
              <div class="row q-gutter-md">
                <q-chip
                  v-for="(s, idx) in editModel.skills"
                  :key="idx"
                  removable
                  @remove="removeSkill(idx)"
                  color="primary"
                  text-color="white"
                  size="lg"
                >
                  <q-icon name="check_circle" class="q-mr-xs" />
                  {{ s }}
                </q-chip>
              </div>
            </div>

            <q-separator v-if="editModel.skills && editModel.skills.length > 0" class="q-my-md" />

            <div class="text-subtitle2 q-mb-md text-grey-8">Ajouter une compétence</div>

            <div class="row q-col-gutter-md">
              <div class="col-12 col-md-6">
                <div class="text-caption text-grey q-mb-sm">Créer une nouvelle compétence</div>
                <div class="row q-gutter-sm">
                  <q-input
                    v-model="newSkill"
                    outlined
                    dense
                    placeholder="Nom de la compétence"
                    class="col-grow"
                    @keyup.enter="addSkill"
                  >
                    <template #prepend>
                      <q-icon name="add" />
                    </template>
                  </q-input>
                  <q-btn
                    outline
                    color="primary"
                    label="Ajouter"
                    @click="addSkill"
                    class="q-px-lg"
                  />
                </div>
              </div>

              <div class="col-12 col-md-6">
                <div class="text-caption text-grey q-mb-sm">Ou en sélectionner une existante</div>
                <div class="row q-gutter-sm">
                  <q-select
                    v-model="selectedSkillId"
                    :options="availableSkills"
                    option-value="idSkill"
                    option-label="libelle"
                    outlined
                    dense
                    emit-value
                    map-options
                    placeholder="Sélectionner une compétence"
                    class="col-grow"
                  >
                    <template #prepend>
                      <q-icon name="category" />
                    </template>
                  </q-select>
                  <q-btn
                    outline
                    color="primary"
                    label="Ajouter"
                    @click="addSkillFromList"
                    class="q-px-lg"
                  />
                </div>
              </div>
            </div>
          </q-card>

          <div class="row q-mt-lg q-gutter-md">
            <q-btn type="submit" label="Enregistrer" color="primary" size="md" class="q-px-lg" />
            <q-btn outline label="Annuler" color="negative" size="md" class="q-px-lg" @click="cancelEdit" />
          </div>
        </q-form>

        <div v-else>
          <div class="q-mb-md">
            <div class="text-overline text-grey">Contact</div>
            <p class="q-my-sm"><strong>Email:</strong> {{ user?.email }}</p>
          </div>

          <div class="q-mb-md">
            <div class="text-overline text-grey">À propos</div>
            <p class="q-my-sm">{{ user?.resume || user?.bio || '— Pas de résumé' }}</p>
          </div>

          <div class="q-mb-md">
            <div class="text-overline text-grey">Paramètres de profil</div>
            <p class="q-my-sm"><q-icon name="visibility" class="q-mr-xs" />Visibilité: {{ user?.visibiliteProfil ? 'Public' : 'Privé' }}</p>
          </div>

          <q-card class="q-pa-md bg-grey-1">
            <div class="text-h6 q-mb-md">
              <q-icon name="school" color="primary" class="q-mr-sm" />
              Compétences
            </div>
            <div v-if="user?.skills && user?.skills.length > 0" class="row q-gutter-md">
              <q-chip
                v-for="(s, idx) in user?.skills || []"
                :key="idx"
                color="primary"
                text-color="white"
                size="lg"
              >
                <q-icon name="verified" class="q-mr-xs" />
                {{ (s && s.libelle) ? s.libelle : s }}
              </q-chip>
            </div>
            <div v-else class="text-grey-7 text-italic">Pas de compétences</div>
          </q-card>
        </div>
      </div>
    </q-card>
  </q-page>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { useRoute } from 'vue-router';
import UtilisateurService from '../services/UtilisateurService';
import SkillService from '../services/SkillService';
import SeConnecteService from '../services/SeConnecteService';
import { useAuthStore } from '../stores/authStore';

const route = useRoute();
const authStore = useAuthStore();

const user = ref(null);
const isEditing = ref(false);
const editModel = reactive({ prenom: '', nom: '', titreProfessionnel: '', email: '', resume: '', skills: [] });
const newSkill = ref('');
const availableSkills = ref([]);
const selectedSkillId = ref(null);
const isConnected = ref(false);
const connectionPending = ref(false);
const isLoadingConnection = ref(false);

const isOwner = computed(() => {
  const currentId = authStore.user?.idUtilisateur ?? authStore.user?.id;
  return currentId && route.params.id && String(currentId) === String(route.params.id);
});

const fetchUser = async () => {
  try {
    const id = route.params.id;
    if (!id) return;
    user.value = await UtilisateurService.getUtilisateur(id);
    
    // Check connection status only if not viewing own profile
    if (!isOwner.value) {
      checkConnectionStatus();
    }
  } catch (err) {
    console.error('Erreur chargement profil:', err);
  }
};

const checkConnectionStatus = async () => {
  try {
    const currentId = authStore.user?.idUtilisateur ?? authStore.user?.id;
    const profileId = parseInt(route.params.id);
    
    // Check if connected
    const acceptedConnections = await SeConnecteService.getAcceptedConnections(currentId);
    isConnected.value = acceptedConnections.some(c => {
      const demandeurId = c.demandeur?.idUtilisateur || c.demandeur?.id;
      const destinataireId = c.destinataire?.idUtilisateur || c.destinataire?.id;
      return demandeurId === profileId || destinataireId === profileId;
    });
    
    // Check if pending
    if (!isConnected.value) {
      const pendingConnections = await SeConnecteService.getPendingConnections(currentId);
      connectionPending.value = pendingConnections.some(c => {
        const demandeurId = c.demandeur?.idUtilisateur || c.demandeur?.id;
        const destinataireId = c.destinataire?.idUtilisateur || c.destinataire?.id;
        return demandeurId === profileId || destinataireId === profileId;
      });
    }
  } catch (err) {
    console.warn('Erreur vérification connexion:', err);
  }
};

onMounted(() => {
  fetchUser();
  loadAvailableSkills();
});

const loadAvailableSkills = async () => {
  try {
    availableSkills.value = await SkillService.getAllSkills();
  } catch (err) {
    console.error('Erreur chargement skills disponibles:', err);
  }
};

const startEdit = () => {
  if (!user.value) return;
  isEditing.value = true;
  editModel.prenom = user.value.prenom || '';
  editModel.nom = user.value.nom || '';
  editModel.titreProfessionnel = user.value.titreProfessionnel || '';
  editModel.email = user.value.email || '';
  editModel.resume = user.value.resume || user.value.bio || '';
  editModel.skills = Array.isArray(user.value.skills) ? user.value.skills.map(s => (typeof s === 'string' ? s : s.libelle || '')) : [];
  newSkill.value = '';
  selectedSkillId.value = null;
};

const cancelEdit = () => {
  isEditing.value = false;
};

const addSkill = () => {
  const s = (newSkill.value || '').trim();
  if (!s) return;
  if (!editModel.skills) editModel.skills = [];
  if (!editModel.skills.includes(s)) editModel.skills.push(s);
  newSkill.value = '';
};

const addSkillFromList = () => {
  if (!selectedSkillId.value) return;
  const skill = availableSkills.value.find(s => s.idSkill === selectedSkillId.value);
  if (!skill) return;
  if (!editModel.skills) editModel.skills = [];
  if (!editModel.skills.includes(skill.libelle)) editModel.skills.push(skill.libelle);
  selectedSkillId.value = null;
};

const removeSkill = (idx) => {
  if (!editModel.skills) return;
  editModel.skills.splice(idx, 1);
};

const saveEdit = async () => {
  try {
    const id = route.params.id;

    // Ensure skills are skill objects { idSkill?, libelle }
    const skillLabels = (editModel.skills || []).map(s => (typeof s === 'string' ? s : s.libelle || ''))
      .map(s => s && s.trim())
      .filter(Boolean);

    const skillPromises = skillLabels.map(async (label) => {
      // try existing
      const existing = await SkillService.getByLibelle(label).catch(() => null);
      if (existing) return existing;
      // create new skill
      return await SkillService.createSkill(label);
    });

    const skillObjects = await Promise.all(skillPromises);

    const payload = {
      prenom: editModel.prenom,
      nom: editModel.nom,
      titreProfessionnel: editModel.titreProfessionnel,
      email: editModel.email,
      resume: editModel.resume,
    };

    await UtilisateurService.updateUtilisateur(id, payload);
    
    // Update skills separately via PUT /utilisateurs/{id}/skills
    const skillIds = skillObjects.map(s => s.idSkill).filter(Boolean);
    await UtilisateurService.updateSkills(id, skillIds);
    
    // Refresh user to get updated data with skills
    const refreshed = await UtilisateurService.getUtilisateur(id);
    user.value = refreshed;
    
    // if editing own profile, update authStore
    const currentId = authStore.user?.idUtilisateur ?? authStore.user?.id;
    if (currentId && String(currentId) === String(id)) {
      authStore.setUser(refreshed);
    }
    isEditing.value = false;
  } catch (err) {
    console.error('Erreur mise à jour profil:', err);
  }
};

const sendConnectionRequest = async () => {
  try {
    isLoadingConnection.value = true;
    const currentId = authStore.user?.idUtilisateur ?? authStore.user?.id;
    const profileId = parseInt(route.params.id);
    
    await SeConnecteService.createConnection(currentId, profileId);
    connectionPending.value = true;
  } catch (err) {
    console.error('Erreur demande de connexion:', err);
  } finally {
    isLoadingConnection.value = false;
  }
};
</script>
