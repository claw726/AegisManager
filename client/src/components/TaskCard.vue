<template>
  <div
    @click="goToTask()"
    class="flex flex-col justify-between p-6 rounded-lg shadow-lg hover:shadow-xl transition-shadow duration-300 bg-white border border-gray-100 hover:border-accent cursor-pointer h-full"
  >
    <!-- Header Section with Title and Status -->
    <div class="flex justify-between items-start mb-4">
      <h2 class="text-xl font-bold text-primary truncate max-w-[70%]">
        {{ task.taskName }}
      </h2>
      <span
        class="px-3 py-1 rounded-full text-sm font-semibold"
        :class="{
          'bg-green-100 text-green-800': task.complete,
          'bg-red-100 text-red-800': !task.complete,
        }"
      >
        {{ task.complete ? "Complete" : "Incomplete" }}
      </span>
    </div>

    <!-- Description -->
    <p class="text-gray-600 mb-4 line-clamp-2 text-sm">
      {{ task.taskDescription }}
    </p>

    <!-- Task Details -->
    <div class="space-y-2 mt-auto">
      <!-- Priority Badge -->
      <div class="flex items-center">
        <span
          class="px-2 py-1 rounded-md text-xs font-medium"
          :class="{
            'bg-red-100 text-red-800': task.taskPriority === 'High',
            'bg-yellow-100 text-yellow-800': task.taskPriority === 'Medium',
            'bg-blue-100 text-blue-800': task.taskPriority === 'Low',
          }"
        >
          {{ task.taskPriority }} Priority
        </span>
      </div>

      <!-- Assigner -->
      <div class="flex items-center text-sm text-gray-600">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          class="h-4 w-4 mr-2"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
          />
        </svg>
        <span class="truncate">{{ task.assignerID }}</span>
      </div>

      <!-- Due Date -->
      <div class="flex items-center text-sm text-gray-600">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          class="h-4 w-4 mr-2"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"
          />
        </svg>
        <span>Due: {{ formatDate(task.dueDate) }}</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    task: {
      type: Object,
      required: true,
    },
  },
  methods: {
    goToTask() {
      this.$router.push({
        name: "TaskDetail",
        params: {
          taskId: this.task.taskID,
        },
      });
    },
    formatDate(date) {
      if (!date) return "No due date";
      return new Date(date).toLocaleDateString("en-US", {
        year: "numeric",
        month: "short",
        day: "numeric",
      });
    },
  },
};
</script>
