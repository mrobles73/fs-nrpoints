import HomeView from '@/views/HomeView.vue'
import DashboardView from '@/views/DashboardView.vue';
import SeasonView from '@/views/SeasonView.vue';
import LoginView from '@/views/LoginView.vue'
import AboutView from '@/views/AboutView.vue';
import RegisterView from '@/views/RegisterView.vue';
import ForgotView from '@/views/ForgotView.vue';
import ResetPasswordView from '@/views/ResetPasswordView.vue';
import ProfileView from '@/views/ProfileView.vue';
import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/store/index';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  linkExactActiveClass: 'active',
  routes: [
    {
      path: '/',
      name: 'Home',
      component: HomeView,
      meta: {
        title: 'nrpoints'
      }
    },
    {
      path: '/login',
      name: 'Login',
      component: LoginView,
      meta: {
        title: 'Sign in to nrpoints'
      }
    },
    {
      path: '/dashboard',
      name: 'Dashboard',
      component: DashboardView,
      meta: {
        title: 'Dashboard'
      }
    },
    {
      path: '/season/:id',
      name: 'Season',
      component: SeasonView,
      meta: {
        title: 'Season'
      }
    },
    {
      path: '/about',
      name: 'About',
      component: AboutView,
      meta: {
        title: 'About'
      }
    },
    {
      path: '/register',
      name: 'Register',
      component: RegisterView,
      meta: {
        title: 'Join nrpoints'
      }
    },
    {
      path: '/forgot',
      name: 'Forgot',
      component: ForgotView,
      meta: {
        title: 'Forgot password'
      }
    },
    {
      path: '/reset/:token',
      name: 'Reset',
      component: ResetPasswordView,
      meta: {
        title: 'Reset password'
      }
    },
    {
      path: '/profile',
      name: 'Profile',
      component: ProfileView,
      meta: {
        title: 'Your Profile'
      }
    },
  ]
});

router.beforeEach(async (to) => {

  const title = to.meta.title;
  if(title) {
    document.title = title as string;
  } 

  const publicPages = ['/', '/login', '/register', '/forgot', '/about'];
  const authRequired = !publicPages.includes(to.path) && !to.path.startsWith('/reset/');
  const authStore = useAuthStore();

  if(authRequired && !authStore.auth) {
    return '/login';
  }
})

export default router
