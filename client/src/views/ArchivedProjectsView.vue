<template>
  <NavBar />
  <div class="min-h-screen bg-gray-50 py-8 px-4 sm:px-6 lg:px-8">
    <!-- Notification -->
    <NotificationComponent
      :show="notification.show"
      :type="notification.type"
      @close="closeNotification"
      class="max-w-7xl mx-auto"
    >
      {{ notification.message }}
    </NotificationComponent>

    <!-- Header Section -->
    <div class="max-w-7xl mx-auto mb-8">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-2xl font-bold text-gray-900">Archived Projects</h1>
          <p class="mt-1 text-sm text-gray-500">
            View and manage your archived projects
          </p>
        </div>
        <div class="flex items-center space-x-4">
          <!-- Search Input -->
          <div class="relative">
            <input
              type="text"
              v-model="searchQuery"
              placeholder="Search projects..."
              class="pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            />
            <i
              class="fas fa-search absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"
            ></i>
          </div>
          <!-- Sort Dropdown -->
          <select
            v-model="sortBy"
            class="border border-gray-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          >
            <option value="name">Sort by Name</option>
            <option value="date">Sort by Date</option>
            <option value="owner">Sort by Owner</option>
          </select>
        </div>
      </div>
    </div>

    <!-- Projects Grid -->
    <div class="max-w-7xl mx-auto">
      <div v-if="loading" class="flex justify-center items-center py-12">
        <i class="fas fa-circle-notch fa-spin text-3xl text-blue-500"></i>
      </div>

      <div v-else-if="filteredProjects.length === 0" class="text-center py-12">
        <i class="fas fa-archive text-4xl text-gray-400 mb-4"></i>
        <h3 class="text-lg font-medium text-gray-900">
          No archived projects found
        </h3>
        <p class="mt-1 text-sm text-gray-500">
          Projects you archive will appear here
        </p>
      </div>

      <div v-else class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
        <ProjectCard
          v-for="(project, index) in filteredProjects"
          :key="project.id"
          :project="project"
          :projIndex="index"
          @click="handleProjectClick(project)"
        />
      </div>
    </div>
  </div>
</template>

<script>
import ProjectCard from "@/components/ProjCard.vue";
import NavBar from "@/components/NavBar.vue";
import NotificationComponent from "@/components/NotificationComponent.vue";

export default {
  name: "ArchivedProjects",
  components: {
    ProjectCard,
    NotificationComponent,
    NavBar,
  },
  data() {
    return {
      projects: [],
      loading: true,
      searchQuery: "",
      sortBy: "name",
      notification: {
        show: false,
        type: "info",
        message: "",
      },
    };
  },
  computed: {
    filteredProjects() {
      let filtered = this.projects.filter((project) => {
        return (
          project.projectName
            .toLowerCase()
            .includes(this.searchQuery.toLowerCase()) ||
          project.projectDescription
            .toLowerCase()
            .includes(this.searchQuery.toLowerCase()) ||
          project.projectOwnerID
            .toLowerCase()
            .includes(this.searchQuery.toLowerCase())
        );
      });

      // Sort projects
      filtered.sort((a, b) => {
        switch (this.sortBy) {
          case "name":
            return a.projectName.localeCompare(b.projectName);
          case "date":
            return new Date(b.archivedDate) - new Date(a.archivedDate);
          case "owner":
            return a.projectOwnerID.localeCompare(b.projectOwnerID);
          default:
            return 0;
        }
      });

      return filtered;
    },
  },
  methods: {
    async fetchArchivedProjects() {
      try {
        this.loading = true;
        const response = await fetch("/api/archived-projects");
        this.projects = await response.json();
        this.showNotification("success", "Projects loaded successfully");
      } catch (error) {
        console.error("Failed to fetch archived projects:", error);
        this.showNotification(
          "error",
          "Failed to load projects. Please try again.",
        );
      } finally {
        this.loading = false;
      }
    },
    showNotification(type, message) {
      this.notification = {
        show: true,
        type,
        message,
      };
      // Auto-hide notification after 5 seconds
      setTimeout(() => {
        this.closeNotification();
      }, 5000);
    },
    closeNotification() {
      this.notification.show = false;
    },
    handleProjectClick(project) {
      // You can add specific handling here if needed
      this.showNotification("info", `Opening project: ${project.projectName}`);
    },
  },
  created() {
    this.fetchArchivedProjects();
  },
  watch: {
    searchQuery() {
      if (this.searchQuery) {
        this.showNotification("info", `Searching for "${this.searchQuery}"`);
      }
    },
  },
};
</script>
