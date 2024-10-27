<template>
  <div class="relative w-full min-h-screen h-full bg-background">
    <NavBar />

    <div v-if="!isLoggedIn" class="flex flex-col items-center mt-20">
      <div class="text-3xl font-bold text-primary">Create Profile</div>
      <div class="text-xl font-semibold text-secondary mt-2">
        Register Securely with Aegis
      </div>

      <div class="w-3/4 max-w-4xl mt-10 bg-white shadow-lg rounded-lg p-8">
        <div class="flex flex-wrap -mx-4">
          <!-- First Name -->
          <div class="w-full md:w-1/2 px-4 mb-4">
            <label class="block text-sm font-semibold text-gray-800 mb-2"
              >First Name</label
            >
            <input
              type="text"
              v-model="user.firstName"
              class="w-full border border-highlight rounded-lg p-3"
              @focus="clearError('firstName')"
            />
            <p v-if="errors.firstName" class="mt-1 text-sm text-red-500">
              {{ errors.firstName }}
            </p>
          </div>

          <!-- Last Name -->
          <div class="w-full md:w-1/2 px-4 mb-4">
            <label class="block text-sm font-semibold text-gray-800 mb-2"
              >Last Name</label
            >
            <input
              type="text"
              v-model="user.lastName"
              class="w-full border border-highlight rounded-lg p-3"
              @focus="clearError('lastName')"
            />
            <p v-if="errors.lastName" class="mt-1 text-sm text-red-500">
              {{ errors.lastName }}
            </p>
          </div>

          <!-- Email Address -->
          <div class="w-full md:w-1/2 px-4 mb-4">
            <label class="block text-sm font-semibold text-gray-800 mb-2"
              >Email Address</label
            >
            <input
              type="email"
              v-model="user.email"
              class="w-full border border-highlight rounded-lg p-3"
              @focus="clearError('email')"
            />
            <p v-if="errors.email" class="mt-1 text-sm text-red-500">
              {{ errors.email }}
            </p>
          </div>

          <!-- Profile Picture -->
          <div class="w-full md:w-1/2 px-4 mb-4">
            <label class="block text-sm font-semibold text-gray-800 mb-2"
              >Profile Picture</label
            >
            <input
              type="file"
              accept="image/jpeg"
              @change="handleFileUpload"
              class="hidden"
              ref="fileInput"
            />
            <button
              @click="triggerFileInput"
              class="w-full bg-primary text-white font-semibold py-3 rounded-lg"
            >
              Upload Image
              <span v-if="imageUploaded" class="text-gray-500 ml-2"
                >(Image Uploaded)</span
              >
            </button>
          </div>

          <!-- Password Input -->
          <div class="w-full flex justify-center px-4 mb-4">
            <div class="w-full md:w-1/2">
              <PasswordInput
                @update-password="updatePassword"
                :Title="'Password'"
              />
              <p v-if="errors.password" class="mt-1 text-sm text-red-500">
                {{ errors.password }}
              </p>
            </div>
          </div>

          <!-- Submit Button -->
          <button
            @click="submitForm"
            data-testid="submit-button"
            class="w-full mt-4 bg-primary text-white font-semibold py-3 rounded-lg"
          >
            Submit
          </button>
        </div>

        <!-- General Error Message -->
        <div v-if="errors.general" class="mt-4">
          <div
            class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative"
            role="alert"
          >
            <span class="block sm:inline">{{ errors.general }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState, mapActions } from "vuex";
import NavBar from "@/components/NavBar.vue";
import PasswordInput from "@/components/PasswordCreator.vue";
import imageCompression from "browser-image-compression";
export default {
  components: {
    PasswordInput,
    NavBar,
  },
  computed: {
    ...mapState("auth", ["isLoggedIn", "error"]),
  },
  data() {
    return {
      user: {
        firstName: "",
        lastName: "",
        email: "",
        password: "",
        profilePicture: "",
      },
      imageUploaded: false, // Track if an image has been uploaded
      errors: {
        firstName: "",
        lastName: "",
        email: "",
        password: "",
        general: "",
      },
    };
  },
  watch: {
    "user.email"(newEmail) {
      this.user.email = newEmail.trim();
      this.user.email = newEmail.toLowerCase();
    },
  },
  methods: {
    ...mapActions("auth", ["register", "login"]),
    clearError(field) {
      this.errors[field] = "";
      this.errors.general = "";
    },
    triggerFileInput() {
      this.$refs.fileInput.click();
    },
    async handleFileUpload(event) {
      event.preventDefault();
      const file = event.target.files[0];
      if (file) {
        try {
          // options for image compression
          const options = {
            maxSizeMB: 0.064,
            maxWidthOrHeight: 512,
            useWebWorker: true,
          };

          // Read the file as a data URL
          const reader = new FileReader();
          reader.onload = (e) => {
            const imageDataUrl = e.target.result;
            const image = new Image();
            image.onload = async () => {
              // Create a canvas to crop the image
              const canvas = document.createElement("canvas");
              const ctx = canvas.getContext("2d");
              const { width: imageWidth, height: imageHeight } = image;
              const aspectRatio = imageWidth / imageHeight;
              let newWidth, newHeight;

              // Calculate the dimensions for the 1:1 aspect ratio crop
              if (aspectRatio < 1) {
                newWidth = imageWidth;
                newHeight = newWidth;
              } else {
                newWidth = imageHeight;
                newHeight = newWidth;
              }

              // Calculate the center coordinates for the crop
              const x = (imageWidth - newWidth) / 2;
              const y = (imageHeight - newHeight) / 2;

              // Set the canvas dimensions and draw the new image
              canvas.width = newWidth;
              canvas.height = newHeight;
              ctx.drawImage(
                image,
                x,
                y,
                newWidth,
                newHeight,
                0,
                0,
                newWidth,
                newHeight,
              );

              // Convert the Canvas to a URL
              const croppedDataURL = canvas.toDataURL("image/jpeg", 0.92);

              // Convert the data URL to a BLOB
              const blob = await fetch(croppedDataURL).then((res) =>
                res.blob(),
              );

              // Create a new file from the BLOB
              const newFile = new File([blob], file.name, {
                type: "image/jpeg",
              });

              // Compress the cropped image
              const compressedCroppedFile = await imageCompression(
                newFile,
                options,
              );

              // Read the compressed file as a data URL
              const reader = new FileReader();
              reader.onload = (e) => {
                // Set the user profile photo to the compressed cropped photo
                this.user.profilePicture = e.target.result;
                this.imageUploaded = true;
              };
              reader.readAsDataURL(compressedCroppedFile);
            };
            image.src = imageDataUrl;
          };
          reader.readAsDataURL(file);
        } catch (error) {
          console.error("Error compressing image:", error);
          this.errors.general = "An error occurred while compressing the image. Please try again with a new file."
        }
      } else {
        this.errors.general = "Please select a valid image format.";
      }
    },
    async submitForm() {
      // Clear previous errors
      this.clearError();

      // Validate fields
      if (!this.user.firstName) {
        this.errors.firstName = "First name is required.";
      }
      if (!this.user.lastName) {
        this.errors.lastName = "Last name is required.";
      }
      if (!this.user.email) {
        this.errors.email = "Email is required.";
      } else {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(this.user.email)) {
          this.errors.email = "Please enter a valid email address.";
        }
      }
      if (!this.user.password) {
        this.errors.password = "Password is required.";
      }
      if (!this.user.profilePicture) {
        this.errors.general = "Please upload a profile picture.";
      }

      // Check if there are any errors
      if (Object.values(this.errors).some((error) => error)) {
        return; // Stop if there are errors
      }

      try {
        // Register the user
        await this.register({
          email: this.user.email,
          name: `${this.user.firstName} ${this.user.lastName}`,
          password: this.user.password,
          profilePicture: this.user.profilePicture,
        });

        this.goToDash();
      } catch (error) {
        // Set error messages based on the response
        if (error.response) {
          this.errors.general =
            error.response.data || "Registration failed. Please try again.";
        } else {
          this.errors.general =
            error ||
            "Unable to connect to the server. Please check your internet connection.";
        }
      }
    },
    goToDash() {
      // Redirect to the dashboard
      this.$router.push({ name: "Dashboard" });
    },
    updatePassword(password) {
      this.user.password = password;
    },
  },
};
</script>

<style scoped>
/* Add any additional styles here if needed */
</style>
