<template>
  <div v-if="isLoggedIn" class="min-h-screen bg-background">
    <NavBar />

    <NotificationComponent
      v-model:show="notification.show"
      :type="notification.type"
      @close="clearNotification"
    >
      {{ notification.message }}
    </NotificationComponent>

    <!-- Back Button and Breadcrumb Navigation -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-6">
      <div class="flex items-center space-x-4">
        <button
          @click="goBack"
          class="inline-flex items-center px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-colors duration-200"
        >
          <i class="fas fa-arrow-left mr-2"></i>
          Back to Organizations
        </button>
        <nav class="flex" aria-label="Breadcrumb">
          <ol class="flex items-center space-x-2">
            <li>
              <router-link
                :to="{ name: 'viewOrgs' }"
                class="text-gray-500 hover:text-gray-700"
              >
                Organizations
              </router-link>
            </li>
            <li class="text-gray-500">/</li>
            <li class="font-medium text-gray-900">
              {{ org?.orgName || "Loading..." }}
            </li>
          </ol>
        </nav>
      </div>
    </div>

    <!-- Organization Header Section -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
      <div
        v-if="org"
        class="bg-white rounded-2xl shadow-lg p-8 border border-gray-100"
      >
        <div class="flex flex-col md:flex-row gap-8">
          <!-- Left Column - Logo & Settings -->
          <div class="flex flex-col items-center md:w-1/3">
            <div class="relative group">
              <img
                :src="
                  org.encodedImage ||
                  'https://d31kswug2i6wp2.cloudfront.net/fallback/company/medium_logo_default.png'
                "
                alt="Organization Logo"
                class="w-48 h-48 rounded-full object-cover ring-4 ring-blue-50 shadow-xl transition-transform duration-300 hover:scale-105"
              />
              <div
                v-if="currentUser.userID === org.orgOwnerID"
                class="absolute bottom-2 right-2"
              >
                <DropdownMenu :items="dropdownOpts" symbol="fas fa-cog">
                  <template #trigger>
                    <button
                      class="p-2 bg-white rounded-full shadow-lg hover:bg-gray-50 transition-colors duration-200"
                    >
                      <i class="fas fa-cog text-gray-600"></i>
                    </button>
                  </template>
                </DropdownMenu>
              </div>
            </div>
          </div>

          <!-- Right Column - Organization Info -->
          <div class="flex-1 space-y-6">
            <div>
              <h1 class="text-4xl font-bold text-gray-900 mb-2">
                {{ org.orgName }}
              </h1>
              <div
                class="flex items-center space-x-2 text-sm text-gray-500 mb-4"
              >
                <i class="fas fa-user-circle"></i>
                <span v-if="creator.userName"
                  >Created by {{ creator.userName }}</span
                >
              </div>
            </div>
            <p class="text-xl text-gray-600 leading-relaxed">
              {{ org.orgDescription }}
            </p>
            <div class="flex flex-wrap gap-4 pt-4">
              <div
                class="flex items-center space-x-2 bg-blue-50 px-4 py-2 rounded-lg"
              >
                <i class="fas fa-project-diagram text-blue-600"></i>
                <span class="text-blue-600 font-medium">
                  {{ projects?.length || 0 }} Projects
                </span>
              </div>
              <!-- Add more organization stats here if needed -->
            </div>
          </div>
        </div>
      </div>
      <div v-else class="text-center py-12">
        <div class="animate-pulse flex justify-center items-center">
          <div class="h-8 w-8 mr-2">
            <i class="fas fa-spinner fa-spin text-blue-500 text-2xl"></i>
          </div>
          <span class="text-lg text-gray-600"
            >Loading organization data...</span
          >
        </div>
      </div>
    </div>

    <!-- Search and Create Section -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
      <div class="bg-white rounded-xl shadow-md p-6 border border-gray-100">
        <div
          class="flex flex-col md:flex-row items-center justify-between gap-4"
        >
          <div class="flex-1 w-full space-y-4">
            <div class="relative">
              <input
                type="text"
                class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200"
                placeholder="Search projects..."
              />
              <i
                class="fas fa-search text-gray-400 absolute left-3 top-3.5"
              ></i>
            </div>
            <button
              @click="viewUsersInOrg"
              class="inline-flex items-center px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors duration-200"
            >
              <i class="fas fa-users mr-2"></i>
              View Organization Users
            </button>
          </div>
          <div v-if="org && currentUser.userID === org.orgOwnerID">
            <DropdownMenu :items="projectToggleOpts" symbol="fas fa-archive">
              <template #trigger>
                <button
                  class="inline-flex items-center px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors duration-200"
                >
                  <i class="fas fa-filter mr-2"></i>
                  Filter Projects
                  <i class="fas fa-caret-down ml-2"></i>
                </button>
              </template>
            </DropdownMenu>
          </div>
          <button
            @click="goToCreateProject"
            class="inline-flex items-center px-6 py-3 bg-primary text-white font-medium rounded-lg hover:bg-green-700 transition duration-200 shadow-sm"
          >
            <i class="fas fa-plus mr-2"></i>
            Create New Project
          </button>
        </div>
      </div>
    </div>

    <!-- Projects Grid -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div
        v-if="org && projects && filteredProjects.length > 0"
        class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6"
      >
        <ProjCard
          v-for="project in filteredProjects"
          :key="project.projectID"
          :project="project"
          :projIndex="project.projectID"
          class="transform hover:scale-105 transition duration-200"
        />
      </div>
      <div
        v-if="
          org &&
          projects &&
          projects.length >= 1 &&
          filteredProjects.length === 0
        "
        class="text-center py-12 bg-white rounded-xl shadow-md p-8 border border-gray-100"
      >
        <i class="fas fa-project-diagram text-gray-400 text-4xl mb-4"></i>
        <p class="text-gray-500 text-lg">No projects in this current filter.</p>
      </div>
      <div
        v-if="org && projects && projects.length === 0"
        class="text-center py-12 bg-white rounded-xl shadow-md p-8 border border-gray-100"
      >
        <i class="fas fa-project-diagram text-gray-400 text-4xl mb-4"></i>
        <p class="text-gray-500 text-lg">
          No projects have been created for this org yet. Hit "create new
          project" to get started!
        </p>
      </div>
    </div>
  </div>
</template>

<script>
import NavBar from "@/components/NavBar.vue";
import { mapState } from "vuex";
import ProjCard from "@/components/ProjCard.vue";
import DropdownMenu from "@/components/DropdownMenu.vue";
import NotificationComponent from "@/components/NotificationComponent.vue";

export default {
  components: {
    NavBar,
    ProjCard,
    DropdownMenu,
    NotificationComponent,
  },
  computed: {
    ...mapState("auth", ["isLoggedIn", "currentUser"]),
    projectToggleLabel() {
      switch (this.projectToggle) {
        case "unarchived":
          return "Unarchived Projects";
        case "archived":
          return "Archived Projects";
        case "all":
          return "All Projects";
        default:
          return "";
      }
    },
    filteredProjects() {
      switch (this.projectToggle) {
        case "unarchived":
          return this.projects.filter((project) => !project.isArchived);
        case "archived":
          return this.projects.filter((project) => project.isArchived);
        case "all":
          return this.projects;
        default:
          return [];
      }
    },
  },
  data() {
    return {
      org: null,
      projects: null,
      dropdownOpts: [
        {
          title: "Edit Organization Details",
          symbol: "fas fa-edit",
          command: this.editOrg,
        },
        {
          title: "Delete This Organization",
          symbol: "fas fa-trash",
          command: this.deleteOrg,
        },
        {
          title: "Edit Organization Users",
          symbol: "fas fa-users",
          command: this.editOrgUsers,
        },
      ],
      projectToggle: "unarchived",
      projectToggleOpts: [
        {
          title: "Unarchived Projects",
          symbol: "fas fa-folder-open",
          command: () => (this.projectToggle = "unarchived"),
        },
        {
          title: "Archived Projects",
          symbol: "fas fa-archive",
          command: () => (this.projectToggle = "archived"),
        },
        {
          title: "All Projects",
          symbol: "fas fa-ellipsis-h",
          command: () => (this.projectToggle = "all"),
        },
      ],
      creator: {},
      notification: {
        show: false,
        type: "info",
        message: "",
      },
      deleteConfirmation: false,
    };
  },
  async created() {
    await this.getOrgData();
    await this.getAllOrgProjects();
    await this.getCreatorData();
  },
  methods: {
    showNotification(type, message, duration = 5000) {
      this.notification = {
        show: true,
        type,
        message,
      };
      if (duration > 0) {
        setTimeout(() => {
          this.clearNotification();
        }, duration);
      }
    },
    clearNotification() {
      this.notification.show = false;
    },
    async getOrgData() {
      try {
        const orgID = this.$route.params.orgIndex;
        this.org = await this.$store.dispatch(
          "organizations/fetchOrganization",
          orgID
        );
      } catch (error) {
        console.error("Error fetching organization data:", error);
        this.showNotification(
          "error",
          `Failed to load organization data: ${error.message}`
        );
      }
    },
    async getAllOrgProjects() {
      try {
        const orgID = this.$route.params.orgIndex;
        const orgProjects = await this.$store.dispatch(
          "projects/fetchOrgProjects",
          orgID
        );
        const filteredProjects = orgProjects.filter((project) => {
          const isAssignedUser = project.assignedUsers.some(
            (user) => user.userID === this.currentUser.userID
          );
          const isProjectOwner =
            project.projectOwnerID === this.currentUser.userID;

          return isAssignedUser || isProjectOwner;
        });
        this.projects = filteredProjects;
      } catch (error) {
        console.error("Error fetching projects:", error);
        this.showNotification(
          "error",
          `Failed to load projects: ${error.message}`
        );
      }
    },
    async getCreatorData() {
      try {
        this.creator = await this.$store.dispatch(
          "users/fetchUserAccountByID",
          this.org.orgOwnerID
        );
      } catch (error) {
        console.error("Error getting org owner info");
        this.showNotification(
          "error",
          "Failed to load organization owner information"
        );
      }
    },
    goToCreateProject() {
      this.$router.push({
        name: "createProject",
        params: { orgIndex: this.index },
      });
    },
    editOrg() {
      // Check if user has permission
      if (this.org.orgOwnerID !== this.currentUser.userID) {
        this.showNotification(
          "error",
          "You do not have permission to edit this organization"
        );
        return;
      }

      this.$router.push({
        name: "EditOrg",
        params: { orgIndex: this.$route.params.orgIndex },
      });
    },
    async deleteOrg() {
      // Check if user has permission
      if (this.org.orgOwnerID !== this.currentUser.userID) {
        this.showNotification(
          "error",
          "You do not have permission to delete this organization"
        );
        return;
      }

      // Show confirmation notification
      if (!this.deleteConfirmation) {
        this.showNotification(
          "warning",
          "Are you sure you want to delete this organization? Click delete again to confirm.",
          5000
        );
        this.deleteConfirmation = true;
        setTimeout(() => {
          this.deleteConfirmation = false;
        }, 5000);
        return;
      }

      try {
        await this.$store.dispatch(
          "organizations/deleteOrganization",
          this.$route.params.orgIndex
        );

        this.showNotification("success", "Organization deleted successfully!");
        setTimeout(() => {
          this.$router.push({
            name: "viewOrgs",
            params: { orgIndex: undefined },
          });
        }, 1500);
      } catch (err) {
        this.showNotification(
          "error",
          `Failed to delete organization: ${err.message}`
        );
      }

      this.deleteConfirmation = false;
    },
    editOrgUsers() {
      // Check if user has permission
      if (this.org.orgOwnerID !== this.currentUser.userID) {
        this.showNotification(
          "error",
          "You do not have permission to edit organization users"
        );
        return;
      }

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
    goBack() {
      this.$router.push({ name: "viewOrgs" });
    },
  },
};
</script>
