import { shallowMount } from "@vue/test-utils";
import CreateProjectView from "@/views/CreateProjView.vue";
import { createStore } from "vuex";
import { beforeEach, describe, it, expect, vi } from "vitest";

describe("CreateProjectView.vue", () => {
  let store;
  let wrapper;

  beforeEach(() => {
    // Create a Vuex store if needed
    store = createStore({
      state: {
        isLoggedIn: true,
        currentUser: {
          userID: "test-user",
        },
      },
    });

    wrapper = shallowMount(CreateProjectView, {
      global: {
        plugins: [store],
      },
    });
  });

  it("does not allow project creation without title and description", async () => {
    // Ensure the title and description fields are empty
    await wrapper.find('[data-testid="projName"]').setValue("");
    await wrapper.find("textarea#projDescription").setValue("");

    // Trigger the form submission
    await wrapper.find('[data-testid="submit-button"]').trigger("click");

    // Check that the alert is shown
    expect(window.alert).toHaveBeenCalledWith(
      "Please Tell us more about your project.",
    );
  });

  it("allows project creation with title and description", async () => {
    // Fill in the title and description fields
    await wrapper.find('input[id="projName"]').setValue("Test Project");
    await wrapper
      .find('input[id="projDescription')
      .setValue("This is a test project.");

    // Trigger the form submission
    await wrapper.find('button[id="submit"]').trigger("click");

    // Check that the project creation is successful
    // This part depends on how you handle project creation in your Vuex store
    // For example, you might check if the project is added to the store
    expect(store.state.projects).toContainEqual({
      projName: "Test Project",
      projDescription: "This is a test project.",
      // Other project details...
    });
  });
});
