<template>
  <NavBar />

  <div class="min-h-screen bg-gray-50 py-8 px-4 sm:px-6 lg:px-8">
    <div v-if="fetchedTask" class="max-w-4xl mx-auto"> <!-- Breadcrumb Navigation -->
      <div class="mb-6 flex items-center space-x-2 text-sm text-gray-500">
        <button
          class="inline-flex items-center px-3 py-2 text-sm font-medium text-gray-700 bg-white rounded-lg border border-gray-300 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-colors duration-200"
          @click="goBack">
          <i class="fas fa-arrow-left mr-2"></i>
          Back
        </button>
        <span>/</span>
        <span>Task Details</span>
      </div>

      <!-- Main Content Card -->
      <div class="bg-white rounded-xl shadow-lg border border-gray-100 overflow-hidden">
        <!-- Task Header -->
        <div class="p-6 border-b border-gray-200">
          <div class="flex justify-between items-start">
            <div class="space-y-1">
              <h1 class="text-2xl font-bold text-gray-900">
                {{ fetchedTask.taskName }}
              </h1>
              <div class="flex items-center space-x-2 text-sm text-gray-500">
                <i class="fas fa-user-circle"></i>
                <span>Assigned by {{ creator?.userName || 'Unknown' }}</span>
              </div>
            </div>
            <div class="flex items-center space-x-3">
              <!-- Status Badge -->
              <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium" :class="{
                'bg-green-100 text-green-800': fetchedTask.isComplete,
                'bg-yellow-100 text-yellow-800': !fetchedTask.isComplete
              }">
                <i :class="[
                  'mr-1.5',
                  fetchedTask.isComplete ? 'fas fa-check' : 'fas fa-clock'
                ]"></i>
                {{ fetchedTask.isComplete ? 'Completed' : 'In Progress' }}
              </span>

              <!-- Complete Button -->
              <button v-if="!fetchedTask.isComplete && showLeftButton"
                class="inline-flex items-center px-4 py-2 bg-green-600 text-white text-sm font-medium rounded-lg hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 transition-colors duration-200"
                @click="markAsComplete">
                <i class="fas fa-check mr-2"></i>
                Mark Complete
              </button>
            </div>
          </div>
        </div>

        <!-- Task Details Grid -->
        <div class="p-6 grid grid-cols-1 md:grid-cols-2 gap-6">
          <!-- Description -->
          <div class="space-y-2">
            <h2 class="text-sm font-medium text-gray-500 uppercase tracking-wider">
              Description
            </h2>
            <p class="text-gray-900">{{ fetchedTask.taskDescription }}</p>
          </div>

          <!-- Priority -->
          <div class="space-y-2">
            <h2 class="text-sm font-medium text-gray-500 uppercase tracking-wider">
              Priority
            </h2>
            <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium" :class="{
              'bg-red-100 text-red-800': fetchedTask.taskPriority === 'High',
              'bg-yellow-100 text-yellow-800': fetchedTask.taskPriority === 'Medium',
              'bg-blue-100 text-blue-800': fetchedTask.taskPriority === 'Low'
            }">
              <i :class="[
                'mr-1.5',
                fetchedTask.taskPriority === 'High' ? 'fas fa-exclamation-circle' :
                  fetchedTask.taskPriority === 'Medium' ? 'fas fa-arrow-circle-up' :
                    'fas fa-arrow-circle-down'
              ]"></i>
              {{ fetchedTask.taskPriority }}
            </span>
          </div>

          <!-- Due Date -->
          <div class="space-y-2">
            <h2 class="text-sm font-medium text-gray-500 uppercase tracking-wider">
              Due Date
            </h2>
            <div class="flex items-center text-gray-900">
              <i class="fas fa-calendar-alt mr-2 text-gray-400"></i>
              {{ formatDate(fetchedTask.dueDate) }}
            </div>
          </div>

          <!-- Assignees -->
          <div class="space-y-2">
            <h2 class="text-sm font-medium text-gray-500 uppercase tracking-wider">
              Assignees
            </h2>
            <div class="flex flex-wrap gap-2">
              <span v-for="user in fetchedTask.assignedUsers" :key="user.userID"
                class="inline-flex items-center px-3 py-1 rounded-full bg-blue-100 text-blue-800 text-sm">
                <i class="fas fa-user mr-2"></i>
                {{ user.userName }}
              </span>
            </div>
          </div>
        </div>

        <!-- Action Buttons -->
        <div class="px-6 py-4 bg-gray-50 border-t border-gray-200">
          <div class="flex justify-between items-center">
            <!-- Left side buttons -->
            <div class="flex items-center space-x-3">
              <button
                class="inline-flex items-center px-4 py-2 bg-indigo-600 text-white text-sm font-medium rounded-lg hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition-colors duration-200"
                @click="goToFiles">
                <i class="fas fa-file mr-2"></i>
                Files
              </button>
              <button
                class="inline-flex items-center px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-colors duration-200"
                @click="goToTaskChat">
                <i class="fas fa-comments mr-2"></i>
                Task Chat
              </button>
              <button v-if="!fetchedTask.isComplete && showLeftButton"
                class="inline-flex items-center px-4 py-2 bg-indigo-600 text-white text-sm font-medium rounded-lg hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition-colors duration-200"
                @click="goToAddUsers">
                <i class="fas fa-user-plus mr-2"></i>
                Add Users
              </button>
            </div>

            <div class="flex items-center space-x-3">
              <!-- Edit Button -->
              <button 
                v-if="!fetchedTask.isComplete && showLeftButton"
                class="inline-flex items-center px-4 py-2 bg-amber-600 text-white text-sm font-medium rounded-lg hover:bg-amber-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-amber-500 transition-colors duration-200"
                @click="goToEditTask"
              >
                <i class="fas fa-edit mr-2"></i>
                Edit Task
              </button>

              <!-- Delete Button -->
              <button 
                v-if="showLeftButton"
                class="inline-flex items-center px-4 py-2 bg-red-600 text-white text-sm font-medium rounded-lg hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 transition-colors duration-200"
                @click="showPopup = true"
              >
                <i class="fas fa-trash-alt mr-2"></i>
                Delete Task
              </button>
            </div>

            <div v-if="showPopup" class="popup">
              <div class="popup-content">
                <p>Are you sure you want to delete this task?</p>
                <button class="remove-btn" @click="handleYes">Yes</button>
                <button class="remove-btn" @click="handleNo">No</button>
              </div>
            </div>

            <!-- New button for file explorer -->
            <!----
            <label v-if="showLeftButton"
              class="px-4 py-2 upload-btn text-white rounded-lg bg-blue-600 hover:bg-blue-700 cursor-pointer">
              Upload File
              <input type="file" class="hidden" @change="handleFileUpload" />
            </label>
          -->


          </div>
        </div>
        <!-- Reassignment Controls -->
        <div v-if="!fetchedTask.isComplete && showLeftButton && taskUsers.length > 0"
            class="mt-6 p-6 bg-gray-50 border-t border-gray-200">
          <div class="flex items-center space-x-4">
            <div class="flex-1">
              <label for="assignerSelect" class="block text-sm font-medium text-gray-700 mb-2">
                Select New Assigner
              </label>
              <select
                id="assignerSelect"
                v-model="selectedAssigner"
                class="block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
              >
                <option value="">Select New Assigner</option>
                <option
                  v-for="user in taskUsers"
                  :key="user.email"
                  :value="user.email"
                >
                  {{ user.label }}
                </option>
              </select>
            </div>

            <button
              class="inline-flex items-center px-4 py-2 bg-indigo-600 text-white text-sm font-medium rounded-lg hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition-colors duration-200"
              @click="sendAssignerInvite"
            >
              <i class="fas fa-user-plus mr-2"></i>
              Confirm Reassignment
            </button>
          </div>
        </div>
      </div>
      <!-- Delete Confirmation Modal -->
      <div v-if="showPopup" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
        <div class="bg-white rounded-lg p-6 max-w-sm w-full mx-4">
          <h3 class="text-lg font-medium text-gray-900 mb-4">
            <i class="fas fa-exclamation-triangle text-yellow-500 mr-2"></i>
            Confirm Deletion
          </h3>
          <p class="text-gray-500 mb-6">
            Are you sure you want to delete this task? This action cannot be undone.
          </p>
          <div class="flex justify-end space-x-3">
            <button
              class="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-500 transition-colors duration-200"
              @click="handleNo">
              Cancel
            </button>
            <button
              class="px-4 py-2 text-sm font-medium text-white bg-red-600 rounded-lg hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 transition-colors duration-200"
              @click="handleYes">
              Delete
            </button>
          </div>
        </div>
      </div>
    </div>
    <!-- Loading State -->
    <div v-else class="flex justify-center items-center h-64">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
    </div>

    <!-- Notification Component -->
    <NotificationComponent v-model:show="notification.show" :type="notification.type" @close="closeNotification">
      {{ notification.message }}
    </NotificationComponent>
  </div>
</template>

<script>
import { mapState } from "vuex";
import NavBar from "@/components/NavBar.vue";
import NotificationComponent from "@/components/NotificationComponent.vue";

export default {
  name: "TaskDetail",

  components: {
    NavBar,
    NotificationComponent,
  },

  props: {
    taskId: {
      type: [String, Number],
      required: true,
    },
  },

  data() {
    return {
      isGrayedOut: null,
      fetchedTask: null,
      completed: null,
      taskUsers: [],
      selectedAssigner: "",
      selectedUserID: -1,
      notification: {
        show: false,
        type: "info",
        message: "",
      },
      showPopup: false,
    };
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
    },
    availableAssigners() {
      return this.taskUsers.filter(user => user.userID !== this.currentUser.userID);
    },
    canReassign() {
      return this.showLeftButton && !this.fetchedTask.isComplete;
    }
  },

  watch: {
    selectedAssigner(newEmail) {
      const selectedUser = this.taskUsers.find(
        (user) => user.email === newEmail
      );
      this.selectedUserID = selectedUser ? selectedUser.ID : null;
    },
  },

  async mounted() {
    this.fetchedTask = await this.$store.dispatch(
      "tasks/fetchTask",
      this.$route.params.taskId
    );
    console.log("Client has stored fetched task.");
    console.log(this.fetchedTask);
    //console.log(JSON.stringify(this.fetchedTask.assignedUsers, null, 2));
    this.makeListOfAssignees();
    this.makeNameListofAssignees();
    await this.populateAssignerDropdown(this.fetchedTask.assignerID);
  },

  async created() {
    this.fetchedTask = await this.$store.dispatch(
      "tasks/fetchTask",
      this.$route.params.taskId
    );
    this.completed = this.fetchedTask.isComplete;
  },

  methods: {
    populateAssignerDropdown(currentUserID) {
      this.taskUsers = this.fetchedTask.assignedUsers
        .filter((user) => user.userID !== currentUserID)
        .map((user) => ({
          label: `${user.email}`,
          email: user.email,
          ID: user.userID,
        }));
    },

    makeListOfAssignees() {
      const emailList = this.getEmails(this.fetchedTask.assignedUsers);
      console.log(emailList);
      return emailList;
    },

    makeNameListofAssignees() {
      const nameList = this.getNames(this.fetchedTask.assignedUsers);
      //console.log("Printing nameList:");
      //console.log(nameList);
      return nameList;
    },

    getEmails(data) {
      if (!Array.isArray(data)) {
        throw new Error("Input data must be an array.");
      }
      return data.map((user) => user.email).join(", ");
    },

    getNames(data) {
      if (!Array.isArray(data)) {
        throw new Error("Input data must be an array.");
      }
      return data.map((user) => user.userName).join(", ");
    },

    goBack() {
      this.$router.go(-1);
    },

    goToEditTask() {
      console.log("Edit task actions");
      this.$router.push({
        name: "editTask",
        params: {
          taskId: this.fetchedTask.taskID,
        },
      });
    },

    async getTask() {
      console.log("Doing async method");
      return this.$store.dispatch("tasks/fetchTask", this.taskId);
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
      this.makeTaskDeleted();
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
      if (!date) return "No due date";
      return new Date(date).toLocaleDateString("en-US", {
        year: "numeric",
        month: "short",
        day: "numeric",
      });
    },

    async makeTaskDeleted() {
      try {
        await this.$store.dispatch("tasks/deleteTask", {
          taskID: this.taskId,
        });

        this.showNotification("success", "Task successfully deleted!");
        await new Promise((resolve) => setTimeout(resolve, 2500));
      } catch (error) {
        //TODO: make sure notification only shows if error shows right status code
        console.error("Failed to delete task:");
        console.error(error);
      }
      this.$router.push({ name: "TDList" });
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
          isComplete: true,
        };

        await this.$store.dispatch("tasks/updateTask", {
          taskId: this.taskId,
          taskData: taskData,
        });

        this.showNotification("success", "Task successfully completed!");
        await new Promise((resolve) => setTimeout(resolve, 2500));

        // Reroute to task to do list as task is now marked isComplete
        this.isGrayedOut = true;
        this.$router.push({ name: "TDList" });
      } catch (error) {
        console.error("Failed to mark task as isComplete:", error);
        // Error will be handled by Vuex store and displayed via updateStatus
      }
    },

    async sendAssignerInvite() {
      if (
        this.selectedAssigner === "" ||
        this.selectedAssigner === "Select New Assigner"
      ) {
        this.showNotification("error", "Select a task assigner.");
        return;
      }
      try {
        const data = {
          senderEmail: this.currentUser.email,
          recipientEmail: this.selectedAssigner,
          invitationType: 1,
          message:
            this.fetchedTask.taskID +
            ": Task Assigner Request - " +
            this.fetchedTask.taskName,
        };
        await this.$store.dispatch("invitations/createInvitation", data);
        this.showNotification("success", "Successfully sent assigner invite!");
      } catch (error) {
        this.showNotification(
          "error",
          "Unexpected error with task delegation."
        );
      }
    },

    async updateTaskAssigner() {
      //this.$store.commit("setNewTaskAssignee", this.taskId, newAssigner);
      if (
        this.selectedAssigner === "" ||
        this.selectedAssigner === "Select New Assigner"
      ) {
        this.showNotification("error", "Select a task assigner.");
        return;
      } else {
        try {
          const dueDate = `${this.fetchedTask.dueDate}T15:30:00.000z`;

          const taskData = {
            taskName: this.fetchedTask.taskName,
            taskDescription: this.fetchedTask.taskDescription,
            assignerID: this.selectedUserID,
            taskPriority: this.fetchedTask.taskPriority,
            dueDate: dueDate,
            isComplete: this.fetchedTask.isComplete,
          };
          await this.$store.dispatch("tasks/updateTask", {
            taskId: this.taskId,
            taskData: taskData,
          });
        } catch (error) {
          this.showNotification(
            "error",
            "Unexpected error with task delegation."
          );
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

    async editTask() {
      try {
        const dueDate = `${this.fetchedTask.dueDate}T15:30:00.000z`;

        const taskData = {
          taskName: this.fetchedTask.taskName,
          taskDescription: this.fetchedTask.taskDescription,
          assignerID: this.fetchedTask.assignerID,
          taskPriority: this.fetchedTask.taskPriority,
          dueDate: dueDate,
          isComplete: true,
        };

        const s = await this.$store.dispatch("tasks/updateTask", {
          taskId: this.taskId,
          taskData: taskData,
        });

        this.showNotification("success", "Task successfully edited!");
        await new Promise((resolve) => setTimeout(resolve, 2500));

        if (s == true) {
          this.showNotification("success", "Task successfully updated!");
          await new Promise((resolve) => setTimeout(resolve, 2500));
        }

        //this.goBack();
      } catch (error) {
        console.error("Failed to edit task", error);
        this.showNotification("error", "Task failed to edit.");
        await new Promise((resolve) => setTimeout(resolve, 2500));
      }
    },

    goToAddUsers() {
      this.$router.push({
        name: "addUserTask",
        params: {
          taskId: this.fetchedTask.taskID.toString(),
        },
      });
    },

    goToRemoveUsers() {
      console.log("Remove Users task actions");
      this.$router.push({
        name: "removeUserTask",
        params: {
          orgIndex: this.fetchedTask.parentOrgID,
          projIndex: this.fetchedTask.parentProjectID,
          taskId: this.$route.params.taskId,
        },
      });
    },
    goToFiles() {
      this.$router.push({
        name: "Files",
        params: {
          orgIndex: this.fetchedTask.parentOrgID,
          projIndex: this.fetchedTask.parentProjectID,
          taskId: this.$route.params.taskId,
        },
      });
    },
    async handleFileUpload(event) {
      try {
        const file = event.target.files[0];
        const fileContents = await this.readFileAsBase64(file);
        if (file) {
          const data = {
            taskID: this.fetchedTask.taskID,
            fileName: file.name,
            fileType: file.type,
            fileContents: fileContents.replace(/ /g, '+').trim(),
            uploaderID: this.currentUser.userID,
          };
          await this.$store.dispatch("tasks/addFile", data);
          console.log("File successfully added to the task!");
          this.showNotification("success", "File successfully added to the task");

        }
      } catch (error) {
        console.error("Error adding file to task:", error);
        this.showNotification("error", "Error adding file to the task");

      }
    },

    readFileAsBase64(file) {
      return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onload = () => resolve(reader.result); // Resolve with file contents
        reader.onerror = (error) => reject(error); // Reject in case of an error
        reader.readAsDataURL(file); // Read the file as a Base64 string
      });
    },

    goToTaskChat() {
      this.$router.push({
        name: 'TaskChat',
        params: {
          taskIndex: `task-${this.fetchedTask.chatID}`,
        }
      });
    },
  },
};
</script>

<style scoped>
/* Remove most custom CSS as we're using Tailwind classes */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* Only keep transitions and animations that aren't easily done with Tailwind */
@keyframes pulse {

  0%,
  100% {
    opacity: 1;
  }

  50% {
    opacity: .5;
  }
}
</style>