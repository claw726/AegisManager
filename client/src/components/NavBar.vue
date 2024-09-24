<template>
    <!-- Navbar -->
    <div class="relative w-full h-20 bg-secondary flex items-center justify-between px-6">
      <div @click="goToHome" class="text-white font-bold text-lg">Aegis Manager</div>
      <div class="flex space-x-4">
        <button @click="goToLogin"  class="bg-primary border border-highlight text-white font-semibold py-2 px-4 rounded">Log In</button>
        <button @click="goToCreateAcct" class="bg-primary border border-highlight text-white font-semibold py-2 px-4 rounded">Sign Up</button>
      </div>
    </div>
</template>

<script>
export default {
    name: 'NavBar',
    methods: {
        goToLogin() {
            this.$router.push({ name: 'Login' });
        },
        goToCreateAcct() {
            this.$router.push({ name: 'CreateAcct' });
        },
        goToHome() {
            this.$router.push({ name: 'Home' });
        },
    },
}
</script>