import { createApp } from 'vue';
import { createPinia } from 'pinia';
import Toast from "vue-toastification";
import "vue-toastification/dist/index.css";
import 'bootstrap/dist/css/bootstrap.css';
import 'bootstrap-vue/dist/bootstrap-vue.css';
import 'bootstrap-icons/font/bootstrap-icons.css';
import bootstrap from 'bootstrap/dist/js/bootstrap.min.js';
import App from './App.vue';
import router from './router';
import './assets/css/globals.min.css';
import './interceptors/axios';

const app = createApp(App)

app.use(createPinia());
app.use(bootstrap);
app.use(router);
app.use(Toast);


app.mount('#app')
