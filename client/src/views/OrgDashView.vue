<template>
    <div v-if="isLoggedIn" class="flex flex-col h-full w-full min-h-screen bg-background">
      <NavBar />
      
      <div class="flex justify-end">
          <button class="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded m-4 justify-center h-12">⚙️</button>
      </div>

      <div class="flex justify-center justify-items-center p-4">
        <div v-if="org">
          <div class="relative flex h-screen/3 py-4">
          <img :src="org.OrgLogo" alt="Profile Picture" class="w-48 h-48 rounded-full drop-shadow-xl col-span-1" />
          <div class="ml-8 flex flex-col justify-center">
            <div class="text-4xl font-bold text-primary">{{ org.OrgName}}</div>
            <div class="text-2xl font-semibold text-secondary">{{ org.OrgDescription }}</div>
            <div class="text-medium text-accent">Created by: {{ org.OrgCreator }}</div>
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
      <div class="grid grid-cols-4 gap-4 m-8">
        <ProjCard v-for="project in projects" :key="project.id" :project="project" />
      </div>
      <div class="flex flex-col items-center">
        <button @click="goToCreateProject" class="dashboard-button ">Create New Task</button>
      </div>
    </div>
</template>
  
  <script>
  import NavBar from '../components/NavBar.vue';
  import { mapState } from 'vuex';
  import ProjCard from '@/components/ProjCard.vue';
  
  export default {
    props: {
      index: {
        type: Number,
        required: true,
      },
    },
    data() {
      return {
        org: null,
        projects: [
        {
          id: 1,
          image: 'https://images7.alphacoders.com/131/1314501.jpg',
          title: 'Project 1',
          description: 'This is a short description of Project 1.',
          owner: 'John Doe',
          tasks: 5
        },
        {
          id: 2,
          image: 'https://www.pixelstalk.net/wp-content/uploads/2016/11/Download-Pictures-Eve-Online.jpg',
          title: 'Project 2',
          description: 'This is a short description of Project 2.',
          owner: 'Jane Doe',
          tasks: 3
        },
        // Add more projects here...
      ]
      };
    },
    components: {
      NavBar,
      ProjCard,
    },
    created() {
      this.getOrgData();
    },
    computed: {
      ...mapState(['isLoggedIn']),
    },
    methods: {
      getOrgData() {
        const userOrganizations = JSON.parse(localStorage.getItem('UserOrganizations'));
        this.org = userOrganizations[this.$route.params.index];
        if (!this.org) {
          alert("There was an error fetching the organization data");
          this.$router.push({name: 'viewOrgs'});
        }
      },
      goToCreateProject() {
            this.$router.push({ name: 'createProject', params: { index: this.index }});
        },
    },
  };
  </script>