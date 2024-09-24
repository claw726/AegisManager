<template>
    <div class="relative w-full h-20 bg-secondary flex items-center justify-between px-6">
      <div class="text-white font-bold text-lg cursor-pointer" @click="goToHome">Aegis Manager</div>
      <div class="flex space-x-4">
        <button v-if="isLoggedIn" @click="logout" class="bg-primary border-2 border-highlight text-white font-semibold py-2 px-4 rounded">Log Out</button>
        <template v-else>
          <button @click="goToLogin" class="bg-primary border border-highlight text-white font-semibold py-2 px-4 rounded">Log In</button>
          <button @click="goToCreateAcct" class="bg-primary border border-highlight text-white font-semibold py-2 px-4 rounded">Sign Up</button>
        </template>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: 'NavBar',
    data() {
      return {
        isLoggedIn: false,
      };
    },
    created() {
      this.checkLoginStatus();
    },
    methods: {
      checkLoginStatus() {
        // Check if the CurrentUser.json exists in local storage
        this.isLoggedIn = !!localStorage.getItem('CurrentUser');
      },
      goToHome() {
        this.$router.push({ name: 'Home' });
      },
      goToLogin() {
        this.$router.push({ name: 'Login' });
      },
      goToCreateAcct() {
        this.$router.push({ name: 'CreateAcct' });
      },
      logout() {
        // Remove the CurrentUser.json from local storage
        localStorage.removeItem('CurrentUser');
        this.$router.push({ name: 'Home' });
        this.isLoggedIn = false;
      },
    },
  };
  </script>