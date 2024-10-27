<template>
  <div @click="goToProj()" class="flex flex-col justify-between p-6 bg-white rounded-lg shadow-lg hover:shadow-xl transition-shadow duration-300 cursor-pointer">
    <!-- Image -->
    <div class="aspect-video rounded-lg overflow-hidden">
      <img
        :src="project.encodedImage"
        alt="Project Image"
        class="c"
      />
    </div>

    <div class="h-1 bg-accent my-4 rounded" />

    <!-- Title -->
    <h2 class="text-xl font-bold text-primary hover:text-secondary transition-colors duration-200">
      {{ project.projectName }}
    </h2>

    <!-- Short Description -->
    <p class="text-sm text-gray-700">{{ project.projectDescription }}</p>

    <div class="flex items-center space-x-2 text-sm text-gray-700">
      <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" viewBox="0 0 20 20" fill="text-accent">
                <path d="M9 6a3 3 0 11-6 0 3 3 0 016 0zM17 6a3 3 0 11-6 0 3 3 0 016 0zM12.93 17c.046-.327.07-.66.07-1a6.97 6.97 0 00-1.5-4.33A5 5 0 0119 16v1h-6.07zM6 11a5 5 0 015 5v1H1v-1a5 5 0 015-5z" />
      </svg>
      Owner: {{ project.projectOwnerID }}
    </div>

     <!-- Task Counter -->
    <p class="text-sm text-gray-600 mt-1 flex items-center">
      <svg class="w-6 h-6 text-accent" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" viewBox="0 0 24 24">
        <path fill-rule="evenodd" d="M5 3a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h11.5c.07 0 .14-.007.207-.021.095.014.193.021.293.021h2a2 2 0 0 0 2-2V7a1 1 0 0 0-1-1h-1a1 1 0 1 0 0 2v11h-2V5a2 2 0 0 0-2-2H5Zm7 4a1 1 0 0 1 1-1h.5a1 1 0 1 1 0 2H13a1 1 0 0 1-1-1Zm0 3a1 1 0 0 1 1-1h.5a1 1 0 1 1 0 2H13a1 1 0 0 1-1-1Zm-6 4a1 1 0 0 1 1-1h6a1 1 0 1 1 0 2H7a1 1 0 0 1-1-1Zm0 3a1 1 0 0 1 1-1h6a1 1 0 1 1 0 2H7a1 1 0 0 1-1-1ZM7 6a1 1 0 0 0-1 1v3a1 1 0 0 0 1 1h3a1 1 0 0 0 1-1V7a1 1 0 0 0-1-1H7Zm1 3V8h1v1H8Z" clip-rule="evenodd"/>
      </svg>

      Tasks: <span class="font-semibold">> 9000</span>
    </p>
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
        alert("Failed to navigate to project dashboard.");
      }
    },
  },
};
</script>

<style scoped>
/* Add any custom styles here */
</style>
