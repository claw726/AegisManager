<template>
  <NavBar />

  <div class="min-h-screen bg-gray-50 p-8">


    <div class="max-w-4xl mx-auto bg-white rounded-lg shadow-lg p-6">
      <div v-if="fetchedTask" class="space-y-6">
        <!-- Task Header -->
        <div class="flex justify-between items-start">
          <h1 class="text-2xl font-bold text-primary">{{ fetchedTask.taskName }}</h1>
          <div class="flex space-x-4">
            <!-- Status Badge -->
            <span class="px-3 py-1 rounded-full text-sm font-semibold" :class="{
              'bg-green-100 text-green-800': fetchedTask.complete,
              'bg-red-100 text-red-800': !fetchedTask.complete
            }">
              {{ fetchedTask.complete ? 'Complete' : 'Incomplete' }}
            </span>
          </div>
        </div>

        <!-- Task Details -->
        <div v-if="fetchedTask.complete" class="greyed-out space-y-4">
          <h2 class="text-lg font-semibold mb-2">Description</h2>
          <p>{{ fetchedTask.taskDescription }}</p>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <h2 class="text-lg font-semibold mb-2">Priority</h2>
              <span class="px-2 py-1 rounded-md text-sm font-medium" :class="{
                'bg-red-100 text-red-800': fetchedTask.taskPriority === 'High',
                'bg-yellow-100 text-yellow-800': fetchedTask.taskPriority === 'Medium',
                'bg-blue-100 text-blue-800': fetchedTask.taskPriority === 'Low'
              }">
                {{ fetchedTask.taskPriority }}
              </span>
            </div>

            <div>
              <h2 class="text-lg font-semibold mb-2">Due Date</h2>
              <p>{{ formatDate(fetchedTask.dueDate) }}</p>
            </div>
          </div>
        </div>

        <div v-else="fetchedTask.complete" class="space-y-4">
          <h2 class="text-lg font-semibold mb-2">Description</h2>
          <p>{{ fetchedTask.taskDescription }}</p>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <h2 class="text-lg font-semibold mb-2">Priority</h2>
              <span class="px-2 py-1 rounded-md text-sm font-medium" :class="{
                'bg-red-100 text-red-800': fetchedTask.taskPriority === 'High',
                'bg-yellow-100 text-yellow-800': fetchedTask.taskPriority === 'Medium',
                'bg-blue-100 text-blue-800': fetchedTask.taskPriority === 'Low'
              }">
                {{ fetchedTask.taskPriority }}
              </span>
            </div>

            <div>
              <h2 class="text-lg font-semibold mb-2">Due Date</h2>
              <p>{{ formatDate(fetchedTask.dueDate) }}</p>
            </div>
          </div>
        </div>

        <div class="flex justify-between mt-6">
          <!--Notification component-->
          <NotificationComponent class="flex" :show="notification.show" :type="notification.type"
            @close="closeNotification">
            {{ notification.message }}
          </NotificationComponent>
        </div>
        <div :class="fetchedTask.complete ? 'greyed-out' : ''" class="space-y-4">
          <div class="flex justify-between mt-6">
            <label v-if="!fetchedTask.complete && showLeftButton && taskUsers.length > 0" for="assignerSelect"
              class="font-semibold text-gray-800">Select Assigner</label>
          </div>
        </div>

        <!-- Task Actions Container -->
        <div class="flex justify-between mt-6">
          <!-- Left-aligned dropdown and button -->
          <div class="flex items-center space-x-4">
            <!-- Text above the dropdown -->

            <!-- Dropdown menu -->
            <select v-if="!fetchedTask.complete && showLeftButton && taskUsers.length > 0" id="assignerSelect"
              v-model="selectedAssigner" class="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300">
              <option v-for="user in taskUsers" :key="user.email" :value="user.email">
                {{ user.label }}
              </option>
            </select>

            <!-- Button, visible only if showLeftButton is true -->
            <button v-if="!fetchedTask.complete && showLeftButton && taskUsers.length > 0" @click="updateTaskAssigner"
              class="px-4 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600">
              Confirm Reassignment
            </button>
          </div>

          <!-- Right-aligned buttons -->
          <div class="flex space-x-4"> <!-- Align buttons to the right -->
            <button @click="goBack" class="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300">
              Back
            </button>
            <button v-if="!fetchedTask.complete" @click="markAsComplete"
              class="px-4 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600">
              Mark Complete
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";
import NavBar from "@/components/NavBar.vue";
import NotificationComponent from "@/components/NotificationComponent.vue";

export default {
  name: 'TaskDetail',

  data() {
    return {
      isGrayedOut: null,
      fetchedTask: null,
      taskUsers: [],
      selectedAssigner: '',
      selectedUserID: -1,
      notification: {
        show: false,
        type: "info",
        message: "",
      },
      notification: {
        show: false,
        type: "success",
        message: "",
      },
    };
  },

  components: {
    NavBar,
    NotificationComponent,
  },

  props: {
    taskId: {
      type: [String, Number],
      required: true
    }
  },

  computed: {
    // Map allTasks from the tasks module
    ...mapState("tasks", ["allTasks"]),

    // Map isLoggedIn and currentUser from the auth module
    ...mapState("auth", ["isLoggedIn", "currentUser"]),

    // Map additional properties if needed
    ...mapState("tasks", ["currentTask", "updateTask"]),
    showLeftButton() {
      return this.IsAssigner();
    }
  },
  async mounted() {
    this.fetchedTask = await this.$store.dispatch("tasks/fetchTask", this.$route.params.taskId);
    await this.populateAssignerDropdown(this.fetchedTask.assignerID);
  },

  methods: {
    populateAssignerDropdown(currentUserID) {
      this.taskUsers = this.fetchedTask.assignedUsers
        .filter(user => user.userID !== currentUserID)
        .map(user => ({
          label: `${user.email}`,
          email: user.email,
          ID: user.userID
        }));
      console.log(this.taskUsers);
      console.log(this.fetchedTask.complete)
    },

    goBack() {
      this.$router.go(-1);
    },

    async getTask() {
      console.log("Doing async method")
      this.$store.dispatch("tasks/fetchTask", this.taskId);
    },

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
    formatDate(date) {
      if (!date) return 'No due date';
      return new Date(date).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      });
    },

    async markAsComplete() {
      try {

        const dueDate = `${this.fetchedTask.dueDate}T15:30:00.000z`;

        const taskData = {
          taskName: this.fetchedTask.taskName,
          taskDescription: this.fetchedTask.taskDescription,
          assignerID: this.fetchedTask.assignerID,
          taskPriority: this.fetchedTask.taskPriority,
          dueDate: dueDate,
          isComplete: true
        };

        await this.$store.dispatch("tasks/updateTask", {
          taskId: this.taskId,
          taskData: taskData,
        });

        // Reroute to task to do list as task is now marked complete
        //await this.loadTask();
        this.isGrayedOut = true;
        this.$router.push({ name: "TDList" });
      } catch (error) {
        console.error('Failed to mark task as complete:', error);
        // Error will be handled by Vuex store and displayed via updateStatus
      }
    },

    async updateTaskAssigner() {
      console.log(this.selectedAssigner);
      //this.$store.commit("setNewTaskAssignee", this.taskId, newAssigner);
      if (this.selectedAssigner === "" || this.selectedAssigner === "Select New Assigner") {
        this.showNotification("error", "Select a task assigner.");
        return;
      }
      else {

        try {

          const dueDate = `${this.fetchedTask.dueDate}`.slice(0, 10);

          const taskData = {
            taskName: this.fetchedTask.taskName,
            taskDescription: this.fetchedTask.taskDescription,
            assignerID: this.selectedUserID,
            taskPriority: this.fetchedTask.taskPriority,
            dueDate: dueDate,
            isComplete: this.fetchedTask.complete
          };
          console.log(taskData.isComplete);
          await this.$store.dispatch("tasks/updateTask", {
            taskId: this.taskId,
            taskData: taskData,
          });
        } catch (error) {
          this.showNotification("error", "Unexpected error with task delegation.");
        }


        this.showNotification("success", "Successfully sent assigner invite!");

        return;
      }
    },
    IsAssigner() {
      if (this.fetchedTask.assignerID === this.currentUser.userID) {
        return true;
      }
      return false;
    },
    showNotification(type, message) {
      this.notification = {
        show: true,
        type,
        message,
      };

      if (type == "success") {
        setTimeout(this.closeNotification, 5000);
      }
    },
    closeNotification() {
      this.notification.show = false;
    },
  },
  watch: {
    selectedAssigner(newEmail) {
      const selectedUser = this.taskUsers.find(user => user.email === newEmail);
      this.selectedUserID = selectedUser ? selectedUser.ID : null;
      console.log(this.selectedUserID);
    },
  },
};
</script>

<style scoped>
.greyed-out {
  opacity: 0.3;
  /* Adjust the opacity as needed */
}
</style>
