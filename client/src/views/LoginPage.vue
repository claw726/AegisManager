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
              <input type="text" class="w-full border border-gray-300 rounded-lg p-3" />
            </div>
            <!-- Last Name -->
            <div class="w-full md:w-1/2 px-4 mb-4">
              <label class="block text-sm font-semibold text-gray-800 mb-2">Last Name</label>
              <input type="text" class="w-full border border-gray-300 rounded-lg p-3" />
            </div>
            <!-- Email Address -->
            <div class="w-full md:w-1/2 px-4 mb-4">
              <label class="block text-sm font-semibold text-gray-800 mb-2">Email Address</label>
              <input type="email" class="w-full border border-gray-300 rounded-lg p-3" />
            </div>

            <!-- Profile Picture Upload -->
            <div class="w-full md:w-1/2 px-4 mb-4">
              <label class="block text-sm font-semibold text-gray-800 mb-2">Profile Picture</label>
              <input type="file" accept="image/jpeg" @change="handleFileUpload" class="hidden" ref="fileInput" />
              <button @click="triggerFileInput" class="w-full bg-gray-800 text-white font-semibold py-3 rounded-lg">Upload Image</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    data() {
      return {
        user: {
          profilePicture: '',
        },
      };
    },
    methods: {
      triggerFileInput() {
        this.$refs.fileInput.click();
      },
      handleFileUpload(event) {
        const file = event.target.files[0];
        if (file && file.type === 'image/jpeg') {
          const reader = new FileReader();
          reader.onload = (e) => {
            this.user.profilePicture = e.target.result;
            this.createUserJson();
          };
          reader.readAsDataURL(file);
        } else {
          alert('Please select a JPG image.');
        }
      },
      createUserJson() {
        const userJson = JSON.stringify(this.user, null, 2);
        console.log('User JSON:', userJson);
        // You can further process the JSON or display it as needed
      },
    },
  };
  </script>
  
  <style scoped>
  /* Add any additional styles here if needed */
  </style>