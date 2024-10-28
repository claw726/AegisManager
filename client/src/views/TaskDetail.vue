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
              <span 
                class="px-3 py-1 rounded-full text-sm font-semibold"
                :class="{
                  'bg-green-100 text-green-800': fetchedTask.complete,
                  'bg-red-100 text-red-800': !fetchedTask.complete
                }"
              >
                {{ fetchedTask.complete ? 'Complete' : 'Incomplete' }}
              </span>
            </div>
          </div>

        <!-- Task Details -->
          <div 
          v-if="fetchedTask.complete" 
          class="greyed-out space-y-4">

            <h2 class="text-lg font-semibold mb-2">Description</h2>
            <p>{{ fetchedTask.taskDescription }}</p>

            <div class="grid grid-cols-2 gap-4">
              <div>
                <h2 class="text-lg font-semibold mb-2">Priority</h2>
                <span 
                  class="px-2 py-1 rounded-md text-sm font-medium"
                  :class="{
                    'bg-red-100 text-red-800': fetchedTask.taskPriority === 'High',
                    'bg-yellow-100 text-yellow-800': fetchedTask.taskPriority === 'Medium',
                    'bg-blue-100 text-blue-800': fetchedTask.taskPriority === 'Low'
                  }"
                >
                  {{ fetchedTask.taskPriority }}
                </span>
              </div>

              <div>
                <h2 class="text-lg font-semibold mb-2">Due Date</h2>
                <p>{{ formatDate(fetchedTask.dueDate) }}</p>
              </div>
            </div>
          </div>

            <div 
          v-else="fetchedTask.complete" 
          class="space-y-4">

            <h2 class="text-lg font-semibold mb-2">Description</h2>
            <p>{{ fetchedTask.taskDescription }}</p>

            <div class="grid grid-cols-2 gap-4">
              <div>
                <h2 class="text-lg font-semibold mb-2">Priority</h2>
                <span 
                  class="px-2 py-1 rounded-md text-sm font-medium"
                  :class="{
                    'bg-red-100 text-red-800': fetchedTask.taskPriority === 'High',
                    'bg-yellow-100 text-yellow-800': fetchedTask.taskPriority === 'Medium',
                    'bg-blue-100 text-blue-800': fetchedTask.taskPriority === 'Low'
                  }"
                >
                  {{ fetchedTask.taskPriority }}
                </span>
              </div>

              <div>
                <h2 class="text-lg font-semibold mb-2">Due Date</h2>
                <p>{{ formatDate(fetchedTask.dueDate) }}</p>
              </div>
            </div>
          </div>

          <!-- Task Actions -->
          <div class="flex justify-end space-x-4 mt-6">
            <button 
              @click="goBack" 
              class="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300"
            >
              Back
            </button>
            <button 
              v-if="!fetchedTask.complete"
              @click="markAsComplete" 
              class="px-4 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600"
            >
              Mark Complete
            </button>
          </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";
import NavBar from "@/components/NavBar.vue";

export default {
  name: 'TaskDetail',

  data() {
    return {
      isGrayedOut: null,
      fetchedTask: null,
    };
  },
  
  components: {
    NavBar,
  },

  props: {
    taskId: {
      type: [String, Number],
      required: true
    }
  },

  computed: {
    ...mapState("tasks", ["allTasks"], "currentTask", "updateTask"),
  },

  async mounted() {
    this.fetchedTask = await this.$store.dispatch("tasks/fetchTask", this.$route.params.taskId);
    console.log("Client has stored fetched task.");
    console.log(JSON.stringify(this.fetchedTask, null, 2));
  },

  methods: {
    goBack() {
     this.$router.go(-1);
   },

    async getTask() {
      console.log("Doing async method")
      //this.$store.dispatch("tasks/fetchTask", this.taskId);
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

.greyed-out {
  opacity: 0.3; /* Adjust the opacity as needed */
}

</style>

