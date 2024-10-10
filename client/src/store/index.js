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
    clearAuth(state) {
      state.currentUser = null;
      state.authToken = null;
    },
    setLogin(state, isLoggedIn) {
      state.isLoggedIn = isLoggedIn;
    },
    addOrganization(state, organization) {
      state.organizations.push(organization);
    },
    removeOrganization(state, index) {
      if (index >= 0 && index < state.organizations.length) {
        state.organizations.splice(index, 1);
      } else {
        console.error("Invalid organization index:", index);
        throw new Error("Invalid organization index");
      }
    },
    modifyOrganization(state, { index, organization }) {
      if (index >= 0 && index < state.organizations.length) {
        state.organizations[index] = organization;
      } else {
        console.error("Invalid organization index:", index);
        throw new Error("Invalid organization index");
      }
    },
    addUserToOrganization(state, { orgIndex, userEmails }) {
      // Check that index matches, then that userEmail is an existing email in the useraccounts state, and that each userEmail in userEmails is not already in the org
      if (orgIndex >= 0 && orgIndex < state.organizations.length) {
        const organization = state.organizations[orgIndex];
        organization.members = organization.members || [];
        userEmails.forEach((userEmail) => {
          if (
            state.userAccounts.includes(userEmail) &&
            !organization.members.includes(userEmail)
          ) {
            organization.members.push(userEmail);
          } else {
            console.error(
              "User not found or already in organization:",
              userEmail,
            );
            throw new Error("User not found or already in organization");
          }
        });
        state.organizations[orgIndex] = organization;
      }
    },
    removeUserFromOrganization(state, { orgIndex, userEmail }) {
      // Check if org index is valid and that the userEmail exists in the userAccounts state
      if (orgIndex >= 0 && orgIndex < state.organizations.length) {
        const organization = state.organizations[orgIndex];
        organization.members = organization.members || [];
        const userIndex = organization.members.indexOf(userEmail);
        if (userIndex >= 0) {
          organization.members.splice(userIndex, 1);
          state.organizations[orgIndex] = organization;
        } else {
          console.error("User not found in organization:", userEmail);
          throw new Error("User not found in organization");
        }
      } else {
        console.error("Invalid organization index:", orgIndex);
        throw new Error("Invalid organization index:" + orgIndex);
      }
    },

    addProject(state, { orgIndex, project }) {
      if (orgIndex >= 0 && orgIndex < state.organizations.length) {
        state.organizations[orgIndex].projects =
          state.organizations[orgIndex].projects || [];
        state.organizations[orgIndex].projects.push(project);
      } else {
        console.error("Invalid organization index:", orgIndex);
        throw new Error("Invalid organization index");
      }
    },
    deleteProject(state, { orgIndex, projIndex }) {
      state.organizations[orgIndex].projects.splice(projIndex, 1);
    },
    modifyProject(state, { orgIndex, projIndex, project }) {
      if (orgIndex && orgIndex >= 0 && orgIndex < state.organizations.length) {
        const organization = state.organizations[orgIndex];
        if (
          organization &&
          organization.projects &&
          projIndex >= 0 &&
          projIndex < organization.projects.length
        ) {
          state.organizations[orgIndex].projects[projIndex] = project;
        } else {
          console.error("Invalid project index:", projIndex);
          throw new Error("Invalid project index");
        }
      } else {
        console.error("Invalid organization index:", orgIndex);
        throw new Error("Invalid organization index");
      }
    },
  },
  actions: {
    async register({ dispatch }, { email, name, password }) {
      try {
        const params = new URLSearchParams();
        params.append("email", email);
        params.append("name", name);
        params.append("password", password);

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
    async login({ commit }, { email, password }) {
      try {
        const params = new URLSearchParams();
        params.append("email", email);
        params.append("password", password);

        const response = await axios.post("/api/auth/login", params, {
          headers: {
            "Content-Type": "application/x-www-form-urlencoded",
          },
        });

        const token = response.data.token;
        commit("setAuthToken", token);
        commit("setCurrentUser", { email });
        commit("setLogin", true);
        alert("Login successful");
      } catch (error) {
        console.error("Failed to login:", error.response.data);
        alert("Login failed: " + error.response.data);
        throw error;
      }
    },
    async logout({ commit }) {
      commit("clearAuth");
      commit("setLogin", false);
    },
    async createOrganization({ commit }, organization) {
      commit("addOrganization", organization);
    },
    async modifyOrganization({ commit }, { index, organization }) {
      commit("modifyOrganization", { index, organization });
    },
    async deleteOrganization({ commit }, index) {
      commit("removeOrganization", index);
    },
    async addUserToOrganization({ commit }, { orgIndex, userEmails }) {
      commit("addUserToOrganization", { orgIndex, userEmails });
    },
    async removeUserFromOrganization({ commit }, { orgIndex, userEmail }) {
      commit("removeUserFromOrganization", { orgIndex, userEmail });
    },
    async createProject({ commit }, { orgIndex, project }) {
      commit("addProject", { orgIndex, project });
    },
    async deleteProject({ commit }, { orgIndex, projIndex }) {
      commit("deleteProject", { orgIndex, projIndex });
    },
    async modifyProject({ commit }, { orgIndex, projIndex, project }) {
      commit("modifyProject", { orgIndex, projIndex, project });
    },
  },
});
