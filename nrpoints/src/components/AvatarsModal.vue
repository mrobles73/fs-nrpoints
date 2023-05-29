<script lang="ts" setup>
import axios from 'axios';
import { onMounted, reactive } from 'vue';
import { useAuthStore } from '@/store/index';
import { useToast, POSITION } from "vue-toastification";

const store = useAuthStore();
const user = store.user;
const toast = useToast();
//import bootstrap from 'bootstrap/dist/js/bootstrap.min.js';



const props = defineProps({
    avatar: {type:String, required:true}
})

const avatar = reactive({
    selected: '', //this should be the user data
    url: ''
})

const selectImage = (event: any) => {
    document.getElementById(avatar.selected)?.classList.remove('active');
    event.target.closest(".image-tile").classList.add('active');
    avatar.selected = event.target.closest(".image-tile").id;
    avatar.url = event.target.getAttribute("url");
}

const updateUsername = async () => {
    //console.log(avatar.url);
    const updateAvatarBtn = document.querySelector("#updateAvatarBtn");
    updateAvatarBtn!.innerHTML = `<div class="spinner-border spinner-border-sm" role="status"><span class="visually-hidden">Loading...</span></div>`;
    const response = await axios.post('users/updateAvatar', { id: user.id, avatar: avatar.url});
    updateAvatarBtn!.innerHTML = 'Update avatar';
    if(response.status === 200) {
        store.setUser(response.data);        
        window.location.reload();
    } else {
        toast.error("Unable to update avatar at this time.", {
            timeout: 4000,
            position: POSITION.TOP_CENTER
        });
    }  
}

const reset = () => {
    document.getElementById(avatar.selected)?.classList.remove('active');
    avatar.selected = props.avatar.replace(".png", "Avatar");
    avatar.url = props.avatar;
    document.getElementById(avatar.selected)?.classList.add('active');
}

onMounted( () => {
    avatar.selected = props.avatar.replace(".png", "Avatar");
    avatar.url = props.avatar;
    //console.log(avatar.selected);
    document.getElementById(avatar.selected)?.classList.add('active');
    //document.getElementById(avatar.selected).firstChild.checked = true;
});

</script>

<template>
    <div class="modal fade" id="avatarModal" tabindex="-1" aria-labelledby="avatarModal" data-bs-backdrop="static" aria-hidden="false" role="dialog">
        <div class="modal-dialog modal-dialog-centered" role="document">
            <div class="modal-content rounded-4 shadow">
            <div class="modal-header border-bottom-0">
                <h1 class="modal-title fs-5">Select Avatar</h1>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close" @click="reset"></button>
            </div>
            <div class="modal-body py-0">
                <div class="wall">
                    <label class="image-tile" id="blueAvatar">
                        <input type="radio" name="avatarSelect" class="image-radio-item" >
                        <img src="/avatars/blue.png" url="blue.png" alt="blue" class="rounded-circle" @click="selectImage">
                    </label>
                    <label class="image-tile" id="redAvatar">
                        <input type="radio" name="avatarSelect" class="image-radio-item">
                        <img src="/avatars/red.png" url="red.png" alt="red" class="rounded-circle" @click="selectImage">
                    </label>
                    <label class="image-tile" id="yellowAvatar">
                        <input type="radio" name="avatarSelect" class="image-radio-item">
                        <img src="/avatars/yellow.png" url="yellow.png" alt="yellow" class="rounded-circle" @click="selectImage">
                    </label>
                </div>

            </div>
            <div class="modal-footer d-flex justify-content-center  pb-3 border-top-0">
                <button type="button" class="btn btn-sm btn-primary py-2" id="updateAvatarBtn" @click="updateUsername">Update avatar</button>
            </div>
            </div>
        </div>
    </div>
</template>

<style>
.image-radio-item {
    position: absolute;
    clip: rect(0, 0, 0, 0);
}
.image-tile {
    width: 150px;
    height: 150px;
    margin:2px;
    padding: 2px;
    position: relative;
    cursor:pointer; 
    background-color: white; 
    background-position: center center;
    background-size: cover;
    box-shadow:inset 0 0 10px rgba(137, 208, 255, 0.471); 
    outline: 2px solid white;
    outline-offset: -3px;
    border:1px solid #9ec5fe; 
}

.image-tile img {
    width: 100%;
    height: 100%;
}
.active.image-tile {
    border: 5px solid var(--bs-primary) ;
}

</style>