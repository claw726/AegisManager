<template>
  <div class="min-h-screen bg-gray-50">
    <NavBar />

    <div v-if="isLoggedIn" class="container mx-auto px-4 py-8">
      <!-- Profile Section -->
      <div v-if="currentUser" class="bg-white rounded-xl shadow-lg p-6 mb-8">
        <div class="flex flex-col md:flex-row items-center gap-8">
          <div class="relative group">
            <img
              :src="currentUser.profilePicture || '/default-avatar.png'"
              alt="Profile Picture"
              class="w-32 h-32 md:w-40 md:h-40 rounded-full object-cover border-4 border-white shadow-lg group-hover:opacity-90 transition-all duration-300"
            />
          </div>
          
          <div class="text-center md:text-left">
            <h1 class="text-3xl font-bold text-gray-800 mb-2">
              {{ currentUser.userName }}
            </h1>
            <p class="text-lg text-gray-600 mb-4">
              Welcome to your dashboard!
            </p>
            <button
              @click="goToViewOrgs"
              class="inline-flex items-center px-6 py-3 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors"
            >
              <i class="fas fa-building mr-2"></i>
              Your Organizations
            </button>
          </div>
        </div>
      </div>

      <!-- Navigation Grid -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <button
          @click="goToTDList"
          class="flex flex-col items-center p-6 bg-white rounded-xl shadow-lg hover:shadow-xl transition-shadow group"
        >
          <div class="w-16 h-16 flex items-center justify-center bg-blue-100 rounded-full mb-4 group-hover:bg-blue-200 transition-colors">
            <i class="fas fa-tasks text-2xl text-blue-600"></i>
          </div>
          <h3 class="text-lg font-semibold text-gray-800">View Tasks</h3>
          <p class="text-sm text-gray-500 mt-2">Manage your daily tasks</p>
        </button>

        <button
          @click="goToSettings"
          class="flex flex-col items-center p-6 bg-white rounded-xl shadow-lg hover:shadow-xl transition-shadow group"
        >
          <div class="w-16 h-16 flex items-center justify-center bg-green-100 rounded-full mb-4 group-hover:bg-green-200 transition-colors">
            <i class="fas fa-cog text-2xl text-green-600"></i>
          </div>
          <h3 class="text-lg font-semibold text-gray-800">Account Settings</h3>
          <p class="text-sm text-gray-500 mt-2">Customize your preferences</p>
        </button>

        <button
          @click="goToCalendar"
          class="flex flex-col items-center p-6 bg-white rounded-xl shadow-lg hover:shadow-xl transition-shadow group"
        >
          <div class="w-16 h-16 flex items-center justify-center bg-purple-100 rounded-full mb-4 group-hover:bg-purple-200 transition-colors">
            <i class="fas fa-calendar-alt text-2xl text-purple-600"></i>
          </div>
          <h3 class="text-lg font-semibold text-gray-800">Calendar</h3>
          <p class="text-sm text-gray-500 mt-2">View your schedule</p>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import NavBar from "@/components/NavBar.vue";
import { mapState } from "vuex";

export default {
  name: 'DashboardMenu',
  components: {
    NavBar,
  },
  computed: {
    ...mapState("auth", ["isLoggedIn", "currentUser"]),
  },
  methods: {
    goToViewOrgs() {
      this.$router.push({ name: "viewOrgs" });
    },
    goToTDList() {
      this.$router.push({ name: "TDList" });
    },
    goToKanban() {
      this.$router.push({ name: "KB" });
    },
    goToViewTasks() {
      this.$router.push({ name: "toDoList" });
    },
    goToSettings() {
      this.$router.push({ name: "AccountSettings" });
    },
    goToCalendar() {
      this.$router.push({ name: "Calendar" });
    },
  },
};
</script>