<template>
  <div class="relative w-full h-screen bg-background">
    <!-- Navbar -->
    <div class="absolute w-full h-20 bg-secondary flex items-center justify-between px-6">
      <div class="text-white font-bold text-lg">Aegis Manager</div>
      <div class="flex space-x-4">
        <button class="bg-primary border-2 border-highlight text-white font-semibold py-2 px-4 rounded">Log In</button>
        <button class="border-2 border-highlight text-white font-semibold py-2 px-4 rounded">Sign Up</button>
      </div>
    </div>

    <!-- Main Content and Decorative Image -->
    <div class="flex justify-between items-center h-full px-28">
      <!-- Left Column: Main Content and Call to Action -->
      <div class="flex flex-col space-y-8">
        <div>
          <h1 class="text-5xl font-extrabold text-primary">Streamline your projects with Aegis</h1>
          <p class="mt-4 text-xl font-medium text-gray-800">A simple, intuitive project management system for teams and individuals.</p>
        </div>
        <div class="flex space-x-4">
          <button class="bg-zinc-800 text-white font-semibold py-3 px-6 rounded">Get Started Now 🚀</button>
        </div>
      </div>

      <!-- Right Column: Decorative Image -->
      <div>
        <img src="../../icon/Check-icon.png" alt="Check Icon" class="w-96" />
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LandingPage',
};
</script>