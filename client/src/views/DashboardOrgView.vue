<template>
  <div
    v-if="isLoggedIn"
    class="flex flex-col h-full w-full min-h-screen bg-background"
  >
    <NavBar />

    

    <div class="flex justify-center p-4">
      <div v-if="org" class="relative flex flex-row items-start h-screen/3 py-4">
        <div class="flex flex-col items-center mr-8">
          <img
            :src="
              org.encodedImage ||
              'https://d31kswug2i6wp2.cloudfront.net/fallback/company/medium_logo_default.png'
            "
            alt="Organization Logo"
            class="w-48 h-48 rounded-full drop-shadow-xl col-span-1"
          />
          <div class="mt-4">
            <DropdownMenu title="⚙️" :items="dropdownOpts" />
          </div>
        </div>
        <div class="flex flex-col justify-center p-4">
          <div class="text-4xl font-bold text-primary">{{ org.orgName }}</div>
          <div class="text-xl font-semibold text-secondary">
            {{ org.orgDescription }}
          </div>
          <button @click="viewUsersInOrg" class="text-2xl font-semibold text-secondary">
            View {{ org.OrgName }} Users
          </button>
          <div class="text-medium text-accent" v-if="creator.userName">
            Created by: {{ creator.userName }}
          </div>
        </div>
      </div>
      <div v-else>
        <p>Loading organization data...</p>
      </div>
    </div>

    <div class="flex p-4 justify-center">
      <p class="text-xl font-semibold text-primary mx-8">Search Projects</p>
      <input
        type="text"
        class="w-1/3 mx-2 border border-highlight rounded-lg p-2"
        placeholder="Placeholder for Search All Projects"
      />
      <button class="dashboard-button">Search</button>
    </div>
    <div class="h-1 bg-accent drop-shadow-lg rounded mx-16" />

    <div class="flex flex-col items-center">
      <button @click="goToCreateProject" class="dashboard-button mt-8">
        Create New Project
      </button>
    </div>

    <div v-if="org && projects" class="grid grid-cols-4 gap-4 m-8">
      <ProjCard
        v-for="project in projects"
        :key="project.projectID"
        :project="project"
        :projIndex="project.projectID"
      />
    </div>
  </div>
</template>

<script>
import NavBar from "@/components/NavBar.vue";
import { mapState } from "vuex";
import ProjCard from "@/components/ProjCard.vue";
import DropdownMenu from "@/components/DropdownMenu.vue";

export default {
  components: {
    NavBar,
    ProjCard,
    DropdownMenu,
  },
  computed: {
    ...mapState(["isLoggedIn", "currentUser"]),
  },
  data() {
    return {
      org: null, // Initialize org as null
      projects: null, // Initialize projects as null
      dropdownOpts: [
        {
          title: "Edit Organization Details",
          command: this.editOrg,
        },
        {
          title: "Delete This Organization",
          command: this.deleteOrg,
        },
        {
          title: "Edit Organization Users",
          command: this.editOrgUsers,
        },
      ],
      creator: {},
    };
  },
  async created() {
    await this.getOrgData(); // Ensure this is awaited
    await this.getAllOrgProjects(); // Ensure this is awaited
    await this.getCreatorData();
    
  },
  methods: {
    async getOrgData() {
      try {
        const orgID = this.$route.params.orgIndex; // Ensure you are getting the correct orgID
        this.org = await this.$store.dispatch("fetchOrganization", orgID);
        console.log("Fetched Organization:", this.org); // Log the fetched organization
      } catch (error) {
        console.error("Error fetching organization data:", error);
        alert("Failed to load organization data: " + error.message);
        this.$router.push({
          name: "viewOrgs",
          params: { orgIndex: undefined },
        });
      }
    },
    async getAllOrgProjects() {
      try {
        const orgID = this.$route.params.orgIndex; // Ensure you are getting the correct orgID
        this.projects = await this.$store.dispatch("fetchOrgProjects", orgID);
        console.log("Fetched Projects:", this.projects); // Log the fetched projects
      } catch (error) {
        console.error("Error fetching projects:", error);
        alert("Failed to load projects: " + error.message);
      }
    },
    async getCreatorData() {
      try {
        this.creator = await this.$store.dispatch("fetchUserAccountByID", this.org.orgOwnerID)
      } catch (error) {
        console.error("Error getting org owner info");
      }
    },
    goToCreateProject() {
      this.$router.push({
        name: "createProject",
        params: { orgIndex: this.index },
      });
    },
    editOrg() {
      this.$router.push({
        name: "EditOrg",
        params: { orgIndex: this.$route.params.orgIndex },
      });
    },
    deleteOrg() {
      if (confirm("Are you sure you want to delete this organization?")) {
        this.$store
          .dispatch("deleteOrganization", this.$route.params.orgIndex)
          .then(() => {
            alert("Organization deleted successfully!");
            this.$router.push({
              name: "viewOrgs",
              params: { orgIndex: undefined },
            });
          })
          .catch((err) => {
            alert("There was an error deleting the organization: ", err);
          });
      }
    },
    editOrgUsers() {
      this.$router.push({
        name: "EditOrgUsers",
        params: { orgIndex: this.$route.params.orgIndex },
      });
    },

    viewUsersInOrg() {
      this.$router.push({ name: 'viewUsersInOrg', query: {org: this.org, orgIndex: this.$route.params.orgIndex } });
    },
  },
};
</script>
