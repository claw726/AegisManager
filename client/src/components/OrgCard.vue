<template>
  <div
    class="group relative p-6 hover:bg-gray-50 transition-colors duration-200 rounded-xl cursor-pointer"
    @click="viewOrganization"
  >
    <!-- Main Content -->
    <div class="flex items-start space-x-4">
      <!-- Organization Image -->
      <div class="relative flex-shrink-0">
        <img
          :src="organization.encodedImage"
          :alt="organization.orgName"
          class="w-16 h-16 rounded-lg object-cover shadow-md group-hover:shadow-lg transition-shadow duration-200"
          @error="handleImageError"
        />
        <div
          v-if="!imageLoaded"
          class="absolute inset-0 bg-gray-200 rounded-lg flex items-center justify-center"
        >
          <svg
            class="w-8 h-8 text-gray-400"
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
        </div>
      </div>

      <!-- Organization Info -->
      <div class="flex-1 min-w-0">
        <div class="flex items-center justify-between">
          <h3
            class="text-lg font-semibold text-gray-900 truncate group-hover:text-primary transition-colors duration-200"
          >
            {{ organization.orgName }}
          </h3>
        </div>

        <!-- Organization Details -->
        <div class="mt-1 space-y-1">
          <div class="flex items-center text-sm text-gray-500">
            <svg
              class="flex-shrink-0 mr-1.5 h-4 w-4 text-gray-400"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
              />
            </svg>
            <span class="truncate">Created by: {{ creator.userName }}</span>
          </div>

          <div class="flex items-center text-sm text-gray-500">
            <svg
              class="flex-shrink-0 mr-1.5 h-4 w-4 text-gray-400"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"
              />
            </svg>
            <span>{{ organization.users?.length || 0 }} members</span>
          </div>

          <div
            v-if="organization.description"
            class="flex items-center text-sm text-gray-500"
          >
            <svg
              class="flex-shrink-0 mr-1.5 h-4 w-4 text-gray-400"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
              />
            </svg>
            <span class="truncate">{{
              organization.description || "No description available"
            }}</span>
          </div>
        </div>
      </div>

      <!-- Arrow Icon -->
      <div class="flex-shrink-0 self-center">
        <svg
          class="h-5 w-5 text-gray-400 group-hover:text-secondary transition-colors duration-200"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M9 5l7 7-7 7"
          />
        </svg>
      </div>
    </div>

    <!-- Hover Actions -->
    <div
      class="absolute inset-x-0 bottom-0 h-0.5 bg-secondary transform origin-left scale-x-0 group-hover:scale-x-100 transition-transform duration-200"
    ></div>
  </div>
</template>

<script>
export default {
  props: {
    organization: {
      type: Object,
      required: true,
    },
    index: {
      type: Number,
      required: true,
    },
  },

  data() {
    return {
      imageLoaded: true,
      creator: "",
    };
  },

  async mounted() {
    await this.getCreatorData();
  },

  methods: {
    viewOrganization() {
      this.$router.push({
        name: "OrganizationDashboard",
        params: { orgIndex: this.index },
      });
    },

    handleImageError() {
      this.imageLoaded = false;
    },
    async getCreatorData() {
      try {
        this.creator = await this.$store.dispatch(
          "users/fetchUserAccountByID",
          this.organization.orgOwnerID,
        );
      } catch (error) {
        this.creator = "Unknown";
      }
    },
  },
};
</script>
