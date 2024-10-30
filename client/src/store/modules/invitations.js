import axios from "@/utils/axios.js";

const actions = {
  async createInvitation({ rootState }, data) {
    try {
      const response = await axios.post(`/api/invitations/createInvitation?senderEmail=${data.senderEmail}&recipientEmail=${data.recipientEmail}&invitationType=${data.invitationType}&message=${data.message}`, {
        headers: {
          Authorization: `Bearer ${rootState.auth.authToken}`,
        },
      });
      return response.data;
    } catch (error) {
      console.error("Failed to create invitation")
      throw new error("Failed to create invitation")
    }
  },
  async getRecipientInvitations({ rootState }, userID) {
    try {
      const response = await axios.get(`/api/invitations/getRecipientInvitations?userID=${userID}`, {
        headers: {
          Authorization: `Bearer ${rootState.auth.authToken}`,
        },
      });
      return response.data;
    } catch (error) {
      console.error("Failed to fetch invitations", error.response.data);
      throw new Error("Failed to fetch invitations");
    }
  },
  async accept({ rootState }, invitationID) {
    try {
      const response = await axios.post(`/api/invitations/${invitationID}/accept`, {
        headers: {
          Authorization: `Bearer ${rootState.auth.authToken}`,
        },
      });
      return response.data;
    } catch (error) {
      console.log(error);
      console.error("Failed to accept invitations", error.response.data);
      throw new Error("Failed to accept invitation");
    }
  },
  async reject({ rootState }, invitationID) {
    try {
      const response = await axios.post(`/api/invitations/${invitationID}/reject`, {
        headers: {
          Authorization: `Bearer ${rootState.auth.authToken}`,
        },
      });
      return response.data;
    } catch (error) {
      console.error("Failed to reject invitation", error.response.data);
      throw new Error("Failed to reject invitation");
    }
  },
};


export default {
  namespaced: true,
  actions,
};
