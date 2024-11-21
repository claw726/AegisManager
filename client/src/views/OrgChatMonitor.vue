<template>
  <div class="p-4 min-h-screen bg-gray-50">
    <!-- Header -->
    <div class="max-w-7xl mx-auto mb-6">
      <div
        class="bg-white rounded-lg shadow-sm p-4 flex items-center justify-between"
      >
        <div class="flex items-center gap-3">
          <i class="fas fa-comments text-blue-500 text-xl"></i>
          <h1 class="text-xl font-semibold text-gray-800">
            Organization Messages
          </h1>
        </div>
        <div class="relative">
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Search messages..."
            class="w-64 px-4 py-2 rounded-full bg-gray-100 focus:bg-white focus:ring-2 focus:ring-blue-500 focus:outline-none transition-all"
          />
          <i
            class="fas fa-search absolute right-4 top-1/2 transform -translate-y-1/2 text-gray-400"
          ></i>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center items-center min-h-[200px]">
      <div class="flex flex-col items-center gap-2">
        <div
          class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"
        ></div>
        <p class="text-gray-600">Loading messages...</p>
      </div>
    </div>

    <!-- Chat Grid -->
    <div
      v-else
      class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 max-w-7xl mx-auto"
    >
      <div
        v-for="chat in filteredChats"
        :key="chat.chat.id"
        @click="openChatModal(chat)"
        class="bg-white rounded-2xl shadow-sm hover:shadow-md transition-all duration-200 cursor-pointer border border-gray-100 overflow-hidden"
      >
        <!-- Chat Header -->
        <div class="p-4 border-b border-gray-100">
          <div class="flex items-center justify-between mb-2">
            <div class="flex items-center gap-2">
              <span class="font-semibold text-gray-800">
                {{ getShortChatDisplayName(chat?.chat) }}
              </span>
              <span
                :class="getChatTypeBadgeClass(chat.chat.type)"
                class="px-2 py-0.5 rounded-full text-xs font-medium"
              >
                {{ formatChatType(chat.chat.type) }}
              </span>
            </div>
            <i class="fas fa-chevron-right text-gray-400"></i>
          </div>
          <p class="text-sm text-gray-500">
            {{ chat.messages.length }} messages
          </p>
        </div>

        <!-- Last Message Preview -->
        <div v-if="chat.chat.lastMessage" class="p-4 bg-gray-50">
          <div class="flex items-start gap-3">
            <div
              v-if="getUserProfilePicture(chat.chat.lastMessage.senderID)"
              class="w-8 h-8"
            >
              <img
                :src="getUserProfilePicture(chat.chat.lastMessage.senderID)"
                :alt="chat.chat.lastMessage.senderName"
                class="w-full h-full rounded-full object-cover"
                @error="handleImageError(chat.chat.lastMessage.senderID)"
              />
            </div>
            <div
              v-else
              class="w-8 h-8 rounded-full flex items-center justify-center"
              :class="
                getInitialsBackgroundColor(chat.chat.lastMessage.senderID)
              "
            >
              <span class="text-sm font-medium text-white">
                {{ getInitials(chat.chat.lastMessage.senderName) }}
              </span>
            </div>
            <div class="flex-1">
              <p class="text-sm font-medium text-gray-900">
                {{ chat.chat.lastMessage.senderName }}
              </p>
              <div class="relative">
                <p
                  class="text-sm text-gray-700 break-words"
                  :class="{ 'text-gray-400': chat.chat.lastMessage.deleted }"
                >
                  {{ chat.chat.lastMessage.content }}
                </p>
                <div
                  v-if="chat.chat.lastMessage.deleted"
                  class="flex items-center gap-1 text-xs text-red-500 mt-1"
                >
                  <i class="fas fa-trash-alt"></i>
                  <span>Message deleted</span>
                </div>
              </div>
              <p class="text-xs text-gray-500 mt-1">
                {{ formatDate(chat.chat.lastMessage.timestamp) }}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Chat Modal -->
    <TransitionRoot appear :show="isModalOpen" as="template">
      <Dialog as="div" @close="closeModal" class="relative z-50">
        <div class="fixed inset-0 bg-black/20 backdrop-blur-sm"></div>

        <div class="fixed inset-0 overflow-y-auto">
          <div class="flex min-h-full items-center justify-center p-4">
            <DialogPanel
              class="w-full max-w-3xl transform overflow-hidden rounded-2xl bg-white shadow-xl"
            >
              <!-- Modal Header -->
              <div class="bg-gray-50 px-6 py-4 border-b border-gray-100">
                <div class="flex items-center justify-between">
                  <div class="flex items-center gap-3">
                    <button
                      @click="closeModal"
                      class="text-gray-500 hover:text-gray-700 transition-colors"
                    >
                      <i class="fas fa-times text-red-600"></i>
                    </button>
                    <h3 class="text-lg font-semibold text-gray-800">
                      {{ getChatDisplayName(selectedChat?.chat) }}
                    </h3>
                    <p
                      v-if="selectedChat?.chat?.participants?.length > 0"
                      class="text-sm text-gray-500 mt-1"
                    >
                      {{ selectedChat.chat.participants.length }}
                      {{
                        selectedChat.chat.participants.length === 1
                          ? "participant"
                          : "participants"
                      }}
                    </p>
                  </div>
                  <span
                    :class="getChatTypeBadgeClass(selectedChat?.chat.type)"
                    class="px-3 py-1 rounded-full text-sm font-medium"
                  >
                    {{ formatChatType(selectedChat?.chat.type) }}
                  </span>
                </div>
              </div>

              <!-- Messages -->
              <div class="max-h-[60vh] overflow-y-auto p-6 space-y-4">
                <div
                  v-for="message in selectedChat?.messages"
                  :key="message.id"
                  class="flex items-start gap-3"
                >
                  <div
                    v-if="getUserProfilePicture(message.senderID)"
                    class="w-10 h-10"
                  >
                    <img
                      :src="getUserProfilePicture(message.senderID)"
                      :alt="message.senderName"
                      class="w-full h-full rounded-full object-cover"
                      @error="handleImageError(message.senderID)"
                    />
                  </div>
                  <div
                    v-else
                    class="w-10 h-10 rounded-full flex items-center justify-center"
                    :class="getInitialsBackgroundColor(message.senderID)"
                  >
                    <span class="text-sm font-medium text-white">
                      {{ getInitials(message.senderName) }}
                    </span>
                  </div>
                  <div class="flex-1">
                    <div class="flex items-baseline gap-2">
                      <span class="font-medium text-gray-900">{{
                        message.senderName
                      }}</span>
                      <span class="text-xs text-gray-500">{{
                        formatDate(message.timestamp)
                      }}</span>
                    </div>
                    <div
                      class="mt-1 p-3 rounded-2xl bg-gray-100 relative"
                      :class="{ 'bg-gray-50': message.deleted }"
                    >
                      <p
                        class="text-gray-700"
                        :class="{ 'text-gray-400': message.deleted }"
                      >
                        {{ message.content }}
                      </p>
                      <div
                        v-if="message.deleted"
                        class="flex items-center gap-1 text-xs text-red-500 mt-2"
                      >
                        <i class="fas fa-trash-alt"></i>
                        <span>Deleted {{ formatDate(message.deletedAt) }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </DialogPanel>
          </div>
        </div>
      </Dialog>
    </TransitionRoot>
  </div>
</template>

<script>
import { defineComponent, ref, computed, onMounted } from "vue";
import { useStore } from "vuex";
import { useRoute } from "vue-router";
import {
  TransitionRoot,
  TransitionChild,
  Dialog,
  DialogPanel,
  DialogTitle,
} from "@headlessui/vue";

export default defineComponent({
  name: "OrgChatMonitor",

  components: {
    TransitionRoot,
    TransitionChild,
    Dialog,
    DialogPanel,
    DialogTitle,
  },

  props: {
    organizationId: {
      type: [Number, String],
      required: true,
    },
    organizationName: {
      type: String,
      required: true,
    },
  },

  setup(props) {
    const store = useStore();
    const route = useRoute();
    const chats = ref([]);
    const searchQuery = ref("");
    const isModalOpen = ref(false);
    const selectedChat = ref(null);
    const failedImages = ref(new Set());
    const loading = ref(true);
    const error = ref(null);

    const loadChats = async () => {
      loading.value = true;
      error.value = null;
      try {
        await store.dispatch("chat/fetchUsers");
        const orgID = route.params.orgIndex;
        const data = await store.dispatch("chat/fetchOrgMessages", orgID);
        chats.value = data || [];
      } catch (error) {
        console.error("Error loading chats:", error);
        error.value = error;
        chats.value = [];
      } finally {
        loading.value = false;
      }
    };

    const openChatModal = (chat) => {
      if (!chat) return;
      selectedChat.value = chat;
      isModalOpen.value = true;
    };

    const closeModal = () => {
      isModalOpen.value = false;
      setTimeout(() => {
        selectedChat.value = null;
      }, 300);
    };

    const getUserProfilePicture = (userId) => {
      if (failedImages.value.has(userId)) return null;
      const user = store.getters["chat/getUserById"](userId);
      return user?.profilePicture || null;
    };

    const handleImageError = (userId) => {
      failedImages.value.add(userId);
    };

    const filteredChats = computed(() => {
      if (!chats.value) return [];

      return chats.value.filter((chat) => {
        if (!chat?.chat) return false;

        const searchLower = searchQuery.value.toLowerCase();
        const displayName = getChatDisplayName(chat.chat).toLowerCase();
        const messageContent =
          chat.messages?.some((msg) =>
            msg?.content?.toLowerCase().includes(searchLower),
          ) || false;

        return displayName.includes(searchLower) || messageContent;
      });
    });

    const getChatTypeBadgeClass = (type) => {
      if (!type) return "bg-gray-100 text-gray-800";
      const classes = {
        project: "bg-blue-100 text-blue-800",
        organization: "bg-green-100 text-green-800",
        direct: "bg-purple-100 text-purple-800",
        group: "bg-yellow-100 text-yellow-800",
      };
      return classes[type] || "bg-gray-100 text-gray-800";
    };

    const formatChatType = (type) => {
      return type ? type.charAt(0).toUpperCase() + type.slice(1) : "Unknown";
    };

    const formatDate = (timestamp) => {
      if (!timestamp) return "";

      const date = new Date(timestamp + "Z");
      const now = new Date();
      const diff = now - date;

      // Less than 24 hours ago
      if (diff < 24 * 60 * 60 * 1000) {
        return date.toLocaleTimeString([], {
          hour: "2-digit",
          minute: "2-digit",
          timeZone: Intl.DateTimeFormat().resolvedOptions().timeZone,
        });
      }

      // Less than 7 days ago
      if (diff < 7 * 24 * 60 * 60 * 1000) {
        const days = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
        return days[date.getDay()];
      }

      // More than 7 days ago
      return date.toLocaleDateString([], {
        month: "short",
        day: "numeric",
        timeZone: Intl.DateTimeFormat().resolvedOptions().timeZone,
      });
    };

    const getChatDisplayName = (chat) => {
      if (!chat) return "";

      if (chat.type !== "group" && chat.type !== "direct") {
        return chat.title || "Unnamed Channel";
      }

      // For both DIRECT and GROUP chats, show participant names
      if (!chat.participants) return "Unknown Chat";

      const participantNames = chat.participants
        .map((userId) => {
          const user = store.getters["chat/getUserById"](userId);
          return user?.userName || "Unknown User";
        })
        .filter(Boolean) // Remove any undefined/null values
        .sort();

      // For group chats, add participant count if more than 3 people
      if (chat.type === "group" && participantNames.length > 3) {
        const firstThreeNames = participantNames.slice(0, 3).join(", ");
        const remainingCount = participantNames.length - 3;
        return `${firstThreeNames} + ${remainingCount} ${remainingCount === 1 ? "other" : "others"}`;
      }
      // For direct messages and smaller group chats, show all names
      return participantNames.join(", ") || "Unknown Chat";
    };

    // Add a shorter version for previews (e.g., in the chat list)
    const getShortChatDisplayName = (chat) => {
      if (!chat) return "";

      if (chat.type !== "group" && chat.type !== "direct") {
        return chat.title || "Unnamed Channel";
      }

      if (!chat.participants) return "Unknown Chat";

      const participantNames = chat.participants
        .map((userId) => {
          const user = store.getters["chat/getUserById"](userId);
          return user?.userName || "Unknown User";
        })
        .filter(Boolean)
        .sort();

      // Always truncate to 2 names + count for preview
      if (participantNames.length > 2) {
        const firstTwoNames = participantNames.slice(0, 2).join(", ");
        const remainingCount = participantNames.length - 2;
        return `${firstTwoNames} + ${remainingCount} ${remainingCount === 1 ? "other" : "others"}`;
      }

      return participantNames.join(", ") || "Unknown Chat";
    };

    const getInitials = (name) => {
      return name
        .split(" ")
        .map((word) => word[0])
        .join("")
        .toUpperCase()
        .slice(0, 2);
    };

    const getInitialsBackgroundColor = (userId) => {
      const colors = [
        "bg-blue-500",
        "bg-green-500",
        "bg-yellow-500",
        "bg-red-500",
        "bg-purple-500",
        "bg-pink-500",
        "bg-indigo-500",
        "bg-teal-500",
      ];

      // Use userId to consistently pick a color
      const colorIndex =
        typeof userId === "number"
          ? userId % colors.length
          : String(userId)
              .split("")
              .reduce((acc, char) => acc + char.charCodeAt(0), 0) %
            colors.length;

      return colors[colorIndex];
    };

    onMounted(() => {
      loadChats();
    });

    return {
      filteredChats,
      searchQuery,
      getChatTypeBadgeClass,
      formatChatType,
      formatDate,
      isModalOpen,
      selectedChat,
      openChatModal,
      closeModal,
      getUserProfilePicture,
      handleImageError,
      getInitials,
      getInitialsBackgroundColor,
      loading,
      error,
      getChatDisplayName,
      getShortChatDisplayName,
    };
  },
});
</script>

<style>
/* Optional: Add smooth scrolling */
.overflow-y-auto {
  scrollbar-width: thin;
  scrollbar-color: #cbd5e1 transparent;
}

.overflow-y-auto::-webkit-scrollbar {
  width: 6px;
}

.overflow-y-auto::-webkit-scrollbar-track {
  background: transparent;
}

.overflow-y-auto::-webkit-scrollbar-thumb {
  background-color: #cbd5e1;
  border-radius: 3px;
}
</style>