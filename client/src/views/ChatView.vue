<template>
  <div class="h-max-screen bg-background">
    <NavBar />
    <div class="flex h-screen">
      <!-- Light gray background -->
      <!-- Sidebar -->
      <div class="w-80 border-r bg-[#f7f7f7] flex flex-col">
        <!-- Search bar -->
        <div class="p-3 border-b">
          <div class="relative">
            <input
              type="text"
              placeholder="Search"
              class="w-full px-4 py-2 pl-10 bg-[#ffffff] rounded-full border-none focus:ring-2 focus:ring-blue-500"
            />
            <i class="fas fa-search absolute left-4 top-3 text-gray-400"></i>
          </div>
        </div>

        <!-- Chat List -->
        <div class="flex-1 overflow-y-auto">
          <ChatListItem
            v-for="chat in chats"
            :key="chat.id"
            :chat="chat"
            :active="activeChat?.id === chat.id"
            @select="handleChatSelect"
          />
        </div>

        <!-- New Chat Button -->
        <div class="p-3 border-t">
          <button
            @click="showNewChatModal = true"
            class="w-full bg-blue-500 hover:bg-blue-600 text-white rounded-full px-4 py-2 flex items-center justify-center transition-colors"
          >
            <i class="fas fa-pen mr-2"></i>
            New Message
          </button>
        </div>
      </div>

      <!-- Main Chat Area -->
      <div class="flex-1 flex flex-col bg-white">
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
</template>

<script>
import { mapState, mapActions } from "vuex";
import ChatListItem from "@/components/ChatListItem.vue";
import NavBar from "@/components/NavBar.vue";

export default {
  name: "ChatView",
  components: {
    ChatListItem,
    NavBar,
  },
  data() {
    return {
      showNewChatModal: false,
    };
  },
  computed: {
    ...mapState({
      chats: (state) => state.chat.chats,
      currentUser: (state) => state.chat.currentUser,
      activeChat: (state) => state.chat.activeChat,
    }),
  },
  created() {
    // Add debug logging
    console.log("Current state:", {
      chats: this.chats,
      currentUser: this.currentUser,
      activeChat: this.activeChat,
    });
  },
  methods: {
    ...mapActions("chat", ["selectChat"]),
    createNewChat(data) {
      // Implement when backend is ready
      this.showNewChatModal = false;
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
                  (id) => id !== this.currentUser.id,
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
  created() {
    console.log("ChatView created");
    const chatId = this.getChatIdFromRoute();
    if (chatId) {
      console.log("Initial chat selection:", chatId);
      this.selectChat(chatId);
    }
  },
};
</script>
