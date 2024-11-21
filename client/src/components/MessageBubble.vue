<template>
  <div class="w-full">
    <div :class="['w-full flex', isOwn ? 'justify-end' : 'justify-start mb-2']">
      <div class="flex items-end gap-2 max-w-[85%]">
        <!-- Increased max-width -->
        <!-- Sender Avatar (for non-own messages) -->
        <div v-if="!isOwn && showSender" class="w-8 h-8 flex-shrink-0">
          <div
            class="w-8 h-8 rounded-full flex items-center justify-center overflow-hidden"
            :class="[!senderProfilePicture ? 'bg-gray-300' : '']"
          >
            <img
              v-if="senderProfilePicture"
              :src="senderProfilePicture"
              :alt="message.senderName"
              class="w-full h-full object-cover"
              @error="handleImageError"
            />
            <i v-else class="fas fa-user text-gray-600"></i>
          </div>
        </div>

        <!-- Message Content -->
        <div class="min-w-0 max-w-full">
          <!-- Changed from max-w-[70%] to max-w-full -->
          <!-- Sender Name -->
          <div
            v-if="!isOwn && showSender"
            class="text-sm text-gray-600 mb-1 ml-1"
          >
            {{ message.senderName }}
          </div>

          <!-- Message Bubble -->
          <div
            :class="[
              'rounded-2xl px-4 py-2 relative group inline-block max-w-full', // Added inline-block
              isOwn
                ? 'bg-blue-500 text-white rounded-tr-sm'
                : 'bg-[#e9e9eb] text-black rounded-tl-sm',
            ]"
          >
            <!-- Delete Button (only show for own messages) -->
            <button
              v-if="isOwn && !message.deleted"
              @click="onDeleteClick"
              class="absolute -right-2 -top-2 opacity-0 group-hover:opacity-100 transition-opacity p-2 bg-white rounded-full shadow-md hover:bg-gray-100"
            >
              <i class="fas fa-trash-alt text-red-500 text-sm"></i>
            </button>

            <!-- Message Content -->
            <div class="whitespace-pre-wrap break-words">
              <span v-if="message.deleted" class="italic text-gray-500">
                This message has been deleted
              </span>
              <span v-else>
                {{ message.content }}
              </span>
            </div>

            <!-- Timestamp -->
            <div
              :class="[
                'text-xs mt-1',
                isOwn ? 'text-blue-100' : 'text-gray-500',
              ]"
            >
              {{ formatTimestamp(message.timestamp) }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div
      v-if="showDeleteModal"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
    >
      <div class="bg-white rounded-lg p-4 max-w-sm w-full mx-4">
        <h3 class="text-lg font-semibold mb-2">Delete Message?</h3>
        <p class="text-gray-600 mb-4">This action cannot be undone.</p>
        <div class="flex justify-end gap-2">
          <button
            @click="showDeleteModal = false"
            class="px-4 py-2 text-gray-600 hover:bg-gray-100 rounded"
          >
            Cancel
          </button>
          <button
            @click="handleDelete"
            class="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600"
          >
            Delete
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState, mapActions } from "vuex";

export default {
  name: "MessageBubble",
  props: {
    message: {
      type: Object,
      required: true,
      validator: function (obj) {
        return typeof obj.deleted !== "undefined";
      },
    },
    isOwn: {
      type: Boolean,
      default: false,
    },
    showSender: {
      type: Boolean,
      default: true,
    },
    showTimestamp: {
      type: Boolean,
      default: false,
    },
  },

  data() {
    return {
      imageLoadError: false,
      showDeleteModal: false,
    };
  },

  computed: {
    ...mapState("chat", ["users"]),

    sender() {
      return this.users.find((user) => user.userID === this.message.senderID);
    },

    senderProfilePicture() {
      return this.sender?.profilePicture || null;
    },
  },

  methods: {
    ...mapActions("chat", ["deleteMessage"]),
    formatTimestamp(timestamp) {
      const utcDate = new Date(timestamp + "Z");
      return utcDate.toLocaleTimeString([], {
        hour: "numeric",
        minute: "2-digit",
        hour12: true,
        timeZone: Intl.DateTimeFormat().resolvedOptions().timeZone,
      });
    },

    handleImageError() {
      this.imageLoadError = true;
    },

    onDeleteClick() {
      console.log("Confirm delete");
      this.showDeleteModal = true;
    },

    async handleDelete() {
      try {
        await this.deleteMessage({
          chatId: this.message.chatId,
          messageId: this.message.id,
        });
        this.showDeleteModal = false;
        this.$emit("message-deleted");
      } catch (error) {
        console.error("Error deleting message:", error);
      }
    },
  },
};
</script>

<style scoped>
.message-bubble {
  position: relative;
  transition: all 0.2s ease;
  word-wrap: break-word;
  word-break: break-word;
}

.delete-button {
  transition: opacity 0.2s ease;
}
</style>