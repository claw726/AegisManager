<template>
    <div @click="goToTask()" class="flex flex-col justify-between p-4 card">
      <div class="flex justify-between">
        <h2 class="text-lg font-bold mt-4 text-primary">{{ task.TaskName }}</h2>
        <span :class="{'text-green-600': task.IsComplete, 'text-red-600': !task.IsComplete}">{{ task.IsComplete ? 'Complete' : 'Incomplete' }}</span>
      </div>
      
      <p class="text-sm text-gray-600">{{ task.TaskDescription }}</p>
      
      <p class="text-sm text-gray-600">Assigner: {{ task.AssignerID }}</p>
      
      <p class="text-sm text-gray-600">Assigned Users: {{ task.AssignedUsers.join(', ') }}</p>
      
      <p class="text-sm text-gray-600">Priority: {{ task.TaskPriority }}</p>
      
      <p class="text-sm text-gray-600">Due Date: {{ task.DueDate }}</p>
      
      <p class="text-sm text-gray-600">Files: {{ task.TaskFiles.join(', ') }}</p>
    </div>
  </template>
  
  <script>
  export default {
    props: {
      task: {
        type: Object,
        required: true
      },
      taskIndex: {
        type: Number,
        required: true
      }
    },
    methods: {
      goToTask() {
        if (this.taskIndex === undefined || this.taskIndex === null) {
          console.error('Task index is not defined:', this.taskIndex);
          return;
        } else {
            alert("Bilsha, can you please implement this? I'm not sure what to do here yet. I love copilot and how it helps me code, but I'm not sure what to do here.");
        }
      }
    }
  }
  </script>