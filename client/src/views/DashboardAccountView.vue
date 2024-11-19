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
            <p class="text-lg text-gray-600 mb-4">Welcome to your dashboard!</p>
          </div>
        </div>
      </div>

      <!-- Navigation Grid -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <button
          v-for="(button, index) in buttons"
          :key="index"
          class="flex flex-col items-center p-6 bg-white rounded-xl shadow-lg hover:shadow-xl transition-shadow group"
          @click="button.action"
        >
          <div
            :class="`w-16 h-16 flex items-center justify-center bg-${button.color}-100 rounded-full mb-4 group-hover:bg-${button.color}-200 transition-colors`"
          >
            <i
              :class="`fas fa-${button.icon} text-2xl text-${button.color}-600`"
            ></i>
          </div>
          <h3 class="text-lg font-semibold text-gray-800">
            {{ button.label }}
          </h3>
          <p class="text-sm text-gray-500 mt-2">{{ button.description }}</p>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import NavBar from "@/components/NavBar.vue";
import { mapState } from "vuex";

export default {
  name: "DashboardMenu",
  components: {
    NavBar,
  },
  data() {
  return {
    buttons: [
      {
        label: "Tasks",
        description: "Manage your daily tasks",
        icon: "tasks",
        color: "blue",
        action: this.goToTDList,
      },
      {
        label: "Chats",
        description: "View your messages and conversations",
        icon: "comments",
        color: "indigo", // or "purple"
        action: this.goToChats,
      },
      {
        label: "Organizations",
        description: "Manage your organizations",
        icon: "building",
        color: "red",
        action: this.goToViewOrgs,
      },
      {
        label: "Kanban Board",
        description: "Manage your projects visually",
        icon: "columns",
        color: "yellow",
        action: this.goToKanban,
      },
      {
        label: "Calendar",
        description: "View your schedule",
        icon: "calendar-alt",
        color: "green",
        action: this.goToCalendar,
      },
      {
        label: "Archived Projects",
        description: "View your archived projects",
        icon: "archive",
        color: "gray",
        action: this.goToArchivedProjects,
      },
      {
        label: "Settings",
        description: "Customize your preferences",
        icon: "cog",
        color: "slate",
        action: this.goToSettings,
      },
    ],
  };
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
    goToArchivedProjects() {
      this.$router.push({ name: "ArchivedProjects" });
    },
    goToChats() {
      this.$router.push({ name: 'Chat' });
    },
  },
};
</script>