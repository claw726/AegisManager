<template>
  <div class="h-screen flex flex-col overflow-hidden bg-background">
    <NavBar class="flex-shrink-0" />
    <div class="flex flex-1 min-h-0">
      <!-- Light gray background -->
      <!-- Sidebar -->
      <div class="w-80 border-r bg-[#f7f7f7] flex flex-col">
        <!-- Search bar -->
        <div class="p-3 border-b flex-shrink-0">
          <div class="relative">
            <input
              v-model="searchQuery"
              type="text"
              placeholder="Search"
              class="w-full px-4 py-2 pl-10 bg-[#ffffff] rounded-full border-none focus:ring-2 focus:ring-blue-500"
            />
            <i class="fas fa-search absolute left-4 top-3 text-gray-400"></i>
          </div>
        </div>

        <!-- Chat List -->
        <div class="flex-1 overflow-y-auto min-h-0">
          <TransitionGroup name="chat-list" tag="div">
            <ChatListItem
              v-for="chat in filteredChats"
              :key="chat.id"
              :chat="chat"
              :active="activeChat?.id === chat.id"
              :searchQuery="searchQuery"
              @select="handleChatSelect"
            />
          </TransitionGroup>
        </div>

        <!-- New Chat Button -->
        <div class="p-3 border-t flex-shrink-0">
          <button
            class="w-full bg-blue-500 hover:bg-blue-600 text-white rounded-full px-4 py-2 flex items-center justify-center transition-colors"
            @click="showNewChatModal = true"
          >
            <i class="fas fa-pen mr-2"></i>
            New Message
          </button>
        </div>
      </div>

      <!-- Main Chat Area -->
      <div class="flex-1 flex flex-col min-h-0 bg-white">
        <router-view v-if="activeChat"></router-view>
        <div
          v-else
          class="h-full flex items-center justify-center text-gray-500 bg-[#ffffff]"
        >
          <div class="text-center">
            <i class="fas fa-comments text-6xl mb-4 text-gray-300"></i>
            <p>Select a conversation to start messaging</p>
          </div>
        </div>
      </div>
    </div>
  </div>
  <NewChatModal
    v-if="showNewChatModal"
    @close="showNewChatModal = false"
    @create="createNewChat"
  />
</template>

<script>
import { mapState, mapActions } from "vuex";
import ChatListItem from "@/components/ChatListItem.vue";
import NavBar from "@/components/NavBar.vue";
import NewChatModal from "@/components/NewChatModal.vue";

export default {
  name: "ChatView",
  components: {
    ChatListItem,
    NavBar,
    NewChatModal,
  },
  data() {
    return {
      showNewChatModal: false,
      searchQuery: "",
    };
  },
  computed: {
    ...mapState({
      chats: (state) => state.chat.chats,
      currentUser: (state) => state.auth.currentUser,
      activeChat: (state) => state.chat.activeChat,
      messages: (state) => state.chat.messages,
    }),

    filteredChats() {
      if (!this.searchQuery.trim()) {
        return this.chats;
      }

      const query = this.searchQuery.toLowerCase().trim();

      return this.chats.filter((chat) => {
        // Search in chat title
        if (chat.title.toLowerCase().includes(query)) {
          return true;
        }

        // Search in chat messages
        const chatMessages = this.messages[chat.id] || [];
        return chatMessages.some(
          (message) =>
            message.content.toLowerCase().includes(query) ||
            message.senderName.toLowerCase().includes(query),
        );
      });
    },
  },

  watch: {
    $route() {
      this.searchQuery = "";
    },
  },

  created() {
    // Add debug logging
    console.log("Current state:", {
      chats: this.chats,
      currentUser: this.currentUser,
      activeChat: this.activeChat,
    });
  },
  created() {
    console.log("ChatView created");
    const chatId = this.getChatIdFromRoute();
    if (chatId) {
      console.log("Initial chat selection:", chatId);
      this.selectChat(chatId);
    }
  },

  async mounted() {
    await this.fetchUserChats();
  },

  methods: {
    ...mapActions("chat", [
      "selectChat",
      "createChat",
      "getChat",
      "fetchUserChats",
    ]),
    async createNewChat(chatData) {
      try {
        await this.$store.dispatch("chat/createNewChat", chatData);
        this.showNewChatModal = false;

        // Get the newly created chat (it will be the first one in the list)
        const newChat = this.$store.state.chat.chats[0];

        // Navigate to the appropriate route based on chat type
        let route;
        if (chatData.type === "direct") {
          route = {
            name: "DirectChat",
            params: { userID: chatData.participants[0] },
          };
        } else {
          route = {
            name: "GroupChat",
            params: { groupID: newChat.id },
          };
        }

        await this.$router.push(route);
      } catch (error) {
        console.error("Error creating new chat:", error);
        // You might want to add error handling UI here
      }
    },
    async handleChatSelect(chat) {
      if (!chat || !chat.id) return;
      console.log("handleChatSelect called with:", chat);

      try {
        await this.selectChat(chat.id);
        console.log("Chat selected:", chat.id);

        let route;
        switch (chat.type) {
          case "direct":
            route = {
              name: "DirectChat",
              params: {
                userID: chat.participants.find(
                  (id) => id !== this.currentUser.userID,
                ),
              },
            };
            break;
          case "group":
            route = {
              name: "GroupChat",
              params: { groupID: chat.id },
            };
            break;
          case "organization":
            route = {
              name: "OrgChat",
              params: { orgIndex: chat.id },
            };
            break;
          case "project":
            route = {
              name: "ProjectChat",
              params: {
                orgIndex: chat.orgId,
                projectIndex: chat.id,
              },
            };
            break;
          case "task":
            route = {
              name: "TaskChat",
              params: {
                orgIndex: chat.orgId,
                projectIndex: chat.projectId,
                taskID: chat.id,
              },
            };
            break;
        }

        if (route) {
          console.log("Navigating to route:", route);
          await this.$router.push(route);
        }
      } catch (error) {
        console.error("Error in handleChatSelect:", error);
      }
    },
    getChatIdFromRoute() {
      const { name, params } = this.$route;
      switch (name) {
        case "DirectChat":
          return `direct-${params.userID}`;
        case "GroupChat":
          return params.groupID;
        case "OrgChat":
          return `org-${params.orgIndex}`;
        case "ProjectChat":
          return `project-${params.projectIndex}`;
        case "TaskChat":
          return `task-${params.taskIndex}`;
        default:
          return null;
      }
    },
  },
};
</script>

<style scoped>
.chat-list-enter-active,
.chat-list-leave-active {
  transition: all 0.5s ease;
}

.chat-list-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.chat-list-leave-to {
  opacity: 0;
  transform: translateX(30px);
}

.chat-list-move {
  transition: transform 0.5s ease;
}

.highlight {
  background-color: yellow;
  padding: 0 2px;
  border-radius: 2px;
}
</style>