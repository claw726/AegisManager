import Vuex from "vuex";

// Modules:

import auth from "@/store/modules/auth.js";
import organizations from "@/store/modules/organizations.js";
import projects from "@/store/modules/project.js";
import tasks from "@/store/modules/tasks.js";
import users from "@/store/modules/users.js";
import invitations from "@/store/modules/invitations.js";
import chat from "@/store/modules/chat.js";

export default new Vuex.Store({
  modules: {
    auth,
    tasks,
    organizations,
    projects,
    users,
    invitations,
    chat,
  },
});
