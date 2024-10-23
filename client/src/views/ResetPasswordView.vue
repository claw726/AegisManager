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
                  @update-password="updatePassword"
                  :Title="'New Password'"
                />
              </div>
            </div>
          </div>
          <button
            @click="resetPassword"
            class="w-full mt-4 bg-primary text-white font-semibold py-3 rounded-lg"
          >
            Update Password
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import NavBar from "@/components/NavBar.vue";
import { mapState } from "vuex";
import PasswordInput from "@/components/PasswordCreator.vue";

export default {
  components: {
    NavBar,
    PasswordInput,
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
  methods: {
    resetPassword() {
      alert("Not implemented yet");
    },
    updatePassword(password) {
      this.password = password;
    },
  },
};
</script>
