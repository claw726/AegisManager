<template>
    <div class="bg-white flex flex-col items-center justify-center">
      <!-- NavBar Component -->
      <NavBar />
      
      <!-- Task Board -->
      <div class="flex flex-col items-center space-y-8 mt-12">
        <!-- Title: Centered and Colored Dark Hunter Green -->
        <h1 class="text-4xl font-bold text-hunter-green mb-6">To Do List</h1>
        
        
        <div v-for="task in tasks" :key="task.id" class="task-card bg-white border border-gray-300 rounded-lg p-4 shadow-md cursor-pointer" @click="viewTask(task)">
            <h3 class="font-bold text-lg">{{ task.title }}</h3>
            <p class="text-gray-600">Due: {{ task.dueDate }}</p>
            <p class="text-gray-600">Priority: {{ task.priority }}</p>
        </div>

        <!-- Tasks Container -->
        <div class="flex flex-col w-96 space-y-6 p-4 items-center">
  
          <!-- Task 1 -->
          <div
            class="task-card cursor-pointer"
            :class="{ 'bg-gray-300': task1.completed }"
            @click="viewTask(task1)"
          >
            <div class="flex items-center mb-4">
              <input
                type="checkbox"
                class="checkbox"
                v-model="task1.completed"
              />
              <div class="task-title ml-2">Task 1</div>
            </div>
            <div class="task-details">Description: {{ String( getTaskfromStorage(1).description ) }} </div>
            <div class="task-details">Due Date: 2024-09-30</div>
          </div>
  
          <!-- Task 2 -->
          <div
            class="task-card cursor-pointer"
            :class="{ 'bg-gray-300': task2.completed }"
            @click="viewTask(task2)"
          >
            <div class="flex items-center mb-4">
              <input
                type="checkbox"
                class="checkbox"
                v-model="task2.completed"
              />
              <div class="task-title ml-2">Task 2</div>
            </div>
            <div class="task-details">Description: Prepare for client presentation</div>
            <div class="task-details">Due Date: 2024-10-05</div>
          </div>
  
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import NavBar from '@/components/NavBar.vue';
  import {getTask} from './TaskDetail.vue';
  import { mapState } from 'vuex';
  
  export default {
    components: {
      NavBar,
    },

    computed: {
      ...mapState(['allTasks'])
    },

    created() {
      this.loadUserData();
    },
    
    
    data() {
      return {
        task1: {
          id: 1, 
          completed: false,
          title: "yes Please",
          description: ' dooodooodooodooo',
          dueDate: '2024-10-05',
          priority: 'Low',
          assignees: ['User X']
        },
        task2: {
          id: 2,
          completed: false,
          title: 'Task 2',
          description: 'Prepare for client presentation',
          dueDate: '2024-10-05',
          priority: 'Medium',
          assignees: ['User C']
        },
        task3: {
          id: 3,
          completed: false,
          title: 'Task 3',
          description: 'Dance the night away',
          dueDate: '2024-10-05',
          priority: 'Medium',
          assignees: ['User C']
        },
      };
    },

    methods: {
      loadUserData() {
        const currentUser = localStorage.getItem('CurrentUser');
        if (currentUser) {
          this.user = JSON.parse(currentUser);
        } else {
          this.$router.push({ name: 'Login' });
        }
      },

      viewTask(task) {
        // Navigate to TaskDetails component, passing the task as a route parameter
        this.$router.push({ name: 'TaskDetail', query: {taskId: task.id } });
      },

      getTaskfromStorage(taskid) {
        //console.log(String(allTasks.taskid))
        t = allTasks.find(task => task.id === parseInt(taskid));
        console.log(t)
        return t;
      },
      
    },
  };
  </script>
  
  <style scoped>
  .task-card {
    transition: transform 0.2s;
  }

  .task-card:hover {
    transform: scale(1.15);
  }
  </style>
  
