<template>
  <div v-if="isLoggedIn" class="relative min-h-screen bg-gray-50">
    <NavBar />
    
    <!-- Main Container -->
    <div class="flex min-h-[calc(100vh-64px)]">
      <!-- Side Navigation -->
      <div class="w-64 bg-white border-r border-gray-200 p-4 shadow-sm">
        <div class="space-y-2">
          <h2 class="text-xs font-semibold text-gray-600 uppercase tracking-wider mb-4">
            Views
          </h2>
          
          <router-link
            to="/calendar"
            class="flex items-center px-4 py-2 text-gray-700 rounded-lg hover:bg-blue-50 hover:text-blue-700 transition-colors group"
            :class="{ 'bg-blue-50 text-blue-700': $route.path === '/calendar' }"
          >
            <i class="fas fa-calendar-alt w-5 h-5 mr-3"></i>
            <span class="font-medium">Calendar</span>
          </router-link>

          <router-link
            to="/todo"
            class="flex items-center px-4 py-2 text-gray-700 rounded-lg hover:bg-blue-50 hover:text-blue-700 transition-colors group"
            :class="{ 'bg-blue-50 text-blue-700': $route.path === '/todo' }"
          >
            <i class="fas fa-tasks w-5 h-5 mr-3"></i>
            <span class="font-medium">To-Do List</span>
          </router-link>

          <router-link
            to="/kanban"
            class="flex items-center px-4 py-2 text-gray-700 rounded-lg hover:bg-blue-50 hover:text-blue-700 transition-colors group"
            :class="{ 'bg-blue-50 text-blue-700': $route.path === '/kanban' }"
          >
            <i class="fas fa-columns w-5 h-5 mr-3"></i>
            <span class="font-medium">Kanban Board</span>
          </router-link>
        </div>

        <!-- Quick Stats -->
        <div class="mt-8 space-y-4">
          <h2 class="text-xs font-semibold text-gray-600 uppercase tracking-wider mb-4">
            Overview
          </h2>
          
          <div class="px-4 py-3 bg-blue-50 rounded-lg">
            <div class="text-sm text-blue-800">Today's Tasks</div>
            <div class="text-2xl font-bold text-blue-900">
              {{ todayTasks }}
            </div>
          </div>

          <div class="px-4 py-3 bg-green-50 rounded-lg">
            <div class="text-sm text-green-800">Completed</div>
            <div class="text-2xl font-bold text-green-900">
              {{ completedTasks }}
            </div>
          </div>
        </div>
      </div>

      <!-- Calendar Container -->
      <div class="flex-1 p-8">
        <div class="bg-white rounded-xl shadow-sm border border-gray-200">
          <!-- Calendar Header -->
          <div class="border-b border-gray-200 p-4">
            <div class="flex items-center justify-between">
              <h1 class="text-2xl font-bold text-gray-900">Calendar</h1>
              <div class="flex items-center space-x-2">
                <button 
                  class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors duration-200 flex items-center"
                  @click="$router.push('/tasks/create')"
                >
                  <i class="fas fa-plus mr-2"></i>
                  Add Task
                </button>
              </div>
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
      @close="popupVisible = false"
      class="z-50"
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

export default {
  components: {
    NavBar,
    Fullcalendar,
    TaskDetailPopup,
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
        height: 'auto',
        // Custom calendar styling
        dayMaxEvents: true,
        eventColor: '#3B82F6',
        eventTextColor: '#FFFFFF',
        eventBorderColor: '#2563EB',
        eventClassNames: ['rounded-md', 'px-2', 'py-1', 'text-sm'],
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
        className: task.complete ? 'bg-green-500' : '',
      }));
    },
    todayTasks() {
      const today = new Date().toISOString().split('T')[0];
      return this.tasks.filter(task => 
        task.dueDate?.split('T')[0] === today
      ).length;
    },
    completedTasks() {
      return this.tasks.filter(task => task.complete).length;
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
      const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
      
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
    document.addEventListener("click", this.handleClickOutside);
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

<style>
/* Add custom styles for FullCalendar */
.fc .fc-toolbar-title {
  @apply text-xl font-bold text-gray-900;
}

.fc .fc-button {
  @apply px-4 py-2 text-sm font-medium rounded-lg transition-colors duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500;
}

.fc .fc-button-primary {
  @apply bg-blue-600 text-white border-blue-600 hover:bg-blue-700 hover:border-blue-700;
}

.fc .fc-button-primary:not(:disabled).fc-button-active,
.fc .fc-button-primary:not(:disabled):active {
  @apply bg-blue-800 border-blue-800;
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