<!-- ForgotPassword.vue -->
<template>
  <div class="relative w-full min-h-screen h-full bg-background">
    <NavBar />

    <div class="flex flex-col items-center mt-20">
      <div class="text-3xl font-bold text-primary" data-testid="Header">
        Reset Password
      </div>
      <div class="text-xl font-semibold text-secondary mt-2">
        Enter your email to reset your password
      </div>

      <div class="w-3/4 max-w-4xl mt-10 bg-white shadow-lg rounded-lg p-8">
        <form class="flex flex-col" @submit.prevent="resetPassword">
          <!-- Email Field -->
          <div class="mb-4">
            <label class="block text-sm font-semibold text-gray-800 mb-2"
              >Email Address</label
            >
            <input
              id="email"
              v-model="email"
              type="email"
              autocomplete="on"
              :class="[
                'w-full border rounded-lg p-3 transition-all duration-200',
                errors.email
                  ? 'border-red-500 focus:ring-red-200'
                  : 'border-highlight focus:ring-blue-200',
              ]"
              @focus="clearError('email')"
            />
            <p v-if="errors.email" class="mt-1 text-sm text-red-500">
              {{ errors.email }}
            </p>
          </div>

          <!-- Submit Button -->
          <button
            type="submit"
            :disabled="isLoading"
            :class="[
              'w-full mt-4 font-semibold py-3 rounded-lg transition-all duration-200',
              isLoading
                ? 'bg-gray-400 cursor-not-allowed'
                : 'bg-primary hover:bg-primary-dark',
              'text-white',
            ]"
          >
            <span v-if="isLoading" class="flex items-center justify-center">
              <i class="fas fa-circle-notch fa-spin mr-2"></i>
              Sending reset link...
            </span>
            <span v-else>Send Reset Link</span>
          </button>

          <!-- Success Message -->
          <div v-if="successMessage" class="mt-4">
            <div
              class="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded relative"
              role="alert"
            >
              <span class="block sm:inline">{{ successMessage }}</span>
            </div>
          </div>

          <!-- Error Message -->
          <div v-if="error" class="mt-4">
            <div
              class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative"
              role="alert"
            >
              <span class="block sm:inline">{{ error }}</span>
            </div>
          </div>

          <!-- Back to Login Link -->
          <div class="text-center mt-6">
            <router-link
              to="/login"
              class="text-primary hover:text-primary-dark transition-colors duration-200"
            >
              <i class="fas fa-arrow-left mr-2"></i>
              Back to Login
            </router-link>
          </div>
        </form>
      </div>
    </div>
    <!-- Notification Component - Repositioned -->
    <div class="fixed bottom-4 right-4 max-w-md z-50">
      <!-- Added max-width and z-index -->
      <NotificationComponent
        :show="notification.show"
        :type="notification.type"
        class="w-full shadow-lg rounded-lg overflow-hidden"
        @close="closeNotification"
      >
        <div class="p-4 break-words">
          <!-- Added padding and word breaking -->
          {{ notification.message }}
        </div>
      </NotificationComponent>
    </div>
  </div>
</template>

<script>
import NavBar from "@/components/NavBar.vue";
import { mapState } from "vuex";
import NotificationComponent from "@/components/NotificationComponent.vue";

export default {
  components: {
    NavBar,
    NotificationComponent,
  },
  data() {
    return {
      email: "",
      isLoading: false,
      successMessage: "",
      errors: {
        email: "",
      },
      notification: {
        show: false,
        type: "info",
        message: "",
      },
    };
  },
  computed: {
    ...mapState("auth", ["error"]),
  },
  watch: {
    email(newEmail) {
      this.email = newEmail.trim().toLowerCase();
    },
  },
  methods: {
    clearError(field) {
      this.errors[field] = "";
    },
    validateForm() {
      let isValid = true;
      this.errors.email = "";

      if (!this.email) {
        this.errors.email = "Email is required";
        isValid = false;
      } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.email)) {
        this.errors.email = "Please enter a valid email address";
        isValid = false;
      }

      return isValid;
    },
    async resetPassword() {
      this.isLoading = true;
      this.errors.email = "";
      try {
        if (!this.email) {
          this.showNotification("error", "Please enter an email.");
          this.errors.email = "Please enter an email.";
          return;
        }

        await this.$store.dispatch("auth/requestPasswordReset", this.email);
        this.showNotification(
          "success",
          "Password reset email sent to " + this.email
        );
      } catch (error) {
        this.showNotification(
          "error",
          "Failed to send reset email: " +
            (error.response?.data || error.message)
        );
      } finally {
        this.isLoading = false;
      }
    },
    showNotification(type, message) {
      this.notification = {
        show: true,
        type,
        message,
      };

      if (type === "success") {
        setTimeout(this.closeNotification, 5000);
      }
    },

    closeNotification() {
      this.notification.show = false;
    },
  },
};
</script>