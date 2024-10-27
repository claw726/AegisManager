<template>
  <div class="relative w-full min-h-screen h-full bg-gray-100">
    <!-- Navbar -->
    <NavBar />

    <!-- Main Content and Decorative Image -->
    <div class="flex justify-between items-center h-full px-10 md:px-28">
      <!-- Left Column: Main Content and Call to Action -->
      <div class="flex flex-col space-y-8 w-full md:w-1/2">
        <div>
          <h1 class="text-5xl font-extrabold text-gray-900">
            Streamline your projects with Ægis
          </h1>
          <p class="mt-4 text-xl font-medium text-gray-700">
            A simple, intuitive project management system for teams and individuals.
          </p>
        </div>
        <div class="flex space-x-4">
          <button
            @click="goToCreateAcct"
            class="flex items-center bg-blue-600 text-white font-semibold py-3 px-6 rounded shadow-lg hover:bg-blue-700 transition duration-300"
          >
            <i class="fas fa-rocket mr-2"></i> Get Started Now
          </button>
        </div>
      </div>

      <!-- Right Column: Decorative Image -->
      <div class="hidden md:block w-1/2">
        <img src="@/assets/Check-icon.png" alt="Check Icon" class="w-full h-auto" />
      </div>
    </div>
  </div>
</template>

<script>
import NavBar from "../components/NavBar.vue";
export default {
  name: "LandingPage",
  components: {
    NavBar,
  },
  methods: {
    goToCreateAcct() {
      this.$router.push({ name: "CreateAcct" });
    },
  },
};
</script>
