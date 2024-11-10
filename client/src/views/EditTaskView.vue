<template>
  <div class="relative w-full h-screen bg-background">
    <!-- NavBar  -->
    <NavBar />

    <!-- Main Content -->
    <div
      v-if="fetchedTask"
      class="cc text-4xl font-bold text-hunter-green mb-6"
    >
      Edit Task
    </div>

    <!-- Form Container -->
    <form @submit.prevent="handleSubmit">
      <div class="flex flex-wrap -mx-4 bg-white shadow-lg rounded-lg p-8">
        <div class="w-full md:w-1/2 px-4 mb-4">
          <label class="block text-sm font-semibold text-gray-800 mb-2">
            Task Name</label
          >
          <input
            v-model="fetchedTask.taskName"
            type="text"
            class="w-full border border-highlight rounded-lg p-3"
          />
        </div>

        <div class="w-full md:w-1/2 px-4 mb-4">
          <label class="block text-sm font-semibold text-gray-800 mb-2"
            >Task Description</label
          >
          <input
            v-model="fetchedTask.taskDescription"
            type="text"
            class="w-full border border-highlight rounded-lg p-3"
          />
        </div>

        <div class="w-full md:w-1/2 px-4 mb-4">
          <label class="block text-sm font-semibold text-gray-800 mb-2"
            >Due Date</label
          >
          <input
            v-model="fetchedTask.dueDate"
            type="date"
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

        <NotificationComponent
          class="flex"
          :show="notification.show"
          :type="notification.type"
          @close="closeNotification"
        >
          {{ notification.message }}
        </NotificationComponent>

        <!-- Submit Button -->
        <button
          type="submit"
          data-testid="submit-button"
          class="w-full mt-4 bg-primary text-white font-semibold py-3 rounded-lg"
          @click="handleEditTask"
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
import NotificationComponent from "@/components/NotificationComponent.vue";

export default {
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

      notification: {
        show: false,
        type: "",
        message: "",
      },
    };
  },

  computed: {
    ...mapState("auth", ["isLoggedIn", "currentUser"]),
  },

  async mounted() {
    this.fetchedTask = await this.$store.dispatch(
      "tasks/fetchTask",
      this.$route.params.taskId
    );
    console.log("Client has stored fetched task.");
  },

  methods: {
    ...mapActions("auth", ["user", "isLoggedIn", "currentUser"]),

    async handleEditTask() {
      try {
        this.editTask();
        console.log("Task has been edited.");
        this.showNotification("success", "Task successfully edited!");
        await new Promise((resolve) => setTimeout(resolve, 2000));
      } catch (error) {
        console.log("Error editing task.");
        this.showNotification("error", "Task was not edited!");
        await new Promise((resolve) => setTimeout(resolve, 2000));
      }

      //Show task again but refreshed with edits, random query to refres h
      this.$router.push({ name: "TDList" });
      this.$forceUpdate();
    },

    async editTask() {
      try {
        //New date format is: "2024-11-01 T11:00:11.000+00:00T15:30:00.000z"
        //const dueDate = `${this.fetchedTask.dueDate}T15:30:00.000z`;
        const dueDate = `${this.fetchedTask.dueDate}T11:00:11.000+00:00T15:30:00.000z`;

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

        //this.showNotification("success", "Task successfully edited!");
        //await new Promise(resolve => setTimeout(resolve, 2500));
        return s;
      } catch (error) {
        console.error("Failed to edit task", error);
        // Error will be handled by Vuex store and displayed via updateStatus
      }
    },

    showNotification(type, message) {
      this.notification = {
        show: true,
        type: type,
        message: message,
      };
    },

    closeNotification() {
      this.notification.show = false;
    },

    async getTask() {
      console.log("Doing async method");
      return this.$store.dispatch("tasks/fetchTask", this.taskId);
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