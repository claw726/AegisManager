<template>
  <div v-if="isLoggedIn" class="relative min-h-screen bg-gray-50">
    <NavBar />

    <!-- Main Container -->
    <div class="flex min-h-[calc(100vh-64px)]">
      <!-- SideBar -->
      <TaskSidebarComponent :tasks="tasks" />

      <!-- Calendar Container -->
      <div class="flex-1 p-8">
        <div class="bg-white rounded-xl shadow-sm border border-gray-200">
          <!-- Calendar Header -->
          <div class="border-b border-gray-200 p-4">
            <div class="flex items-center justify-between">
              <h1 class="text-2xl font-bold text-gray-900">Calendar</h1>
            </div>
          </div>

          <!-- Calendar Component -->
          <div class="p-6">
            <Fullcalendar
              :options="calendarOptions"
              class="fc-theme-standard"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- Task Detail Popup -->
    <TaskDetailPopup
      v-if="selectedTask"
      :task="selectedTask"
      :visible="popupVisible"
      :position="popupPosition"
      class="z-50"
      @close="popupVisible = false"
    />
  </div>
</template>

<script>
import NavBar from "@/components/NavBar.vue";
import { mapActions, mapState } from "vuex";
import Fullcalendar from "@fullcalendar/vue3";
import dayGridPlugin from "@fullcalendar/daygrid";
import interactionPlugin from "@fullcalendar/interaction";
import listPlugin from "@fullcalendar/list";
import TaskDetailPopup from "@/components/TaskDetailPopup.vue";
import TaskSidebarComponent from "@/components/TaskSidebarComponent.vue";

export default {
  components: {
    NavBar,
    Fullcalendar,
    TaskDetailPopup,
    TaskSidebarComponent,
  },
  data() {
    return {
      calendarOptions: {
        plugins: [dayGridPlugin, interactionPlugin, listPlugin],
        initialView: "dayGridMonth",
        headerToolbar: {
          left: "prev,next today",
          center: "title",
          right: "dayGridMonth,dayGridWeek,listWeek",
        },
        editable: false,
        selectable: true,
        events: [],
        eventClick: this.handleEventClick,
        // Styling options
        height: "auto",
        // Custom calendar styling
        dayMaxEvents: true,
        eventColor: "#0D203B",
        eventTextColor: "#FFFFFF",
        eventBorderColor: "#2563EB",
        eventClassNames: ["rounded-md", "px-2", "py-1", "text-sm"],
      },
      selectedTask: null,
      popupVisible: false,
      popupPosition: { top: 0, left: 0 },
    };
  },
  computed: {
    ...mapState("auth", ["isLoggedIn", "currentUser"]),
    ...mapState("tasks", ["tasks"]),
    calendarEvents() {
      return this.tasks.map((task) => ({
        id: task.taskID,
        title: task.taskName,
        start: task.dueDate,
        allDay: true,
        extendedProps: {
          description: task.taskDescription,
          isComplete: task.isComplete,
        },
        className: task.isComplete ? "bg-green-500" : "",
      }));
    },
  },
  watch: {
    tasks: {
      handler() {
        this.calendarOptions.events = this.calendarEvents;
      },
      deep: true,
    },
  },
  async mounted() {
    await this.loadTasks();
    document.addEventListener("click", this.handleClickOutside);
  },
  beforeUnmount() {
    document.removeEventListener("click", this.handleClickOutside);
  },
  methods: {
    ...mapActions("tasks", ["fetchTasks"]),

    async loadTasks() {
      try {
        await this.fetchTasks();
        this.calendarOptions.events = this.calendarEvents;
      } catch (error) {
        console.error(error);
      }
    },

    handleEventClick(info) {
      const rect = info.el.getBoundingClientRect();
      const scrollTop =
        window.pageYOffset || document.documentElement.scrollTop;

      this.selectedTask = {
        ...info.event.extendedProps,
        title: info.event.title,
        dueDate: info.event.start,
      };

      this.popupPosition = {
        top: rect.top + scrollTop,
        left: rect.left + rect.width / 2,
      };

      this.popupVisible = true;
    },

    closePopup() {
      this.popupVisible = false;
      this.selectedTask = null;
    },

    handleClickOutside(event) {
      if (this.popupVisible) {
        const popup = document.querySelector(".popup");
        if (popup && !popup.contains(event.target)) {
          this.closePopup();
        }
      }
    },
  },
};
</script>

<style>
/* Add custom styles for FullCalendar */
.fc .fc-toolbar-title {
  @apply text-xl font-bold text-gray-900;
}

.fc .fc-button {
  @apply px-4 py-2 text-sm font-medium rounded-lg transition-colors duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500;
}

.fc .fc-button-primary {
  @apply bg-secondary text-white border-secondary hover:bg-blue-700 hover:border-blue-700;
}

.fc .fc-button-primary:not(:disabled).fc-button-active,
.fc .fc-button-primary:not(:disabled):active {
  @apply bg-primary border-primary;
}

.fc .fc-daygrid-day-number {
  @apply text-sm text-gray-700;
}

.fc .fc-daygrid-day.fc-day-today {
  @apply bg-blue-50;
}

.fc .fc-list-event:hover td {
  @apply bg-blue-50;
}
</style>