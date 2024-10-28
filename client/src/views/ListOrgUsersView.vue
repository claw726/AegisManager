<template>
  <div class="relative w-full min-h-screen h-full bg-background">
    <!-- Navbar -->
    <NavBar />

    <!-- Notification -->
    <Notification v-if="notification.show" :type="notification.type" @close="notification.show = false">
      {{ notification.message }}
    </Notification>

    <!-- Main Content: Organization and Users List -->
    <div class="flex justify-center items-center h-full px-28">
      <!-- Container for organizations and their users -->
      <div class="w-full max-w-6xl space-y-12">
        <!-- Organization Information -->
        <div class="mb-6 text-center mt-8">
          <h2 class="text-4xl font-bold text-primary">
            {{ org.orgName }}
          </h2>
          <div class="h-1 bg-brown-500 my-4"></div>
        </div>

        <!-- Users List -->
        <div>
          <h3 class="text-2xl font-semibold text-gray-800 mb-4">Members</h3>
          <ul class="space-y-4">
            <li
                v-for="user in org.users"
                :key="user.username"
                class="flex justify-between items-center bg-gray-100 p-4 rounded-lg"
            >
              <div>
                <p class="text-lg font-medium text-gray-900">
                  Name: {{ user.userName }}
                </p>
                <p class="text-sm text-gray-600">Email: {{ user.email }}</p>
              </div>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import NavBar from "@/components/NavBar.vue";
import Notification from "@/components/NotificationComponent.vue";

export default {
  name: "OrganizationUsersPage",
  components: {
    NavBar,
    Notification,
  },

  data() {
    return {
      org: null,
      notification: {
        show: false,
        type: 'error',
        message: '',
      },
    };
  },
  created() {
    this.getOrgData();
  },
  methods: {
    async getOrgData() {
      try {
        const orgID = this.$route.params.orgIndex;
        this.org = await this.$store.dispatch("organizations/fetchOrganization", orgID);
        if (!this.org) {
          throw new Error("Organization not found");
        }
      } catch (error) {
        console.error("Error fetching organization data:", error);
        this.showNotification("error", "There was an error fetching the organization data");
        this.$router.push({ name: "viewOrgs" });
      }
    },
    showNotification(type, message) {
      this.notification.type = type;
      this.notification.message = message;
      this.notification.show = true;
    },
  },
};
</script>

<style scoped>
/* Custom styles to align with homepage design */
.bg-background {
  background-color: #f9fafb; /* Light background */
}

.text-primary {
  color: #1f2937; /* Dark primary color */
}

.bg-brown-500 {
  background-color: #8B4513; /* Brown bar color */
}

.shadow-lg {
  box-shadow:
      0 10px 15px -3px rgba(0, 0, 0, 0.1),
      0 4px 6px -2px rgba(0, 0, 0, 0.05);
}
</style>