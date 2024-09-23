<template>
  <div class="relative w-full h-screen bg-gray-100">
    <!-- Navbar -->
    <div class="absolute w-full h-20 bg-gray-900 flex items-center justify-between px-6">
      <div class="text-white font-bold text-lg">Aegis Manager</div>
      <div class="flex space-x-4">
        <button class="bg-gray-800 text-white font-semibold py-2 px-4 rounded">Log In</button>
        <button class="border-2 border-white text-white font-semibold py-2 px-4 rounded">Sign Up</button>
      </div>
    </div>

    <!-- Main Content -->
    <div class="absolute top-64 left-28">
      <h1 class="text-5xl font-extrabold text-black">Streamline your projects with Aegis</h1>
      <p class="mt-4 text-xl font-medium text-gray-800">A simple, intuitive project management system for teams and individuals.</p>
    </div>

    <!-- Call to Action -->
    <div class="absolute top-96 left-28 flex space-x-4">
      <button class="bg-gray-800 text-white font-semibold py-3 px-6 rounded">Get Started Now 🚀</button>
      <button class="border-3 border-gray-300 text-gray-300 font-semibold py-3 px-6 rounded">Learn More ⚡️</button>
    </div>

    <!-- Decorative Image -->
    <div class="absolute top-56 right-28">
      <img src="Check-icon.png" alt="Check Icon" class="w-96 h-80" />
    </div>
  </div>
</template>

<script>
export default {
  name: 'LandingPage',
};
</script>

<style scoped>
/* Add any additional styles here if needed */
</style>