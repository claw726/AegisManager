<template>
  <div class="h-screen flex flex-col overflow-hidden bg-background">
    <NavBar class="flex-shrink-0" />
    <div class="flex flex-1 min-h-0">
      <!-- Sidebar -->
      <div class="w-80 border-r bg-[#f7f7f7] flex flex-col">
        <!-- Search bar -->
        <div class="p-3 border-b flex-shrink-0">
          <div class="relative">
            <input
              v-model="searchQuery"
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
              @click="retryLoading"
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
            <template v-for="org in organizationStructure" :key="`org-${org.orgID}`">
              <!-- Organization Header -->
              <div
                class="px-4 py-2 text-sm font-semibold text-gray-500 bg-gray-100 cursor-pointer hover:bg-gray-200 transition-colors flex items-center justify-between"
                @click="toggleCategory(`org-${org.orgID}`)"
              >
                <div class="flex items-center">
                  <i class="fas fa-building mr-2"></i>
                  {{ org.orgName }}
                </div>
                <i :class="[
                  'fas',
                  expandedCategories[`org-${org.orgID}`] ? 'fa-chevron-down' : 'fa-chevron-right',
                  'text-xs transition-transform duration-200'
                ]"></i>
              </div>

              <!-- Organization Content -->
              <div v-show="expandedCategories[`org-${org.orgID}`]" class="ml-4">
                <!-- Organization Chat -->
                <ChatListItem
                  v-if="org.chat"
                  :key="`orgchat-${org.orgID}`"
                  :chat="org.chat"
                  :active="activeChat?.id === org.chat.id"
                  :searchQuery="searchQuery"
                  @select="handleChatSelect"
                  class="border-l-2 border-gray-200"
                />

                <!-- Projects within Organization -->
                <template v-for="project in org.projects" :key="`project-${project.projectID}`">
                  <!-- Project Header -->
                  <div
                    class="px-4 py-2 text-sm font-semibold text-gray-500 bg-gray-50 cursor-pointer hover:bg-gray-100 transition-colors flex items-center justify-between border-l-2 border-gray-200"
                    @click="toggleCategory(`project-${project.projectID}`)"
                  >
                    <div class="flex items-center">
                      <i class="fas fa-project-diagram mr-2"></i>
                      {{ project.projectName }}
                    </div>
                    <i :class="[
                      'fas',
                      expandedCategories[`project-${project.projectID}`] ? 'fa-chevron-down' : 'fa-chevron-right',
                      'text-xs transition-transform duration-200'
                    ]"></i>
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
                      :searchQuery="searchQuery"
                      @select="handleChatSelect"
                    />

                    <!-- Template section for tasks -->
                    <template v-if="project.tasks && project.tasks.length > 0">
                      <div
                        class="px-4 py-2 text-sm font-semibold text-gray-500 bg-gray-50 cursor-pointer hover:bg-gray-100 transition-colors flex items-center justify-between"
                        @click="toggleCategory(`tasks-${project.projectID}`)"
                      >
                        <div class="flex items-center">
                          <i class="fas fa-tasks mr-2"></i>
                          Tasks
                        </div>
                        <i :class="[
                          'fas',
                          expandedCategories[`tasks-${project.projectID}`] ? 'fa-chevron-down' : 'fa-chevron-right',
                          'text-xs transition-transform duration-200'
                        ]"></i>
                      </div>

                      <div v-show="expandedCategories[`tasks-${project.projectID}`]">
                        <template v-for="taskItem in project.tasks" :key="`task-${taskItem.taskID}`">
                          <ChatListItem
                            v-if="taskItem && taskItem.chatID && findChat('task', taskItem.chatID)"
                            :chat="findChat('task', taskItem.chatID)"
                            :active="activeChat?.id === `task-${taskItem.chatID}`"
                            :searchQuery="searchQuery"
                            @select="handleChatSelect"
                            class="ml-4"
                          />
                        </template>
                      </div>
                    </template>
                    </div>
                </template>
              </div>
            </template>

            <!-- Direct Messages Section -->
            <template v-if="categorizedChats.direct.length">
              <!-- ... existing direct messages section ... -->
            </template>

            <!-- Groups Section -->
            <template v-if="categorizedChats.groups.length">
              <!-- ... existing groups section ... -->
            </template>
          </TransitionGroup>
        </div>

        <!-- New Chat Button -->
        <div class="p-3 border-t flex-shrink-0">
          <button
            class="w-full bg-blue-500 hover:bg-blue-600 text-white rounded-full px-4 py-2 flex items-center justify-center transition-colors"
            @click="showNewChatModal = true"
          >
            <i class="fas fa-pen mr-2"></i>
            New Message
          </button>
        </div>
      </div>

      <!-- Main Chat Area -->
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
    </div>
  </div>
</template>

<script>
import { mapState, mapActions } from "vuex";
import ChatListItem from "@/components/ChatListItem.vue";
import NavBar from "@/components/NavBar.vue";
import NewChatModal from "@/components/NewChatModal.vue";

export default {
  name: "ChatView",
  components: {
    ChatListItem,
    NavBar,
    NewChatModal,
  },
  data() {
    return {
      loading: true,
      error: null,
      showNewChatModal: false,
      searchQuery: "",
      expandedCategories: {},
    };
  },
  computed: {
    ...mapState({
      chats: (state) => state.chat.chats,
      currentUser: (state) => state.auth.currentUser,
      activeChat: (state) => state.chat.activeChat,
      messages: (state) => state.chat.messages,
      organizations: (state) => state.chat.organizations,
      projects: (state) => state.projects.projects,
      tasks: (state) => state.tasks.tasks,
    }),

    // Filter organizations where user is a member
    userOrganizations() {
      return this.organizations.filter(org =>
        org.users.some(user => user.userID === this.currentUser.userID)
      );
    },

    organizationStructure() {
      if (!this.userOrganizations || !Array.isArray(this.userOrganizations)) {
        return [];
      }

      return this.userOrganizations.map(org => ({
        ...org,
        chat: this.findChat('organization', org.chatID),
        projects: (this.projects || [])
          .filter(project => project && project.parentOrgID === org.orgID)
          .map(project => ({
            ...project,
            chat: this.findChat('project', project.chatID), // Use project.chatID
            tasks: (project.tasks || project.projectTasks || []).map(task => {
              // Make sure task exists and has an ID
              if (!task || !task.taskID) return null;

              // Find the chat for this task using task.chatID
              const taskChat = this.findChat('task', task.chatID);

              return {
                ...task,
                chat: taskChat
              };
            }).filter(task => task !== null) // Remove null tasks
          }))
      }));
    },

    categorizedChats() {
    const filtered = this.searchQuery.trim() 
      ? this.filteredChats 
      : this.chats;

    return {
      organizations: filtered.filter(chat => chat.type === 'organization'),
      projects: filtered.filter(chat => chat.type === 'project'),
      tasks: filtered.filter(chat => chat.type === 'task'),
      direct: filtered.filter(chat => chat.type === 'direct'),
      groups: filtered.filter(chat => chat.type === 'group')
    };
  },

    filteredChats() {
      if (!this.searchQuery.trim()) {
        return this.chats;
      }

      const query = this.searchQuery.toLowerCase().trim();

      return this.chats.filter((chat) => {
        // Search in chat title
        if (chat.title.toLowerCase().includes(query)) {
          return true;
        }

        // Search in chat messages
        const chatMessages = this.messages[chat.id] || [];
        return chatMessages.some(
          (message) =>
            message.content.toLowerCase().includes(query) ||
            message.senderName.toLowerCase().includes(query),
        );
      });
    },
  },

  watch: {
    $route() {
      this.searchQuery = "";
    },
    searchQuery(newValue) {
    if (newValue.trim()) {
      Object.keys(this.expandedCategories).forEach(category => {
        this.expandedCategories[category] = true;
      });
    }
  }
  },


  created() {
    // Initialize expanded states for organizations and projects
    this.organizationStructure.forEach(org => {
      this.expandedCategories[`org-${org.orgID}`] = true;
      org.projects.forEach(project => {
        this.expandedCategories[`project-${project.projectID}`] = true;
        this.expandedCategories[`tasks-${project.projectID}`] = true;
      });
    });
    this.restoreExpandedState();
  },


  async mounted() {
    await this.loadData();
    await this.fetchUserChats();
    await this.fetchAndStoreOrganizations();
  },

  methods: {
    ...mapActions("chat", [
      "selectChat",
      "createChat",
      "getChat",
      "fetchUserChats",
      "fetchAndStoreOrganizations",
    ]),
    toggleCategory(categoryId) {
    this.$set(
      this.expandedCategories,
      categoryId,
      !this.expandedCategories[categoryId]
    );
    localStorage.setItem(
      'chatExpandedCategories',
      JSON.stringify(this.expandedCategories)
    );
  },

  restoreExpandedState() {
    const savedState = localStorage.getItem('chatExpandedCategories');
    if (savedState) {
      const parsed = JSON.parse(savedState);
      Object.keys(parsed).forEach(key => {
        this.$set(this.expandedCategories, key, parsed[key]);
      });
    }
  },
    async createNewChat(chatData) {
      try {
        await this.$store.dispatch("chat/createNewChat", chatData);
        this.showNewChatModal = false;

        // Get the newly created chat (it will be the first one in the list)
        const newChat = this.$store.state.chat.chats[0];

        // Navigate to the appropriate route based on chat type
        let route;
        if (chatData.type === "direct") {
          route = {
            name: "DirectChat",
            params: { userID: chatData.participants[0] },
          };
        } else {
          route = {
            name: "GroupChat",
            params: { groupID: newChat.id },
          };
        }

        await this.$router.push(route);
      } catch (error) {
        console.error("Error creating new chat:", error);
        // You might want to add error handling UI here
      }
    },
    async handleChatSelect(chat) {
      if (!chat || !chat.id) return;
      console.log("handleChatSelect called with:", chat);

      try {
        await this.selectChat(chat.id);
        console.log("Chat selected:", chat.id);

        let route;
        switch (chat.type) {
          case "direct":
            route = {
              name: "DirectChat",
              params: {
                userID: chat.participants.find(
                  (id) => id !== this.currentUser.userID,
                ),
              },
            };
            break;
          case "group":
            route = {
              name: "GroupChat",
              params: { groupID: chat.id },
            };
            break;
          case "organization":
            route = {
              name: "OrgChat",
              params: { orgIndex: chat.id },
            };
            break;
          case "project":
            route = {
              name: "ProjectChat",
              params: {
                projectIndex: chat.id,
              },
            };
            break;
          case "task":
            route = {
              name: "TaskChat",
              params: {
                taskIndex: chat.id,
              },
            };
            break;
        }

        if (route) {
          console.log("Navigating to route:", route);
          await this.$router.push(route);
        }
      } catch (error) {
        console.error("Error in handleChatSelect:", error);
      }
    },
    getChatIdFromRoute() {
      const { name, params } = this.$route;
      switch (name) {
        case "DirectChat":
          return `direct-${params.userID}`;
        case "GroupChat":
          return params.groupID;
        case "OrgChat":
          return `org-${params.orgIndex}`;
        case "ProjectChat":
          return `project-${params.projectIndex}`;
        case "TaskChat":
          return `task-${params.taskIndex}`;
        default:
          return null;
      }
    },
    toggleCategory(category) {
      this.expandedCategories[category] = !this.expandedCategories[category];
      // Save state to localStorage
      localStorage.setItem('chatExpandedCategories', JSON.stringify(this.expandedCategories));
    },

    // Add this method to restore the expanded state
    restoreExpandedState() {
      const savedState = localStorage.getItem('chatExpandedCategories');
      if (savedState) {
        this.expandedCategories = JSON.parse(savedState);
      }
    },
    findChat(type, id) {
      if (!type || !id || !this.chats || !Array.isArray(this.chats)) {
        console.log(`Invalid parameters for findChat - type: ${type}, id: ${id}`);
        return null;
      }

      const formattedId = `${type}-${id}`;
      const chat = this.chats.find(chat =>
        chat &&
        chat.id === formattedId &&
        chat.type === type
      );

      if (!chat) {
        console.log(`No chat found for ${formattedId}`);
      }

      return chat;
    },
    // Helper method to check if user is member of an organization
    isUserMemberOfOrg(org) {
      return org &&
             org.users &&
             Array.isArray(org.users) &&
             org.users.some(user => user && user.userID === this.currentUser.userID);
    },
    // Update the projects fetch method to only fetch for member organizations
    async fetchUserProjectsAndTasks() {
      const memberOrgIds = this.userOrganizations.map(org => org.orgID);
      return await this.$store.dispatch('projects/fetchProjectsWithTasks', memberOrgIds);
    },
    async loadData() {
      this.loading = true;
      this.error = null;

      try {
        // Get organizations first
        const orgs = await this.$store.dispatch('organizations/fetchOrganizations');

        console.log('Organizations:', orgs);

        // Check if we have organizations and filter for user membership
        if (!orgs || !Array.isArray(orgs)) {
          console.log('No organizations found');
          this.loading = false;
          return;
        }

        // Filter organizations where user is a member
        const userOrgs = orgs.filter(org =>
          org.users && Array.isArray(org.users) &&
          org.users.some(user => user.userID === this.currentUser.userID)
        );

        console.log('User organizations:', userOrgs);

        if (userOrgs.length > 0) {
          // Get organization IDs
          const orgIDs = userOrgs.map(org => org.orgID).filter(id => id != null);

          console.log('User organization IDs:', orgIDs);

          if (orgIDs.length > 0) {
            // Fetch projects only for organizations where user is a member
            const projectsWithTasks = await this.$store.dispatch(
              'projects/fetchProjectsWithTasks',
              orgIDs
            );

            console.log('Projects with tasks:', projectsWithTasks);

            if (projectsWithTasks && Array.isArray(projectsWithTasks)) {
              await this.$store.commit('projects/SET_PROJECTS', projectsWithTasks);
            }
          }
        }

        // Fetch all chats after we have the organization structure
        await this.$store.dispatch('chat/fetchUserChats');

        console.log('Chats:', this.chats);

        // Restore expanded state from localStorage
        this.restoreExpandedState();

        // Select initial chat if specified in route
        const chatId = this.getChatIdFromRoute();
        if (chatId) {
          await this.selectChat(chatId);
        }
      } catch (error) {
        console.error('Error loading chat data:', error);
        this.error = 'Failed to load chat data. Please try again.';
      } finally {
        this.loading = false;
      }
    },
    async retryLoading() {
      await this.loadData();
    }
  },
};
</script>

<style scoped>
.chat-list-enter-active,
.chat-list-leave-active {
  transition: all 0.5s ease;
}

.chat-list-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.chat-list-leave-to {
  opacity: 0;
  transform: translateX(30px);
}

.chat-list-move {
  transition: transform 0.5s ease;
}

.highlight {
  background-color: yellow;
  padding: 0 2px;
  border-radius: 2px;
}

.category-header {
  position: sticky;
  top: 0;
  z-index: 10;
  backdrop-filter: blur(8px);
  border-bottom: 1px solid theme('colors.gray.200');
}

/* Smooth transitions for category sections */
.chat-list-enter-active .category-header,
.chat-list-leave-active .category-header {
  transition: all 0.5s ease;
}

.chat-list-enter-from .category-header {
  opacity: 0;
  transform: translateY(-20px);
}

.chat-list-leave-to .category-header {
  opacity: 0;
  transform: translateY(20px);
}
.list-enter-active,
.list-leave-active {
  transition: all 0.3s ease;
  overflow: hidden;
}

.list-enter-from,
.list-leave-to {
  opacity: 0;
  transform: translateY(-20px);
  max-height: 0;
}

.list-enter-to,
.list-leave-from {
  opacity: 1;
  transform: translateY(0);
  max-height: 1000px;
}

/* Chevron rotation animation */
.fa-chevron-down,
.fa-chevron-right {
  transition: transform 0.2s ease;
}

.fa-chevron-down {
  transform: rotate(0deg);
}

.fa-chevron-right {
  transform: rotate(-90deg);
}

/* Category header hover effect */
.category-header {
  transition: background-color 0.2s ease;
}

.category-header:hover {
  background-color: theme("colors.gray.100");
}
.chat-list-item {
  @apply transition-all duration-200;
}

.chat-list-item:hover {
  @apply bg-gray-100;
}

/* Indentation and hierarchy lines */
.ml-4 {
  @apply border-l border-gray-200;
}

/* Transition animations for expanding/collapsing */
.expand-enter-active,
.expand-leave-active {
  transition: all 0.3s ease-in-out;
  overflow: hidden;
}

.expand-enter-from,
.expand-leave-to {
  opacity: 0;
  max-height: 0;
}

.expand-enter-to,
.expand-leave-from {
  opacity: 1;
  max-height: 1000px;
}
</style>