<template>
  <div
    class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
  >
    <div class="bg-white rounded-lg p-6 w-[480px] max-h-[90vh] flex flex-col">
      <!-- Header -->
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-xl font-semibold">New Conversation</h2>
        <button
          class="text-gray-500 hover:text-gray-700"
          @click="$emit('close')"
        >
          <i class="fas fa-times"></i>
        </button>
      </div>

      <!-- Chat Type Selection -->
      <div class="mb-4">
        <div class="flex gap-2">
          <button
            v-for="type in chatTypes"
            :key="type.value"
            :class="[
              'flex-1 py-2 px-4 rounded-lg font-medium transition-colors',
              chatType === type.value
                ? 'bg-blue-500 text-white'
                : 'bg-gray-100 text-gray-600 hover:bg-gray-200',
            ]"
            @click="chatType = type.value"
          >
            <i :class="[type.icon, 'mr-2']"></i>
            {{ type.label }}
          </button>
        </div>
      </div>

      <!-- Organization Filter -->
      <div v-if="organizations?.length > 0" class="mb-4">
        <label class="block text-sm font-medium text-gray-700 mb-1"
          >Organization</label
        >
        <select
          v-model="selectedOrg"
          class="w-full border-gray-300 rounded-lg shadow-sm focus:border-blue-500 focus:ring-blue-500"
        >
          <option value="">All Organizations</option>
          <option v-for="org in organizations" :key="org.id" :value="org.id">
            {{ org.name }}
          </option>
        </select>
      </div>

      <!-- Search Input -->
      <div class="mb-4">
        <div class="relative">
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Search by name or email..."
            class="w-full pl-10 pr-4 py-2 border-gray-300 rounded-lg shadow-sm focus:border-blue-500 focus:ring-blue-500"
            @input="handleSearch"
          />
          <i
            class="fas fa-search absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"
          ></i>
        </div>
      </div>

      <!-- Selected Users Pills -->
      <div v-if="selectedUsers.length > 0" class="flex flex-wrap gap-2 mb-4">
        <div
          v-for="user in selectedUsers"
          :key="user.id"
          class="bg-blue-100 text-blue-700 px-3 py-1 rounded-full flex items-center gap-2"
        >
          <!-- Small Profile Picture in Pill -->
          <div class="w-6 h-6 rounded-full overflow-hidden flex-shrink-0">
            <img
              v-if="user.profilePicture"
              :src="user.profilePicture"
              :alt="user.userName || user.name"
              class="w-full h-full object-cover"
              @error="handleImageError"
            />
            <div
              v-else
              class="w-full h-full bg-blue-200 flex items-center justify-center text-blue-700"
            >
              <span class="text-xs font-medium">
                {{ getInitials(user.userName || user.name) }}
              </span>
            </div>
          </div>

          <span class="truncate">{{ user.userName || user.name }}</span>
          <button
            class="text-blue-500 hover:text-blue-700"
            @click="removeUser(user)"
          >
            <i class="fas fa-times-circle"></i>
          </button>
        </div>
      </div>

      <div class="flex-1 overflow-y-auto min-h-[200px] border rounded-lg">
        <div
          v-if="loading"
          class="h-full flex items-center justify-center text-gray-500"
        >
          <i class="fas fa-spinner fa-spin mr-2"></i>
          Loading users...
        </div>
        <div
          v-else-if="error"
          class="h-full flex flex-col items-center justify-center text-red-500 p-4"
        >
          <i class="fas fa-exclamation-circle mb-2"></i>
          <p class="text-center mb-2">{{ error }}</p>
          <button
            class="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
            @click="loadUsers"
          >
            Try Again
          </button>
        </div>
        <div
          v-else-if="filteredUsers.length === 0"
          class="h-full flex items-center justify-center text-gray-500"
        >
          No users found
        </div>
        <div v-else class="divide-y">
          <div
            v-for="user in filteredUsers"
            :key="user.userID"
            :class="[
              'p-3 flex items-center gap-3 cursor-pointer hover:bg-gray-50',
              isSelected(user) ? 'bg-blue-50' : '',
            ]"
            @click="toggleUser(user)"
          >
            <!-- Profile Picture -->
            <div class="w-10 h-10 rounded-full overflow-hidden flex-shrink-0">
              <img
                v-if="user.profilePicture"
                :src="user.profilePicture"
                :alt="user.userName || user.name"
                class="w-full h-full object-cover"
                @error="handleImageError"
              />
              <div
                v-else
                class="w-full h-full bg-gray-200 flex items-center justify-center text-gray-600"
              >
                <span class="text-lg font-medium">
                  {{ getInitials(user.userName || user.name) }}
                </span>
              </div>
            </div>
            <!-- User Info -->
            <div class="flex-1 min-w-0">
              <div class="font-medium truncate">
                {{ user.userName || user.name }}
              </div>
              <div class="text-sm text-gray-500 truncate">{{ user.email }}</div>
            </div>

            <!-- Selected Check Icon -->
            <div v-if="isSelected(user)" class="text-blue-500">
              <i class="fas fa-check-circle"></i>
            </div>
          </div>
        </div>
      </div>

      <!-- Footer -->
      <div class="mt-4 flex justify-end gap-2">
        <button
          class="px-4 py-2 text-gray-600 hover:bg-gray-100 rounded-lg"
          @click="$emit('close')"
        >
          Cancel
        </button>
        <button
          :disabled="!isValid"
          :class="[
            'px-4 py-2 rounded-lg',
            isValid
              ? 'bg-blue-500 text-white hover:bg-blue-600'
              : 'bg-gray-200 text-gray-500 cursor-not-allowed',
          ]"
          @click="handleCreateChat"
        >
          Create Chat
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState, mapActions, mapGetters } from "vuex";

export default {
  name: "NewChatModal",
  data() {
    return {
      chatType: "direct",
      selectedUsers: [],
      searchQuery: "",
      selectedOrg: "",
      loading: true,
      error: null,
      chatTypes: [
        {
          value: "direct",
          label: "Direct Message",
          icon: "fas fa-user",
        },
        {
          value: "group",
          label: "Group Chat",
          icon: "fas fa-users",
        },
      ],
    };
  },

  computed: {
    ...mapState("chat", ["users", "organizations"]),
    ...mapState("auth", ["currentUser"]),
    filteredUsers() {
      // Start with all users except current user
      let filtered = (this.users || []).filter(
        (user) => user.userID !== this.currentUser?.userID,
      );

      // Filter by organization if selected
      if (this.selectedOrg) {
        filtered = filtered.filter((user) => user.orgId === this.selectedOrg);
      }

      // Filter by search query
      if (this.searchQuery.trim()) {
        const query = this.searchQuery.toLowerCase();
        filtered = filtered.filter(
          (user) =>
            user.userName?.toLowerCase().includes(query) ||
            user.email?.toLowerCase().includes(query),
        );
      }

      return filtered;
    },

    isValid() {
      if (this.chatType === "direct") {
        return this.selectedUsers.length === 1;
      }
      return this.selectedUsers.length >= 2;
    },

    chatTitleComputed() {
      if (this.chatType === "direct") {
        // For direct messages, use the other user's name
        return (
          this.selectedUsers[0]?.userName || this.selectedUsers[0]?.name || ""
        );
      }
      // For group chats, use the user-entered title or generate one from participants
      return this.chatTitle || this.generateDefaultGroupTitle();
    },
  },

  async created() {
    console.log("NewChatModal created");
    await this.loadUsers();
  },

  mounted() {
    console.log("Users in store:", this.users);
  },
  methods: {
    ...mapActions("chat", ["fetchUsers", "createNewChat"]),

    async loadUsers() {
      try {
        this.loading = true;
        this.error = null;
        await this.fetchUsers();
        console.log("Users loaded:", this.users);
      } catch (error) {
        this.error = "Failed to load users. Please try again.";
        console.log("Error loading users:", error);
      } finally {
        this.loading = false;
      }
    },

    handleSearch() {
      // In real implementation, this could debounce API calls
      this.loading = true;
      setTimeout(() => {
        this.loading = false;
      }, 300);
    },

    toggleUser(user) {
      if (this.isSelected(user)) {
        this.removeUser(user);
      } else {
        if (this.chatType === "direct" && this.selectedUsers.length === 1) {
          this.selectedUsers = [user];
        } else {
          this.selectedUsers.push(user);
        }
      }
    },

    removeUser(user) {
      this.selectedUsers = this.selectedUsers.filter(
        (u) => u.userID !== user.userID,
      );
    },

    isSelected(user) {
      return this.selectedUsers.some((u) => u.userID === user.userID);
    },

    async handleCreateChat() {
      if (!this.isValid) return;

      try {
        this.loading = true;
        this.error = null;

        const chatData = {
          type: this.chatType,
          participants: new Set([
            ...this.selectedUsers.map((u) => Number(u.userID)),
            this.currentUser.userID,
          ]),
          title: this.chatTitleComputed,
        };

        console.log("Creating chat with data:", chatData);

        await this.createNewChat(chatData);

        // Close modal and reset form
        this.$emit("close");
        this.resetForm();
      } catch (error) {
        console.error("Failed to create chat:", error);
        this.error =
          error.response?.data || "Failed to create chat. Please try again.";
      } finally {
        this.loading = false;
      }
    },

    resetForm() {
      this.chatType = "direct";
      this.selectedUsers = [];
      this.chatTitle = "";
      this.searchQuery = "";
      this.selectedOrg = "";
      this.error = null;
    },

    getUserSubtitle(user) {
      return user.title || user.email;
    },

    getInitials(name) {
      if (!name) return "?";
      return name
        .split(" ")
        .map((word) => word[0])
        .join("")
        .toUpperCase()
        .slice(0, 2);
    },

    handleImageError(event) {
      // Replace broken image with default avatar background
      event.target.style.display = "none";
      event.target.parentElement.innerHTML = `
            <div class="w-full h-full bg-gray-200 flex items-center justify-center text-gray-600">
              <i class="fas fa-user"></i>
            </div>
          `;
    },
    generateDefaultGroupTitle() {
      return (
        this.selectedUsers
          .slice(0, 3)
          .map((user) => user.userName || user.name)
          .join(", ") + (this.selectedUsers.length > 3 ? "..." : "")
      );
    },
  },
};
</script>

<style scoped>
/* Add any specific styles here */
.user-list-enter-active,
.user-list-leave-active {
  transition: all 0.3s ease;
}

.user-list-enter-from,
.user-list-leave-to {
  opacity: 0;
  transform: translateY(30px);
}
</style>
