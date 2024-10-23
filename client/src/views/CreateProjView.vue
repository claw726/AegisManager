<template>
  <div
    v-if="isLoggedIn"
    class="relative w-full h-full min-h-screen bg-background"
  >
    <NavBar />
    <div class="">
      <div class="flex flex-col justify-center py-16">
        <div class="text-4xl font-bold text-primary text-center py-8">
          Create New Project
        </div>
        <div class="h-1 bg-accent rounded-lg"></div>
        <div class="py-16">
          <div
            class="relative flex flex-col justify-items-center p-16 mx-24 rounded-lg bg-white drop-shadow-lg"
          >
            <div>
              <!-- Proj Title -->
              <div class="flex flex-col justify-center p-4 bg-white">
                <label for="projName" class="text-lg font-bold text-gray-800"
                  >Project Name:</label
                >
                <input
                  type="text"
                  data-testid="projName"
                  id="projName"
                  v-model="newProj.projName"
                  class="w-full border border-gray-300 rounded-lg p-2"
                />
              </div>

              <!-- Proj Desc -->
              <div class="flex flex-col justify-center p-4 bg-white">
                <label
                  for="projDescription"
                  class="text-lg font-bold text-gray-800"
                  >Project Description:</label
                >
                <textarea
                  id="projDescription"
                  v-model="newProj.projDescription"
                  class="w-full border border-gray-300 rounded-lg p-2"
                ></textarea>
              </div>

              <!-- Proj Img -->
              <div class="w-full md:w-1/2 px-4 mb-4 justify-center">
                <label class="block text-sm font-semibold text-gray-800 mb-2"
                  >Project Picture</label
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
                  class="flex-col flex px-8 bg-primary text-white font-semibold py-3 rounded-lg"
                >
                  Upload Image
                  <span v-if="imageUploaded" class="text-gray-500 ml-2"
                    >(Image Uploaded)</span
                  >
                </button>
              </div>

              <!-- Submit Form -->
              <div class="flex justify-center">
                <button
                  data-testid="submit"
                  @click="submitForm"
                  class="w-full mt-4 bg-primary text-white font-semibold py-3 rounded-lg"
                >
                  Submit
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import NavBar from "@/components/NavBar.vue";
import { mapState } from "vuex";
import imageCompression from "browser-image-compression";

export default {
  data() {
    return {
      newProj: {
        projCreator: "",
        projName: "",
        projDescription: "",
        projImg: "",
        projUsers: "",
        parentOrgID: "",
      },
      imageUploaded: false,
    };
  },
  components: {
    NavBar,
  },
  computed: {
    ...mapState(["isLoggedIn", "currentUser"]),
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
                this.newProj.projImg = e.target.result;
                this.imageUploaded = true;
              };
              reader.readAsDataURL(compressedCroppedFile);
            };
            image.src = imageDataUrl;
          };
          reader.readAsDataURL(file);
        } catch (error) {
          console.error("Error compressing image:", error);
          alert(
            "An error occurred while compressing the image. Please try again with a new file.",
          );
        }
      } else {
        alert("Please select a valid image format.");
      }
    },
    async submitForm() {
      // Add the new org to localstorage
      if (!this.newProj.projName || !this.newProj.projDescription) {
        alert("Please Tell us more about your project.");
        return;
      }

      const project = {
        projName: this.newProj.projName,
        projDescription: this.newProj.projDescription,
        projCreator: this.currentUser.userID,
        parentOrgID: this.$route.params.orgIndex,
        projImg: this.newProj.projImg,
      };

      // Add the store to the localStore
      try {
        await this.$store.dispatch("createProject", project);

        // Redirect to the Org Dashboard
        this.$router.push({
          name: "OrganizationDashboard",
          params: { index: this.$route.params.orgIndex },
        });
      } catch (error) {
        console.error("Error creating project:", error);
        alert(
          "An error occurred while creating the project. Please try again.",
        );
      }
    },
  },
};
</script>
