<template>
  <div
    @click="goToProj()"
    class="group flex flex-col h-full bg-white rounded-xl shadow-md hover:shadow-xl transition-all duration-300 cursor-pointer overflow-hidden"
  >
    <!-- Image with overlay -->
    <div class="relative aspect-video overflow-hidden">
      <img
        :src="project.encodedImage"
        :alt="project.projectName"
        class="object-cover w-full h-full transition-transform duration-500 group-hover:scale-105"
      />
      <div
        class="absolute inset-0 bg-gradient-to-t from-black/30 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300"
      />
      <div
        v-if="project.isArchived"
        class="absolute inset-0 bg-gray-900 bg-opacity-50 flex items-center justify-center"
      >
        <span class="text-white font-medium">Archived</span>
      </div>
    </div>

    <div class="flex flex-col flex-grow p-5 space-y-4">
      <!-- Title -->
      <h2
        class="text-xl font-bold transition-colors duration-200"
        :class="{
          'text-gray-800 group-hover:text-blue-600': !project.isArchived,
          'text-gray-400 line-through': project.isArchived,
        }"
      >
        {{ project.projectName }}
      </h2>

      <!-- Description with truncation -->
      <p
        class="text-sm transition-colors duration-200"
        :class="{
          'text-gray-600': !project.isArchived,
          'text-gray-400': project.isArchived,
        }"
      >
        {{ project.projectDescription }}
      </p>

      <div class="flex-grow" />

      <div class="h-px bg-gray-200 w-full" />

      <!-- Footer -->
      <div class="flex items-center justify-between pt-2">
        <!-- Owner info -->
        <div class="flex items-center space-x-2">
          <i
            class="fas fa-user-circle"
            :class="{
              'text-gray-400': !project.isArchived,
              'text-gray-500': project.isArchived,
            }"
          />
          <span
            class="text-sm transition-colors duration-200"
            :class="{
              'text-gray-600': !project.isArchived,
              'text-gray-500': project.isArchived,
            }"
          >
            {{ project.projectOwnerID }}
          </span>
        </div>

        <!-- Task counter -->
        <div class="flex items-center space-x-2">
          <i
            class="fas fa-tasks"
            :class="{
              'text-gray-400': !project.isArchived,
              'text-gray-500': project.isArchived,
            }"
          />
          <span
            class="text-sm font-medium transition-colors duration-200"
            :class="{
              'text-gray-600': !project.isArchived,
              'text-gray-500': project.isArchived,
            }"
          >
            {{ formatTaskCount(9000) }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    project: {
      type: Object,
      required: true,
    },
    projIndex: {
      type: Number,
      required: true,
    },
  },
  methods: {
    formatTaskCount(count) {
      return count > 999 ? `${(count / 1000).toFixed(1)}k` : count;
    },
    async goToProj() {
      if (this.projIndex === undefined || this.projIndex === null) {
        console.error("Project index is not defined:", this.projIndex);
        return;
      }
      try {
        await this.$router.push({
          name: "ProjectDashboard",
          params: {
            orgIndex: this.project.parentOrgID,
            projIndex: this.project.projectID,
          },
        });
      } catch (error) {
        console.error("Failed to navigate to project dashboard:", error);
      }
    },
  },
};
</script>
