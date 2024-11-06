<template>
  <div
    class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center"
  >
    <div class="bg-white rounded-lg p-6 w-96">
      <h2 class="text-xl font-semibold mb-4">New Chat</h2>

      <div class="mb-4">
        <label class="block text-sm font-medium mb-2">Chat Type</label>
        <select v-model="chatType" class="w-full border rounded-lg px-3 py-2">
          <option value="direct">Direct Message</option>
          <option value="group">Group Chat</option>
        </select>
      </div>

      <div class="mb-4">
        <label class="block text-sm font-medium mb-2">Select Users</label>
        <select
          v-model="selectedUsers"
          multiple
          class="w-full border rounded-lg px-3 py-2"
        >
          <option v-for="user in users" :key="user.id" :value="user.id">
            {{ user.name }}
          </option>
        </select>
      </div>

      <div class="flex justify-end gap-2">
        <button
          class="px-4 py-2 text-gray-600 hover:bg-gray-100 rounded-lg"
          @click="$emit('close')"
        >
          Cancel
        </button>
        <button
          class="px-4 py-2 bg-blue-500 text-white rounded-lg"
          :disabled="!isValid"
          @click="createChat"
        >
          Create Chat
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "NewChatModal",
  data() {
    return {
      chatType: "direct",
      selectedUsers: [],
    };
  },
  computed: {
    users() {
      return this.$store.state.chat.users.filter(
        (user) => user.id !== this.$store.state.chat.currentUser.id,
      );
    },
    isValid() {
      if (this.chatType === "direct") {
        return this.selectedUsers.length === 1;
      }
      return this.selectedUsers.length > 0;
    },
  },
  methods: {
    createChat() {
      this.$emit("create", {
        type: this.chatType,
        participants: this.selectedUsers,
      });
    },
  },
};
</script>