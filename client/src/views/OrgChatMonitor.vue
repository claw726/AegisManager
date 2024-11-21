<template>
  <div class="p-4 min-h-screen bg-grid-pattern">
    <!-- Search bar -->
    <div class="mb-6 max-w-2xl mx-auto">
      <div class="relative">
        <i class="fas fa-search absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400"></i>
        <input
          v-model="searchQuery"
          type="text"
          placeholder="Search messages..."
          class="w-full p-3 pl-10 border rounded-lg bg-white/90 backdrop-blur-sm shadow-lg"
        />
      </div>
    </div>

    <!-- Chat grid -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 max-w-7xl mx-auto">
      <div v-for="chat in filteredChats" 
           :key="chat.chat.id" 
           @click="openChatModal(chat)"
           class="bg-white/90 backdrop-blur-sm rounded-lg shadow-lg p-4 hover:shadow-xl transition-all duration-200 transform hover:-translate-y-1 cursor-pointer border border-gray-100">
        <div class="flex justify-between items-center mb-4">
          <div>
            <h3 class="text-lg font-semibold text-gray-800">
              {{ chat.chat.title }}
            </h3>
            <span :class="getChatTypeBadgeClass(chat.chat.type)"
                  class="inline-block px-2 py-1 rounded-full text-xs font-medium mt-1">
              {{ formatChatType(chat.chat.type) }}
            </span>
          </div>
          <span class="text-sm bg-gray-100 px-2 py-1 rounded-full">
            {{ chat.messages?.length || 0 }} messages
          </span>
        </div>

        <div class="text-sm text-gray-600 mb-4">
          <div class="flex items-center gap-1">
            <i class="fas fa-calendar-alt text-gray-400"></i>
            <span>Created {{ formatDate(chat.chat.createdDate) }}</span>
          </div>
        </div>
        
        <div v-if="chat.chat.lastMessage" class="border-t pt-4">
          <p class="text-sm font-medium text-gray-500 mb-2">Latest message:</p>
          <div class="bg-gray-50 rounded-lg p-3">
            <div class="flex items-start gap-3">
              <div class="flex-shrink-0">
                <div class="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center">
                  <span class="text-sm font-medium text-blue-800">
                    {{ getInitials(chat.chat.lastMessage.senderName) }}
                  </span>
                </div>
              </div>
              <div class="flex-1 min-w-0">
                <p class="text-sm font-medium text-gray-900">
                  {{ chat.chat.lastMessage.senderName }}
                </p>
                <p class="text-sm text-gray-700 break-words">
                  {{ chat.chat.lastMessage.content }}
                </p>
                <p class="text-xs text-gray-500 mt-1">
                  {{ formatDate(chat.chat.lastMessage.timestamp) }}
                </p>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="border-t pt-4 text-gray-500 text-sm">
          No messages yet
        </div>
      </div>
    </div>

    <!-- Chat Modal -->
    <TransitionRoot appear :show="isModalOpen" as="template">
      <Dialog as="div" @close="closeModal" class="relative z-50">
        <TransitionChild
          as="template"
          enter="duration-300 ease-out"
          enter-from="opacity-0"
          enter-to="opacity-100"
          leave="duration-200 ease-in"
          leave-from="opacity-100"
          leave-to="opacity-0"
        >
          <div class="fixed inset-0 bg-black/30 backdrop-blur-sm" />
        </TransitionChild>

        <div class="fixed inset-0 overflow-y-auto">
          <div class="flex min-h-full items-center justify-center p-4">
            <TransitionChild
              as="template"
              enter="duration-300 ease-out"
              enter-from="opacity-0 scale-95"
              enter-to="opacity-100 scale-100"
              leave="duration-200 ease-in"
              leave-from="opacity-100 scale-100"
              leave-to="opacity-0 scale-95"
            >
              <DialogPanel class="w-full max-w-3xl transform overflow-hidden rounded-2xl bg-white p-6 shadow-xl transition-all">
                <div class="flex justify-between items-center mb-4">
                  <DialogTitle as="h3" class="text-xl font-semibold text-gray-900">
                    {{ selectedChat?.chat.title }}
                  </DialogTitle>
                  <button @click="closeModal" class="text-gray-500 hover:text-gray-700">
                    <i class="fas fa-times"></i>
                  </button>
                </div>

                <div class="mt-4 space-y-4 max-h-[60vh] overflow-y-auto px-2">
                  <div v-for="message in selectedChat?.messages" 
                       :key="message.id"
                       class="flex items-start gap-3 p-3 rounded-lg hover:bg-gray-50">
                    <div class="flex-shrink-0">
                      <div class="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center">
                        <span class="text-sm font-medium text-blue-800">
                          {{ getInitials(message.senderName) }}
                        </span>
                      </div>
                    </div>
                    <div class="flex-1">
                      <div class="flex items-baseline gap-2">
                        <span class="font-medium text-gray-900">{{ message.senderName }}</span>
                        <span class="text-xs text-gray-500">{{ formatDate(message.timestamp) }}</span>
                      </div>
                      <p class="text-gray-700 mt-1">{{ message.content }}</p>
                    </div>
                  </div>
                </div>
              </DialogPanel>
            </TransitionChild>
          </div>
        </div>
      </Dialog>
    </TransitionRoot>
  </div>
</template>

<script>
import { defineComponent, ref, computed, onMounted } from 'vue';
import { useStore } from 'vuex';
import { useRoute } from 'vue-router';
import {
  TransitionRoot,
  TransitionChild,
  Dialog,
  DialogPanel,
  DialogTitle,
} from '@headlessui/vue';


export default defineComponent({
  name: 'OrgChatMonitor',
  
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
      required: true
    },
    organizationName: {
      type: String,
      required: true
    }
  },

  setup(props) {
    const store = useStore();
    const route = useRoute();
    const chats = ref([]);
    const searchQuery = ref('');
    const isModalOpen = ref(false);
    const selectedChat = ref(null);

    const loadChats = async () => {
      try {
        const orgID = route.params.orgIndex;
        const data = await store.dispatch('chat/fetchOrgMessages', orgID);
        chats.value = data || [];
      } catch (error) {
        console.error('Error loading chats:', error);
        chats.value = [];
      }
    };

    const openChatModal = (chat) => {
      selectedChat.value = chat;
      isModalOpen.value = true;
    };

    const closeModal = () => {
      isModalOpen.value = false;
      selectedChat.value = null;
    };

    const filteredChats = computed(() => {
      if (!searchQuery.value) return chats.value;
      
      const query = searchQuery.value.toLowerCase();
      return chats.value.filter(chat => {
        const chatData = chat.chat;
        const messages = chat.messages || [];

        return chatData.title.toLowerCase().includes(query) ||
            messages.some(message =>
                message.content.toLowerCase().includes(query) ||
                message.senderName.toLowerCase().includes(query)
            );
      });
    });

    const getChatTypeBadgeClass = (type) => {
      const classes = {
        'project': 'bg-blue-100 text-blue-800',
        'organization': 'bg-green-100 text-green-800',
        'direct': 'bg-purple-100 text-purple-800',
        'group': 'bg-yellow-100 text-yellow-800'
      };
      return classes[type] || 'bg-gray-100 text-gray-800';
    };

    const formatChatType = (type) => {
      return type ? type.charAt(0).toUpperCase() + type.slice(1) : 'Unknown';
    };

    const formatDate = (timestamp) => {
      if (!timestamp) return '';
      return new Date(timestamp).toLocaleString();
    };

    const getInitials = (name) => {
      return name
        .split(' ')
        .map(word => word[0])
        .join('')
        .toUpperCase()
        .slice(0, 2);
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
      getInitials,
      isModalOpen,
      selectedChat,
      openChatModal,
      closeModal
    };
  }
});
</script>

<style>
.bg-grid-pattern {
  background-color: #f8fafc;
  background-image: linear-gradient(#e2e8f0 1px, transparent 1px),
    linear-gradient(90deg, #e2e8f0 1px, transparent 1px);
  background-size: 20px 20px;
}
</style>