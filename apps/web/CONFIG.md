# Configuration LinkDong Frontend

## Démarrage de l'application

```bash
# Assurez-vous que le backend est en cours d'exécution
# Backend: java spring-boot sur http://localhost:8080

# Frontend
cd c:\Users\axelp\Desktop\reseau-social\Frontend\linkdong
npm install  # Si première fois
npm run dev   # Lancer le serveur de développement
```

## URLs importantes

- **Frontend**: http://localhost:9000
- **Backend API**: http://localhost:8080/api
- **Swagger UI**: http://localhost:8080/swagger-ui.html

## Variables d'environnement

Le fichier `.env` contient:
```
VITE_API_URL=http://localhost:8080/api
```

## Structure du projet

```
linkdong/
├── src/
│   ├── pages/              # Pages Vue (Login, Register, Dashboard, etc.)
│   ├── layouts/            # Layouts (MainLayout)
│   ├── components/         # Composants réutilisables
│   ├── services/           # Services API (UtilisateurService, PostService, etc.)
│   ├── stores/             # Pinia stores (authStore, postStore, conversationStore)
│   ├── router/             # Configuration Vue Router
│   ├── css/                # Styles globaux
│   ├── App.vue             # Composant root
│   └── main.js             # Point d'entrée
├── public/                 # Fichiers statiques
├── .env                    # Configuration environnement
├── quasar.config.js        # Configuration Quasar
├── vite.config.js          # Configuration Vite
└── package.json            # Dépendances npm
```

## Commandes utiles

```bash
# Développement
npm run dev

# Build production
npm run build

# Linting
npm run lint

# Format code
npm run format
```

## Intégration Backend

L'application consomme l'API REST du backend Spring Boot:

### Endpoints principaux utilisés:

**Utilisateurs:**
- `GET /api/utilisateurs` - Liste tous les utilisateurs
- `POST /api/utilisateurs` - Créer un utilisateur
- `GET /api/utilisateurs/{id}` - Obtenir un utilisateur
- `PUT /api/utilisateurs/{id}` - Mettre à jour un utilisateur

**Posts (MongoDB):**
- `GET /api/posts` - Récupérer tous les posts
- `POST /api/posts` - Créer un post
- `POST /api/posts/{id}/like` - Liker un post
- `POST /api/posts/{id}/comments` - Ajouter un commentaire

**Conversations (MongoDB):**
- `GET /api/conversations` - Récupérer les conversations
- `POST /api/conversations` - Créer une conversation
- `POST /api/conversations/{id}/messages` - Envoyer un message

**Connexions:**
- `GET /api/se-connecte/{id}` - Récupérer les connexions
- `POST /api/se-connecte` - Envoyer une demande de connexion
- `PUT /api/se-connecte/{demandeurId}/{destinataireId}/accept` - Accepter
- `PUT /api/se-connecte/{demandeurId}/{destinataireId}/refuse` - Refuser

## État de développement

✅ Structure de base créée
✅ Services API intégrés (Axios)
✅ Pinia stores configurés
✅ Pages principales créées
✅ Layout principal avec navigation
✅ Authentification basique (localStorage)

⏳ À faire:
- Intégration JWT tokens
- Tests unitaires
- E2E tests
- Optimisations performances
- WebSocket pour messagerie temps réel
- Upload d'images
- Notifications temps réel

## Problèmes courants

### "Cannot GET /login"
- Le serveur de développement Vite n'est pas lancé
- Solution: `npm run dev`

### "API not responding"
- Le backend Spring Boot n'est pas lancé
- Solution: Vérifier que le backend tourne sur port 8080

### CORS errors
- Le backend doit avoir @CrossOrigin configuré
- Vérifiez les controllers (déjà configuré)

### Module not found
- Vérifier les imports (utiliser @/ pour src/)
- Vérifier les types TypeScript
