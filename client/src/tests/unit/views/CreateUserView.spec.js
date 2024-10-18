import { shallowMount } from "@vue/test-utils"; // Ensure shallowMount is imported
import CreateProfile from "@/views/CreateUserView.vue";
import NavBar from "@/components/NavBar.vue";
import PasswordInput from "@/components/PasswordCreator.vue";
import { createStore } from "vuex"; // Use createStore from Vuex
import { beforeEach, describe, it, expect, vi } from "vitest"; // Import from Vitest

vi.mock("browser-image-compression", () => {
  return {
    default: vi.fn(() =>
      Promise.resolve(new File([""], "mock-image.jpg", { type: "image/jpeg" })),
    ),
  };
});

describe("CreateProfile.vue", () => {
  let actions;
  let store;
  let wrapper;

  beforeEach(() => {
    actions = {
      register: vi.fn(), // Use vi.fn() instead of jest.fn()
      login: vi.fn(),
    };

    store = createStore({
      // Use createStore instead of Vuex.Store
      state: {
        isLoggedIn: false,
      },
      actions,
    });

    wrapper = shallowMount(CreateProfile, {
      global: {
        plugins: [store],
        stubs: {
          NavBar,
          PasswordInput,
        },
      },
      // Provide the store to the component
      store,
    });
  });

  it("renders the component", () => {
    expect(wrapper.exists()).toBe(true);
  });

  it("should update email with trim and toLowerCase", async () => {
    const emailInput = wrapper.find('input[type="email"]');

    await emailInput.setValue("  Test@Email.com  ");
    expect(wrapper.vm.user.email).toBe("test@email.com");
  });

  it("should submit the form with valid data", async () => {
    wrapper.setData({
      user: {
        firstName: "John",
        lastName: "Doe",
        email: "john.doe@example.com",
        password: "password123",
        profilePicture: "mock-data-url",
      },
    });

    await wrapper.find('[data-testid="submit-button"]').trigger("click");

    expect(actions.register).toHaveBeenCalledWith(expect.any(Object), {
      email: "john.doe@example.com",
      name: "John Doe",
      password: "password123",
      profilePicture: "mock-data-url",
    });
    expect(actions.login).toHaveBeenCalledWith(expect.any(Object), {
      email: "john.doe@example.com",
      password: "password123",
    });
  });

  it("should show an alert for invalid email", async () => {
    window.alert = vi.fn(); // Use vi.fn() for mocking
    wrapper.setData({
      user: {
        firstName: "John",
        lastName: "Doe",
        email: "invalid-email",
        password: "password123",
      },
    });

    await wrapper.find('[data-testid="submit-button"]').trigger("click");

    expect(window.alert).toHaveBeenCalledWith(
      "Please enter a valid email address.",
    );
  });

  it("should show an alert for empty fields", async () => {
    window.alert = vi.fn(); // Use vi.fn() for mocking
    await wrapper.find('[data-testid="submit-button"]').trigger("click");
    expect(window.alert).toHaveBeenCalledWith(
      "Please fill out all fields and upload an image.",
    );
  });
});
