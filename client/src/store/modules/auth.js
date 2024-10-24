import axios from "@/utils/axios.js";

const state = {
  isLoggedIn: false,
  currentUser: null,
  authToken: null,
};

const mutations = {
  setCurrentUser(state, user) {
    state.currentUser = user;
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
};

const actions = {
    async register({ dispatch }, { email, name, password, profilePicture }) {
      try {
        const params = new URLSearchParams();
        params.append("email", email);
        params.append("name", name);
        params.append("password", password);
        params.append("profilePicture", profilePicture);

        const response = await axios.post("/api/auth/register", params, {
          headers: {
            "Content-Type": "application/x-www-form-urlencoded",
          },
        });

        alert(response.data);
        await dispatch("login", { email, password });
      } catch (error) {
        console.error("Failed to register:", error.response.data);
        alert("Registration failed: " + error.response.data);
      }
    },
    async login({ commit, dispatch }, { email, password }) {
      try {
        const params = new URLSearchParams();
        params.append("email", email);
        params.append("password", password);

        const response = await axios.post("/api/auth/login", params, {
          headers: {
            "Content-Type": "application/x-www-form-urlencoded",
          },
        });

        const data = response.data;

        if (response.data && response.data.token) {
          commit("setAuthToken", response.data.token);
          commit("setLogin", true);
          // Fetch user details after login
          const user = await dispatch("users/fetchUserAccountByEmail", email, { root: true });
          commit("setCurrentUser", user);
        } else {
          console.error("Login Failed:", data ? data : "No data received!");
          throw new Error("Login Failed!");
        }
      } catch (error) {
        console.error(
          "Failed to login:",
          error.response ? error.response.data : error.message,
        );
        alert("Login failed");
        throw error;
      }
    },
    async requestPasswordReset({ state }, email) {
      try {
        const params = new URLSearchParams();
        params.append("email", email);
        const response = await axios.post(params, {
          headers: {
            Authorization: `Bearer ${state.authToken}`,
          },
        });
        console.log("Password reset requested:", response.data);
        return response.data;
      } catch (error) {
        console.error(
          "Failed to request password reset:",
          error.response?.data || error.message,
        );
        throw new Error(
          error.response?.data || "Error requesting password reset",
        );
      }
    },
    async resetPassword({ state }, { newPassword, token }) {
      try {
        const params = new URLSearchParams();
        params.append("password", newPassword);
        params.append("token", token);
        const response = await axios.post("/api/auth/resetPassword", params, {
          headers: {
            Authorization: `Bearer ${state.authToken}`,
          },
        });
        console.log("Password reset:", response.data);
        return response.data;
      } catch (error) {
        console.error(
          "Failed to reset password:",
          error.response?.data || error.message,
        );
        throw new Error(error.response?.data || "Error resetting password");
      }
    },
  async logout({ commit }) {
    commit("clearAuth");
    commit("setLogin", false);
  },
  // Other auth-related actions
};

export default {
  namespaced: true,
  state,
  mutations,
  actions,
};