<template>
  <div class="relative w-full h-full min-h-screen bg-background">
    <NavBar />

    <div v-if="isLoggedIn">
      <div class="text-4xl font-semibold text-secondary text-center mt-8">
        Account Settings
      </div>

      <!-- Profile Section -->
      <div v-if="currentUser" class="relative flex flex-col items-center py-12">
        <div class="text-2xl font-semibold text-secondary mb-8">
          Welcome to your account settings!
        </div>
        <div class="h-1 bg-accent drop-shadow-lg w-screen mb-8"></div>
        <div class="flex flex-row items-center">
          <img
            :src="
              currentUser.profilePicture ||
              'https://toppng.com/public/uploads/preview/instagram-default-profile-picture-11562973083brycehrmyv.png'
            "
            alt="Profile Picture"
            class="w-48 h-48 rounded-full drop-shadow-xl col-span-1"
          />
          <div class="ml-8 flex flex-col justify-center">
            <div class="text-4xl font-bold text-primary text-center">
              Name: {{ currentUser.userName }}
            </div>
            <div class="text-2xl font-semibold text-secondary text-center">
              Email: {{ currentUser.email }}
            </div>
            <button class="dashboard-button mt-4" @click="goToEditProfile">Edit Profile Details</button>
          </div>
        </div>
      </div>
      <!-- Button Row -->
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
  methods: {
    goToEditProfile() {
      this.$router.push({ name: 'UpdateAccount' });
    },
  },
};
</script>
