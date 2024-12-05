<template>
  <div v-if="isLoggedIn" class="relative min-h-screen bg-gray-50">
    <NavBar />
    <div class="flex min-h-[calc(100vh-64px)]">
      <TaskSidebarComponent :tasks="tasks" />

      <div class="flex-1 overflow-x-hidden">
        <div
          class="flex flex-col items-center w-full max-w-7xl px-4 mx-auto space-y-8 mt-12"
        >
          <h1
            class="text-4xl font-bold text-hunter-green mb-6 flex-grow text-center"
          >
            Kanban Board
          </h1>

          <div
            class="h-1 bg-accent drop-shadow-lg rounded mx-16 flex w-screen"
          />

          <SearchComponent
            v-model="searchQuery"
            width="70%"
            placeholder="Search by task name or date (MM/DD/YYYY)"
            :showDateHelp="true"
            class="w-full justify-center"
          />

          <ControlPanelComponent
            :tasks="tasks"
            :unique-assigners="uniqueAssigners"
            :unique-projects="uniqueProjects"
            :unique-orgs="uniqueOrgs"
            :search-query="searchQuery"
            @tasks-filtered="handleFilteredTasks"
          />

          <!-- Kanban Board -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6 p-6">
            <!-- Incomplete Tasks Column -->
            <div class="space-y-4">
              <h2 class="text-xl font-bold text-gray-900">Incomplete Tasks</h2>
              <div class="space-y-4">
                <TaskCard
                  v-for="task in filteredIncompleteTasks"
                  :key="task.taskID"
                  :task="task"
                />
              </div>
            </div>

            <!-- Complete Tasks Column -->
            <div class="space-y-4">
              <h2 class="text-xl font-bold text-gray-900">Complete Tasks</h2>
              <div class="space-y-4">
                <TaskCard
                  v-for="task in filteredCompleteTasks"
                  :key="task.taskID"
                  :task="task"
                />
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
import SearchComponent from "@/components/SearchComponent.vue";
import TaskSidebarComponent from "@/components/TaskSidebarComponent.vue";
import ControlPanelComponent from "@/components/ControlPanelComponent.vue";

export default {
  components: {
    NavBar,
    TaskCard,
    SearchComponent,
    TaskSidebarComponent,
    ControlPanelComponent,
    ControlPanelComponent,
  },

  data() {
    return {
      uniqueAssigners: [],
      filteredCompleteTasks: [],
      filteredIncompleteTasks: [],
      uniqueProjects: [],
      uniqueOrgs: [],
      searchQuery: "",
    };
  },

  computed: {
    ...mapState("tasks", ["tasks"]),
    ...mapState("auth", ["currentUser", "isLoggedIn"]),
  },

  watch: {
    searchQuery: {
      immediate: true,
      handler() {
        this.$nextTick(() => {
          if (this.$refs.ControlPanelComponent) {
            this.$refs.ControlPanelComponent.filterTasks();
          }
        });
      },
    },
    tasks: {
      immediate: true,
      handler() {
        this.$nextTick(() => {
          if (this.$refs.ControlPanelComponent) {
            this.$refs.ControlPanelComponent.emitFilteredTasks();
          }
        });
      },
    },
  },

  async mounted() {
    await this.initalize();
  },

  methods: {
    ...mapActions("tasks", ["fetchTasks"]),

    async initalize() {
      await this.fetchTasks();
      await this.fetchUniqueAssigners();
      await this.fetchUniqueProjects();
      await this.fetchUniqueOrgs();
      this.$nextTick(() => {
        if (this.$refs.ControlPanelComponent) {
          this.$refs.ControlPanelComponent.emitFilteredTasks();
        }
      });
    },

    handleFilteredTasks({ complete, incomplete }) {
      this.filteredCompleteTasks = complete;
      this.filteredIncompleteTasks = incomplete;
    },

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

    toggleSortOrder() {
      this.sortOrder = this.sortOrder === "asc" ? "desc" : "asc";
    },

    goToCreateTask() {
      this.$router.push({ name: "createTask" });
    },
    goToTDList() {
      this.$router.push({ name: "TDList" });
    },
    getDueToday() {
      const today = new Date().toISOString().split("T")[0];
      return this.tasks.filter(
        (task) => task.dueDate?.split("T")[0] === today && !task.isComplete,
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