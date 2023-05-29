<script lang="ts" setup>
import { reactive, useAttrs } from 'vue';
import { useAuthStore } from '@/store/index';

const user = reactive({
    email: '',
    password: ''
})

const store = useAuthStore();

async function onSubmit() {
    document.querySelector('.needs-validation')?.classList.add('was-validated');
    document.querySelector('#invalidCredentialsAlert')?.classList.add('d-none');
    if(user.email.trim() != '' && user.password.trim() != '' && user.password.trim().length > 7) {
      //need to validate the email
      try {
        await store.login(user);
        document.querySelector('#confirmpassword')?.classList.add('d-none');
      } catch (err: any) {
        document.querySelector('.needs-validation')?.classList.remove('was-validated');        
        if(err.message === 'Invalid Credentials') {
          document.querySelector('#invalidCredentialsAlert')!.innerHTML = 'Incorrect email or password.';
        } else {
          document.querySelector('#invalidCredentialsAlert')!.innerHTML = 'There was an error logging in, please try again.';
        }
        document.querySelector('#invalidCredentialsAlert')?.classList.remove('d-none');        
      }
    } 
} 
</script>

<template>
      <div class="form-signin w-100 m-auto pt-nav">
        <form data-bitwarden-watching="1" @submit.prevent="onSubmit" class="needs-validation" novalidate >
          <div class="text-center">
            <span class="fs-1 mb-4"><i class="bi bi-car-front-fill"></i></span>
            <h1 class="h3 mb-3 fw-normal">Sign in</h1>
          </div>

          <div class="alert alert-danger d-none" role="alert" id="invalidCredentialsAlert">
            Incorrect email or password.
          </div>
          
          <div class="mb-3">
            <label for="email" class="form-label">Email address</label>
            <input type="email" class="form-control" placeholder="name@example.com" id="email" v-model="user.email" autocomplete="on" required>
            <div class="invalid-feedback">Please provide a valid email</div>
          </div>
          <div class="mb-3">
            <label for="inputPassword" class="form-label">Password</label>
            <RouterLink to="/forgot" class="float-end fs-small">Forgot password?</RouterLink>
            <input type="password" class="form-control" id="inputPassword" placeholder="Password" v-model="user.password" autocomplete="on" required minlength="8">
            <div class="invalid-feedback">Please provide a valid password (min 8 characters)</div>
          </div>
          
          <button class="w-100 btn btn-primary" type="submit">Sign in</button>
          <p class="mt-4 mb-3 text-body-secondary text-center">New to nrpoints? <RouterLink to='/register'>Create an account</RouterLink>.</p>
        </form>
      </div>
    
</template>

<style>
  .form-signin {
  max-width: 330px;
  padding: 15px;
}

 .fs-small {
  font-size: .875rem;
 }
</style>