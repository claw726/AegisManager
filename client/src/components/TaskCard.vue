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
          'bg-green-800 text-white': task.complete,
          'bg-orange-800 text-white': !task.complete,
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
        <i class="fas fa-user h-4 w-4 mr-2"></i>
        <span class="truncate">{{ creator.userName }}</span>
      </div>

      <!-- Due Date -->
      <div class="flex items-center text-sm text-gray-600">
        <i class="fas fa-calendar h-4 w-4 mr-2"></i>
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
  data() {
    return {
      creator: "",
    };
  },
  async mounted() {
    await this.getCreatorData();
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
    async getCreatorData() {
      try {
        this.creator = await this.$store.dispatch(
          "users/fetchUserAccountByID",
          this.task.assignerID
        );
      } catch (error) {
        this.creator = "Unknown";
      }
    },
  },
};
</script>
