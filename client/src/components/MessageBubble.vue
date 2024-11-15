<template>
  <div class="w-full">
    <!-- Timestamp -->
    <div v-if="showTimestamp" class="flex justify-center mb-4">
      <div class="bg-gray-100 rounded-full px-3 py-1 text-xs text-gray-500">
        {{ formatTimestamp(message.timestamp) }}
      </div>
    </div>

  <div :class="['w-full flex', isOwn ? 'justify-end' : 'justify-start mb-2']">
      <div class="flex items-end gap-2 message-container">
        <!-- ... existing avatar code ... -->
        
        <div class="relative group"> <!-- Add this wrapper -->
          <div
            :class="[
              'max-w-[50%]',
              'min-w-[100%]',
              'rounded-2xl px-4 py-2 message-content',
              isOwn
                ? 'bg-blue-500 text-white ml-auto rounded-tr-sm own-message'
                : 'bg-[#e9e9eb] text-black mr-auto rounded-tl-sm other-message',
            ]"
          >
            <!-- Message content -->
            <div
              v-if="showSender"
              class="text-xs font-medium mb-1 sender-name"
              :class="isOwn ? 'text-white/90' : 'text-gray-500'"
            >
              {{ message.senderName }}
            </div>
            <div class="whitespace-pre-wrap break-words">
              {{ message.content }}
            </div>

            <!-- Delete button (only shows on hover for own messages) -->
            <div
              v-if="isOwn"
              class="absolute top-2 right-2 opacity-0 group-hover:opacity-100 transition-opacity"
            >
              <button
                @click.stop="confirmDelete"
                class="p-1 hover:bg-blue-600 rounded-full transition-colors"
                title="Delete message"
              >
                <i class="fas fa-trash-alt text-xs text-white/80 hover:text-white"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div
      v-if="showDeleteModal"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
      @click="showDeleteModal = false"
    >
      <div
        class="bg-white rounded-lg p-6 max-w-sm mx-4"
        @click.stop
      >
        <h3 class="text-lg font-semibold mb-4">Delete Message</h3>
        <p class="text-gray-600 mb-6">Are you sure you want to delete this message?</p>
        <div class="flex justify-end gap-4">
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
import { mapState, mapActions } from 'vuex';

export default {
  name: "MessageBubble",
  props: {
    message: {
      type: Object,
      required: true,
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
      return this.users.find(user => user.userID === this.message.senderId);
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

    confirmDelete() {
      this.showDeleteModal = true;
    },

    async handleDelete() {
      try {
        await this.deleteMessage(this.message.messageId);
        this.showDeleteModal = false;
      } catch (error) {
        console.error("Error deleting message:", error);
      }
    },
  },
};
</script>

<style scoped>
.bg-gray-100 {
  animation: timestamp-appear 0.3s ease forwards;
}

@keyframes timestamp-appear {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message-container {
  animation: message-appear 0.3s ease forwards;
}

.message-content {
  transition: all 0.2s ease;
  position: relative;
  transform-origin: bottom;
}

/* Different animations for own vs other messages */
.own-message {
  animation: slide-left 0.3s ease forwards;
}

.other-message {
  animation: slide-right 0.3s ease forwards;
}

.avatar {
  animation: fade-in 0.3s ease forwards;
}

.sender-name {
  animation: fade-in 0.3s ease 0.1s forwards;
}

.timestamp {
  animation: fade-in 0.3s ease 0.2s forwards;
  opacity: 0;
  animation-fill-mode: forwards;
}

@keyframes message-appear {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slide-left {
  from {
    opacity: 0;
    transform: translateX(20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes slide-right {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes fade-in {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

/* Hover effects */
.message-content:hover {
  transform: scale(1.01);
}

/* Add a subtle shadow effect */
.message-content {
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.message-content:hover {
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.15);
}

/* Optional: Add a shine effect on hover */
.message-content::after {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    45deg,
    transparent,
    rgba(255, 255, 255, 0.1),
    transparent
  );
  opacity: 0;
  transition: opacity 0.3s ease;
}

.message-content:hover::after {
  opacity: 1;
}

.avatar {
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.avatar:hover {
  transform: scale(1.1);
  border-color: #e5e7eb;
}

.avatar img {
  transition: transform 0.3s ease;
}

.avatar:hover img {
  transform: scale(1.1);
}

/* Optional: Add a subtle glow effect on hover */
.avatar:hover {
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

/* Adjust message container spacing */
.message-container {
  gap: 8px;
}

/* Make avatar size consistent */
.avatar {
  min-width: 2rem;
  min-height: 2rem;
}

/* Add smooth transition for image loading */
.avatar img {
  opacity: 0;
  animation: image-fade-in 0.3s ease forwards;
}

@keyframes image-fade-in {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

/* Optional: Add hover tooltip for sender name */
.avatar {
  position: relative;
}

.avatar::before {
  content: attr(data-sender-name);
  position: absolute;
  bottom: -20px;
  left: 50%;
  transform: translateX(-50%) scale(0);
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  opacity: 0;
  transition: all 0.3s ease;
  pointer-events: none;
  white-space: nowrap;
}

.avatar:hover::before {
  opacity: 1;
  transform: translateX(-50%) scale(1);
}

.group:hover .message-content {
  transform: scale(1.01);
}

/* Ensure the delete button container doesn't affect message padding */
.message-content {
  position: relative;
  padding-right: 2.5rem;
}
</style>