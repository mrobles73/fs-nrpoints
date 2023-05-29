<script lang="ts" setup>

import { reactive, onMounted, ref } from 'vue';
import axios, { type AxiosResponse } from 'axios';
import { useRoute } from 'vue-router';
import router from "@/router";
import { useToast, POSITION } from "vue-toastification";
import { validatePassword } from '@/lib/fileutils';

const toast = useToast();

const resetData = reactive({
    password: '',
    confirmPassword: ''
});

const route = useRoute();

const onSubmit = async () => {
    document.querySelector('#errorAlert')?.classList.add('d-none');
    if(validatePassword(resetData.password.trim()) && validatePassword(resetData.confirmPassword.trim())  && resetData.password === resetData.confirmPassword) {
      document.querySelector('#confirmpassword')?.classList.remove('is-invalid');
      document.querySelector('#password')?.classList.remove('is-invalid');
      try {
          const response = await axios.post('auth/resetPassword', { 
              ...resetData,
              token: route.params.token
          }) as AxiosResponse<any, any> | any;      
          if(response.status === 200) {
            toast.success("Successfully reset password", {
                timeout: 4000,
                position: POSITION.TOP_CENTER
            });
            router.push('/login');
          } else if(response.response.status === 403) {
            errorAlert(response.response.data.message);
          } else {
            errorAlert('Error resetting password. If error persists, contact site owner.');
          }          
      } catch (e) {
        errorAlert('Error resetting password. If error persists, contact site owner.');
      }
    } else if(!validatePassword(resetData.password.trim())) {
      invalidDisplay('Please provide a valid password (min 8 characters, one letter, one number, one special character)');
    } else if(resetData.password != resetData.confirmPassword) {
      invalidDisplay('Provided passwords do not match');
    }
    document.querySelector('.needs-validation')?.classList.add('was-validated');
}

function errorAlert(message: string) {
  document.querySelector('#errorAlert')?.classList.remove('d-none');
  document.querySelector('#errorAlert')!.innerHTML = message;
}

function invalidDisplay(feedback: string) {
  document.querySelector('#confirmpassword')?.classList.add('is-invalid');
  document.querySelector('#password')?.classList.add('is-invalid');
  document.querySelectorAll('.invalid-feedback').forEach(feedbackElement => feedbackElement.innerHTML = feedback);
}

function invalidRedirect() {
  toast.error("Invalid token.", {
      timeout: 4000,
      position: POSITION.TOP_CENTER
  });
  router.push('/');
}

onMounted(async () => {
  try {
    const response = await axios.post('auth/resetPasswordValidate?token='+route.params.token);
    if(response.status != 200) {
      invalidRedirect();
    }
  } catch (error) {
    invalidRedirect();
  }      
});

</script>

<template>

<div class="form-signin w-100 m-auto pt-nav">
        <form data-bitwarden-watching="1" @submit.prevent="onSubmit" class="needs-validation" novalidate>
          <div class="text-center">
            <span class="fs-1 mb-4"><i class="bi bi-car-front-fill"></i></span>
            <h1 class="h3 mb-3 fw-normal">Reset your password</h1>
          </div>

          <div class="alert-danger alert d-none" id="errorAlert" role="alert">
          </div>

          <div class="mb-2">
            <label for="password" class="form-label">Password</label>
            <input type="password" class="form-control" id="password" placeholder="Password" v-model="resetData.password" required minlength="8">
            <div class="invalid-feedback">Please provide a valid password (min 8 characters, one letter, one number, one special character)</div>
          </div>
          <div class="mb-3">
            <label for="confirmpassword" class="form-label">Confirm password</label>
            <input type="password" class="form-control" id="confirmpassword" placeholder="Password" v-model="resetData.confirmPassword" required minlength="8">
            <!-- <div class="invalid-feedback" id="confirmpassinvalidfeedback">Please provide a valid password (min 8 characters, one letter, one number, one special character)</div> -->
          </div>
          
          <button class="w-100 btn btn-primary" type="submit">Submit</button>
        </form>
      </div>  
  
</template>