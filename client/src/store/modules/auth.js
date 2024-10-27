import axios from "@/utils/axios.js";

const state = {
  isLoggedIn: false,
  currentUser: null,
  authToken: null,
  loading: false,
  error: null,
};

const mutations = {
  SET_LOADING(state, status) {
    state.loading = status;
  },
  SET_ERROR(state, error) {
    state.error = error;
  },
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
  async register(
    { commit, dispatch },
    { email, name, password, profilePicture },
  ) {
    // Clear previous errors
    commit("SET_ERROR", null);

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

      if (response.data == "User created successfully") {
        // Automatically log in the user after successful registration

        await dispatch("login", { email, password });
      } else {
        commit("SET_ERROR", "Registration failed. Please try again.");
      }
    } catch (error) {
      let errorMessage = "An unexpected error occurred. Please try again.";

      // Handle specific error responses
      if (error.response) {
        switch (error.response.status) {
          case 400:
            errorMessage = "Invalid input. Please check your details."; // General error for bad requests
            break;
          case 409: // Conflict, e.g., email already exists
            errorMessage =
              "Email already exists. Please use a different email.";
            break;
          case 500:
            errorMessage = "Server error. Please try again later.";
            break;
          default:
            error.response.data.message ||
              "An unexpected error occurred. Please try again.";
        }
      } else if (error.request) {
        // Network error or other issues
        errorMessage =
          "Unable to connect to the server. Please check your internet connection. Is the server running? 🤔";
      } else {
        errorMessage = error.message;
      }

      commit("SET_ERROR", errorMessage);
      throw new Error(errorMessage);
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

      if (data && data.token) {
        commit("setAuthToken", data.token);
        commit("setLogin", true);
        // Fetch user details after login
        const user = await dispatch("users/fetchUserAccountByEmail", email, {
          root: true,
        });
        commit("setCurrentUser", user);
      } else {
        console.error("Login Failed:", data ? data : "No data received!");
        throw new Error("Login Failed!");
      }
      commit("SET_ERROR", null);
    } catch (error) {
      let errorMessage = "An unexpected error occurred. Please try again.";

      if (error.response) {
        // Handle specific error responses
        switch (error.response.status) {
          case 401:
            errorMessage = "Incorrect password. Please try again.";
            break;
          case 404:
            errorMessage = "Email not found. Please check your email.";
            break;
          case 400:
            errorMessage = "Invalid request. Please check your input.";
            break;
          default:
            errorMessage = error.response.data.message || errorMessage;
        }
      } else if (error.request) {
        // The request was made but no response was received
        errorMessage =
          "Unable to connect to the server. Please check your internet connection.";
      } else {
        // Something happened in setting up the request that triggered an Error
        errorMessage = error.message;
      }

      console.error("Failed to login:", errorMessage);
      // Optionally, you can commit an error message to the state
      commit("SET_ERROR", errorMessage); // Assuming you have a mutation to handle errors
      throw new Error(errorMessage); // Rethrow the error for further handling
    }
  },
  async requestPasswordReset({ state }, email) {
    try {
      const params = new URLSearchParams();
      params.append("email", email);
      const response = await axios.post(
        "/api/auth/requestPasswordReset",
        params,
        {
          headers: {
            Authorization: `Bearer ${state.authToken}`,
          },
        },
      );
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
