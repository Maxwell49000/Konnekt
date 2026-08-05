const routes = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      { path: '', redirect: '/dashboard' },
      { path: 'posts', component: () => import('pages/PostsPage.vue') },
      { path: 'profile/:id', component: () => import('pages/ProfilePage.vue') },
      { path: 'connections', component: () => import('pages/ConnectionsPage.vue') },
      { path: 'messages', component: () => import('pages/MessagesPage.vue') },
      { path: 'notifications', component: () => import('pages/NotificationsPage.vue') },
      { path: 'search', component: () => import('pages/SearchPage.vue') },
    ]
  },

  // Auth routes
  {
    path: '/login',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      { path: '', component: () => import('pages/LoginPage.vue') }
    ]
  },
  {
    path: '/register',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      { path: '', component: () => import('pages/RegisterPage.vue') }
    ]
  },

  {
    path: '/dashboard',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      { path: '', component: () => import('pages/DashboardPage.vue') }
    ]
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/ErrorNotFound.vue')
  }
]

export default routes
