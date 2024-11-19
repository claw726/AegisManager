import axios from "@/utils/axios.js";

const state = {
  projects: [],
  currentProject: null,
  error: null,
  loading: false,
};

const getters = {
  projects: (state) => state.projects,
  isLoading: (state) => state.loading,
  hasError: (state) => !!state.error,
  errorMessage: (state) => state.error,
};

const mutations = {
  SET_PROJECTS(state, projects) {
    state.projects = projects;
  },

  SET_CURRENT_PROJECT(state, project) {
    state.currentProject = project;
  },

  SET_ERROR(state, error) {
    state.error = error;
  },

  SET_LOADING(state, status) {
    state.loading = status;
  },
};

const actions = {
  async fetchOrgProjects({ rootState }, orgID) {
    try {
      const response = await axios.get(
        `/api/orgs/${orgID}/getAllProjectsFromOrg`,
        {
          headers: {
            Authorization: `Bearer ${rootState.auth.authToken}`,
          },
        },
      );
      return response.data;
    } catch (error) {
      console.error(
        `Failed to fetch projects for org ${orgID}:`,
        error.response ? error.response.data : error.message,
      );
      throw new Error("Failed to fetch projects");
    }
  },
  async createProject({ rootState }, project) {
    try {
      const params = new URLSearchParams();
      params.append("projectName", project.projName);
      params.append("projectDescription", project.projDescription);
      params.append("projectOwnerID", project.projCreator);
      params.append("parentOrgID", project.parentOrgID);
      params.append("encodedImage", project.projImg);

      await axios.post("/api/projects/createProject", params, {
        headers: {
          Authorization: `Bearer ${rootState.auth.authToken}`,
        },
      });
    } catch (error) {
      console.error(
        "Error creating project:",
        error.response ? error.response.data : error.message,
      );
      throw new Error("Failed to Create Project");
    }
  },
  async deleteProject({ rootState }, { projectID }) {
    try {
      await axios.delete(`/api/projects/${projectID}/deleteProject`, {
        headers: {
          Authorization: `Bearer ${rootState.auth.authToken}`,
        },
      });
    } catch (error) {
      let errorMessage = "Failed to delete project";
      if (error.response) {
        switch (error.response.status) {
          case 404:
            console.error("Project not found with ID:", projectID);
            errorMessage = "Project not found";
            break;
          case 403:
            console.error("Unauthorized to delete project with ID:", projectID);
            errorMessage = "Unauthorized to delete project";
            break;
          default:
            console.error("Failed to delete project with ID:", projectID);
            errorMessage = "Failed to delete project";
            break;
        }
      } else if (error.request) {
        console.error("No response from server. Please check your connection.");
        errorMessage = "No response from server. Please check your connection.";
      } else {
        console.error("Error deleting project:", error.message);
        errorMessage = `Error: ${error.message}`;
      }
      throw new Error(errorMessage);
    }
  },
  async modifyProject({ rootState }, { project, projectID }) {
    try {
      const params = new URLSearchParams();
      params.append("projectName", project.projectName);
      params.append("projectDescription", project.projectDescription);
      params.append("projectOwnerID", project.projectOwnerID);
      params.append("encodedImage", project.encodedImage);
      await axios.post(`/api/projects/${projectID}/update`, params, {
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
          Authorization: `Bearer ${rootState.auth.authToken}`,
        },
      });
    } catch (error) {
      console.error(
        "Failed to modify project:",
        error.response ? error.response.data : error.message,
      );
      throw new Error("Failed to modify project");
    }
  },
  async fetchProject({ rootState }, projID) {
    try {
      const response = await axios.get(`/api/projects/${projID}/getProject`, {
        headers: {
          Authorization: `Bearer ${rootState.auth.authToken}`,
        },
      });
      return response.data;
    } catch (error) {
      console.error(
        `Failed to fetch project ${projID}`,
        error.response ? error.response.data : error.message,
      );
      throw new Error("Failed to fetch project");
    }
  },
  async fetchProjectMembers({ rootState }, projectID) {
    try {
      const response = await axios.get(`/api/projects/${projectID}/getUsers`, {
        headers: {
          Authorization: `Bearer ${rootState.auth.authToken}`,
        },
      });
      return response.data;
    } catch (error) {
      console.error(
        "Error fetching project members: ",
        error.response?.data || error.message,
      );
      throw error;
    }
  },
  async removeUserFromProject({ rootState }, { projectID, email }) {
    try {
      const params = new URLSearchParams();
      params.append("email", email);

      const response = await axios.post(
        `/api/projects/${projectID}/removeUser`,
        params,
        {
          headers: {
            Authorization: `Bearer ${rootState.auth.authToken}`,
          },
        },
      );
      return response.data;
    } catch (error) {
      console.error(
        "Error removing user from project: ",
        error.response?.data || error.message,
      );
      throw error;
    }
  },
  async addUserToProject({ rootState }, { projectID, email }) {
    try {
      const params = new URLSearchParams();
      params.append("email", email);
      const response = await axios.post(
        `/api/projects/${projectID}/addUser`,
        params,
        {
          headers: {
            Authorization: `Bearer ${rootState.auth.authToken}`,
          },
        },
      );
      return response.data;
    } catch (error) {
      console.error(
        "Error adding user to project: ",
        error.response?.data || error.message,
      );
      throw error;
    }
  },

  async fetchProjectFromOrgs({ dispatch, commit}, orgIDs) {
    commit("SET_LOADING", true);
    commit("SET_ERROR", null);
    try {
      // check if orgIDs exists and is not empty
      if (!orgIDs || !Array.isArray(orgIDs) || orgIDs.length === 0) {
        console.log("No orgIDs provided");
        commit("SET_PROJECTS", []);
        return [];
      }

      const projectPromises = orgIDs.map(orgID => dispatch("fetchOrgProjects", orgID));
      const projectArrays = await Promise.all(projectPromises);

      // Flatten the array of arrays
      const allProjects = Array.from(new Set(projectArrays.flat()));
      console.log("All projects: ", allProjects);
      commit("SET_PROJECTS", allProjects);
      return allProjects;
    } catch (error) {
      commit("SET_ERROR", error.message);
      throw error;
  } finally {
    commit("SET_LOADING", false);
  }
},

  async fetchProjectsWithTasks({ dispatch }, orgIDs) {
    try {
      console.log('Starting fetchProjectsWithTasks with orgIDs:', orgIDs);
      // Check if orgIDs exists and is not empty
      if (!orgIDs || !Array.isArray(orgIDs) || orgIDs.length === 0) {
        console.log('No organizations to fetch projects from');
        return [];
      }
      const projects = await dispatch('fetchProjectFromOrgs', orgIDs);
      console.log('Received projects from fetchProjectFromOrgs:', projects);

      if (!projects || projects.length === 0) {
        console.log('No projects received');
        return [];
      }

      // Fetch tasks for each project
      const projectsWithTasks = await Promise.all(
        projects.map(async (project) => {
          try {
            console.log(`Fetching tasks for project ${project.projectID}`);
            const tasks = await dispatch('fetchTasksFromProject', project.projectID);
            console.log(`Received tasks for project ${project.projectID}:`, tasks);
            return {
              ...project,
              tasks: tasks || []
            };
          } catch (error) {
            console.error(`Error fetching tasks for project ${project.projectID}:`, error);
            return {
              ...project,
              tasks: []
            };
          }
        })
      );
      console.log('Final projectsWithTasks:', projectsWithTasks);
      return projectsWithTasks;
    } catch (error) {
      console.error("Error fetching projects with tasks: ", error.message);
      throw error;
    }
  },

  async fetchTasksFromProject({ rootState }, projectID) {
    try {
      const response = await axios.get(
        `/api/projects/${projectID}/getAllTasksFromProject`,
        {
          headers: {
            Authorization: `Bearer ${rootState.auth.authToken}`,
          },
        },
      );
      return response.data;
    } catch (error) {
      let errorMessage = "An error occurred while fetching tasks.";
      if (error.response) {
        switch (error.response.status) {
          case 404:
            errorMessage = "Project not found.";
            break;
          case 403:
            errorMessage = "Unauthorized to fetch tasks.";
            break;
          case 500:
            errorMessage = "Internal server error. Please Try again later";
            break;
          default:
            errorMessage = `Unexpected error: ${error.response.status}`;
            break;
        }
      } else if (error.request) {
        errorMessage = "No response from server. Please check your connection.";
      } else {
        errorMessage = `Error: ${error.message}`;
      }
      throw new Error(errorMessage);
    }
  },
  async changeArchivedStatus({ commit, rootState }, { projectID, isArchived }) {
    try {
      const params = new URLSearchParams();
      params.append("isArchived", isArchived);
      const response = await axios.post(
        `/api/projects/${projectID}/changeArchivedStatus`,
        params,
        {
          headers: {
            Authorization: `Bearer ${rootState.auth.authToken}`,
          },
        },
      );
      return response.data;
    } catch (error) {
      console.error(
        "Error changing archived status: ",
        error.response?.data || error.message,
      );
      var errorMessage = "An error occurred while changing archived status.";
      if (error.response) {
        switch (error.response.status) {
          case 404:
            errorMessage = "Project not found.";
            break;
          case 403:
            errorMessage = "Unauthorized to change archived status.";
            break;
          default:
            errorMessage = `Unexpected error: ${error.response.status}`;
            break;
        }
      } else if (error.request) {
        errorMessage = "No response from server. Please check your connection.";
      } else {
        errorMessage = `Error: ${error.message}`;
      }
      commit("SET_ERROR", errorMessage);
      throw new Error(errorMessage);
    }
  },
  async fetchUserProjects({ commit, rootState }, email) {
    commit("SET_ERROR", null);
    commit("SET_LOADING", true);
    try {
      const params = new URLSearchParams();
      params.append("email", email);
      const response = await axios.get("/api/projects/getAllUserProjects", {
        params: { email },
        headers: {
          Authorization: `Bearer ${rootState.auth.authToken}`,
        },
      });

      commit("SET_PROJECTS", response.data);
      return response.data;
    } catch (error) {
      const errorMessage =
        error.response?.data || error.message || "Failed to fetch projects";
      commit("SET_ERROR", errorMessage);
      throw error;
    } finally {
      commit("SET_LOADING", false);
    }
  },
  // Other project-related actions
};

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters,
};
