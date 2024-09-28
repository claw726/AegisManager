<template>
    
    <div class="flex flex-col items-center space-y-4 p-4">
      <div class="task-card bg-white border border-gray-300 rounded-lg p-4 shadow-md cursor-pointer">

        <h3 class="font-bold text-lg">{{ this.task.title }}</h3>
        <p class="text-gray-600">Due: {{ this.task.dueDate }}</p>
        <p class="text-gray-600">Description: {{ this.task.description }}</p>
        <p class="text-gray-600">Priority: {{ this.task.priority }}</p>
        <p class="text-gray-600">Completed: {{ this.task.completed }}</p>
      </div>
    </div>
  </template>
  

  <script>


  export default {
    

    created() {
      const taskId = this.$route.query.taskId;
      return this.fetchTask(taskId);
    },


    methods: {
      viewTask(task) {
        // Navigate to TaskDetail, passing the task as a route parameter
        this.$router.push({ name: 'TaskDetail', params: { task } });
      },

      fetchTask(taskId) {
        // Simulate fetching task from SQL
        const allTasks = [
          { 
            id: 1, title: 'Task 1', description: 'Complete project', dueDate: '2024-09-30', 
            priority: 'High', completed: false, assignees: ['User A', 'User B']
          },
          { 
            id: 2, title: 'Task 2', description: 'Prepare presentation', dueDate: '2024-10-05', 
          priority: 'High', completed: false, assignees: ['User C'] 
          },
          { 
            id: 3, title: 'Task 3', description: 'DO whatever I want to', dueDate: '2024-11-15', 
          priority: 'High', completed: false, assignees: ['User D', 'User B'] 
          }
        ];


        // Find the task by taskId
        this.task = allTasks.find(task => task.id === parseInt(taskId));
        return this.task;
      }
    },

  };
  </script>
  
  <style scoped>
  .task-card {
    transition: transform 0.2s;
  }
  .task-card:hover {
    transform: scale(1.02);
  }
  </style>
  