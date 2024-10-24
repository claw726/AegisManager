<template>
  <div class="relative w-full min-h-screen h-full bg-background">
    <!-- Navbar -->
    <NavBar />

    <!-- Main Content: Organization and Users List -->
    <div class="flex justify-center items-center h-full px-28">
      <!-- Container for organizations and their users -->
      <div class="w-full max-w-6xl space-y-12">
        <!-- Organization Information -->
        <div class="mb-6 bg-white shadow-lg rounded-lg p-8">
          <h2 class="text-4xl font-bold text-primary">
            Organization Name: {{ org.orgName }}
          </h2>
          <p class="text-lg text-gray-700">Organization ID: {{ org.orgID }}</p>
        </div>

        <!-- Users List -->
        <div>
          <h3 class="text-2xl font-semibold text-gray-800 mb-4">Users</h3>
          <ul class="space-y-4">
            <li
              v-for="user in org.users"
              :key="user.username"
              class="flex justify-between items-center bg-gray-100 p-4 rounded-lg"
            >
              <div>
                <p class="text-lg font-medium text-gray-900">
                  Name: {{ user.name }}
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
import NavBar from "../components/NavBar.vue";

export default {
  name: "OrganizationUsersPage",
  components: {
    NavBar,
  },

  data() {
    return {
      org: null,
    };
  },
  created() {
    this.getOrgData();
  },
  methods: {
    getOrgData() {
      this.org = this.organizations[this.$route.params.orgIndex];
      if (!this.org) {
        alert("There was an error fetching the organization data");
        this.$router.push({ name: "viewOrgs" });
      }
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

.bg-zinc-800 {
  background-color: #27272a; /* Dark button background */
}

.shadow-lg {
  box-shadow:
    0 10px 15px -3px rgba(0, 0, 0, 0.1),
    0 4px 6px -2px rgba(0, 0, 0, 0.05);
}
</style>
