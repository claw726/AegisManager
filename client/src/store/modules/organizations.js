import axios from "@/utils/axios.js";

const actions = {
  async fetchOrganizations({ rootState }) {
    try {
      const response = await axios.get("/api/orgs/getAllOrgs", {
        headers: {
          Authorization: `Bearer ${rootState.auth.authToken}`,
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
  async fetchOrganization({ rootState }, orgID) {
    try {
      const response = await axios.get(`/api/orgs/${orgID}/getOrg`, {
        headers: {
          Authorization: `Bearer ${rootState.auth.authToken}`,
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
  async createOrganization({ rootState }, organization) {
    try {
      const params = new URLSearchParams();
      params.append("orgName", organization.orgName);
      params.append("orgDescription", organization.orgDescription);
      params.append("orgOwnerID", organization.orgOwnerID);
      params.append("encodedImage", organization.OrgLogo);

      const response = await axios.post("/api/orgs/createOrg", params, {
        headers: {
          Authorization: `Bearer ${rootState.auth.authToken}`,
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
  async modifyOrganization({ rootState }, { orgID, organization }) {
    try {
      const params = new URLSearchParams();
      params.append("orgName", organization.orgName);
      params.append("orgDescription", organization.orgDescription);
      params.append("orgOwnerID", organization.orgOwnerID);
      params.append("encodedImage", organization.OrgLogo);
      const response = await axios.post(`/api/orgs/${orgID}/update`, params, {
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
          Authorization: `Bearer ${rootState.auth.authToken}`,
        },
      });
      return response.data;
    } catch (error) {
      console.error("Failed to modify organization:", error.response.data);
      throw new Error("Failed to modify organization");
    }
  },
  async deleteOrganization({ rootState }, orgID) {
    try {
      await axios.delete(`/api/orgs/${orgID}/deleteOrg`, {
        headers: {
          Authorization: `Bearer ${rootState.auth.authToken}`,
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
  async addUserToOrganization({ rootState }, { orgID, email }) {
    try {
      const params = new URLSearchParams();
      params.append("email", email);
      const response = await axios.post(`/api/orgs/${orgID}/addUser`, params, {
        headers: {
          Authorization: `Bearer ${rootState.auth.authToken}`,
        },
      });
      return response.data;
    } catch (error) {
      console.error(
        "Error adding user to organization: ",
        error.response?.data || error.message,
      );
      throw error;
    }
  },
  async removeUserFromOrganization({ rootState }, { orgID, email }) {
    try {
      const params = new URLSearchParams();
      params.append("email", email);
      const response = await axios.post(
        `/api/orgs/${orgID}/removeUser`,
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
        "Failed to remove user from organization: ",
        error.response?.data || error.message,
      );
      throw error;
    }
  },
  async fetchOrgMembers({ rootState }, orgID) {
    try {
      const response = await axios.get(`/api/orgs/${orgID}/members`, {
        headers: {
          Authorization: `Bearer ${rootState.auth.authToken}`,
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
  // Other organization-related actions
};

export default {
  namespaced: true,
  actions,
};
