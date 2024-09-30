<template>
    <div v-if="isLoggedIn" class="flex flex-col h-full w-full min-h-screen bg-background">
      <NavBar />
      
      <div class="flex justify-end">
          <button class="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded m-4 justify-center h-12">⚙️</button>
      </div>

      <div class="flex justify-center justify-items-center p-4">
        <div v-if="proj">
          <div class="relative flex h-screen/3 py-4">
          <img :src="proj.ProjImg" alt="Profile Picture" class="h-48 con drop-shadow-xl col-span-1" />
          <div class="ml-8 flex flex-col justify-center">
            <div class="text-4xl font-bold text-primary">{{ proj.ProjName}}</div>
            <div class="text-2xl font-semibold text-secondary">{{ proj.ProjDescription }}</div>
            <div class="text-medium text-accent">Created by: {{ proj.ProjCreator }}</div>
            </div>
          </div>
        </div>
      </div>

      
      
      <!-- Search Bar -->
      <div class="flex p-4 justify-center">
        <p class="text-xl font-semibold text-primary mx-8">Search Projects</p>
        <input type="text" class="w-1/3 mx-2 border border-highlight rounded-lg p-2 " placeholder="Placeholder for Search All Projects" />
        <button class="dashboard-button">Search</button>
      </div>
      <div class="h-1 bg-accent drop-shadow-lg rounded mx-16" />

      <!-- Create Project Button -->
      <div class="flex flex-col items-center">
        <button @click="goToCreateTask" class="dashboard-button mt-8">Create New Task</button>
      </div>

      <!-- List of Projects -->
      <div class="grid grid-cols-4 gap-4 m-8">
        <ProjCard v-for="project in projects" :key="project.id" :project="project" />
      </div>

      
    </div>
</template>
  
  <script>
  import NavBar from '@/components/NavBar.vue';
  import { mapState } from 'vuex';
  
  export default {
    data() {
      return {
        proj: null,
        projects: [],
      };
    },
    components: {
      NavBar,
    },
    created() {
      this.getProjData();
    },
    computed: {
      ...mapState(['isLoggedIn']),
    },
    mounted() {
      this.tasks = JSON.parse(localStorage.getItem(this.$route.params.orgIndex)) || [];
    },
    methods: {
      getProjData() {
        const userOrganizations = JSON.parse(localStorage.getItem(this.$route.params.orgIndex));
        this.proj = userOrganizations[this.$route.params.projIndex];
        if (!this.proj) {
          alert("There was an error fetching the organization data");
          this.$router.push({name: 'OrganizationDashboard'});
        }
      },
      goToCreateTask() {
            // this.$router.push({ name: 'createTask', params: { orgIndex: this.index }});
            alert("Bilsha, can you implement this?");
        },
    },
  };
  </script>   