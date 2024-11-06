<template>
  <div class="w-64 bg-white border-r border-gray-200 p-4 shadow-sm">
    <div class="space-y-2">
      <h2
        class="text-xs font-semibold text-gray-600 uppercase tracking-wider mb-4"
      >
        Views
      </h2>

      <router-link
        to="/calendar"
        class="flex items-center px-4 py-2 text-gray-700 rounded-lg hover:bg-blue-50 hover:text-blue-700 transition-colors group"
        :class="{ 'bg-blue-50 text-blue-700': $route.path === '/calendar' }"
      >
        <i
          class="fas fa-calendar-alt w-5 h-5 mr-3 group-hover:scale-110 transition-transform"
        ></i>
        <span class="font-medium">Calendar</span>
      </router-link>

      <router-link
        to="/todolist"
        class="flex items-center px-4 py-2 text-gray-700 rounded-lg hover:bg-blue-50 hover:text-blue-700 transition-colors group"
        :class="{ 'bg-blue-50 text-blue-700': $route.path === '/todolist' }"
      >
        <i
          class="fas fa-check-circle w-5 h-5 mr-3 group-hover:scale-110 transition-transform"
        ></i>
        <span class="font-medium">To-Do List</span>
      </router-link>

      <router-link
        to="/kanban"
        class="flex items-center px-4 py-2 text-gray-700 rounded-lg hover:bg-blue-50 hover:text-blue-700 transition-colors group"
        :class="{ 'bg-blue-50 text-blue-700': $route.path === '/kanban' }"
      >
        <i
          class="fas fa-columns w-5 h-5 mr-3 group-hover:scale-110 transition-transform"
        ></i>
        <span class="font-medium">Kanban Board</span>
      </router-link>
    </div>

    <!-- Quick Stats -->
    <div class="mt-8 space-y-4">
      <h2
        class="text-xs font-semibold text-gray-600 uppercase tracking-wider mb-4"
      >
        Overview
      </h2>

      <div
        class="px-4 py-3 bg-blue-50 rounded-lg hover:shadow-md transition-shadow duration-300"
      >
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

      <div
        class="px-4 py-3 bg-green-50 rounded-lg hover:shadow-md transition-shadow duration-300"
      >
        <div class="flex items-center justify-between">
          <div>
            <div class="text-sm text-green-800">Completed</div>
            <div class="text-2xl font-bold text-green-900">
              {{ completedTasksCount }}
            </div>
          </div>
          <i class="fas fa-check-double text-green-400 text-xl"></i>
        </div>
      </div>

      <div
        class="px-4 py-3 bg-yellow-50 rounded-lg hover:shadow-md transition-shadow duration-300"
      >
        <div class="flex items-center justify-between">
          <div>
            <div class="text-sm text-yellow-800">Due Today</div>
            <div class="text-2xl font-bold text-yellow-900">{{ dueToday }}</div>
          </div>
          <i class="fas fa-clock text-yellow-400 text-xl"></i>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "TaskSidebarComponent",
  props: {
    tasks: {
      type: Array,
      required: true,
    },
  },
  computed: {
    completedTasksCount() {
      return this.tasks.filter((task) => task.complete).length;
    },
    dueToday() {
      const today = new Date().toISOString().split("T")[0];
      return this.tasks.filter(
        (task) => task.dueDate?.split("T")[0] === today && !task.complete,
      ).length;
    },
  },
};
</script>
