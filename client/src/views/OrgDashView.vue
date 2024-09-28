<template>
    <div class="flex flex-col h-screen">
      <NavBar />
      
      <div class="flex justify-between p-4">
        <div v-if="org">
          <div class="text-lg font-bold text-gray-800">{{ org.OrgName }}</div>
          <div class="text-sm text-gray-700">{{ org.OrgDescription }}</div>
        </div>
        <div class="flex justify-end">
          <button class="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded">Delete</button>
        </div>
      </div>
      
      <div class="flex justify-center p-4">
        <input type="text" class="w-full border border-gray-300 rounded-lg p-2" placeholder="Placeholder for Search All Projects" />
        <button class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">Search</button>
      </div>
      
    </div>
  </template>
  
  <script>
  import NavBar from '../components/NavBar.vue';
  
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
      };
    },
    components: {
      NavBar,
    },
    created() {
      this.getOrgData();
    },
    methods: {
      getOrgData() {
        const userOrganizations = JSON.parse(localStorage.getItem('UserOrganizations'));
        this.org = userOrganizations[this.$route.params.index];
        if (!this.org) {
          alert("There was an error fetching the organization data");
          this.$router.push({name: 'viewOrgs'});
        }
      }
    },
  };
  </script>