<template>
  <div v-if="isLoggedIn" class="min-h-screen bg-background">
    <NavBar />

    <div class="flex justify-center p-4">
      <div
        v-if="org"
        class="relative flex flex-row items-start h-screen/3 py-4"
      >
        <div class="flex flex-col items-center mr-8">
          <img
            :src="
              org.encodedImage ||
              'https://d31kswug2i6wp2.cloudfront.net/fallback/company/medium_logo_default.png'
            "
            alt="Organization Logo"
            class="w-48 h-48 rounded-full drop-shadow-xl col-span-1"
          />
          <div class="mt-4" v-if="currentUser.userID === org.orgOwnerID">
            <DropdownMenu title="⚙️" :items="dropdownOpts" />
          </div>
        </div>
        <div class="flex flex-col justify-center p-4">
          <div class="text-4xl font-bold text-primary">{{ org.orgName }}</div>
          <div class="text-xl font-semibold text-secondary">
            {{ org.orgDescription }}
          </div>
          <div class="text-medium text-accent" v-if="creator.userName">
            Created by: {{ creator.userName }}
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
      <div v-else>
        <p>Loading organization data...</p>
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
            <button @click="viewUsersInOrg" class="dashboard-button mt-4">
        View Organization Users
      </button>
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
