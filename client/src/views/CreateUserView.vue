<template>
  <div class="min-h-screen bg-gray-50">
    <NavBar />

    <div v-if="!isLoggedIn" class="container mx-auto px-4 py-12">
      <div class="text-center mb-12">
        <h1 class="text-4xl font-bold text-gray-800 mb-2">
          <i class="fas fa-user-plus text-primary mr-2"></i>
          Create Your Account
        </h1>

        <p class="text-gray-600">Join our community and get started today</p>
      </div>

      <div class="max-w-4xl mx-auto">
        <div class="bg-white rounded-2xl shadow-xl p-8 md:p-10">
          <div class="flex justify-center mb-10">
            <div class="flex items-center space-x-4">
              <div class="flex items-center">
                <div
                  class="w-8 h-8 rounded-full bg-primary text-white flex items-center justify-center"
                >
                  <i class="fas fa-user text-sm"></i>
                </div>

                <div class="ml-2 text-sm font-medium text-primary">
                  Personal Info
                </div>
              </div>

              <div class="w-16 h-0.5 bg-primary"></div>
              <div class="flex items-center">
                <div
                  class="w-8 h-8 rounded-full bg-primary text-white flex items-center justify-center"
                >
                  <i class="fas fa-lock text-sm"></i>
                </div>

                <div class="ml-2 text-sm font-medium text-primary">
                  Security
                </div>
              </div>
            </div>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div class="form-group">
              <label class="form-label">
                <i class="fas fa-user text-primary mr-2"></i>
                First Name</label
              >
              <input
                v-model="user.firstName"
                type="text"
                class="form-input"
                placeholder="John"
                @focus="clearError('firstName')"
              />

              <p v-if="errors.firstName" class="form-error">
                <i class="fas fa-exclamation-circle mr-1"></i>
                {{ errors.firstName }}
              </p>
            </div>

            <div class="form-group">
              <label class="form-label">
                <i class="fas fa-user text-primary mr-2"></i>
                LastName</label
              >
              <input
                v-model="user.lastName"
                type="text"
                class="form-input"
                placeholder="Doe"
                @focus="clearError('lastName')"
              />

              <p v-if="errors.lastName" class="form-error">
                <i class="fas fa-exclamation-circle mr-1"></i>
                {{ errors.lastName }}
              </p>
            </div>

            <div class="form-group">
              <label class="form-label">
                <i class="fas fa-envelope text-primary mr-2"></i>
                Email Address</label
              >
              <input
                v-model="user.email"
                type="email"
                class="form-input"
                placeholder="john.doe@example.com"
                @focus="clearError('email')"
              />

              <p v-if="errors.email" class="form-error">
                <i class="fas fa-exclamation-circle mr-1"></i>
                {{ errors.email }}
              </p>
            </div>

            <div class="form-group">
              <label class="form-label">
                <i class="fas fa-camera text-primary mr-2"></i>
                Profile Picture</label
              >

              <div
                @drop.prevent="handleDrop"
                @dragover.prevent="dragOver = true"
                @dragleave.prevent="dragOver = false"
                @click="triggerFileInput"
                :class="[
                  'relative group cursor-pointer transition-all duration-300',
                  'border-2 border-dashed rounded-xl p-4 h-[150px]',
                  dragOver ? 'border-primary bg-primary/5' : 'border-gray-300',
                  imageUploaded ? 'bg-green-50' : 'hover:bg-gray-50',
                ]"
              >
                <div
                  v-if="imagePreview"
                  class="flex items-center justify-center h-full"
                >
                  <div class="relative">
                    <img
                      :src="imagePreview"
                      class="w-24 h-24 rounded-full object-cover shadow-lg"
                      alt="Profile preview"
                    />
                    <button
                      @click.stop="clearImage"
                      class="absolute -top-2 -right-2 bg-red-500 text-white rounded-full p-1.5 hover:bg-red-600 transition-colors duration-200 shadow-lg"
                    >
                      <i class="fas fa-times text-xs"></i>
                    </button>
                  </div>
                </div>

                <div
                  v-else
                  class="flex flex-col items-center justify-center h-full"
                >
                  <i
                    class="fas fa-cloud-upload-alt text-4xl text-primary mb-2 group-hover:scale-110 transition-transform duration-200"
                  ></i>

                  <p class="text-sm text-gray-600 text-center">
                    Drag and drop your image here or click to browse
                  </p>

                  <p class="text-xs text-gray-400 mt-1">
                    Supported formats: JPG, PNG, GIF, WebP (Max 5MB)
                  </p>
                  <input
                    ref="fileInputRef"
                    type="file"
                    accept="image/jpeg,image/png,image/gif,image/webp"
                    class="hidden"
                    @change="handleFileUpload"
                  />
                </div>
              </div>
              <p v-if="errors.profilePicture" class="form-error">
                <i class="fas fa-exclamation-circle mr-1"></i>
                {{ errors.profilePicture }}
              </p>
            </div>

            <div class="col-span-full">
              <PasswordInput
                :Title="'Create Password'"
                @update-password="updatePassword"
              />

              <p v-if="errors.password" class="form-error">
                <i class="fas fa-exclamation-circle mr-1"></i>
                {{ errors.password }}
              </p>
            </div>
          </div>

          <div class="mt-8">
            <button
              data-testid="submit-button"
              class="w-full bg-primary hover:bg-primary/90 text-white font-semibold py-4 rounded-xl transition-all duration-200 transform hover:scale-[1.02] focus:outline-none focus:ring-2 focus:ring-primary focus:ring-offset-2"
              @click="submitForm"
            >
              <i class="fas fa-check-circle mr-2"></i>
              Create Account
            </button>
          </div>

          <div v-if="errors.general" class="mt-6">
            <div
              class="bg-red-50 border-l-4 border-red-500 text-red-700 p-4 rounded-lg"
              role="alert"
            >
              <div class="flex items-center">
                <i class="fas fa-exclamation-triangle mr-2"></i>
                <span>{{ errors.general }}</span>
              </div>
            </div>
          </div>

          <div class="text-center mt-6 text-gray-600">
            Already have an account?<router-link
              to="/login"
              class="text-primary hover:text-primary/80 font-medium ml-1"
            >
              Sign In
            </router-link>
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
        profilePicture: "",
      },
      dragOver: false,
      imagePreview: null,
      maxFileSize: 5 * 1024 * 1024, // 5MB
      supportedFormats: ["image/jpeg", "image/png", "image/webp", "image/avif"],
    };
  },
  computed: {
    ...mapState("auth", ["isLoggedIn", "error"]),
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
      this.$refs.fileInputRef.click();
    },
    handleDrop(event) {
      this.dragOver = false;
      const file = event.dataTransfer.files[0];
      if (file) {
        this.processFile(file);
      }
    },

    clearImage() {
      this.imagePreview = null;
      this.user.profilePicture = null;
      this.imageUploaded = false;
      if (this.$refs.fileInput) {
        this.$refs.fileInput.value = "";
      }
    },

    handleFileUpload(event) {
      const file = event.target.files[0];
      if (file) {
        this.processFile(file);
      }
    },

    validateFile(file) {
      if (!this.supportedFormats.includes(file.type)) {
        this.errors.profilePicture =
          "Please select a supported image format (JPG, PNG, WEBP, or AVIF)";
        return false;
      }

      if (file.size > this.maxFileSize) {
        this.errors.profilePicture = "File size must be less than 5MB";
        return false;
      }

      return true;
    },

    async processFile(file) {
      this.errors.profilePicture = "";

      if (!this.validateFile(file)) {
        return;
      }

      try {
        // options for image compression
        const options = {
          maxSizeMB: 0.032,
          maxWidthOrHeight: 256,
          useWebWorker: true,
        };

        // Read the file as a data URL
        const reader = new FileReader();
        reader.onload = (e) => {
          this.imagePreview = e.target.result;
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
            const croppedDataURL = canvas.toDataURL("image/webp", 0.92);
            this.imagePreview = croppedDataURL;

            // Convert the data URL to a BLOB
            const blob = await fetch(croppedDataURL).then((res) => res.blob());

            // Create a new file from the BLOB
            const newFile = new File([blob], file.name, {
              type: "image/webp",
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
        this.errors.profilePicture =
          "An error occurred while compressing the image. Please try again with a new file.";
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
.form-group {
  @apply space-y-1;
}

.form-label {
  @apply block text-sm font-medium text-gray-700;
}

.form-input  {
  @apply w-full px-4 py-3 rounded-lg border border-gray-300
  focus:ring-2 focus:ring-primary focus:border-primary
  transition-all duration-200 bg-white
  placeholder:text-gray-400;
}

.form-error  {
  @apply text-sm text-red-500 mt-1;
}

/* Animations */
.fade-enter-active,
.fade-leave-active {
  @apply transition-opacity duration-300;
}

.fade-enter-from,
.fade-leave-to {
  @apply opacity-0;
}
</style>
