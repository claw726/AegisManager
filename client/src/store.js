import Vuex from 'vuex';
import axios from '@/utils/axios.js';

export default new Vuex.Store({
  state: {
    isLoggedIn: false,
    userEmail: '',
    userName: '',
    token: '',
  },
  mutations: {
    login(state, userEmail) {
      const userAccounts = JSON.parse(localStorage.getItem('userAccounts'));
      const user = userAccounts.find(account => account.email === userEmail);
      if (user) {
        state.isLoggedIn = true;
        state.userEmail = user.email;
        state.userFirstName = user.firstName;
        state.userLastName = user.lastName;
        state.userPhoto = user.profilePicture;
      } else {
        state.isLoggedIn = false;
        state.userEmail = '';
        state.userFirstName = '';
        state.userLastName = '';
        state.userPhoto = '';
      }
    },
    logout(state) {
      state.isLoggedIn = false;
      state.userEmail = '';
      state.userFirstName = '';
      state.userLastName = '';
      state.userPhoto = '';
    },
    setAuth(state, {token, email, name }) {
      state.isLoggedIn = true;
      state.token = token;
      state.userEmail = userEmail;
      state.userName = userName;
    },
    clearAuth(state) {
      state.isLoggedIn = false;
      state.token = '';
      state.userEmail = '';
      state.userName = '';
    }
  },
  actions: {
    login({ commit }, userEmail) {
      commit('login', userEmail);
    },
    logout({ commit }) {
      localStorage.removeItem('CurrentUser')
      commit('logout');
    },
    async register({ dispatch }, { email, name, password }) {
      try {
        await axios.post('/api/auth/register', {email, name, password });
        await dispatch('login', { email, password });
      } catch (error) {
        console.error('Registration Failed:', error);
      }
    },
    async login({ commit }, { email, password }) {
      try {
        const response = await axios.post('/api/auth/login', null, {
          params: {email, password}
        });
        const { token } = response.data;
        commit('setAuth', { token, email, name: response.data.name});
        localStorage.setItem('authToken', token);
      } catch (error) {
        console.error('Login failed:', error);
      }
    },
    logout({ commit }) {
      localStorage.removeItem('authToken');
      commit('clearAuth');
    }
  },
});