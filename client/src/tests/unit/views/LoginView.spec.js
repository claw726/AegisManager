import { mount } from "@vue/test-utils";
import Login from "@/views/LoginPage.vue"; // Adjust the path as necessary
import { createStore } from "vuex";
import { beforeEach, describe, it, expect, vi } from "vitest";
import { router } from "@/routes.js";

describe("Login.vue", () => {
  let actions;
  let store;
  let wrapper;

  beforeEach(() => {
    actions = {
      login: vi.fn(), // Mock the login action
    };

    // Create a Vuex store with the mocked actions
    store = createStore({
      state: {
        isLoggedIn: false,
        currentUser: null,
      },
      actions,
    });

    // Mount the Login component with the store
    wrapper = mount(Login, {
      global: {
        plugins: [store, router],
      },
    });
  });

  it("renders the login form", () => {
    console.log(wrapper.html());

    expect(wrapper.find('[data-testid="Header"]').exists()).toBe(true);
    expect(wrapper.find('[data-testid="Header"]').text()).toBe("Login");

    expect(wrapper.find('input[type="email"]').exists()).toBe(true);
    expect(wrapper.find('input[type="password"]').exists()).toBe(true);
    expect(wrapper.find("button").text()).toBe("Log In");
  });

  it("displays an alert when fields are empty", async () => {
    window.alert = vi.fn(); // Mock the alert function

    wrapper.setData({
      email: "",
      password: "",
    }); // Set the email and password to empty strings

    await wrapper.find('input[type="email"]').setValue(wrapper.email);
    await wrapper.find('input[type="password"]').setValue(wrapper.password);

    await wrapper.find("button").trigger("click"); // Trigger the login button

    expect(window.alert).toHaveBeenCalledWith(
      "Please enter both email and password.",
    );
  });

  it("calls the login action with correct credentials", async () => {
    // Set the email and password
    await wrapper.find('input[type="email"]').setValue("test@example.com");
    await wrapper.find('input[type="password"]').setValue("password123");

    await wrapper.find("button").trigger("click"); // Trigger the login button

    expect(actions.login).toHaveBeenCalledWith(expect.any(Object), {
      email: "test@example.com",
      password: "password123",
    });
  });

  it("displays an alert on login error", async () => {
    window.alert = vi.fn(); // Mock the alert function

    await wrapper.find('input[type="email"]').setValue("test@example.com");
    await wrapper.find('input[type="password"]').setValue("password123");

    await wrapper.find("button").trigger("click"); // Trigger the login button

    actions.login.mockImplementationOnce(() => {
      throw new Error("Login failed"); // Simulate a login error
    });

    expect(window.alert).toHaveBeenCalledWith(
      "An Error occurred. Please try again.",
    );
  });
});
