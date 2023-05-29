<script lang="ts" setup>
import { reactive } from 'vue';
//import { useRouter } from 'vue-router';
import { useAuthStore } from '@/store/index';
import { validatePassword, validateUsername } from '@/lib/fileutils';


const user = reactive({
    username: '',
    email: '',
    password: '',
    confirmPassword: ''
});

const store = useAuthStore();

const onSubmit = async () => { 
    document.querySelector('#registerFailedAlert')?.classList.add('d-none');   
    if(validateUsername(user.username.trim()) && user.email.trim() != '' && validatePassword(user.password.trim()) && validatePassword(user.confirmPassword.trim()) && user.password === user.confirmPassword) {
      document.querySelector('#password')?.classList.remove('is-invalid');
      document.querySelector('#confirmpassword')?.classList.remove('is-invalid');      
      try {
        await store.register(user);
      } catch (err: any) {
        document.querySelector('#registerFailedAlert')!.innerHTML = err.message;
        document.querySelector('#registerFailedAlert')?.classList.remove('d-none'); 
      }
    } else if(!validatePassword(user.password.trim())) {
      invalidPasswordDisplay("Please provide a valid password (min 8 characters, one letter, one number, one special character)");
    } else if(user.password != user.confirmPassword) {
      invalidPasswordDisplay("Provided passwords do not match");
    }     
    document.querySelector('.needs-validation')?.classList.add('was-validated');    
}

const invalidPasswordDisplay = (feedback:string) => {
    document.querySelector('#password')?.classList.add('is-invalid');
    document.querySelector('#confirmpassword')?.classList.add('is-invalid');
    document.querySelector('#passInvalidFeedback')!.innerHTML = feedback;
}

</script>

<template>
      <div class="form-signin w-100 m-auto pt-nav">
        <form data-bitwarden-watching="1" @submit.prevent="onSubmit" class="needs-validation" novalidate>
          <div class="text-center">
            <span class="fs-1 mb-4"><i class="bi bi-car-front-fill"></i></span>
            <h1 class="h3 mb-3 fw-normal">Create account</h1>
          </div>

          <div class="alert alert-danger d-none" role="alert" id="registerFailedAlert"></div>

          <div class="mb-2">
            <label for="username" class="form-label">Username</label>
            <input type="text" id="username" placeholder="Username" class="form-control" v-model="user.username" required minlength="3">
            <div class="invalid-feedback">Please provide a valid username (min 3 characters)</div>         
          </div>
          
          <div class="mb-2">
            <label for="email" class="form-label">Email address</label>
            <input type="email" class="form-control" placeholder="name@example.com" id="email" v-model="user.email" required>
            <div class="invalid-feedback">Please provide a valid email</div>            
          </div>
          <div class="mb-2">
            <label for="password" class="form-label">Password</label>
            <input type="password" class="form-control" id="password" placeholder="Password" v-model="user.password" required minlength="8">
            <div class="invalid-feedback" id="passInvalidFeedback">Please provide a valid password (min 8 characters, one letter, one number, one special character)</div>
          </div>
          <div class="mb-3">
            <label for="confirmpassword" class="form-label">Confirm password</label>
            <input type="password" class="form-control" id="confirmpassword" placeholder="Password" v-model="user.confirmPassword" required minlength="8" aria-describedby="confirmpassinvalidfeedback">
            <!-- <div class="invalid-feedback" id="confirmpassinvalidfeedback">Provided passwords do not match</div> -->
          </div>
          
          <button class="w-100 btn btn-primary" type="submit">Sign up</button>
          <p class="mt-4 mb-3 text-body-secondary text-center">Already have an account? <RouterLink to='/login'>Sign in</RouterLink>.</p>
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