<template>
    <div class="flex flex-col">
      <table class="justify-center">
        <thead>
          <tr class="flex text-xl justify-around">
            <th class="p-6 border">Email</th>
            <th class="p-6 border">Add</th>
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