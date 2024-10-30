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
          @click="callCreateTask"
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
      task: {
        taskName: "",
        taskDescription: "",
        dueDate: "",
        taskPriority: "",
        parentOrgID: null,
        parentProjectID: null,
        assignerID: null,
      },

      //THIS BOOL CONTROLS WHETHER 'TASK CREATED SUCCESSFULLY' NOTIFICATION SHOULD SHOW
      showTaskCreatedNotifBool: false,
    };
  },
  watch: {},
  methods: {
    ...mapActions("auth", ["user", "isLoggedIn", "currentUser"]),
    
    callCreateTask() {
      const t = {
        dueDate: this.task.dueDate.toString().concat("T11:00:11.000Z"),
        taskName: this.task.taskName,
        taskDescription: this.task.taskDescription,
        taskPriority: this.task.taskPriority,

        assignerID: this.currentUser.userID, //NEED TO FIX, FOR NOW HARDCODING USERID this.currentUser.userID
        parentProjectID: this.$route.params.projIndex, //this.$route.params.orgId,
        parentOrgID: this.$route.params.orgIndex, //this.$route.params.projId,
      };

      try {
        console.log(t);
        const r = this.$store.dispatch("tasks/createTask", t);
        console.log("Resulting Promise", r);

        //ADD NOTIFICATION HERE FOR TASK SUCCESSFULLY CREATED
        console.log("About to show notif");
        this.showTaskCreatedNotifBool = true;
      
      } catch(error) {
        console.log("Task cannot be created.");
        //ADD NOTIFICATION FOR FAILED CRREATION
      }
      //will remove this afrer notifiction is implemented
      this.$router.push({ name: "TDList" });
      
    },

    //CALL THIS METHOD WHEN TASK CREATED NOTIFICATION IS CLOSED
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
