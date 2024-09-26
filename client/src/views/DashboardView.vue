<template>
  <div v-if="!isLoggedIn" class="relative w-full h-screen bg-white">
    <NavBar />

    <!-- Profile Section -->
    <div class="absolute left-32 top-36 flex items-center">
      <img :src="user.profilePicture" alt="Profile Picture" class="w-48 h-48 rounded-full" />
      <div class="ml-8">
        <div class="text-4xl font-bold text-primary">{{ user.firstName }} {{ user.lastName }}</div>
        <div class="text-2xl font-semibold text-secondary">Welcome to your dashboard!</div>
      </div>
    </div>

    <!-- Kanban Board -->
    <div class="absolute left-28 top-96 flex space-x-8">
      <div class="flex flex-col w-72">
        <div class="flex items-center justify-between p-4 bg-white border border-gray-300 rounded-t-lg">
          <div class="text-xl font-bold text-gray-800">Column Title</div>
          <div class="w-6 h-6 border border-gray-300 rounded-full flex items-center justify-center">
            <span class="text-sm font-bold text-gray-800">0</span>
          </div>
        </div>
        <div class="flex flex-col space-y-4 p-4 bg-white border border-gray-300 rounded-b-lg">
          <div class="p-4 bg-white border border-gray-300 rounded-lg">Task 1</div>
          <div class="p-4 bg-white border border-gray-300 rounded-lg">Task 2</div>
        </div>
      </div>
      <!-- Repeat for additional columns -->
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