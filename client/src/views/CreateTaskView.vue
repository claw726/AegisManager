<template>
  <div class="relative w-full h-screen bg-background">
    <!-- NavBar  -->
    <NavBar />

    <!-- Main Content -->
    <div class="cc text-4xl font-bold text-hunter-green mb-6">Create Task</div>

    <!-- Form Container -->
    <form @submit.prevent="handleSubmit">
      <div class="flex flex-wrap -mx-4 bg-white shadow-lg rounded-lg p-8">
        <div class="w-full md:w-1/2 px-4 mb-4">
          <label class="block text-sm font-semibold text-gray-800 mb-2">
            Task Name</label
          >
          <input
            type="text"
            v-model="task.taskName"
            class="w-full border border-highlight rounded-lg p-3"
          />
        </div>

        <div class="w-full md:w-1/2 px-4 mb-4">
          <label class="block text-sm font-semibold text-gray-800 mb-2"
            >Task Description</label
          >
          <input
            type="text"
            v-model="task.taskDescription"
            class="w-full border border-highlight rounded-lg p-3"
          />
        </div>

        <div class="w-full md:w-1/2 px-4 mb-4">
          <label class="block text-sm font-semibold text-gray-800 mb-2"
            >Due Date</label
          >
          <input
            type="date"
            v-model="task.dueDate"
            class="w-full border border-highlight rounded-lg p-3"
          />
        </div>

        <div class="w-full md:w-1/2 px-4 mb-4">
          <label class="block text-sm font-semibold text-gray-800 mb-2"
            >Priority</label
          >
          <select v-model="task.taskPriority">
            <option value="Low">Low</option>
            <option value="Medium">Medium</option>
            <option value="High">High</option>
          </select>
        </div>

        <!-- Submit Button -->
        <button
          @click="showTaskCreatedNotif"
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
    ...mapState(["isLoggedIn", "currentUser"]),
  },
  data() {
    return {
      task: {
        taskName: "",
        taskDescription: "",
        dueDate: "",
        taskPriority: "",
        parentOrgID: null,
        parentProjectID: null,
        assignerID: null,
      },

      showTaskCreatedNotifBool: false,
    };
  },
  watch: {},
  methods: {
    ...mapActions(["user", "isLoggedIn", "currentUser"]),
    showTaskCreatedNotif() {
      this.showTaskCreatedNotifBool = true;
      this.callCreateTask();
    },

    callCreateTask() {
      const t = {
        dueDate: this.task.dueDate.toString().concat("T11:00:11.000Z"),
        taskName: this.task.taskName,
        taskDescription: this.task.taskDescription,
        taskPriority: this.task.taskPriority,

        assignerID: 1, //NEED TO FIX, FOR NOW HARDCODING USERID this.currentUser.userID
        parentProjectID: 1, //this.$route.params.orgId,
        parentOrgID: 1, //this.$route.params.projId,
      };

      console.log(t);
      this.$store.dispatch("tasks/createTask", t);
    },

    closeTaskCreatedNotif() {
      this.showTaskCreatedNotifBool = false;
      // Redirect to the viewOrgs page
      this.$router.push({ name: "TDList" });
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
