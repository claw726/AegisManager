<template>
    <div v-if="isLoggedIn" class="flex flex-col h-full w-full min-h-screen bg-background">
      <NavBar />
      
      <div class="flex justify-end">
          <button class="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded m-4 justify-center h-12">⚙️</button>
      </div>

      <div class="flex justify-center justify-items-center p-4">
        <div v-if="proj">
          <div class="relative flex h-screen/3 py-4">
          <img :src="proj.ProjImg" alt="Profile Picture" class="h-48 con drop-shadow-xl col-span-1" />
          <div class="ml-8 flex flex-col justify-center">
            <div class="text-4xl font-bold text-primary">{{ proj.ProjName}}</div>
            <div class="text-2xl font-semibold text-secondary">{{ proj.ProjDescription }}</div>
            <div class="text-medium text-accent">Created by: {{ proj.ProjCreator }}</div>
            </div>
          </div>
        </div>
      </div>

      
      
      <!-- Search Bar -->
      <div class="flex p-4 justify-center">
        <p class="text-xl font-semibold text-primary mx-8">Search Tasks</p>
        <input type="text" class="w-1/3 mx-2 border border-highlight rounded-lg p-2 " placeholder="Placeholder for Search All Projects" />
        <button class="dashboard-button">Search</button>
      </div>
      <div class="h-1 bg-accent drop-shadow-lg rounded mx-16" />

      <!-- Create Project Button -->
      <div class="flex flex-col items-center">
        <button @click="goToCreateTask" class="dashboard-button mt-8">Create New Task</button>
      </div>

      <!-- List of Tasks -->
      <div class="grid grid-cols-4 gap-4 m-8">
        <TaskCard v-for="(task, index) in tasks" :key="index" :task="task" :taskIndex="index" />
      </div>

      
    </div>
</template>
  
  <script>
  import NavBar from '@/components/NavBar.vue';
  import { mapState } from 'vuex';
  import TaskCard from '../components/TaskCard.vue';
  
  export default {
    data() {
      return {
        proj: null,
        projects: [],
        tasks: [
        {
          TaskName: "Gather Materials for Tempest Hull",
          TaskDescription: "Collect 1000 units of Mexallon and 500 units of Pyroxeres",
          AssignerID: 1,
          AssignedUsers: ["Capsuleer1", "Capsuleer2"],
          TaskPriority: "High",
          DueDate: new Date("2024-10-01T14:30:00.000Z"),
          TaskFiles: ["Mexallon_Sourcing_Report.pdf", "Pyroxeres_Sourcing_Report.pdf"],
          IsComplete: false
        },
        {
          TaskName: "Assemble Tempest Frame",
          TaskDescription: "Construct the frame of the Tempest Battleship",
          AssignerID: 2,
          AssignedUsers: ["Capsuleer3", "Capsuleer4"],
          TaskPriority: "Medium",
          DueDate: new Date("2024-10-05T10:00:00.000Z"),
          TaskFiles: ["Tempest_Frame_Blueprint.bpt", "Assembly_Instructions.pdf"],
          IsComplete: true
        },
        {
          TaskName: "Install Propulsion System",
          TaskDescription: "Install the propulsion system for the Tempest Battleship",
          AssignerID: 3,
          AssignedUsers: ["Capsuleer5", "Capsuleer6"],
          TaskPriority: "Low",
          DueDate: new Date("2024-10-10T12:00:00.000Z"),
          TaskFiles: ["Propulsion_System_Blueprint.bpt", "Installation_Guide.pdf"],
          IsComplete: false
        },
        {
          TaskName: "Fit Turrets and Missiles",
          TaskDescription: "Equip the Tempest Battleship with turrets and missiles",
          AssignerID: 4,
          AssignedUsers: ["Capsuleer7", "Capsuleer8"],
          TaskPriority: "High",
          DueDate: new Date("2024-10-15T14:00:00.000Z"),
          TaskFiles: ["Turret_Fitting_Guide.pdf", "Missile_Fitting_Guide.pdf"],
          IsComplete: false
        },
        {
          TaskName: "Finalize Ship Configuration",
          TaskDescription: "Finalize the configuration of the Tempest Battleship",
          AssignerID: 5,
          AssignedUsers: ["Capsuleer9", "Capsuleer10"],
          TaskPriority: "Medium",
          DueDate: new Date("2024-10-20T10:00:00.000Z"),
          TaskFiles: ["Ship_Configuration_Guide.pdf", "Final_Checklist.pdf"],
          IsComplete: false
        },
        // Add more tasks as needed
      ]
      };
    },
    components: {
      NavBar,
      TaskCard,
    },
    created() {
      this.getProjData();
    },
    computed: {
      ...mapState(['isLoggedIn', 'organizations']),
    },
    // mounted() {
    //   this.tasks = this.proj.tasks || [];
    // },
    methods: {
      getProjData() {
        this.proj = this.organizations[this.$route.params.orgIndex].projects[this.$route.params.projIndex];
        if (!this.proj) {
          alert("There was an error fetching the organization data");
          this.$router.push({name: 'OrganizationDashboard'});
        }
      },
      goToCreateTask() {
            // this.$router.push({ name: 'createTask', params: { orgIndex: this.index }});
            alert("Bilsha, can you implement this?");
        },
    },
  };
  </script>   