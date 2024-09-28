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
                    component: () => import('./views/UserCreation.vue'),
                    meta: {requiresGuest: true},
                },
                {
                    path: '/login',
                    name: 'Login',
                    component: () => import('./views/LoginPage.vue'),
                    meta: {requiresGuest: true},
                },
                {
                    path: '/dashboard',
                    name: 'Dashboard',
                    component: () => import('./views/DashboardView.vue'),
                    meta: {requiresAuth: true},
                },

                {
                    path: '/todolist',
                    name: 'TDList',
                    component: () => import('./views/ToDoListView.vue'),
                    meta: {requiresAuth: true},
                },

                {
                    path: '/taskdetail',
                    name: 'TaskDetail',
                    component: () => import('./views/TaskDetail.vue'),
                    props: true // Allow passing route params as props
                }
            ],
    });

// Navigation Guard
router.beforeEach((to, from, next) => {
    const isLoggedIn = !!localStorage.getItem('CurrentUser');

    if (to.matched.some(record => record.meta.requiresAuth) && !isLoggedIn) {
        // Redirect to login if trying to access a protected route without being logged in
        next({ name: 'Login' });
    } else if (to.matched.some(record => record.meta.requiresGuest) && isLoggedIn) {
        // Redirect to dashboard if trying to access login or create account while logged in
        next({ name: 'Dashboard' });
    } else {
        next();
    }
});

export default router;