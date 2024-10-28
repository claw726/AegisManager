<template>
  <div v-if="isLoggedIn" class="relative w-full h-full min-h-screen bg-gray-100">
    <NavBar />
    <div class="flex flex-col items-center py-16">
      <h1 class="text-4xl font-bold text-primary text-center py-8">
        Edit Organization Users
      </h1>
      <div class="h-1 bg-accent rounded-lg w-1/4 mx-auto mb-8"></div>
      <div class="py-8 w-full max-w-4xl">
        <div class="relative flex flex-col p-8 mx-auto rounded-lg bg-white shadow-lg">
          <!-- Button to toggle between Add Users and Remove Users tables -->
          <div class="flex justify-center mb-6">
            <button
              @click="toggleTable"
              class="px-6 py-3 text-white bg-primary rounded-lg hover:bg-primary-dark transition duration-200 flex items-center"
            >
              <i :class="showAddUsers ? 'fas fa-user-plus' : 'fas fa-user-minus'"></i>
              <span class="ml-2">{{ showAddUsers ? "Add Users" : "Remove Users" }}</span>
            </button>
          </div>
          <!-- Add Users Table -->
          <AvailableUsersTable
            v-if="showAddUsers"
            :users="availableUsers"
            @addUser="addUser"
          />
          <!-- Remove Users Table -->
          <CurrentUsersTable
            v-else
            :users="members"
            @removeUser="removeUser"
          />
        </div>
      </div>
      <NotificationComponent
        class="flex"
        :show="notification.show"
        :type="notification.type"
        @close="closeNotification"
      >
        {{ notification.message }}
      </NotificationComponent>
    </div>
  </div>
</template>

<script>
import NavBar from "@/components/NavBar.vue";
import { mapState } from "vuex";
import AvailableUsersTable from "@/components/AvailableUsersTable.vue";
import CurrentUsersTable from "@/components/CurrentUsersTable.vue";
import NotificationComponent from "@/components/NotificationComponent.vue";

export default {
  data() {
    return {
      availableUsers: [],
      members: [],
      showAddUsers: true,
      notification: {
        show: false,
        type: "info",
        message: "",
      },
    };
  },
  components: {
    NavBar,
    AvailableUsersTable,
    CurrentUsersTable,
    NotificationComponent,
  },
  computed: {
    ...mapState("auth", ["isLoggedIn", "currentUser"]),
  },
  mounted() {
    this.fetchOrgMembers();
  },
  methods: {
    async addUser(email) {
      try {
        const orgID = this.$route.params.orgIndex;
        console.log(`Adding ${email} to org ${orgID}`);
        const message = await this.$store.dispatch(
          "organizations/addUserToOrganization",
          {
            orgID,
            email,
          },
        );
        alert(message);
        this.fetchOrgMembers();
      } catch (error) {
        alert(
          "Failed to add user to organization:" +
            (error.response?.data || error.message),
        );
      }
    },
    async removeUser(email) {
      try {
        const orgID = this.$route.params.orgIndex;
        const message = await this.$store.dispatch(
          "organizations/removeUserFromOrganization",
          {
            orgID,
            email,
          },
        );
        alert(message);
        this.fetchOrgMembers();
      } catch (error) {
        alert(
          "Failed to remove member from organization: " +
            (error.response?.data || error.message),
        );
      }
    },
    toggleTable() {
      this.showAddUsers = !this.showAddUsers;
    },
    async fetchOrgMembers() {
      try {
        const orgID = this.$route.params.orgIndex;
        if (!orgID) {
          throw new Error("Organization ID is not available!");
        }

        // Fetch org members
        this.members = await this.$store.dispatch(
          "organizations/fetchOrgMembers",
          orgID,
        );
        console.log("Organization Members:", this.members);

        // Fetch all users
        const allUsers = await this.$store.dispatch("users/fetchAllUsers");
        console.log("All Users:", allUsers);

        // Filter out users who are not a member of this org
        const memberIDs = this.members.map((member) => member.userID);
        this.availableUsers = allUsers.filter(
          (user) => !memberIDs.includes(user.userID),
        );
      } catch (error) {
        console.error(
          "Error fetching organization members or users:",
          error.message,
        );
        alert(
          "Failed to load organization members or available users. Please Try again later",
        );
      }
    },
  },
};
</script>