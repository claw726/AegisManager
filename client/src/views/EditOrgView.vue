<template>
  <div
    v-if="isLoggedIn"
    class="relative w-full h-full min-h-screen bg-background"
  >
    <NavBar />
    <div class="">
      <div class="flex flex-col justify-center h-screen/3 py-16">
        <div class="text-4xl font-bold text-primary text-center py-8">
          Edit Organization
        </div>
        <div class="h-1 bg-accent rounded-lg"></div>
        <div class="py-16">
          <div
            class="relative flex flex-col justify-items-center p-16 mx-96 rounded-lg bg-white drop-shadow-lg"
          >
            <div>
              <!-- Proj Title -->
              <div class="flex flex-col justify-center p-4 bg-white">
                <label for="projName" class="text-lg font-bold text-gray-800"
                  >Organization Name:</label
                >
                <input
                  type="text"
                  id="projName"
                  v-model="modifiedOrganization.OrgName"
                  class="w-full border border-gray-300 rounded-lg p-2"
                />
              </div>

              <!-- Proj Desc -->
              <div class="flex flex-col justify-center p-4 bg-white">
                <label
                  for="projDescription"
                  class="text-lg font-bold text-gray-800"
                  >Organization Description:</label
                >
                <textarea
                  id="projDescription"
                  v-model="modifiedOrganization.OrgDescription"
                  class="w-full border border-gray-300 rounded-lg p-2"
                ></textarea>
              </div>

              <!-- Proj Users -->
              <div class="flex flex-col justify-center p-4 bg-white">
                <label
                  for="projDescription"
                  class="text-lg font-bold text-gray-800"
                  >Organizaiton Users:</label
                >
                <Button class="dashboard-button" @click="selectUsers"
                  >Select Users</Button
                >
              </div>

              <!-- Proj Img -->
              <div class="w-full md:w-1/2 px-4 mb-4 justify-center">
                <label class="block text-sm font-semibold text-gray-800 mb-2"
                  >Organization Picture</label
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
      currentOrganization: null,
      modifiedOrganization: {
        OrgName: "",
        OrgDescription: "",
        OrgLogo: "",
        OrgUsers: [],
        OrgCreator: "",
        members: [],
      },
      imageUploaded: false,
    };
  },
  components: {
    NavBar,
  },
  computed: {
    ...mapState(["isLoggedIn", "currentUser", "organizations"]),
  },
  created() {
    this.modifiedOrganization = this.organizations[this.$route.params.orgIndex];
  },
  methods: {
    handleImageChange(event) {
      this.projImg = event.target.files[0];
    },
    selectUsers() {
      alert("Not Implementing. Selecting all users");
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
          alert(
            "An error occurred while compressing the image. Please try again with a new file.",
          );
        }
      } else {
        alert("Please select a valid image format.");
      }
    },
    async submitForm() {
      // Check if the current user is the creator of the project. If not, they may not edit it.
      if (this.modifiedOrganization.OrgCreator !== this.currentUser.email) {
        console.log(
          this.modifiedOrganization.OrgCreator,
          this.currentUser.email,
        );
        alert("You are not authorized to edit this project.");
        return;
      }

      // Get the index of the organization
      const organizationID = this.$route.params.orgIndex;

      //Ensure this is a valid organization
      const organization = this.organizations[organizationID];
      if (!organization) {
        alert("Error getting the organization details!");
        return;
      }

      // Modify the project in the organization
      await this.$store
        .dispatch("modifyOrganization", {
          index: organizationID,
          organization: this.modifiedOrganization,
        })
        .then(() => {
          alert("Organization updated successfully!");
          this.$router.push({ name: "viewOrgs" });
        })
        .catch((err) => {
          alert("Failed to update project");
          console.error(err);
        });
    },
  },
};
</script>
