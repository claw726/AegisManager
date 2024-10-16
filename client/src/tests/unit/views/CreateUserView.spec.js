import { shallowMount, createLocalVue } from "@vue/test-utils";
import CreateProfile from "@/components/CreateProfile.vue";
import NavBar from "@/components/NavBar.vue";
import PasswordInput from "@/components/PasswordCreator.vue";
import Vuex from "vuex";

const localVue = createLocalVue();
localVue.use(Vuex);

describe("CreateProfile.vue", () => {
  let actions;
  let store;
  let wrapper;

  beforeEach(() => {
    actions = {
      register: jest.fn(),
      login: jest.fn(),
    };

    store = new Vuex.Store({
      state: {
        isLoggedIn: false,
      },
      actions,
    });

    wrapper = shallowMount(CreateProfile, {
      localVue,
      store,
      stubs: {
        NavBar,
        PasswordInput,
      },
    });
  });

  it("renders the component", () => {
    expect(wrapper.exists()).toBe(true);
  });

  it("should update email and password with trim and toLowerCase", async () => {
    const emailInput = wrapper.find('input[type="email"]');
    const passwordInputStub = wrapper.findComponent(PasswordInput);

    await emailInput.setValue("  Test@Email.com  ");
    expect(wrapper.vm.user.email).toBe("test@email.com");

    // Simulate password update from PasswordInput component
    passwordInputStub.vm.$emit("update-password", "  P@$wOrd ");
    expect(wrapper.vm.user.password).toBe("P@$wOrd");
  });

  it("should handle file uploads", async () => {
    const fileInput = wrapper.find('input[type="file"]');
    const mockFile = new File([""], "mock-image.jpg", { type: "image/jpeg" });

    // Set up a mock for the FileReader
    const mockFileReader = {
      readAsDataURL: jest.fn(),
      onload: jest.fn(),
      result: "mock-data-url",
    };
    window.FileReader = jest.fn(() => mockFileReader);

    // Mock the imageCompression library
    jest.mock("browser-image-compression", () =>
      jest.fn(() => Promise.resolve(mockFile)),
    );

    await fileInput.trigger("change", { target: { files: [mockFile] } });

    expect(mockFileReader.readAsDataURL).toHaveBeenCalledWith(mockFile);

    // Simulate the FileReader onload event
    mockFileReader.onload();

    // Check that the imageUploaded flag and user.profilePicture are updated
    expect(wrapper.vm.imageUploaded).toBe(true);
    expect(wrapper.vm.user.profilePicture).toBe("mock-data-url");
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

    await wrapper.find('button[type="submit"]').trigger("click");

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
    window.alert = jest.fn();
    wrapper.setData({
      user: {
        firstName: "John",
        lastName: "Doe",
        email: "invalid-email",
        password: "password123",
      },
    });

    await wrapper.find('button[type="submit"]').trigger("click");

    expect(window.alert).toHaveBeenCalledWith(
      "Please enter a valid email address.",
    );
  });

  it("should show an alert for empty fields", async () => {
    window.alert = jest.fn();
    await wrapper.find('button[type="submit"]').trigger("click");
    expect(window.alert).toHaveBeenCalledWith(
      "Please fill out all fields and upload an image.",
    );
  });
});
