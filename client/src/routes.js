import {createRouter, createWebHistory} from 'vue-router';

const router = createRouter({
    history: createWebHistory(),
    routes: [
                {
                    path: '/',
                    name: 'Home',
                    component: () => import('./views/HomeView.vue')
                },
                {
                    path: '/createAccount',
                    name: 'CreateAcct',
                    component: () => import('./views/UserCreation.vue')
                },
                {
                    path: '/login',
                    name: 'Login',
                    component: () => import('./views/LoginPage.vue')
                }
            ],
    });

export default router;