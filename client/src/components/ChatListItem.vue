<template>
  <div
    :class="[
      'p-4 hover:bg-gray-100 cursor-pointer border-b',
      active ? 'bg-gray-100' : '',
      `chat-type-${chat.type}`,
    ]"
    @click="handleClick"
  >
    <div class="flex items-center">
      <!-- Updated Avatar Section -->
      <div class="relative w-12 h-10 mr-3 flex-shrink-0">
        <!-- Single Avatar for Direct Messages -->
        <div
          v-if="chat.type === 'direct'"
          class="w-10 h-10 rounded-full flex items-center justify-center overflow-hidden"
          :class="[!profilePicture ? 'bg-gray-300' : '']"
        >
          <img
            v-if="profilePicture"
            :src="profilePicture"
            :alt="displayTitle"
            class="w-full h-full object-cover"
            @error="handleImageError"
          />
          <i v-else :class="[chatTypeIcon, 'text-gray-600']" />
        </div>

        <!-- Overlapping Avatars for Group Chats -->
        <div v-else-if="chat.type === 'group'" class="relative w-full h-full">
          <template
            v-for="(participant, index) in groupParticipants"
            :key="participant.userID"
          >
            <div
              v-if="index < 4"
              class="absolute rounded-full border-2 border-white overflow-hidden bg-gray-300"
              :class="[
                'w-7 h-7',
                getAvatarPosition(index),
                { 'z-20': index === 0 },
                { 'z-10': index === 1 },
                { 'z-0': index >= 2 },
              ]"
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

        <!-- Organization Logo or Default Icon -->
        <div
          v-else
          class="w-10 h-10 rounded-full bg-gray-100 flex items-center justify-center overflow-hidden"
        >
          <img
            v-if="chat.type === 'organization' && organizationLogo"
            :src="organizationLogo"
            :alt="displayTitle"
            class="w-full h-full object-cover"
            @error="handleImageError"
          />
          <i
            v-else
            :class="[
              chatTypeIcon,
              'text-gray-600 transition-colors duration-300 ease-in-out',
            ]"
          />
        </div>
      </div>

      <!-- Rest of the chat item content -->
      <div class="flex-1 min-w-0">
        <div class="font-semibold truncate flex items-center">
          <i :class="[chatTypeIconSmall, 'mr-2 text-gray-400 text-sm']" />
          <span :class="titleClass" v-html="highlightText(displayTitle)"></span>
        </div>
        <div
          class="text-sm text-gray-500 truncate"
          v-html="highlightText(chat.lastMessage)"
        ></div>
      </div>
      <div
        v-if="chat.unreadCount"
        class="bg-blue-500 text-white rounded-full px-2 py-1 text-xs ml-2"
      >
        {{ chat.unreadCount }}
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";

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
      switch (this.chat.type) {
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
      switch (this.chat.type) {
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
      const positions = {
        0: "top-0 left-0",
        1: "top-0 right-0",
        2: "bottom-0 left-0",
        3: "bottom-0 right-0",
      };
      return positions[index] || "";
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
      if (!this.searchQuery.trim() || !text) {
        return text;
      }

      const query = this.searchQuery.toLowerCase();
      const index = text.toLowerCase().indexOf(query);

      if (index === -1) {
        return text;
      }

      const before = text.slice(0, index);
      const match = text.slice(index, index + query.length);
      const after = text.slice(index + query.length);

      return `${before}<span class="highlight">${match}</span>${after}`;
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
.chat-list-item {
  transition: all 0.3s ease;
  transform-origin: center left;
}

/* Hover animation */
.chat-list-item:hover {
  transform: translateX(4px);
}

/* Avatar animation */
.avatar {
  transition: all 0.3s ease;
}

.chat-list-item:hover .avatar {
  transform: scale(1.05);
  background-color: theme("colors.gray.400");
}

/* Content animation */
.content {
  transition: all 0.3s ease;
}

.chat-list-item:hover .content {
  transform: translateX(4px);
}

/* Badge animation */
.badge {
  transition: all 0.3s ease;
}

.chat-list-item:hover .badge {
  transform: scale(1.1);
}

/* Active state animation */
.chat-list-item.bg-gray-100 {
  transition: background-color 0.3s ease;
}

/* Optional: Add a subtle shadow on hover */
.chat-list-item:hover {
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

/* Optional: Add a ripple effect when clicking */
.chat-list-item:active {
  transform: scale(0.99);
}

/* Optional: Smooth transition for icon colors */
i {
  transition: color 0.3s ease;
}

.chat-list-item:hover i {
  color: theme("colors.gray.800");
}

.highlight {
  background-color: rgba(59, 130, 246, 0.2);
  padding: 0 2px;
  border-radius: 2px;
}

.profile-picture-container {
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}

.profile-picture-container img {
  transition: transform 0.3s ease;
}

.chat-list-item:hover .profile-picture-container img {
  transform: scale(1.1);
}
.group-avatars {
  position: relative;
  width: 40px;
  height: 40px;
}

/* Smooth transitions for avatars */
.group-avatars img {
  transition: all 0.3s ease;
}

/* Hover effects for group avatars */
.group-avatars:hover img {
  transform: scale(1.05);
}

/* Animation for avatar appearance */
@keyframes avatar-pop {
  0% {
    transform: scale(0.8);
    opacity: 0;
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

.rounded-full {
  animation: avatar-pop 0.3s ease forwards;
}

/* Staggered animation delay for group avatars */
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

/* Hover effect for individual avatars */
.rounded-full {
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
}

.rounded-full:hover {
  transform: scale(1.1) !important;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  z-index: 30 !important;
}

/* Additional count indicator styling */
.remaining-count {
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  transition: all 0.3s ease;
}

.remaining-count:hover {
  transform: scale(1.1);
}
.chat-type-organization {
  border-left: 3px solid theme("colors.blue.500");
}

.chat-type-project {
  border-left: 3px solid theme("colors.green.500");
}

.chat-type-task {
  border-left: 3px solid theme("colors.purple.500");
}

.chat-type-direct {
  border-left: 3px solid theme("colors.gray.500");
}

.chat-type-group {
  border-left: 3px solid theme("colors.yellow.500");
}
</style>
```
