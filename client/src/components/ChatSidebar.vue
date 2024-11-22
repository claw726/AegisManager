<!-- components/ChatSidebar.vue -->
<template>
  <div class="w-80 border-r bg-[#f7f7f7] flex flex-col">
    <!-- Search bar -->
    <div class="p-3 border-b flex-shrink-0">
      <div class="relative">
        <input
          v-model="localSearchQuery"
          type="text"
          placeholder="Search"
          class="w-full px-4 py-2 pl-10 bg-[#ffffff] rounded-full border-none focus:ring-2 focus:ring-blue-500"
        />
        <i class="fas fa-search absolute left-4 top-3 text-gray-400"></i>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex-1 flex items-center justify-center">
      <div class="text-center">
        <i class="fas fa-spinner fa-spin text-2xl text-gray-400 mb-2"></i>
        <p class="text-gray-600">Loading chats...</p>
      </div>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="flex-1 flex items-center justify-center">
      <div class="text-center text-red-600">
        <i class="fas fa-exclamation-circle text-2xl mb-2"></i>
        <p>{{ error }}</p>
        <button
          @click="$emit('retry')"
          class="mt-2 text-sm text-blue-600 hover:underline"
        >
          Retry
        </button>
      </div>
    </div>

    <!-- Chat List -->
    <div v-else class="flex-1 overflow-y-auto min-h-0">
      <TransitionGroup name="chat-list" tag="div">
        <!-- Organizations -->
        <template
          v-for="org in organizationStructure"
          :key="`org-${org.orgID}`"
        >
          <!-- Organization Header -->
          <div
            class="px-4 py-2 text-sm font-semibold text-gray-500 bg-gray-100 cursor-pointer hover:bg-gray-200 transition-colors flex items-center justify-between"
            @click="$emit('toggleCategory', `org-${org.orgID}`)"
          >
            <div class="flex items-center">
              <i class="fas fa-building mr-2"></i>
              {{ org.orgName }}
            </div>
            <i
              :class="[
                'fas',
                expandedCategories[`org-${org.orgID}`]
                  ? 'fa-chevron-down'
                  : 'fa-chevron-right',
                'text-xs transition-transform duration-200',
              ]"
            ></i>
          </div>

          <!-- Organization Content -->
          <div v-show="expandedCategories[`org-${org.orgID}`]" class="ml-4">
            <!-- Organization Chat -->
            <ChatListItem
              v-if="org.chat"
              :key="`orgchat-${org.orgID}`"
              :chat="org.chat"
              :active="activeChat?.id === org.chat.id"
              :searchQuery="localSearchQuery"
              @select="$emit('chatSelect', $event)"
              class="border-l-2 border-gray-200"
            />

            <!-- Projects within Organization -->
            <template
              v-for="project in org.projects"
              :key="`project-${project.projectID}`"
            >
              <!-- Project Header -->
              <div
                class="px-4 py-2 text-sm font-semibold text-gray-500 bg-gray-50 cursor-pointer hover:bg-gray-100 transition-colors flex items-center justify-between border-l-2 border-gray-200"
                @click="$emit('toggleCategory', `project-${project.projectID}`)"
              >
                <div class="flex items-center">
                  <i class="fas fa-project-diagram mr-2"></i>
                  {{ project.projectName }}
                </div>
                <i
                  :class="[
                    'fas',
                    expandedCategories[`project-${project.projectID}`]
                      ? 'fa-chevron-down'
                      : 'fa-chevron-right',
                    'text-xs transition-transform duration-200',
                  ]"
                ></i>
              </div>

              <!-- Project Content -->
              <div
                v-show="expandedCategories[`project-${project.projectID}`]"
                class="ml-4 border-l-2 border-gray-200"
              >
                <!-- Project Chat -->
                <ChatListItem
                  v-if="project.chat"
                  :key="`projectchat-${project.projectID}`"
                  :chat="project.chat"
                  :active="activeChat?.id === project.chat.id"
                  :searchQuery="localSearchQuery"
                  @select="$emit('chatSelect', $event)"
                />

                <!-- Tasks Section -->
                <template v-if="project.tasks && project.tasks.length > 0">
                  <div
                    class="px-4 py-2 text-sm font-semibold text-gray-500 bg-gray-50 cursor-pointer hover:bg-gray-100 transition-colors flex items-center justify-between"
                    @click="
                      $emit('toggleCategory', `tasks-${project.projectID}`)
                    "
                  >
                    <div class="flex items-center">
                      <i class="fas fa-tasks mr-2"></i>
                      Tasks
                    </div>
                    <i
                      :class="[
                        'fas',
                        expandedCategories[`tasks-${project.projectID}`]
                          ? 'fa-chevron-down'
                          : 'fa-chevron-right',
                        'text-xs transition-transform duration-200',
                      ]"
                    ></i>
                  </div>

                  <div
                    v-show="expandedCategories[`tasks-${project.projectID}`]"
                  >
                    <ChatListItem
                      v-for="task in project.tasks"
                      :key="`task-${task.taskID}`"
                      v-if="task && task.chat"
                      :chat="task.chat"
                      :active="activeChat?.id === task.chat.id"
                      :searchQuery="localSearchQuery"
                      @select="$emit('chatSelect', $event)"
                      class="ml-4"
                    />
                  </div>
                </template>
              </div>
            </template>
          </div>
        </template>

        <!-- Direct Messages Section -->
        <template v-if="categorizedChats.direct.length">
          <div
            class="category-header px-4 py-2 text-sm font-semibold text-gray-500 bg-gray-100"
          >
            Direct Messages
          </div>
          <ChatListItem
            v-for="chat in categorizedChats.direct"
            :key="chat.id"
            :chat="chat"
            :active="activeChat?.id === chat.id"
            :searchQuery="localSearchQuery"
            @select="$emit('chatSelect', $event)"
          />
        </template>

        <!-- Groups Section -->
        <template v-if="categorizedChats.groups.length">
          <div
            class="category-header px-4 py-2 text-sm font-semibold text-gray-500 bg-gray-100"
          >
            Groups
          </div>
          <ChatListItem
            v-for="chat in categorizedChats.groups"
            :key="chat.id"
            :chat="chat"
            :active="activeChat?.id === chat.id"
            :searchQuery="localSearchQuery"
            @select="$emit('chatSelect', $event)"
          />
        </template>
      </TransitionGroup>
    </div>

    <!-- New Chat Button -->
    <div class="p-3 border-t flex-shrink-0">
      <button
        class="w-full bg-blue-500 hover:bg-blue-600 text-white rounded-full px-4 py-2 flex items-center justify-center transition-colors"
        @click="$emit('showNewChat')"
      >
        <i class="fas fa-pen mr-2"></i>
        New Message
      </button>
    </div>
  </div>
</template>

<script>
import { defineComponent } from "vue";
import ChatListItem from "./ChatListItem.vue";

export default defineComponent({
  name: "ChatSidebar",

  components: {
    ChatListItem,
  },

  props: {
    loading: {
      type: Boolean,
      default: false,
    },
    error: {
      type: String,
      default: null,
    },
    searchQuery: {
      type: String,
      required: true,
    },
    organizationStructure: {
      type: Array,
      required: true,
    },
    expandedCategories: {
      type: Object,
      required: true,
    },
    activeChat: {
      type: Object,
      default: null,
    },
    categorizedChats: {
      type: Object,
      required: true,
    },
  },

  computed: {
    localSearchQuery: {
      get() {
        return this.searchQuery;
      },
      set(value) {
        this.$emit("update:searchQuery", value);
      },
    },
  },
});
</script>

<style scoped>
/* All the styles from the original component remain the same */
</style>
