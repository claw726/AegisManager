<template>
  <div class="relative w-full h-full min-h-screen bg-background">
    <NavBar />

    <div v-if="isLoggedIn">
      <!-- Profile Section -->
      <div v-if="user" class="relative flex justify-center h-screen/3 py-12">
        <img
          :src="currentUser.profilePicture || 'https://toppng.com/public/uploads/preview/instagram-default-profile-picture-11562973083brycehrmyv.png'" 
          alt="Profile Picture"
          class="w-48 h-48 rounded-full drop-shadow-xl col-span-1"
        />
        <div class="ml-8 flex flex-col justify-center">
          <div class="text-4xl font-bold text-primary">
            {{ currentUser.userName }}
          </div>
          <div class="text-2xl font-semibold text-secondary">
            Welcome to your dashboard!
          </div>
          <div class="py-4 flex-col">
            <button
              class="bg-primary text-white rounded-lg p-4"
              @click="goToViewOrgs"
            >
              Your Organizations 🏢
            </button>
          </div>
        </div>
      </div>
      <!-- Button Row -->

      <div class="flex justify-center">
        <button
          @click="goToViewTasks"
          class="button-container dashboard-button"
        >
          View Tasks
        </button>

        <button @click="goToTDList" class="button-container dashboard-button">
          View Tasks
        </button>

        <button @click="goToKanban" class="button-container dashboard-button">
          View Projects
        </button>

        <button @click="goToViewOrgs" class="button-container dashboard-button">
          View Organizations 🏢
        </button>

        <button @click="goToSettings" class="button-container dashboard-button">
          Settings ⚙️
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import NavBar from "@/components/NavBar.vue";
import { mapState } from "vuex";

export default {
  components: {
    NavBar,
  },
  computed: {
    ...mapState(["isLoggedIn", "currentUser"]),
  },
  data() {
    return {
      user: this.currentUser,
    };
  },
  created() {
    this.user = this.currentUser
  },
  methods: {
    goToViewOrgs() {
      this.$router.push({ name: "viewOrgs" });
    },

    goToTDList() {
      this.$router.push({ name: "TDList" });
    },

    goToKanban() {
      this.$router.push({ name: "KB" });
    },

    goToViewTasks() {
      this.$router.push({ name: "toDoList" });
    },
    goToProjects() {
      alert("Not Implemented");
    },
    goToSettings() {
      alert("Not Implemented");
    },
  },
};
</script>
