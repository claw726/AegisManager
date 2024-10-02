import Vuex from 'vuex';
import createPersistedState from "vuex-persistedstate";
import axios from '@/utils/axios.js';
// import LocalStorageService from '@/utils/LocalStorageService.js';


export default new Vuex.Store({
  plugins: [createPersistedState()],

  state: {
    isLoggedIn: false,
    currentUser: null,
    authToken: null,
    userAccounts: [],
    organizations: [],  

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
    setCurrentUser(state, user) {
      state.currentUser = user;
    },
    addUserAccount(state, user) {
      state.userAccounts.push(user);
    },
    setAuthToken(state, token) {
      state.authToken = token;
    },
    clearAuth(state) {
      state.currentUser = null;
      state.authToken = null;
    },
    setLogin(state, isLoggedIn) {
      state.isLoggedIn = isLoggedIn;
    },
    addOrganization(state, organization) {
      state.organizations.push(organization);
    },
    removeOrganization(state, index) {
      state.organizations.splice(index, 1);
    }

  },
  actions: {
    async register({ dispatch, commit }, { firstName, lastName, email, password, profilePicture }) {
      const user = {firstName, lastName, email, password, profilePicture};
      commit('addUserAccount', user);
      dispatch('login', { email, password });
    },
    async login({ commit, state }, { email, password }) {
      const user = state.userAccounts.find(user => user.email === email);
      if (user && user.password === password) {
        commit('setCurrentUser', user);
        commit('setAuthToken', 'logged_in');
        commit('setLogin', true);
      } else {
        alert('Login failed: Invalid email or password');
      }
    },
    async logout({ commit }) {
      commit('clearAuth');
      commit('setLogin', false);
    },
    async createOrganization({ commit }, organization) {
      commit('addOrganization', organization);
    },
  },
});