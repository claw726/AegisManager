import axios from "@/utils/axios.js";

const state = {
  tasks: [],
  loading: false,
  error: null,
};

const mutations = {
  SET_TASKS(state, tasks) {
    state.tasks = Array.isArray(tasks) ? tasks : [];
  },
};

const actions = {
  async fetchTasks({ commit, rootState }) {
    try {
      const userID = rootState.auth.currentUser.userID;
      const response = await axios.get(
        `/api/tasks/getAllUserTasks?userID=${userID}`,
        {
          headers: {
            Authorization: `Bearer ${rootState.auth.authToken}`,
          },
        },
      );
      console.log("API Tasks response: ", response.data);

      let tasksData = response.data;
      if (typeof tasksData === "string") {
        try {
          tasksData = JSON.parse(tasksData);
        } catch (error) {
          console.error("Error parsing tasks data: ", error);
          tasksData = [];
        }
      }

      const tasks = Array.isArray(tasksData) ? tasksData : [];
      commit("SET_TASKS", tasks);
      return tasks;
    } catch (error) {
      let errorMessage = "Error fetching tasks";

      if (error.response) {
        switch (error.response.status) {
          case 401:
            errorMessage = "Please login again";
            break;
          case 403:
            errorMessage = "You are not authorized to view tasks";
            break;
          case 404:
            errorMessage = "User not found";
            break;
          default:
            errorMessage = error.response.data || errorMessage;
        }
      } else if (error.request) {
        errorMessage = "Error connecting to server";
      }
      throw new Error(errorMessage);
    }
  },
  // Other task-related actions
};

export default {
  state,
  mutations,
  namespaced: true,
  actions,
};
