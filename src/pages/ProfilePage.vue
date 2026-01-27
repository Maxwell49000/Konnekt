<template>
  <q-page class="profile-page bg-grey-1 q-pa-md">
    <!-- Header du profil -->
    <q-card class="profile-header relative-position q-mb-xl" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); min-height: 200px">
      <div class="absolute full-width q-pa-lg" style="height: 150px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
      </div>
      
      <div class="relative-position q-pa-lg">
        <div class="row items-end justify-between">
          <div class="row items-end q-gutter-lg">
            <!-- Avatar professionnel -->
            <q-avatar 
              size="120px" 
              color="white" 
              text-color="primary"
              font-size="48px"
              class="shadow-2"
            >
              {{ user?.prenom?.charAt(0) ?? 'U' }}
            </q-avatar>
            
            <div class="text-white">
              <h3 class="q-ma-none">{{ user?.prenom }} {{ user?.nom }}</h3>
              <p class="q-ma-sm text-caption text-white-70">{{ user?.titreProfessionnel || 'Titre professionnel' }}</p>
            </div>
          </div>
          
          <!-- Boutons d'actions -->
          <div class="q-gutter-sm">
            <q-btn 
              v-if="isOwner && !isEditing" 
              label="Modifier le profil" 
              color="white"
              text-color="primary"
              icon="edit"
              @click="startEdit"
              unelevated
              size="md"
            />
            <q-btn 
              v-if="isOwner && isEditing" 
              label="Annuler" 
              color="negative"
              icon="close"
              flat
              @click="cancelEdit"
            />
            
            <!-- Boutons de connexion pour les autres profils -->
            <q-btn 
              v-if="!isOwner && !isConnected && !connectionPending" 
              label="Connecter" 
              color="positive"
              icon="person_add"
              @click="sendConnectionRequest"
              :loading="isLoadingConnection"
              unelevated
            />
            <q-btn 
              v-if="!isOwner && connectionPending" 
              label="Demande en attente" 
              color="warning"
              icon="schedule"
              disable
              flat
            />
            <q-btn 
              v-if="!isOwner && isConnected" 
              label="Connecté" 
              color="positive"
              icon="check_circle"
              disable
              flat
            />
          </div>
        </div>
      </div>
    </q-card>

    <!-- Contenu du profil -->
    <div class="row q-col-gutter-lg">
      <!-- Colonne principale -->
      <div class="col-12 col-md-8">
        <!-- Section About -->
        <q-card class="q-mb-lg">
          <q-card-section class="bg-white">
            <div class="text-h6 q-mb-md">
              <q-icon name="info" color="primary" class="q-mr-sm" />
              À propos
            </div>
            <p v-if="!isEditing" class="q-my-sm text-body2">
              {{ user?.resume || user?.bio || 'Pas de résumé pour ce profil' }}
            </p>
            <q-input 
              v-if="isEditing"
              v-model="editModel.resume" 
              type="textarea"
              outlined
              dense
              label="Résumé professionnel"
              rows="4"
            />
          </q-card-section>
        </q-card>

        <!-- Section Expériences -->
        <q-card class="q-mb-lg">
          <q-card-section>
            <div class="text-h6 q-mb-md">
              <q-icon name="work" color="primary" class="q-mr-sm" />
              Expériences
            </div>

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
              <div v-if="experiences && experiences.length > 0">
                <div v-for="exp in experiences" :key="exp.idExperience" class="q-mb-md">
                  <div class="row items-start justify-between">
                    <div class="col">
                      <div class="text-weight-bold text-primary">{{ exp.poste }}</div>
                      <div class="text-subtitle2 text-grey">{{ exp.entreprise }}</div>
                      <div class="text-caption text-grey-7 q-mt-xs">
                        <q-icon name="event" size="xs" class="q-mr-xs" />
                        {{ formatDate(exp.dateDebut) }} — {{ exp.dateFin ? formatDate(exp.dateFin) : 'Présent' }}
                      </div>
                      <p v-if="exp.description" class="q-mt-sm q-mb-none text-body2">{{ exp.description }}</p>
                    </div>
                    <div v-if="isOwner" class="row q-gutter-xs">
                      <q-btn dense flat icon="edit" size="sm" @click="openEditExperience(exp)" />
                      <q-btn dense flat icon="delete" size="sm" color="negative" @click="confirmDeleteExperience(exp.idExperience)" />
                    </div>
                  </div>
                  <q-separator class="q-my-md" />
                </div>
              </div>
              <div v-else class="text-grey-7 text-italic q-py-md">Aucune expérience renseignée.</div>
              <q-btn v-if="isOwner" color="primary" icon="add" label="Ajouter une expérience" @click="openAddExperience" flat />
            </div>
          </q-card-section>
        </q-card>

        <!-- Section Derniers Posts -->
        <q-card class="q-mb-lg">
          <q-card-section>
            <div class="text-h6 q-mb-md">
              <q-icon name="post" color="primary" class="q-mr-sm" />
              Derniers posts
            </div>

            <div v-if="userPosts && userPosts.length > 0" class="q-gutter-md">
              <q-card 
                v-for="post in userPosts.slice(0, 5)" 
                :key="post.idPost"
                class="post-card"
              >
                <q-card-section>
                  <div class="text-weight-bold text-primary q-mb-sm">{{ post.contenu?.substring(0, 100) }}{{ post.contenu?.length > 100 ? '...' : '' }}</div>
                  <p v-if="post.contenu?.length > 100" class="text-body2 text-grey q-my-md">{{ post.contenu }}</p>
                  <div class="text-caption text-grey-7 q-mt-md">
                    <q-icon name="schedule" size="xs" class="q-mr-xs" />
                    {{ formatDate(post.dateCreation) }}
                  </div>
                </q-card-section>
                <q-separator />
                <q-card-actions>
                  <div class="row q-gutter-lg">
                    <div class="row items-center q-gutter-xs">
                      <q-icon name="favorite" color="red" size="sm" />
                      <span class="text-caption">{{ post.likes?.length || 0 }}</span>
                    </div>
                    <div class="row items-center q-gutter-xs">
                      <q-icon name="comment" color="primary" size="sm" />
                      <span class="text-caption">{{ post.comments?.length || 0 }}</span>
                    </div>
                  </div>
                  <q-space />
                  <q-btn 
                    flat 
                    color="primary" 
                    icon="open_in_new" 
                    label="Voir" 
                    size="sm"
                    @click="router.push('/dashboard')"
                  />
                </q-card-actions>
              </q-card>
            </div>
            <div v-else class="text-grey-7 text-italic q-py-md">Aucun post pour ce profil.</div>
          </q-card-section>
        </q-card>
      </div>

      <!-- Colonne latérale -->
      <div class="col-12 col-md-4">
        <!-- Infos de contact -->
        <q-card class="q-mb-lg">
          <q-card-section>
            <div class="text-h6 q-mb-md">
              <q-icon name="mail" color="primary" class="q-mr-sm" />
              Contact
            </div>
            <div v-if="!isEditing">
              <p class="q-my-sm text-body2">
                <strong>Email:</strong><br/>
                {{ user?.email }}
              </p>
              <p class="q-my-sm text-body2">
                <strong>Visibilité du profil:</strong><br/>
                <q-icon name="visibility" size="sm" class="q-mr-xs" />
                {{ user?.visibiliteProfil ? 'Public' : 'Privé' }}
              </p>
            </div>
            <div v-else>
              <q-input v-model="editModel.email" label="Email" outlined dense />
            </div>
          </q-card-section>
        </q-card>

        <!-- Section Compétences -->
        <q-card>
          <q-card-section>
            <div class="text-h6 q-mb-md">
              <q-icon name="verified" color="primary" class="q-mr-sm" />
              Compétences
            </div>
            <div v-if="user?.skills && user?.skills.length > 0" class="q-gutter-sm">
              <q-chip
                v-for="(s, idx) in user?.skills || []"
                :key="idx"
                color="primary"
                text-color="white"
                size="md"
                dense
              >
                <q-icon name="verified" size="xs" class="q-mr-xs" />
                {{ (s && s.libelle) ? s.libelle : s }}
              </q-chip>
            </div>
            <div v-else class="text-grey-7 text-italic text-body2">Pas de compétences</div>
          </q-card-section>
        </q-card>
      </div>
    </div>
    <!-- Experience add/edit dialog -->
    <q-dialog v-model="experienceDialog">
      <q-card style="min-width: 500px;">
        <q-card-section>
          <div class="text-h6">{{ isEditingExperience ? 'Modifier l\'expérience' : 'Ajouter une expérience' }}</div>
        </q-card-section>

        <q-card-section>
          <q-input v-model="experienceModel.poste" label="Poste" outlined dense />
          <q-input v-model="experienceModel.entreprise" label="Entreprise" outlined dense class="q-mt-md" />
          <q-input v-model="experienceModel.dateDebut" label="Date début" type="date" outlined dense class="q-mt-md" />
          <q-input v-model="experienceModel.dateFin" label="Date fin" type="date" outlined dense class="q-mt-md" />
          <q-input v-model="experienceModel.description" label="Description" type="textarea" outlined dense class="q-mt-md" />
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="Annuler" color="negative" v-close-popup @click="experienceDialog = false" />
          <q-btn color="primary" label="Enregistrer" @click="saveExperience" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import UtilisateurService from '../services/UtilisateurService';
import SkillService from '../services/SkillService';
import SeConnecteService from '../services/SeConnecteService';
import PostService from '../services/PostService';
import { useAuthStore } from '../stores/authStore';
import ExperienceService from '../services/ExperienceService';

const route = useRoute();
const router = useRouter();
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
const userPosts = ref([]);
const isSavingSkills = ref(false);

const experiences = ref([]);
const experienceDialog = ref(false);
const isEditingExperience = ref(false);
const currentExperience = ref(null);
const experienceModel = reactive({ poste: '', entreprise: '', dateDebut: '', dateFin: '', description: '' });

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
  loadExperiences();
  loadUserPosts();
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
    isSavingSkills.value = true;
    const id = route.params.id;

    // Ensure skills are skill objects { idSkill?, libelle }
    const skillLabels = (editModel.skills || []).map(s => (typeof s === 'string' ? s : s.libelle || ''))
      .map(s => s && s.trim())
      .filter(Boolean);

    const skillPromises = skillLabels.map(async (label) => {
      try {
        // Essayer de créer la compétence
        const created = await SkillService.createSkill(label).catch(async (err) => {
          // Si elle existe déjà (409), la chercher plutôt
          if (err.response?.status === 409 || err.response?.status === 400) {
            try {
              return await SkillService.getByLibelle(label);
            } catch {
              return null;
            }
          }
          throw err;
        });
        return created;
      } catch (err) {
        console.error(`Erreur création compétence "${label}":`, err.message);
        return null;
      }
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
    const skillIds = skillObjects
      .filter(s => s && s.idSkill) // Filter out null/undefined
      .map(s => s.idSkill);
    
    console.log('Skill IDs à sauvegarder:', skillIds);
    
    if (skillIds.length > 0) {
      await UtilisateurService.updateSkills(id, skillIds);
    }
    
    // Refresh user to get updated data with skills
    const refreshed = await UtilisateurService.getUtilisateur(id);
    user.value = refreshed;
    console.log('Compétences après refresh:', user.value.skills);
    
    // if editing own profile, update authStore
    const currentId = authStore.user?.idUtilisateur ?? authStore.user?.id;
    if (currentId && String(currentId) === String(id)) {
      authStore.setUser(refreshed);
    }
    isEditing.value = false;
  } catch (err) {
    console.error('Erreur mise à jour profil:', err);
    // Rechargez les données en cas d'erreur
    await fetchUser();
  } finally {
    isSavingSkills.value = false;
  }
};

const loadExperiences = async () => {
  try {
    const profileId = route.params.id;
    if (!profileId) return;
    experiences.value = await ExperienceService.getExperiencesByUtilisateur(profileId);
  } catch (err) {
    console.error('Erreur chargement expériences:', err);
  }
};

const openAddExperience = () => {
  isEditingExperience.value = false;
  currentExperience.value = null;
  experienceModel.poste = '';
  experienceModel.entreprise = '';
  experienceModel.dateDebut = '';
  experienceModel.dateFin = '';
  experienceModel.description = '';
  experienceDialog.value = true;
};

const openEditExperience = (exp) => {
  isEditingExperience.value = true;
  currentExperience.value = exp;
  experienceModel.poste = exp.poste || '';
  experienceModel.entreprise = exp.entreprise || '';
  experienceModel.dateDebut = exp.dateDebut || '';
  experienceModel.dateFin = exp.dateFin || '';
  experienceModel.description = exp.description || '';
  experienceDialog.value = true;
};

const saveExperience = async () => {
  try {
    const profileId = route.params.id;
    if (isEditingExperience.value && currentExperience.value) {
      const expId = currentExperience.value.idExperience ?? currentExperience.value.id;
      await ExperienceService.updateExperience(expId, {
        poste: experienceModel.poste,
        entreprise: experienceModel.entreprise,
        dateDebut: experienceModel.dateDebut,
        dateFin: experienceModel.dateFin,
        description: experienceModel.description,
      });
    } else {
      await ExperienceService.createExperience(profileId, {
        poste: experienceModel.poste,
        entreprise: experienceModel.entreprise,
        dateDebut: experienceModel.dateDebut,
        dateFin: experienceModel.dateFin,
        description: experienceModel.description,
      });
    }
    experienceDialog.value = false;
    await loadExperiences();
  } catch (err) {
    console.error('Erreur sauvegarde expérience:', err);
  }
};

const confirmDeleteExperience = async (id) => {
  if (!confirm('Supprimer cette expérience ?')) return;
  try {
    await ExperienceService.deleteExperience(id);
    await loadExperiences();
  } catch (err) {
    console.error('Erreur suppression expérience:', err);
  }
};

const formatDate = (dateStr) => {
  if (!dateStr) return '';
  try {
    return new Date(dateStr).toLocaleDateString();
  } catch {
    return dateStr;
  }
};

const loadUserPosts = async () => {
  try {
    const profileId = route.params.id;
    if (!profileId) return;
    
    // Utiliser l'endpoint spécifique pour les posts de l'utilisateur
    const posts = await PostService.getPostsByAuthor(profileId);
    // Trier par date décroissante (plus récent en premier)
    userPosts.value = Array.isArray(posts) ? posts.sort((a, b) => {
      const dateA = new Date(b.dateCreation || b.createdAt || 0).getTime();
      const dateB = new Date(a.dateCreation || a.createdAt || 0).getTime();
      return dateA - dateB;
    }) : [];
    console.log(`Posts chargés pour l'utilisateur ${profileId}:`, userPosts.value);
  } catch (err) {
    console.error('Erreur chargement posts utilisateur:', err);
    userPosts.value = [];
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

<style scoped>
.profile-page {
  background-color: #f5f7fa;
}

.profile-header {
  border-radius: 8px;
  overflow: hidden;
}

.post-card {
  border-left: 4px solid #667eea;
  transition: all 0.3s ease;
}

.post-card:hover {
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
  transform: translateY(-2px);
}
</style>
