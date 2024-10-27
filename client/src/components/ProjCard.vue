<template>
  <div
    @click="goToProj()"
    class="group flex flex-col h-full bg-white rounded-xl shadow-md hover:shadow-xl transition-all duration-300 cursor-pointer overflow-hidden"
  >
    <!-- Image -->
    <div class="aspect-video rounded-lg overflow-hidden">
      <img
        :src="project.encodedImage"
        :alt="project.projectName"
        class="object-cover w-full h-full transition-transform duration-500 group-hover:scale-105"
      />
      <div
        class="absolute inset-0 bg-gradient-to-t from-black/30 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300"
      />
    </div>
    <div class="h-1 bg-accent my-4 rounded" />

    <!-- Title -->
    <h2
      class="text-xl font-bold text-primary hover:text-secondary transition-colors duration-200"
    >
      {{ project.projectName }}
    </h2>

    <!-- Short Description -->
    <p class="text-sm text-gray-700">{{ project.projectDescription }}</p>

    <div class="flex items-center space-x-2 text-sm text-gray-700">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        class="h-4 w-4"
        viewBox="0 0 20 20"
        fill="text-accent"
      >
        <path
          d="M9 6a3 3 0 11-6 0 3 3 0 016 0zM17 6a3 3 0 11-6 0 3 3 0 016 0zM12.93 17c.046-.327.07-.66.07-1a6.97 6.97 0 00-1.5-4.33A5 5 0 0119 16v1h-6.07zM6 11a5 5 0 015 5v1H1v-1a5 5 0 015-5z"
        />
      </svg>
      Owner: {{ project.projectOwnerID }}
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
            orgIndex: this.$route.params.orgIndex,
            projIndex: this.projIndex,
          },
        });
      } catch (error) {
        console.error("Failed to navigate to project dashboard:", error);
      }
    },
  },
};
</script>
