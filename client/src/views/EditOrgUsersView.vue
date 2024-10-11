<template>
  <div
    v-if="isLoggedIn"
    class="relative w-full h-full min-h-screen bg-background"
  >
    <NavBar />
    <div class="">
      <div class="flex flex-col justify-center h-screen/3 py-16">
        <div class="text-4xl font-bold text-primary text-center py-8">
          Edit Organization
        </div>
        <div class="text-4xl font-bold text-red-500 text-center py-8">
          Can't implement until server is completed
        </div>
        <div class="h-1 bg-accent rounded-lg"></div>
        <div class="py-16">
          <div
            class="relative flex flex-col justify-items-center p-16 mx-96 rounded-lg bg-white drop-shadow-lg"
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
              :users="currentUsers"
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
      currentUsers: [],
      showAddUsers: true,
    };
  },
  components: {
    NavBar,
    AvailableUsersTable,
    CurrentUsersTable,
  },
  computed: {
    ...mapState(["isLoggedIn", "currentUser", "organizations"]),
  },
  created() {
    const organization = this.organizations[this.$route.params.orgIndex];
    if (organization) {
      this.currentUsers = organization.members;
      this.availableUsers = this.$store.state.userAccounts.filter(
        (user) => !organization.members.includes(user.email),
      );
    }
  },
  methods: {
    async addUser(userEmail) {
      const organizationID = this.$route.params.orgIndex;
      await this.$store
        .dispatch("addUserToOrganization", {
          orgIndex: organizationID,
          userEmails: userEmail,
        })
        .then(() => {
          this.currentUsers.push(userEmail);
        })
        .catch((error) => {
          alert(error);
        });
    },
    async removeUser(userEmail) {
      const organizationID = this.$route.params.orgIndex;
      await this.$store
        .dispatch("removeUserFromOrganization", {
          orgIndex: organizationID,
          userEmail: userEmail,
        })
        .then(() => {
          this.currentUsers = this.currentUsers.filter(
            (user) => user !== userEmail,
          );
        })
        .catch((error) => {
          alert(error);
        });
    },
    toggleTable() {
      this.showAddUsers = !this.showAddUsers;
    },
  },
};
</script>
