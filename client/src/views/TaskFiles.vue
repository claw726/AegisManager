<template>
  <div class="min-h-screen bg-gray-50">
    <NavBar />

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- Header Section -->
      <div class="mb-8">
        <div class="flex items-center justify-between">
          <div>
            <h1 class="text-3xl font-bold text-gray-900 flex items-center">
              <i class="fas fa-file mr-3 text-blue-600"></i>
              Task Files
            </h1>
            <p class="mt-2 text-sm text-gray-600">
              Manage all files associated with task
            </p>
          </div>
          <label class="px-4 py-2 upload-btn text-white rounded-lg bg-blue-600 hover:bg-blue-700 cursor-pointer">
            Upload File
            <input type="file" class="hidden" @change="handleFileUpload" />
          </label>
        </div>
      </div>



      <!-- Notification Component -->
      <NotificationComponent class="mb-6" :show="notification.show" :type="notification.type"
        @close="closeNotification">
        {{ notification.message }}
      </NotificationComponent>

      <!-- Files List -->
      <div class="space-y-4">
        <div v-for="file in files" :key="file.fileID"
          class="bg-white rounded-xl shadow-sm border border-gray-200 hover:shadow-md transition-shadow duration-200">
          <div class="p-6">
            <div class="flex items-center justify-between">
              <!-- Invitation Content -->
              <div class="flex-1 min-w-0">
                <div class="flex items-center mb-2">

                  <div class="flex-1 min-w-0" style="flex: 3 1 55%;">
                    <p class="text-lg font-semibold text-gray-900 truncate">
                      {{ file.fileName }}
                    </p>
                  </div>
                  <div class="flex-1 min-w-0" style="flex: 1 1 20%;">
                    <p class="text-lg text-gray-900 truncate">
                      {{ uploaderEmails[file.fileID] || "Loading..." }}
                    </p>
                  </div>
                  <div class="flex-1 min-w-0" style="flex: 1 1 15%;">
                    <p class="text-lg text-gray-900 truncate">
                      {{ getMimeTypeDescription(file.fileType) || "Loading..." }}
                    </p>
                  </div>
                  <div class="flex-1 min-w-0" style="flex: 1 1 10%;">
                    <p class="text-lg text-gray-900 truncate">
                      {{ formatFileSize(file.fileSize) || "Loading..." }}
                    </p>
                  </div>

                </div>
              </div>

              <!-- Action Buttons -->
              <label class="px-4 py-2 upload-btn text-white rounded-lg bg-blue-600 hover:bg-blue-700 cursor-pointer"
                @click="downloadFile(file)">
                Download
              </label>
              <!----
              <label v-if="hasDeletePermissions(file)"
                class="px-4 py-2 text-white rounded-lg bg-red-600 hover:bg-red-700 cursor-pointer"
                @click="deleteFile(file)">
                Delete
              </label>
            -->
              <label v-if="hasDeletePermissions(file)"
                class="px-4 py-2 text-white rounded-lg bg-red-600 hover:bg-red-700 cursor-pointer"
                @click="setFileToDelete(file)">
                Delete
              </label>

            </div>
            <div v-if="showPopup" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
              <div class="bg-white rounded-lg p-6 max-w-sm w-full mx-4">
                <h3 class="text-lg font-medium text-gray-900 mb-4">
                  <i class="fas fa-exclamation-triangle text-yellow-500 mr-2"></i>
                  Confirm Deletion
                </h3>
                <p class="text-gray-500 mb-6">
                  Are you sure you want to delete this file? This action cannot be undone.
                </p>
                <div class="flex justify-end space-x-3">
                  <button
                    class="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-500 transition-colors duration-200"
                    @click="handleNo">
                    Cancel
                  </button>
                  <button
                    class="px-4 py-2 text-sm font-medium text-white bg-red-600 rounded-lg hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 transition-colors duration-200"
                    @click="handleYes">
                    Delete
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div v-if="files.length === 0" class="text-center py-12 bg-white rounded-xl shadow-sm border border-gray-200">
          <i class="fas fa-inbox text-gray-400 text-5xl mb-4"></i>
          <h3 class="text-lg font-medium text-gray-900 mb-2">No Files</h3>
          <p class="text-gray-500">There have been no files uploaded to this task.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import NavBar from "../components/NavBar.vue";
import { mapState } from "vuex";
import NotificationComponent from "@/components/NotificationComponent.vue";

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
        const MAX_FILE_SIZE = 50 * 1024 * 1024;
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
      switch (fileType) {
        // Text Files
        case "text/plain":
          return "Plain Text";
        case "text/html":
          return "HTML";
        case "text/css":
          return "CSS";
        case "application/javascript":
          return "JavaScript";
        case "application/json":
          return "JSON";
        case "application/xml":
          return "XML";

        // Image Files
        case "image/jpeg":
          return "JPEG";
        case "image/png":
          return "PNG";
        case "image/gif":
          return "GIF";
        case "image/bmp":
          return "BMP";
        case "image/webp":
          return "WebP";
        case "image/svg+xml":
          return "SVG";
        case "image/tiff":
          return "TIFF";
        case "image/x-icon":
          return "Icon File";

        // Audio Files
        case "audio/mpeg":
          return "MP3";
        case "audio/wav":
          return "WAV";
        case "audio/ogg":
          return "OGG";
        case "audio/aac":
          return "AAC";
        case "audio/flac":
          return "FLAC";
        case "audio/midi":
          return "MIDI";

        // Video Files
        case "video/mp4":
          return "MP4";
        case "video/webm":
          return "WebM";
        case "video/ogg":
          return "OGG";
        case "video/x-msvideo":
          return "AVI";
        case "video/mpeg":
          return "MPEG";
        case "video/quicktime":
          return "QuickTime (MOV)";

        // Document Files
        case "application/pdf":
          return "PDF";
        case "application/msword":
          return "Word";
        case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
          return "Word";
        case "application/vnd.ms-excel":
          return "Excel";
        case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
          return "Excel";
        case "application/vnd.ms-powerpoint":
          return "PowerPoint";
        case "application/vnd.openxmlformats-officedocument.presentationml.presentation":
          return "PowerPoint";
        case "application/rtf":
          return "RTF";

        // Compressed Files
        case "application/zip":
          return "ZIP";
        case "application/vnd.rar":
          return "RAR";
        case "application/gzip":
          return "GZIP";
        case "application/x-7z-compressed":
          return "7z";
        case "application/x-tar":
          return "TAR";

        // Code Files
        case "text/x-python":
          return "Python";
        case "application/javascript":
          return "JavaScript";
        case "text/x-java-source":
          return "Java Source";
        case "text/x-c":
          return "C Source";
        case "text/x-c++src":
          return "C++ Source";

        // Application and Executable Files
        case "application/vnd.microsoft.portable-executable":
          return "Executable";
        case "application/x-apple-diskimage":
          return "Disk Image";
        case "application/vnd.android.package-archive":
          return "Package";

        // Font Files
        case "font/ttf":
          return "TTF";
        case "font/otf":
          return "OTF";
        case "font/woff":
          return "WOFF";
        case "font/woff2":
          return "WOFF2";

        // Other Common Types
        case "text/csv":
          return "CSV";
        case "application/x-yaml":
        case "text/yaml":
          return "YAML";
        case "text/markdown":
          return "Markdown";
        case "text/calendar":
          return "ICS";

        // Default case for unknown types
        default:
          return "File";
      }
    }

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
</style>