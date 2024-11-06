<template>
  <div class="w-full">
    <!-- Timestamp -->
    <div v-if="showTimestamp" class="flex justify-center mb-4">
      <div class="bg-gray-100 rounded-full px-3 py-1 text-xs text-gray-500">
        {{ formatTimestamp(message.timestamp) }}
      </div>
    </div>

    <!-- Message Content -->
    <div :class="['w-full flex', isOwn ? 'justify-end' : 'justify-start mb-2']">
      <div class="flex items-end gap-2 message-container">
        <div
          v-if="!isOwn"
          class="w-6 h-6 rounded-full bg-gray-200 flex-shrink-0 flex items-center justify-center avatar"
        >
          <i class="fas fa-user text-gray-600 text-xs"></i>
        </div>
        <div
          :class="[
            'max-w-[70%]',
            'rounded-2xl px-4 py-2 message-content',
            isOwn
              ? 'bg-blue-500 text-white ml-auto rounded-tr-sm own-message'
              : 'bg-[#e9e9eb] text-black mr-auto rounded-tl-sm other-message',
          ]"
        >
          <div
            v-if="showSender"
            class="text-xs font-medium mb-1 sender-name"
            :class="isOwn ? 'text-white/90' : 'text-gray-500'"
          >
            {{ message.senderName }}
          </div>
          <div class="whitespace-pre-wrap break-words">
            {{ message.content }}
          </div>
        </div>
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
    showTimestamp: {
      type: Boolean,
      default: false,
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

<style scoped>
.bg-gray-100 {
  animation: timestamp-appear 0.3s ease forwards;
}

@keyframes timestamp-appear {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message-container {
  animation: message-appear 0.3s ease forwards;
}

.message-content {
  transition: all 0.2s ease;
  position: relative;
  transform-origin: bottom;
}

/* Different animations for own vs other messages */
.own-message {
  animation: slide-left 0.3s ease forwards;
}

.other-message {
  animation: slide-right 0.3s ease forwards;
}

.avatar {
  animation: fade-in 0.3s ease forwards;
}

.sender-name {
  animation: fade-in 0.3s ease 0.1s forwards;
}

.timestamp {
  animation: fade-in 0.3s ease 0.2s forwards;
  opacity: 0;
  animation-fill-mode: forwards;
}

@keyframes message-appear {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slide-left {
  from {
    opacity: 0;
    transform: translateX(20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes slide-right {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes fade-in {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

/* Hover effects */
.message-content:hover {
  transform: scale(1.01);
}

/* Add a subtle shadow effect */
.message-content {
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.message-content:hover {
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.15);
}

/* Optional: Add a shine effect on hover */
.message-content::after {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    45deg,
    transparent,
    rgba(255, 255, 255, 0.1),
    transparent
  );
  opacity: 0;
  transition: opacity 0.3s ease;
}

.message-content:hover::after {
  opacity: 1;
}
</style>
