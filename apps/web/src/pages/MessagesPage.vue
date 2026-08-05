<template>
  <q-page class="q-pa-md">
    <div class="row q-col-gutter-md" style="height: 600px">
      <!-- Conversations List -->
      <div class="col-12 col-md-4">
        <q-card class="full-height column">
          <q-card-section class="q-pa-md border-bottom">
            <div class="row items-center justify-between">
              <h6 class="q-ma-none">Messages</h6>
              <q-btn icon="add" round flat size="sm" color="primary" @click="showNewConversationDialog = true" />
            </div>
          </q-card-section>
          <q-scroll-area class="col">
            <q-list separator>
              <q-item v-if="conversations.length === 0" class="text-center">
                <q-item-section>
                  <q-item-label caption>Aucune conversation</q-item-label>
                </q-item-section>
              </q-item>
              <q-item
                v-for="conv in conversations"
                :key="`conv-${conv.id}`"
                clickable
                :active="currentConversation && currentConversation.id === conv.id"
                active-class="bg-primary text-white"
                @click="openConversation(conv.id)"
              >
                <q-item-section avatar>
                  <q-avatar color="primary" text-color="white">{{ getConversationAvatar(conv) }}</q-avatar>
                </q-item-section>
                <q-item-section>
                  <q-item-label>{{ getConversationTitle(conv) }}</q-item-label>
                  <q-item-label caption lines="1">{{ conv.lastMessagePreview || '' }}</q-item-label>
                </q-item-section>
              </q-item>
            </q-list>
          </q-scroll-area>
        </q-card>
      </div>

      <!-- Chat View -->
      <div class="col-12 col-md-8">
        <q-card class="full-height column" v-if="currentConversation">
          <q-card-section class="q-pa-md border-bottom">
            <div class="row items-center q-gutter-md">
              <q-avatar color="primary" text-color="white">{{ getConversationAvatar(currentConversation) }}</q-avatar>
              <div>
                <div class="text-weight-bold">{{ getConversationTitle(currentConversation) }}</div>
                <div class="text-grey text-caption">{{ getConversationSubtitle(currentConversation) }}</div>
              </div>
            </div>
          </q-card-section>

          <q-scroll-area class="col q-pa-md">
            <div class="q-gutter-md">
              <div v-for="msg in currentConversation?.messages || []" :key="`msg-${msg.id || msg._id || msg.timestamp}`" :class="(msg.auteurId || msg.senderId) === getUserId() ? 'row' : 'row reverse'">
                <q-chat-message
                  :name="msg.auteurName || (((msg.auteurId || msg.senderId) === getUserId()) ? 'Moi' : 'Utilisateur')"
                  :avatar="undefined"
                  :text="[msg.contenu || msg.text || '']"
                  :sent="msg.auteurId === getUserId()"
                />
              </div>
            </div>
          </q-scroll-area>

          <q-card-section class="q-pa-md border-top">
            <div class="row items-center q-gutter-md">
              <q-input
                v-model="messageText"
                filled
                dense
                placeholder="Écrivez un message..."
                class="col"
                @keyup.enter="sendMessage"
              />
              <q-btn icon="send" round color="primary" @click="sendMessage" :loading="isSendingMessage" />
            </div>
          </q-card-section>
        </q-card>
        <q-card v-else class="full-height column items-center justify-center">
          <q-card-section class="text-center">
            <p class="text-grey">Sélectionnez une conversation ou créez-en une nouvelle</p>
          </q-card-section>
        </q-card>
      </div>
    </div>

    <!-- New Conversation Dialog -->
    <q-dialog v-model="showNewConversationDialog">
      <q-card style="min-width: 400px">
        <q-card-section class="row items-center q-pb-none">
          <div class="text-h6">Nouvelle conversation</div>
          <q-space />
          <q-btn icon="close" flat round dense v-close-popup />
        </q-card-section>
        <q-card-section>
          <div class="text-subtitle2 q-mb-md">Sélectionnez une connexion</div>
          <q-list bordered separator>
            <q-item
              v-for="conn in connections"
              :key="`conn-${getOther(conn).idUtilisateur}`"
              clickable
              @click="selectConnectionForChat(getOther(conn))"
            >
              <q-item-section avatar>
                <q-avatar color="primary" text-color="white">{{ getOther(conn).prenom?.charAt(0) || 'U' }}</q-avatar>
              </q-item-section>
              <q-item-section>
                <q-item-label>{{ getOther(conn).prenom }} {{ getOther(conn).nom }}</q-item-label>
              </q-item-section>
            </q-item>
            <q-item v-if="connections.length === 0" class="text-center">
              <q-item-section>
                <q-item-label caption>Aucune connexion disponible</q-item-label>
              </q-item-section>
            </q-item>
          </q-list>
        </q-card-section>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useAuthStore } from '../stores/authStore';
import { useConversationStore } from '../stores/conversationStore';
import ConversationService from '../services/ConversationService';
import SeConnecteService from '../services/SeConnecteService';
import UtilisateurService from '../services/UtilisateurService';

const authStore = useAuthStore();
const conversationStore = useConversationStore();

const conversations = ref([]);
const messageText = ref('');
const showNewConversationDialog = ref(false);
const connections = ref([]);
const isSendingMessage = ref(false);
const authorsCache = ref({});
const isLoading = ref(true);

// Utiliser currentConversation du store au lieu d'une variable locale
const currentConversation = computed(() => conversationStore.currentConversation);

const getOther = (seConnecte) => {
  const meId = authStore.user?.idUtilisateur || authStore.user?.id;
  const demandeur = seConnecte.demandeur || {};
  const destinataire = seConnecte.destinataire || {};
  if ((demandeur.idUtilisateur || demandeur.id) === meId) return destinataire;
  return demandeur;
};

const getUserId = () => {
  // Essayer d'abord le store
  let id = authStore.user?.idUtilisateur || authStore.user?.id;
  if (id) return id;
  
  // Sinon, extraire du token
  const token = authStore.token || localStorage.getItem('authToken');
  if (token) {
    const match = String(token).match(/^(?:demo|token)_(\d+)$/);
    if (match) return parseInt(match[1]);
  }
  
  return undefined;
};

const openConversation = async (id) => {
  try {
    const conv = await ConversationService.getConversation(id);
    // Enrichir les messages avec les noms des auteurs
    await enrichConversationMessages(conv);
    conversationStore.currentConversation = conv;
    
    // Rejoindre la conversation via WebSocket
    const userId = getUserId();
    if (userId && conversationStore.wsConnected) {
      conversationStore.joinConversation(id, userId);
    }
  } catch (err) {
    console.error('Erreur chargement conversation:', err);
  }
};

const enrichConversationMessages = async (conv) => {
  if (!conv) return;
  
  // D'abord enrichir les IDs utilisateur1Id/utilisateur2Id
  if (conv.utilisateur1Id && !authorsCache.value[conv.utilisateur1Id]) {
    try {
      const user = await UtilisateurService.getUtilisateur(conv.utilisateur1Id);
      authorsCache.value[conv.utilisateur1Id] = user;
    } catch (err) {
      console.warn(`Failed to fetch user ${conv.utilisateur1Id}:`, err);
      authorsCache.value[conv.utilisateur1Id] = { prenom: 'User', nom: conv.utilisateur1Id, id: conv.utilisateur1Id };
    }
  }
  
  if (conv.utilisateur2Id && !authorsCache.value[conv.utilisateur2Id]) {
    try {
      const user = await UtilisateurService.getUtilisateur(conv.utilisateur2Id);
      authorsCache.value[conv.utilisateur2Id] = user;
    } catch (err) {
      console.warn(`Failed to fetch user ${conv.utilisateur2Id}:`, err);
      authorsCache.value[conv.utilisateur2Id] = { prenom: 'User', nom: conv.utilisateur2Id, id: conv.utilisateur2Id };
    }
  }
  
  // Puis enrichir les participants avec leurs infos complètes
  if (conv.participants && Array.isArray(conv.participants)) {
    const enrichedParticipants = [];
    for (const p of conv.participants) {
      let pId = typeof p === 'object' ? (p.idUtilisateur || p.id) : p;
      pId = typeof pId === 'string' ? parseInt(pId) : pId;
      
      if (!authorsCache.value[pId]) {
        try {
          const user = await UtilisateurService.getUtilisateur(pId);
          authorsCache.value[pId] = user;
        } catch (err) {
          console.warn(`Failed to fetch participant ${pId}:`, err);
          authorsCache.value[pId] = { prenom: 'User', nom: pId, idUtilisateur: pId, id: pId };
        }
      }
      enrichedParticipants.push(authorsCache.value[pId]);
    }
    conv.participants = enrichedParticipants;
  }
  
  // Puis enrichir les messages
  if (conv.messages && Array.isArray(conv.messages)) {
    for (const msg of conv.messages) {
      const authorId = msg.sender_id || msg.auteurId;
      const text = msg.text || msg.contenu;
      
      msg.auteurId = authorId;
      msg.contenu = text;
      
      if (authorId && !authorsCache.value[authorId]) {
        try {
          const user = await UtilisateurService.getUtilisateur(authorId);
          authorsCache.value[authorId] = user;
        } catch (err) {
          console.warn(`Failed to fetch user ${authorId}:`, err);
          authorsCache.value[authorId] = { prenom: 'User', nom: authorId };
        }
      }
      
      if (authorsCache.value[authorId]) {
        const user = authorsCache.value[authorId];
        msg.auteurName = `${user.prenom || ''} ${user.nom || ''}`.trim();
      }
    }
  }
};

const sendMessage = async () => {
  if (!messageText.value.trim() || !currentConversation.value) return;
  
  const meId = getUserId();
  isSendingMessage.value = true;
  try {
    // Essayer WebSocket d'abord si connecté
    if (conversationStore.wsConnected) {
      conversationStore.sendMessage(messageText.value, meId, currentConversation.value.id);
      messageText.value = '';
    } else {
      // Fallback HTTP
      const message = await ConversationService.sendMessage(currentConversation.value.id, {
        contenu: messageText.value,
        auteurId: meId,
      });
      currentConversation.value.messages.push(message);
      messageText.value = '';
    }
  } catch (err) {
    console.error('Erreur envoi message:', err);
  } finally {
    isSendingMessage.value = false;
  }
};

const getConversationTitle = (conv) => {
  if (!conv) return 'Conversation';
  
  const meId = getUserId();
  
  // Chercher les participants de plusieurs façons
  let otherUserId = null;
  
  // D'abord chercher dans utilisateur1Id/utilisateur2Id (format MongoDB)
  if (conv.utilisateur1Id && conv.utilisateur2Id) {
    otherUserId = conv.utilisateur1Id === meId ? conv.utilisateur2Id : conv.utilisateur1Id;
  } else {
    // Sinon chercher dans participants
    const participants = conv.participants || [];
    for (const p of participants) {
      const pId = typeof p === 'object' ? (p.idUtilisateur || p.id) : p;
      const pNum = typeof pId === 'string' ? parseInt(pId) : pId;
      
      if (pNum !== meId) {
        otherUserId = pNum;
        break;
      }
    }
  }
  
  // Si on a l'ID, chercher le nom
  if (otherUserId) {
    if (authorsCache.value[otherUserId]) {
      const user = authorsCache.value[otherUserId];
      return `${user.prenom || ''} ${user.nom || ''}`.trim();
    }
    return `User ${otherUserId}`;
  }
  
  return conv.titre || 'Conversation';
};

const getConversationSubtitle = (conv) => {
  if (!conv) return '';
  // Retourner une chaîne vide pour ne pas afficher de subtitle
  return '';
};

const getConversationAvatar = (conv) => {
  const title = conv.titre || getConversationTitle(conv) || '';
  return title.charAt(0).toUpperCase() || 'U';
};

const loadConnections = async () => {
  try {
    const meId = authStore.user?.idUtilisateur || authStore.user?.id;
    connections.value = await SeConnecteService.getAcceptedConnections(meId);
  } catch (err) {
    console.error('Erreur chargement connexions:', err);
  }
};

const selectConnectionForChat = async (user) => {
  showNewConversationDialog.value = false;
  try {
    const meId = getUserId();
    const otherUserId = user.idUtilisateur || user.id;
    
    console.log('[selectConnectionForChat] meId:', meId, 'otherUserId:', otherUserId);
    
    // Check if conversation already exists with this user
    const existing = conversations.value.find(conv => {
      // Chercher dans utilisateur1Id/utilisateur2Id (format MongoDB)
      if (conv.utilisateur1Id && conv.utilisateur2Id) {
        return (conv.utilisateur1Id === otherUserId || conv.utilisateur2Id === otherUserId);
      }
      
      // Fallback sur participants
      const participants = conv.participants || [];
      const otherIds = participants
        .filter(p => {
          const pId = typeof p === 'object' ? (p.idUtilisateur || p.id) : p;
          return pId !== meId;
        })
        .map(p => typeof p === 'object' ? (p.idUtilisateur || p.id) : p);
      return otherIds.includes(otherUserId);
    });
    
    console.log('[selectConnectionForChat] existing conversation:', existing);
    
    if (existing) {
      console.log('[selectConnectionForChat] Opening existing conversation');
      await openConversation(existing.id);
    } else {
      console.log('[selectConnectionForChat] Creating new conversation');
      // Create new conversation
      const newConv = await ConversationService.createConversation({
        utilisateur1Id: meId,
        utilisateur2Id: otherUserId,
      });
      await enrichConversationMessages(newConv);
      conversations.value.push(newConv);
      await openConversation(newConv.id);
    }
  } catch (err) {
    console.error('Erreur création conversation:', err);
  }
};

onMounted(async () => {
  try {
    isLoading.value = true;
    
    // Récupérer l'ID utilisateur de manière robuste
    const meId = getUserId();
    console.log('[MessagesPage] Loading conversations for user:', meId);
    console.log('[MessagesPage] authStore.user:', authStore.user);
    
    if (!meId) {
      console.error('[MessagesPage] Could not determine user ID');
      throw new Error('User ID not found');
    }

    // Initialiser WebSocket
    console.log('[MessagesPage] Initializing WebSocket...');
    await conversationStore.initWebSocket();
    
    const convs = await ConversationService.getConversationsByParticipant(meId);
    console.log('[MessagesPage] Raw conversations from backend:', convs);
    
    if (!convs || convs.length === 0) {
      console.warn('[MessagesPage] No conversations found for user', meId);
    } else {
      // Enrichir chaque conversation chargée
      for (const conv of convs) {
        console.log('[MessagesPage] Enriching conversation:', conv);
        await enrichConversationMessages(conv);
      }
    }
    
    conversations.value = convs || [];
    console.log('[MessagesPage] Conversations loaded and enriched:', conversations.value);
    
    await loadConnections();
  } catch (err) {
    console.error('[MessagesPage] Error loading:', err);
  } finally {
    isLoading.value = false;
  }
});

onUnmounted(() => {
  // Déconnecter WebSocket quand on quitte la page
  conversationStore.disconnectWebSocket();
});
</script>

<style scoped>
.border-bottom {
  border-bottom: 1px solid #e0e0e0;
}

.border-top {
  border-top: 1px solid #e0e0e0;
}
</style>
