<template>
  <q-page class="q-pa-md">
    <!-- Header avec formulaire de recherche -->
    <div class="row q-gutter-md q-mb-lg">
      <q-input
        v-model="searchQuery"
        outlined
        placeholder="Chercher utilisateurs, compétences, expériences..."
        class="col"
        @keyup.enter="performSearch"
      >
        <template v-slot:prepend>
          <q-icon name="search" />
        </template>
        <template v-slot:append v-if="searchQuery">
          <q-icon name="close" @click="clearSearch" class="cursor-pointer" />
        </template>
      </q-input>
      <q-btn
        color="primary"
        label="Chercher"
        @click="performSearch"
        icon="search"
        :loading="isSearching"
      />
    </div>

    <!-- Filtres par catégorie -->
    <div class="q-mb-lg">
      <div class="text-subtitle2 q-mb-sm">Catégories:</div>
      <div class="row q-gutter-md">
        <q-checkbox
          v-model="selectedCategories"
          val="utilisateurs"
          label="Utilisateurs"
          @update:model-value="toggleCategory('utilisateurs')"
        />
        <q-checkbox
          v-model="selectedCategories"
          val="skills"
          label="Compétences"
          @update:model-value="toggleCategory('skills')"
        />
        <q-checkbox
          v-model="selectedCategories"
          val="experiences"
          label="Expériences"
          @update:model-value="toggleCategory('experiences')"
        />
      </div>
    </div>

    <!-- Message d'erreur -->
    <q-banner v-if="searchError" class="bg-red-2 text-red-9 q-mb-md">
      ❌ {{ searchError }}
    </q-banner>

    <!-- Résultats -->
    <div v-if="hasSearched" class="q-gutter-lg">
      <!-- Utilisateurs -->
      <div v-if="selectedCategories.includes('utilisateurs')">
        <div class="text-h6 q-mb-md">
          👥 Utilisateurs
          <q-badge
            v-if="categoryCount.utilisateurs > 0"
            color="primary"
            floating
            rounded
            :label="categoryCount.utilisateurs"
          />
        </div>

        <div v-if="resultsByCategory.utilisateurs.length === 0" class="text-grey-7 q-pa-md">
          Aucun utilisateur trouvé
        </div>

        <div v-else class="row q-gutter-md">
          <q-card
            v-for="user in resultsByCategory.utilisateurs"
            :key="user.idUtilisateur"
            class="col-12 col-sm-6 col-md-4 cursor-pointer"
            @click="goToProfile(user.idUtilisateur)"
          >
            <q-card-section class="text-center">
              <div class="text-h6">{{ user.prenom }} {{ user.nom }}</div>
              <div class="text-caption text-grey-7">{{ user.email }}</div>

              <!-- Skills si présents -->
              <div v-if="user.skills && user.skills.length > 0" class="q-mt-md">
                <div class="text-caption text-weight-bold q-mb-sm">Compétences:</div>
                <div class="row justify-center q-gutter-xs">
                  <q-chip
                    v-for="skill in user.skills.slice(0, 3)"
                    :key="skill.idSkill"
                    size="sm"
                    color="blue-2"
                    text-color="blue-9"
                  >
                    {{ skill.libelle }}
                  </q-chip>
                  <q-chip
                    v-if="user.skills.length > 3"
                    size="sm"
                    color="blue-2"
                    text-color="blue-9"
                  >
                    +{{ user.skills.length - 3 }}
                  </q-chip>
                </div>
              </div>

              <q-btn
                flat
                color="primary"
                label="Voir profil"
                class="full-width q-mt-md"
                @click.stop="goToProfile(user.idUtilisateur)"
              />
            </q-card-section>
          </q-card>
        </div>
      </div>

      <!-- Compétences -->
      <div v-if="selectedCategories.includes('skills')">
        <div class="text-h6 q-mb-md">
          🎯 Compétences
          <q-badge
            v-if="categoryCount.skills > 0"
            color="info"
            floating
            rounded
            :label="categoryCount.skills"
          />
        </div>

        <div v-if="resultsByCategory.skills.length === 0" class="text-grey-7 q-pa-md">
          Aucune compétence trouvée
        </div>

        <div v-else class="row q-gutter-md">
          <q-chip
            v-for="skill in resultsByCategory.skills"
            :key="skill.idSkill"
            clickable
            color="orange-2"
            text-color="orange-9"
            size="lg"
            class="cursor-pointer"
            @click="navigateToSkillUsers(skill.idSkill, skill.libelle)"
          >
            <q-icon name="star" left />
            {{ skill.libelle }}
          </q-chip>
        </div>
      </div>

      <!-- Expériences -->
      <div v-if="selectedCategories.includes('experiences')">
        <div class="text-h6 q-mb-md">
          💼 Expériences
          <q-badge
            v-if="categoryCount.experiences > 0"
            color="positive"
            floating
            rounded
            :label="categoryCount.experiences"
          />
        </div>

        <div v-if="resultsByCategory.experiences.length === 0" class="text-grey-7 q-pa-md">
          Aucune expérience trouvée
        </div>

        <q-timeline
          v-else
          color="positive"
          layout="dense"
        >
          <q-timeline-entry
            v-for="exp in resultsByCategory.experiences"
            :key="exp.idExperience"
            :title="exp.poste"
            :subtitle="exp.entreprise"
            :body="exp.description"
            icon="business"
          >
            <div class="text-caption text-grey-7">
              {{ formatDate(exp.dateDebut) }} - {{ exp.dateFin ? formatDate(exp.dateFin) : 'Actuellement' }}
            </div>
          </q-timeline-entry>
        </q-timeline>
      </div>

      <!-- Résumé -->
      <div class="q-mt-lg text-center text-grey-7">
        <div v-if="searchResults.totalResults === 0" class="text-h6">
          Aucun résultat pour "{{ searchQuery }}"
        </div>
        <div v-else class="text-h6">
          {{ searchResults.totalResults }} résultat{{ searchResults.totalResults > 1 ? 's' : '' }} trouvé{{ searchResults.totalResults > 1 ? 's' : '' }}
        </div>
      </div>
    </div>

    <!-- Appel à l'action initial -->
    <div v-else class="text-center q-pa-lg text-grey-7">
      <q-icon name="search" size="64px" class="q-mb-md" />
      <div class="text-h6">Commencez à chercher</div>
      <div>Entrez un terme de recherche pour trouver des utilisateurs, compétences ou expériences</div>
    </div>
  </q-page>
</template>

<script setup>
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { useSearchStore } from '../stores/searchStore';

const router = useRouter();
const searchStore = useSearchStore();

const searchQuery = computed({
  get: () => searchStore.searchQuery,
  set: (value) => searchStore.updateSearchQuery(value)
});

const selectedCategories = computed({
  get: () => searchStore.selectedCategories,
  set: (value) => searchStore.setCategories(value)
});

const isSearching = computed(() => searchStore.isSearching);
const hasSearched = computed(() => searchStore.hasSearched);
const searchError = computed(() => searchStore.searchError);
const searchResults = computed(() => searchStore.searchResults);
const resultsByCategory = computed(() => searchStore.resultsByCategory);
const categoryCount = computed(() => searchStore.categoryCount);

const performSearch = () => {
  searchStore.performSearch(searchQuery.value, selectedCategories.value);
};

const clearSearch = () => {
  searchStore.clearSearch();
};

const toggleCategory = (category) => {
  searchStore.toggleCategory(category);
  if (hasSearched.value) {
    performSearch();
  }
};

const goToProfile = (userId) => {
  router.push(`/profile/${userId}`);
};

const navigateToSkillUsers = (skillId, skillName) => {
  router.push({
    name: 'utilisateurs',
    query: { skill: skillId, skillName: skillName }
  });
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('fr-FR', { year: 'numeric', month: 'long' });
};
</script>

<style scoped>
.q-badge {
  position: absolute;
  right: -8px;
  top: -8px;
}
</style>
