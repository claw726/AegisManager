<template>
  <div class="relative w-full h-screen bg-background">
    <!-- NavBar  -->
    <NavBar />

    <!-- Main Content -->
    <div 
    v-if="fetchedTask"
    class="cc text-4xl font-bold text-hunter-green mb-6">Add Users to Task</div>

    <!-- Form Container -->
    <form @submit.prevent="handleSubmit">
      <div class="flex flex-wrap cc -mx-4 bg-grey shadow-lg rounded-lg p-8">
        <div class="w-full md:w-1/2 px-4 mb-4">
          <label class="block text-sm font-semibold text-gray-800 mb-2">
            Please Enter the Assignee's Email</label
          >
          <input
            type="text"
            v-model="email"
            class="w-full border border-highlight rounded-lg p-3"
          />
        </div>

        <NotificationComponent class="flex" :show="notification.show" :type="notification.type"
            @close="closeNotification">
            {{ notification.message }}
        </NotificationComponent>

        <!-- Submit Button -->
        <button
          @click="handleAddUser"
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
import NotificationComponent from "@/components/NotificationComponent.vue";


export default {
  components: {
    NavBar,
    NotificationComponent,
  },

  computed: {
    ...mapState("auth", ["isLoggedIn", "currentUser"]),
  },
  data() {
    return {
      fetchedTask: null,
      email: "",

      notification: {
        show: false,
        type: "error",
        message: "",
      },

      notification: {
        show: false,
        type: "info",
        message: "",
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

    async handleAddUser() {
        try {
            const s = this.addUser(this.email)
            if (s == true) {
                //TODO: THROW NOTIFICATION that add is successful
                console.log("Added user successfully inside handle.")
            }
            this.showNotification("success", "Successfully added user to task!");
            await new Promise(resolve => setTimeout(resolve, 2500));
            this.$router.push({ name: "TDList"});
            
        } catch (error) {
            console.error('Failed to add user to task', error);
            //await new Promise(resolve => setTimeout(resolve, 2000));
            this.showNotification("error", "Unexpected error with task delegation.");
            //TODO: THROW NOTIFICATION AND DON'T LEAVE
        }
    },

    async addUser(email) {
      try {

        const s = await this.$store.dispatch("tasks/addUserToTask", {
          email: email,
          taskId: this.fetchedTask.taskID,
        });

        if (s == true) {
          //TODO: show notification that task has been updated
          console.log("User added to task.");
        }
        return true;
      } catch (error) {
        console.error('Failed to add user to task', error);

      }
      this.$router.push({ name: "TDList"});
    },

    async getTask() {
      console.log("Doing async method")
      return this.$store.dispatch("tasks/fetchTask", this.taskId);
    },

    goBack() {
      this.$router.go(-1);
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
  display: flex;
  justify-content: center; /* Center horizontally */
  align-items: center; /* Center vertically */
}
</style>
