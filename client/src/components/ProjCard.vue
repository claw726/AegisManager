<template>
    <div class="flex flex-col justify-between p-4 card">
      <!-- Image -->
       <div class="aspect-video">
        <img :src="project.image" alt="Project Image" class=" object-contain rounded-lg drop-shadow-lg">
       </div>
      

      <div class="h-1 bg-accent drop-shadow-lg my-4 rounded" />
  
      <!-- Title -->
      <h2 class="text-lg font-bold mt-4 text-primary">{{ project.title }}</h2>
  
      <!-- Short Description -->
      <p class="text-sm text-gray-600">{{ project.description }}</p>
  
      <!-- Owner Name -->
      <p class="text-sm text-gray-600">Owner: {{ project.owner }}</p>
  
      <!-- Task Counter -->
      <p class="text-sm text-gray-600">Tasks: {{ project.tasks }}</p>
    </div>
  </template>
  
  <script>
  export default {
    props: {
      project: {
        type: Object,
        required: true
      }
    }
  }
  </script>
  
  <style scoped>
  /* Add any custom styles here */
  </style>