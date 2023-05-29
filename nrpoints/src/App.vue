<script setup lang="ts">
import NavBar from '@/components/NavBar.vue';
import BNav from '@/components/BNav.vue';
import CFooter from './components/CFooter.vue';
import { RouterLink, RouterView } from 'vue-router';
import CookieRequest from './components/CookieRequest.vue';




</script>

<template>
  <BNav />
  <main class="mb-footer">
    <RouterView />
  </main>
    
  <CookieRequest />
  <CFooter />

</template>
