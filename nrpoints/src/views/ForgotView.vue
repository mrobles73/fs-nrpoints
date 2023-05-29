<script lang="ts" setup>

import axios from 'axios';
import { reactive, ref } from 'vue';

const email = ref('');
const notify = reactive({
    message: '',
    success: false
})

const onSubmit = async () => { //make sure full email is input
  const sendBtn = document.querySelector("#sendbtn");
    if(email.value.trim() != '' && /^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/.test(email.value.trim())) {   
      document.querySelector('#email')?.classList.remove('is-invalid');
      sendBtn!.innerHTML = `<div class="spinner-border spinner-border-sm" role="status"><span class="visually-hidden">Loading...</span></div>`;

      try {
          const response = await axios.post('auth/forgot', { email: email.value });
          if(response.status === 200) {
            notify.message = 'Email was sent, please check your email';
            notify.success = true;
          } else {
            notify.message = 'No user exists with that email';
            notify.success = false;
          }        
      } catch (e) {
          notify.message = 'No user exists with that email';
          notify.success = false;
      }
    } else {
      document.querySelector('#email')?.classList.add('is-invalid');
    }
    sendBtn!.innerHTML = `Send password reset email`;
    //document.querySelector('.needs-validation')?.classList.add('was-validated');
    
}

</script>

<template>
    <div class="form-signin w-100 m-auto pt-nav">

        <form data-bitwarden-watching="1" @submit.prevent="onSubmit" class="needs-validation" novalidate>
          <div class="text-center">
            <span class="fs-1 mb-4"><i class="bi bi-car-front-fill"></i></span>
            <h1 class="h3 mb-3 fw-normal">Reset password</h1>
          </div>

          <div v-if="notify.message" :class="[notify.success ? 'alert-success' : 'alert-danger' , 'alert' ]" role="alert">
            {{ notify.message }}
          </div>
          
          <div class="mb-3">
            <label for="email" class="form-label">Email address</label>
            <input type="email" class="form-control" placeholder="name@example.com" id="email" v-model="email" required>
            <div class="invalid-feedback">Please provide a valid email</div>            
          </div>
          
          <button class="w-100 btn btn-primary" type="submit" id="sendbtn">Send password reset email</button>
        </form>
      </div>



</template>