<template>
  <NavBar />
  <div class="bg-white flex flex-col items-center min-h-screen h-full">
    

    <div class="flex flex-col items-center space-y-8 mt-12">
      <h1 class="text-4xl font-bold text-hunter-green mb-6">To Do List</h1>

      <button class="edit-btn rounded" @click="goToCreateTask">
        Create Task
      </button>

     <!-- Grid container for task cards -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 w-full max-w-7xl px-4">
        <template v-if="allTasks && allTasks.length > 0">
          <TaskCard
            v-for="task in allTasks"
            :key="task.taskID"
            :task="task"
          />
        </template>
        <div v-else class="col-span-full text-center text-gray-600">
          No tasks available.
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import NavBar from "@/components/NavBar.vue";
import { mapState, mapActions } from "vuex";
import TaskCard from "@/components/TaskCard.vue";

export default {
  components: {
    NavBar,
    TaskCard,
  },

  computed: {
    ...mapState('tasks', ['tasks']),
    ...mapState('auth', ['currentUser']),
    allTasks() {
      console.log("Tasks in component: ", this.tasks);
      return this.tasks;
    }
  },

  created() {
    this.fetchTasks(); // Fetch tasks when the component is created
  },

  methods: {
    ...mapActions('tasks', ['fetchTasks']),


    viewTask(task) {
      this.$router.push({
        name: "TaskDetail",
        query: { taskId: task.id },
      });
    },

    goToCreateTask() {
      this.$router.push({ name: "createTask" });
    },
  },
};
</script>

<style scoped>
.task-card {
  transition: transform 0.2s;
}

.edit-btn {
  cursor: pointer;
  background-color: rgb(2, 2, 58);
  color: white;
  border: none;
  padding: 5px 10px;
}

.task-card:hover {
  transform: scale(1.15);
}
</style>