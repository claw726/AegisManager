<template>
  <div class="min-h-screen bg-gray-50">
    <NavBar />

    <NotificationComponent
      v-model:show="notification.show"
      :type="notification.type"
      @close="closeNotification"
    >
      {{ notification.message }}
    </NotificationComponent>

    <div class="container mx-auto px-4 py-12">
      <div class="max-w-4xl mx-auto">
        <!-- Header -->
        <div class="text-center mb-12">
          <h1 class="text-4xl font-bold text-gray-900 mb-4">
            Your Organizations
          </h1>
          <div class="h-1 w-32 bg-accent mx-auto rounded-full"></div>
        </div>

        <!-- Loading State -->
        <div v-if="isLoading" class="flex justify-center items-center py-12">
          <div
            class="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"
          ></div>
        </div>

        <!-- Organizations List -->
        <div v-else>
          <TransitionGroup
            enter-active-class="transition-all duration-300 ease-out"
            enter-from-class="opacity-0 translate-y-4"
            enter-to-class="opacity-100 translate-y-0"
            leave-active-class="transition-all duration-200 ease-in"
            leave-from-class="opacity-100"
            leave-to-class="opacity-0"
            class="space-y-4"
          >
            <div
              v-for="organization in filteredOrganizations"
              :key="organization.orgID"
              class="bg-white rounded-xl shadow-sm hover:shadow-md transition-shadow duration-200"
            >
              <OrgCard
                :organization="organization"
                :index="organization.orgID"
                @click="viewOrganization(organization)"
              />
            </div>
          </TransitionGroup>

          <!-- Empty State -->
          <div
            v-if="!filteredOrganizations || filteredOrganizations.length === 0"
            class="text-center py-12 bg-white rounded-xl shadow-sm"
          >
            <svg
              class="mx-auto h-12 w-12 text-gray-400"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"
              />
            </svg>
            <h3 class="mt-4 text-lg font-medium text-gray-900">
              No Organizations Found
            </h3>
            <p class="mt-2 text-gray-500">
              Get started by creating your first organization.
            </p>
          </div>

          <!-- Create Organization Button -->
          <div class="flex justify-center mt-8">
            <button
              class="inline-flex items-center px-6 py-3 bg-primary text-white rounded-lg hover:bg-secondary transition-colors duration-200"
              @click="goToCreateOrg"
            >
              <svg
                class="w-5 h-5 mr-2"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M12 4v16m8-8H4"
                />
              </svg>
              Create an Organization
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import NavBar from "@/components/NavBar.vue";
import OrgCard from "@/components/OrgCard.vue";
import NotificationComponent from "@/components/NotificationComponent.vue";
import { mapState } from "vuex";

export default {
  components: {
    NavBar,
    OrgCard,
    NotificationComponent,
  },

  data() {
    return {
      userOrganizations: null,
      isLoading: true,
      notification: {
        show: false,
        type: "info",
        message: "",
      },
    };
  },

  computed: {
    ...mapState("auth", ["isLoggedIn", "currentUser"]),
    filteredOrganizations() {
      return (this.userOrganizations || []).filter((organization) =>
        organization.users.some(
          (user) => user.userID === this.currentUser.userID
        )
      );
    },
  },

  async mounted() {
    this.isLoading = true;
    setTimeout(async () => {
      await this.fetchOrganizations();
    }, 300);
  },

  methods: {
    showNotification(type, message, duration = 5000) {
      this.notification = {
        show: true,
        type,
        message,
      };

      if (duration) {
        setTimeout(() => {
          this.closeNotification();
        }, duration);
      }
    },

    closeNotification() {
      this.notification.show = false;
    },

    goToCreateOrg() {
      this.$router.push({ name: "createOrg" });
    },

    async fetchOrganizations() {
      try {
        this.isLoading = true;
        const organizations = await this.$store.dispatch(
          "organizations/fetchOrganizations"
        );
        this.userOrganizations = organizations;

        if (this.userOrganizations?.length > 0) {
          this.showNotification(
            "success",
            "Organizations loaded successfully",
            3000
          );
        }
        console.log("Organizations: ", organizations);
      } catch (error) {
        console.error("Error Loading Organizations:", error.message);
        this.showNotification(
          "error",
          "Failed to load organizations. Please try again later.",
          5000
        );
      } finally {
        this.isLoading = false;
      }
    },

    viewOrganization(organization) {
      this.$router.push({
        name: "OrganizationDashboard",
        params: { orgIndex: organization.orgID },
      });
    },
  },
};
</script>