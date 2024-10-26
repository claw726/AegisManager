import axios from "@/utils/axios.js";

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
      console.error(
        "Failed to delete project:",
        error.response ? error.response.data : error.message,
      );
      if (error.response) {
        switch (error.response.status) {
          case 404:
            console.error("Project not found with ID:", projectID);
            break;
          case 403:
            console.error("Unauthorized to delete project with ID:", projectID);
            break;
          default:
            console.error("Failed to delete project with ID:", projectID);
            break;
        }
      }
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
  // Other project-related actions
};

export default {
  namespaced: true,
  actions,
};
