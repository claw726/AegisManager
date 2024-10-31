<template>
  <div v-if="isLoggedIn" class="relative min-h-screen bg-gray-50">
    <NavBar />

    <!-- Main Container -->
    <div class="flex min-h-[calc(100vh-64px)]">
      <!-- Side Navigation -->
      <div class="w-64 bg-white border-r border-gray-200 p-4 shadow-sm">
        <div class="space-y-2">
          <h2 class="text-xs font-semibold text-gray-600 uppercase tracking-wider mb-4">
            Views
          </h2>

          <router-link
            to="/calendar"
            class="flex items-center px-4 py-2 text-gray-700 rounded-lg hover:bg-blue-50 hover:text-blue-700 transition-colors group"
            :class="{ 'bg-blue-50 text-blue-700': $route.path === '/calendar' }"
          >
            <i class="fas fa-calendar-alt w-5 h-5 mr-3 group-hover:scale-110 transition-transform"></i>
            <span class="font-medium">Calendar</span>
          </router-link>

          <router-link
            to="/todolist"
            class="flex items-center px-4 py-2 text-gray-700 rounded-lg hover:bg-blue-50 hover:text-blue-700 transition-colors group"
            :class="{ 'bg-blue-50 text-blue-700': $route.path === '/todolist' }"
          >
            <i class="fas fa-check-circle w-5 h-5 mr-3 group-hover:scale-110 transition-transform"></i>
            <span class="font-medium">To-Do List</span>
          </router-link>

          <router-link
            to="/kanban"
            class="flex items-center px-4 py-2 text-gray-700 rounded-lg hover:bg-blue-50 hover:text-blue-700 transition-colors group"
            :class="{ 'bg-blue-50 text-blue-700': $route.path === '/kanban' }"
          >
            <i class="fas fa-columns w-5 h-5 mr-3 group-hover:scale-110 transition-transform"></i>
            <span class="font-medium">Kanban Board</span>
          </router-link>
        </div>

        <!-- Quick Stats -->
        <div class="mt-8 space-y-4">
          <h2 class="text-xs font-semibold text-gray-600 uppercase tracking-wider mb-4">
            Overview
          </h2>

          <div class="px-4 py-3 bg-blue-50 rounded-lg hover:shadow-md transition-shadow duration-300">
            <div class="flex items-center justify-between">
              <div>
                <div class="text-sm text-blue-800">Total Tasks</div>
                <div class="text-2xl font-bold text-blue-900">
                  {{ tasks.length }}
                </div>
              </div>
              <i class="fas fa-tasks text-blue-400 text-xl"></i>
            </div>
          </div>

          <div class="px-4 py-3 bg-green-50 rounded-lg hover:shadow-md transition-shadow duration-300">
            <div class="flex items-center justify-between">
              <div>
                <div class="text-sm text-green-800">Completed</div>
                <div class="text-2xl font-bold text-green-900">
                  {{ taskStats.completed }}
                </div>
              </div>
              <i class="fas fa-check-double text-green-400 text-xl"></i>
            </div>
          </div>

          <div class="px-4 py-3 bg-yellow-50 rounded-lg hover:shadow-md transition-shadow duration-300">
            <div class="flex items-center justify-between">
              <div>
                <div class="text-sm text-yellow-800">Due Today</div>
                <div class="text-2xl font-bold text-yellow-900">
                  {{ getDueToday() }}
                </div>
              </div>
              <i class="fas fa-clock text-yellow-400 text-xl"></i>
            </div>
          </div>
        </div>
      </div>

      <!-- Kanban Content -->
      <div class="flex-1 p-8">
        <div class="bg-white rounded-xl shadow-sm border border-gray-200">
          <!-- Controls Panel -->
      <div class="w-full max-w-4xl bg-white shadow-lg rounded-lg overflow-hidden">
        <!-- Main Controls Header -->
        <div class="flex justify-between items-center p-4 bg-gray-50">
          <button @click="isFilterMenuOpen = !isFilterMenuOpen"
            class="flex items-center px-4 py-2 rounded-md hover:bg-blue-50 transition-all duration-200 border border-gray-200 hover:border-blue-300 hover:shadow-md group">
            <i class="fas fa-sliders-h mr-2 text-blue-600 group-hover:rotate-180 transition-transform duration-300"></i>
            <span class="relative text-gray-700 group-hover:text-blue-600">
              Filter & Sort
              <span
                class="absolute bottom-0 left-0 w-full h-0.5 bg-blue-600 transform scale-x-0 group-hover:scale-x-100 transition-transform duration-200">
              </span>
            </span>
            <i :class="[
              'fas',
              'ml-2',
              isFilterMenuOpen ? 'fa-chevron-up' : 'fa-chevron-down',
              'transform group-hover:translate-y-0.5 transition-transform text-blue-600',
            ]"></i>
          </button>
        </div>

        <!-- Expandable Filter Panel -->
        <div v-show="isFilterMenuOpen" class="border-t border-gray-200">
          <div class="p-4 space-y-4">
            <!-- Filter Section -->
            <div class="space-y-3">
              <h3 class="font-semibold text-gray-700">Filter By</h3>
              <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                <!-- Priority Filter -->
                <div class="space-y-1">
                  <label class="text-sm text-gray-600">Priority</label>
                  <select v-model="selectedPriority" @change="filterTasks"
                    class="w-full border border-gray-300 rounded-md p-2 text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500">
                    <option value="">All Priorities</option>
                    <option value="High">High</option>
                    <option value="Medium">Medium</option>
                    <option value="Low">Low</option>
                  </select>
                </div>

                <!-- Assigner Filter -->
                <div class="space-y-1" v-if="uniqueAssigners">
                  <label class="text-sm text-gray-600">Assigner</label>
                  <select v-model="selectedAssigner" @change="filterTasks"
                    class="w-full border border-gray-300 rounded-md p-2 text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500">
                    <option value="">All Assigners</option>
                    <option v-for="user in uniqueAssigners" :key="user.id" :value="user.id">
                      {{ user.name }}
                    </option>
                  </select>
                </div>

                <!-- Project Filter -->
                <div class="space-y-1">
                  <label class="text-sm text-gray-600">Project</label>
                  <select v-model="selectedProject" @change="filterTasks"
                    class="w-full border border-gray-300 rounded-md p-2 text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500">
                    <option value="">All Projects</option>
                    <option v-for="project in uniqueProjects" :key="project.id" :value="project.id">
                      {{ project.name }}
                    </option>
                  </select>
                </div>

                <!-- Organization Filter -->
                <div class="space-y-1">
                  <label class="text-sm text-gray-600">Organization</label>
                  <select v-model="selectedOrg" @change="filterTasks"
                    class="w-full border border-gray-300 rounded-md p-2 text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500">
                    <option value="">All Organizations</option>
                    <option v-for="org in uniqueOrgs" :key="org.id" :value="org.id">
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
                <select v-model="sortField"
                  class="border border-gray-300 rounded-md p-2 text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500">
                  <option value="dueDate">Due Date</option>
                  <option value="taskName">Task Name</option>
                  <option value="taskPriority">Priority</option>
                </select>
                <button @click="toggleSortOrder"
                  class="flex items-center px-4 py-2 border border-gray-300 rounded-md hover:bg-gray-50 transition">
                  <i :class="[
                    'fas',
                    sortOrder === 'asc'
                      ? 'fa-sort-amount-down-alt'
                      : 'fa-sort-amount-up-alt',
                    'mr-2',
                  ]"></i>
                  {{ sortOrder === "asc" ? "Ascending" : "Descending" }}
                </button>
              </div>
            </div>

            <!-- Active Filters Display -->
            <div v-if="hasActiveFilters" class="pt-3 border-t border-gray-200">
              <h3 class="text-sm font-medium text-gray-700 mb-2">
                Active Filters:
              </h3>
              <div class="flex flex-wrap gap-2">
                <span v-if="selectedPriority"
                  class="inline-flex items-center px-3 py-1 rounded-full text-sm bg-blue-100 text-blue-800">
                  Priority: {{ selectedPriority }}
                  <button @click="selectedPriority = ''" class="ml-2 text-blue-600 hover:text-blue-800">
                    <i class="fas fa-times"></i>
                  </button>
                </span>
                <span v-if="selectedAssigner"
                  class="inline-flex items-center px-3 py-1 rounded-full text-sm bg-blue-100 text-blue-800">
                  Assigned By: {{ getAssignerName(selectedAssigner) }}
                  <button @click="selectedAssigner = ''" class="ml-2 text-blue-600 hover:text-blue-800">
                    <i class="fas fa-times"></i>
                  </button>
                </span>
                <span v-if="selectedProject"
                  class="inline-flex items-center px-3 py-1 rounded-full text-sm bg-blue-100 text-blue-800">
                  Project: {{ getProjectName(selectedProject) }}
                  <button @click="selectedProject = ''" class="ml-2 text-blue-600 hover:text-blue-800">
                    <i class="fas fa-times"></i>
                  </button>
                </span>
                <span v-if="selectedOrg"
                  class="inline-flex items-center px-3 py-1 rounded-full text-sm bg-blue-100 text-blue-800">
                  Organization: {{ getOrgName(selectedOrg) }}
                  <button @click="selectedOrg = ''" class="ml-2 text-blue-600 hover:text-blue-800">
                    <i class="fas fa-times"></i>
                  </button>
                </span>
                <!-- Add similar spans for other active filters -->
              </div>
            </div>
          </div>
        </div>
      </div>

          <!-- Kanban Board -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6 p-6">
            <!-- Incomplete Tasks Column -->
            <div class="space-y-4">
              <h2 class="text-xl font-bold text-gray-900">Incomplete Tasks</h2>
              <div class="space-y-4">
                <TaskCard v-for="task in filteredIncompleteTasks" 
                         :key="task.taskID" 
                         :task="task" />
              </div>
            </div>

            <!-- Complete Tasks Column -->
            <div class="space-y-4">
              <h2 class="text-xl font-bold text-gray-900">Complete Tasks</h2>
              <div class="space-y-4">
                <TaskCard v-for="task in filteredCompleteTasks" 
                         :key="task.taskID" 
                         :task="task" />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import NavBar from "@/components/NavBar.vue";
import { mapState, mapActions } from "vuex";
import TaskCard from "@/components/TaskCard.vue";

export default {
  components: {
    NavBar,
    TaskCard,
  },

  data() {
    return {
      isFilterMenuOpen: false,
      selectedPriority: '',
      selectedAssigner: null,
      selectedAssignerName: '',
      selectedProject: '',
      selectedOrg: '',
      sortField: 'dueDate',
      sortOrder: 'asc',
      uniqueAssigners: {},
      uniqueProjects: {},
      uniqueOrgs: {},
      searchQuery: '',
    };
  },

  async mounted() {
    await this.fetchTasks();
    await this.fetchUniqueAssigners();
    await this.fetchUniqueProjects();
    await this.fetchUniqueOrgs();
  },

  computed: {
    ...mapState('tasks', ['tasks']),
    ...mapState('auth', ['currentUser', "isLoggedIn"]),

    hasActiveFilters() {
      return this.selectedPriority || this.selectedAssigner ||
        this.selectedProject || this.selectedOrg;
    },

    filteredIncompleteTasks() {
      return this.filterTasks(false);
    },
    filteredCompleteTasks() {
      return this.filterTasks(true);
    },
    taskStats() {
    return {
      total: this.tasks.length,
      completed: this.filteredCompleteTasks.length,
      pending: this.filteredIncompleteTasks.length
    }
  }
  },

  methods: {
    ...mapActions('tasks', ['fetchTasks']),

    async fetchUniqueAssigners() {
      // Get unique assigner IDs from tasks
      const assignerIDs = [...new Set(this.tasks.map(task => task.assignerID))];

      // Use Promise.all to wait for all user fetches to complete
      const assigners = await Promise.all(assignerIDs.map(async (id) => {
        const user = await this.$store.dispatch("users/fetchUserAccountByID", id);
        return {
          id,
          name: user ? user.userName : 'Unknown User', // Adjust based on your user object structure
        };
      }));
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
      const orgIDs = [
        ...new Set(this.tasks.map((task) => task.parentOrgID)),
      ];
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
      const assigner = this.uniqueAssigners.find(assigner => assigner.id === assignerID);
      return assigner ? assigner.name : 'Unknown User';
    },
    getProjectName(projectID) {
      const project = this.uniqueProjects.find(
        (project) => project.id === projectID,
      );
      return project ? project.name : "Unknown Project";
    },

    getOrgName(orgID) {
      const org = this.uniqueOrgs.find(
        (org) => org.id === orgID,
      );
      return org ? org.name : "Unknown Organization";
    },

    toggleSortOrder() {
      this.sortOrder = this.sortOrder === 'asc' ? 'desc' : 'asc';
    },

    goToCreateTask() {
      this.$router.push({ name: "createTask" });
    },
    goToTDList() {
      this.$router.push({ name: "TDList" });
    },

    filterTasks(isComplete) {
      let filtered = this.tasks.filter(task => task.complete === isComplete);

      // Apply filters
      if (this.selectedPriority) {
        filtered = filtered.filter(task => task.taskPriority === this.selectedPriority);
      }

      if (this.selectedAssigner) {
        filtered = filtered.filter(task => task.assignerID === this.selectedAssigner);
      }

      if (this.selectedProject) {
        filtered = filtered.filter(task => task.parentProjectID === this.selectedProject);
      }

      if (this.selectedOrg) {
        filtered = filtered.filter(task => task.parentOrgID === this.selectedOrg);
      }

      // Apply sorting
      filtered.sort((a, b) => {
        const direction = this.sortOrder === 'asc' ? 1 : -1;
        const priorityOrder = { High: 3, Medium: 2, Low: 1 };
        switch (this.sortField) {
          case 'dueDate':
            return direction * (new Date(a.dueDate) - new Date(b.dueDate));
          case 'taskName':
            return direction * a.taskName.localeCompare(b.taskName);
          case 'priority':
            return direction * (priorityOrder[a.taskPriority] - priorityOrder[b.taskPriority]);
          default:
            return 0;
        }
      });

      return filtered;
    },
    getDueToday() {
    const today = new Date().toISOString().split('T')[0];
    return this.tasks.filter(task => 
      task.dueDate?.split('T')[0] === today && !task.isComplete
    ).length;
  },

  getTaskPriorityColor(priority) {
    return {
      'High': 'text-red-600',
      'Medium': 'text-yellow-600',
      'Low': 'text-green-600'
    }[priority] || 'text-gray-600';
  },
  },
};
</script>

<style>
.router-link-active {
  @apply bg-blue-50 text-blue-700;
}

.sidebar-stats {
  @apply transition-all duration-200;
}

.sidebar-stats:hover {
  @apply transform scale-105;
}

.task-card-enter-active,
.task-card-leave-active {
  transition: all 0.3s ease;
}

.task-card-enter-from,
.task-card-leave-to {
  opacity: 0;
  transform: translateY(30px);
}
</style>