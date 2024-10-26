<template>
  <div class="relative w-full h-full min-h-screen bg-background">
    <NavBar />

    <div class="relative flex flex-col justify-center py-16">
      <div class="text-4xl font-bold text-primary text-center py-4">
        Your Organizations
      </div>
      <div class="h-1 bg-accent drop-shadow-lg"></div>
      <div class="grid grid-cols-3">
        <!-- List of a user's orgs -->

        <div class="flex-col" />
        <div class="relative flex-col col-span-1 justify-center flex w-full">
          <div
            v-if="
              !isLoading &&
              filteredOrganizations &&
              filteredOrganizations.length > 0
            "
          >
            <div
              v-for="organization in filteredOrganizations"
              :key="organization.orgID"
              class="grid my-4"
            >
              <div class="p-4 card">
                <OrgCard
                  :organization="organization"
                  :index="organization.orgID"
                />
              </div>
            </div>
          </div>
          <div v-else>
            <div class="text-lg font-bold text-gray-800 text-center">
              You are not enrolled in an organization ☹️
            </div>
          </div>
          <div class="flex justify-center">
            <button
              class="bg-primary text-white rounded-lg p-2 mt-4"
              @click="goToCreateOrg"
            >
              Create an Organization
            </button>
          </div>
        </div>
        <div class="flex-col" />
      </div>
    </div>
  </div>
</template>

<script>
import NavBar from "@/components/NavBar.vue";
import OrgCard from "@/components/OrgCard.vue";
import { mapState } from "vuex";

export default {
  components: {
    NavBar,
    OrgCard,
  },
  computed: {
    ...mapState("auth", ["isLoggedIn", "currentUser"]),
    filteredOrganizations() {
      const filteredOrganizations = (this.userOrganizations || []).filter(
        (organization) =>
          organization.users.some(
            (user) => user.userID === this.currentUser.userID,
          ),
      );
      return filteredOrganizations;
    },
  },
  data() {
    return {
      userOrganizations: null,
      isLoading: false,
    };
  },
  async mounted() {
    const delay = (ms) => new Promise((res) => setTimeout(res, ms));

    await delay(500);

    await this.fetchOrganizations();
    this.isLoading = false;
  },
  watch: {
    organizations: {
      handler(newVal) {
        this.userOrganizations = newVal;
      },
      immediate: true,
    },
  },
  methods: {
    goToCreateOrg() {
      this.$router.push({ name: "createOrg" });
    },
    async fetchOrganizations() {
      try {
        this.userOrganizations = await this.$store.dispatch(
          "organizations/fetchOrganizations",
        );
      } catch (error) {
        console.error("Error Loading Organizaitons:", error.message);
        alert("Failed to load organizations!");
      }
    },
  },
};
</script>
