import { mount } from "@vue/test-utils";
import CreateProjectView from "@/views/CreateProjView.vue";
import OrganizationDashboard from "@/views/DashboardOrgView.vue";
import { createStore } from "vuex";
import { beforeEach, describe, it, expect, vi } from "vitest";
import { createMemoryHistory, createRouter } from "vue-router";

describe("CreateProjectView.vue", () => {
  let actions;
  let store;
  let wrapper;
  let router;

  beforeEach(() => {
    // Create a Vuex store if needed
    store = createStore({
      state: {
        isLoggedIn: true,
        currentUser: {
          userID: "2",
        },
      },
    });

    actions = {
      createProject: vi.fn(),
    };

    router = createRouter({
      history: createMemoryHistory(),
      routes: [
        {
          path: "/org/:orgIndex",
          name: "organizationDashboard",
          component: CreateProjectView,
        },
      ],
    });

    router.push({ name: "organizationDashboard", params: { orgIndex: "1" } });

    wrapper = mount(CreateProjectView, {
      global: {
        plugins: [store],
      },
      store,
    });
  });

  it("renders the form", () => {
    // Check that the form is rendered
    expect(wrapper.find("div").exists()).toBe(true);
    expect(wrapper.find('[data-testid="projName"]').exists()).toBe(true);
    expect(wrapper.find("textarea#projDescription").exists()).toBe(true);
    expect(wrapper.find('[data-testid="submit"]').exists()).toBe(true);
  });

  it("does not allow project creation without title and description", async () => {
    // Ensure the title and description fields are empty
    window.alert = vi.fn();
    wrapper.setData({
      newProj: {
        projName: "",
        projDescription: "",
        projCreator: store.state.currentUser.userID,
        parentOrgID: "1",
        projImg: "encodedImage",
      },
    });

    // Trigger the form submission
    await wrapper.find('[data-testid="submit"]').trigger("click");

    // Check that the alert is shown
    expect(window.alert).toHaveBeenCalledWith(
      "Please Tell us more about your project.",
    );
  });
});
