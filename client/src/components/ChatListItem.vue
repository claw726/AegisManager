<template>
  <div
    :class="[
      'p-4 hover:bg-gray-100 cursor-pointer border-b',
      active ? 'bg-gray-100' : '',
    ]"
    @click="handleClick"
  >
    <div class="flex items-center">
      <div
        class="w-10 h-10 rounded-full bg-gray-300 flex items-center justify-center mr-3"
      >
        <i :class="[chatTypeIcon, 'text-gray-600']" />
      </div>
      <div class="flex-1 min-w-0">
        <div class="font-semibold truncate flex items-center">
          <i :class="[chatTypeIconSmall, 'mr-2 text-gray-400 text-sm']" />
          <span v-html="highlightText(chat.title)"></span>
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
  computed: {
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
  methods: {
    handleClick(event) {
      event.stopPropagation();
      console.log("ChatListItem clicked:", this.chat);
      this.$emit("select", this.chat);
    },
    highlightText(text) {
      if (!this.searchQuery.trim()) {
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
</style>
```
