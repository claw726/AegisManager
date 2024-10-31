<template>
  <div>
    <Navbar />
    <div class="container mx-auto p-4">
      <h1 class="text-2xl font-semibold mb-4">Disable Two-Factor Authentication</h1>
      <div class="bg-white shadow-md rounded-lg p-6">
        <div v-if="!isVerified">
          <p class="mb-4">Enter the code from your authenticator app to verify:</p>
          <div class="mb-4">
            <input
                type="text"
                v-model="verificationCode"
                placeholder="Enter the code"
                class="w-full p-2 border rounded"
            />
          </div>
          <button @click="verify2fa" class="w-full bg-blue-500 text-white p-2 rounded">
            Verify
          </button>
        </div>
        <div v-else>
          <p class="mb-4">Verification successful. You can now disable 2FA.</p>
          <button @click="disable2fa" class="w-full bg-red-500 text-white p-2 rounded">
            Disable 2FA
          </button>
        </div>
      </div>
      <div class="flex justify-center mt-4">
        <NotificationComponent
            :show="notification.show"
            :type="notification.type"
            @close="closeNotification"
            class="max-w-md w-full shadow-lg rounded-lg overflow-hidden"
        >
          <div class="p-4 break-words">
            {{ notification.message }}
          </div>
        </NotificationComponent>
      </div>
    </div>
  </div>
</template>

<script>
import Navbar from "@/components/NavBar.vue";
import NotificationComponent from "@/components/NotificationComponent.vue";

export default {
  components: {
    Navbar,
    NotificationComponent,
  },
  data() {
    return {
      verificationCode: "",
      isVerified: false,
      notification: {
        show: false,
        type: "info",
        message: "",
      },
    };
  },
  methods: {
    async verify2fa() {
      try {
        await this.$store.dispatch("auth/verify2fa", this.verificationCode);
        this.isVerified = true;
        this.showNotification("success", "Verification successful");
      } catch (error) {
        this.showNotification("error", "Verification failed: " + error.message);
      }
    },
    async disable2fa() {
      try {
        await this.$store.dispatch("auth/disable2fa");
        this.$store.commit("auth/set2FAStatus", false);
        this.showNotification("success", "2FA disabled successfully");
        setTimeout(() => {
          this.$router.push({ name: "AccountSettings" });
        }, 2000);
      } catch (error) {
        this.showNotification("error", "Failed to disable 2FA: " + error.message);
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
</style>