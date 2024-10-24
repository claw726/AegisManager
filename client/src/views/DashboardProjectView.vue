<template>
  <div
    v-if="isLoggedIn && proj"
    class="flex flex-col h-full w-full min-h-screen bg-background"
  >
    <NavBar />

    <div class="flex justify-center p-4">
      <div
        v-if="proj"
        class="relative flex flex-row items-start h-screen/3 py-4"
      >
        <div class="flex flex-col items-center mr-8">
          <img
            :src="proj.encodedImage"
            alt="Profile Picture"
            class="h-auto w-full drop-shadow-xl col-span-1 rounded-lg"
          />
          <!-- Dropdown Menu -->
          <div class="mt-4" v-if="currentUser.userID === proj.projectOwnerID">
            <DropdownMenu
              title="⚙️"
              :items="dropdownOpts"
              @command="handleCommand"
            />
          </div>
        </div>
        <div class="flex flex-col justify-center p-4">
          <div class="text-4xl font-bold text-primary">
            {{ proj.projectName }}
          </div>
          <div class="text-xl font-semibold text-secondary mt-2">
            {{ proj.projectDescription }}
          </div>
          <div class="text-medium text-accent mt-2">
            Created by: {{ creator.userName }}
          </div>
        </div>
      </div>
    </div>

    <!-- Search Bar -->
    <div class="flex p-4 justify-center">
      <p class="text-xl font-semibold text-primary mx-8">Search Tasks</p>
      <input
        type="text"
        class="w-1/3 mx-2 border border-highlight rounded-lg p-2"
        placeholder="Placeholder for Search All Projects"
      />
      <button class="dashboard-button">Search</button>
    </div>
    <div class="h-1 bg-accent drop-shadow-lg rounded mx-16" />

    <!-- Create Project Button -->
    <div class="flex flex-col items-center">
      <button @click="goToCreateTask" class="dashboard-button mt-8">
        Create New Task
      </button>
    </div>

    <!-- List of Tasks -->
    <div class="grid grid-cols-4 gap-4 m-8">
      <TaskCard
        v-for="(task, index) in tasks"
        :key="index"
        :task="task"
        :taskIndex="index"
      />
    </div>
  </div>
</template>

<script>
import NavBar from "@/components/NavBar.vue";
import { mapState } from "vuex";
import TaskCard from "@/components/TaskCard.vue";
import DropdownMenu from "@/components/DropdownMenu.vue";

export default {
  data() {
    return {
      proj: null,
      projects: [],
      dropdownOpts: [
        {
          title: "Edit Project Details ✏️",
          command: this.editProject,
        },
        {
          title: "Delete This Project 🗑️",
          command: this.deleteProject,
        },
        {
          title: "Edit Project Members 🤵",
          command: this.editProjUsers,
        },
      ],
      tasks: [
        {
          TaskName: "Gather Materials for Tempest Hull",
          TaskDescription:
            "Collect 1000 units of Mexallon and 500 units of Pyroxeres",
          AssignerID: 1,
          AssignedUsers: ["Capsuleer1", "Capsuleer2"],
          TaskPriority: "High",
          DueDate: new Date("2024-10-01T14:30:00.000Z"),
          TaskFiles: [
            "Mexallon_Sourcing_Report.pdf",
            "Pyroxeres_Sourcing_Report.pdf",
          ],
          IsComplete: false,
        },
        {
          TaskName: "Assemble Tempest Frame",
          TaskDescription: "Construct the frame of the Tempest Battleship",
          AssignerID: 2,
          AssignedUsers: ["Capsuleer3", "Capsuleer4"],
          TaskPriority: "Medium",
          DueDate: new Date("2024-10-05T10:00:00.000Z"),
          TaskFiles: [
            "Tempest_Frame_Blueprint.bpt",
            "Assembly_Instructions.pdf",
          ],
          IsComplete: true,
        },
        {
          TaskName: "Install Propulsion System",
          TaskDescription:
            "Install the propulsion system for the Tempest Battleship",
          AssignerID: 3,
          AssignedUsers: ["Capsuleer5", "Capsuleer6"],
          TaskPriority: "Low",
          DueDate: new Date("2024-10-10T12:00:00.000Z"),
          TaskFiles: [
            "Propulsion_System_Blueprint.bpt",
            "Installation_Guide.pdf",
          ],
          IsComplete: false,
        },
        {
          TaskName: "Fit Turrets and Missiles",
          TaskDescription:
            "Equip the Tempest Battleship with turrets and missiles",
          AssignerID: 4,
          AssignedUsers: ["Capsuleer7", "Capsuleer8"],
          TaskPriority: "High",
          DueDate: new Date("2024-10-15T14:00:00.000Z"),
          TaskFiles: ["Turret_Fitting_Guide.pdf", "Missile_Fitting_Guide.pdf"],
          IsComplete: false,
        },
        {
          TaskName: "Finalize Ship Configuration",
          TaskDescription:
            "Finalize the configuration of the Tempest Battleship",
          AssignerID: 5,
          AssignedUsers: ["Capsuleer9", "Capsuleer10"],
          TaskPriority: "Medium",
          DueDate: new Date("2024-10-20T10:00:00.000Z"),
          TaskFiles: ["Ship_Configuration_Guide.pdf", "Final_Checklist.pdf"],
          IsComplete: false,
        },
        // Add more tasks as needed
      ],
      creator: {},
      isLoaded: false,
    };
  },
  components: {
    NavBar,
    TaskCard,
    DropdownMenu,
  },
  async created() {
    await this.getProjData();
    await this.getCreatorData();
  },
  computed: {
    ...mapState('auth', ["isLoggedIn", "currentUser"]),
  },
  // mounted() {
  //   this.tasks = this.proj.tasks || [];
  // },
  methods: {
    async getProjData() {
      try {
        this.proj = await this.$store.dispatch(
          "projects/fetchProject",
          this.$route.params.projIndex,
        );
      } catch (err) {
        alert("There was an error fetching the organization data");
        this.$router.push({ name: "OrganizationDashboard" });
      }
    },
    async getCreatorData() {
      try {
        this.creator = await this.$store.dispatch(
          "users/fetchUserAccountByID",
          this.proj.projectOwnerID,
        );
      } catch (error) {
        console.error("Error getting project owner info");
      }
    },
    goToCreateTask() {
      // this.$router.push({ name: 'createTask', params: { orgIndex: this.index }});
      alert("Bilsha, can you implement this?");
    },
    handleCommand(command) {
      if (command === "edit") {
        this.editProject();
      } else if (command === "delete") {
        this.deleteProject();
      }
    },
    editProject() {
      // Confirm the current user is the project creator
      if (this.proj.projectOwnerID !== this.currentUser.userID) {
        alert("You are not authorized to modify this project.");
        return;
      }
      this.$router.push({
        name: "EditProject",
        params: {
          orgIndex: this.$route.params.orgIndex,
          projIndex: this.$route.params.projIndex,
        },
      });
    },
    deleteProject() {
      // Confirm the current user is the project creator
      if (this.proj.projectOwnerID !== this.currentUser.userID) {
        alert("You are not authorized to delete this project.");
        return;
      }

      if (confirm("Are you sure you want to delete this project?")) {
        this.$store
          .dispatch("projects/deleteProject", {
            projectID: this.$route.params.projIndex,
          })
          .then(() => {
            alert("Project deleted successfully!");
            this.$router.push({ name: "OrganizationDashboard" });
          })
          .catch((err) => {
            alert("Failed to delete project");
            console.error(err);
          });
      }
    },
    editProjUsers() {
      this.$router.push({
        name: "EditProjUsers",
        params: {
          orgIndex: this.$route.params.orgIndex,
          projIndex: this.$route.params.projIndex,
        },
      });
    },
  },
};
</script>
