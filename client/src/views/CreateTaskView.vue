<template>
  <div class="relative w-full h-screen bg-background">
    <!-- NavBar  -->
    <NavBar />

    <!-- Main Content -->
      <div class="cc text-4xl font-bold text-hunter-green mb-6">Create Task</div>


      <!-- Form Container -->
        <div class="flex flex-wrap -mx-4 bg-white shadow-lg rounded-lg p-8">

          <div class="w-full md:w-1/2 px-4 mb-4 ">
            <label class="block text-sm font-semibold text-gray-800 mb-2"
              >Task Name</label
            >
            <input
              type="text"
              v-model="task.name"
              class="w-full border border-highlight rounded-lg p-3"
            />
          </div>

          <div class="w-full md:w-1/2 px-4 mb-4">
            <label class="block text-sm font-semibold text-gray-800 mb-2"
              >Task Description</label
            >
            <input
              type="text"
              v-model="task.description"
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
            <select v-model="selectedOption">
                <option value="Low">Low</option>
                <option value="Medium">Medium</option>
                <option value="High">High</option>
            </select>
          </div>

          <!-- Submit Button -->
          <button
            @click="showTaskCreatedNotif"
            data-testid="submit-button"
            class="w-full mt-4 bg-primary text-white font-semibold py-3 rounded-lg"
          >
            Submit
          </button>

          <Notification 
            @close="closeTaskCreatedNotif"
            v-if="showTaskCreatedNotifBool" 
            type="error" 
            message="This feature is not functional yet.">
          </Notification>
        </div>
      </div>
</template>

<script>
import { mapState, mapActions } from "vuex";
import NavBar from "@/components/NavBar.vue";
import Notification from "../components/Notification.vue";

export default {
  components: {
    NavBar,
    Notification,
  },

  computed: {
    ...mapState(["isLoggedIn"]),
  },
  data() {
    return {
      task: {
        name: "",
        description: "",
        dueDate: "",
        priority: "",
        orgId: null,
        projId: null,
        userId: null,
      },
      showTaskCreatedNotifBool: false,
    };
  },
  watch: {
    
  },
  methods: {
    ...mapActions(["user", "isLoggedIn"]),
    showTaskCreatedNotif() {
      this.showTaskCreatedNotifBool = true;
    },
    closeTaskCreatedNotif() {
      this.showTaskCreatedNotifBool = false;
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