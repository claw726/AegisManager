<template>
  <div class="flex flex-col h-full min-h-0">
    <ConnectionStatus />
    <!-- Chat Header -->
    <div class="border-b p-4 flex items-center bg-white flex-shrink-0">
      <div class="flex-1 flex items-center">
        <!-- Updated Avatar Section -->
        <div class="relative w-12 h-10 mr-3 flex-shrink-0">
          <!-- Single Avatar for Direct Messages -->
          <div
            v-if="activeChat?.type === 'direct'"
            class="w-10 h-10 rounded-full flex items-center justify-center overflow-hidden"
            :class="[!otherUser?.profilePicture || imageLoadError ? 'bg-gray-300' : '']"
          >
            <img
              v-if="otherUser?.profilePicture && !imageLoadError"
              :src="otherUser.profilePicture"
              :alt="displayTitle"
              class="w-full h-full object-cover"
              @error="handleImageError"
            />
            <i v-else :class="[chatTypeIcon, 'text-gray-600']" />
          </div>

          <!-- Overlapping Avatars for Group Chats -->
          <div
            v-else-if="activeChat?.type === 'group'"
            class="relative w-full h-full group"
          >
            <template v-for="(participant, index) in groupParticipants" :key="participant.userID">
              <div
                v-if="index < 4"
                class="absolute rounded-full border-2 border-white overflow-hidden bg-gray-300 group-hover:scale-95 transition-transform"
                :class="[
                  'w-7 h-7',
                  getAvatarPosition(index),
                  {'z-20': index === 0},
                  {'z-10': index === 1},
                  {'z-0': index >= 2}
                ]"
                :title="participant.userName"
              >
                <img
                  v-if="participant.profilePicture"
                  :src="participant.profilePicture"
                  :alt="participant.userName"
                  class="w-full h-full object-cover"
                  @error="participant.imageError = true"
                />
                <span
                  v-else
                  class="w-full h-full flex items-center justify-center text-xs text-gray-600 font-medium bg-gray-200"
                >
                  {{ getInitials(participant.userName) }}
                </span>
              </div>
            </template>
            <!-- Additional count indicator -->
            <div
              v-if="remainingParticipantsCount > 0"
              class="absolute bottom-0 right-0 bg-gray-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center z-30"
            >
              +{{ remainingParticipantsCount }}
            </div>
          </div>

          <!-- Default Icon for Other Chat Types -->
          <div
            v-else
            class="w-10 h-10 rounded-full bg-gray-300 flex items-center justify-center"
          >
            <i :class="[chatTypeIcon, 'text-gray-600']" />
          </div>
        </div>

        <div>
          <div class="flex items-center">
            <h2 class="text-xl font-semibold">{{ displayTitle }}</h2>
            <i :class="[chatTypeIconSmall, 'ml-2 text-gray-400']"></i>
          </div>
          <p class="text-sm text-gray-500 flex items-center">
            <i class="fas fa-users mr-1"></i>
            {{ activeChat?.participants?.length || 0 }} members
          </p>
        </div>
      </div>
      <div>
        <button
          class="text-gray-500 hover:text-gray-700 p-2 rounded-full hover:bg-gray-100"
        >
          <i class="fas fa-ellipsis-v"></i>
        </button>
      </div>
    </div>

    <!-- Messages Area -->
    <div
      ref="messageContainer"
      class="flex-1 overflow-y-auto p-4 space-y-2 bg-[#ffffff] min-h-0"
    >
      <!-- Loading State -->
      <div v-if="loading" class="flex justify-center items-center h-full">
        <div
          class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"
        ></div>
      </div>

      <!-- Error State -->
      <div
        v-else-if="error"
        class="flex justify-center items-center h-full text-red-500"
      >
        <div class="text-center">
          <p>{{ error }}</p>
          <button
            class="mt-2 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
            @click="loadChatMessages(activeChat?.id)"
          >
            Retry
          </button>
        </div>
      </div>

      <!-- Messages -->
      <TransitionGroup
        v-else
        name="message"
        tag="div"
        class="flex flex-col flex-grow"
      >
        <div
          v-for="(message, index) in chatMessages"
          :key="message.id"
          class="w-full message-item"
        >
          <MessageBubble
            :message="message"
            :isOwn="message.senderId === currentUser?.userID"
            :showSender="shouldShowSenderName(message, index)"
            :showTimestamp="shouldShowTimestamp(message, index)"
            class="w-full"
          />
        </div>
      </TransitionGroup>

      <!-- Empty State -->
      <div
        v-if="!loading && !error && chatMessages.length === 0"
        class="flex justify-center items-center h-full text-gray-500"
      >
        <p>No messages yet. Start the conversation!</p>
      </div>
    </div>

    <!-- Message Input -->
    <div class="p-3 bg-[#f7f7f7] flex-shrink-0">
      <div class="flex items-center bg-white rounded-full border">
        <button class="p-2 text-gray-500 hover:text-gray-700">
          <i class="fas fa-plus"></i>
        </button>
        <input
          v-model="newMessage"
          type="text"
          class="flex-1 px-4 py-2 bg-transparent border-none focus:ring-0"
          placeholder="Type a message..."
          @keyup.enter="handleSendMessage"
        />
        <button
          v-if="newMessage.trim()"
          class="p-2 text-blue-500 hover:text-blue-600"
          @click="handleSendMessage"
        >
          <i class="fas fa-paper-plane"></i>
        </button>
        <button v-else class="p-2 text-gray-500">
          <i class="fas fa-microphone"></i>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState, mapGetters, mapActions } from "vuex";
import MessageBubble from "./MessageBubble.vue";
import ConnectionStatus from "@/components/ConnectionStatus.vue";

export default {
  name: "ChatWindow",
  components: {
    MessageBubble,
    ConnectionStatus,
  },

  props: {
    userID: String,
    groupID: String,
    orgIndex: String,
    projectIndex: String,
    taskID: String,
  },

  data() {
  return {
    newMessage: "",
    resolvedTitle: "",
    profilePicture: null,
    imageLoadError: false,
  };
},

  computed: {
    ...mapState("chat", {
      activeChat: (state) => state.activeChat,
      loading: (state) => state.loading,
    }),
    ...mapState("auth", ["currentUser"]),
    ...mapGetters("chat", ["getChatMessages"]),
    chatMessages() {
      if (!this.activeChat?.id) return [];
      return this.getChatMessages(this.activeChat.id) || [];
    },
    chatTypeIcon() {
      if (!this.activeChat) return "fas fa-comment";

      switch (this.activeChat.type) {
        case "direct":
          return "fas fa-user";
        case "group":
          return "fas fa-users";
        case "organization":
          return "fas fa-building";
        case "project":
          return "fas fa-project-diagram";
        case "task":
          return "fas fa-tasks";
        default:
          return "fas fa-comment";
      }
    },

    groupParticipants() {
      if (!this.activeChat?.participants) return [];
      return this.activeChat.participants
        .filter(id => id !== this.currentUser?.userID) // Exclude current user
        .map(id => this.$store.state.chat.users.find(user => user.userID === id))
        .filter(user => user); // Filter out undefined users
    },

    remainingParticipantsCount() {
      return Math.max(0, this.groupParticipants.length - 4);
    },

     otherUser() {
      if (this.activeChat?.type !== 'direct') return null;
      const otherUserId = this.activeChat.participants.find(
        id => id !== this.currentUser?.userID
      );
      return this.$store.state.chat.users.find(
        user => user.userID === otherUserId
      );
    },

  displayTitle() {
    if (!this.activeChat) return '';

    if (this.activeChat.type === 'direct') {
      return this.otherUser?.userName || this.resolvedTitle || 'Loading...';
    }

    return this.activeChat.title;
  },

  chatTypeIconSmall() {
      if (!this.activeChat) return "fas fa-comment";

      switch (this.activeChat.type) {
        case "direct":
          return "fas fa-user-circle";
        case "group":
          return "fas fa-users";
        case "organization":
          return "fas fa-building";
        case "project":
          return "fas fa-project-diagram";
        case "task":
          return "fas fa-tasks";
        default:
          return "fas fa-comment";
      }
    },
    wsConnected() {
      return this.$store.state.chat.wsConnected;
    },
  },


  watch: {
    "activeChat.id": {
      immediate: true,
      async handler(newChatId, oldChatId) {
        if (oldChatId) {
          this.$store.dispatch("chat/leaveChatRoom", oldChatId);
        }

        if (newChatId) {
          try {
            await this.loadChatMessages(newChatId);
            this.$store.dispatch("chat/joinChatRoom", newChatId);
          } catch (error) {
            console.error("Error loading chat messages:", error);
            this.error = "Failed to load messages";
          }
        }
      },
    },
    'activeChat': {
      immediate: true,
      handler() {
        this.updateDisplayTitle();
      }
    },
  },

  async created() {
    if (this.activeChat?.type === "direct") {
      const otherUserId = this.activeChat.participants.find(
        (id) => id !== this.currentUser?.userID
      );

      if (otherUserId) {
        await this.$store.dispatch("chat/fetchUsers", otherUserId);
      }
    }
  },

  async mounted() {
    if (this.activeChat?.id) {
      await this.loadChatMessages(this.activeChat.id);
    }
    console.log("Chat window mounted: ", this.activeChat);
  },

  updated() {
    this.scrollToBottom();
  },

  mounted() {
    this.scrollToBottom();
  },

  beforeUnmount() {
    if (this.activeChat) {
      this.$store.dispatch("chat/leaveChatRoom", this.activeChat.id);
    }
  },

  methods: {
    ...mapActions("chat", ["sendMessage"]),

    async handleSendMessage() {
      if (!this.newMessage.trim() || !this.activeChat?.id) return;

      try {
        await this.sendMessage({
          chatId: this.activeChat.id,
          content: this.newMessage.trim(),
        });

        this.newMessage = "";
        // Scroll to bottom after sending
        this.$nextTick(() => {
          this.scrollToBottom();
        });
      } catch (error) {
        console.error("Error sending message:", error);
        // Show error to user
        this.error = "Failed to send message";
      }
    },
    scrollToBottom() {
      const container = this.$refs.messageContainer;
      if (container) {
        container.scrollTop = container.scrollHeight;
      }
    },

    async updateDisplayTitle() {
      if (!this.activeChat?.type === "direct") return;

      const otherUserId = this.activeChat.participants.find(
        (id) => id !== this.currentUser?.userID
      );
      if (otherUserId) {
        try {
          await this.$store.dispatch("chat/fetchUsers", otherUserId);
          const otherUser = this.$store.state.chat.users.find(
            (user) => user.userID === otherUserId
          );
          this.resolvedTitle = otherUser?.userName || "Unknown User";
        } catch (error) {
          console.error("Error loading chat title:", error);
          this.resolvedTitle = "Error loading name";
        }
      }
    },

    async loadChatMessages(chatId) {
      if (!chatId) return;

      this.loading = true;
      this.error = null;

      try {
        await this.$store.dispatch("chat/getMessages", chatId);
        console.log("Messages loaded:", this.chatMessages);
        this.scrollToBottom();
      } catch (error) {
        console.error("Error loading messages:", error);
        this.error = error.response?.data?.message || "Failed to load messages";
      } finally {
        this.loading = false;
      }
    },
    shouldShowSenderName(message, index) {
      // Don't show sender name for direct chats
      if (this.activeChat?.type === "direct") return false;

      // Don't show sender name for own messages
      if (message.senderId === this.currentUser?.id) return false;

      // Show sender name if it's the first message
      if (index === 0) return true;

      // Get the previous message
      const previousMessage = this.chatMessages[index - 1];

      // Show sender name if previous message was from a different sender
      return previousMessage.senderId !== message.senderId;
    },
    shouldShowTimestamp(message, index) {
      if (index === 0) return true;

      const currentTime = new Date(message.timestamp);
      const previousTime = new Date(this.chatMessages[index - 1].timestamp);

      // Show timestamp if more than 15 minutes have passed
      const timeDifference = currentTime - previousTime;
      return timeDifference > 15 * 60 * 1000; // 15 minutes in milliseconds
    },
    getAvatarPosition(index) {
      const positions = {
        0: 'top-0 left-0',
        1: 'top-0 right-0',
        2: 'bottom-0 left-0',
        3: 'bottom-0 right-0'
      };
      return positions[index] || '';
    },

    getInitials(name) {
      if (!name) return '?';
      return name
        .split(' ')
        .map(word => word[0])
        .join('')
        .toUpperCase()
        .slice(0, 2);
    },

    handleImageError() {
      this.imageLoadError = true;
    },
  },
};
</script>

<style scoped>
.message-item {
  position: relative; /* Add this to maintain position during animation */
}

.message-enter-active,
.message-leave-active {
  transition: all 0.3s ease;
  position: absolute; /* Changed from relative */
  width: 100%;
}

.message-enter-from {
  opacity: 0;
  transform: translateY(30px);
}

.message-leave-to {
  opacity: 0;
  transform: translateY(-30px);
}

.message-move {
  transition: all 0.3s ease;
}

.profile-picture-enter-active,
.profile-picture-leave-active {
  transition: opacity 0.3s ease;
}

.profile-picture-enter-from,
.profile-picture-leave-to {
  opacity: 0;
}

img {
  transition: transform 0.3s ease;
}

img:hover {
  transform: scale(1.05);
}
</style>