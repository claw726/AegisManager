import axios from "@/utils/axios.js";

const actions = {
  async createInvitation({ rootState }, data) {
    try {
      const response = await axios.post(
        `/api/invitations/createInvitation?senderEmail=${data.senderEmail}&recipientEmail=${data.recipientEmail}&invitationType=${data.invitationType}&message=${data.message}`,
        {
          headers: {
            Authorization: `Bearer ${rootState.auth.authToken}`,
          },
        },
      );
      return response.data;
    } catch (error) {
      // Initialize a default error message
      let errorMessage = "An unexpected error occurred. Please try again.";

      if (error.response) {
        // Customize the message based on the status code
        switch (error.response.status) {
          case 409:
            errorMessage = "This invitation has already been sent to the user.";
            break;
          case 404:
            errorMessage = "User not found.";
            break;
          case 400:
            errorMessage = "Cannot send an invitation to yourself.";
            break;
          default:
            errorMessage = error.response.data || errorMessage;
        }
      } else if (error.request) {
        // Handle network issues
        errorMessage = "Error connecting to the server.";
      }

      // Throw an error with the processed message
      throw new Error(errorMessage);
    }
  },
  async getRecipientInvitations({ rootState }, userID) {
    try {
      const response = await axios.get(
        `/api/invitations/getRecipientInvitations?userID=${userID}`,
        {
          headers: {
            Authorization: `Bearer ${rootState.auth.authToken}`,
          },
        },
      );
      return response.data;
    } catch (error) {
      console.error("Failed to fetch invitations", error.response.data);
      throw new Error("Failed to fetch invitations");
    }
  },
  async accept({ rootState }, invitationID) {
    try {
      const response = await axios.post(
        `/api/invitations/${invitationID}/accept`,
        {
          headers: {
            Authorization: `Bearer ${rootState.auth.authToken}`,
          },
        },
      );
      return response.data;
    } catch (error) {
      console.log(error);
      console.error("Failed to accept invitations", error.response.data);
      throw new Error("Failed to accept invitation");
    }
  },
  async reject({ rootState }, invitationID) {
    try {
      const response = await axios.post(
        `/api/invitations/${invitationID}/reject`,
        {
          headers: {
            Authorization: `Bearer ${rootState.auth.authToken}`,
          },
        },
      );
      return response.data;
    } catch (error) {
      console.error("Failed to reject invitation", error.response.data);
      throw new Error("Failed to reject invitation");
    }
  },
  async fetchUsersWithInvites({ rootState }, message) {
    try {
      const response = await axios.get(
        `/api/invitations/getUsersWithInvites?message=${message}`,
        {
          headers: {
            Authorization: `Bearer ${rootState.auth.authToken}`,
          },
        },
      );
      return response.data;
    } catch (error) {
      console.error("Failed to fetch invitations", error.response.data);
      throw new Error("Failed to fetch invitations");
    }
  },
};

export default {
  namespaced: true,
  actions,
};
