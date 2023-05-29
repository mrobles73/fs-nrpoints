<script lang="ts" setup>
import axios, { type AxiosResponse } from 'axios';
import { useAuthStore } from '@/store/index';
import { reactive, ref } from 'vue';
import { useToast, POSITION } from "vue-toastification";
import { validatePassword } from '@/lib/fileutils';
import AvatarsModal from '@/components/AvatarsModal.vue';

const toast = useToast();
const store = useAuthStore();
const user = store.user;
const namereg = /(?=^.{3,15}$)(?!.*[_-]{2,})[^<>[\]{}|\\\/^~%# :;,$%?\0-\cZ]+$/;

const profiledata = reactive({
    username: user.username,
    avatar: user.avatar,
    oldPassword: '',
    newPassword: '',
    confirmNewPassword: ''
});

const sidebar = reactive({
    selectedProfile: true,
    selectedEmail: false,
    selectedPassword: false,
    curActive: "#profilePill"
})

const updateSidebar = (event: any) => {
    profiledata.oldPassword = '';
    profiledata.newPassword = '';
    profiledata.confirmNewPassword = '';  
    profiledata.username = user.username;
    document.querySelector(sidebar.curActive)?.classList.remove('active');
    event.target.classList.add('active');
    sidebar.curActive = "#"+event.target.id;
    
}

const updateUsername = async () => {
    if(user.username === profiledata.username.trim() || !namereg.test(profiledata.username.trim())) {
        document.querySelector('#username')?.classList.add('is-invalid');
        return;
    } else {
        document.querySelector('#username')?.classList.remove('is-invalid');
        const updateUsernameBtn = document.querySelector("#updateUsernameBtn");
        updateUsernameBtn!.innerHTML = `<div class="spinner-border spinner-border-sm" role="status"><span class="visually-hidden">Loading...</span></div>`;        
        const response = await axios.post('users/updateUsername', { id: user.id, username: profiledata.username, email: user.email});
        updateUsernameBtn!.innerHTML = 'Update username';
        if(response.status === 200) {
            store.setUser(response.data);
            window.location.reload();
        } else {
            toast.error("Unable to update username at this time.", {
                timeout: 4000,
                position: POSITION.TOP_CENTER
            });
        }        
    }
}

const submitPasswordChange = async () => {
    const data = {
        email: user.email,
        oldPassword : profiledata.oldPassword.trim(),
        newPassword : profiledata.newPassword.trim(),
        confirmNewPassword : profiledata.confirmNewPassword.trim(),
    };
    document.querySelector('.needs-validation')?.classList.add('was-validated');
    if(data.oldPassword != '' && data.oldPassword.length > 7 && validatePassword(data.newPassword) && validatePassword(data.confirmNewPassword) ) {              
        if(data.newPassword != data.confirmNewPassword) {
            invalidPasswordDisplay('Provided passwords do not match');
        } else if(data.oldPassword === data.newPassword) {
            invalidPasswordDisplay('New password cannot be the same as old password');
        } else {
            const updatePasswordBtn = document.querySelector("#updatePasswordBtn");
            updatePasswordBtn!.innerHTML = `<div class="spinner-border spinner-border-sm" role="status"><span class="visually-hidden">Loading...</span></div>`;
            document.querySelector('#newpassword')?.classList.remove('is-invalid');
            document.querySelector('#oldpassword')?.classList.remove('is-invalid');
            document.querySelector('#confirmnewpassword')?.classList.remove('is-invalid');
            document.querySelector('.needs-validation')?.classList.remove('was-validated');      
            const response = await axios.post('auth/updatePassword', { email: data.email, oldPassword: data.oldPassword, newPassword: data.newPassword, confirmNewPassword: data.confirmNewPassword }) as AxiosResponse<any, any> | any;
            updatePasswordBtn!.innerHTML = `Update`;              
            if(response.status === 200) {                
                toast.success("Successfully reset password", {
                    timeout: 4000,
                    position: POSITION.TOP_CENTER
                });
                profiledata.oldPassword = '';
                profiledata.newPassword = '';
                profiledata.confirmNewPassword = '';  
            } else if(response.response.status === 403) {
                document.querySelector('#oldpassword')?.classList.add('is-invalid');
                document.querySelector('#oldpassinvalidfeedback')!.innerHTML = "Incorrect password";
            } else {
                toast.error("Unable to reset password.", {
                    timeout: 4000,
                    position: POSITION.TOP_CENTER
                });
            }            
        }
    } else if(!validatePassword(data.newPassword)) {
        invalidPasswordDisplay("Please provide a valid password (min 8 characters, one letter, one number, one special character)");
    }    
}


const invalidPasswordDisplay = (feedback:string) => {
    document.querySelector('#newpassword')?.classList.add('is-invalid');
    document.querySelector('#confirmnewpassword')?.classList.add('is-invalid');
    // document.querySelector('#confirmpassinvalidfeedback')!.innerHTML = feedback;
    document.querySelector('#newpassinvalidfeedback')!.innerHTML = feedback;
}

</script>

<template>

    <AvatarsModal :avatar="profiledata.avatar" />

    <div class="container pt-nav">

        <div class="row">
            <div class="col">
                <img :src="'/avatars/'+profiledata.avatar" alt="mdo" width="64" height="64" class="rounded-circle">
                <h4>{{ user.username }}</h4>
            </div>
        </div>

        <div class="row">
            <div class="col-11 col-lg-2">
                <ul class="nav nav-pills flex-lg-column flex-row mb-auto">
                    <li class="nav-item">
                        <a href="#" class="nav-link active" id="profilePill" @click="updateSidebar">
                            <i class="bi bi-person" style="pointer-events: none;"></i>
                            Profile
                        </a>
                    </li>
                    <!-- <li>
                        <a href="#" class="nav-link link-body-emphasis" id="emailPill" @click="updateSidebar">
                            <i class="bi bi-envelope" style="pointer-events: none;"></i>
                            Email
                        </a>
                    </li> -->
                    <li>
                        <a href="#" class="nav-link link-body-emphasis " id="passwordPill" @click="updateSidebar">
                            <i class="bi bi-shield-lock" style="pointer-events: none;"></i>
                            Password
                        </a>
                    </li>
                </ul>
                
            </div>
            <div v-if="sidebar.curActive === '#profilePill'" class="col-11 col-lg-9">
                <div class="border-bottom">
                    <h4>Profile</h4>
                </div>
                <div class="row mt-2">
                    <div class="col-sm-6">
                        <div class="mb-3">
                            <p class="mb-0">Email</p>
                            <h5>{{ user.email }}</h5>
                        </div>
                        <div class="mb-3">
                            <label for="username" class="form-label">Username</label>
                            <input type="text" id="username" placeholder="Username" class="form-control" v-model="profiledata.username">
                            <div class="invalid-feedback">Please provide a valid username. (3-15, no special characters)</div>                                 
                        </div>                   
                        <button type="button" class="btn btn-primary" @click="updateUsername" id="updateUsernameBtn">Update username</button>
                    </div>

                    <div class="col-sm-6">
                        <strong>Avatar</strong>
                        <a href="#" class="d-block link-light text-decoration-none mt-2">
                            <img :src="'/avatars/'+profiledata.avatar" alt="mdo" width="192" height="192" class="rounded-circle">
                        </a>
                        <button type="button" class="btn btn-primary btn-sm mt-3" data-bs-toggle="modal" data-bs-target="#avatarModal">Change</button>
                    </div>                    
                </div>                                
            </div>

            <!-- <div v-else-if="sidebar.curActive === '#emailPill'" class="col-9">
                <div class="border-bottom">
                    <h4>Email</h4>
                </div>
                <div  class="row mt-2">
                    <div class="col-sm-6">

                        <div class="mb-2">
                            <h5>{{ user.email }}</h5>
                        </div>
                        
                        
                        <div class="mb-2">
                            <label for="email" class="form-label">Change</label>
                            <input type="email" class="form-control" placeholder="name@example.com" id="email" v-model="profiledata.email">
                            <div class="invalid-feedback">Please provide a valid email</div>            
                        </div>
                    </div>                        
                </div>            
            </div> -->

            <div v-else-if="sidebar.curActive === '#passwordPill'" class="col-11 col-lg-9">
                <div class="border-bottom">
                    <h4>Update Password</h4>
                </div>
                <div  class="row mt-2">
                    <div class="col-sm-6">

                        <form data-bitwarden-watching="1" @submit.prevent="submitPasswordChange" class="needs-validation" novalidate>
                            <div class="mb-2">
                                <label for="oldpassword" class="form-label">Old password</label>
                                <input type="password" class="form-control" id="oldpassword" placeholder="Password" required minlength="8" v-model="profiledata.oldPassword">
                                <div class="invalid-feedback" id="oldpassinvalidfeedback">Please provide a valid password (min 8 characters)</div>
                            </div>
                            <div class="mb-2">
                                <label for="newpassword" class="form-label">New password</label>
                                <input type="password" class="form-control" id="newpassword" placeholder="Password" required minlength="8" v-model="profiledata.newPassword">
                                <div class="invalid-feedback" id="newpassinvalidfeedback">Please provide a valid password (min 8 characters, one letter, one number, one special character)</div>
                            </div>
                            <div class="mb-3">
                                <label for="confirmnewpassword" class="form-label">Confirm new password</label>
                                <input type="password" class="form-control" id="confirmnewpassword" placeholder="Password" required minlength="8" v-model="profiledata.confirmNewPassword">
                                <!-- <div class="invalid-feedback" id="confirmpassinvalidfeedback">Please provide a valid password (min 8 characters, one letter, one number, one special character)</div> -->
                            </div>
                            
                            <button class="btn btn-primary" type="submit" id="updatePasswordBtn">Update</button>
                            <RouterLink to="/forgot" class="btn btn-link text-decoration-none">Forgot my password</RouterLink>
                            
                        </form>
                    </div>                        
                </div>            
            </div>
                            

            <div class="col-1"></div>
        </div>
        

    </div>
    
</template>