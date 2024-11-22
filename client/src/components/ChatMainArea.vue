<!-- components/ChatMainArea.vue -->
<template>
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
</template>

<script>
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'ChatMainArea',
  
  props: {
    activeChat: {
      type: Object,
      default: null
    }
  }
});
</script>