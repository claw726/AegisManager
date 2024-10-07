<template>
    <div >
      <table class="flex justify-center">
        <thead>
          <tr class="text-xl">
            <th class="p-6">Email</th>
            <th class="p-6">Add</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(user, index) in users" :key="index">
            <td>{{ user }}</td>
            <td>
              <button @click="addUser(user)">Add</button>
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
      addUser(email) {
        this.$emit('addUser', email);
      },
    },
  };
  </script>