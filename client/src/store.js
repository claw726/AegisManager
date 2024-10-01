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
    setAuth(state, {token, email, name }) {
      state.isLoggedIn = true;
      state.token = token;
      state.userEmail = email;
      state.userName = name;
    },
    clearAuth(state) {
      state.isLoggedIn = false;
      state.token = '';
      state.userEmail = '';
      state.userName = '';
    }
  },
  actions: {
    async register({ dispatch }, { email, name, password }) {
      try {
        await axios.post('/api/auth/register', { email, name, password });
      } catch (error) {
        console.error('Registration Failed:', error);
      }
    },
    async login({ commit }, { email, password }) {
      try {
        const response = await axios.post('/api/auth/login', null, {
          params: {email, password}
        });
        console.log("response", response);
        const { token } = response.data;
        if ( token === undefined ) {
          throw new Error('Token not found in response');
        }
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