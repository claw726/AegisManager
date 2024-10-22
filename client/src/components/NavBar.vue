<template>
  <div
    class="relative w-full h-20 bg-secondary flex items-center justify-between px-6"
  >
    <!-- Brand Name -->
    <div class="text-white font-bold text-lg cursor-pointer" @click="goToHome">
      Ægis Manager
    </div>
    <!-- Navigation Buttons -->
    <div class="flex flex-row items-center space-x-4">
      <!-- If the user is logged in, show the logout button -->
      <div v-if="isLoggedIn" class="flex items-center space-x-4">
        <button @click="goToDashboard" class="dashboard-button">
          Dashboard
        </button>
        <button @click="logout" class="dashboard-button">Log Out</button>
        <!-- User Profile Button -->
        <button @click="goToSettings">
          <img
            :src="
              currentUser.profilePicture ||
              'https://toppng.com/public/uploads/preview/instagram-default-profile-picture-11562973083brycehrmyv.png'
            "
            alt="Profile Picture"
            class="w-12 h-12 rounded-full drop-shadow-xl border border-gray-200 justify-center transform transition-transform duration-300 hover:scale-105"
          />
        </button>
      </div>
      <!-- If the user is not logged in, show the login and sign up buttons -->
      <template v-else>
        <button
          @click="goToLogin"
          class="bg-primary border border-highlight text-white font-semibold py-2 px-4 rounded"
        >
          Log In
        </button>
        <button
          @click="goToCreateAcct"
          class="bg-primary border border-highlight text-white font-semibold py-2 px-4 rounded"
        >
          Sign Up
        </button>
      </template>
    </div>
  </div>
</template>

<script>
import { mapState, mapActions } from "vuex";

export default {
  name: "NavBar",
  computed: {
    ...mapState(["isLoggedIn", "currentUser"]),
  },
  methods: {
    ...mapActions(["logout"]),
    // Check if the user is logged in
    checkLoginStatus() {
      this.$store.dispatch("checkLoginStatus");
    },
    // Navigate to the home page
    goToHome() {
      this.$router.push({ name: "Home" });
    },
    // Navigate to the login page
    goToLogin() {
      this.$router.push({ name: "Login" });
    },
    // Navigate to the create account page
    goToCreateAcct() {
      this.$router.push({ name: "CreateAcct" });
    },
    // Logout the user and navigate to the home page
    logout() {
      this.$store.dispatch("logout");
      this.$router.push({ name: "Home" });
    },
    //Access Dashboard from anywhere
    goToDashboard() {
      this.$router.push({ name: "Dashboard" });
    },
    goToSettings() {
      this.$router.push({ name: "AccountSettings" });
    },
  },
};
</script>
