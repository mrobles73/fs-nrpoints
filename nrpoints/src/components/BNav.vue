<script lang="ts" setup>

import { RouterLink } from 'vue-router';
import { computed, onMounted, ref } from 'vue';
import { useAuthStore } from '@/store/index';

const store = useAuthStore();
const auth = computed(() => store.auth);
const user = computed(() => store.user);

const navigation = [
  { name: 'Home', href: '/', current: true },  
  { name: 'About', href: '/about', current: false },
]

async function logout() {
  console.log('logging out');
  await store.logout();
}

</script>

<template>
  <header>

    <nav class="navbar navbar-expand-md navbar-dark fixed-top bg-dark">
      <div class="container">
        <a href="/" class="d-flex align-items-center my-2 my-lg-0 me-lg-auto text-white text-decoration-none">
            <span class="fs-4"><i class="bi bi-car-front-fill"></i></span>
          </a>
        <button class="navbar-toggler collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#navbarCollapse" aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="navbar-collapse collapse" id="navbarCollapse" style="">
          <ul class="navbar-nav nav-pills me-auto mb-2 mb-md-0 ms-md-3">
            <li v-for="item in navigation" :key="item.name"><RouterLink :to="item.href" class="nav-link px-2" >{{ item.name }}</RouterLink></li>
            <li v-if="auth" key="Dashboard"><RouterLink to="/dashboard" class="nav-link px-2">Dashboard</RouterLink></li>
          </ul>
          <RouterLink v-if="!auth" to="/login" class="btn btn-link mb-md-0 mb-2 text-light text-decoration-none">Login</RouterLink>
          <RouterLink v-if="!auth" to="/register" class="btn btn-outline-light mb-md-0 mb-2">Sign up</RouterLink>
          <div class="dropdown" v-if="auth">
            <a href="#" class="d-block link-light text-decoration-none dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">
              <img :src="'/avatars/'+user.avatar" alt="mdo" width="32" height="32" class="rounded-circle">
            </a>
            <ul class="dropdown-menu dropdown-menu-left dropdown-menu-dark text-small" data-bs-theme="dark" style="">
              <li><div class="dropdown-item">Signed in as <strong>{{ user.username }}</strong></div></li>
              <li><hr class="dropdown-divider"></li>
              <!-- <li><a class="dropdown-item disabled" href="#">Settings</a></li> -->
              <li><a class="dropdown-item" href="/profile">Profile</a></li>
              <li><hr class="dropdown-divider"></li>
              <li><a class="dropdown-item" href="/" @click="logout">Sign out</a></li>
            </ul>
          </div>
        </div>
      </div>
    </nav>
  </header>

</template>