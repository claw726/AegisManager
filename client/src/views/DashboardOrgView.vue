<template>
  <div v-if="isLoggedIn" class="min-h-screen bg-background">
    <NavBar />

    <!-- Organization Header Section -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div v-if="org" class="bg-white rounded-2xl shadow-lg p-8">
        <div class="flex flex-col md:flex-row gap-8">
          <!-- Left Column - Logo & Settings -->
          <div class="flex flex-col items-center">
            <div class="relative group">
              <img
                :src="
                  org.encodedImage ||
                  'https://d31kswug2i6wp2.cloudfront.net/fallback/company/medium_logo_default.png'
                "
                alt="Organization Logo"
                class="w-48 h-48 rounded-full object-cover ring-4 ring-blue-50 shadow-xl"
              />
              <div
                v-if="currentUser.userID === org.orgOwnerID"
                class="absolute bottom-2 right-2"
              >
                <DropdownMenu :items="dropdownOpts">
                  <template #trigger>
                    <button
                      class="p-2 bg-white rounded-full shadow-lg hover:bg-gray-50"
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        class="h-5 w-5 text-gray-600"
                        viewBox="0 0 20 20"
                        fill="currentColor"
                      >
                        <path
                          fill-rule="evenodd"
                          d="M11.49 3.17c-.38-1.56-2.6-1.56-2.98 0a1.532 1.532 0 01-2.286.948c-1.372-.836-2.942.734-2.106 2.106.54.886.061 2.042-.947 2.287-1.561.379-1.561 2.6 0 2.978a1.532 1.532 0 01.947 2.287c-.836 1.372.734 2.942 2.106 2.106a1.532 1.532 0 012.287.947c.379 1.561 2.6 1.561 2.978 0a1.533 1.533 0 012.287-.947c1.372.836 2.942-.734 2.106-2.106a1.533 1.533 0 01.947-2.287c1.561-.379 1.561-2.6 0-2.978a1.532 1.532 0 01-.947-2.287c.836-1.372-.734-2.942-2.106-2.106a1.532 1.532 0 01-2.287-.947zM10 13a3 3 0 100-6 3 3 0 000 6z"
                          clip-rule="evenodd"
                        />
                      </svg>
                    </button>
                  </template>
                </DropdownMenu>
              </div>
            </div>
          </div>

          <!-- Right Column - Organization Info -->
          <div class="flex-1 space-y-4">
            <h1 class="text-4xl font-bold text-gray-900">{{ org.orgName }}</h1>
            <p class="text-xl text-gray-600">{{ org.orgDescription }}</p>
            <div class="flex items-center space-x-2 text-sm text-gray-500">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-4 w-4"
                viewBox="0 0 20 20"
                fill="currentColor"
              >
                <path
                  d="M9 6a3 3 0 11-6 0 3 3 0 016 0zM17 6a3 3 0 11-6 0 3 3 0 016 0zM12.93 17c.046-.327.07-.66.07-1a6.97 6.97 0 00-1.5-4.33A5 5 0 0119 16v1h-6.07zM6 11a5 5 0 015 5v1H1v-1a5 5 0 015-5z"
                />
              </svg>
              <span v-if="creator.userName"
                >Created by {{ creator.userName }}</span
              >
            </div>
          </div>
        </div>
      </div>
      <div v-else class="text-center py-12">
        <div class="animate-pulse flex justify-center items-center">
          <div class="h-8 w-8 mr-2">
            <svg
              class="animate-spin h-8 w-8 text-blue-500"
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
            >
              <circle
                class="opacity-25"
                cx="12"
                cy="12"
                r="10"
                stroke="currentColor"
                stroke-width="4"
              ></circle>
              <path
                class="opacity-75"
                fill="currentColor"
                d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
              ></path>
            </svg>
          </div>
          <span class="text-lg text-gray-600"
            >Loading organization data...</span
          >
        </div>
      </div>
    </div>
    <!-- Search and Create Section -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
      <div class="bg-white rounded-xl shadow-md p-6">
        <div
          class="flex flex-col md:flex-row items-center justify-between gap-4"
        >
          <div class="flex-1 w-full">
            <div class="relative">
              <input
                type="text"
                class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200"
                placeholder="Search projects..."
              />
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-5 w-5 text-gray-400 absolute left-3 top-2.5"
                viewBox="0 0 20 20"
                fill="currentColor"
              >
                <path
                  fill-rule="evenodd"
                  d="M8 4a4 4 0 100 8 4 4 0 000-8zM2 8a6 6 0 1110.89 3.476l4.817 4.817a1 1 0 01-1.414 1.414l-4.816-4.816A6 6 0 012 8z"
                  clip-rule="evenodd"
                />
              </svg>
            </div>
          </div>
          <button
            @click="goToCreateProject"
            class="inline-flex items-center px-6 py-3 bg-blue-600 text-white font-medium rounded-lg hover:bg-blue-700 transition duration-200"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-5 w-5 mr-2"
              viewBox="0 0 20 20"
              fill="currentColor"
            >
              <path
                fill-rule="evenodd"
                d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z"
                clip-rule="evenodd"
              />
            </svg>
            Create New Project
          </button>
        </div>
      </div>
    </div>

    <!-- Projects Grid -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div
        v-if="org && projects"
        class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6"
      >
        <ProjCard
          v-for="project in projects"
          :key="project.projectID"
          :project="project"
          :projIndex="project.projectID"
          class="transform hover:scale-105 transition duration-200"
        />
      </div>
      <div v-else-if="!projects" class="text-center py-12">
        <p class="text-gray-500">No projects found.</p>
      </div>
    </div>
  </div>
</template>

<script>
import NavBar from "@/components/NavBar.vue";
import { mapState } from "vuex";
import ProjCard from "@/components/ProjCard.vue";
import DropdownMenu from "@/components/DropdownMenu.vue";

export default {
  components: {
    NavBar,
    ProjCard,
    DropdownMenu,
  },
  computed: {
    ...mapState("auth", ["isLoggedIn", "currentUser"]),
  },
  data() {
    return {
      org: null, // Initialize org as null
      projects: null, // Initialize projects as null
      dropdownOpts: [
        {
          title: "Edit Organization Details",
          command: this.editOrg,
        },
        {
          title: "Delete This Organization",
          command: this.deleteOrg,
        },
        {
          title: "Edit Organization Users",
          command: this.editOrgUsers,
        },
      ],
      creator: {},
    };
  },
  async created() {
    await this.getOrgData(); // Ensure this is awaited
    await this.getAllOrgProjects(); // Ensure this is awaited
    await this.getCreatorData();
  },
  methods: {
    async getOrgData() {
      try {
        const orgID = this.$route.params.orgIndex; // Ensure you are getting the correct orgID
        this.org = await this.$store.dispatch(
          "organizations/fetchOrganization",
          orgID,
        );
      } catch (error) {
        console.error("Error fetching organization data:", error);
        alert("Failed to load organization data: " + error.message);
        this.$router.push({
          name: "viewOrgs",
          params: { orgIndex: undefined },
        });
      }
    },
    async getAllOrgProjects() {
      try {
        const orgID = this.$route.params.orgIndex; // Ensure you are getting the correct orgID
        const orgProjects = await this.$store.dispatch(
          "projects/fetchOrgProjects",
          orgID,
        );
        const filteredProjects = orgProjects.filter((project) => {
          // Check if currentUser.userID is in the assignedUsers array or is the project owner
          const isAssignedUser = project.assignedUsers.some(
            (user) => user.userID === this.currentUser.userID,
          );
          const isProjectOwner =
            project.projectOwnerID === this.currentUser.userID;

          return isAssignedUser || isProjectOwner;
        });
        this.projects = filteredProjects;
      } catch (error) {
        console.error("Error fetching projects:", error);
        alert("Failed to load projects: " + error.message);
      }
    },
    async getCreatorData() {
      try {
        this.creator = await this.$store.dispatch(
          "users/fetchUserAccountByID",
          this.org.orgOwnerID,
        );
      } catch (error) {
        console.error("Error getting org owner info");
      }
    },
    goToCreateProject() {
      this.$router.push({
        name: "createProject",
        params: { orgIndex: this.index },
      });
    },
    editOrg() {
      this.$router.push({
        name: "EditOrg",
        params: { orgIndex: this.$route.params.orgIndex },
      });
    },
    deleteOrg() {
      if (confirm("Are you sure you want to delete this organization?")) {
        this.$store
          .dispatch(
            "organizations/deleteOrganization",
            this.$route.params.orgIndex,
          )
          .then(() => {
            alert("Organization deleted successfully!");
            this.$router.push({
              name: "viewOrgs",
              params: { orgIndex: undefined },
            });
          })
          .catch((err) => {
            alert("There was an error deleting the organization: ", err);
          });
      }
    },
    editOrgUsers() {
      this.$router.push({
        name: "EditOrgUsers",
        params: { orgIndex: this.$route.params.orgIndex },
      });
    },

    viewUsersInOrg() {
      this.$router.push({
        name: "viewUsersInOrg",
        query: { org: this.org, orgIndex: this.$route.params.orgIndex },
      });
    },
  },
};
</script>
