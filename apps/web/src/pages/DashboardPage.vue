<template>
  <q-page class="dashboard-page">
    <div class="row q-col-gutter-lg">
      <!-- Sidebar -->
      <div class="profile-summary col-12 col-md-3">
        <q-card class="q-pa-md">
          <div class="text-center q-mb-md">
            <q-avatar size="80px" color="primary" text-color="white">
              {{ authStore.user?.prenom?.charAt(0) ?? 'U' }}
            </q-avatar>
          </div>
          <div class="text-center q-mb-lg">
            <h6 class="q-ma-none">{{ authStore.user?.prenom }} {{ authStore.user?.nom }}</h6>
            <p class="text-grey q-ma-xs text-caption">{{ authStore.user?.email }}</p>
          </div>

          <q-list bordered separator>
            <q-item clickable to="/">
              <q-item-section avatar>
                <q-icon name="home" />
              </q-item-section>
              <q-item-section>Accueil</q-item-section>
            </q-item>
            <q-item clickable :to="`/profile/${authStore.user?.idUtilisateur ?? authStore.user?.id}`">
              <q-item-section avatar>
                <q-icon name="person" />
              </q-item-section>
              <q-item-section>Mon profil</q-item-section>
            </q-item>
            <q-item clickable to="/connections">
              <q-item-section avatar>
                <q-icon name="people" />
              </q-item-section>
              <q-item-section>Connexions</q-item-section>
            </q-item>
            <q-item clickable to="/messages">
              <q-item-section avatar>
                <q-icon name="mail" />
              </q-item-section>
              <q-item-section>Messages</q-item-section>
            </q-item>
            <q-item clickable to="/notifications">
              <q-item-section avatar>
                <q-icon name="notifications" />
              </q-item-section>
              <q-item-section>Notifications</q-item-section>
              <q-item-section side v-if="unreadCount > 0">
                <q-badge color="negative">{{ unreadCount }}</q-badge>
              </q-item-section>
            </q-item>
            <q-item clickable @click="authStore.logout">
              <q-item-section avatar>
                <q-icon name="exit_to_app" />
              </q-item-section>
              <q-item-section>Déconnexion</q-item-section>
            </q-item>
          </q-list>
        </q-card>
      </div>

      <!-- Main Feed -->
      <div class="feed-column col-12 col-md-8">
        <!-- Create Post -->
        <q-card class="q-pa-md q-mb-md">
          <div class="row items-center q-gutter-md">
            <q-avatar size="40px" color="primary" text-color="white">
              {{ authStore.user?.prenom?.charAt(0) ?? 'U' }}
            </q-avatar>
            <q-input
              v-model="newPostContent"
              filled
              dense
              class="col"
              placeholder="Quoi de neuf?"
              @keyup.enter="createPost"
            />
            <q-btn icon="send" round color="primary" @click="createPost" :loading="isCreatingPost" />
          </div>
        </q-card>

        <!-- Posts Feed -->
        <q-card v-if="postStore.isLoading" class="text-center q-pa-lg">
          <q-spinner color="primary" size="50px" />
        </q-card>

        <q-card
          v-for="post in postStore.posts"
          :key="post.id"
          class="q-mb-md q-pa-md"
        >
          <q-card-section>
            <div class="row items-center q-gutter-sm">
              <q-avatar size="40px" color="primary" text-color="white">
                {{ (authors[post.auteurId]?.prenom?.charAt(0) || authors[post.auteurId]?.nom?.charAt(0) || 'U') }}
              </q-avatar>
              <div>
                <div class="text-weight-bold">{{ authors[post.auteurId]?.prenom }} {{ authors[post.auteurId]?.nom }}</div>
                <div class="text-caption text-grey">{{ formatDate(post.dateCreation) }}</div>
              </div>
            </div>
          </q-card-section>

          <q-card-section class="q-py-md">
            <p class="q-ma-none">{{ post.contenu }}</p>
          </q-card-section>

          <q-card-section class="q-py-sm">
            <div class="row items-center q-gutter-lg text-grey">
              <div 
                class="row items-center q-gutter-xs cursor-pointer hover" 
                @click="toggleLike(post.id)"
                :class="{ 'text-primary': likedPosts.has(post.id) }"
              >
                <q-icon :name="likedPosts.has(post.id) ? 'thumb_up' : 'thumb_up_off_alt'" />
                <span>{{ (Array.isArray(post.likes) ? post.likes.length : 0) }}</span>
              </div>
              <div class="row items-center q-gutter-xs">
                <q-icon name="comment" />
                <span>{{ post.comments?.length ?? 0 }}</span>
              </div>
            </div>
          </q-card-section>

          <!-- Comments -->
          <q-separator />
          <q-card-section class="q-pa-md bg-grey-1">
            <div v-if="post.comments && post.comments.length > 0" class="q-mb-md">
              <div class="text-subtitle2 q-mb-md">Commentaires ({{ post.comments.length }})</div>
              <div
                v-for="comment in post.comments"
                :key="comment.id"
                class="q-mb-md q-pa-sm bg-white rounded-borders"
              >
                <div class="row items-start q-gutter-sm">
                  <q-avatar 
                    size="32px" 
                    color="primary" 
                    text-color="white"
                    class="q-mt-xs"
                  >
                    {{ (authors[comment.auteurId || comment.user_id || comment.userId]?.prenom?.charAt(0) || authors[comment.auteurId || comment.user_id || comment.userId]?.nom?.charAt(0) || 'U') }}
                  </q-avatar>
                  <div class="col">
                    <div class="text-weight-bold text-caption">
                      {{ authors[comment.auteurId || comment.user_id || comment.userId]?.prenom || '' }} {{ authors[comment.auteurId || comment.user_id || comment.userId]?.nom || 'Utilisateur inconnu' }}
                    </div>
                    <p class="q-ma-xs text-caption q-pt-xs">
                      {{ comment.text || comment.contenu || comment.content || '(Commentaire vide)' }}
                    </p>
                    <div class="text-grey text-caption">{{ formatDate(comment.createdAt || comment.created_at || comment.dateCreation) }}</div>
                    <!-- Edit/Delete buttons (only for comment author) -->
                    <div v-if="isCommentAuthor(comment)" class="q-mt-xs row items-center q-gutter-sm">
                      <q-btn 
                        icon="edit" 
                        flat 
                        dense 
                        size="sm"
                        color="primary"
                        @click="startEditingComment(post.id, comment)"
                        title="Éditer le commentaire"
                      />
                      <q-btn 
                        icon="delete" 
                        flat 
                        dense 
                        size="sm"
                        color="negative"
                        @click="deleteComment(post.id, comment.id)"
                        title="Supprimer le commentaire"
                      />
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div class="text-subtitle2 q-mb-md">Ajouter un commentaire</div>
            <div class="row items-center q-gutter-xs">
              <q-avatar size="32px" color="primary" text-color="white">
                {{ authStore.user?.prenom?.charAt(0) ?? 'U' }}
              </q-avatar>
              <q-input
                v-model="postComments[post.id]"
                dense
                outlined
                placeholder="Ajouter un commentaire..."
                class="col"
                @keyup.enter="addComment(post.id)"
              />
              <q-btn 
                icon="send" 
                round 
                dense 
                flat
                color="primary"
                @click="addComment(post.id)"
                :disable="!postComments[post.id]?.trim()"
              />
            </div>
          </q-card-section>
        </q-card>

        <q-card v-if="postStore.posts.length === 0 && !postStore.isLoading" class="text-center q-pa-lg">
          <p class="text-grey">Aucun post pour le moment</p>
        </q-card>
      </div>

      <!-- Right Sidebar - Suggestions -->
      <div class="suggestions-column col-12 col-md-4">
        <q-card class="q-pa-md">
          <h6 class="q-ma-none q-mb-md">Suggestions</h6>
          <q-list bordered separator>
            <q-item 
              v-for="s in suggestions" 
              :key="s.idUtilisateur" 
              clickable
              @click="router.push(`/profile/${s.idUtilisateur || s.id}`)"
            >
              <q-item-section avatar>
                <q-avatar color="primary" text-color="white">{{ s.prenom?.charAt(0) || s.nom?.charAt(0) || 'U' }}</q-avatar>
              </q-item-section>
              <q-item-section>
                <q-item-label>{{ s.prenom }} {{ s.nom }}</q-item-label>
                <q-item-label caption>À suivre</q-item-label>
              </q-item-section>
            </q-item>
          </q-list>
        </q-card>
      </div>
    </div>

    <!-- Edit Comment Modal -->
    <q-dialog v-model="editingCommentDialog" @hide="resetEditingComment">
      <q-card style="min-width: 400px">
        <q-card-section class="row items-center q-pb-none">
          <div class="text-h6">Éditer le commentaire</div>
          <q-space />
          <q-btn icon="close" flat round dense v-close-popup />
        </q-card-section>
        <q-card-section>
          <q-input
            v-model="editingCommentText"
            filled
            type="textarea"
            rows="3"
            label="Commentaire"
            @keyup.enter="saveEditedComment"
          />
        </q-card-section>
        <q-card-actions align="right">
          <q-btn flat label="Annuler" v-close-popup color="primary" />
          <q-btn 
            label="Enregistrer" 
            color="primary"
            @click="saveEditedComment"
            :loading="isUpdatingComment"
          />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/authStore';
import { usePostStore } from '../stores/postStore';
import { useNotificationStore } from '../stores/notificationStore';
import PostService from '../services/PostService';
import UtilisateurService from '../services/UtilisateurService';
import SeConnecteService from '../services/SeConnecteService';

const authStore = useAuthStore();
const postStore = usePostStore();
const notificationStore = useNotificationStore();
const router = useRouter();

const newPostContent = ref('');
const isCreatingPost = ref(false);
const postComments = ref({});
const authors = ref({});
const suggestions = ref([]);
const likedPosts = ref(new Set());
const editingCommentDialog = ref(false);
const editingCommentData = ref(null);
const editingCommentText = ref('');
const isUpdatingComment = ref(false);

// Computed property for unread notification count
const unreadCount = computed(() => notificationStore.unreadCount);

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
};

const loadPostsAndAuthors = async () => {
  // Récupérer l'ID de l'utilisateur connecté
  const currentUserId = authStore.user?.idUtilisateur || authStore.user?.id;
  await postStore.fetchPosts(currentUserId);
  
  // Charger les likes actuels de l'utilisateur
  likedPosts.value = new Set();
  for (const post of postStore.posts) {
    if (post.likes && Array.isArray(post.likes) && post.likes.includes(currentUserId)) {
      likedPosts.value.add(post.id);
    }
  }
  
  // prefetch authors for posts and comments
  for (const post of postStore.posts) {
    const aid = post.auteurId || post.authorId || post.auteur?.idUtilisateur;
    if (aid && !authors.value[aid]) {
      try {
        const u = await UtilisateurService.getUtilisateur(aid);
        authors.value[aid] = u;
      } catch (e) {
        console.warn('Author fetch failed for', aid, e);
      }
    }

    if (post.comments && Array.isArray(post.comments)) {
      for (const c of post.comments) {
        // Essayer plusieurs noms de champs possibles pour l'auteur
        const cid = c.user_id || c.userId || c.auteurId || c.auteur?.idUtilisateur || c.idAuteur;
        
        if (cid && !authors.value[cid]) {
          try {
            const cu = await UtilisateurService.getUtilisateur(cid);
            authors.value[cid] = cu;
          } catch (e) {
            console.warn('Comment author fetch failed for', cid, e);
          }
        }
      }
    }
  }

  // suggestions: fetch users not connected with current user
  try {
    const meId = authStore.user?.idUtilisateur || authStore.user?.id;
    
    // Get all users
    const all = await UtilisateurService.getAllUtilisateurs();
    
    // Get all connections (any status) with current user
    let connectedUserIds = new Set();
    try {
      const connections = await SeConnecteService.getAllConnections(meId);
      if (connections && Array.isArray(connections)) {
        // Each connection has demandeur and destinataire, get the other user's ID
        connections.forEach(conn => {
          const demandeurId = conn.demandeur?.idUtilisateur || conn.demandeur?.id;
          const destinataireId = conn.destinataire?.idUtilisateur || conn.destinataire?.id;
          
          if (demandeurId === meId) {
            connectedUserIds.add(destinataireId);
          } else {
            connectedUserIds.add(demandeurId);
          }
        });
      }
    } catch (e) {
      console.warn('Failed to fetch connections', e);
    }

    
    // Filter: exclude current user and already connected users
    suggestions.value = (all || [])
      .filter(u => {
        const uId = u.idUtilisateur || u.id;
        return uId !== meId && !connectedUserIds.has(uId);
      })
      .slice(0, 5);
  } catch (e) {
    console.warn('Failed to fetch suggestions', e);
  }
};

// Ensure we load the feed only after the authenticated user is available.
onMounted(() => {
  const currentUserId = authStore.user?.idUtilisateur || authStore.user?.id;
  if (currentUserId) {
    loadPostsAndAuthors().catch(err => console.error(err));
    notificationStore.fetchNotificationsByUser(currentUserId).catch(err => console.error(err));
  }
});

// If user is set asynchronously (login after mount), react and load feed then.
watch(() => authStore.user, (newUser) => {
  const uid = newUser?.idUtilisateur || newUser?.id;
  if (uid) {
    loadPostsAndAuthors().catch(err => console.error(err));
    notificationStore.fetchNotificationsByUser(uid).catch(err => console.error(err));
  }
});

const createPost = async () => {
  if (!newPostContent.value.trim() || !authStore.user) return;

  isCreatingPost.value = true;
  try {
    const userId = authStore.user?.idUtilisateur || authStore.user?.id;
    await postStore.addPost(newPostContent.value, userId);
    if (userId && !authors.value[userId]) {
      try {
        const u = await UtilisateurService.getUtilisateur(userId);
        authors.value[userId] = u;
      } catch { /* ignore */ }
    }
    newPostContent.value = '';
  } catch (error) {
    console.error('Erreur lors de la création du post:', error);
  } finally {
    isCreatingPost.value = false;
  }
};

const toggleLike = async (postId) => {
  try {
    const userId = authStore.user?.idUtilisateur || authStore.user?.id;
    const hadLike = likedPosts.value.has(postId);
    
    console.log(`[toggleLike] postId=${postId}, userId=${userId}, currentlyLiked=${hadLike}`);
    
    if (hadLike) {
      // Unlike
      console.log(`[toggleLike] Calling unlikePost...`);
      await PostService.unlikePost(postId, userId);
      console.log(`[toggleLike] unlikePost done, refreshing...`);
    } else {
      // Like
      console.log(`[toggleLike] Calling likePost...`);
      await PostService.likePost(postId, userId);
      console.log(`[toggleLike] likePost done, refreshing...`);
    }
    
    // Refresh posts to update like count and state
    await loadPostsAndAuthors();
    
    // Log final state
    const stillLiked = likedPosts.value.has(postId);
    console.log(`[toggleLike] After refresh - postId=${postId}, likedPosts.has(postId)=${stillLiked}`);
  } catch (error) {
    console.error('Erreur lors du like:', error);
  }
};

const addComment = async (postId) => {
  if (!postComments.value[postId]?.trim() || !authStore.user) return;

  try {
    const userId = authStore.user?.idUtilisateur || authStore.user?.id;
    await PostService.addComment(postId, {
      contenu: postComments.value[postId],
      auteurId: userId,
    });
    postComments.value[postId] = '';
    // Refresh posts to show new comment
    await loadPostsAndAuthors();
  } catch (error) {
    console.error("Erreur lors de l'ajout du commentaire:", error);
  }
};

const isCommentAuthor = (comment) => {
  const userId = authStore.user?.idUtilisateur || authStore.user?.id;
  const commentAuthorId = comment.auteurId || comment.user_id || comment.userId;
  return userId === commentAuthorId;
};

const startEditingComment = (postId, comment) => {
  editingCommentData.value = { postId, comment };
  editingCommentText.value = comment.text || comment.contenu || '';
  editingCommentDialog.value = true;
};

const saveEditedComment = async () => {
  if (!editingCommentText.value?.trim() || !editingCommentData.value) return;

  isUpdatingComment.value = true;
  try {
    const { postId, comment } = editingCommentData.value;
    await PostService.updateComment(postId, comment.id, {
      contenu: editingCommentText.value,
    });
    editingCommentDialog.value = false;
    // Refresh posts to show updated comment
    await loadPostsAndAuthors();
  } catch (error) {
    console.error('Erreur lors de la mise à jour du commentaire:', error);
  } finally {
    isUpdatingComment.value = false;
  }
};

const deleteComment = async (postId, commentId) => {
  try {
    // Confirmation dialog
    const response = await new Promise((resolve) => {
      // Simple confirm - you can use q-dialog for better UX
      if (confirm('Êtes-vous sûr de vouloir supprimer ce commentaire?')) {
        resolve(true);
      } else {
        resolve(false);
      }
    });

    if (!response) return;

    await PostService.deleteComment(postId, commentId);
    // Refresh posts to remove deleted comment
    await loadPostsAndAuthors();
  } catch (error) {
    console.error('Erreur lors de la suppression du commentaire:', error);
  }
};

const resetEditingComment = () => {
  editingCommentData.value = null;
  editingCommentText.value = '';
};
</script>
