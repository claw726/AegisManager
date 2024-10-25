import axios from "@/utils/axios.js";

const actions = {
  async fetchTasks({ rootState }, userID) {
    try {
      const response = await axios
        .get(`/api/tasks/getAllUserTasks`, {
          params: {
            userID: userID,
          },
          headers: {
            Authorization: `Bearer ${rootState.auth.authToken}`,
          },
        })
        .then((response) => response.json());
      //this.tasks = response.data;
      return response.data;
    } catch (error) {
      console.error("Failed to fetch tasks:", error.response.data);
      throw new Error("Failed to fetch tasks");
    }
  },
  // Other task-related actions
};

export default {
  namespaced: true,
  actions,
};
