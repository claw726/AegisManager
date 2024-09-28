<template>
    <div class="relative w-full h-screen bg-background">
      <NavBar />
  
      <div v-if="!isLoggedIn" class="flex flex-col items-center mt-20">
        <div class="text-3xl font-bold text-primary">Login</div>
        <div class="text-xl font-semibold text-secondary mt-2">Access Your Aegis Account</div>
  
        <div class="w-3/4 max-w-4xl mt-10 bg-white shadow-lg rounded-lg p-8">
          <div class="flex flex-wrap -mx-4">
            <div class="w-full md:w-1/2 px-4 mb-4">
              <label class="block text-sm font-semibold text-gray-800 mb-2">Email Address</label>
              <input type="email" v-model="email" class="w-full border border-highlight rounded-lg p-3" />
            </div>
            <div class="w-full md:w-1/2 px-4 mb-4">
              <label class="block text-sm font-semibold text-gray-800 mb-2">Password</label>
              <input type="password" v-model="password" class="w-full border border-highlight rounded-lg p-3" />
            </div>
            <button @click="login" class="w-full mt-4 bg-primary text-white font-semibold py-3 rounded-lg">Log In</button>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import NavBar from '@/components/NavBar.vue';
  import { mapState, mapActions } from 'vuex';
  
  export default {
    components: {
      NavBar,
    },
    computed: {
      ...mapState(['isLoggedIn', 'userEmail', 'userFirstName', 'userLastName', 'userPhoto']),
    },
    data() {
      return {
        email: '',
        password: '',
      };
    },
    watch: {
      'email'(newEmail) {
        this.email = newEmail.trim();
        this.email = newEmail.toLowerCase();
      },
      'password'(newPassword) {
        this.password = newPassword.trim();
      },
    },
    methods: {
      login() {
        if (!this.email || !this.password) {
          alert('Please enter both email and password.');
          return;
        }
  
        const existingAccounts = localStorage.getItem('userAccounts');
        if (existingAccounts) {
          const accounts = JSON.parse(existingAccounts);
          const user = accounts.find(account => account.email === this.email && account.password === this.password);
  
          if (user) {
            // Redirect to a different page or perform other actions upon successful login
            this.$store.dispatch('login', this.email);
            // localStorage.setItem('CurrentUser', JSON.stringify(user));
            
            this.$router.push({ name: 'Dashboard' });
          } else {
            alert('Invalid email or password.');
          }
        } else {
          alert('No accounts found. Please create an account first.');
        }
      },
    },
  };
  </script>