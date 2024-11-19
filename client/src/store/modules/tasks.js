import axios from "@/utils/axios.js";

const state = {
  tasks: [],
  currentTask: null,
  loading: false,
  error: null,
  updateStatus: {
    loading: false,
    error: null,
    success: false,
  },
};

const mutations = {
  SET_LOADING(state, status) {
    state.loading = status;
  },
  SET_ERROR(state, error) {
    state.error = error;
  },
  SET_TASKS(state, tasks) {
    state.tasks = tasks;
  },
  SET_CURRENT_TASK(state, task) {
    state.currentTask = task;
  },
  SET_UPDATE_STATUS(state, status) {
    state.updateStatus = { ...state.updateStatus, ...status };
  },
  UPDATE_TASK_IN_LIST(state, updatedTask) {
    const index = state.tasks.findIndex(
      (task) => task.taskID === updatedTask.taskID,
    );
    if (index !== -1) {
      state.allTasks.splice(index, 1, updatedTask);
    }
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

  async fetchTask({ commit, rootState }, taskId) {
    commit("SET_LOADING", true);
    commit("SET_ERROR", null);

    try {
      const response = await axios.get(`/api/tasks/${taskId}/getTask`, {
        headers: {
          Authorization: `Bearer ${rootState.auth.authToken}`,
        },
      });

      if (response.status === 200) {
        commit("SET_CURRENT_TASK", response.data);
        console.log("Task fetched!");
        //console.log(response.data)
        return response.data;
      }
    } catch (error) {
      let errorMessage = "An error occurred while fetching the task.";

      if (error.response) {
        switch (error.response.status) {
          case 404:
            errorMessage = "Task not found.";
            break;
          case 403:
            errorMessage = "You do not have permission to view this task.";
            break;
          default:
            errorMessage = error.response.data || errorMessage;
        }
      }
      throw new Error(errorMessage);
    }
  },

  async fetchTasksForProjects({ dispatch, commit }, projectIDs) {
    commit("SET_LOADING", true);
    try {
      const taskPromises = projectIDs.map((projectID) => dispatch("fetchTasksFromProject", projectID));
      const taskArrays = await Promise.all(taskPromises);

      // Flatten the array of arrays
      const allTasks = Array.from(new Set(taskArrays.flat()));

      commit("SET_TASKS", allTasks);
      return allTasks;
    } catch (error) {
      commit("SET_ERROR", error.message);
      throw error;
    } finally {
      commit("SET_LOADING", false);
    }
  },

  async createTask({ commit, rootState }, task) {
    try {
      const params = {
        taskName: task.taskName,
        taskDescription: task.taskDescription,
        dueDate: task.dueDate,
        taskPriority: task.taskPriority,

        assignerID: task.assignerID,
        parentProjectID: task.parentProjectID,
        parentOrgID: task.parentOrgID,
      };

      const response = await axios({
        method: "post",
        url: `/api/tasks/createTask`,
        params: params,
        headers: {
          Authorization: `Bearer ${rootState.auth.authToken}`,
          "Content-Type": "application/x-www-form-urlencoded",
        },
      });

      if (response.status === 200) {
        commit("UPDATE_TASK_IN_LIST", task);
        //commit('SET_UPDATE_STATUS', { success: true });
        commit("SET_CURRENT_TASK", response.data);
        console.log("Task created 204");
        return true;
      }
      console.log("Task created successfully");
      return response.status;
    } catch (error) {
      console.log("An error occurred while updating the task.");
      console.log("error response: ", error.response);
      console.log("error status: ", error.response.status);
    }
  },

  async updateTask({ commit, rootState }, { taskId, taskData }) {
    commit("SET_UPDATE_STATUS", { loading: true, error: null, success: false });

    try {
      const params = {
        taskName: taskData.taskName,
        taskDescription: taskData.taskDescription,
        assignerID: taskData.assignerID,
        taskPriority: taskData.taskPriority,
        dueDate: taskData.dueDate,
        isComplete: taskData.isComplete,
      };

      const response = await axios({
        method: "post",
        url: `/api/tasks/${taskId}/update`,
        params: params,
        headers: {
          Authorization: `Bearer ${rootState.auth.authToken}`,
          "Content-Type": "application/x-www-form-urlencoded",
        },
      });

      if (response.status === 204) {
        commit("UPDATE_TASK_IN_LIST", { taskId, ...taskData });
        commit("SET_UPDATE_STATUS", { success: true });
        return true;
      }
      return true;
    } catch (error) {
      let errorMessage = "An error occurred while updating the task.";

      if (error.response) {
        switch (error.response.status) {
          case 404:
            errorMessage = "Task or user not found.";
            break;
          case 403:
            errorMessage = "You do not have permission to update this task.";
            break;
          default:
            errorMessage = error.response.data || errorMessage;
        }
      }

      commit("SET_UPDATE_STATUS", { error: errorMessage });
      throw new Error(errorMessage);
    } finally {
      commit("SET_UPDATE_STATUS", { loading: false });
    }
  },

  async deleteTask({ commit, rootState }, task) {
    const id = Number(task.taskID);
    console.log(id);
    console.log(typeof id);
    try {
      const response = await axios({
        method: "delete",
        url: `/api/tasks/${id}/deleteTask`,
        headers: {
          Authorization: `Bearer ${rootState.auth.authToken}`,
          "Content-Type": "application/x-www-form-urlencoded",
        },
      });

      if (response.status === 200) {
        commit("SET_UPDATE_STATUS", { success: true });
        console.log("Task deleted");
        return true;
      }
      console.log("Task deleted successfully");
    } catch (error) {
      let errorMessage = "An error occurred while deleting the task.";

      if (error.response) {
        switch (error.response.status) {
          case 404:
            errorMessage = "Task or user not found.";
            break;
          case 403:
            errorMessage = "You do not have permission to delete this task.";
            break;
          default:
            errorMessage = error.response.data || errorMessage;
        }
      }
      console.log("error response: ", errorMessage);
      //console.log('error status: ', error.response.status);
    }
  },

  async addUserToTask({ commit, rootState }, { taskId, email }) {
    commit("SET_UPDATE_STATUS", { loading: true, error: null, success: false });

    try {
      const params = {
        email: email,
      };

      const response = await axios({
        method: "post",
        url: `/api/tasks/${taskId}/addUser`,
        params: params,
        headers: {
          Authorization: `Bearer ${rootState.auth.authToken}`,
          "Content-Type": "application/x-www-form-urlencoded",
        },
      });

      if (response.status === 200) {
        commit("SET_UPDATE_STATUS", { success: true });
      }
      //can't read status if successful, return number
      return true;
    } catch (error) {
      let errorMessage = "An error occurred while adding user to the task.";

      if (error.response) {
        switch (error.response.status) {
          case 404:
            errorMessage = "Task or user not found.";
            break;
          case 403:
            errorMessage =
              "You do not have permission to add a user this task.";
            break;
          default:
            errorMessage = error.response.data || errorMessage;
        }
        return error.response.status;
      }

      commit("SET_UPDATE_STATUS", { error: errorMessage });
      //throw new Error(errorMessage);
      console.log("printing repsonse status for error:", error.response.status);
    } finally {
      commit("SET_UPDATE_STATUS", { loading: false });
    }
  },

  async removeUserToTask({ commit, rootState }, { taskId, email }) {
    commit("SET_UPDATE_STATUS", { loading: true, error: null, success: false });

    try {
      const params = {
        email: email,
      };

      const response = await axios({
        method: "post",
        url: `/api/tasks/${taskId}/removeUser`,
        params: params,
        headers: {
          Authorization: `Bearer ${rootState.auth.authToken}`,
          "Content-Type": "application/x-www-form-urlencoded",
        },
      });

      if (response.status === 200) {
        commit("SET_UPDATE_STATUS", { success: true });
        return true;
      }
      return 200;
    } catch (error) {
      let errorMessage = null;

      if (error.response) {
        switch (error.response.status) {
          case 404:
            errorMessage = "Task or user not found.";
            break;
          case 403:
            errorMessage =
              "You do not have permission to add a user this task.";
            break;
          default:
            errorMessage = error.response.data || errorMessage;
        }
        return error.response.status;
      }

      commit("SET_UPDATE_STATUS", { error: errorMessage });

      console.log(errorMessage);
      return false;
    } finally {
      commit("SET_UPDATE_STATUS", { loading: false });
    }
  },
};

const getters = {
  isLoading: (state) => state.loading,
  hasError: (state) => state.error !== null,
  errorMessage: (state) => state.error,
  currentTask: (state) => state.currentTask,
  isUpdateLoading: (state) => state.updateStatus.loading,
  updateError: (state) => state.updateStatus.error,
  updateSuccess: (state) => state.updateStatus.success,
  fullTasks: (state) => state.tasks,
};

export default {
  state,
  mutations,
  namespaced: true,
  getters,
  actions,
};
