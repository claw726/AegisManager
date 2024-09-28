<template>
  <div class="relative w-full h-screen bg-background">
    <NavBar />

    <div v-if="isLoggedIn">
      <!-- Profile Section -->
      <div class="relative flex justify-center h-screen/3 py-12">
        <img :src="userPhoto" alt="Profile Picture" class="w-48 h-48 rounded-full drop-shadow-xl col-span-1" />
        <div class="ml-8 flex flex-col justify-center">
          <div class="text-4xl font-bold text-primary">{{ userFirstName }} {{ userLastName }}</div>
          <div class="text-2xl font-semibold text-secondary">Welcome to your dashboard!</div>
          <div class="py-4 flex-col">
            <button class="bg-primary text-white rounded-lg p-4" @click="goToViewOrgs">Your Organizations 🏢</button>
          </div>
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
  </div>
</template>

  
  <script>
  import NavBar from '@/components/NavBar.vue';
  import { mapState } from 'vuex';
  
  export default {
    components: {
      NavBar,
    },
    data() {
      return {
        user: {},
      };
    },
    computed: {
      ...mapState(['isLoggedIn', 'userEmail', 'userFirstName', 'userLastName', 'userPhoto']),
    },
    methods: {

      goToViewOrgs() {
        this.$router.push({ name: 'viewOrgs' });
      },
    },
  };
  </script>