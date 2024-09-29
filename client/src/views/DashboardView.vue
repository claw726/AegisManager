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
            <button @click="goToKanban" class="button-container viewtask-button bg-primary border border-highlight text-white font-semibold rounded">
              View Projects!
            </button>

          </div>
        </div>
        
    </div>
  </div>
  </div>

  <div class=" flex justify-center items-start min-h-screen">

    <div class=" flex-col space-y-4 mt-8">
            <button class="bg-primary text-white rounded-lg p-4" @click="goToTDList">Your Tasks 🏢</button>

            <button @click="goToTDList" class="button-container viewtask-button bg-primary border border-highlight text-white font-semibold rounded">
              View Tasks!
            </button>

            <button @click="goToKanban" class="button-container viewtask-button bg-primary border border-highlight text-white font-semibold rounded">
              View Projects!
            </button>

            <button class="button-container viewtask-button bg-primary border border-highlight text-white font-semibold rounded">
              View Organizations!
            </button>

            <button class="button-container viewtask-button bg-primary border border-highlight text-white font-semibold rounded">
              View Settings!
            </button>
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

      goToTDList() {
        this.$router.push({ name: 'TDList' });
      },

      goToKanban() {
        this.$router.push({ name: 'KB' });
      },
      
    },
  };
  </script>