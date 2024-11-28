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
          <h1 class="text-3xl font-bold text-gray-900">Task User Management</h1>
          <p class="mt-2 text-gray-600">Add or remove users from this task</p>
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
                  : 'text-gray-500 hover:text-gray-700 hover:bg-gray-50'
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
                  : 'text-gray-500 hover:text-gray-700 hover:bg-gray-50'
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
                v-model="searchQuery" 
                type="text" 
                :placeholder="showAddUsers 
                  ? 'Search project members to add...' 
                  : 'Search current task members...'"
                class="w-full pl-10 pr-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500" 
              />
            </div>
          </div>

          <!-- User Lists -->
          <div class="p-4">
            <div v-if="showAddUsers">
              <AvailableUsersTable 
                :users="filteredAvailableUsers" 
                @addUser="sendInvite" 
              />
            </div>
            <div v-else>
              <CurrentUsersTable 
                :users="filteredTaskMembers" 
                @removeUser="removeUser" 
              />
            </div>
          </div>
        </div>
      </div>

      <!-- Notification -->
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
import NavBar from "@/components/NavBar.vue";
import { mapState } from "vuex";
import AvailableUsersTable from "@/components/AvailableUsersTable.vue";
import CurrentUsersTable from "@/components/CurrentUsersTable.vue";
import NotificationComponent from "@/components/NotificationComponent.vue";

export default {
  components: {
    NavBar,
    NotificationComponent,
    AvailableUsersTable,
    CurrentUsersTable,
    NotificationComponent,
  },

  data() {
    return {
      fetchedTask: null,
      projectMembers: [],
      taskMembers: [],
      showAddUsers: true,
      searchQuery: "",
      taskID: "",

      notification: {
        show: false,
        type: "info",
        message: "",
      },
    };
  },

  computed: {
    ...mapState("auth", ["isLoggedIn", "currentUser"]),
    filteredAvailableUsers() {
      // Get the emails of current task members for comparison
      const taskMemberEmails = this.fetchedTask?.assignedUsers.map(user => user.email) || [];

      if (!this.searchQuery) {
        // Filter project members who aren't in this task and aren't the current user
        return this.projectMembers.filter(user =>
          !taskMemberEmails.includes(user.email) &&
          user.email !== this.currentUser.email
        );
      }

      const query = this.searchQuery.toLowerCase();
      return this.projectMembers.filter(user =>
        !taskMemberEmails.includes(user.email) &&
        user.email !== this.currentUser.email &&
        (user.userName.toLowerCase().includes(query) ||
          user.email.toLowerCase().includes(query))
      );
    },

    filteredTaskMembers() {
      if (!this.fetchedTask?.assignedUsers) return [];

      if (!this.searchQuery) {
        return this.fetchedTask.assignedUsers.filter(user =>
          user.email !== this.currentUser.email
        );
      }

      const query = this.searchQuery.toLowerCase();
      return this.fetchedTask.assignedUsers.filter(user =>
        user.email !== this.currentUser.email &&
        (user.userName.toLowerCase().includes(query) ||
          user.email.toLowerCase().includes(query))
      );
    },
  },

  async mounted() {
    this.taskID = this.$route.params.taskId;
    await this.initializeData()
  },

  methods: {

    async initializeData() {
      try {
        if (!this.taskID) {
          this.showNotification("error", "taskId is null");
          return;
        }
        // fetch task details
        this.fetchedTask = await this.$store.dispatch("tasks/fetchTask", this.taskID);

        // Fetch project members using the task's parentProjectID
        this.projectMembers = await this.$store.dispatch("projects/fetchProjectMembers", this.fetchedTask.parentProjectID);

      } catch (error) {
        console.error("Failed to load task user data: ", error);
        this.showNotification("error", "Failed to Load Task User Data")
      }
    },

    async sendInvite(email) {
      try {
        const data = {
          senderEmail: this.currentUser.email,
          recipientEmail: email,
          invitationType: 4,
          message: `${this.fetchedTask.taskID}: Task Addition Request - ${this.fetchedTask.taskName}`,
        };

        // Call the action and handle success
        await this.$store.dispatch("invitations/createInvitation", data);
        this.showNotification("success", "Successfully sent task invite!");

        return true;  // Return true if invitation is successfully sent
      } catch (error) {
        // Catch the error thrown from createInvitation and display the message
        this.showNotification("error", error.message || "An unexpected error occurred.");
        return false;
      }
    },

    async removeUser(email) {
      try {
  
        await this.$store.dispatch("tasks/removeUserToTask", {
          taskId: this.taskID,
          email: email,
        });
        this.showNotification("success", `Successfully removed ${email} from task`);
        await this.initializeData();
      } catch (error) {
        this.showNotification("error", `Failed to remove user: ${error.message}`);
      }
    },

    async addUser(email) {
      try {
        const s = await this.$store.dispatch("tasks/addUserToTask", {
          email: email,
          taskId: this.fetchedTask.taskID,
        });

        return s;
      } catch (error) {
        console.error("Failed to add user PEEEEEE to task", error);
      }
    },

    goBack() {
      this.$router.go(-1);
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
.cc {
  text-align: center;
  padding-top: 50px;
  display: flex;
  justify-content: center;
  /* Center horizontally */
  align-items: center;
  /* Center vertically */
}
</style>