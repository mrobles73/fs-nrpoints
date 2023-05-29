<script lang="ts" setup>
import axios from 'axios';
import { computed, onMounted, ref } from 'vue';
import { useAuthStore } from '@/store/index';
import UploadRaces from '@/components/UploadRaces.vue';
import Standings from '@/components/Standings.vue';


const message = ref('');
const store = useAuthStore();
const auth = computed(() => store.auth);

// onMounted(async () => {
    
//     try {
//         if(auth.value) {
//             const response = await axios.get('users/user');
//             message.value = `Hi ${response.data.username}`;
//             store.setAuth(true);
//         } else {
//             message.value = 'You are not logged in';
//         }
//     } catch(e) {
//         store.setAuth(false);
//         message.value = 'You are not logged in';
//     }
    
// })


</script>

<template>
    <div class="container pt-nav text-center">
        <div class="w-100 pb-footer">
            <Standings/>
        </div>
    </div>
    
</template>
