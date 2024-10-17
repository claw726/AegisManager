<template>
  <div class="relative w-full h-screen bg-background">
    <NavBar />

    <div v-if="!isLoggedIn" class="flex flex-col items-center mt-20">
      <div class="text-3xl font-bold text-primary" data-testid="Header">
        Login
      </div>
      <div class="text-xl font-semibold text-secondary mt-2">
        Access Your Aegis Account
      </div>

      <div class="w-3/4 max-w-4xl mt-10 bg-white shadow-lg rounded-lg p-8">
        <div class="flex flex-wrap -mx-4">
          <div class="w-full md:w-1/2 px-4 mb-4">
            <label class="block text-sm font-semibold text-gray-800 mb-2"
              >Email Address</label
            >
            <input
              type="email"
              v-model="email"
              class="w-full border border-highlight rounded-lg p-3"
            />
          </div>
          <div class="w-full md:w-1/2 px-4 mb-4">
            <label class="block text-sm font-semibold text-gray-800 mb-2"
              >Password</label
            >
            <input
              type="password"
              v-model="password"
              class="w-full border border-highlight rounded-lg p-3"
            />
          </div>
          <button
            @click="login"
            class="w-full mt-4 bg-primary text-white font-semibold py-3 rounded-lg"
          >
            Log In
          </button>
        </div>
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
    ...mapState(["isLoggedIn", "currentUser"]),
  },
  data() {
    return {
      email: "",
      password: "",
    };
  },
  watch: {
    email(newEmail) {
      this.email = newEmail.trim();
      this.email = newEmail.toLowerCase();
    },
    password(newPassword) {
      this.password = newPassword.trim();
    },
  },
  methods: {
    async login() {
      if (!this.email || !this.password) {
        alert("Please enter both email and password.");
        return;
      }
      try {
        await this.$store.dispatch("login", {
          email: this.email,
          password: this.password,
        });
        this.$router.push({ name: "Dashboard" });
      } catch (error) {
        console.error("Error during login:", error);
        alert("An Error occurred. Please try again.");
      }
    },
  },
};
</script>
