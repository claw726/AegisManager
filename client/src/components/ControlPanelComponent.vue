<template>
  <div class="w-full max-w-4xl bg-white shadow-lg rounded-lg overflow-hidden">
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
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            <!-- Priority Filter -->
            <div class="space-y-1">
              <label class="text-sm text-gray-600">Priority</label>
              <select
                v-model="filters.priority"
                class="w-full border border-gray-300 rounded-md p-2 text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                @change="emitFilterChange"
              >
                <option value="">All Priorities</option>
                <option value="High">High</option>
                <option value="Medium">Medium</option>
                <option value="Low">Low</option>
              </select>
            </div>

            <!-- Assigner Filter -->
            <div v-if="uniqueAssigners.length" class="space-y-1">
              <label class="text-sm text-gray-600">Assigner</label>
              <select
                v-model="filters.assigner"
                class="w-full border border-gray-300 rounded-md p-2 text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                @change="emitFilterChange"
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
                v-model="filters.project"
                class="w-full border border-gray-300 rounded-md p-2 text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                @change="emitFilterChange"
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
                v-model="filters.organization"
                class="w-full border border-gray-300 rounded-md p-2 text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                @change="emitFilterChange"
              >
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
            <select
              v-model="sorting.field"
              class="border border-gray-300 rounded-md p-2 text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              @change="emitSortChange"
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
                  sorting.order === 'asc'
                    ? 'fa-sort-amount-down-alt'
                    : 'fa-sort-amount-up-alt',
                  'mr-2',
                ]"
              ></i>
              {{ sorting.order === "asc" ? "Ascending" : "Descending" }}
            </button>
          </div>
        </div>

        <!-- Active Filters Display -->
        <div v-if="hasActiveFilters" class="pt-3 border-t border-gray-200">
          <h3 class="text-sm font-medium text-gray-700 mb-2">
            Active Filters:
          </h3>
          <div class="flex flex-wrap gap-2">
            <span
              v-if="filters.priority"
              class="inline-flex items-center px-3 py-1 rounded-full text-sm bg-blue-100 text-blue-800"
            >
              Priority: {{ filters.priority }}
              <button
                class="ml-2 text-blue-600 hover:text-blue-800"
                @click="clearFilter('priority')"
              >
                <i class="fas fa-times"></i>
              </button>
            </span>
            <!-- Similar spans for other active filters -->
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "ControlPanelComponent",

  props: {
    tasks: {
      type: Array,
      required: true,
    },
    uniqueAssigners: {
      type: Array,
      default: () => [],
    },
    uniqueProjects: {
      type: Array,
      default: () => [],
    },
    uniqueOrgs: {
      type: Array,
      default: () => [],
    },
    searchQuery: {
      type: String,
      default: "",
    },
  },

  emits: ["tasks-filtered"],

  data() {
    return {
      isFilterMenuOpen: false,
      filters: {
        priority: "",
        assigner: "",
        project: "",
        organization: "",
      },
      sorting: {
        field: "dueDate",
        order: "asc",
      },
    };
  },

  computed: {
    hasActiveFilters() {
      return Object.values(this.filters).some((value) => value !== "");
    },

    filteredAndSortedTasks() {
      let filtered = this.filterTasks(this.tasks);
      return this.sortTasks(filtered);
    },
  },

  watch: {
    // Watch for any changes in filters, sorting, or searchQuery
    filters: {
      deep: true,
      handler() {
        this.emitFilteredTasks();
      },
    },
    sorting: {
      deep: true,
      handler() {
        this.emitFilteredTasks();
      },
    },
    searchQuery: {
      immediate: true,
      handler() {
        this.emitFilteredTasks();
      },
    },
    tasks: {
      immediate: true,
      handler() {
        this.emitFilteredTasks();
      },
    },
  },

  mounted() {
    this.$nextTick(() => {
      this.emitFilteredTasks();
    });
  },

  methods: {
    filterTasks(tasks) {
      let filtered = [...tasks];

      // Apply filters
      if (this.filters.priority) {
        filtered = filtered.filter(
          (task) => task.taskPriority === this.filters.priority,
        );
      }

      if (this.filters.assigner) {
        filtered = filtered.filter(
          (task) => task.assignerID === this.filters.assigner,
        );
      }

      if (this.filters.project) {
        filtered = filtered.filter(
          (task) => task.parentProjectID === this.filters.project,
        );
      }

      if (this.filters.organization) {
        filtered = filtered.filter(
          (task) => task.parentOrgID === this.filters.organization,
        );
      }

      // Apply search query
      if (this.searchQuery) {
        if (this.validateDateFormat(this.searchQuery)) {
          const searchMonth = this.searchQuery.substring(0, 2);
          const searchDay = this.searchQuery.substring(3, 5);
          const searchYear = this.searchQuery.substring(6, 10);
          const formattedDay = `${searchYear}-${searchMonth}-${searchDay}`;

          filtered = filtered.filter((task) =>
            task.dueDate.includes(formattedDay),
          );
        } else {
          filtered = filtered.filter((task) =>
            task.taskName
              .toLowerCase()
              .includes(this.searchQuery.toLowerCase()),
          );
        }
      }

      return filtered;
    },

    sortTasks(tasks) {
      const direction = this.sorting.order === "asc" ? 1 : -1;
      const priorityOrder = { High: 3, Medium: 2, Low: 1 };

      return [...tasks].sort((a, b) => {
        switch (this.sorting.field) {
          case "dueDate":
            return direction * (new Date(a.dueDate) - new Date(b.dueDate));
          case "taskName":
            return direction * a.taskName.localeCompare(b.taskName);
          case "taskPriority":
            return (
              direction *
              (priorityOrder[a.taskPriority] - priorityOrder[b.taskPriority])
            );
          default:
            return 0;
        }
      });
    },

    emitFilteredTasks() {
      const filteredTasks = this.filteredAndSortedTasks;
      this.$emit("tasks-filtered", {
        complete: filteredTasks.filter((task) => task.complete),
        incomplete: filteredTasks.filter((task) => !task.complete),
      });
    },

    toggleSortOrder() {
      this.sorting.order = this.sorting.order === "asc" ? "desc" : "asc";
    },

    clearFilter(filterType) {
      this.filters[filterType] = "";
    },

    validateDateFormat(dateStr) {
      const datePattern = /^\d{2}\/\d{2}\/\d{4}$/;
      return datePattern.test(dateStr);
    },
  },
};
</script>