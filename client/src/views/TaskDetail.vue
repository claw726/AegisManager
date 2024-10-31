<template>
  <NavBar />

  <div class="min-h-screen bg-gray-50 p-8">

    <!-- Moving buttons up to clear up space -->
    <div 
    v-if="fetchedTask"
    class="max-w-4xl mx-auto bg-white rounded-lg p-6">
      <button @click="goBack" 
      class="px-4 py-2 text-white rounded-lg hover:bg-gray-300"
      style="background-color: #555;">
        Back
      </button>

      <button 
        v-if="!fetchedTask.complete && showLeftButton" @click="markAsComplete"
        class="px-4 py-2 complete-btn text-white rounded-lg hover:bg-green-600">
        Mark Complete
      </button>

    <div class="max-w-4xl mx-auto bg-white rounded-lg shadow-lg p-6">
      
      
      <div v-if="fetchedTask" class="space-y-6">
        <!-- Task Header -->
        <div class="flex justify-between items-start">
          <h1 class="text-2xl font-bold text-primary">{{ fetchedTask.taskName }}</h1>
          <div class="flex space-x-4">
            <!-- Status Badge -->
            <span class="px-3 py-1 rounded-full text-sm font-semibold" :class="{
              'bg-green-800 text-white': fetchedTask.complete,
              'bg-orange-800 text-white': !fetchedTask.complete
            }">
              {{ fetchedTask.complete ? 'Complete' : 'Incomplete' }}
            </span>
          </div>
        </div>

        <!-- Task Details -->
        <div v-if="fetchedTask.complete" class="greyed-out space-y-4">
          <div class="grid grid-cols-2 gap-4">

            <div>
              <h2 class="text-lg font-semibold mb-2">Description</h2>
              <p>{{ fetchedTask.taskDescription }}</p>
            </div>

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

            <div>
              <h2 class="text-lg font-semibold mb-2">Assignees:</h2>
              <p>{{ this.makeNameListofAssignees(fetchedTask.assignedUsers).toString() }}</p>
            </div>

          </div>
        </div>

        <div v-else="fetchedTask.complete" class="space-y-4">
          <div class="grid grid-cols-2 gap-4">

            <div>
              <h2 class="text-lg font-semibold mb-2">Description</h2>
              <p>{{ fetchedTask.taskDescription }}</p>
            </div>

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

            <div>
              <h2 class="text-lg font-semibold mb-2">Assignees:</h2>
              <p>{{ this.makeNameListofAssignees(fetchedTask.assignedUsers).toString() }}</p>
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
              class="px-4 py-2 complete-btn text-white rounded-lg hover:bg-green-600">
              Confirm Reassignment
            </button>
          </div>

          <!-- Right-aligned buttons -->
          <div class="flex space-x-4"> <!-- Align buttons to the right -->
            

            <button
              v-if="!fetchedTask.complete && showLeftButton"
              @click="goToEditTask"
              class="px-4 py-2 edit-btn text-white rounded-lg hover:bg-green-600"
            >
              Edit Task
            </button>

           <button
              v-if="!fetchedTask.complete && showLeftButton"
              @click="goToAddUsers"
              class="px-4 py-2 add-btn text-white rounded-lg hover:bg-green-600"
            >
              Add Users
           </button>

            <button
             v-if="showLeftButton"
             @click="showPopup = true"
             class="px-4 py-2 remove-btn text-gray-700 rounded-lg hover:bg-gray-300"
           >
             Delete Task
           </button>

            <div v-if="showPopup" class="popup">
              <div class="popup-content">
                <p>Are you sure you want to delete this task?</p>
                <button @click="handleYes" class="remove-btn">Yes</button>
                <button @click="handleNo" class="remove-btn">No</button>
              </div>
            </div>


          </div>
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
      completed: null,
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
      showPopup: false,
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
    console.log("Client has stored fetched task.");
    console.log(this.fetchedTask);
    //console.log(JSON.stringify(this.fetchedTask.assignedUsers, null, 2));
    this.makeListOfAssignees();
    this.makeNameListofAssignees();
    await this.populateAssignerDropdown(this.fetchedTask.assignerID);
  },

  async created() {
    this.fetchedTask = await this.$store.dispatch("tasks/fetchTask", this.$route.params.taskId);
    this.completed = this.fetchedTask.isComplete;
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
    },


    makeListOfAssignees() {
      const emailList = this.getEmails(this.fetchedTask.assignedUsers);
      console.log(emailList);
      return emailList;
    },

    makeNameListofAssignees() {
      const nameList = this.getNames(this.fetchedTask.assignedUsers);
      console.log("Printing nameList:");
      console.log(nameList);
      return nameList;
    },

    getEmails(data) {
      if (!Array.isArray(data)) {
        throw new Error("Input data must be an array.");
      }
      return data.map(user => user.email).join(", ");
    },  

    getNames(data) {
      if (!Array.isArray(data)) {
        throw new Error("Input data must be an array.");
      }
      return data.map(user => user.userName).join(", ");
    },

    goBack() {
      this.$router.push({ name: "TDList" });
    },

    goToEditTask(){
     console.log("Edit task actions");
     this.$router.push({
       name: "editTask",
       params: {
         taskId: this.fetchedTask.taskID,
       },
     });
   },

    async getTask() {
      console.log("Doing async method")
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
      if (!date) return 'No due date';
      return new Date(date).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      });
    },

    async makeTaskDeleted() {
     try {
       await this.$store.dispatch("tasks/deleteTask", {
         taskID: this.taskId,
       });

      this.showNotification("success", "Task successfully deleted!");
      await new Promise(resolve => setTimeout(resolve, 2500));

     } catch (error) {
      //TODO: make sure notification only shows if error shows right status code
      //this.showNotification("error", "Task failed to delete.");
     console.error('Failed to delete task:');
     console.error(error);
     //await new Promise(resolve => setTimeout(resolve, 2000));
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
          isComplete: true
        };

        await this.$store.dispatch("tasks/updateTask", {
          taskId: this.taskId,
          taskData: taskData,
        });

        this.showNotification("success", "Task successfully completed!");
        await new Promise(resolve => setTimeout(resolve, 2500));

        // Reroute to task to do list as task is now marked complete
        this.isGrayedOut = true;
        this.$router.push({ name: "TDList" });
      } catch (error) {
        console.error('Failed to mark task as complete:', error);
        // Error will be handled by Vuex store and displayed via updateStatus
      }
    },

    async sendAssignerInvite() {
      if (this.selectedAssigner === "" || this.selectedAssigner === "Select New Assigner") {
        this.showNotification("error", "Select a task assigner.");
        return;
      }
      try {
        const data = {
          senderEmail: this.currentUser.email,
          recipientEmail: this.selectedAssigner,
          invitationType: 1,
          message: this.fetchedTask.taskID + ": Task Assigner Request - " + this.fetchedTask.taskName
        }
        console.log("DATA")
        console.log(data);
        await this.$store.dispatch("invitations/createInvitation", data);
        this.showNotification("success", "Successfully sent assigner invite!");
      }
      catch (error) {
        this.showNotification("error", "Unexpected error with task delegation.");
      }
    },

    async updateTaskAssigner() {
      //this.$store.commit("setNewTaskAssignee", this.taskId, newAssigner);
      if (this.selectedAssigner === "" || this.selectedAssigner === "Select New Assigner") {
        this.showNotification("error", "Select a task assigner.");
        return;
      }
      else {

        try {

          const dueDate = `${this.fetchedTask.dueDate}T15:30:00.000z`;

          const taskData = {
            taskName: this.fetchedTask.taskName,
            taskDescription: this.fetchedTask.taskDescription,
            assignerID: this.selectedUserID,
            taskPriority: this.fetchedTask.taskPriority,
            dueDate: dueDate,
            isComplete: this.fetchedTask.complete
          };
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

    async editTask() {
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

        const s = await this.$store.dispatch("tasks/updateTask", {
          taskId: this.taskId,
          taskData: taskData,
        });

        this.showNotification("success", "Task successfully edited!");
        await new Promise(resolve => setTimeout(resolve, 2500));

        if (s==true) {
          this.showNotification("success", "Task successfully updated!");
          await new Promise(resolve => setTimeout(resolve, 2500));
        }
        
        //this.goBack();
        
      } catch (error) {
        console.error('Failed to edit task', error);
        this.showNotification("error", "Task failed to edit.");
        await new Promise(resolve => setTimeout(resolve, 2500));      
      }
      
    },



    goToAddUsers() {
      this.$router.push({
       name: "addUserTask",
       params: {
        orgIndex: this.fetchedTask.parentOrgID,
        projIndex: this.fetchedTask.parentProjectID,
        taskId: this.$route.params.taskId,
       },
     });
      
    }

  },

  watch: {
    selectedAssigner(newEmail) {
      const selectedUser = this.taskUsers.find(user => user.email === newEmail);
      this.selectedUserID = selectedUser ? selectedUser.ID : null;
    },
  },
};
</script>

<style scoped>
.greyed-out {
  opacity: 0.3;
  /* Adjust the opacity as needed */
}

.remove-btn {
  cursor: pointer;
  background-color: rgb(2, 2, 58);
  color: white;
  border: none;
  padding: 5px 10px;
  margin-left: 63px;
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


.edit-btn {
  cursor: pointer;
  background-color: rgb(77, 12, 23);
  color: white;
  border: none;
  padding: 5px 10px;
}

.add-btn {
 cursor: pointer;
 background-color: rgb(73, 116, 99);
 color: white;
 border: none;
 padding: 5px 10px;
}

.complete-btn {
 cursor: pointer;
 background-color: rgb(15, 54, 38);
 margin-left: 10px;
 padding-left: 20px
 
}
</style>