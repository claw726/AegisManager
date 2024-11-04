<template>
  <div class="relative w-full min-h-screen h-full bg-background">
    <NavBar />

    <div v-if="!requires2FA" class="flex flex-col items-center mt-20">
      <div class="text-3xl font-bold text-primary" data-testid="Header">
        Login
      </div>
      <div class="text-xl font-semibold text-secondary mt-2">
        Access Your Aegis Account
      </div>

      <div class="w-3/4 max-w-4xl mt-10 bg-white shadow-lg rounded-lg p-8">
        <form class="flex flex-wrap -mx-4" @submit.prevent="login">
          <!-- Email Field -->
          <div class="w-full md:w-1/2 px-4 mb-4">
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

          <!-- Password Field -->
          <div class="w-full md:w-1/2 px-4 mb-4">
            <label class="block text-sm font-semibold text-gray-800 mb-2"
              >Password</label
            >
            <div class="relative">
              <input
                id="password"
                v-model="password"
                :type="showPassword ? 'text' : 'password'"
                autocomplete="on"
                :class="[
                  'w-full border rounded-lg p-3 pr-10 transition-all duration-200',
                  errors.password
                    ? 'border-red-500 focus:ring-red-200'
                    : 'border-highlight focus:ring-blue-200',
                ]"
                @focus="clearError('password')"
              />
              <button
                type="button"
                class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-500 hover:text-gray-700"
                @click="showPassword = !showPassword"
              >
                <i
                  :class="['fas', showPassword ? 'fa-eye-slash' : 'fa-eye']"
                ></i>
              </button>
            </div>
            <p v-if="errors.password" class="mt-1 text-sm text-red-500">
              {{ errors.password }}
            </p>
            <!-- Forgot Password Link -->
            <div class="mt-2 flex justify-end">
              <router-link
                to="/forgot-password"
                class="text-sm text-primary hover:text-primary-dark transition-colors duration-200"
              >
                <i class="fas fa-key mr-1"></i>
                Forgot Password?
              </router-link>
            </div>
          </div>

          <!-- Login Button -->
          <div class="w-full px-4">
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
                Logging in...
              </span>
              <span v-else>Log In</span>
            </button>
          </div>

          <!-- General Error Message -->
          <div v-if="error" class="w-full px-4 mt-4">
            <div
              class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative"
              role="alert"
            >
              <span class="block sm:inline">{{ error }}</span>
            </div>
          </div>
        </form>
      </div>
    </div>

    <div v-else class="flex flex-col items-center mt-20">
      <div class="text-3xl font-bold text-primary" data-testid="Header">
        Verify Two-Factor Authentication
      </div>
      <div class="text-xl font-semibold text-secondary mt-2">
        Enter the code from your authenticator app
      </div>

      <div class="w-3/4 max-w-4xl mt-10 bg-white shadow-lg rounded-lg p-8">
        <form class="flex flex-wrap -mx-4" @submit.prevent="verify2fa">
          <!-- 2FA Code Field -->
          <div class="w-full px-4 mb-4">
            <label class="block text-sm font-semibold text-gray-800 mb-2"
              >2FA Code</label
            >
            <input
              v-model="code"
              type="text"
              :class="[
                'w-full border rounded-lg p-3 transition-all duration-200',
                errors.code
                  ? 'border-red-500 focus:ring-red-200'
                  : 'border-highlight focus:ring-blue-200',
              ]"
              required
              @focus="clearError('code')"
            />
            <p v-if="errors.code" class="mt-1 text-sm text-red-500">
              {{ errors.code }}
            </p>
          </div>

          <!-- Verify Button -->
          <div class="w-full px-4">
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
                Verifying...
              </span>
              <span v-else>Verify</span>
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Centered Notification Component -->
    <div class="flex justify-center mt-4">
      <NotificationComponent
        :show="notification.show"
        :type="notification.type"
        class="max-w-md w-full shadow-lg rounded-lg overflow-hidden"
        @close="closeNotification"
      >
        <div class="p-4 break-words">
          {{ notification.message }}
        </div>
      </NotificationComponent>
    </div>
  </div>
</template>

<script>
import NavBar from "@/components/NavBar.vue";
import NotificationComponent from "@/components/NotificationComponent.vue";
import { mapState } from "vuex";
import { connect } from "@/utils/websocket.js";

export default {
  components: {
    NavBar,
    NotificationComponent,
  },
  data() {
    return {
      email: "",
      password: "",
      code: "",
      isLoading: false,
      showPassword: false,
      requires2FA: false,
      errors: {
        email: "",
        password: "",
        code: "",
      },
      notification: {
        show: false,
        type: "info",
        message: "",
      },
    };
  },
  computed: {
    ...mapState("auth", ["isLoggedIn", "error"]),
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
        code: "",
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
        const response = await this.$store.dispatch("auth/login", {
          email: this.email,
          password: this.password,
        });
        if (response.has2fa) {
          this.requires2FA = true;
        } else {
          connect();
          this.$router.push({ name: "Dashboard" });
        }
      } catch (error) {
        console.error("Error during login:", error);
        this.errors.general =
          error.response?.data?.message || "An unexpected error occurred.";
      } finally {
        this.isLoading = false;
      }
    },
    async verify2fa() {
      this.isLoading = true;
      this.notification.show = false;

      try {
        await this.$store.dispatch("auth/verify2fa", this.code);
        this.showNotification("success", "2FA verified successfully");
        connect();
        setTimeout(() => {
          this.$router.push({ name: "Dashboard" });
        }, 2000);
      } catch (error) {
        this.showNotification(
          "error",
          error.message || "An unexpected error occurred."
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

<style scoped>
.container {
  max-width: 600px;
}
/* Add any additional styling here */
.bg-primary-dark {
  @apply bg-opacity-90;
}

input:focus {
  @apply outline-none ring-2;
}
</style>
