<template>
  <NavBar />
  <div class="bg-background flex flex-col items-center min-h-screen h-full">
    <div class="flex flex-col items-center space-y-8 mt-12 w-full max-w-7xl px-4 flex-grow">
      <!-- Added max-w-7xl and px-4 here -->
      <div class="flex justify-between items-center w-full"> <!-- Flex container for text and button -->
        <h1 class="text-4xl font-bold text-hunter-green mb-6 flex-grow text-center">Kanban Board List</h1>
        <Button class="dashboard-button ml-4" @click="goToTDList">To Do List</Button>
      </div>
      <div class="h-1 bg-accent drop-shadow-lg rounded mx-16 flex w-screen" />

      <!-- Controls Panel -->
      <div class="w-full max-w-4xl bg-white shadow-lg rounded-lg overflow-hidden">
        <!-- Main Controls Header -->
        <div class="flex justify-between items-center p-4 bg-gray-50">
          <button class="bg-blue-600 text-white rounded-md px-4 py-2 hover:bg-blue-700 transition"
            @click="goToCreateTask">
            <i class="fas fa-plus mr-2"></i>Create Task
          </button>

          <button @click="isFilterMenuOpen = !isFilterMenuOpen" class="flex items-center px-4 py-2 rounded-md 
          hover:bg-blue-50 transition-all duration-200 
            border border-gray-200 hover:border-blue-300 
            hover:shadow-md group">
            <i class="fas fa-sliders-h mr-2 text-blue-600 
                      group-hover:rotate-180 transition-transform duration-300"></i>
            <span class="relative text-gray-700 group-hover:text-blue-600">
              Filter & Sort
              <span class="absolute bottom-0 left-0 w-full h-0.5 bg-blue-600 
                            transform scale-x-0 group-hover:scale-x-100 
                            transition-transform duration-200">
              </span>
            </span>
            <i :class="[
              'fas',
              'ml-2',
              isFilterMenuOpen ? 'fa-chevron-up' : 'fa-chevron-down',
              'transform group-hover:translate-y-0.5 transition-transform text-blue-600'
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
                  <i
                    :class="['fas', sortOrder === 'asc' ? 'fa-sort-amount-down-alt' : 'fa-sort-amount-up-alt', 'mr-2']"></i>
                  {{ sortOrder === 'asc' ? 'Ascending' : 'Descending' }}
                </button>
              </div>
            </div>

            <!-- Active Filters Display -->
            <div v-if="hasActiveFilters" class="pt-3 border-t border-gray-200">
              <h3 class="text-sm font-medium text-gray-700 mb-2">Active Filters:</h3>
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
                <!-- Add similar spans for other active filters -->
              </div>
            </div>
          </div>
        </div>
      </div>


      <div class="container">
        <!-- Task Grid -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-2 gap-4 w-full max-w-7xl px-4">
          <!-- Incomplete Task List -->
          <div class="grid grid-cols-1 gap-4 h-48">
            <h2 class="text-4xl font-bold text-hunter-green mb-6 flex-grow text-center">Incomplete Tasks</h2>
            <!-- Label for Incomplete Tasks -->
            <template v-if="filteredTasks && filteredTasks.length > 0">
              <TaskCard v-for="task in filteredIncompleteTasks" :key="task.taskID" :task="task" />
            </template>
            <div v-else class="col-span-full text-center text-gray-600">
              No tasks available.
            </div>
          </div>

          <!-- Complete Task List -->
          <div class="grid grid-cols-1 gap-4 h-48">
            <h2 class="text-4xl font-bold text-hunter-green mb-6 flex-grow text-center">Complete Tasks</h2>
            <!-- Label for Complete Tasks -->
            <template v-if="filteredTasks && filteredTasks.length > 0">
              <TaskCard v-for="task in filteredCompleteTasks" :key="task.taskID" :task="task" />
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
    };
  },

  async mounted() {
    this.fetchTasks();
    await this.fetchUniqueAssigners();
  },

  computed: {
    ...mapState('tasks', ['tasks']),
    ...mapState('auth', ['currentUser']),

    hasActiveFilters() {
      return this.selectedPriority || this.selectedAssigner ||
        this.selectedProject || this.selectedOrg;
    },

    filteredIncompleteTasks() {
      return this.filteredTasks(0);
    },
    filteredCompleteTasks() {
      return this.filteredTasks(1);
    },

    uniqueProjects() {
      return [...new Set(this.tasks.map(task => ({
        id: task.parentProjectID,
        name: task.parentProject
      })))];
    },

    uniqueOrgs() {
      return [...new Set(this.tasks.map(task => ({
        id: task.parentOrgID,
        name: task.parentOrg
      })))];
    },
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

    getAssignerName(assignerID) {
      const assigner = this.uniqueAssigners.find(assigner => assigner.id === assignerID);
      return assigner ? assigner.name : 'Unknown User';
    },

    filterTasks() {
      // Method kept for potential future use
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

    filteredTasks(isComplete) {
      let tasks = this.tasks;
      if (isComplete == 1) {
        tasks = tasks.filter(task => task.complete);
      }
      else {
        tasks = tasks.filter(task => !task.complete);
      }
      // Apply filters
      if (this.selectedPriority) {
        tasks = tasks.filter(task => task.taskPriority === this.selectedPriority);
      }
      if (this.selectedAssigner) {
        tasks = tasks.filter(task => task.assignerID === this.selectedAssigner);
      }
      if (this.selectedProject) {
        tasks = tasks.filter(task => task.parentProjectID === this.selectedProject);
      }
      if (this.selectedOrg) {
        tasks = tasks.filter(task => task.parentOrgID === this.selectedOrg);
      }

      // Apply sorting
      tasks.sort((a, b) => {
        let comparison = 0;
        const priorityOrder = { High: 1, Medium: 2, Low: 3 };
        switch (this.sortField) {
          case 'dueDate':
            comparison = new Date(a.dueDate) - new Date(b.dueDate);
            break;
          case 'taskName':
            comparison = a.taskName.localeCompare(b.taskName);
            break;
          case 'taskPriority':

            comparison = priorityOrder[a.taskPriority] - priorityOrder[b.taskPriority];
            break;
        }
        return this.sortOrder === 'asc' ? comparison : -comparison;
      });

      return tasks;
    },
  },
};
</script>