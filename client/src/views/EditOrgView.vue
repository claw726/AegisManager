<template>
  <NavBar />
  <div
    v-if="isLoggedIn"
    class="min-h-screen bg-background py-12 px-4 sm:px-6 lg:px-8"
  >
    <div class="max-w-3xl mx-auto">
      <!-- Header -->
      <div class="text-center mb-8">
        <h1 class="text-4xl font-bold text-primary mb-4">
          Edit Organization: {{ modifiedOrganization.orgName }}
        </h1>
        <div class="h-1 bg-accent mx-auto rounded-full" />
      </div>

      <div class="space-y-8">
        <!-- Org name -->
        <div class="space-y-2">
          <label
            for="orgName"
            class="flex items-center text-lg font-medium text-gray-700"
            ><svg
              xmlns="http://www.w3.org/2000/svg"
              class="w-4 h-4 mr-2 text-accent"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <path
                d="M3 9v10a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V9M3 9l2.111-6.333A2 2 0 0 1 7 1h10a2 2 0 0 1 1.889 1.667L21 9M3 9h18M12 3v6m-4 0v6m8-6v6"
              />
            </svg>
            Organization Name:</label
          >
          <input
            type="text"
            id="orgName"
            v-model="modifiedOrganization.orgName"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-accent focus:border-transparent transition duration-200"
          />
        </div>

        <!-- Org Desc -->
        <div class="space-y-2">
          <label
            for="orgDescription"
            class="flex items-center text-lg font-medium text-gray-700"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="w-4 h-4 mr-2 text-accent"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <path
                d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"
              ></path>
              <polyline points="14 2 14 8 20 8"></polyline>
              <line x1="16" y1="13" x2="8" y2="13"></line>
              <line x1="16" y1="17" x2="8" y2="17"></line>
              <polyline points="10 9 9 9 8 9"></polyline></svg
            >Organization Description:</label
          >
          <textarea
            id="orgDescription"
            v-model="modifiedOrganization.orgDescription"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-accent focus:border-transparent transition duration-200 min-h-[120px]"
          ></textarea>
        </div>

        <!-- Image Upload -->
        <div class="space-y-2">
          <label class="flex items-center text-lg font-medium text-gray-700">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="w-4 h-4 mr-2 text-accent"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
              <circle cx="8.5" cy="8.5" r="1.5"></circle>
              <polyline points="21 15 16 10 5 21"></polyline>
            </svg>
            Profile Picture</label
          >
          <input
            type="file"
            accept="image/jpeg"
            @change="handleImageUpload"
            class="hidden"
            ref="fileInput"
          />
          <button
            @click="triggerFileInput"
            class="flex items-center justify-center w-full px-4 py-2 border-2 border-dashed border-gray-300 rounded-lg hover:border-secondary hover:bg-blue-50 transition duration-200"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="w-5 h-5 mr-2 text-accent"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
              <polyline points="17 8 12 3 7 8"></polyline>
              <line x1="12" y1="3" x2="12" y2="15"></line>
            </svg>
            <span class="text-sm font-medium text-gray-600">
              {{ imageUploaded ? "Image Uploaded" : "Upload Image" }}
            </span>
          </button>
        </div>

        <!-- Submit Button -->
        <div class="flex justify-center">
          <button
            @click="submitForm()"
            class="w-full bg-primary text-white font-medium py-3 px-4 rounded-lg hover:bg-secondary transition duration-200 flex items-center justify-center space-x-2"
          >
            Submit
          </button>
        </div>
        <NotificationComponent
          class="flex"
          :show="notification.show"
          :type="notification.type"
          @close="closeNotification"
        >
          {{ notification.message }}
        </NotificationComponent>
      </div>
    </div>
  </div>
</template>

<script>
import NavBar from "@/components/NavBar.vue";
import { mapState } from "vuex";
import imageCompression from "browser-image-compression";
import NotificationComponent from "@/components/NotificationComponent.vue";

export default {
  data() {
    return {
      modifiedOrganization: {
        orgName: "",
        orgDescription: "",
        orgLogo: "",
        orgUsers: [],
        orgOwnerID: "",
        members: [],
      },
      imageUploaded: false,
      notification: {
        show: false,
        type: "info",
        message: "",
      },
    };
  },
  components: {
    NavBar,
    NotificationComponent,
  },
  computed: {
    ...mapState("auth", ["isLoggedIn", "currentUser"]),
  },
  async created() {
    try {
      const orgID = this.$route.params.orgIndex; // Ensure you are getting the correct orgID
      this.modifiedOrganization = await this.$store.dispatch(
        "organizations/fetchOrganization",
        orgID,
      );
      console.log("Fetched Organization:", this.modifiedOrganization); // Log the fetched organization
    } catch (error) {
      console.error("Error fetching organization data:", error);
      alert("Failed to load organization data: " + error.message);
      this.$router.push({ name: "viewOrgs", params: { orgIndex: undefined } });
    }
  },
  methods: {
    handleImageChange(event) {
      this.projImg = event.target.files[0];
    },
    triggerFileInput() {
      this.$refs.fileInput.click();
    },
    async handleImageUpload(event) {
      event.preventDefault(); // Prevent the default form submission behavior
      const file = event.target.files[0];
      if (file) {
        try {
          // options for image compression
          const options = {
            maxSizeMB: 0.512,
            maxWidthOrHeight: 1024,
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

              // Calculate the dimensions for the 16:10 aspect ratio crop
              const targetAspectRatio = 16 / 10;
              if (aspectRatio > targetAspectRatio) {
                newWidth = imageHeight * targetAspectRatio;
                newHeight = imageHeight;
              } else {
                newWidth = imageWidth;
                newHeight = imageWidth / targetAspectRatio;
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
                this.modifiedOrganization.OrgLogo = e.target.result;
                this.imageUploaded = true;
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
            "An error occurred while compressing the image. Please try again with a new file.",
          );
        }
      } else {
        this.showNotification("error", "Please select a valid image format.");
      }
    },
    async submitForm() {
      // Check if the current user is the creator of the project. If not, they may not edit it.
      if (this.modifiedOrganization.orgOwnerID !== this.currentUser.userID) {
        console.log(
          this.modifiedOrganization.orgOwnerID,
          this.currentUser.userID,
        );
        this.showNotification(
          "error",
          "Error determining your identity! Please log out and back in to continue.",
        );
        return;
      }

      // Get the index of the organization
      const organizationID = this.modifiedOrganization.orgID;

      // Modify the project in the organization

      try {
        this.$store.dispatch("organizations/modifyOrganization", {
          orgID: organizationID,
          organization: this.modifiedOrganization,
        });
        this.$router.push({ name: "viewOrgs" });
      } catch (error) {
        this.showNotification(
          "error",
          "An error occurred while updating the organization. Please try again.",
        );
      }
    },
    showNotification(type, message) {
      this.notification = {
        show: true,
        type,
        message,
      };

      if (type == "success") {
        setTimeout(this.closeNotification, 5000);
      }
    },
    closeNotification() {
      this.notification.show = false;
    },
  },
};
</script>
