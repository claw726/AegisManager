<template>
  <div class="flex flex-col items-center space-y-4 p-4">
    <div class="task-card cursor-pointerbg-white border border-gray-300 rounded-lg p-4 shadow-md cursor-pointer">
      <div class="flex items-center mb-4">
        <input
          @click="completeTask"
          type="checkbox"
          class="checkbox"
          v-model="this.task.completed"
        />
        <h3 class="font-bold text-lg">{{ task.title }}</h3>
      </div>

      <p class="text-gray-600">Completed: {{ task.completed }}</p> 
      <p class="text-gray-600">Due: {{ task.dueDate }}</p>
      <p class="text-gray-600">Description: {{ task.description }}</p>
      <p class="text-gray-600">Priority: {{ task.priority }}</p>
      
      

      <label for="assigner-dropdown"></label>
      <p class="text-gray-600">Task Assigner: {{ task.task_assigner }}</p>
        <select id="assigner-dropdown" v-model="selectedAssigner" @change="assignerClick">
          <option v-for="(assigneer, index) in task.assignees" :key="index" :value="assignee">
            {{ assigneer }}
          </option>
        </select>

      
      <!-- Dropdown for Assignees -->
      <div class="assignees-section">
        <label for="assignees-dropdown">Assignees:</label>
        <select id="assignees-dropdown" v-model="selectedAssignee">
          <option v-for="(assignee, index) in task.assignees" :key="index" :value="assignee">
            {{ assignee }}
          </option>
        </select>

        <!-- List of Assignees with Remove Button -->
        <ul class="mt-2">
          <li v-for="(assignee, index) in task.assignees" :key="index" class="flex items-center space-x-2">
            <span>{{ assignee }}</span>
            <button @click="removeAssignee(index)" class="remove-btn text-red-600">Remove</button>
          </li>
        </ul>
      </div>

      <!-- Button to add new assignees -->
      <div class="add-assignee mt-4 flex items-center space-x-2">
        <input v-model="newAssignee" placeholder="Enter new assignee name" class="border rounded p-2" />
        <button @click="addAssignee" class="bg-green-500 card:hover bg-light-blue text-white p-2 rounded">Add Assignee</button>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex';

export default {
  data() {
    return {
      taskId: "",
      task: "",
      selectedAssignee: null, // For the dropdown
      newAssignee: "",        // For adding a new assignee
      selectedAssigner: null, 
      newAssigner: "",
    };
  },

  created() {
    this.taskId = this.$route.query.taskId;
  },

  mounted() {
    this.taskId = this.$route.query.taskId;
    this.task = this.getTaskfromStorage(this.taskId);
  },

  computed: {
    ...mapState(['allTasks']),
  },

  methods: {
    completeTask() {
      this.task.completed = true;
      const all = this.allTasks;
      const id = this.taskId;
      //this.allTasks.id.completed = true; 
      //localStorage.setItem("allTasks", JSON.stringify(all));
    },

    getTaskfromStorage(taskId) {
      const task = JSON.parse(JSON.stringify(this.allTasks))[String(this.taskId)];
      return task;
    },

    removeAssignee(index) {
      // Remove the selected assignee from the task
      this.task.assignees.splice(index, 1);
    },

    addAssignee() {
      if (this.newAssignee.trim()) {
        // Add new assignee if input is not empty
        this.task.assignees.push(this.newAssignee.trim());
        this.newAssignee = ""; // Clear input after adding
      }
    },

    assignerClick() {
      this.updateTaskAssigner(this.selectedAssigner);
    }, 

    updateTaskAssigner(newAssigner) {
      this.$store.commit('setNewTaskAssignee', this.taskId, newAssigner);
    },
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

.remove-btn {
  cursor: pointer;
  background-color: darkblue;
  color: white;
  border: none;
  padding: 5px 10px;
}

.add-assignee input {
  padding: 5px;
  margin-right: 10px;
}

.add-assignee button {
  padding: 5px 10px;
  color: white;
  border: none;
  cursor: pointer;
}
</style>
