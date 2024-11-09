<template>
  <div
    class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
  >
    <div class="bg-white rounded-lg p-6 w-[480px] max-h-[90vh] flex flex-col">
      <!-- Header -->
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-xl font-semibold">New Conversation</h2>
        <button
          @click="$emit('close')"
          class="text-gray-500 hover:text-gray-700"
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
            @click="chatType = type.value"
            :class="[
              'flex-1 py-2 px-4 rounded-lg font-medium transition-colors',
              chatType === type.value
                ? 'bg-blue-500 text-white'
                : 'bg-gray-100 text-gray-600 hover:bg-gray-200',
            ]"
          >
            <i :class="[type.icon, 'mr-2']"></i>
            {{ type.label }}
          </button>
        </div>
      </div>

      <!-- Organization Filter -->
      <div class="mb-4" v-if="organizations?.length > 0">
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
      <div class="flex flex-wrap gap-2 mb-4" v-if="selectedUsers.length > 0">
        <div
          v-for="user in selectedUsers"
          :key="user.id"
          class="bg-blue-100 text-blue-700 px-3 py-1 rounded-full flex items-center gap-2"
        >
          <span>{{ user.name }}</span>
          <button
            @click="removeUser(user)"
            class="text-blue-500 hover:text-blue-700"
          >
            <i class="fas fa-times-circle"></i>
          </button>
        </div>
      </div>

      <!-- Users List -->
      <div class="flex-1 overflow-y-auto min-h-[200px] border rounded-lg">
        <div
          v-if="loading"
          class="h-full flex items-center justify-center text-gray-500"
        >
          <i class="fas fa-spinner fa-spin mr-2"></i>
          Loading...
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
            :key="user.id"
            @click="toggleUser(user)"
            :class="[
              'p-3 flex items-center gap-3 cursor-pointer hover:bg-gray-50',
              isSelected(user) ? 'bg-blue-50' : '',
            ]"
          >
            <div
              class="w-10 h-10 rounded-full bg-gray-200 flex items-center justify-center"
            >
              <i class="fas fa-user text-gray-600"></i>
            </div>
            <div class="flex-1">
              <div class="font-medium">{{ user.name }}</div>
              <div class="text-sm text-gray-500">{{ user.email }}</div>
            </div>
            <div v-if="isSelected(user)" class="text-blue-500">
              <i class="fas fa-check-circle"></i>
            </div>
          </div>
        </div>
      </div>

      <!-- Footer -->
      <div class="mt-4 flex justify-end gap-2">
        <button
          @click="$emit('close')"
          class="px-4 py-2 text-gray-600 hover:bg-gray-100 rounded-lg"
        >
          Cancel
        </button>
        <button
          @click="createChat"
          :disabled="!isValid"
          :class="[
            'px-4 py-2 rounded-lg',
            isValid
              ? 'bg-blue-500 text-white hover:bg-blue-600'
              : 'bg-gray-200 text-gray-500 cursor-not-allowed',
          ]"
        >
          Create Chat
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";

export default {
  name: "NewChatModal",
  data() {
    return {
      chatType: "direct",
      selectedUsers: [],
      searchQuery: "",
      selectedOrg: "",
      loading: false,
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
    ...mapState("chat", ["users", "currentUser", "organizations"]),
    filteredUsers() {
      let filtered = this.users.filter(
        (user) => user.id !== this.$store.state.chat.currentUser.id,
      );

      if (this.selectedOrg) {
        filtered = filtered.filter((user) => user.orgId === this.selectedOrg);
      }

      if (this.searchQuery.trim()) {
        const query = this.searchQuery.toLowerCase();
        filtered = filtered.filter(
          (user) =>
            user.name.toLowerCase().includes(query) ||
            user.email.toLowerCase().includes(query),
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
  },
  methods: {
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
      this.selectedUsers = this.selectedUsers.filter((u) => u.id !== user.id);
    },

    isSelected(user) {
      return this.selectedUsers.some((u) => u.id === user.id);
    },

    createChat() {
      if (!this.isValid) return;

      this.$emit("create", {
        type: this.chatType,
        participants: this.selectedUsers.map((user) => user.id),
      });
    },
    getUserSubtitle(user) {
      return user.title || user.email;
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
