import Vuex from "vuex";
// import LocalStorageService from '@/utils/LocalStorageService.js';

import auth from "@/store/modules/auth.js";
import organizations from "@/store/modules/organizations.js";
import projects from "@/store/modules/project.js";
import tasks from "@/store/modules/tasks.js";
import users from "@/store/modules/users.js";

export default new Vuex.Store({
  modules: {
    auth,
    tasks,
    organizations,
    projects,
    users,
  },
});
