<template>
  <div
    class="flex flex-col items-center min-h-screen h-full w-full space-y-4 p-4"
  >
    <div
      class="task-card cursor-pointerbg-white border border-gray-300 rounded-lg p-4 shadow-md cursor-pointer"
    >
      <div class="flex">
        <input
          v-on:click="decideFun($event)"
          type="checkbox"
          class="checkbox"
          v-model="isChecked"
        />
        <h3 class="font-bold text-3xl text-hunter-green mb-6">
          {{ task.title }}
        </h3>
        <button class="edit-btn rounded top-right-button">Edit Task</button>
        <button
          @click="showPopup = true"
          class="delete-btn rounded top-right-button"
        >
          Delete Task
        </button>
      </div>

      <div v-if="showPopup" class="popup">
        <div class="popup-content">
          <p>Are you sure you want to delete this task?</p>
          <button @click="handleYes" class="remove-btn">Yes</button>
          <button @click="handleNo" class="remove-btn">No</button>
        </div>
      </div>

      <div class="h-1 bg-accent drop-shadow-lg my-4 rounded" />
      <div style="margin-inline-start: 20px">
        <div class="flex items-center mb-4 infobar text-hunter-green text-xl">
          <p>
            Assigner:
            <select v-model="selectedValue">
              <option value="">{{ task.task_assigner }}</option>
              <option
                v-for="option in options"
                :key="option.value"
                :value="option.value"
              >
                {{ option.text }}
              </option>
            </select>
          </p>
          <!-- <p>Selected: {{ selectedValue }}</p> -->

          <p>
            Completed:
            <select
              v-model="selectedCompletionValue"
              @change="handleOptionChange"
            >
              <option value="">{{ task.completed }}</option>
              <option
                v-for="option in completedOptions"
                :key="option.value"
                :value="option.value"
              >
                {{ option.text }}
              </option>
            </select>
          </p>

          <p>
            Due Date:
            <select v-model="selectedValue">
              <option value="">{{ task.dueDate }}</option>
              <option
                v-for="option in options"
                :key="option.value"
                :value="option.value"
              >
                {{ task.dueDate }}
              </option>
            </select>
          </p>

          <p>
            Priority:
            <select v-model="selectedValue">
              <option value="">{{ task.priority }}</option>
              <option
                v-for="option in priorityOptions"
                :key="option.value"
                :value="option.value"
              >
                {{ option.text }}
              </option>
            </select>
          </p>
        </div>

        <h4 class="text-hunter-green text-xl infobar">
          Description: {{ task.description }}
        </h4>

        <!-- Dropdown for Assignees -->
        <div class="text-lg">
          <label for="assignees-dropdown" class="text-hunter-green text-xl"
            >Assignees:</label
          >

          <!-- List of Assignees with Remove Button -->
          <ul class="text-hunter-green text-xl">
            <li
              v-for="(assignee, index) in task.assignees"
              :key="index"
              class="flex items-center space-x-2"
            >
              <span>{{ assignee }}</span>
              <button
                @click="removeAssignee(index)"
                class="remove-btn rounded text-red-600"
              >
                Remove
              </button>
            </li>
          </ul>
        </div>

        <!-- Button to add new assignees -->
        <div class="add-assignee mt-4 flex items-center space-x-2">
          <input
            v-model="newAssignee"
            placeholder="Enter new assignee name"
            class="border rounded p-2"
          />
          <button
            @click="addAssignee"
            class="bg-green-500 card:hover bg-light-blue text-white p-2 rounded"
          >
            Add Assignee
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";

export default {
  data() {
    return {
      showPopup: false,
      taskId: "",
      task: "",
      selectedAssignee: null, // For the dropdown
      newAssignee: "", // For adding a new assignee
      selectedAssigner: null,
      newAssigner: "",

      selectedValue: "",
      options: [
        { value: "option1", text: "Option 1" },
        { value: "option2", text: "Option 2" },
        { value: "option3", text: "Option 3" },
      ],

      priorityOptions: [
        { value: "option1", text: "High" },
        { value: "option2", text: "Medium" },
        { value: "option3", text: "Low" },
      ],
      completedOptions: [
        { value: "true", text: "true" },
        { value: "false", text: "false" },
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
    ...mapState('tasks', ["allTasks"]),
  },

  methods: {
    handleOptionChange() {
      if (this.selectedCompletionValue === "true") {
        this.completeTask();
      }
      if (this.selectedCompletionValue === "false") {
        this.unCompleteTask();
      }
    },

    handleYes() {
      console.log("User wants to delete task");
      this.showPopup = false;
    },
    handleNo() {
      console.log("Nevermind");
      this.showPopup = false;
    },

    decideFun(event) {
      if (event.target.checked) {
        this.completeTask(this);
      } else {
        this.unCompleteTask(this);
      }
    },

    completeTask() {
      this.task.completed = true;
      this.selectedAssigner = true;
      this.isChecked = true;

      //this.allTasks.id.completed = true;
      //localStorage.setItem("allTasks", JSON.stringify(all));
    },
    unCompleteTask() {
      this.task.completed = false;
      this.selectedAssigner = false;
      this.isChecked = false;

      //this.allTasks.id.completed = true;
      //localStorage.setItem("allTasks", JSON.stringify(all));
    },

    getTaskfromStorage(taskId) {
      const task = JSON.parse(JSON.stringify(this.allTasks))[
        String(this.taskId)
      ];
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
      this.$store.commit("setNewTaskAssignee", this.taskId, newAssigner);
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

.popup {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.popup-content {
  background-color: white;
  padding: 20px;
  border-radius: 5px;
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
  background-color: rgb(2, 2, 58);
  color: white;
  border: none;
  padding: 5px 10px;
  margin-left: 63px;
}

.edit-btn {
  cursor: pointer;
  background-color: rgb(2, 2, 58);
  color: white;
  border: none;
  padding: 5px 10px;
  position: absolute;
  top: 25px;
  right: 25px;
}

.delete-btn {
  cursor: pointer;
  background-color: rgb(77, 12, 23);
  color: white;
  border: none;
  padding: 5px 10px;
  position: absolute;
  top: 60px;
  right: 25px;
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
