<template>
  <div
    v-if="isLoggedIn"
    class="relative w-full h-full min-h-screen bg-background"
  >
    <NavBar />
    <div class="">
      <div class="flex flex-col justify-center h-screen/2 py-16">
        <div class="text-4xl font-bold text-primary text-center py-8">
          Edit Project Users
        </div>
        <div class="h-1 bg-accent rounded-lg"></div>
        <div class="py-16">
          <div
            class="relative flex flex-col justify-full items-stretch p-16 mx-24 rounded-lg bg-white drop-shadow-lg"
          >
            <!-- Button to toggle between Add Users and Remove Users tables It is in a div that is centered, but only 1/3 width of the parent -->
            <div class="flex justify-center">
              <button @click="toggleTable" class="px-12 dashboard-button">
                {{ showAddUsers ? "Add Users" : "Remove Users" }}
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
    this.fetchProjMembers();
  },
  methods: {
    async addUser(email) {
      try {
        const projectID = this.$route.params.projIndex;
        console.log(`Adding ${email} to proj ${projectID}`);
        const message = await this.$store.dispatch(
          "projects/addUserToProject",
          {
            projectID,
            email,
          },
        );
        alert(message);
        this.fetchProjMembers();
      } catch (error) {
        this.showNotification(
          "error",
          "Failed to add user to Project: " +
            (error.response?.data || error.message),
        );
        );
      }
    },
    async removeUser(email) {
      try {
        const projectID = this.$route.params.projIndex;
        const message = await this.$store.dispatch(
          "projects/removeUserFromProject",
          {
            projectID,
            email,
          },
        );
        alert(message);
        this.fetchProjMembers();
      } catch (error) {
        this.showNotification(
          "error",
          "Failed to remove member from Project: " +
            (error.response?.data || error.message),
        );
        );
      }
    },
    toggleTable() {
      this.showAddUsers = !this.showAddUsers;
    },
    async fetchProjMembers() {
      try {
        const orgID = this.$route.params.orgIndex;
        const projID = this.$route.params.projIndex;
        if (!projID || !orgID) {
          this.showNotification("error", "Project or Org does not exist!");
        }

        // Fetch proj members
        this.members = await this.$store.dispatch(
          "projects/fetchProjectMembers",
          projID,
        );
        console.log("Organization Members:", this.members);

        // Fetch all org users
        const allUsers = await this.$store.dispatch(
          "organizations/fetchOrgMembers",
          orgID,
        );
        console.log("All org Users:", allUsers);

        // Filter out users who are not a member of this project
        const memberIDs = this.members.map((member) => member.userID);
        this.availableUsers = allUsers.filter(
          (user) => !memberIDs.includes(user.userID),
        );
      } catch (error) {
        console.error("Error fetching project members:", error.message);
        this.showNotification("error", "Failed to fetch users");
      }
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
