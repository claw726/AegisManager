import { createRouter, createWebHistory } from "vue-router";
import store from "./store/index.js";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: "/",
      name: "Home",
      component: () => import("./views/HomeView.vue"),
    },
    {
      path: "/createAccount",
      name: "CreateAcct",
      component: () => import("./views/CreateUserView.vue"),
      meta: { requiresGuest: true },
    },
    {
      path: "/login",
      name: "Login",
      component: () => import("./views/LoginPage.vue"),
      meta: { requiresGuest: true },
    },
    {
      path: "/dashboard",
      name: "Dashboard",
      component: () => import("./views/DashboardAccountView.vue"),
      meta: { requiresAuth: true },
    },

    {
      path: "/organization",
      name: "viewOrgs",
      component: () => import("./views/ListOrgView.vue"),
      meta: { requiresAuth: true },
    },
    {
      path: "/createOrg",
      name: "createOrg",
      component: () => import("./views/CreateOrgView.vue"),
      meta: { requiresAuth: true },
    },
    {
      path: "/organization/:orgIndex",
      name: "OrganizationDashboard",
      component: () => import("./views/DashboardOrgView.vue"),
      meta: { requiresAuth: true },
    },
    {
      path: "/todolist",
      name: "TDList",
      component: () => import("./views/ToDoList.vue"),
      meta: { requiresAuth: true },
    },
    {
      path: "/tasks/:taskId",
      name: "TaskDetail",
      component: () => import("./views/TaskDetail.vue"),
      meta: { requiresAuth: true },
      props: true, // Allow passing route params as props
    }, 
    {
      path: "/tasks/:taskId/editTask",
      name: "editTask",
      component: () => import("./views/EditTaskView.vue"),
      meta: { requiresAuth: true },
      props: true, // Allow passing route params as props
    },
    {
      path: "/organization/:orgIndex/project/:projIndex/createTask",
      name: "createTask",
      component: () => import("./views/CreateTaskView.vue"),
      meta: { requiresAuth: true },
    }, 
    {
      path: "/tasks/:taskId/addUserTask",
      name: "addUserTask",
      component: () => import("./views/AddUsersToTask.vue"),
      meta: { requiresAuth: true },
    },

    {
      path: "/organization/:orgIndex/createProject",
      name: "createProject",
      component: () => import("./views/CreateProjView.vue"),
      meta: { requiresAuth: true },
    },
    {
      path: "/organization/:orgIndex/edit",
      name: "EditOrg",
      component: () => import("./views/EditOrgView.vue"),
      meta: { requiresAuth: true },
    },
    {
      path: "/organization/:orgIndex/editUsers",
      name: "EditOrgUsers",
      component: () => import("./views/EditOrgUsersView.vue"),
      meta: { requiresAuth: true },
    },
    {
      path: "/kanban",
      name: "KB",
      component: () => import("./views/KanbanView.vue"),
      meta: { requiresAuth: true },
    },
    {
      path: "/organization/:orgIndex/project/:projIndex",
      name: "ProjectDashboard",
      component: () => import("./views/DashboardProjectView.vue"),
      meta: { requiresAuth: true },
    },
    {
      path: "/organization/:orgIndex/project/:projIndex/edit",
      name: "EditProject",
      component: () => import("./views/EditProjectView.vue"),
      meta: { requiresAuth: true },
    },
    {
      path: "/organization/:orgIndex/viewUsers",
      name: "viewUsersInOrg",
      component: () => import("./views/ListOrgUsersView.vue"),
      meta: { requiresAuth: true },
    },
    {
      path: "/organization/:orgIndex/project/:projIndex/editUsers",
      name: "EditProjUsers",
      component: () => import("./views/EditProjUsersView.vue"),
      meta: { requiresAuth: true },
    },
    {
      path: "/organization/:orgIndex/project/:projIndex/edit/assignUsers",
      name: "assignUsersinProj",
      component: () => import("./views/ListProjUsersView.vue"),
      meta: { requiresAuth: true },
    },
    {
      path: "/AccountSettings",
      name: "AccountSettings",
      component: () => import("./views/AccountSettingsView.vue"),
      meta: { requiresAuth: true },
    },
    {
      path: "/updateAccount",
      name: "UpdateAccount",
      component: () => import("./views/EditUserView.vue"),
      meta: { requiresAuth: true },
    },
    {
      path: "/reset-password",
      name: "ResetPassword",
      component: () => import("./views/ResetPasswordView.vue"),
      meta: { requiresAuth: false }, // No need to be logged in to reset password
    },
    {
      path: "/calendar/",
      name: "Calendar",
      component: () => import("./views/CalendarTaskView.vue"),
      meta: { requiresAuth: true },
    },
    {
      path: "/archived-projects",
      name: "ArchivedProjects",
      component: () => import("./views/ArchivedProjectsView.vue"),
      meta: { requiresAuth: true },
    },
  ],
});

// Navigation Guard
router.beforeEach((to, from, next) => {
  const isLoggedIn = store.state.auth.isLoggedIn;

  if (to.matched.some((record) => record.meta.requiresAuth) && !isLoggedIn) {
    // Redirect to login if trying to access a protected route without being logged in
    next({ name: "Login" });
  } else if (
    to.matched.some((record) => record.meta.requiresGuest) &&
    isLoggedIn
  ) {
    // Redirect to dashboard if trying to access login or create account while logged in
    next({ name: "TDList" });
  } else {
    next();
  }
});

export default router;
