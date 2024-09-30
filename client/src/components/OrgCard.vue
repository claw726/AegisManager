<!-- OrganizationCard.vue -->
<template>
    <div class="flex flex-row place-items-center p-4" @click="viewOrganization">
      <img :src="organization.OrgLogo" alt="🏢" class="w-16 h-16 rounded-full drop-shadow-xl mr-4 text-center text-4xl" />
      <div class="flex-1 flex flex-col">
        <div class="text-lg font-bold text-gray-800 text-left">{{ organization.OrgName }}</div>
        <div class="text-sm text-gray-500 text-left">{{ organization.OrgCreator }}</div>
      </div>
    </div>
  </template>
  
  <script>

  import { mapState } from 'vuex';

  export default {
    props: {
      organization: {
        type: Object,
        required: true,
      },
      index: {
        type: Number,
        required: true,
      },
    },
    computed: {
        ...mapState(['userEmail']),
    },
    methods: {
        deleteOrganization() {
            const userOrganizations = JSON.parse(localStorage.getItem('UserOrganizations'));
            userOrganizations.splice(this.index, 1);
            localStorage.setItem('UserOrganizations', JSON.stringify(userOrganizations));
            this.$emit('organizationDeleted');
            alert("Not fully implemented yet. Leftover org data will persist in storage.");
        },
        viewOrganization() {
            this.$router.push({ name: 'OrganizationDashboard', params: { index: this.index }});
        },
    },
  };
  </script>