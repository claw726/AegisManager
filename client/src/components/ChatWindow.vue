<template>
  <div class="flex flex-col h-full min-h-0">
    <!-- Chat Header -->
    <div class="border-b p-4 flex items-center bg-white flex-shrink-0">
      <div class="flex-1 flex items-center">
        <div
          class="w-10 h-10 rounded-full bg-gray-100 flex items-center justify-center mr-3"
        >
          <i :class="[chatTypeIcon, 'text-gray-600']"></i>
        </div>
        <div>
          <div class="flex items-center">
            <h2 class="text-xl font-semibold">{{ activeChat?.title }}</h2>
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
      <TransitionGroup name="message" tag="div" class="flex flex-col flex-grow">
        <div
          v-for="(message, index) in chatMessages"
          :key="message.id"
          class="w-full message-item"
        >
          <MessageBubble
            :message="message"
            :isOwn="message.senderId === currentUser?.id"
            :showSender="shouldShowSenderName(message, index)"
            :showTimestamp="shouldShowTimestamp(message, index)"
            class="w-full"
          />
        </div>
      </TransitionGroup>
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

export default {
  name: "ChatWindow",
  components: {
    MessageBubble,
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
    };
  },

  computed: {
    ...mapState("chat", {
      activeChat: (state) => state.activeChat,
      currentUser: (state) => state.currentUser,
    }),
    ...mapGetters("chat", ["getChatMessages"]),
    chatMessages() {
      if (!this.activeChat) return [];
      return this.$store.state.chat.messages[this.activeChat.id] || [];
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
  },

  watch: {
    chatMessages: {
      handler() {
        this.$nextTick(() => {
          this.scrollToBottom();
        });
      },
      deep: true,
    },
  },

  updated() {
    this.scrollToBottom();
  },

  mounted() {
    this.scrollToBottom();
  },

  methods: {
    ...mapActions("chat", ["sendMessage"]),

    async handleSendMessage() {
      if (!this.newMessage.trim() || !this.activeChat) return;

      try {
        await this.sendMessage({
          chatId: this.activeChat.id,
          content: this.newMessage,
        });

        this.newMessage = "";
        this.$nextTick(() => {
          this.scrollToBottom();
        });
      } catch (error) {
        console.error("Error sending message:", error);
      }
    },

    scrollToBottom() {
      const container = this.$refs.messageContainer;
      if (container) {
        container.scrollTop = container.scrollHeight;
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
</style>
