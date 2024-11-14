<template>
  <div
    :class="[
      'p-4 hover:bg-gray-100 cursor-pointer border-b',
      active ? 'bg-gray-100' : '',
    ]"
    @click="handleClick"
  >
    <div class="flex items-center">
      <!-- Updated Avatar Section -->
      <div
        class="w-10 h-10 rounded-full flex items-center justify-center mr-3 overflow-hidden"
        :class="[!profilePicture ? 'bg-gray-300' : '']"
      >
        <img
          v-if="profilePicture && chat.type === 'direct'"
          :src="profilePicture"
          :alt="displayTitle"
          class="w-full h-full object-cover"
          @error="handleImageError"
        />
        <i
          v-else
          :class="[chatTypeIcon, 'text-gray-600']"
        />
      </div>

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
import { mapState } from 'vuex';

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
    
    displayTitle() {
      if (!this.chat) return '';

      if (this.chat.type === 'direct') {
        if (this.titleError) {
          return 'Error loading name';
        }
        return this.resolvedTitle || 'Loading...';
      }

      return this.chat.title;
    },

    titleClass() {
      return {
        'text-gray-400': !this.resolvedTitle && this.chat.type === 'direct',
        'text-red-500': this.titleError,
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
  },

  watch: {
    chat: {
      immediate: true,
      deep: true,
      handler() {
        this.updateDisplayTitle();
        this.updateProfilePicture();
      }
    },
  },

  methods: {
    handleClick(event) {
      event.stopPropagation();
      console.log("ChatListItem clicked:", this.chat);
      this.$emit("select", this.chat);
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
        (id) => id !== this.currentUser?.userID
      );

      if (otherUserId) {
        try {
          // Fetch user data if needed
          await this.$store.dispatch("chat/fetchUsers", otherUserId);
          
          // Get user from store
          const otherUser = this.$store.state.chat.users.find(
            (user) => user.userID === otherUserId
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
        (id) => id !== this.currentUser?.userID
      );

      if (otherUserId) {
        try {
          await this.$store.dispatch("chat/fetchUsers", otherUserId);
          const otherUser = this.$store.state.chat.users.find(
            (user) => user.userID === otherUserId
          );
          
          if (!otherUser) {
            throw new Error('User not found');
          }
          
          this.resolvedTitle = otherUser.name || otherUser.userName || "Unknown User";
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
}
</style>
```