   // store/index.js
   import { createStore } from 'vuex';

   export default createStore({
     state: {
       isLoggedIn: !!localStorage.getItem('CurrentUser'),
     },
     mutations: {
       setLoginStatus(state, status) {
         state.isLoggedIn = status;
       },
     },
     actions: {
       login({ commit }) {
         localStorage.setItem('CurrentUser', 'user data');
         commit('setLoginStatus', true);
       },
       logout({ commit }, router) {
         localStorage.removeItem('CurrentUser');
         commit('setLoginStatus', false);
         router.push({ name: 'Home'});
       },
     },
   });