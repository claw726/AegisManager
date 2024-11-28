<template>
  <div
    :class="[
      'p-4 hover:bg-gray-100 cursor-pointer border-b transition-all duration-300 origin-left hover:shadow-sm',
      active ? 'bg-gray-100' : '',
      `chat-type-${chat.type}`,
    ]"
    @click="handleClick"
  >
    <div class="flex items-center">
      <!-- Avatar Section -->
      <div class="relative w-12 h-10 mr-3 flex-shrink-0">
        <!-- Direct Message Avatar -->
        <div
          v-if="chat.type === 'direct'"
          class="w-10 h-10 rounded-full flex items-center justify-center overflow-hidden transition-all duration-300"
          :class="[!profilePicture ? 'bg-gray-300' : '']"
        >
          <img
            v-if="profilePicture"
            :src="profilePicture"
            :alt="displayTitle"
            class="w-full h-full object-cover transition-transform duration-300 hover:scale-105"
            @error="handleImageError"
          />
          <i
            v-else
            :class="[
              chatTypeIcon,
              'text-gray-600 transition-colors duration-300',
            ]"
          />
        </div>

        <!-- Group Chat Avatars -->
        <div
          v-else-if="chat.type === 'group'"
          class="relative w-full h-full group"
        >
          <template
            v-for="(participant, index) in groupParticipants"
            :key="participant.userID"
          >
            <div
              v-if="index < 4"
              class="absolute rounded-full border-2 border-white overflow-hidden bg-gray-300 transition-all duration-300"
              :class="[
                'w-7 h-7',
                getAvatarPosition(index),
                { 'z-20': index === 0 },
                { 'z-10': index === 1 },
                { 'z-0': index >= 2 },
              ]"
              :title="participant.userName"
            >
              <img
                v-if="participant.profilePicture"
                :src="participant.profilePicture"
                :alt="participant.userName"
                class="w-full h-full object-cover transition-transform duration-300 hover:scale-105"
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
            class="absolute bottom-0 right-0 bg-black/60 backdrop-blur text-white text-xs rounded-full w-5 h-5 flex items-center justify-center z-30 transition-transform duration-300 hover:scale-110"
          >
            +{{ remainingParticipantsCount }}
          </div>
        </div>

        <!-- Organization/Other Avatar -->
        <div
          v-else
          class="w-10 h-10 rounded-full bg-gray-100 flex items-center justify-center overflow-hidden transition-all duration-300 hover:shadow-md"
        >
          <img
            v-if="chat.type === 'organization' && organizationLogo"
            :src="organizationLogo"
            :alt="displayTitle"
            class="w-full h-full object-cover transition-transform duration-300 hover:scale-105"
            @error="handleImageError"
          />
          <i
            v-else
            :class="[
              chatTypeIcon,
              'text-gray-600 transition-colors duration-300',
            ]"
          />
        </div>
      </div>

      <!-- Content Section -->
      <div class="flex-1 min-w-0">
        <!-- Title -->
        <div class="font-semibold truncate flex items-center">
          <i
            :class="[
              chatTypeIconSmall,
              'mr-2 text-gray-400 text-sm transition-colors duration-300',
            ]"
          />
          <span :class="titleClass" v-html="highlightText(displayTitle)"></span>
        </div>

        <!-- Last Message -->
        <div class="flex flex-col">
          <!-- Message content -->
          <div class="text-sm text-gray-600 truncate">
            <template v-if="chat.lastMessage">
              <span
                v-if="chat.lastMessage.deleted"
                class="italic text-gray-400 flex items-center"
              >
                <i class="fas fa-ban mr-1 text-xs"></i>
                Message deleted
              </span>
              <span
                v-else
                v-html="highlightText(chat.lastMessage.content)"
                class="transition-colors duration-300"
              ></span>
            </template>
            <span v-else class="text-gray-400 italic">No messages yet</span>
          </div>

          <!-- Sender and timestamp -->
          <div
            v-if="chat.lastMessage"
            class="flex items-center text-xs text-gray-400 mt-1 transition-colors duration-300 hover:text-gray-600"
          >
            <span class="font-medium max-w-[120px] truncate" v-html="highlightText(chat.lastMessage.senderName)"/>
            <span class="mx-1">•</span>
            <span>{{ formatTimestamp(chat.lastMessage.timestamp) }}</span>
          </div>
        </div>
      </div>

      <!-- Unread Count Badge -->
      <div
        v-if="chat.unreadCount"
        class="bg-blue-500 text-white rounded-full px-2 py-1 text-xs ml-2 transition-all duration-300 hover:scale-110"
      >
        {{ chat.unreadCount }}
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";
import { CHAT_ICONS, AVATAR_POSITIONS } from '@/constants/chat.js';


export default {
  name: "ChatListItem",
  props: {
    chat: {
      type: Object,
      required: true,
    },
    active: {
      type: Boolean,
      default: false,
    },
    searchQuery: {
      type: String,
      default: "",
    },
  },

  data() {
    return {
      resolvedTitle: "",
      titleError: null,
      profilePicture: null,
      imageLoadError: false,
    };
  },

  computed: {
    ...mapState("auth", ["currentUser"]),
    ...mapState("chat", ["organizations"]),

    groupParticipants() {
      if (!this.chat?.participants) return [];
      return this.chat.participants
        .map((id) =>
          this.$store.state.chat.users.find((user) => user.userID === id),
        )
        .filter((user) => user); // Filter out undefined users
    },

    remainingParticipantsCount() {
      return Math.max(0, this.groupParticipants.length - 4);
    },

    displayTitle() {
      if (!this.chat) return "";

      if (this.chat.type === "direct") {
        if (this.titleError) {
          return "Error loading name";
        }
        return this.resolvedTitle || "Loading...";
      }

      return this.chat.title;
    },

    titleClass() {
      return {
        "text-gray-400": !this.resolvedTitle && this.chat.type === "direct",
        "text-red-500": this.titleError,
      };
    },

    chatTypeIcon() {
      return CHAT_ICONS[this.chat?.type]?.large || CHAT_ICONS.default.large;
    },

    chatTypeIconSmall() {
      return CHAT_ICONS[this.chat?.type]?.small || CHAT_ICONS.default.small;
    },

    organizationLogo() {
      if (this.chat.type === "organization") {
        // Find matching organization by name
        const matchingOrg = this.organizations.find(
          (org) => org.orgName.toLowerCase() === this.chat.title.toLowerCase(),
        );
        return matchingOrg?.encodedImage || null;
      }
      return null;
    },
  },

  watch: {
    chat: {
      immediate: true,
      deep: true,
      handler() {
        this.updateDisplayTitle();
        this.updateProfilePicture();
      },
    },
  },

  methods: {
    handleClick(event) {
      event.stopPropagation();
      console.log("ChatListItem clicked:", this.chat);
      this.$emit("select", this.chat);
    },

    getAvatarPosition(index) {
      return AVATAR_POSITIONS[index] || '';
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

    highlightText(text) {
      // Early return if no search query or text
      if (!this.searchQuery?.trim() || !text) {
        return text;
      }

      try {
        // Escape special characters in the search query to prevent regex errors
        const escapedQuery = this.searchQuery.trim().replace(/[-\/\\^$*+?.()|[${}]/g, '\\$&');

        // Create a case-insensitive regular expression
        const regex = new RegExp(escapedQuery, 'gi');

        // Replace matches with highlighted spans
        return text.replace(regex, match => 
          `<span class="highlight">${match}</span>`
        );
      } catch (error) {
        console.error('Regex error in highlightText:', error);
        return text; // Return original text if there's an error
      }
    },

    formatTimestamp(timestamp) {
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
    },

    handleImageError() {
      this.imageLoadError = true;
      this.profilePicture = null;
    },

    async updateProfilePicture() {
      if (this.chat?.type !== "direct" || this.imageLoadError) {
        this.profilePicture = null;
        return;
      }

      const otherUserId = this.chat.participants.find(
        (id) => id !== this.currentUser?.userID,
      );

      if (otherUserId) {
        try {
          // Fetch user data if needed
          await this.$store.dispatch("chat/fetchUsers", otherUserId);

          // Get user from store
          const otherUser = this.$store.state.chat.users.find(
            (user) => user.userID === otherUserId,
          );

          if (otherUser?.profilePicture) {
            this.profilePicture = otherUser.profilePicture;
            this.imageLoadError = false;
          }
        } catch (error) {
          console.error("Error loading profile picture:", error);
          this.handleImageError();
        }
      }
    },

    async updateDisplayTitle() {
      this.titleError = null;

      if (this.chat?.type !== "direct") {
        this.resolvedTitle = this.chat?.title;
        return;
      }

      const otherUserId = this.chat.participants.find(
        (id) => id !== this.currentUser?.userID,
      );

      if (otherUserId) {
        try {
          await this.$store.dispatch("chat/fetchUsers", otherUserId);
          const otherUser = this.$store.state.chat.users.find(
            (user) => user.userID === otherUserId,
          );

          if (!otherUser) {
            throw new Error("User not found");
          }

          this.resolvedTitle =
            otherUser.name || otherUser.userName || "Unknown User";
        } catch (error) {
          console.error("Error loading chat title:", error);
          this.titleError = error;
          this.resolvedTitle = "Error loading name";
        }
      }
    },
  },
};
</script>
<style scoped>
/* Animation keyframes */
@keyframes avatar-pop {
0% { @apply scale-75 opacity-0; }
100% { @apply scale-100 opacity-100; }
}

.avatar-pop {
animation: avatar-pop 0.3s ease forwards;
}

:deep(.highlight) {
@apply bg-yellow-200 px-0.5 rounded inline transition-all duration-200 hover:bg-yellow-300;
}

:deep(.highlight:hover) {
@apply bg-yellow-300;
}

/* Animation classes */
.rounded-full {
  animation: avatar-pop 0.3s ease forwards;
}

.rounded-full:nth-child(1) {
  animation-delay: 0s;
}
.rounded-full:nth-child(2) {
  animation-delay: 0.1s;
}
.rounded-full:nth-child(3) {
  animation-delay: 0.2s;
}
.rounded-full:nth-child(4) {
  animation-delay: 0.3s;
}

/* Chat type indicators */
.chat-type-organization { @apply border-l-4 border-blue-500; }
.chat-type-project { @apply border-l-4 border-green-500; }
.chat-type-task { @apply border-l-4 border-purple-500; }
.chat-type-direct { @apply border-l-4 border-gray-500; }
.chat-type-group { @apply border-l-4 border-yellow-500; }

/* Complex hover interactions */
.chat-list-item:hover .profile-picture-container img {
  transform: scale(1.1);
}

.group-avatars:hover img {
  transform: scale(1.05);
}

.rounded-full:hover {
  transform: scale(1.1) !important;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  z-index: 30 !important;
}
</style>
