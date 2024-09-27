import Vuex from 'vuex';

export default new Vuex.Store({
  state: {
    isLoggedIn: !!localStorage.getItem('CurrentUser'),
  },
  mutations: {
    setLoginStatus(state, status) {
      state.isLoggedIn = status;
      if (status) {
        localStorage.setItem('CurrentUser', 'userAccounts');
      } else {
        localStorage.removeItem('CurrentUser');
      }
    },
  },
  actions: {
    login({ commit }) {
      commit('setLoginStatus', true);
    },
    logout({ commit }) {
      localStorage.removeItem('CurrentUser')
      commit('setLoginStatus', false);
    },
  },
});