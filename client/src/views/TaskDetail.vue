<template>
  <NavBar />
  <div class="min-h-screen bg-gray-50 p-8">
    <div class="max-w-4xl mx-auto bg-white rounded-lg shadow-lg p-6">
      <div v-if="currentTask" class="space-y-6">
        <!-- Task Header -->
        <div class="flex justify-between items-start">
          <h1 class="text-2xl font-bold text-primary">
            {{ currentTask.taskName }}
          </h1>
          <div class="flex space-x-4">
            <!-- Status Badge -->
            <span
              class="px-3 py-1 rounded-full text-sm font-semibold"
              :class="{
                'bg-green-100 text-green-800': currentTask.complete,
                'bg-red-100 text-red-800': !currentTask.complete,
              }"
            >
              {{ currentTask.complete ? "Complete" : "Incomplete" }}
            </span>
          </div>
        </div>

        <!-- Task Details -->
        <div class="space-y-4">
          <div class="text-gray-600">
            <h2 class="text-lg font-semibold mb-2">Description</h2>
            <p>{{ currentTask.taskDescription }}</p>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <h2 class="text-lg font-semibold mb-2">Priority</h2>
              <span
                class="px-2 py-1 rounded-md text-sm font-medium"
                :class="{
                  'bg-red-100 text-red-800':
                    currentTask.taskPriority === 'High',
                  'bg-yellow-100 text-yellow-800':
                    currentTask.taskPriority === 'Medium',
                  'bg-blue-100 text-blue-800':
                    currentTask.taskPriority === 'Low',
                }"
              >
                {{ currentTask.taskPriority }}
              </span>
            </div>

            <div>
              <h2 class="text-lg font-semibold mb-2">Due Date</h2>
              <p>{{ formatDate(currentTask.dueDate) }}</p>
            </div>
          </div>

          <!-- Task Actions -->
          <div class="flex justify-end space-x-4 mt-6">
            <button
              @click="goBack"
              class="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300"
            >
              Back
            </button>
            <button
              v-if="!currentTask.complete"
              @click="markAsComplete"
              class="px-4 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600"
            >
              Mark Complete
            </button>
          </div>
        </div>
      </div>

      <!-- Loading State -->
      <div v-else class="flex justify-center items-center h-64">
        <p>Loading task details...</p>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters, mapActions } from "vuex";
import NavBar from "@/components/NavBar.vue";

export default {
  name: "TaskDetail",

  components: {
    NavBar,
  },

  props: {
    taskId: {
      type: [String, Number],
      required: true,
    },
  },

  computed: {
    ...mapGetters("tasks", [
      "isLoading",
      "hasError",
      "errorMessage",
      "currentTask",
      "isUpdateLoading",
      "updateError",
      "updateSuccess",
    ]),
  },

  methods: {
    ...mapActions("tasks", ["fetchTask", "updateTask"]),

    async loadTask() {
      try {
        await this.fetchTask(this.$route.params.taskId);
        console.log("Got new task: ", this.currentTask);
      } catch (error) {
        console.error("Failed to load task:", error);
      }
    },

    async retryLoading() {
      await this.loadTask();
    },

    async handleUpdateTask(taskData) {
      try {
        await this.updateTask({
          taskId: this.taskId,
          taskData,
        });
      } catch (error) {
        console.error("Failed to update task:", error);
      }
    },
    formatDate(date) {
      if (!date) return "No due date";
      return new Date(date).toLocaleDateString("en-US", {
        year: "numeric",
        month: "short",
        day: "numeric",
      });
    },
    async markAsComplete() {
      try {
        const dueDate = `${this.currentTask.dueDate}T15:30:00.000z`;

        const taskData = {
          taskName: this.currentTask.taskName,
          taskDescription: this.currentTask.taskDescription,
          assignerID: this.currentTask.assignerID,
          taskPriority: this.currentTask.taskPriority,
          dueDate: dueDate,
          isComplete: true,
        };

        await this.updateTask({
          taskId: this.taskId,
          taskData: taskData,
        });

        // Reload the task to get the updated data
        await this.loadTask();
      } catch (error) {
        console.error("Failed to mark task as complete:", error);
        // Error will be handled by Vuex store and displayed via updateStatus
      }
    },
    goBack() {
      this.$router.go(-1);
    },
  },

  created() {
    this.loadTask();
  },
};
</script>
