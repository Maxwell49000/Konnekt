<template>
  <q-page class="q-pa-md">
    <div class="text-h6 q-mb-md">Connexions</div>
    <div class="row q-col-gutter-md">
      <!-- Demandes en attente -->
      <div class="col-12 col-md-6">
        <q-card class="q-pa-md">
          <h6 class="q-ma-none q-mb-md">Demandes en attente</h6>
          <q-list bordered separator>
            <q-item v-for="req in pendingRequests" :key="`pending-${req.id || req.demandeur?.idUtilisateur}-${req.destinataire?.idUtilisateur}`">
              <q-item-section avatar>
                <q-avatar color="primary" text-color="white">{{ (getOther(req).prenom?.charAt(0) || 'U') }}</q-avatar>
              </q-item-section>
              <q-item-section>
                <q-item-label>{{ getOther(req).prenom }} {{ getOther(req).nom }}</q-item-label>
              </q-item-section>
              <q-item-section side>
                <div class="text-grey q-gutter-xs">
                  <q-btn size="sm" color="positive" label="Accepter" @click="accept(req)" />
                  <q-btn size="sm" color="negative" label="Refuser" @click="refuse(req)" />
                </div>
              </q-item-section>
            </q-item>
            <q-item v-if="pendingRequests.length === 0" class="text-center">
              <q-item-section>
                <q-item-label caption>Aucune demande en attente</q-item-label>
              </q-item-section>
            </q-item>
          </q-list>
        </q-card>
      </div>

      <!-- Connexions -->
      <div class="col-12 col-md-6">
        <q-card class="q-pa-md">
          <h6 class="q-ma-none q-mb-md">Mes connexions</h6>
          <q-list bordered separator>
            <q-item v-for="conn in connections" :key="conn.id || (conn.demandeur?.idUtilisateur + '-' + conn.destinataire?.idUtilisateur)" clickable @click="goToProfile(getOther(conn))">
              <q-item-section avatar>
                <q-avatar color="primary" text-color="white">{{ (getOther(conn).prenom?.charAt(0) || 'U') }}</q-avatar>
              </q-item-section>
              <q-item-section>
                <q-item-label>{{ getOther(conn).prenom }} {{ getOther(conn).nom }}</q-item-label>
              </q-item-section>
              <q-item-section side>
                <div class="text-grey q-gutter-xs">
                  <q-btn size="sm" color="warning" label="Bloquer" @click="blockConn(conn)" />
                  <q-btn size="sm" color="negative" label="Supprimer" @click="removeConn(conn)" />
                </div>
              </q-item-section>
            </q-item>
            <q-item v-if="connections.length === 0" class="text-center">
              <q-item-section>
                <q-item-label caption>Aucune connexion</q-item-label>
              </q-item-section>
            </q-item>
          </q-list>
        </q-card>
      </div>
    </div>
  </q-page>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/authStore';
import ConnectionService from '../services/ConnectionService';

const authStore = useAuthStore();
const router = useRouter();

const pendingRequests = ref([]);
const connections = ref([]);

const getOther = (seConnecte) => {
  const meId = authStore.user?.idUtilisateur || authStore.user?.id;
  const demandeur = seConnecte.demandeur || seConnecte.demandeurUtilisateur || {};
  const destinataire = seConnecte.destinataire || seConnecte.destinataireUtilisateur || {};
  if (String(demandeur.idUtilisateur || demandeur.id) === String(meId)) return destinataire;
  return demandeur;
};

const load = async () => {
  try {
    let meId = authStore.user?.idUtilisateur || authStore.user?.id;
    if (!meId && authStore.token) {
      const m = String(authStore.token).match(/^(?:demo|token)_(\d+)$/);
      if (m) meId = m[1];
    }
      if (!meId) {
        console.log('Connections.load: no meId resolved, aborting load');
        return;
      }
    const pending = await ConnectionService.getPendingConnections(meId) || [];
    const accepted = await ConnectionService.getConnections(meId) || [];
      console.log('Connections.load: authUser=', authStore.user, 'token=', authStore.token, 'resolvedMeId=', meId);
      console.log('Connections.load: pending=', pending, 'accepted=', accepted);
    pendingRequests.value = pending;
    connections.value = accepted;
  } catch (err) {
    console.error('Erreur chargement connexions:', err);
  }
};

const accept = async (se) => {
  try {
    const demandeurId = se.demandeur?.idUtilisateur || se.demandeur?.id;
    const destinataireId = se.destinataire?.idUtilisateur || se.destinataire?.id;
    await ConnectionService.acceptConnectionRequest(demandeurId, destinataireId);
    await load();
  } catch (err) {
    console.error('Erreur acceptation:', err);
  }
};

const refuse = async (se) => {
  try {
    const demandeurId = se.demandeur?.idUtilisateur || se.demandeur?.id;
    const destinataireId = se.destinataire?.idUtilisateur || se.destinataire?.id;
    await ConnectionService.refuseConnectionRequest(demandeurId, destinataireId);
    await load();
  } catch (err) {
    console.error('Erreur refus:', err);
  }
};

const blockConn = async (se) => {
  try {
    const demandeurId = se.demandeur?.idUtilisateur || se.demandeur?.id;
    const destinataireId = se.destinataire?.idUtilisateur || se.destinataire?.id;
    await ConnectionService.blockUser(demandeurId, destinataireId);
    await load();
  } catch (err) {
    console.error('Erreur blocage:', err);
  }
};

const removeConn = async (se) => {
  try {
    const id = se.id || se.seConnecteId || se.idSeConnecte;
    if (id) {
      await ConnectionService.deleteConnectionById(id);
    } else {
      const demandeurId = se.demandeur?.idUtilisateur || se.demandeur?.id;
      const destinataireId = se.destinataire?.idUtilisateur || se.destinataire?.id;
      await ConnectionService.deleteConnectionBetween(demandeurId, destinataireId);
    }
    await load();
  } catch (err) {
    console.error('Erreur suppression connexion:', err);
  }
};

const goToProfile = (user) => {
  const userId = user?.idUtilisateur || user?.id;
  if (userId) {
    router.push(`/profile/${userId}`);
  }
};

let pollId = null;

onMounted(() => {
  load();
  // refresh when window/tab regains focus
  window.addEventListener('focus', load);

  // refresh when document becomes visible
  const onVisibility = () => {
    if (document.visibilityState === 'visible') load();
  };
  document.addEventListener('visibilitychange', onVisibility);

  // periodic polling as fallback
  pollId = setInterval(() => {
    load().catch(() => {});
  }, 15000);

  onUnmounted(() => {
    window.removeEventListener('focus', load);
    document.removeEventListener('visibilitychange', onVisibility);
    if (pollId) clearInterval(pollId);
  });
});
</script>
