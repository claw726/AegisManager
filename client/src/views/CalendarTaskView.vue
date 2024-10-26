<template>
  <div
    v-if="isLoggedIn"
    class="relative w-full h-full min-h-screen bg-background"
  >
    <NavBar />
    <div class="bg-white m-24 mb-24 rounded-lg p-8 border dropshadow-lg">
      <Fullcalendar :options="calendarOptions" />
    </div>
    <TaskDetailPopup
      v-if="selectedTask"
      :task="selectedTask"
      :visible="popupVisible"
      :position="popupPosition"
      @close="popupVisible = false"
    />
  </div>
</template>

<script>
import NavBar from "@/components/NavBar.vue";
import { mapActions, mapState } from "vuex";
import Fullcalendar from "@fullcalendar/vue3";
import DayGridPlugin from "@fullcalendar/daygrid";
import InteractionPlugin from "@fullcalendar/interaction";
import ListPlugin from "@fullcalendar/list";

import TaskDetailPopup from "@/components/TaskDetailPopup.vue";

export default {
  components: {
    NavBar,
    Fullcalendar,
    TaskDetailPopup,
  },
  data() {
    return {
      calendarOptions: {
        plugins: [DayGridPlugin, InteractionPlugin, ListPlugin],
        initialView: "dayGridMonth",
        headerToolbar: {
          left: "prev,next today",
          center: "title",
          right: "dayGridMonth,dayGridWeek,listWeek",
        },
        editable: false,
        selectable: true,
        events: [],
        // Event Handlers
        eventClick: this.handleEventClick,
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
          complete: task.complete,
        },
      }));
    },
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

  async mounted() {
    await this.loadTasks();
  },

  beforeUnmount() {
    document.removeEventListener("click", this.handleClickOutside);
  },

  watch: {
    tasks: {
      handler() {
        this.calendarOptions.events = this.calendarEvents;
      },
      deep: true,
    },
  },
};
</script>
