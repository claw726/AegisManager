<template>
    <div @click="goToTask()" class="flex flex-col justify-between p-4 card">
      <div class="flex justify-between">
        <h2 class="text-lg font-bold mt-4 text-primary">{{ task.TaskName }}</h2>
        <span :class="{'text-green-600': task.IsComplete, 'text-red-600': !task.IsComplete}">{{ task.IsComplete ? 'Complete' : 'Incomplete' }}</span>
      </div>
      
      <p class="text-sm text-gray-600">{{ task.taskDescription }}</p>
      
      <p class="text-sm text-gray-600">Assigner: {{ task.AssignerID }}</p>
      
      <!-- <p class="text-sm text-gray-600">Assigned Users: {{ task.AssignedUsers.join(', ') }}</p> -->
      
      <p class="text-sm text-gray-600">Priority: {{ task.taskPriority }}</p>
      
      <p class="text-sm text-gray-600">Due Date: {{ task.dueDate }}</p>
      
      <!-- <p class="text-sm text-gray-600">Files: {{ task.TaskFiles.join(', ') }}</p> -->
    </div>
  </template>
  
  <script>
  export default {
    props: {
      task: {
        type: Object,
        required: true
      },
    },
    methods: {
      goToTask() {
        
      }
    }
  }
  </script>