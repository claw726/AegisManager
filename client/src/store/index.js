import Vuex from "vuex";
import createPersistedState from "vuex-persistedstate";
import axios from "@/utils/axios.js";
// import LocalStorageService from '@/utils/LocalStorageService.js';

export default new Vuex.Store({
  plugins: [createPersistedState()],

  state: {
    isLoggedIn: false,
    currentUser: null,
    authToken: null,
    userAccounts: [],
    organizations: [],
    tasks: [],

    allTasks: {
      1: {
        id: 1,
        title: "Task 1",
        description: "Complete project FROM THE DATABASE",
        dueDate: "2024-09-30",
        priority: "High",
        completed: false,
        assignees: ["User A", "User B"],
      },
      2: {
        id: 2,
        title: "Task 2",
        description: "Prepare presentation",
        dueDate: "2024-10-05",
        priority: "High",
        completed: false,
        assignees: ["User C"],
      },
      3: {
        id: 3,
        title: "Task 3",
        description: "DO whatever I want to",
        dueDate: "2024-11-15",
        priority: "High",
        completed: false,
        assignees: ["User D", "User B"],
      },
    },
  },

  mutations: {
    setCurrentUser(state, user) {
      state.currentUser = user;
    },
    setAuthToken(state, token) {
      state.authToken = token;
    },
    setTasks(state, t) {
      state.tasks = t;
    },
    clearAuth(state) {
      state.currentUser = null;
      state.authToken = null;
    },
    setLogin(state, isLoggedIn) {
      state.isLoggedIn = isLoggedIn;
    },
  },
  actions: {
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
          const user = await dispatch("fetchUserAccountByEmail", email);
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
    async logout({ commit }) {
      commit("clearAuth");
      commit("setLogin", false);
    },
    async fetchUserAccountByID({ state }, userID) {
      try {
        const response = await axios.get(`/api/users/${userID}`, {
          headers: {
            Authorization: `Bearer ${state.authToken}`,
          },
        });

        return response.data;
      } catch (error) {
        console.error("Failed to fetch user account:", error.response.data);
        throw new Error("Failed to fetch user account");
      }
    },
    async fetchUserAccountByEmail({ state }, email) {
      try {
        if (typeof email !== "string") {
          throw new Error("Email must be a String!");
        }

        const response = await axios.get("/api/users/getUserByEmail", {
          params: { email },
          headers: {
            Authorization: `Bearer ${state.authToken}`,
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
    async fetchOrganizations({ state }) {
      try {
        const response = await axios.get("/api/orgs/getAllOrgs", {
          headers: {
            Authorization: `Bearer ${state.authToken}`,
          },
        });
        return response.data;
      } catch (error) {
        console.error(
          "Failed to fetch organizations:",
          error.response ? error.response.data : error.message,
        );
        throw new Error("Failed to fetch organizations");
      }
    },

    async fetchTasks({ state }, userID) {
      try {
        const response = await axios
          .get(`/api/tasks/getAllUserTasks`, {
            params: {
              userID: userID,
            },

            headers: {
              Authorization: `Bearer ${state.authToken}`,
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

    async fetchOrganization({ state }, orgID) {
      try {
        const response = await axios.get(`/api/orgs/${orgID}/getOrg`, {
          headers: {
            Authorization: `Bearer ${state.authToken}`,
          },
        });
        return response.data;
      } catch (error) {
        console.error(
          `Failed to fetch organization ${orgID}`,
          error.response ? error.response.data : error.message,
        );
        throw new Error("Failed to fetch organization");
      }
    },

    async fetchOrgProjects({ state }, orgID) {
      try {
        const response = await axios.get(
          `/api/orgs/${orgID}/getAllProjectsFromOrg`,
          {
            headers: {
              Authorization: `Bearer ${state.authToken}`,
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

    async createOrganization({ state }, organization) {
      try {
        const params = new URLSearchParams();
        params.append("orgName", organization.orgName);
        params.append("orgDescription", organization.orgDescription);
        params.append("orgOwnerID", organization.orgOwnerID);
        params.append("encodedImage", organization.OrgLogo);

        const response = await axios.post("/api/orgs/createOrg", params, {
          headers: {
            Authorization: `Bearer ${state.authToken}`,
            "Content-Type": "application/x-www-form-urlencoded",
          },
        });
        return response.data;
      } catch (error) {
        console.error(
          "Failed to create organization:",
          error.response ? error.response.data : error.message,
        );
        throw new Error("Failed to create organization");
      }
    },
    async modifyOrganization({ state }, { orgID, organization }) {
      try {
        const params = new URLSearchParams();
        params.append("orgName", organization.orgName);
        params.append("orgDescription", organization.orgDescription);
        params.append("orgOwnerID", organization.orgOwnerID);
        params.append("encodedImage", organization.OrgLogo);
        const response = await axios.post(`/api/orgs/${orgID}/update`, params, {
          headers: {
            "Content-Type": "application/x-www-form-urlencoded",
            Authorization: `Bearer ${state.authToken}`,
          },
        });
        return response.data;
      } catch (error) {
        console.error("Failed to modify organization:", error.response.data);
        throw new Error("Failed to modify organization");
      }
    },
    async deleteOrganization({ state }, orgID) {
      try {
        await axios.delete(`/api/orgs/${orgID}/deleteOrg`, {
          headers: {
            Authorization: `Bearer ${state.authToken}`,
          },
        });
        return true;
      } catch (error) {
        console.error(
          `Failed to delete organization ${orgID}:`,
          error.response ? error.response.data : error.message,
        );
        throw new Error("Failed to delete organization");
      }
    },
    async addUserToOrganization({ state }, { orgID, email }) {
      try {
        const params = new URLSearchParams();
        params.append("email", email);
        const response = await axios.post(
          `/api/orgs/${orgID}/addUser`,
          params,
          {
            headers: {
              Authorization: `Bearer ${state.authToken}`,
            },
          },
        );
        return response.data;
      } catch (error) {
        console.error(
          "Error adding user to organization: ",
          error.response?.data || error.message,
        );
        throw error;
      }
    },
    async removeUserFromOrganization({ state }, { orgID, email }) {
      try {
        const params = new URLSearchParams();
        params.append("email", email);
        const response = await axios.post(
          `/api/orgs/${orgID}/removeUser`,
          params,
          {
            headers: {
              Authorization: `Bearer ${state.authToken}`,
            },
          },
        );
        return response.data;
      } catch (error) {
        console.error(
          "Failed to remove user from organization: ",
          error.response?.data || error.message,
        );
        throw error;
      }
    },
    async fetchOrgMembers({ state }, orgID) {
      try {
        const response = await axios.get(`/api/orgs/${orgID}/members`, {
          headers: {
            Authorization: `Bearer ${state.authToken}`,
          },
        });

        const users =
          typeof response.data === "string"
            ? JSON.parse(response.data)
            : response.data;

        if (!Array.isArray(users)) {
          throw new Error("Expected an array of users");
        }
        return users;
      } catch (error) {
        console.error("Error fetching organization members:", error);
        throw error;
      }
    },
    async fetchAllUsers({ state }) {
      try {
        const response = await axios.get("/api/users/getAllUsers", {
          headers: {
            Authorization: `Bearer ${state.authToken}`,
            "Content-Type": "application/json",
          },
        });

        return response.data;
      } catch (error) {
        console.error("Error fetching all users:", error);
        throw error;
      }
    },
    async createProject({ state }, project) {
      try {
        const params = new URLSearchParams();
        params.append("projectName", project.projName);
        params.append("projectDescription", project.projDescription);
        params.append("projectOwnerID", project.projCreator);
        params.append("parentOrgID", project.parentOrgID);
        params.append("encodedImage", project.projImg);

        await axios.post("/api/projects/createProject", params, {
          headers: {
            Authorization: `Bearer ${state.authToken}`,
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
    async deleteProject({ state }, { projectID }) {
      try {
        await axios.delete(`/api/projects/${projectID}/deleteProject`, {
          headers: {
            Authorization: `Bearer ${state.authToken}`,
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
              console.error(
                "Unauthorized to delete project with ID:",
                projectID,
              );
              break;
            default:
              console.error("Failed to delete project with ID:", projectID);
              break;
          }
        }
      }
    },
    async modifyProject({ state }, { project, projectID }) {
      try {
        const params = new URLSearchParams();
        params.append("projectName", project.projectName);
        params.append("projectDescription", project.projectDescription);
        params.append("projectOwnerID", project.projectOwnerID);
        params.append("encodedImage", project.encodedImage);
        await axios.post(`/api/projects/${projectID}/update`, params, {
          headers: {
            "Content-Type": "application/x-www-form-urlencoded",
            Authorization: `Bearer ${state.authToken}`,
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
    async fetchProject({ state }, projID) {
      try {
        const response = await axios.get(`/api/projects/${projID}/getProject`, {
          headers: {
            Authorization: `Bearer ${state.authToken}`,
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
    async fetchProjectMembers({ state }, projectID) {
      try {
        const response = await axios.get(
          `/api/projects/${projectID}/getUsers`,
          {
            headers: {
              Authorization: `Bearer ${state.authToken}`,
            },
          },
        );
        return response.data;
      } catch (error) {
        console.error(
          "Error fetching project members: ",
          error.response?.data || error.message,
        );
        throw error;
      }
    },
    async removeUserFromProject({ state }, { projectID, email }) {
      try {
        const params = new URLSearchParams();
        params.append("email", email);

        const response = await axios.post(
          `/api/projects/${projectID}/removeUser`,
          params,
          {
            headers: {
              Authorization: `Bearer ${state.authToken}`,
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
    async addUserToProject({ state }, { projectID, email }) {
      try {
        const params = new URLSearchParams();
        params.append("email", email);
        const response = await axios.post(
          `/api/projects/${projectID}/addUser`,
          params,
          {
            headers: {
              Authorization: `Bearer ${state.authToken}`,
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
  },
});
