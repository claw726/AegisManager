<template>
  <div v-if="isLoggedIn" class="relative min-h-screen bg-gray-50">
    <NavBar />

    <!-- Main Container -->
    <div class="flex min-h-[calc(100vh-64px)]">
      <!-- Sidebar -->
      <TaskSidebarComponent :tasks="filteredTasks" />

      <!-- Main Content Area -->
      <div class="flex-1 overflow-x-hidden">
        <div
          class="flex flex-col items-center w-full max-w-7xl px-4 mx-auto space-y-8 mt-12"
        >
          <!-- Header -->
          <div class="flex justify-between items-center w-full">
            <h1
              class="text-4xl font-bold text-hunter-green mb-6 flex-grow text-center"
            >
              To Do List
            </h1>
          </div>

          <div
            class="h-1 bg-accent drop-shadow-lg rounded mx-16 flex w-screen"
          />

          <SearchComponent v-model:searchQuery="searchQuery" size="70%" />

          <!-- Controls Panel -->
          <div
            class="w-full max-w-4xl bg-white shadow-lg rounded-lg overflow-hidden"
          >
            <!-- Main Controls Header -->
            <div class="flex justify-between items-center p-4 bg-gray-50">
              <button
                class="flex items-center px-4 py-2 rounded-md hover:bg-blue-50 transition-all duration-200 border border-gray-200 hover:border-blue-300 hover:shadow-md group"
                @click="isFilterMenuOpen = !isFilterMenuOpen"
              >
                <i
                  class="fas fa-sliders-h mr-2 text-blue-600 group-hover:rotate-180 transition-transform duration-300"
                ></i>
                <span class="relative text-gray-700 group-hover:text-blue-600">
                  Filter & Sort
                  <span
                    class="absolute bottom-0 left-0 w-full h-0.5 bg-blue-600 transform scale-x-0 group-hover:scale-x-100 transition-transform duration-200"
                  >
                  </span>
                </span>
                <i
                  :class="[
                    'fas',
                    'ml-2',
                    isFilterMenuOpen ? 'fa-chevron-up' : 'fa-chevron-down',
                    'transform group-hover:translate-y-0.5 transition-transform text-blue-600',
                  ]"
                ></i>
              </button>
            </div>

            <!-- Expandable Filter Panel -->
            <div v-show="isFilterMenuOpen" class="border-t border-gray-200">
              <div class="p-4 space-y-4">
                <!-- Filter Section -->
                <div class="space-y-3">
                  <h3 class="font-semibold text-gray-700">Filter By</h3>
                  <div
                    class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4"
                  >
                    <!-- Priority Filter -->
                    <div class="space-y-1">
                      <label class="text-sm text-gray-600">Priority</label>
                      <select
                        v-model="selectedPriority"
                        class="w-full border border-gray-300 rounded-md p-2 text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                        @change="filterTasks"
                      >
                        <option value="">All Priorities</option>
                        <option value="High">High</option>
                        <option value="Medium">Medium</option>
                        <option value="Low">Low</option>
                      </select>
                    </div>

                    <!-- Assigner Filter -->
                    <div v-if="uniqueAssigners" class="space-y-1">
                      <label class="text-sm text-gray-600">Assigner</label>
                      <select
                        v-model="selectedAssigner"
                        class="w-full border border-gray-300 rounded-md p-2 text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                        @change="filterTasks"
                      >
                        <option value="">All Assigners</option>
                        <option
                          v-for="user in uniqueAssigners"
                          :key="user.id"
                          :value="user.id"
                        >
                          {{ user.name }}
                        </option>
                      </select>
                    </div>

                    <!-- Project Filter -->
                    <div class="space-y-1">
                      <label class="text-sm text-gray-600">Project</label>
                      <select
                        v-model="selectedProject"
                        class="w-full border border-gray-300 rounded-md p-2 text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                        @change="filterTasks"
                      >
                        <option value="">All Projects</option>
                        <option
                          v-for="project in uniqueProjects"
                          :key="project.id"
                          :value="project.id"
                        >
                          {{ project.name }}
                        </option>
                      </select>
                    </div>

                    <!-- Organization Filter -->
                    <div class="space-y-1">
                      <label class="text-sm text-gray-600">Organization</label>
                      <select
                        v-model="selectedOrg"
                        class="w-full border border-gray-300 rounded-md p-2 text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                        @change="filterTasks"
                      >
                        <option value="">All Organizations</option>
                        <option
                          v-for="org in uniqueOrgs"
                          :key="org.id"
                          :value="org.id"
                        >
                          {{ org.name }}
                        </option>
                      </select>
                    </div>
                  </div>
                </div>

                <!-- Sort Section -->
                <div class="space-y-3">
                  <h3 class="font-semibold text-gray-700">Sort By</h3>
                  <div class="flex space-x-4">
                    <select
                      v-model="sortField"
                      class="border border-gray-300 rounded-md p-2 text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                    >
                      <option value="dueDate">Due Date</option>
                      <option value="taskName">Task Name</option>
                      <option value="taskPriority">Priority</option>
                    </select>
                    <button
                      class="flex items-center px-4 py-2 border border-gray-300 rounded-md hover:bg-gray-50 transition"
                      @click="toggleSortOrder"
                    >
                      <i
                        :class="[
                          'fas',
                          sortOrder === 'asc'
                            ? 'fa-sort-amount-down-alt'
                            : 'fa-sort-amount-up-alt',
                          'mr-2',
                        ]"
                      ></i>
                      {{ sortOrder === "asc" ? "Ascending" : "Descending" }}
                    </button>
                  </div>
                </div>

                <!-- Active Filters Display -->
                <div
                  v-if="hasActiveFilters"
                  class="pt-3 border-t border-gray-200"
                >
                  <h3 class="text-sm font-medium text-gray-700 mb-2">
                    Active Filters:
                  </h3>
                  <div class="flex flex-wrap gap-2">
                    <span
                      v-if="selectedPriority"
                      class="inline-flex items-center px-3 py-1 rounded-full text-sm bg-blue-100 text-blue-800"
                    >
                      Priority: {{ selectedPriority }}
                      <button
                        class="ml-2 text-blue-600 hover:text-blue-800"
                        @click="selectedPriority = ''"
                      >
                        <i class="fas fa-times"></i>
                      </button>
                    </span>
                    <span
                      v-if="selectedAssigner"
                      class="inline-flex items-center px-3 py-1 rounded-full text-sm bg-blue-100 text-blue-800"
                    >
                      Assigned By: {{ getAssignerName(selectedAssigner) }}
                      <button
                        class="ml-2 text-blue-600 hover:text-blue-800"
                        @click="selectedAssigner = ''"
                      >
                        <i class="fas fa-times"></i>
                      </button>
                    </span>
                    <span
                      v-if="selectedProject"
                      class="inline-flex items-center px-3 py-1 rounded-full text-sm bg-blue-100 text-blue-800"
                    >
                      Project: {{ getProjectName(selectedProject) }}
                      <button
                        class="ml-2 text-blue-600 hover:text-blue-800"
                        @click="selectedProject = ''"
                      >
                        <i class="fas fa-times"></i>
                      </button>
                    </span>
                    <span
                      v-if="selectedOrg"
                      class="inline-flex items-center px-3 py-1 rounded-full text-sm bg-blue-100 text-blue-800"
                    >
                      Organization: {{ getOrgName(selectedOrg) }}
                      <button
                        class="ml-2 text-blue-600 hover:text-blue-800"
                        @click="selectedOrg = ''"
                      >
                        <i class="fas fa-times"></i>
                      </button>
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Task Grid -->
          <div
            class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 w-full max-w-7xl px-4"
          >
            <template v-if="filteredTasks && filteredTasks.length > 0">
              <TaskCard
                v-for="task in filteredTasks"
                :key="task.taskID"
                :task="task"
              />
            </template>
            <div v-else class="col-span-full text-center text-gray-600">
              No tasks available.
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import NavBar from "@/components/NavBar.vue";
import SearchComponent from "@/components/SearchComponent.vue";
import { mapState, mapActions } from "vuex";
import TaskCard from "@/components/TaskCard.vue";
import TaskSidebarComponent from "@/components/TaskSidebarComponent.vue";

export default {
  components: {
    NavBar,
    TaskCard,
    SearchComponent,
    TaskSidebarComponent,
  },

  data() {
    return {
      isFilterMenuOpen: false,
      selectedPriority: "",
      selectedAssigner: null,
      selectedAssignerName: "",
      selectedProject: "",
      selectedOrg: "",
      sortField: "dueDate",
      sortOrder: "asc",
      uniqueAssigners: {},
      uniqueProjects: {},
      uniqueOrgs: {},
      searchQuery: "",
    };
  },

  computed: {
    ...mapState("tasks", ["tasks"]),
    ...mapState("auth", ["currentUser", "isLoggedIn"]),

    hasActiveFilters() {
      return (
        this.selectedPriority ||
        this.selectedAssigner ||
        this.selectedProject ||
        this.selectedOrg
      );
    },

    filteredTasks() {
      let tasks = this.tasks;
      console.log("tasks");
      console.log(tasks);
      if (this.searchQuery) {
        if (this.validateDateFormat(this.searchQuery)) {
          //dueDate: "2024-10-29T15:45:30.123+00:00"
          const searchMonth = this.searchQuery.substring(0, 2);
          const searchDay = this.searchQuery.substring(3, 5);
          const searchYear = this.searchQuery.substring(6, 10);
          const formattedDay = searchYear + "-" + searchMonth + "-" + searchDay;
          console.log(formattedDay);
          tasks = tasks.filter((task) => task.dueDate.includes(formattedDay));
        } else {
          tasks = tasks.filter((task) =>
            task.taskName
              .toLowerCase()
              .includes(this.searchQuery.toLowerCase()),
          );
        }
      }

      // Apply filters
      if (this.selectedPriority) {
        tasks = tasks.filter(
          (task) => task.taskPriority === this.selectedPriority,
        );
      }
      if (this.selectedAssigner) {
        tasks = tasks.filter(
          (task) => task.assignerID === this.selectedAssigner,
        );
      }
      if (this.selectedProject) {
        tasks = tasks.filter(
          (task) => task.parentProjectID === this.selectedProject,
        );
      }
      if (this.selectedOrg) {
        tasks = tasks.filter((task) => task.parentOrgID === this.selectedOrg);
      }

      // Apply sorting
      tasks.sort((a, b) => {
        let comparison = 0;
        const priorityOrder = { High: 1, Medium: 2, Low: 3 };
        switch (this.sortField) {
          case "dueDate":
            comparison = new Date(a.dueDate) - new Date(b.dueDate);
            break;
          case "taskName":
            comparison = a.taskName.localeCompare(b.taskName);
            break;
          case "taskPriority":
            comparison =
              priorityOrder[a.taskPriority] - priorityOrder[b.taskPriority];
            break;
        }
        return this.sortOrder === "asc" ? comparison : -comparison;
      });

      return tasks;
    },

    taskStats() {
      return {
        total: this.tasks.length,
        completed: this.tasks.filter((task) => task.complete).length,
        pending: this.tasks.filter((task) => !task.complete).length,
      };
    },
  },

  watch: {
    searchQuery() {
      this.filterTasks();
    },

    $route(to, from) {
      if (to.path === from.path) {
        this.$nextTick(() => {
          this.$forceUpdate();
        });
      }
    },
  },

  async mounted() {
    await this.fetchTasks();
    await this.fetchUniqueAssigners();
    await this.fetchUniqueProjects();
    await this.fetchUniqueOrgs();
  },

  methods: {
    ...mapActions("tasks", ["fetchTasks"]),

    async fetchUniqueAssigners() {
      // Get unique assigner IDs from tasks
      const assignerIDs = [
        ...new Set(this.tasks.map((task) => task.assignerID)),
      ];

      // Use Promise.all to wait for all user fetches to complete
      const assigners = await Promise.all(
        assignerIDs.map(async (id) => {
          const user = await this.$store.dispatch(
            "users/fetchUserAccountByID",
            id,
          );
          return {
            id,
            name: user ? user.userName : "Unknown User", // Adjust based on your user object structure
          };
        }),
      );
      this.uniqueAssigners = assigners;
    },

    async fetchUniqueProjects() {
      const projectIDs = [
        ...new Set(this.tasks.map((task) => task.parentProjectID)),
      ];
      const projects = await Promise.all(
        projectIDs.map(async (id) => {
          const project = await this.$store.dispatch(
            "projects/fetchProject",
            id,
          );
          return {
            id,
            name: project ? project.projectName : "Unknown Project", // Adjust based on your user object structure
          };
        }),
      );
      this.uniqueProjects = projects;
    },

    async fetchUniqueOrgs() {
      const orgIDs = [...new Set(this.tasks.map((task) => task.parentOrgID))];
      const orgs = await Promise.all(
        orgIDs.map(async (id) => {
          const org = await this.$store.dispatch(
            "organizations/fetchOrganization",
            id,
          );
          return {
            id,
            name: org ? org.orgName : "Unknown Org", // Adjust based on your user object structure
          };
        }),
      );
      this.uniqueOrgs = orgs;
    },

    getAssignerName(assignerID) {
      const assigner = this.uniqueAssigners.find(
        (assigner) => assigner.id === assignerID,
      );
      return assigner ? assigner.name : "Unknown User";
    },

    getProjectName(projectID) {
      const project = this.uniqueProjects.find(
        (project) => project.id === projectID,
      );
      return project ? project.name : "Unknown Project";
    },

    getOrgName(orgID) {
      const org = this.uniqueOrgs.find((org) => org.id === orgID);
      return org ? org.name : "Unknown Organization";
    },

    filterTasks() {
      // Method kept for potential future use
    },

    toggleSortOrder() {
      this.sortOrder = this.sortOrder === "asc" ? "desc" : "asc";
    },

    goToCreateTask() {
      this.$router.push({ name: "createTask" });
    },
    goToKanbanBoard() {
      this.$router.push({ name: "KB" });
    },
    getDueToday() {
      const today = new Date().toISOString().split("T")[0];
      return this.filteredTasks.filter(
        (task) => task.dueDate?.split("T")[0] === today && !task.complete,
      ).length;
    },
    getTaskPriorityColor(priority) {
      return (
        {
          High: "text-red-600",
          Medium: "text-yellow-600",
          Low: "text-green-600",
        }[priority] || "text-gray-600"
      );
    },
    validateDateFormat(dateStr) {
      const datePattern = /^\d{2}\/\d{2}\/\d{4}$/;
      return datePattern.test(dateStr);
    },
  },
};
</script>

<style>
.router-link-active {
  @apply bg-blue-50 text-blue-700;
}

.stat-card {
  @apply transition-all duration-300 hover:scale-105 cursor-pointer;
}

/* Custom scrollbar */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  @apply bg-gray-100 rounded-full;
}

::-webkit-scrollbar-thumb {
  @apply bg-gray-300 rounded-full hover:bg-gray-400 transition-colors;
}

/* Improved transitions */
.fade-enter-active,
.fade-leave-active {
  @apply transition-opacity duration-200;
}

.fade-enter-from,
.fade-leave-to {
  @apply opacity-0;
}
</style>
