<template>
    <div class="flex flex-col">
      <table class="justify-center">
        <thead>
          <tr class="flex text-xl justify-around">
            <th class="p-6">Email</th>
            <th class="p-6">Remove</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(user, index) in users" :key="index">
            <td>{{ user }}</td>
            <td>
              <button @click="removeUser(user)" class="dashboard-button">Remove</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </template>
  
  <script>
  export default {
    props: {
      users: {
        type: Array,
        required: true,
      },
    },
    methods: {
      removeUser(email) {
        this.$emit('removeUser', email);
      },
    },
  };
  </script>