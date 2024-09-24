<template>
    <div class="relative w-full min-h-screen bg-white">
      <!-- Navbar -->
      <div class="w-full h-20 border-b border-gray-300 flex items-center justify-between px-6 bg-white">
        <div class="text-lg font-bold text-gray-800">Sign Up</div>
        <div class="flex space-x-4">
          <button class="border-2 border-gray-800 text-gray-800 font-semibold py-2 px-4 rounded">Log In</button>
          <button class="bg-gray-800 text-white font-semibold py-2 px-4 rounded">Sign Up</button>
        </div>
      </div>
  
      <!-- Main Content -->
      <div class="flex flex-col items-center mt-16">
        <div class="text-3xl font-bold text-gray-800">Create Profile</div>
        <div class="text-xl font-semibold text-pink-500 mt-2">Register Securely with Aegis</div>
  
        <!-- Form Container -->
        <div class="w-3/4 max-w-4xl mt-10 bg-white shadow-lg rounded-lg p-8">
          <div class="flex flex-wrap -mx-4">
            <!-- First Name -->
            <div class="w-full md:w-1/2 px-4 mb-4">
              <label class="block text-sm font-semibold text-gray-800 mb-2">First Name</label>
              <input type="text" v-model="user.firstName" class="w-full border border-gray-300 rounded-lg p-3" />
            </div>
            <!-- Last Name -->
            <div class="w-full md:w-1/2 px-4 mb-4">
              <label class="block text-sm font-semibold text-gray-800 mb-2">Last Name</label>
              <input type="text" v-model="user.lastName" class="w-full border border-gray-300 rounded-lg p-3" />
            </div>
            <!-- Email Address -->
            <div class="w-full md:w-1/2 px-4 mb-4">
              <label class="block text-sm font-semibold text-gray-800 mb-2">Email Address</label>
              <input type="email" v-model="user.email" class="w-full border border-gray-300 rounded-lg p-3" />
            </div>

            <!-- Profile Picture Upload -->
            <div class="w-full md:w-1/2 px-4 mb-4">
              <label class="block text-sm font-semibold text-gray-800 mb-2">Profile Picture</label>
              <input type="file" accept="image/jpeg" @change="handleFileUpload" class="hidden" ref="fileInput" />
              <button @click="triggerFileInput" class="w-full bg-gray-800 text-white font-semibold py-3 rounded-lg">Upload Image
                <span v-if="imageUploaded" class="text-gray-500 ml-2">(Image Uploaded)</span>
              </button>
            </div>
            <!-- Password Input -->
            <div class="w-full flex justify-center px-4 mb-4">
                <div class="w-full md:w-1/2">
                  <PasswordInput @update-password="updatePassword" />
                </div>
              </div>
            <!-- Submit form button -->
            <button @click="submitForm" class="w-full mt-4 bg-blue-600 text-white font-semibold py-3 rounded-lg">Submit</button>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import PasswordInput from '@/components/PasswordCreator.vue';
  import imageCompression from 'browser-image-compression'
  export default {
    components: {
      PasswordInput,
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
    methods: {
      triggerFileInput() {
        this.$refs.fileInput.click();
      },
      async handleFileUpload(event) {
         const file = event.target.files[0];
         if (file) {
           try {
             const options = {
               maxSizeMB: 1,
               maxWidthOrHeight: 128,
               useWebWorker: true,
             };
             const compressedFile = await imageCompression(file, options);
             const reader = new FileReader();
             reader.onload = (e) => {
               this.user.profilePicture = e.target.result;
               this.imageUploaded = true;
             };
             reader.readAsDataURL(compressedFile);
           } catch (error) {
             console.error('Error compressing image:', error);
           }
         } else {
           alert('Please select a valid image format.');
         }
        },
      createUserJson() {
        const userJson = JSON.stringify(this.user, null, 2);
        console.log('User JSON:', userJson);
        // You can further process the JSON or display it as needed
      },
      submitForm() {
        if (!this.imageUploaded || !this.user.firstName || !this.user.lastName || !this.user.email) {
          alert('Please fill out all fields and upload an image.');
        } else {
          alert('Form submitted successfully!');
          console.log('User Data:', this.user);
        }
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