<template>
  <div
    v-if="isLoggedIn"
    class="relative w-full h-full min-h-screen bg-background"
  >
    <NavBar />
    <div class="">
      <div class="flex flex-col justify-center py-16">
        <div class="text-4xl font-bold text-primary text-center py-8">
          Edit Organization Users
        </div>
        <div class="h-1 bg-accent rounded-lg"></div>
        <div class="py-16">
          <div
            class="relative flex flex-col justify-items-center p-16 mx-24 rounded-lg bg-white drop-shadow-lg"
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
      </div>
    </div>
  </div>
</template>

<script>
import NavBar from "@/components/NavBar.vue";
import { mapState } from "vuex";
import AvailableUsersTable from "@/components/AvailableUsersTable.vue";
import CurrentUsersTable from "@/components/CurrentUsersTable.vue";

export default {
  data() {
    return {
      availableUsers: [],
      members: [],
      showAddUsers: true,
    };
  },
  components: {
    NavBar,
    AvailableUsersTable,
    CurrentUsersTable,
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
