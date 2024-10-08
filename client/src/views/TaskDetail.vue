<template>
  <div class="flex flex-col items-center space-y-4 p-4">
    <div class="task-card cursor-pointerbg-white border border-gray-300 rounded-lg p-4 shadow-md cursor-pointer">
      
      
      <div class="flex items-center">
        <input
          @click="completeTask"
          type="checkbox"
          class="checkbox"
          v-model="this.task.completed"
        />
        <h3 class="font-bold  text-3xl text-hunter-green mb-6">{{  task.title }}</h3>
      </div>

      <div style="margin-inline-start: 20px;">

      <div class="flex items-center mb-4 infobar">

        <p class="text-lg">Assigner:  
        <select v-model="selectedValue">
          <option value="">  {{ task.task_assigner }}</option>
          <option v-for="option in options" :key="option.value" :value="option.value">
            {{ option.text }}
          </option>
        </select>
        </p>
        <!-- <p>Selected: {{ selectedValue }}</p> -->



        <p class="text-lg">Completed:  
        <select v-model="selectedValue">
          <option value="">  {{ task.task_assigner }}</option>
          <option v-for="option in options" :key="option.value" :value="option.value">
            {{ task.completed }}
          </option>
        </select>
        </p>

        <p class="text-lg">Due Date:  
        <select v-model="selectedValue">
          <option value="">  {{ task.task_assigner }}</option>
          <option v-for="option in options" :key="option.value" :value="option.value">
            {{ task.dueDate }}
          </option>
        </select>
        </p>

        <p class="text-lg">Priority:  
        <select v-model="selectedValue">
          <option value="">  {{ task.task_assigner }}</option>
          <option v-for="option in options" :key="option.value" :value="option.value">
            {{ task.priority }}
          </option>
        </select>
        </p>

        <h2 class=" text-lg">Description: {{ task.description }}</h2>
      </div>
          
      
      
    
      <!-- Dropdown for Assignees -->
      <div class="text-lg ">
        <label for="assignees-dropdown" class="text-hunter-green text-xl">Assignees:</label>
        
        <!-- List of Assignees with Remove Button -->
        <ul class="mt-2">
          <li v-for="(assignee, index) in task.assignees" :key="index" class="flex items-center space-x-2">
            <span>{{ assignee }}</span>
            <button @click="removeAssignee(index)" class="remove-btn rounded text-red-600">Remove</button>
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


      selectedValue: "",
      options: [
        { value: 'option1', text: 'Option 1' },
        { value: 'option2', text: 'Option 2' },
        { value: 'option3', text: 'Option 3' },
      ],
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

select {
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  appearance: none; /* Customize the appearance */
  /*background: url("path/to/dropdown-arrow.svg") no-repeat right 10px center; /* Add a custom dropdown arrow */
}

.infobar {
  margin-right: 600px;
  gap: 20px;
  font-size: "text-lg";
  margin-bottom: 20px;
  
}
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
