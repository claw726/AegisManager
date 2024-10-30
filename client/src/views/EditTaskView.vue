<template>
  <div class="relative w-full h-screen bg-background">
    <!-- NavBar  -->
    <NavBar />

    <!-- Main Content -->
    <div 
    v-if="fetchedTask"
    class="cc text-4xl font-bold text-hunter-green mb-6">Edit Task</div>

    <!-- Form Container -->
    <form @submit.prevent="handleSubmit">
      <div class="flex flex-wrap -mx-4 bg-white shadow-lg rounded-lg p-8">
        <div class="w-full md:w-1/2 px-4 mb-4">
          <label class="block text-sm font-semibold text-gray-800 mb-2">
            Task Name</label
          >
          <input
            type="text"
            v-model="fetchedTask.taskName"
            class="w-full border border-highlight rounded-lg p-3"
          />
        </div>

        <div class="w-full md:w-1/2 px-4 mb-4">
          <label class="block text-sm font-semibold text-gray-800 mb-2"
            >Task Description</label
          >
          <input
            type="text"
            v-model="fetchedTask.taskDescription"
            class="w-full border border-highlight rounded-lg p-3"
          />
        </div>

        <div class="w-full md:w-1/2 px-4 mb-4">
          <label class="block text-sm font-semibold text-gray-800 mb-2"
            >Due Date</label
          >
          <input
            type="date"
            v-model="fetchedTask.dueDate"
            class="w-full border border-highlight rounded-lg p-3"
          />
        </div>

        <div class="w-full md:w-1/2 px-4 mb-4">
          <label class="block text-sm font-semibold text-gray-800 mb-2"
            >Priority</label
          >
          <select v-model="fetchedTask.taskPriority">
            <option value="Low">Low</option>
            <option value="Medium">Medium</option>
            <option value="High">High</option>
          </select>
        </div>

        <!-- Submit Button -->
        <button
          @click="handleEditTask"
          type="submit"
          data-testid="submit-button"
          class="w-full mt-4 bg-primary text-white font-semibold py-3 rounded-lg"
        >
          Submit
        </button>
      </div>
    </form>
  </div>
</template>

<script>
import { mapState, mapActions } from "vuex";
import NavBar from "@/components/NavBar.vue";

export default {
  components: {
    NavBar,
  },

  computed: {
    ...mapState("auth", ["isLoggedIn", "currentUser"]),
  },
  data() {
    return {
      fetchedTask: this.getTask(),
      task: {
        taskName: "",
        taskDescription: "",
        dueDate: "",
        taskPriority: "",
        parentOrgID: null,
        parentProjectID: null,
        assignerID: null,
      },

    };
  },
  props: {
    taskId: {
      type: [String, Number],
      required: true
    }
  },

  async mounted() {
    this.fetchedTask = await this.$store.dispatch("tasks/fetchTask", this.$route.params.taskId);
    console.log("Client has stored fetched task.");
  },

  methods: {
    ...mapActions("auth", ["user", "isLoggedIn", "currentUser"]),

    handleEditTask() {
        try {
            console.log("Inside handling edit task");
        this.editTask();
        //TODO: show notification that task has been updated
        ///console.log("Task has been edited.");
        } catch (error) {
            console.log("Error editing task.")
            //TODO: show notification that task has not been updated
        }
        //Show task again but refreshed with edits, random query to refres h
        this.$router.push({ name: "TDList"});
        this.$forceUpdate();
    },

    async editTask() {
      try {

        //New date format is: "2024-11-01 T11:00:11.000+00:00T15:30:00.000z"
        //const dueDate = `${this.fetchedTask.dueDate}T15:30:00.000z`;
        const dueDate = `${this.fetchedTask.dueDate}T11:00:11.000+00:00T15:30:00.000z`;
        //const dueDate = `${this.fetchedTask.dueDate}`;

        const taskData = {
          taskName: this.fetchedTask.taskName,
          taskDescription: this.fetchedTask.taskDescription,
          assignerID: this.fetchedTask.assignerID,
          taskPriority: this.fetchedTask.taskPriority,
          dueDate: dueDate,
          isComplete: false, //shouldn't be on this page if task is completed
        };

        console.log("Here is the task with updated details:");
        console.log(taskData);

        const s = await this.$store.dispatch("tasks/updateTask", {
          taskId: this.taskId,
          taskData: taskData,
        });

      } catch (error) {
        console.error('Failed to edit task', error);
        // Error will be handled by Vuex store and displayed via updateStatus
      }
    },

    async getTask() {
      console.log("Doing async method")
      return this.$store.dispatch("tasks/fetchTask", this.taskId);
    },
    goBack() {
      this.$router.go(-1);
    },

    createUserJson() {
      const userJson = JSON.stringify(this.user, null, 2);
      console.log("User JSON:", userJson);
      return userJson;
    },
  },
};
</script>

<style scoped>
.cc {
  text-align: center;
  padding-top: 50px;
}
</style>
