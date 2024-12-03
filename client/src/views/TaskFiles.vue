<template>
  <div class="min-h-screen bg-gray-50">
    <NavBar />

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- Header Section -->
      <div class="mb-8">
        <div class="flex items-center justify-between bg-white p-6 rounded-lg shadow-md">
          <div>
            <h1 class="text-3xl font-bold text-gray-900 flex items-center">
              <i class="fas fa-folder-open mr-3 text-blue-500"></i>
              Task Files
            </h1>
            <p class="mt-2 text-sm text-gray-500">
              Manage all files associated with this task.
            </p>
          </div>
          <label class="flex items-center space-x-2 px-4 py-2 text-white rounded-lg bg-blue-600 hover:bg-blue-700 cursor-pointer transition duration-200">
            <i class="fas fa-upload"></i>
            <span>Upload File</span>
            <input type="file" class="hidden" @change="handleFileUpload" />
          </label>
        </div>
      </div>

      <!-- Notification Component -->
      <NotificationComponent class="mb-6" :show="notification.show" :type="notification.type"
        @close="closeNotification">
        {{ notification.message }}
      </NotificationComponent>

      <!-- Files Grid -->
      <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
        <div v-for="file in files" :key="file.fileID"
          class="bg-white rounded-lg shadow-md border border-gray-200 hover:shadow-lg transition-duration-200 p-6 flex flex-col">
            <!-- File Icon -->
             <div class="flex justify-center mb-4">
              <i :class="[
                getFileIcon(file.fileType),
                getFileColor(file.fileType),
                'text-5xl transition-all duration-200 group-hover:scale-110'
              ]"></i>
            </div>

            <!-- File Info -->
            <div class="text-center mb-4">
              <h3 class="font-medium text-gray-900 truncate mb-1" :title="file.fileName">
                {{ file.fileName }}
              </h3>
              <div class="text-sm text-gray-500 space-y-1">
                <p>{{ getMimeTypeDescription(file.fileType) }}</p>
                <p>{{ formatFileSize(file.fileSize) }}</p>
                <p class="text-xs">Uploaded by: {{ uploaderEmails[file.fileID] || "Loading..." }}</p>
              </div>
            </div>

            <!-- Action Buttons -->
            <div class="flex justify-center space-x-2 mt-4">
              <button class="p-2 text-blue-600 hover:bg-blue-50 rounded-full transition duration-200"
                @click="downloadFile(file)"
                title="Download">
                <i class="fas fa-download"/>
              </button>
              <button v-if="hasDeletePermissions(file)"
                class="py-2 text-red-600 hover:bg-red-50 rounded-full transition duration-200"
                @click="setFileToDelete(file)"
                title="Delete">
                <i class="fas fa-trash"/>
              </button>
            </div>
          </div>

        <!-- Empty State -->
        <div v-if="files.length === 0" class="col-span-full text-center py-12 bg-white rounded-xl shadow-sm border border-gray-200">
          <i class="fas fa-inbox text-gray-400 text-5xl mb-4"></i>
          <h3 class="text-lg font-medium text-gray-900 mb-2">No Files</h3>
          <p class="text-gray-500">There have been no files uploaded to this task.</p>
        </div>
      </div>
    </div>
  </div>


  <!-- Delete Confirmation Modal -->
  <div v-if="showPopup" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
  <div class="bg-white rounded-lg p-6 max-w-sm w-full mx-4 shadow-lg">
    <h3 class="text-lg font-medium text-gray-900 mb-4 flex items-center">
      <i class="fas fa-exclamation-triangle text-yellow-500 mr-2"></i>
      Confirm Deletion
    </h3>
    <p class="text-gray-500 mb-6">
      Are you sure you want to delete this file? This action cannot be undone.
    </p>
    <div class="flex justify-end space-x-3">
      <button
        class="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200 transition duration-200"
        @click="handleNo">
        Cancel
      </button>
      <button
        class="px-4 py-2 text-sm font-medium text-white bg-red-600 rounded-lg hover:bg-red-700 transition duration-200"
        @click="handleYes">
        Delete
      </button>
    </div>
  </div>
</div>
</template>

<script>
import NavBar from "../components/NavBar.vue";
import { mapState } from "vuex";
import NotificationComponent from "@/components/NotificationComponent.vue";
import { FILE_TYPE_DESCRIPTIONS } from "@/constants/fileTypes.js";
import { getFileIcon, getFileColor, getFileCategory } from "@/constants/fileIcons.js";

export default {
  name: "TaskFiles",
  components: {
    NavBar,
    NotificationComponent,
  },
  data() {
    return {
      files: [],
      fetchedTask: null,
      uploaderEmails: [],
      notification: {
        show: false,
        type: "success",
        message: "",
      },
      showPopup: false,
      fileToDelete: null,
    };
  },
  computed: {
    ...mapState("auth", ["isLoggedIn", "currentUser"]),
  },
  async created() {
    this.fetchedTask = await this.$store.dispatch(
      "tasks/fetchTask",
      this.$route.params.taskId
    );
    console.log("Client has stored fetched task.");
    console.log(this.fetchedTask);
    await this.getTaskFiles();
    this.files.forEach(file => {
      if (!this.uploaderEmails[file.fileID]) {
        this.getEmailFromID(file.uploaderID, file.fileID);
      }
    });
  },
  methods: {
    async getTaskFiles() {
      try {
        const rawFiles = await this.$store.dispatch("tasks/fetchAllFiles", this.$route.params.taskId)
        this.files = rawFiles.map((file) => ({
          fileData: file.fileData,
          fileID: file.fileID,
          fileName: file.fileName,
          fileType: file.fileType,
          fileSize: file.fileSize,
          taskID: file.taskID,
          uploaderID: file.uploaderID
        }))
      }
      catch (error) {
        this.showNotification("error", "Error retrieving files");
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
    async handleFileUpload(event) {
      try {
        const file = event.target.files[0];
        const MAX_FILE_SIZE = 50 * 1024 * 1024; //50MB
        if (file.size > MAX_FILE_SIZE) {
          this.showNotification("error", "File size exceeds the 50 MB limit.");
          console.error("File size exceeds 50 MB");
          return;
        }

        const fileContents = await this.readFileAsBase64(file);
        if (file) {
          const data = {
            taskID: this.fetchedTask.taskID,
            fileName: file.name,
            fileType: file.type,
            fileSize: file.size,
            fileContents: fileContents.replace(/ /g, '+').trim(),
            uploaderID: this.currentUser.userID,
          };
          await this.$store.dispatch("tasks/addFile", data);
          console.log("File successfully added to the task!");
          this.showNotification("success", "File successfully added to the task");
          await this.getTaskFiles();
          await this.files.forEach(file => {
            if (!this.uploaderEmails[file.fileID]) {
              this.getEmailFromID(file.uploaderID, file.fileID);
            }
          });
        }
      } catch (error) {
        console.error("Error adding file to task:", error.message);
        this.showNotification("error", error.message || "Error adding file to the task");
      } finally {
        event.target.value = null;
      }
    },
    readFileAsBase64(file) {
      return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onload = () => resolve(reader.result); // Resolve with file contents
        reader.onerror = (error) => reject(error); // Reject in case of an error
        reader.readAsDataURL(file); // Read the file as a Base64 string
      });
    },
    async getEmailFromID(uploaderID, fileID) {
      try {
        const user = await this.$store.dispatch(
          "users/fetchUserAccountByID",
          uploaderID,
        );
        console.log("USER");
        console.log(user.email);
        this.uploaderEmails[fileID] = user.email;
      } catch (error) {
        this.uploaderEmails[fileID] = "Unknown";
      }
    },
    hasDeletePermissions(file) {
      return (file.uploaderID === this.currentUser.userID || this.fetchedTask.assignerID === this.currentUser.userID)
    },
    setFileToDelete(file) {
      this.fileToDelete = file;
      console.log(file.fileSize);
      this.showPopup = true;
    },
    handleYes() {
      this.showPopup = false;
      this.deleteFile();
    },
    handleNo() {
      this.showPopup = false;
    },
    async deleteFile() {
      try {
        const data = {
          taskID: this.fetchedTask.taskID,
          fileID: this.fileToDelete.fileID,
        };
        await this.$store.dispatch("tasks/deleteFile", data);

        this.showNotification("success", "File successfully deleted!");
        await this.getTaskFiles();
        await this.files.forEach(file => {
          if (!this.uploaderEmails[file.fileID]) {
            this.getEmailFromID(file.uploaderID, file.fileID);
          }
        });
      } catch (error) {
        console.error("Failed to delete task:");
        console.error(error);
      }

    },
    async downloadFile(file) {
      try {
        const byteCharacters = atob(file.fileData);
        const byteArrays = [];

        for (let offset = 0; offset < byteCharacters.length; offset += 1024) {
          const slice = byteCharacters.slice(offset, offset + 1024);
          const byteNumbers = new Array(slice.length);
          for (let i = 0; i < slice.length; i++) {
            byteNumbers[i] = slice.charCodeAt(i);
          }
          byteArrays.push(new Uint8Array(byteNumbers));
        }

        const blob = new Blob(byteArrays, { type: file.fileType });

        const link = document.createElement("a");
        link.href = URL.createObjectURL(blob);
        link.download = file.fileName;
        document.body.appendChild(link);

        link.click();

        document.body.removeChild(link);
        URL.revokeObjectURL(link.href);

        console.log("File downloaded successfully!");
      } catch (error) {
        console.error("Error downloading the file:", error);
      }
    },
    formatFileSize(fileSize) {
      if (!fileSize || isNaN(fileSize)) {
        console.error("Invalid file size:", fileSize);
        return "Unknown Size";
      }
      if (fileSize < 1000) {
        return fileSize + " B"
      }
      else if (fileSize < 1000000) {
        return Math.floor(fileSize / 1000) + " KB"
      }
      else {
        return Math.floor(fileSize / 1000000) + " MB"
      }
    },
    getMimeTypeDescription(fileType) {
      return FILE_TYPE_DESCRIPTIONS.get(fileType) || "Unknown File Type";
    },
    getFileIcon,
    getFileColor,

  },
};
</script>

<style scoped>
/* Animation for notification badges */
@keyframes pulse {

  0%,
  100% {
    opacity: 1;
  }

  50% {
    opacity: .5;
  }
}

.notification-badge {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}
.file-name {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.action-button {
  @apply p-2 rounded-full transition duration-200;
}

.action-button:hover {
  @apply bg-gray-100;
}
</style>