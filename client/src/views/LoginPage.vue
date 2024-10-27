<template>
  <div class="relative w-full min-h-screen h-full bg-background">
    <NavBar />

    <div v-if="!isLoggedIn" class="flex flex-col items-center mt-20">
      <div class="text-3xl font-bold text-primary" data-testid="Header">
        Login
      </div>
      <div class="text-xl font-semibold text-secondary mt-2">
        Access Your Aegis Account
      </div>

      <div class="w-3/4 max-w-4xl mt-10 bg-white shadow-lg rounded-lg p-8">
        <form @submit.prevent="login" class="flex flex-wrap -mx-4">
          <!-- Email Field -->
          <div class="w-full md:w-1/2 px-4 mb-4">
            <label class="block text-sm font-semibold text-gray-800 mb-2">Email Address</label>
            <input
              type="email"
              id="email"
              autocomplete="on"
              v-model="email"
              :class="[
                'w-full border rounded-lg p-3 transition-all duration-200',
                errors.email ? 'border-red-500 focus:ring-red-200' : 'border-highlight focus:ring-blue-200'
              ]"
              @focus="clearError('email')"
            />
            <p v-if="errors.email" class="mt-1 text-sm text-red-500">{{ errors.email }}</p>
          </div>

          <!-- Password Field -->
          <div class="w-full md:w-1/2 px-4 mb-4">
            <label class="block text-sm font-semibold text-gray-800 mb-2">Password</label>
            <div class="relative">
              <input
                :type="showPassword ? 'text' : 'password'"
                id="password"
                autocomplete="on"
                v-model="password"
                :class="[
                  'w-full border rounded-lg p-3 pr-10 transition-all duration-200',
                  errors.password ? 'border-red-500 focus:ring-red-200' : 'border-highlight focus:ring-blue-200'
                ]"
                @focus="clearError('password')"
              />
              <button
                type="button"
                @click="showPassword = !showPassword"
                class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-500 hover:text-gray-700"
              >
                <i :class="['fas', showPassword ? 'fa-eye-slash' : 'fa-eye']"></i>
              </button>
            </div>
            <p v-if="errors.password" class="mt-1 text-sm text-red-500">{{ errors.password }}</p>
          </div>

          <!-- Login Button -->
          <div class="w-full px-4">
            <button
              type="submit"
              :disabled="isLoading"
              :class="[
                'w-full mt-4 font-semibold py-3 rounded-lg transition-all duration-200',
                isLoading ? 'bg-gray-400 cursor-not-allowed' : 'bg-primary hover:bg-primary-dark',
                'text-white'
              ]"
            >
              <span v-if="isLoading" class="flex items-center justify-center">
                <i class="fas fa-circle-notch fa-spin mr-2"></i>
                Logging in...
              </span>
              <span v-else>Log In</span>
            </button>
          </div>

          <!-- General Error Message -->
          <div v-if="error" class="w-full px-4 mt-4">
            <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative" role="alert">
              <span class="block sm:inline">{{ error }}</span>
            </div>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import NavBar from "@/components/NavBar.vue";
import { mapState } from "vuex";

export default {
  components: {
    NavBar,
  },
  computed: {
    ...mapState('auth', ["isLoggedIn", "error"]), // Assuming error is in your Vuex state
  },
  data() {
    return {
      email: "",
      password: "",
      isLoading: false,
      showPassword: false,
      errors: {
        email: "",
        password: "",
      },
    };
  },
  watch: {
    email(newEmail) {
      this.email = newEmail.trim().toLowerCase();
    },
    password(newPassword) {
      this.password = newPassword.trim();
    },
  },
  mounted() {
    // Reset error state when the component is created
    this.clearErrors();
  },
  methods: {
    clearError(field) {
      this.errors[field] = "";
    },
    clearErrors() {
      this.errors = {
        email: "",
        password: "",
      };
    },
    validateForm() {
      let isValid = true;
      this.errors = {
        email: "",
        password: "",
      };

      if (!this.email) {
        this.errors.email = "Email is required";
        isValid = false;
      } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.email)) {
        this.errors.email = "Please enter a valid email address";
        isValid = false;
      }

      if (!this.password) {
        this.errors.password = "Password is required";
        isValid = false;
      }

      return isValid;
    },
    async login() {
      if (!this.validateForm()) return;

      this.isLoading = true;

      try {
        await this.$store.dispatch("auth/login", {
          email: this.email,
          password: this.password,
        });
        this.$router.push({ name: "Dashboard" });
      } catch (error) {
        console.error("Error during login:", error);
        // Set the error message from the Vuex state
        this.errors.general = error.response?.data?.message || "An unexpected error occurred.";
      } finally {
        this.isLoading = false;
      }
    },
  },
};
</script>

<style scoped>
/* Add any additional styling here */
.bg-primary-dark {
  @apply bg-opacity-90;
}

input:focus {
  @apply outline-none ring-2;
}
</style>