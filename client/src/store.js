import Vuex from 'vuex';

export default new Vuex.Store({
  state: {
    isLoggedIn: false,
    userEmail: '',
    userFirstName: '',
    userLastName: '',
    userPhoto: '',
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
  },
});