<template>
  <nav class="relative w-full h-20 bg-slate-800 shadow-lg">
    <div class="container mx-auto px-4 h-full">
      <div class="flex justify-between items-center h-full">
        <!-- Brand Logo & Name -->
        <div
          @click="goToHome"
          class="flex items-center space-x-2 cursor-pointer group"
        >
          <i
            class="fa-solid fa-check text-2xl text-emerald-400 group-hover:text-blue-300 transition-colors"
          ></i>
          <span
            class="text-white text-xl font-bold tracking-wide group-hover:text-blue-300 transition-colors"
          >
            Ægis Manager
          </span>
        </div>

        <!-- Navigation Items -->
        <div class="flex items-center space-x-6">
          <!-- Logged In State -->
          <div v-if="isLoggedIn" class="flex items-center space-x-4">
            <!-- Dashboard Button -->
            <button
              @click="goToDashboard"
              class="flex items-center space-x-2 px-4 py-2 text-gray-300 hover:text-white transition-colors"
            >
              <i class="fas fa-chart-line"></i>
              <span>Dashboard</span>
            </button>

            <!-- Settings Dropdown -->
            <div class="relative" v-click-outside="closeDropdown">
              <button
                @click.stop="toggleDropdown"
                class="flex items-center space-x-2"
              >
                <img
                  :src="currentUser?.profilePicture || defaultProfilePicture"
                  :alt="currentUser?.name || 'User'"
                  class="w-10 h-10 rounded-full border-2 border-transparent hover:border-blue-400 transition-all duration-300 object-cover"
                />
                <i
                  :class="[
                    'fas fa-chevron-down text-gray-300 transition-transform duration-300',
                    { 'transform rotate-180': isDropdownOpen },
                  ]"
                ></i>
              </button>

              <!-- Dropdown Menu -->
              <div
                v-if="isDropdownOpen"
                class="absolute right-0 mt-2 w-48 rounded-md shadow-lg py-1 bg-white ring-1 ring-black ring-opacity-5 focus:outline-none z-50"
              >
                <a
                  href="#"
                  @click.prevent.stop="handleSettingsClick"
                  class="flex items-center space-x-2 px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 w-full text-left"
                >
                  <i class="fas fa-cog"></i>
                  <span>Settings</span>
                </a>
                <a
                  href="#"
                  @click.prevent.stop="handleLogout"
                  class="flex items-center space-x-2 px-4 py-2 text-sm text-red-600 hover:bg-gray-100 w-full text-left"
                >
                  <i class="fas fa-sign-out-alt"></i>
                  <span>Log Out</span>
                </a>
              </div>
            </div>
          </div>

          <!-- Logged Out State -->
          <template v-else>
            <button
              @click="goToLogin"
              class="px-4 py-2 text-white hover:text-blue-300 transition-colors"
            >
              <i class="fas fa-sign-in-alt mr-2"></i>
              Log In
            </button>
            <button
              @click="goToCreateAcct"
              class="px-4 py-2 bg-blue-500 hover:bg-blue-600 text-white rounded-md transition-colors shadow-md hover:shadow-lg"
            >
              <i class="fas fa-user-plus mr-2"></i>
              Sign Up
            </button>
          </template>
        </div>
      </div>
    </div>
  </nav>
</template>

<script>
import { ref } from "vue";
import { mapState, mapActions } from "vuex";

export default {
  name: "NavBar",

  setup() {
    const isDropdownOpen = ref(false);
    const defaultProfilePicture =
      "https://toppng.com/public/uploads/preview/instagram-default-profile-picture-11562973083brycehrmyv.png";

    const toggleDropdown = () => {
      isDropdownOpen.value = !isDropdownOpen.value;
    };

    const closeDropdown = () => {
      isDropdownOpen.value = false;
    };

    return {
      isDropdownOpen,
      defaultProfilePicture,
      toggleDropdown,
      closeDropdown,
    };
  },

  computed: {
    ...mapState("auth", ["isLoggedIn", "currentUser"]),
  },

  methods: {
    ...mapActions("auth", ["logout"]),

    goToHome() {
      this.$router.push({ name: "Home" });
    },

    goToLogin() {
      this.$router.push({ name: "Login" });
    },

    goToCreateAcct() {
      this.$router.push({ name: "CreateAcct" });
    },

    goToDashboard() {
      this.$router.push({ name: "Dashboard" });
    },

    handleSettingsClick() {
      this.closeDropdown();
      this.$router.push({ name: "AccountSettings" });
    },

    async handleLogout() {
      try {
        await this.logout();
        this.closeDropdown();
        this.$router.push({ name: "Home" });
      } catch (error) {
        console.error("Logout failed:", error);
      }
    },
  },

  directives: {
    "click-outside": {
      mounted(el, binding) {
        el.clickOutsideEvent = function (event) {
          if (!(el === event.target || el.contains(event.target))) {
            binding.value(event);
          }
        };
        document.addEventListener("click", el.clickOutsideEvent);
      },
      unmounted(el) {
        document.removeEventListener("click", el.clickOutsideEvent);
      },
    },
  },
};
</script>
