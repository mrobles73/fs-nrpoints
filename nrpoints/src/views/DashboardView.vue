<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue';
import { useAuthStore } from '@/store/index';
import Standings from '@/components/Standings.vue';
import PastSeasons from '@/components/PastSeasons.vue';

const store = useAuthStore();

onMounted(async () => {

})

</script>

<template>
    <div class="container pt-nav">
        <div class="px-md-4">
            <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                <h1 class="h2">Dashboard</h1>
            </div>

            <div class="pb-3 mb-5 ">
                <Standings />
            </div>
            
            <PastSeasons />

            
            
            
        </div>

    </div>
    
</template>
