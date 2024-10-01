import Vuex from 'vuex';
import axios from '@/utils/axios.js';

export default new Vuex.Store({
  state: {
    isLoggedIn: false,
    userEmail: '',
    userFirstName: '',
    userLastName: '',
    userPhoto: '',

    allTasks: {
    "1": { 
        id: 1, title: 'Task 1', description: 'Complete project FROM THE DATABASE', dueDate: '2024-09-30', 
        priority: 'High', completed: false, assignees: ['User A', 'User B']
    },
    "2" : { 
        id: 2, title: 'Task 2', description: 'Prepare presentation', dueDate: '2024-10-05', 
      priority: 'High', completed: false, assignees: ['User C'] 
    },
    "3" : { 
        id: 3, title: 'Task 3', description: 'DO whatever I want to', dueDate: '2024-11-15', 
      priority: 'High', completed: false, assignees: ['User D', 'User B'] 
    }
  }
  
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

  plugins: [
    store => {
      store.subscribe((mutation, state) => {
        localStorage.setItem('vuex', JSON.stringify(state));
      });
    }
  ]
});