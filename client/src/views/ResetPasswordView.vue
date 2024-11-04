<template>
  <div class="relative w-full min-h-screen h-full bg-background">
    <NavBar />

    <div class="flex flex-col items-center mt-20">
      <div class="text-3xl font-bold text-primary" data-testid="Header">
        Reset Password
      </div>

      <div class="w-3/4 max-w-4xl mt-10 bg-white shadow-lg rounded-lg p-8">
        <div class="flex flex-wrap -mx-4">
          <div class="w-full px-4 mb-4">
            <!-- Password Input -->
            <div class="w-full flex justify-center px-4 mb-4">
              <div class="w-full">
                <PasswordInput
                  :Title="'New Password'"
                  @update-password="updatePassword"
                />
              </div>
            </div>
          </div>
          <button
            class="w-full mt-4 bg-primary text-white font-semibold py-3 rounded-lg"
            @click="resetPassword"
          >
            Update Password
          </button>
        </div>
      </div>
      <NotificationComponent
        class="flex"
        :show="notification.show"
        :type="notification.type"
        @close="closeNotification"
      >
        {{ notification.message }}
      </NotificationComponent>
    </div>
  </div>
</template>

<script>
import NavBar from "@/components/NavBar.vue";
import { mapState } from "vuex";
import PasswordInput from "@/components/PasswordCreator.vue";
import NotificationComponent from "@/components/NotificationComponent.vue";

export default {
  components: {
    NavBar,
    PasswordInput,
    NotificationComponent,
  },
  data() {
    return {
      email: "",
      password: "",
      notification: {
        show: false,
        type: "info",
        message: "",
      },
    };
  },
  computed: {
    ...mapState("auth", ["isLoggedIn", "currentUser"]),
    token() {
      return this.$route.query.token;
    },
  },
  methods: {
    async resetPassword() {
      try {
        await this.$store.dispatch("auth/resetPassword", {
          newPassword: this.password,
          token: this.token,
        });
        this.showNotification("success", "Password successfully updated");
      } catch (error) {
        this.showNotification(
          "error",
          "Error resetting password: " + error.message
        );
      }
    },
    updatePassword(password) {
      this.password = password;
    },
    showNotification(type, message) {
      this.notification = {
        show: true,
        type,
        message,
      };

      if (type == "success") {
        setTimeout(this.closeNotification, 5000);
      }
    },
    closeNotification() {
      this.notification.show = false;
    },
  },
};
</script>