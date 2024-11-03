<template>
  <div v-if="isLoggedIn" class="min-h-screen bg-background relative">
    <NavBar />

    <!-- Main Content -->
    <div class="container mx-auto px-4 py-8">
      <!-- Back Button -->
      <button
        @click="$router.back()"
        class="mb-6 inline-flex items-center px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md shadow-sm hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-colors"
      >
        <i class="fas fa-arrow-left mr-2"></i>
        Back
      </button>

      <div class="flex flex-col space-y-6">
        <!-- Header -->
        <div class="text-center">
          <h1 class="text-3xl font-bold text-gray-900">
            Organization Users Management
          </h1>
          <p class="mt-2 text-gray-600">
            Add or remove users from your organization
          </p>
        </div>

        <!-- Main Card -->
        <div class="bg-white rounded-xl shadow-lg overflow-hidden">
          <!-- Toggle Tabs -->
          <div class="flex border-b">
            <button
              @click="showAddUsers = true"
              :class="[
                'flex-1 px-6 py-4 text-sm font-medium focus:outline-none transition-colors',
                showAddUsers
                  ? 'text-blue-600 border-b-2 border-blue-600 bg-blue-50'
                  : 'text-gray-500 hover:text-gray-700 hover:bg-gray-50',
              ]"
            >
              <i class="fas fa-user-plus mr-2"></i>
              Add Users
            </button>
            <button
              @click="showAddUsers = false"
              :class="[
                'flex-1 px-6 py-4 text-sm font-medium focus:outline-none transition-colors',
                !showAddUsers
                  ? 'text-blue-600 border-b-2 border-blue-600 bg-blue-50'
                  : 'text-gray-500 hover:text-gray-700 hover:bg-gray-50',
              ]"
            >
              <i class="fas fa-user-minus mr-2"></i>
              Remove Users
            </button>
          </div>

          <!-- Search Bar -->
          <div class="p-4 border-b">
            <div class="relative">
              <i class="fas fa-search absolute left-3 top-3 text-gray-400"></i>
              <input
                type="text"
                v-model="searchQuery"
                :placeholder="
                  showAddUsers
                    ? 'Search available users...'
                    : 'Search current members...'
                "
                class="w-full pl-10 pr-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              />
            </div>
          </div>

          <!-- User Lists -->
          <div class="p-4">
            <div v-if="showAddUsers">
              <AvailableUsersTable
                :users="filteredAvailableUsers"
                @addUser="addUser"
              />
            </div>
            <div v-else>
              <CurrentUsersTable
                :users="filteredMembers"
                @removeUser="removeUser"
              />
            </div>
          </div>
        </div>
      </div>

      <!-- Container for notification -->
      <div
        class="fixed inset-x-0 bottom-0 pb-4 sm:pb-6 mx-auto px-4 sm:px-6 md:px-8"
        style="max-width: 500px"
      >
        <NotificationComponent
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
      searchQuery: "",
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
    filteredAvailableUsers() {
      if (!this.searchQuery) return this.availableUsers;
      const query = this.searchQuery.toLowerCase();
      return this.availableUsers.filter(
        (user) =>
          user.userName.toLowerCase().includes(query) ||
          user.email.toLowerCase().includes(query)
      );
    },
    filteredMembers() {
      if (!this.searchQuery) return this.members;
      const query = this.searchQuery.toLowerCase();
      return this.members.filter(
        (user) =>
          user.userName.toLowerCase().includes(query) ||
          user.email.toLowerCase().includes(query)
      );
    },
  },
  methods: {
    async addUser(email) {
      try {
        const orgID = this.$route.params.orgIndex;
        await this.$store.dispatch("organizations/addUserToOrganization", {
          orgID,
          email,
        });
        this.showNotification(
          "success",
          `Successfully added ${email} to organization`
        );
        await this.fetchOrgMembers();
      } catch (error) {
        this.showNotification(
          "error",
          `Failed to add user: ${error.response?.data || error.message}`
        );
      }
    },
    async removeUser(email) {
      try {
        const orgID = this.$route.params.orgIndex;
        await this.$store.dispatch("organizations/removeUserFromOrganization", {
          orgID,
          email,
        });
        this.showNotification(
          "success",
          `Successfully removed ${email} from organization`
        );
        await this.fetchOrgMembers();
      } catch (error) {
        this.showNotification(
          "error",
          `Failed to remove user: ${error.response?.data || error.message}`
        );
      }
    },
    showNotification(type, message) {
      this.notification = {
        show: true,
        type,
        message,
      };
      setTimeout(this.closeNotification, 5000);
    },
    closeNotification() {
      this.notification.show = false;
    },
    async fetchOrgMembers() {
      try {
        const orgID = this.$route.params.orgIndex;
        if (!orgID) {
          throw new Error("Organization ID is not available!");
        }

        this.members = await this.$store.dispatch(
          "organizations/fetchOrgMembers",
          orgID
        );
        const allUsers = await this.$store.dispatch("users/fetchAllUsers");

        const memberIDs = this.members.map((member) => member.userID);
        this.availableUsers = allUsers.filter(
          (user) => !memberIDs.includes(user.userID)
        );
      } catch (error) {
        this.showNotification(
          "error",
          "Failed to load users. Please try again later."
        );
      }
    },
  },
  mounted() {
    this.fetchOrgMembers();
  },
};
</script>
