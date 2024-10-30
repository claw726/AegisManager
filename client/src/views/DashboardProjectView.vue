<template>
  <div v-if="isLoggedIn && proj" class="min-h-screen bg-background">
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
          Back to Organization
        </button>
        <nav class="flex" aria-label="Breadcrumb">
          <ol class="flex items-center space-x-2">
            <li>
              <router-link
                :to="{ name: 'OrganizationDashboard' }"
                class="text-gray-500 hover:text-gray-700"
              >
                Organization
              </router-link>
            </li>
            <li class="text-gray-500">/</li>
            <li class="font-medium text-gray-900">{{ proj.projectName }}</li>
          </ol>
        </nav>
      </div>
    </div>

    <!-- Project Header Section with enhanced styling -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
      <div
        v-if="proj"
        class="bg-white rounded-2xl shadow-lg p-8 border border-gray-100"
      >
        <div class="flex flex-col md:flex-row gap-8">
          <!-- Left Column - Logo & Settings -->
          <div class="flex flex-col items-center">
            <div class="relative group">
              <div class="aspect-video rounded-lg overflow-hidden">
                <img
                  :src="
                    proj.encodedImage ||
                    'https://d31kswug2i6wp2.cloudfront.net/fallback/company/medium_logo_default.png'
                  "
                  alt="Organization Logo"
                  class="object-cover h-48 transition-transform duration-300 transform hover:scale-105"
                />
              </div>
              <div
                v-if="currentUser.userID === proj.projectOwnerID"
                class="absolute bottom-2 right-2"
              >
                <DropdownMenu :items="dropdownOpts" symbol="fas fa-cog">
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

          <!-- Right Column - Project Info with enhanced typography -->
          <div class="flex-1 space-y-6">
            <div>
              <h1 class="text-4xl font-bold mb-2 flex items-center">
                <span
                  :class="{
                    'text-gray-900': !proj.isArchived,
                    'text-gray-400 line-through': proj.isArchived,
                  }"
                >
                  {{ proj.projectName }}
                </span>
                <span
                  v-if="proj.isArchived"
                  class="bg-gray-200 text-gray-600 px-3 py-1 rounded-lg text-sm font-medium ml-4"
                >
                  <i class="fas fa-archive mr-2"></i> Archived
                </span>
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
              {{ proj.projectDescription }}
            </p>
            <div class="flex flex-wrap gap-4 pt-4">
              <div
                class="flex items-center space-x-2 bg-blue-50 px-4 py-2 rounded-lg"
              >
                <i class="fas fa-tasks text-blue-600"></i>
                <span class="text-blue-600 font-medium">
                  {{ tasks.length }} Tasks
                </span>
              </div>
              <!-- Add more project stats here -->
            </div>
          </div>
        </div>
      </div>
      <div v-else class="text-center py-12">
        <div class="animate-pulse flex justify-center items-center">
          <div class="h-8 w-8 mr-2">
            <i class="fas fa-spinner fa-spin text-blue-500 text-2xl"></i>
          </div>
          <span class="text-lg text-gray-600">Loading Project data...</span>
        </div>
      </div>
    </div>

    <!-- Search and Create Section with improved styling -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
      <div class="bg-white rounded-xl shadow-md p-6 border border-gray-100">
        <div
          class="flex flex-col md:flex-row items-center justify-between gap-4"
        >
          <div class="flex-1 w-full">
            <div class="relative">
              <input
                type="text"
                class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200"
                placeholder="Search tasks..."
              />
              <i
                class="fas fa-search text-gray-400 absolute left-3 top-3.5"
              ></i>
            </div>
          </div>
          <button
            @click="goToCreateTask"
            class="inline-flex items-center px-6 py-3 bg-blue-600 text-white font-medium rounded-lg hover:bg-blue-700 transition duration-200 shadow-sm"
          >
            <i class="fas fa-plus mr-2"></i>
            Create New Task
          </button>
        </div>
      </div>
    </div>

    <!-- List of Tasks with improved empty state -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div
        v-if="tasks && tasks.length > 0"
        class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6"
      >
        <TaskCard
          v-for="(task, index) in tasks"
          :key="index"
          :task="task"
          :taskIndex="index"
        />
      </div>
      <div
        v-else
        class="text-center py-12 bg-white rounded-xl shadow-md p-8 border border-gray-100"
      >
        <i class="fas fa-tasks text-gray-400 text-4xl mb-4"></i>
        <p class="text-gray-500 text-lg">
          No tasks yet! Click 'Create New Task' to get started.
        </p>
      </div>
    </div>
  </div>
</template>

<script>
import NavBar from "@/components/NavBar.vue";
import { mapState } from "vuex";
import TaskCard from "@/components/TaskCard.vue";
import DropdownMenu from "@/components/DropdownMenu.vue";
import NotificationComponent from "@/components/NotificationComponent.vue";

export default {
  data() {
    return {
      proj: null,
      projects: [],
      dropdownOpts: [
        {
          title: "Edit Project Details",
          symbol: "fas fa-edit",
          command: this.editProject,
        },
        {
          title: "Delete This Project",
          symbol: "fas fa-trash",
          command: this.deleteProject,
        },
        {
          title: "Edit Project Members",
          symbol: "fas fa-users",
          command: this.editProjUsers,
        },
        {
          title: "Archive Projects",
          symbol: "fas fa-archive",
          command: this.toggleArchived,
        },
      ],
      tasks: [],
      creator: {},
      isLoaded: false,
      notification: {
        show: false,
        type: "info",
        message: "",
      },
    };
  },
  components: {
    NavBar,
    TaskCard,
    DropdownMenu,
    NotificationComponent,
  },
  async created() {
    await this.getProjData();
    await this.getCreatorData();
  },
  computed: {
    ...mapState("auth", ["isLoggedIn", "currentUser"]),
  },
  methods: {
    goToCreateTask() {
      // this.$router.push({ name: 'createTask', params: { orgIndex: this.index }});
      this.$router.push({
        name: "createTask",
        params: {
          orgId: this.$route.params.orgIndex,
          projId: this.$route.params.projIndex,
          userID: this.currentUser.userID,
        },
      });
    },

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
    async getProjData() {
      try {
        this.proj = await this.$store.dispatch(
          "projects/fetchProject",
          this.$route.params.projIndex,
        );
      } catch (err) {
        this.showNotification(
          "error",
          "There was an error fetching the organization data",
        );
        this.$router.push({ name: "OrganizationDashboard" });
      }
    },
    async getCreatorData() {
      try {
        this.creator = await this.$store.dispatch(
          "users/fetchUserAccountByID",
          this.proj.projectOwnerID,
        );
      } catch (error) {
        this.showNotification("error", "Error getting project owner info");
      }
    },
    goBack() {
      this.$router.push({ name: "OrganizationDashboard" });
    },
    async getAllProjectTasks() {
      try {
        const projID = this.$route.params.projIndex;
        this.tasks = this.$store.dispatch(
          "projects/fetchTasksFromProject",
          projID,
        );
      } catch (error) {
        this.showNotification("error", "Error getting project tasks");
      }
    },
    editProject() {
      if (this.proj.projectOwnerID !== this.currentUser.userID) {
        this.showNotification(
          "error",
          "You are not authorized to modify this project.",
        );
        return;
      }
      this.$router.push({
        name: "EditProject",
        params: {
          orgIndex: this.$route.params.orgIndex,
          projIndex: this.$route.params.projIndex,
        },
      });
    },
    async toggleArchived() {
      if (this.proj.projectOwnerID !== this.currentUser.userID) {
        this.showNotification(
          "error",
          "You are not authorized to archive this project.",
        );
        return;
      }

      try {
        await this.$store.dispatch("projects/changeArchivedStatus", {
          projectID: this.$route.params.projIndex,
          isArchived: !this.proj.isArchived,
        });
        this.proj.isArchived = !this.proj.isArchived;
        this.showNotification("success", "Project archived successfully!");
        setTimeout(() => {
          this.$router.push({ name: "OrganizationDashboard" });
        }, 1500);
      } catch (err) {
        this.showNotification("error", "Failed to archive project");
        console.error(err);
      }
    },
    async deleteProject() {
      if (this.proj.projectOwnerID !== this.currentUser.userID) {
        this.showNotification(
          "error",
          "You are not authorized to delete this project.",
        );
        return;
      }

      // Show confirmation notification
      this.showNotification(
        "warning",
        "Are you sure you want to delete this project? Click again to confirm.",
        0,
      );

      // Set up confirmation action
      if (!this.deleteConfirmation) {
        this.deleteConfirmation = true;
        setTimeout(() => {
          this.deleteConfirmation = false;
          this.clearNotification();
        }, 5000);
        return;
      }

      try {
        await this.$store.dispatch("projects/deleteProject", {
          projectID: this.$route.params.projIndex,
        });

        this.showNotification("success", "Project deleted successfully!");
        setTimeout(() => {
          this.$router.push({ name: "OrganizationDashboard" });
        }, 1500);
      } catch (err) {
        this.showNotification(
          "error",
          "Failed to change project archival status: " + err,
        );
        console.error(err);
      }

      this.deleteConfirmation = false;
    },
    editProjUsers() {
      this.$router.push({
        name: "EditProjUsers",
        params: {
          orgIndex: this.$route.params.orgIndex,
          projIndex: this.$route.params.projIndex,
        },
      });
    },
  },
};
</script>
