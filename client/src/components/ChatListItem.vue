<!-- components/ChatListItem.vue -->
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
          {{ chat.title }}
        </div>
        <div class="text-sm text-gray-500 truncate">{{ chat.lastMessage }}</div>
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
  },
};
</script>
