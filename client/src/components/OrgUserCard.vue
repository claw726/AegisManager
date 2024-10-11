<template>
    <div class="flex flex-row place-items-center p-4" @click="viewOrganization">
      <img :src="user.profilePicture" alt="🤵" class="w-16 h-16 rounded-full drop-shadow-xl mr-4 text-center text-4xl" />
      <div class="flex-1 flex flex-col">
        <div class="text-lg font-bold text-gray-800 text-left">{{ user.name }}</div>
        <div class="text-sm text-gray-500 text-left">{{ user.email }}</div>
        <button v-if="!isCurrent" @click.stop="addUser(user)">Add</button>
        <button v-else @click.stop="removeUser(user)">Remove</button>
      </div>
    </div>
  </template>
  
  <script>
  import { mapState } from 'vuex';
  
  export default {
    props: {
      user: {
        type: Object,
        required: true,
      },
      index: {
        type: Number,
        required: true,
      },
      isCurrent: {
        type: Boolean,
        required: true,
      },
    },
    computed: {
      ...mapState(['userEmail']),
    },
    methods: {
      viewOrganization() {
        this.$router.push({ name: 'OrganizationDashboard', params: { orgIndex: this.index }});
      },
      addUser(user) {
        this.$emit('addUser', user);
      },
      removeUser(user) {
        this.$emit('removeUser', user);
      },
    },
  };
  </script>