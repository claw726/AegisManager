<template>
  
  <div class="relative w-full h-full min-h-screen bg-background">
    <NavBar />

    <div v-if="isLoggedIn">
      <!-- Profile Section -->
      <div class="relative flex justify-center h-screen/3 py-12">
        <img :src="userPhoto" alt="Profile Picture" class="w-48 h-48 rounded-full drop-shadow-xl col-span-1" />
        <div class="ml-8 flex flex-col justify-center">
          <div class="text-4xl font-bold text-primary">{{ userFirstName }} {{ userLastName }}</div>
          <div class="text-2xl font-semibold text-secondary">Welcome to your dashboard!</div>
          <div class="py-4 flex-col">

            <button @click="goToViewTasks" class="button-container dashboard-button">View Tasks</button>

          <button @click="goToTDList" class="button-container dashboard-button">View Tasks</button>

          <button @click="goToProjects" class="button-container dashboard-button">View Projects </button>

          <button @click="goToViewOrgs" class="button-container dashboard-button"> View Organizations 🏢 </button>

          <button @click="goToSettings" class="button-container dashboard-button">Settings ⚙️</button>


          </div>
        </div>
    </div>
  </div>
  </div>

  <div class=" flex justify-center items-start min-h-screen">

    <!-- Button Row -->

    <div class=" flex-col space-y-4 mt-8">


        

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
      ...mapState('auth', ['isLoggedIn', 'currentUser']),
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
      
      goToViewTasks() {
        this.$router.push({ name: 'toDoList'});
      },
      goToProjects() {
        alert("Not Implemented");
      },
      goToSettings() {
        alert("Not Implemented");
      }
    },
  };
  </script>