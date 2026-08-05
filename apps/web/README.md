# LinkDong Frontend

Application Quasar/Vue.js 3 pour le réseau social LinkDong.

## Installation

```bash
npm install
# ou
yarn install
```

## Configuration

1. Copier `.env.example` en `.env`
2. Modifier l'URL de l'API si nécessaire :
   ```
   VITE_API_URL=http://localhost:8080/api
   ```

## Développement

```bash
quasar dev
```

L'application s'ouvrira à `http://localhost:9000`

## Build

```bash
quasar build
```

## Structure du projet

```
src/
├── pages/          # Pages de l'application
├── layouts/        # Layouts principaux
├── components/     # Composants Vue réutilisables
├── services/       # Services API (Axios)
├── stores/         # Stores Pinia (state management)
├── router/         # Configuration du routeur
└── css/           # Styles globaux
```

## Pages implémentées

- **LoginPage** - Connexion utilisateur
- **RegisterPage** - Inscription nouveau compte
- **DashboardPage** - Feed principal avec posts
- **ProfilePage** - Profil utilisateur
- **ConnectionsPage** - Gestion des connexions
- **MessagesPage** - Messagerie
- **NotificationsPage** - Notifications

## Services API

- **UtilisateurService** - Gestion des utilisateurs
- **PostService** - Gestion des posts et commentaires
- **ConversationService** - Gestion de la messagerie
- **ConnectionService** - Gestion des connexions
- **NotificationService** - Gestion des notifications

## States (Pinia)

- **authStore** - État d'authentification et utilisateur
- **postStore** - État des posts et likes
- **conversationStore** - État des conversations

## Features implémentées

✅ Authentification et inscription
✅ Feed avec posts et commentaires
✅ Like pour les posts
✅ Recherche utilisateurs
✅ Gestion des connexions
✅ Messagerie
✅ Notifications
✅ Interface responsive Quasar

## Dépendances principales

- Vue.js 3 (Composition API)
- Quasar Framework v2
- TypeScript
- Pinia v2 (state management)
- Axios (HTTP client)
- Vue Router (routing)
- SCSS (preprocesseur CSS)
- ESLint & Prettier

## Notes importantes

- L'authentification actuelle est basique (localStorage avec token simulé)
- À implémenter : JWT tokens avec backend
- À ajouter : WebSocket pour messagerie temps réel
- À ajouter : Upload d'images pour profils et posts
- Backend API doit être en cours d'exécution sur http://localhost:8080

### Start the app in development mode (hot-code reloading, error reporting, etc.)
```bash
quasar dev
```


### Lint the files
```bash
yarn lint
# or
npm run lint
```


### Format the files
```bash
yarn format
# or
npm run format
```


### Build the app for production
```bash
quasar build
```

### Customize the configuration
See [Configuring quasar.config.js](https://v2.quasar.dev/quasar-cli-vite/quasar-config-js).
