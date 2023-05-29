import { defineStore } from "pinia";
import axios from 'axios';
import router from "@/router";


export const useAuthStore = defineStore({
    id: 'auth',
    state: () => {
        return {
            auth: localStorage.getItem('auth') ? true : false,
            user: localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user')!) : null,
        }
    },
    actions: {
        setAuth(auth: boolean) {
            this.auth = auth;
        },
        setUser(user: { id:string, username:string, email:string }) {
            localStorage.setItem('user', JSON.stringify(user));
            this.user = user;            
        },
        async register(user: any) {
            const response = await axios.post('auth/register', user);

            if(response.status === 200) {
                //console.log(response);
                if(response.data.error) {
                    throw new Error(response.data.message)
                } else {
                    router.push('/login');
                }
                
            } else {
                throw new Error('Unable to register. Please try again.');
            }
        },
        async login(user: any) {
            const response = await axios.post('auth/login', user, { withCredentials: true});

            if(response.status === 200) {

                localStorage.setItem('auth', 'true');
                localStorage.setItem('user', JSON.stringify(response.data.user));
                this.user = response.data.user;
                this.auth = true;

                axios.defaults.headers.common['Authorization'] = `Bearer ${response.data.accessToken}`;
                router.push('/');
            } else {
                throw new Error('Invalid Credentials');
            }            
        },
        async logout() {
            await axios.post('auth/logout', {}, { withCredentials: true });
            
            this.user = null;
            this.auth = false;
            localStorage.removeItem('user');
            localStorage.removeItem('auth');
            axios.defaults.headers.common['Authorization'] = '';
            router.push('/login');            
        },
    }
})
