<template>
  <div class="relative w-full min-h-screen h-full bg-background">
    <!-- Navbar -->
    <NavBar />

    <!-- Main Content and Decorative Image -->
    <div class="flex justify-between items-center h-full px-28">
      <!-- Left Column: Main Content and Call to Action -->
      <div class="flex flex-col space-y-8 w-1/2">
        <div>
          <h1 class="text-5xl font-extrabold text-primary">
            Streamline your projects with Ægis
          </h1>
          <p class="mt-4 text-xl font-medium text-gray-800">
            A simple, intuitive project management system for teams and
            individuals.
          </p>
        </div>
        <div class="flex space-x-4">
          <button
            @click="goToCreateAcct"
            class="bg-zinc-800 text-white font-semibold py-3 px-6 rounded"
          >
            Get Started Now 🚀
          </button>
        </div>
      </div>

      <!-- Right Column: Decorative Image -->
      <div class="w-1/2">
        <img src="@/assets/Check-icon.png" alt="Check Icon" class="" />
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
