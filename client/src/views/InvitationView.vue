<template>
  <div class="min-h-screen bg-gray-50">
    <NavBar />

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- Header Section -->
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-900 flex items-center">
          <i class="fas fa-envelope-open-text mr-3 text-blue-600"></i>
          Invitations & Notifications
        </h1>
        <p class="mt-2 text-sm text-gray-600">
          Manage your pending invitations and notifications
        </p>
      </div>

      <!-- Notification Component -->
      <NotificationComponent
        class="mb-6"
        :show="notification.show"
        :type="notification.type"
        @close="closeNotification"
      >
        {{ notification.message }}
      </NotificationComponent>

      <!-- Invitations List -->
      <div class="space-y-4">
        <div
          v-for="invitation in invitations"
          :key="invitation.id"
          class="bg-white rounded-xl shadow-sm border border-gray-200 hover:shadow-md transition-shadow duration-200"
        >
          <div class="p-6">
            <div class="flex items-center justify-between">
              <!-- Invitation Content -->
              <div class="flex-1 min-w-0">
                <div class="flex items-center mb-2">
                  <!-- Invitation Type Icon -->
                  <span
                    class="inline-flex items-center justify-center w-10 h-10 rounded-full mr-3"
                    :class="{
                      'bg-blue-100 text-blue-600': invitation.invitationType === 2,
                      'bg-green-100 text-green-600': invitation.invitationType === 1,
                      'bg-purple-100 text-purple-600': invitation.invitationType === 3,
                      'bg-yellow-100 text-yellow-600': invitation.invitationType === 4,
                      'bg-gray-100 text-gray-600': invitation.invitationType === 0
                    }"
                  >
                    <i
                      :class="{
                        'fas fa-building': invitation.invitationType === 2,
                        'fas fa-tasks': invitation.invitationType === 1,
                        'fas fa-project-diagram': invitation.invitationType === 3,
                        'fas fa-clipboard-check': invitation.invitationType === 4,
                        'fas fa-bell': invitation.invitationType === 0
                      }"
                    ></i>
                  </span>

                  <!-- Invitation Details -->
                  <div class="flex-1 min-w-0">
                    <p class="text-lg font-semibold text-gray-900 truncate">
                      {{ invitation.source }}
                    </p>
                    <div class="flex items-center text-sm text-gray-500 mt-1">
                      <i class="fas fa-user mr-2"></i>
                      <span>{{ invitation.fromUser }}</span>
                      <span class="mx-2">•</span>
                      <span
                        class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                        :class="{
                          'bg-blue-100 text-blue-800': invitation.invitationType === 2,
                          'bg-green-100 text-green-800': invitation.invitationType === 1,
                          'bg-purple-100 text-purple-800': invitation.invitationType === 3,
                          'bg-yellow-100 text-yellow-800': invitation.invitationType === 4,
                          'bg-gray-100 text-gray-800': invitation.invitationType === 0
                        }"
                      >
                        {{ convertNumberToType(invitation.invitationType) }}
                      </span>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Action Buttons -->
              <div class="ml-4 flex-shrink-0">
                <div v-if="invitation.invitationType !== 0" class="flex space-x-3">
                  <button
                    class="inline-flex items-center px-3 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 transition-colors duration-200"
                    @click="acceptInvitation(invitation.id)"
                  >
                    <i class="fas fa-check mr-2"></i>
                    Accept
                  </button>
                  <button
                    class="inline-flex items-center px-3 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-red-600 hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 transition-colors duration-200"
                    @click="rejectInvitation(invitation.id)"
                  >
                    <i class="fas fa-times mr-2"></i>
                    Reject
                  </button>
                </div>
                <div v-else>
                  <button
                    class="inline-flex items-center px-3 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-colors duration-200"
                    @click="markAsRead(invitation.id)"
                  >
                    <i class="fas fa-check-double mr-2"></i>
                    Mark as Read
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div
          v-if="invitations.length === 0"
          class="text-center py-12 bg-white rounded-xl shadow-sm border border-gray-200"
        >
          <i class="fas fa-inbox text-gray-400 text-5xl mb-4"></i>
          <h3 class="text-lg font-medium text-gray-900 mb-2">No Invitations</h3>
          <p class="text-gray-500">You don't have any pending invitations or notifications.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import NavBar from "../components/NavBar.vue";
import { mapState } from "vuex";
import NotificationComponent from "@/components/NotificationComponent.vue";

export default {
  name: "InvitationsPage",
  components: {
    NavBar,
    NotificationComponent,
  },
  data() {
    return {
      invitations: [],
      notification: {
        show: false,
        type: "success",
        message: "",
      },
    };
  },
  computed: {
    ...mapState("auth", ["isLoggedIn", "currentUser"]),
  },
  async created() {
    await this.getRecipientInvitations();
  },
  methods: {
    async acceptInvitation(id) {
      try {
        console.log(id);
        await this.$store.dispatch("invitations/accept", id);
        this.showNotification("success", "Invitation accepted successfully!");
        this.getRecipientInvitations();
      } catch (error) {
        this.showNotification("error", "Error accepting invitation");
      }
    },
    async rejectInvitation(id) {
      try {
        await this.$store.dispatch("invitations/reject", id);
        this.showNotification("success", "Invitation rejected successfully!");
        this.getRecipientInvitations();
      } catch (error) {
        this.showNotification("error", "Error rejecting invitation");
      }
    },
    async markAsRead(id) {
      try {
        await this.$store.dispatch("invitations/reject", id);
        this.showNotification("success", "Notification marked as read!");
        this.getRecipientInvitations();
      } catch (error) {
        this.showNotification("error", "Error marking invitation as read");
      }
    },
    async getRecipientInvitations() {
      try {
        const rawInvitations = await this.$store.dispatch(
          "invitations/getRecipientInvitations",
          this.currentUser.userID
        );
        console.log(rawInvitations);
        this.invitations = rawInvitations.map((invitation) => ({
          id: invitation.invitationID,
          source: invitation.message,
          fromUser: invitation.senderEmail,
          invitationType: invitation.invitationType,
        }));
      } catch (error) {
        this.showNotification("error", "Error getting invitations");
      }
    },
    convertNumberToType(invitationType) {
      if (invitationType === 1) {
        return "Task Assigner Delegation";
      }
      else if (invitationType === 2) {
        return "Organization Invite"
      }
      else if (invitationType === 3) {
        return "Project Invite"
      }
      else if (invitationType === 4) {
        return "Task Invite"
      }
      return "Notification"
    },
    showNotification(type, message) {
      this.notification = {
        show: true,
        type,
        message,
      };

      if (type == "success") {
        setTimeout(this.closeNotification, 5000);
      }
    },
    closeNotification() {
      this.notification.show = false;
    },
  },
};
</script>

<style scoped>
/* Animation for notification badges */
@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: .5;
  }
}

.notification-badge {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}
</style>