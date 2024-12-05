<!-- TaskDetailPopup.vue -->
<template>
  <div
    v-if="visible"
    class="absolute z-50 bg-white rounded-lg shadow-lg border border-gray-200 p-4 min-w-[250px]"
    :style="popupStyle"
  >
    <!-- Arrow pointing down -->
    <div
      class="absolute -bottom-2 left-1/2 transform -translate-x-1/2 w-4 h-4 rotate-45 bg-white border-r border-b border-gray-200"
    ></div>

    <!-- Content -->
    <div class="space-y-2">
      <!-- Title with close button -->
      <div class="flex justify-between items-start">
        <h3 class="font-semibold text-lg text-gray-800">{{ task.title }}</h3>
        <button
          class="text-gray-400 hover:text-gray-600 transition-colors"
          @click="closePopup"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-5 w-5"
            viewBox="0 0 20 20"
            fill="currentColor"
          >
            <path
              fill-rule="evenodd"
              d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
              clip-rule="evenodd"
            />
          </svg>
        </button>
      </div>

      <div class="h-1 bg-accent drop-shadow-lg w-auto rounded-lg"></div>

      <!-- Description -->
      <p class="text-gray-600 text-sm">{{ task.description }}</p>

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

      <!-- Status -->
      <div class="flex items-center">
        <span
          :class="[
            'px-2 py-1 text-xs rounded-full',
            task.isComplete
              ? 'bg-green-100 text-green-800'
              : 'bg-yellow-100 text-yellow-800',
          ]"
        >
          {{ task.isComplete ? "Complete" : "In Progress" }}
        </span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    task: Object,
    visible: Boolean,
    position: Object,
  },
  computed: {
    popupStyle() {
      return {
        top: `${this.position.top}px`,
        left: `${this.position.left}px`,
        transform: "translate(-50%, -100%) translateY(-10px)", // Center horizontally and position above
      };
    },
  },
  methods: {
    closePopup() {
      this.$emit("close");
    },
    formatDate(date) {
      return new Date(date).toLocaleDateString("en-US", {
        month: "short",
        day: "numeric",
        year: "numeric",
      });
    },
  },
};
</script>

<style scoped>
.popup {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
</style>