const user = {
    state: {
      userAccounts: [],
      isLoggedIn: false,
      currentUser: null,
      authToken: null
    },
    mutations: {
      addUserAccount(state, user) {
        state.userAccounts.push(user);
      },
      setCurrentUser(state, user) {
        state.currentUser = user;
      },
      setAuthToken(state, token) {
        state.authToken = token;
      },
      setLogin(state, isLoggedIn) {
        state.isLoggedIn = isLoggedIn;
      }
    },
    actions: {
      async register({ commit }, { firstName, lastName, email, password, profilePicture }) {
        const user = {firstName, lastName, email, password, profilePicture};
        commit('addUserAccount', user);
        commit('login', { email, password });
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
      logout({ commit }) {
        commit('clearAuth');
        commit('setLogin', false);
      }
    }
  };
  
  export default user;