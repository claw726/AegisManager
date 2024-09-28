<template>
    <div class="relative w-full h-screen bg-background">
      <NavBar />
    
      <div class="relative flex flex-col justify-center h-screen/3 py-16">
        <div class="text-4xl font-bold text-primary text-center text-primary py-4">Your Organizations</div>
        <div class="h-1 bg-accent drop-shadow-lg"></div>
    
        <div class="relative flex justify-evenly w-full">
          <div v-if="userOrganizations.length > 0">
            <div v-for="organization in userOrganizations" :key="organization.OrgID" class="w-96 my-4">
              <div class="flex flex-col justify-between p-4 border border-gray-300 rounded-lg drop-shadow-lg">
                <OrgCard :organization="organization" />
              </div>
            </div>
          </div>
          <div v-else>
            <div class="text-lg font-bold text-gray-800 text-center">You are not enrolled in an organization ☹️</div>
          </div>
        </div>
        <div class="flex justify-center">
            <button class="bg-primary text-white rounded-lg p-2 mt-4" @click="goToCreateOrg">Create an Organization</button>
          </div>
      </div>
    </div>
  </template>

<script>
import NavBar from '@/components/NavBar.vue';
import OrgCard from '@/components/OrgCard.vue';
import { mapState } from 'vuex';

export default {
    components: {
        NavBar,
        OrgCard,
    },
    computed: {
      ...mapState(['isLoggedIn']),
    },
    data() {
        return {
            userOrganizations: [],
        };
    },
    created() {
        this.loadUserOrganizations();
    },
    methods: {
        loadUserOrganizations() {
            //Retreive a list of user's orgs from local storage
            const userOrganizations = localStorage.getItem('UserOrganizations');
            if (userOrganizations) {
                this.userOrganizations = JSON.parse(userOrganizations);
            } else {
                this.userOrganizations = [];
            }
        },
        goToCreateOrg() {
            this.$router.push({ name: 'createOrg' });
        }
    }
};
</script>