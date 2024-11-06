<template>
  <div class="relative min-h-screen bg-gradient-to-b from-gray-50 to-white">
    <NavBar />

    <!-- Hero Section -->
    <div class="relative overflow-hidden">
      <!-- Background Pattern -->
      <div class="absolute inset-0 z-0">
        <div class="absolute inset-0 bg-grid-gray-100/25 bg-[size:20px_20px]" />
        <div class="absolute inset-0 bg-gradient-to-b from-white/50" />
      </div>

      <div
        class="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20 md:py-28"
      >
        <div class="grid grid-cols-1 md:grid-cols-2 gap-12 items-center">
          <!-- Left Column: Hero Content -->
          <div class="space-y-8">
            <div class="space-y-4">
              <span
                class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-blue-100 text-blue-800"
              >
                <i class="fas fa-sparkles mr-2"></i> New Features Available
              </span>
              <h1
                class="text-4xl md:text-5xl lg:text-6xl font-extrabold text-gray-900 leading-tight"
              >
                Streamline your projects with
                <span
                  class="text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-indigo-600"
                >
                  Ægis
                </span>
              </h1>
              <p class="text-xl text-gray-600 leading-relaxed">
                A powerful, intuitive project management system that helps teams
                collaborate, track progress, and achieve goals seamlessly.
              </p>
            </div>

            <!-- CTA Buttons -->
            <div class="flex flex-col sm:flex-row gap-4">
              <button
                class="flex items-center justify-center px-6 py-3 bg-blue-600 text-white font-medium rounded-lg shadow-lg hover:bg-blue-700 transition-all duration-300 transform hover:scale-105 group"
                @click="goToCreateAcct"
              >
                <i
                  class="fas fa-rocket mr-2 group-hover:translate-x-1 transition-transform"
                ></i>
                Get Started Free
              </button>
              <button
                class="flex items-center justify-center px-6 py-3 bg-white text-gray-700 font-medium rounded-lg border border-gray-200 hover:bg-gray-50 hover:border-gray-300 transition-all duration-300"
                @click="watchDemo"
              >
                <i class="fas fa-play-circle mr-2"></i>
                Watch Demo
              </button>
            </div>

            <!-- Social Proof -->
            <div class="pt-6 border-t border-gray-200">
              <p class="text-sm text-gray-500 mb-4">Trusted by teams at</p>
              <div class="flex flex-wrap gap-6 items-center opacity-60">
                <img src="@/assets/company1.svg" alt="Company 1" class="h-8" />
                <img src="@/assets/company2.svg" alt="Company 2" class="h-8" />
                <img src="@/assets/company3.svg" alt="Company 3" class="h-8" />
              </div>
            </div>
          </div>

          <!-- Right Column: Hero Image -->
          <div class="relative">
            <div
              class="absolute inset-0 bg-gradient-to-r from-blue-500/10 to-indigo-500/10 rounded-3xl transform rotate-3"
            ></div>
            <img
              src="@/assets/Check-icon2.svg"
              alt="Project Management Dashboard"
              class="relative rounded-2xl shadow-2xl transform hover:-rotate-2 transition-transform duration-500"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- Features Section -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20">
      <div class="text-center mb-16">
        <h2 class="text-3xl font-bold text-gray-900">
          Everything you need to succeed
        </h2>
        <p class="mt-4 text-xl text-gray-600">
          Powerful features to help your team stay organized and efficient
        </p>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
        <!-- Feature Card 1 -->
        <div
          class="group p-6 bg-white rounded-xl shadow-sm hover:shadow-md transition-all duration-300"
        >
          <div
            class="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center mb-4 group-hover:scale-110 transition-transform"
          >
            <i class="fas fa-tasks text-blue-600 text-xl"></i>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">
            Task Management
          </h3>
          <p class="text-gray-600">
            Create, assign, and track tasks with ease. Set priorities and
            deadlines to keep everything on schedule.
          </p>
        </div>

        <!-- Feature Card 2 -->
        <div
          class="group p-6 bg-white rounded-xl shadow-sm hover:shadow-md transition-all duration-300"
        >
          <div
            class="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center mb-4 group-hover:scale-110 transition-transform"
          >
            <i class="fas fa-chart-line text-green-600 text-xl"></i>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">
            Progress Tracking
          </h3>
          <p class="text-gray-600">
            Monitor project progress with interactive dashboards and real-time
            updates.
          </p>
        </div>

        <!-- Feature Card 3 -->
        <div
          class="group p-6 bg-white rounded-xl shadow-sm hover:shadow-md transition-all duration-300"
        >
          <div
            class="w-12 h-12 bg-purple-100 rounded-lg flex items-center justify-center mb-4 group-hover:scale-110 transition-transform"
          >
            <i class="fas fa-users text-purple-600 text-xl"></i>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">
            Team Collaboration
          </h3>
          <p class="text-gray-600">
            Work together seamlessly with built-in communication tools and
            shared workspaces.
          </p>
        </div>
      </div>
    </div>

    <!-- Stats Section -->
    <div class="bg-blue-600 py-16">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid grid-cols-2 md:grid-cols-4 gap-8 text-center">
          <div class="space-y-2">
            <div class="text-4xl font-bold text-white">10k+</div>
            <div class="text-blue-100">Active Users</div>
          </div>
          <div class="space-y-2">
            <div class="text-4xl font-bold text-white">50k+</div>
            <div class="text-blue-100">Tasks Completed</div>
          </div>
          <div class="space-y-2">
            <div class="text-4xl font-bold text-white">99%</div>
            <div class="text-blue-100">Satisfaction Rate</div>
          </div>
          <div class="space-y-2">
            <div class="text-4xl font-bold text-white">24/7</div>
            <div class="text-blue-100">Support</div>
          </div>
        </div>
      </div>
    </div>

    <!-- CTA Section -->
    <div class="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20">
      <div
        class="bg-gradient-to-r from-blue-600 to-indigo-600 rounded-2xl p-8 md:p-12 overflow-hidden"
      >
        <div
          class="absolute right-0 top-0 transform translate-x-1/4 -translate-y-1/4"
        >
          <div class="w-64 h-64 bg-white/10 rounded-full filter blur-3xl"></div>
        </div>
        <div class="relative z-10 max-w-3xl">
          <h2 class="text-3xl font-bold text-white mb-4">
            Ready to get started?
          </h2>
          <p class="text-blue-100 mb-8">
            Join thousands of teams who are already using Ægis to streamline
            their projects.
          </p>
          <div class="flex flex-col sm:flex-row gap-4">
            <button
              class="inline-flex items-center px-6 py-3 bg-white text-blue-600 font-medium rounded-lg hover:bg-blue-50 transition-colors duration-300"
              @click="goToCreateAcct"
            >
              Start Free Trial
              <i class="fas fa-arrow-right ml-2"></i>
            </button>
            <button
              class="inline-flex items-center px-6 py-3 bg-blue-700 text-white font-medium rounded-lg hover:bg-blue-800 transition-colors duration-300"
              @click="contactSales"
            >
              Contact Sales
            </button>
          </div>
        </div>
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
    contactSales() {
      // Implement sales contact form/modal
      console.log("Opening sales contact form");
    },
    watchDemo() {
      window.location.href = "https://youtu.be/o-YBDTqX_ZU?si=XmogUL0Q6TNM3oEZ";
    },
  },
};
</script>

<style>
/* Optional: Add custom animations */
@keyframes float {
  0% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-10px);
  }
  100% {
    transform: translateY(0px);
  }
}

.animate-float {
  animation: float 3s ease-in-out infinite;
}
</style>
