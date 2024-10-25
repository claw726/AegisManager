<template>
  <div @click="goToProj()" class="flex flex-col justify-between p-4 card">
    <!-- Image -->
    <div class="aspect-video">
      <img
        :src="project.encodedImage"
        alt="Project Image"
        class="object-contain rounded-lg drop-shadow-lg"
      />
    </div>

    <div class="h-1 bg-accent drop-shadow-lg my-4 rounded" />

    <!-- Title -->
    <h2 class="text-lg font-bold mt-4 text-primary">
      {{ project.projectName }}
    </h2>

    <!-- Short Description -->
    <p class="text-sm text-gray-600">{{ project.projectDescription }}</p>

    <!-- Owner Name -->
    <p class="text-sm text-gray-600">Owner: {{ project.projectOwnerID }}</p>

    <!-- Task Counter -->
    <p class="text-sm text-gray-600">Tasks: > 9000</p>
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
