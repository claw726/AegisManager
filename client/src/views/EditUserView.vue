<template>
  <div class="relative w-full h-full min-h-screen bg-background">
    <NavBar />

    <div v-if="isLoggedIn">
      <div class="text-4xl font-semibold text-secondary text-center mt-8">
        Edit User Details
      </div>

      <div v-if="currentUser" class="relative flex flex-col items-center py-12">
        <form class="w-full max-w-lg" @submit.prevent="updateUserDetails">
          <div class="mb-4">
            <label
              class="block text-gray-700 text-sm font-bold mb-2"
              for="userName"
            >
              Name
            </label>
            <input
              id="userName"
              v-model="userName"
              class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
              type="text"
              placeholder="Enter your name"
            />
          </div>
          <div class="mb-4">
            <label
              class="block text-gray-700 text-sm font-bold mb-2"
              for="email"
            >
              Email
            </label>
            <input
              id="email"
              v-model="email"
              class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
              type="email"
              placeholder="Enter your email"
            />
          </div>
          <div class="mb-4">
            <label
              class="block text-gray-700 text-sm font-bold mb-2"
              for="profilePicture"
            >
              Profile Picture
            </label>
            <input
              id="profilePicture"
              class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
              type="file"
              accept="image/*"
              @change="handleFileUpload"
            />
          </div>
          <div class="flex items-center justify-between">
            <button class="dashboard-button" type="submit">Save Changes</button>
          </div>
        </form>
      </div>
    </div>

    <div class="flex justify-center mt-4">
      <NotificationComponent
        :show="notification.show"
        :type="notification.type"
        class="max-w-md w-full shadow-lg rounded-lg overflow-hidden"
        @close="closeNotification"
      >
        <div class="p-4 break-words">
          {{ notification.message }}
        </div>
      </NotificationComponent>
    </div>
  </div>
</template>

<script>
import NavBar from "@/components/NavBar.vue";
import NotificationComponent from "@/components/NotificationComponent.vue";
import { mapActions, mapState } from "vuex";
import imageCompression from "browser-image-compression";

export default {
  components: {
    NavBar,
    NotificationComponent,
  },
  data() {
    return {
      userName: "",
      email: "",
      profilePicture: "",
      notification: {
        show: false,
        type: "error",
        message: "",
      },
    };
  },
  computed: {
    ...mapState("auth", ["isLoggedIn", "currentUser"]),
  },
  watch: {
    currentUser: {
      immediate: true,
      handler(currentUser) {
        if (currentUser) {
          this.userName = currentUser.userName;
          this.email = currentUser.email;
          this.profilePicture = currentUser.profilePicture;
        }
      },
    },
  },
  methods: {
    ...mapActions("users", ["updateUser"]),
    async handleFileUpload(event) {
      event.preventDefault();
      const file = event.target.files[0];
      if (file) {
        const validImageTypes = ["image/jpeg", "image/png", "image/gif"];
        if (!validImageTypes.includes(file.type)) {
          this.showNotification(
            "error",
            "Invalid file type. Please select an image file."
          );
          return;
        }
        try {
          const options = {
            maxSizeMB: 0.064,
            maxWidthOrHeight: 512,
            useWebWorker: true,
          };

          const reader = new FileReader();
          reader.onload = (e) => {
            const imageDataUrl = e.target.result;
            const image = new Image();
            image.onload = async () => {
              const canvas = document.createElement("canvas");
              const ctx = canvas.getContext("2d");
              const { width: imageWidth, height: imageHeight } = image;
              const aspectRatio = imageWidth / imageHeight;
              let newWidth, newHeight;

              if (aspectRatio < 1) {
                newWidth = imageWidth;
                newHeight = newWidth;
              } else {
                newWidth = imageHeight;
                newHeight = newWidth;
              }

              const x = (imageWidth - newWidth) / 2;
              const y = (imageHeight - newHeight) / 2;

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
                newHeight
              );

              const croppedDataURL = canvas.toDataURL("image/jpeg", 0.92);
              const blob = await fetch(croppedDataURL).then((res) =>
                res.blob()
              );
              const newFile = new File([blob], file.name, {
                type: "image/jpeg",
              });
              const compressedCroppedFile = await imageCompression(
                newFile,
                options
              );

              const reader = new FileReader();
              reader.onload = (e) => {
                this.profilePicture = e.target.result;
              };
              reader.readAsDataURL(compressedCroppedFile);
            };
            image.src = imageDataUrl;
          };
          reader.readAsDataURL(file);
        } catch (error) {
          console.error("Error compressing image:", error);
          this.showNotification(
            "error",
            "An error occurred while compressing the image. Please try again with a new file."
          );
        }
      } else {
        this.showNotification("error", "Please select a valid image format.");
      }
    },
    async updateUserDetails() {
      const updatedDetails = {
        name: this.userName || this.currentUser.userName,
        email: this.email || this.currentUser.email,
        profilePicture: this.profilePicture || this.currentUser.profilePicture,
      };

      try {
        await this.updateUser(updatedDetails);
        this.showNotification("success", "User details updated successfully!");
        setTimeout(() => {
          this.$router.push({ name: "AccountSettings" });
        }, 2000);
      } catch (error) {
        this.showNotification("error", error.message);
      }
    },
    showNotification(type, message) {
      this.notification = {
        show: true,
        type,
        message,
      };
      if (type === "success") {
        setTimeout(this.closeNotification, 5000);
      }
    },
    closeNotification() {
      this.notification.show = false;
    },
  },
};
</script>