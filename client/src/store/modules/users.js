import axios from "@/utils/axios.js";

const actions = {
  async fetchUserAccountByID({ rootState }, userID) {
    try {
      const response = await axios.get(`/api/users/${userID}`, {
        headers: {
          Authorization: `Bearer ${rootState.auth.authToken}`,
        },
      });
      return response.data;
    } catch (error) {
      console.error("Failed to fetch user account:", error.response.data);
      throw new Error("Failed to fetch user account");
    }
  },
  async fetchUserAccountByEmail({ rootState }, email) {
    try {
      if (typeof email !== "string") {
        throw new Error("Email must be a String!");
      }

      const response = await axios.get("/api/users/getUserByEmail", {
        params: { email },
        headers: {
          Authorization: `Bearer ${rootState.auth.authToken}`,
        },
      });
      return response.data;
    } catch (error) {
      if (error.response && error.response.status === 404) {
        console.error("User not found: ", error.response.data);
        throw new Error("User not found:");
      } else {
        console.error(
          "Failed to Fetch User:",
          error.response ? error.response.data : error.message,
        );
        throw new Error("Failed to Fetch User");
      }
    }
  },
  async fetchAllUsers({ rootState }) {
    try {
      const response = await axios.get("/api/users/getAllUsers", {
        headers: {
          Authorization: `Bearer ${rootState.auth.authToken}`,
          "Content-Type": "application/json",
        },
      });

      return response.data;
    } catch (error) {
      console.error("Error fetching all users:", error);
      throw error;
    }
  },
  // Other project-related actions
};

export default {
  namespaced: true,
  actions,
};
