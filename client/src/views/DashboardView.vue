<template>
  <div class="relative w-full h-screen bg-white">
    <NavBar />

    <!-- Profile Section -->
    <div class="absolute left-32 top-36 flex items-center">
      <img :src="user.profilePicture" alt="Profile Picture" class="w-48 h-48 rounded-full" />
      <div class="ml-8">
        <div class="text-4xl font-bold text-primary">{{ user.firstName }} {{ user.lastName }}</div>
        <div class="text-2xl font-semibold text-secondary">Welcome to your dashboard!</div>
      </div>
    </div>

    
  </div>

  <div class=" flex justify-center items-start min-h-screen h-[calc(100vh-10vh)]">

    <div class=" flex-col space-y-4 mt-8">


        <button @click="goToTDList" class="button-container viewtask-button bg-primary border border-highlight text-white font-semibold rounded">
          <router-link to="/todolist">View Tasks!</router-link>
        </button>

        <button class="button-container viewtask-button bg-primary border border-highlight text-white font-semibold rounded">
          <router-link to="/todolist">View Projects!</router-link>
        </button>

        <button class="button-container viewtask-button bg-primary border border-highlight text-white font-semibold rounded">
          <router-link to="/todolist">View Organizations!</router-link>
        </button>

        <button class="button-container viewtask-button bg-primary border border-highlight text-white font-semibold rounded">
          <router-link to="/todolist">View Settings!</router-link>
        </button>


    </div>     

  </div> 
  
</template>

  
  <script>
  import NavBar from '@/components/NavBar.vue';
  
  export default {
    components: {
      NavBar,
    },
    data() {
      return {
        user: {},
      };
    },
    created() {
      this.loadUserData();
    },
    methods: {
      loadUserData() {
        // Retrieve the current user's data from local storage
        const currentUser = localStorage.getItem('CurrentUser');
        if (currentUser) {
          this.user = JSON.parse(currentUser);
        } else {
          // Redirect to the login page
          this.$router.push({ name: 'Login' });
          // Optionally, redirect to the login page or handle the case where no user is logged in
        }
      },
    },
  };
  </script>