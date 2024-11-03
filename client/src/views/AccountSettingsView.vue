<template>
  <div class="relative w-full h-full min-h-screen bg-gray-50">
    <NavBar />

    <div v-if="isLoggedIn">
      <div class="container mx-auto px-4 py-8">
        <h1 class="text-4xl font-bold text-gray-800 text-center mb-8">
          <font-awesome-icon icon="fa-solid fa-gear" class="mr-2" />
          Account Settings
        </h1>

        <div v-if="currentUser" class="max-w-4xl mx-auto">
          <!-- Profile Card -->
          <div class="bg-white rounded-xl shadow-lg p-8 mb-8">
            <div class="flex flex-col md:flex-row items-center gap-8">
              <div class="relative group">
                <img
                  :src="currentUser.profilePicture || '/default-avatar.png'"
                  alt="Profile Picture"
                  class="w-48 h-48 rounded-full object-cover border-4 border-gray-200"
                />
                <button
                  class="absolute bottom-2 right-2 bg-gray-800 text-white p-2 rounded-full opacity-0 group-hover:opacity-100 transition-opacity"
                  @click="uploadNewPhoto"
                >
                  <font-awesome-icon icon="fa-solid fa-camera" />
                </button>
              </div>

              <div class="flex-1 space-y-4 text-center md:text-left">
                <div>
                  <h2 class="text-3xl font-bold text-gray-800">
                    {{ currentUser.userName }}
                  </h2>
                  <p class="text-gray-600">
                    <font-awesome-icon
                      icon="fa-solid fa-envelope"
                      class="mr-2"
                    />
                    {{ currentUser.email }}
                  </p>
                </div>
                <button
                  class="inline-flex items-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
                  @click="goToEditProfile"
                >
                  <font-awesome-icon icon="fa-solid fa-pen" class="mr-2" />
                  Edit Profile
                </button>
              </div>
            </div>
          </div>

          <!-- Settings Sections -->
          <div class="space-y-6">
            <!-- Security Settings -->
            <div class="bg-white rounded-xl shadow-lg p-6">
              <h3
                class="text-xl font-semibold text-gray-800 mb-4 flex items-center"
              >
                <font-awesome-icon
                  icon="fa-solid fa-shield-halved"
                  class="mr-2"
                />
                Security Settings
              </h3>
              <div class="space-y-4">
                <button
                  class="w-full text-left px-4 py-3 rounded-lg hover:bg-gray-50 flex items-center justify-between"
                  @click="resetPassword"
                >
                  <div>
                    <span class="font-medium">Change Password</span>
                    <p class="text-sm text-gray-600">
                      Update your password to keep your account secure
                    </p>
                  </div>
                  <font-awesome-icon
                    icon="fa-solid fa-chevron-right"
                    class="text-gray-400"
                  />
                </button>

                <div class="flex items-center justify-between px-4 py-3">
                  <div>
                    <span class="font-medium">Two-Factor Authentication</span>
                    <p class="text-sm text-gray-600">
                      Add an extra layer of security to your account
                    </p>
                  </div>
                  <label
                    class="relative inline-flex items-center cursor-pointer"
                  >
                    <input
                      v-model="settings.twoFactorEnabled"
                      type="checkbox"
                      class="sr-only peer"
                      @click="toggleTwoFactorAuth"
                    />
                    <div
                      class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-blue-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-600"
                    ></div>
                  </label>
                </div>
              </div>
            </div>

            <!-- Notification Preferences -->
            <div class="bg-white rounded-xl shadow-lg p-6">
              <h3
                class="text-xl font-semibold text-gray-800 mb-4 flex items-center"
              >
                <font-awesome-icon icon="fa-solid fa-bell" class="mr-2" />
                Notification Settings
              </h3>
              <div class="space-y-4">
                <div
                  v-for="(pref, key) in settings.notifications"
                  :key="key"
                  class="flex items-center justify-between px-4 py-3"
                >
                  <div>
                    <span class="font-medium">{{ pref.label }}</span>
                    <p class="text-sm text-gray-600">{{ pref.description }}</p>
                  </div>
                  <label
                    class="relative inline-flex items-center cursor-pointer"
                  >
                    <input
                      v-model="settings.notifications[key].enabled"
                      type="checkbox"
                      class="sr-only peer"
                    />
                    <div
                      class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-blue-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-600"
                    ></div>
                  </label>
                </div>
              </div>
            </div>

            <!-- Privacy Settings -->
            <div class="bg-white rounded-xl shadow-lg p-6">
              <h3
                class="text-xl font-semibold text-gray-800 mb-4 flex items-center"
              >
                <font-awesome-icon
                  icon="fa-solid fa-user-shield"
                  class="mr-2"
                />
                Privacy Settings
              </h3>
              <div class="space-y-4">
                <div class="px-4 py-3">
                  <span class="font-medium">Profile Visibility</span>
                  <select
                    v-model="settings.profileVisibility"
                    class="mt-2 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
                  >
                    <option value="public">Public</option>
                    <option value="friends">Friends Only</option>
                    <option value="private">Private</option>
                  </select>
                </div>
              </div>
            </div>

            <!-- Danger Zone -->
            <div
              class="bg-white rounded-xl shadow-lg p-6 border-2 border-red-200"
            >
              <h3
                class="text-xl font-semibold text-red-600 mb-4 flex items-center"
              >
                <font-awesome-icon
                  icon="fa-solid fa-triangle-exclamation"
                  class="mr-2"
                />
                Danger Zone
              </h3>
              <button
                class="w-full text-left px-4 py-3 rounded-lg hover:bg-red-50 flex items-center justify-between text-red-600"
                @click="confirmDeleteAccount"
              >
                <div>
                  <span class="font-medium">Delete Account</span>
                  <p class="text-sm text-red-500">
                    Permanently delete your account and all associated data
                  </p>
                </div>
                <font-awesome-icon icon="fa-solid fa-trash" />
              </button>
            </div>
          </div>
        </div>
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
import { library } from "@fortawesome/fontawesome-svg-core";
import {
  faGear,
  faEnvelope,
  faPen,
  faCamera,
  faShieldHalved,
  faBell,
  faUserShield,
  faChevronRight,
  faTriangleExclamation,
  faTrash,
} from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";

library.add(
  faGear,
  faEnvelope,
  faPen,
  faCamera,
  faShieldHalved,
  faBell,
  faUserShield,
  faChevronRight,
  faTriangleExclamation,
  faTrash
);

export default {
  components: {
    NavBar,
    NotificationComponent,
    FontAwesomeIcon,
  },
  data() {
    return {
      notification: {
        show: false,
        type: "info",
        message: "",
      },
      settings: {
        twoFactorEnabled: false,
        profileVisibility: "public",
        notifications: {
          emailUpdates: {
            label: "Email Updates",
            description: "Receive updates about your account via email",
            enabled: true,
          },
          pushNotifications: {
            label: "Push Notifications",
            description: "Receive notifications on your device",
            enabled: true,
          },
          marketingEmails: {
            label: "Marketing Emails",
            description: "Receive promotional offers and updates",
            enabled: false,
          },
        },
      },
    };
  },
  computed: {
    ...mapState("auth", ["isLoggedIn", "currentUser"]),
  },
  mounted() {
    this.settings.twoFactorEnabled = this.currentUser.has2fa;
  },
  methods: {
    toggleTwoFactorAuth() {
      if (this.settings.twoFactorEnabled) {
        this.$router.push({ name: "Disable2FA" });
      } else {
        this.$router.push({ name: "Enable2FA" });
      }
    },
    goToEditProfile() {
      this.$router.push({ name: "UpdateAccount" });
    },
    async resetPassword() {
      try {
        await this.$store.dispatch(
          "auth/requestPasswordReset",
          this.currentUser.email
        );
        this.showNotification(
          "success",
          "Password reset email sent to " + this.currentUser.email
        );
      } catch (error) {
        this.showNotification(
          "error",
          "Failed to send reset email: " +
            (error.response?.data || error.message)
        );
      }
    },
    uploadNewPhoto() {
      // Implement photo upload logic
      this.showNotification("info", "Photo upload feature coming soon");
    },
    confirmDeleteAccount() {
      // Implement account deletion confirmation dialog
      this.showNotification(
        "warning",
        "Account deletion requires confirmation"
      );
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