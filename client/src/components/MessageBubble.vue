<template>
  <div :class="['w-full flex', isOwn ? 'justify-end' : 'justify-start mb-2']">
    <div class="flex items-end gap-2">
      <div
        v-if="!isOwn"
        class="w-6 h-6 rounded-full bg-gray-200 flex-shrink-0 flex items-center justify-center"
      >
        <i class="fas fa-user text-gray-600 text-xs"></i>
      </div>
      <div
        :class="[
          'max-w-[70%]',
          'rounded-2xl px-4 py-2',
          isOwn
            ? 'bg-blue-500 text-white ml-auto rounded-tr-sm'
            : 'bg-[#e9e9eb] text-black mr-auto rounded-tl-sm',
        ]"
      >
        <div
          v-if="showSender"
          class="text-xs font-medium mb-1"
          :class="isOwn ? 'text-white/90' : 'text-gray-500'"
        >
          {{ message.senderName }}
        </div>
        <div class="whitespace-pre-wrap break-words">
          {{ message.content }}
        </div>
      </div>
      <div
        class="text-xs mt-1 text-right"
        :class="isOwn ? 'text-white/70' : 'text-gray-500'"
      >
        {{ formatTimestamp(message.timestamp) }}
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "MessageBubble",
  props: {
    message: {
      type: Object,
      required: true,
    },
    isOwn: {
      type: Boolean,
      default: false,
    },
    showSender: {
      type: Boolean,
      default: true,
    },
  },
  methods: {
    formatTimestamp(timestamp) {
      return new Date(timestamp).toLocaleTimeString([], {
        hour: "numeric",
        minute: "2-digit",
      });
    },
  },
};
</script>