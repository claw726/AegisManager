import axios from "@/utils/axios.js";

const state = {
  tasks: [],
  currentTask: null,
  loading: false,
  error: null,
  updateStatus: {
    loading: false,
    error: null,
    success: false
  }
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
    const index = state.tasks.findIndex(task => task.taskID === updatedTask.taskID);
    if (index !== -1) {
      state.allTasks.splice(index, 1, updatedTask);
    }
  }
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
    commit('SET_LOADING', true);
    commit('SET_ERROR', null);
    
    try {
      const response = await axios.get(`/api/tasks/${taskId}/getTask`,
        {
          headers: {
            Authorization: `Bearer ${rootState.auth.authToken}`,
          },
        },
      );
      
      if (response.status === 200) {
        commit('SET_CURRENT_TASK', response.data);
        return response.data;
      }
    } catch (error) {
      let errorMessage = 'An error occurred while fetching the task.';
      
      if (error.response) {
        switch (error.response.status) {
          case 404:
            errorMessage = 'Task not found.';
            break;
          case 403:
            errorMessage = 'You do not have permission to view this task.';
            break;
          default:
            errorMessage = error.response.data || errorMessage;
        }
      }
      
      commit('SET_ERROR', errorMessage);
      throw new Error(errorMessage);
    } finally {
      commit('SET_LOADING', false);
    }
  },
  async updateTask({ commit, rootState }, { taskId, taskData }) {
    commit('SET_UPDATE_STATUS', { loading: true, error: null, success: false });
    
    try {
      const params = {
        taskName: taskData.taskName,
        taskDescription: taskData.taskDescription,
        assignerID: taskData.assignerID,
        taskPriority: taskData.taskPriority,
        dueDate: taskData.dueDate,
        isComplete: taskData.isComplete
      };

      const response = await axios({
        method: 'post',
        url: `/api/tasks/${taskId}/update`,
        params: params,
          headers: {
            Authorization: `Bearer ${rootState.auth.authToken}`,
            'Content-Type': 'application/x-www-form-urlencoded',
          }
        });

      if (response.status === 204) {
        commit('UPDATE_TASK_IN_LIST', { taskId, ...taskData });
        commit('SET_UPDATE_STATUS', { success: true });
        return true;
      }
    } catch (error) {
      let errorMessage = 'An error occurred while updating the task.';
      
      if (error.response) {
        switch (error.response.status) {
          case 404:
            errorMessage = 'Task or user not found.';
            break;
          case 403:
            errorMessage = 'You do not have permission to update this task.';
            break;
          default:
            errorMessage = error.response.data || errorMessage;
        }
      }
      
      commit('SET_UPDATE_STATUS', { error: errorMessage });
      throw new Error(errorMessage);
    } finally {
      commit('SET_UPDATE_STATUS', { loading: false });
    }
  }
  // Other task-related actions
};

const getters = {
  isLoading: state => state.loading,
  hasError: state => state.error !== null,
  errorMessage: state => state.error,
  currentTask: state => state.currentTask,
  isUpdateLoading: state => state.updateStatus.loading,
  updateError: state => state.updateStatus.error,
  updateSuccess: state => state.updateStatus.success
};

export default {
  state,
  mutations,
  namespaced: true,
  getters,
  actions,
};
