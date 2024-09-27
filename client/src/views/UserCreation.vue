<template>
  <div class="relative w-full h-screen bg-background">
    <!-- NavBar  -->
    <NavBar />

    <!-- Main Content -->
    <div v-if="!isLoggedIn" class="flex flex-col items-center mt-20"> <!-- Adjusted margin to ensure content is below the NavBar -->
      <div class="text-3xl font-bold text-primary">Create Profile</div>
      <div class="text-xl font-semibold text-secondary mt-2">Register Securely with Aegis</div>

      <!-- Form Container -->
      <div class="w-3/4 max-w-4xl mt-10 bg-white shadow-lg rounded-lg p-8">
        <div class="flex flex-wrap -mx-4">

          <!-- First Name -->
          <div class="w-full md:w-1/2 px-4 mb-4">
            <label class="block text-sm font-semibold text-gray-800 mb-2">First Name</label>
            <input type="text" v-model="user.firstName" class="w-full border border-highlight rounded-lg p-3" />
          </div>

          <!-- Last Name -->
          <div class="w-full md:w-1/2 px-4 mb-4">
            <label class="block text-sm font-semibold text-gray-800 mb-2">Last Name</label>
            <input type="text" v-model="user.lastName" class="w-full border border-highlight rounded-lg p-3" />
          </div>

          <!-- Email Address -->
          <div class="w-full md:w-1/2 px-4 mb-4">
            <label class="block text-sm font-semibold text-gray-800 mb-2">Email Address</label>
            <input type="email" v-model="user.email" class="w-full border border-highlight rounded-lg p-3" />
          </div>

          <!-- Profile Picture -->
          <div class="w-full md:w-1/2 px-4 mb-4">
            <label class="block text-sm font-semibold text-gray-800 mb-2">Profile Picture</label>
            <input type="file" accept="image/jpeg" @change="handleFileUpload" class="hidden" ref="fileInput" />
            <button @click="triggerFileInput" class="w-full bg-primary text-white font-semibold py-3 rounded-lg">
              Upload Image
              <span v-if="imageUploaded" class="text-gray-500 ml-2">(Image Uploaded)</span>
            </button>
          </div>

          <!-- Password Input -->
          <div class="w-full flex justify-center px-4 mb-4">
            <div class="w-full md:w-1/2">
              <PasswordInput @update-password="updatePassword" />
            </div>
          </div>
          
          <!-- Submit Button -->
          <button @click="submitForm" class="w-full mt-4 bg-primary text-white font-semibold py-3 rounded-lg">Submit</button>
        </div>
      </div>
    </div>
  </div>
</template>
  
  <script>
  import { mapState } from 'vuex';
  import NavBar from '@/components/NavBar.vue';
  import PasswordInput from '@/components/PasswordCreator.vue';
  import imageCompression from 'browser-image-compression'
  export default {
    components: {
      PasswordInput,
      NavBar,
    },
    computed: {
      ...mapState(['isLoggedIn']),
    },
    data() {
      return {
        user: {
          firstName: '',
          lastName: '',
          email: '',
          password: '',
          profilePicture: '',
        },
        imageUploaded: false, // Track if an image has been uploaded
      };
    },
    watch: {
      'user.email'(newEmail) {
        this.user.email = newEmail.trim();
        this.user.email = newEmail.toLowerCase();
      },
      'user.password'(newPassword) {
        this.user.password = newPassword.trim();
      },
    },
    methods: {
      triggerFileInput() {
        this.$refs.fileInput.click();
      },
      async handleFileUpload(event) {
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
                const canvas = document.createElement('canvas');
                const ctx = canvas.getContext('2d');
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
                ctx.drawImage(image, x, y, newWidth, newHeight, 0, 0, newWidth, newHeight);

                // Convert the Canvas to a URL
                const croppedDataURL = canvas.toDataURL('image/jpeg', 0.92);

                // Convert the data URL to a BLOB
                const blob = await fetch(croppedDataURL).then(res => res.blob());

                // Create a new file from the BLOB
                const newFile = new File([blob], file.name, { type: 'image/jpeg' });

                // Compress the cropped image
                const compressedCroppedFile = await imageCompression(newFile, options);

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
            console.error('Error compressing image:', error);
            alert('An error occurred while compressing the image. Please try again with a new file.');
          }
        } else {
          alert('Please select a valid image format.');
        }
      },
      createUserJson() {
        const userJson = JSON.stringify(this.user, null, 2);
        console.log('User JSON:', userJson);
        return userJson;
      },
      submitForm() {
        // TODO: when server is setup, request all user accounts and check if email already exists
        if (!this.imageUploaded || !this.user.firstName || !this.user.lastName || !this.user.email) {
          alert('Please fill out all fields and upload an image.');
          return;
        }
        // Validate email
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(this.user.email)) {
          alert('Please enter a valid email address.');
          return;
        } 

        let accounts = [];
        // Retrieve existing accounts from local storage
        const existingAccounts = localStorage.getItem('userAccounts');
        if (existingAccounts) {
          accounts = JSON.parse(existingAccounts);

          // Check if email already exists
          const emailExists = accounts.some((account) => account.email === this.user.email);
          if (emailExists) {
            alert('This email is already registered. Please use a different email.');
            return;
          }
        }
        // Add new account to existing accounts
        accounts.push(this.user);

        // make isLoggedIn true
        this.$store.dispatch('login');

        // Set the CurrentUser to the newly created account
        localStorage.setItem('CurrentUser', JSON.stringify(this.user));

        // Store updated accounts in local storage
        localStorage.setItem('userAccounts', JSON.stringify(accounts)); // Store user data in local storage

        // Redirect to the dashboard
        this.$router.push({ name: 'Dashboard' });
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