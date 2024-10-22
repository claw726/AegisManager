<template>
  <div class="relative w-full h-full min-h-screen bg-background">
    <NavBar />

    <div v-if="isLoggedIn">
      <div class="text-4xl font-semibold text-secondary text-center mt-8">
        Edit User Details
      </div>

      <div v-if="currentUser" class="relative flex flex-col items-center py-12">
        <form @submit.prevent="updateUserDetails" class="w-full max-w-lg">
          <div class="mb-4">
            <label class="block text-gray-700 text-sm font-bold mb-2" for="userName">
              Name
            </label>
            <input
                v-model="userName"
                class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                id="userName"
                type="text"
                placeholder="Enter your name"
            />
          </div>
          <div class="mb-4">
            <label class="block text-gray-700 text-sm font-bold mb-2" for="email">
              Email
            </label>
            <input
                v-model="email"
                class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                id="email"
                type="email"
                placeholder="Enter your email"
            />
          </div>
          <div class="mb-4">
            <label class="block text-gray-700 text-sm font-bold mb-2" for="profilePicture">
              Profile Picture
            </label>
            <input
                @change="handleFileUpload"
                class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                id="profilePicture"
                type="file"
                accept="image/*"
            />
          </div>
          <div class="flex items-center justify-between">
            <button
                class="dashboard-button"
                type="submit"
            >
              Save Changes
            </button>
          </div>
        </form>
      </div>
    </div>

    <Notification v-if="notification.show" :type="notification.type" @close="notification.show = false">
      {{ notification.message }}
    </Notification>
  </div>
</template>

<script>
import NavBar from "@/components/NavBar.vue";
import Notification from "@/components/Notification.vue";
import { mapState } from "vuex";
import axios from "axios";
import imageCompression from "browser-image-compression";

export default {
  components: {
    NavBar,
    Notification,
  },
  data() {
    return {
      userName: this.currentUser.userName,
      email: this.currentUser.email,
      profilePicture: this.currentUser.profilePicture || '',
      notification: {
        show: false,
        type: 'error',
        message: '',
      },
    };
  },
  computed: {
    ...mapState(["isLoggedIn", "currentUser"]),
  },
  methods: {
    async handleFileUpload(event) {
      event.preventDefault();
      const file = event.target.files[0];
      if (file) {
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
              const {width: imageWidth, height: imageHeight} = image;
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
                  newHeight,
              );

              const croppedDataURL = canvas.toDataURL("image/jpeg", 0.92);
              const blob = await fetch(croppedDataURL).then((res) => res.blob());
              const newFile = new File([blob], file.name, {type: "image/jpeg"});
              const compressedCroppedFile = await imageCompression(newFile, options);

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
          this.showNotification('error', "An error occurred while compressing the image. Please try again with a new file.");
        }
      } else {
        this.showNotification('error', "Please select a valid image format.");
      }
    },
    async updateUserDetails() {
      const updatedDetails = {
        name: this.userName || this.currentUser.userName,
        email: this.email || this.currentUser.email,
        profilePicture: this.profilePicture || this.currentUser.profilePicture,
      };

      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(updatedDetails.email)) {
        this.showNotification('error', "Please enter a valid email address.");
        return;
      }

      try {
        const response = await axios.put(`/api/users/${this.currentUser.id}/update`, new URLSearchParams(updatedDetails), {
          headers: {
            'Authorization': `Bearer ${this.$store.state.authToken}`,
            'Content-Type': 'application/x-www-form-urlencoded'
          }
        });
        this.$store.commit("setCurrentUser", response.data);
        this.showNotification('success', "User details updated successfully!");
      } catch (error) {
        console.error("Error updating user details:", error);
        this.showNotification('error', "Failed to update user details.");
      }
    },
    showNotification(type, message) {
      this.notification.type = type;
      this.notification.message = message;
      this.notification.show = true;
    },
  },
};
</script>