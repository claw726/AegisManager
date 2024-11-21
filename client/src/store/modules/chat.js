import axios from "axios";
import { sendMessage } from "../../utils/websocket";

const state = {
  currentUser: null,
  activeChat: null,
  chats: [],
  messages: {},
  users: [],
  organizations: [],
  loading: false,
  error: null,
  wsConnected: false,
  chatInfoCache: {},
};

const mutations = {
  SET_CHATS(state, chats) {
    state.chats = chats;
  },
  ADD_CHAT(state, chat) {
    state.chats.unshift(chat);
    // In Vue 3, you can directly assign to reactive objects
    state.messages[chat.id] = [];
  },
  SET_MESSAGES(state, { chatId, messages }) {
    const messageArray = Array.isArray(messages) ? messages : [];
    state.messages = {
      ...state.messages,
      [chatId]: messageArray,
    };
  },
  CLEAR_MESSAGES(state, chatId) {
    if (chatId) {
      state.messages = {
        ...state.messages,
        [chatId]: [],
      };
    } else {
      state.messages = {};
    }
  },
  CLEAR_UNREAD_COUNT(state, chatId) {
    const chat = state.chats.find((chat) => chat.id === chatId);
    if (chat) {
      chat.unreadCount = 0;
    }
  },
  ADD_MESSAGE(state, { chatId, message }) {
    if (!state.messages[chatId]) {
      state.messages[chatId] = [];
    }
    // Check for duplicate messages
    if (!state.messages[chatId].find((msg) => msg.id === message.id)) {
      state.messages[chatId].push(message);
    }
  },
  SET_ACTIVE_CHAT(state, chat) {
    state.activeChat = chat;
  },
  SET_USERS(state, users) {
    state.users = Array.isArray(users) ? [...users] : [users];
  },
  SET_LOADING(state, loading) {
    state.loading = loading;
  },
  SET_ERROR(state, error) {
    state.error = error;
  },
  SET_CHAT_INFO(state, { chatId, chatInfo }) {
    state.chatInfoCache[chatId] = chatInfo;
  },
  UPDATE_CHAT_LAST_MESSAGE(state, { chatId, lastMessage }) {
    const chat = state.chats.find((chat) => chat.id === chatId);
    if (chat) {
      chat.lastMessage = lastMessage;
    }
  },
  UPDATE_CHAT(state, updatedChat) {
    const index = state.chats.findIndex((chat) => chat.id === updatedChat.id);
    if (index !== -1) {
      state.chats.splice(index, 1, updatedChat);
    }
  },
  SET_WS_CONNECTED(state, connected) {
    state.wsConnected = connected;
  },
  DELETE_MESSAGE(state, { chatId, messageId, deleted }) {
    if (state.messages[chatId]) {
      const messageIndex = state.messages[chatId].findIndex(
        (msg) => msg.id === messageId,
      );
      if (messageIndex !== -1) {
        // Create a new message object with the deleted flag
        const updatedMessage = {
          ...state.messages[chatId][messageIndex],
          deleted: deleted,
        };

        // create a new array with the updated message
        const updatedMessages = [...state.messages[chatId]];
        updatedMessages[messageIndex] = updatedMessage;

        // Update the messages in the state
        state.messages = {
          ...state.messages,
          [chatId]: updatedMessages,
        };
      }
    }
  },
  REFRESH_CHAT_MESSAGES(state, chatId) {
    const messages = state.messages[chatId] || [];
    state.messages = {
      ...state.messages,
      [chatId]: [...messages],
    };
  },
  SET_ORGANIZATIONS(state, organizations) {
    state.organizations = organizations;
  },
};

const actions = {
  async refreshChatMessages({ commit, dispatch }, chatId) {
    await dispatch("getMessages", chatId);
    commit("REFRESH_CHAT_MESSAGES", chatId);
  },

  async fetchUsers({ rootState, commit }) {
    try {
      commit("SET_LOADING", true);
      const response = await axios.get("/api/users/getAllUsers", {
        headers: {
          Authorization: `Bearer ${rootState.auth.authToken}`,
          "Content-Type": "application/json",
        },
      });
      const users = Array.isArray(response.data)
        ? response.data
        : [response.data];
      commit("SET_USERS", users);
    } catch (error) {
      console.error("Error fetching users:", error);
      commit("SET_ERROR", "Failed to fetch users");
      throw error;
    } finally {
      commit("SET_LOADING", false);
    }
  },

  async getMessages({ rootState, commit }, chatId) {
    if (!chatId) {
      console.warn("Chat ID is required to select a chat");
      return;
    }
    const numericChatId = parseInt(chatId.match(/\d+/)[0]);
    commit("SET_LOADING", true);
    try {
      const response = await axios.get(
        `/api/messages/${numericChatId}/getMessages`,
        {
          headers: {
            Authorization: `Bearer ${rootState.auth.authToken}`,
          },
        },
      );

      if (!state.messages[chatId]) {
        commit("SET_MESSAGES", { chatId, messages: [] });
      }
      const messages = response.data || [];
      commit("SET_MESSAGES", { chatId, messages: messages });
      return messages;
    } catch (error) {
      console.error("Error fetching messages:", error);
      commit("SET_ERROR", "Failed to fetch messages");
      throw error;
    } finally {
      commit("SET_LOADING", false);
    }
  },

  async selectChat({ dispatch, commit }, chatId) {
    if (!chatId) {
      console.warn("No chatId provided to selectChat");
      return;
    }

    try {
      // Get chat and messages in parallel
      const [chat, messages] = await Promise.all([
        dispatch("getChat", chatId),
        dispatch("getMessages", chatId),
      ]);

      commit("SET_ACTIVE_CHAT", chat);

      return { chat, messages };
    } catch (error) {
      console.error("Error selecting chat:", error);
      commit(
        "SET_ERROR",
        error.response?.data?.message || "Failed to select chat",
      );
      throw error;
    }
  },

  async sendMessage({ rootState, commit }, { chatId, content }) {
    if (!chatId || !content) return;

    const numericChatId = parseInt(chatId.match(/\d+/)[0]);
    // Send message to server
    try {
      await axios.post(
        "/api/messages/add",
        {
          chatId: numericChatId,
          content: content,
        },
        {
          headers: {
            Authorization: `Bearer ${rootState.auth.authToken}`,
          },
        },
      );

      // emit through socket
      await sendMessage({
        chatId: chatId,
        content: content,
        senderID: rootState.auth.currentUser.userID,
        senderEmail: rootState.auth.currentUser.email,
        targetEmail: state.activeChat.participants.find(
          (p) => p !== rootState.auth.currentUser.userID,
        ),
        date: new Date().toISOString(),
      });
      return;
    } catch (error) {
      console.error("Error sending message:", error);
      commit("SET_ERROR", "Failed to send message");
      throw error;
    }
  },

  async getChat({ rootState, commit }, chatId) {
    commit("SET_LOADING", true);
    const numericChatId = parseInt(chatId.match(/\d+/)[0]);
    try {
      const response = await axios.get(`/api/chats/${numericChatId}/get`, {
        headers: {
          Authorization: `Bearer ${rootState.auth.authToken}`,
        },
      });
      commit("SET_ACTIVE_CHAT", response.data);
      return response.data;
    } catch (error) {
      commit("SET_ERROR", error.response?.data || "Failed to get chat");
      throw error;
    } finally {
      commit("SET_LOADING", false);
    }
  },

  async fetchOrgMessages({ rootState, commit }, orgId) {
    commit("SET_LOADING", true);
    try {
      const response = await axios.get(`/api/messages/history/${orgId}`, {
        headers: {
          Authorization: `Bearer ${rootState.auth.authToken}`,
        },
      });

      return response.data;
    } catch (error) {
      console.error("Error fetching org messages:", error);
      commit("SET_ERROR", "Failed to fetch organization messages");
      throw error;
    } finally {
      commit("SET_LOADING", false);
    }
  },

  async deleteMessage({ rootState, commit }, { chatId, messageId }) {
    try {
      await axios.delete(`/api/messages/${messageId}/delete`, {
        headers: {
          Authorization: `Bearer ${rootState.auth.authToken}`,
        },
      });
      commit("DELETE_MESSAGE", { chatId, messageId });
      return true;
    } catch (error) {
      console.error("Error deleting message:", error);
      commit("SET_ERROR", "Failed to delete message");
      throw error;
    }
  },

  async fetchUserChats({ rootState, commit }) {
    commit("SET_LOADING", true);
    try {
      const response = await axios.get(
        `/api/chats/${rootState.auth.currentUser.userID}/chats`,
        {
          headers: {
            Authorization: `Bearer ${rootState.auth.authToken}`,
          },
        },
      );
      commit("SET_CHATS", response.data);
      return response.data;
    } catch (error) {
      commit("SET_ERROR", error.response?.data || "Failed to fetch user chats");
      throw error;
    } finally {
      commit("SET_LOADING", false);
    }
  },

  async createNewChat(
    { rootState, commit, dispatch },
    { type, participants, title },
  ) {
    commit("SET_LOADING", true);
    try {
      const formData = new FormData();
      formData.append("type", type);
      formData.append("title", title);
      participants.forEach((participantId) => {
        formData.append("participants", participantId);
      });

      const response = await axios.post("/api/chats/create", formData, {
        headers: {
          Authorization: `Bearer ${rootState.auth.authToken}`,
          "Content-Type": "application/x-www-form-urlencoded",
        },
      });
      if (response.data) {
        await dispatch("fetchUserChats", rootState.auth.currentUser.userID);
      }
      return response.data;
    } catch (error) {
      commit("SET_ERROR", error.response?.data || "Failed to create chat");
      throw error;
    } finally {
      commit("SET_LOADING", false);
    }
  },

  async handleNewMessage({ commit, state, rootState, dispatch }, messageData) {
    try {
      // Validate message
      if (!messageData || !messageData.chatId) {
        console.warn("Invalid message data:", messageData);
        return;
      }

      var chatInfo = state.chats.find((chat) => chat.id === messageData.chatId);
      if (!chatInfo) {
        try {
          chatInfo = await dispatch("getChat", messageData.chatId);
          if (chatInfo) {
            commit("ADD_CHAT", {
              id: messageData.chatId,
              title: chatInfo.title,
              lastMessage: messageData.content,
              participants: chatInfo.participants || [],
              type: chatInfo.type || "direct",
              unreadCount: 0,
            });
          }
        } catch (error) {
          console.error("Error fetching chat info:", error);
          // create a temporary chat object
          chatInfo = {
            id: messageData.chatId,
            title: "Loading...",
            participants: [],
            type: "direct",
            unreadCount: 0,
          };
          commit("ADD_CHAT", chatInfo);
        }
      }

      // Create a formatted message object
      const formattedMessage = {
        id: messageData.id || `temp-${Date.now()}`,
        chatId: messageData.chatId,
        content: messageData.content,
        deleted: messageData.deleted || false,
        senderID: messageData.senderID,
        senderEmail: messageData.senderEmail,
        timestamp: messageData.timestamp || new Date().toISOString(),
        status: messageData.status || "delivered",
        type: messageData.type || "text",
      };

      // Add message to chat messages
      commit("ADD_MESSAGE", {
        chatId: messageData.chatId,
        message: formattedMessage,
      });

      // set the delete flag if the deleted flag is true
      if (messageData.deleted) {
        commit("DELETE_MESSAGE", {
          chatId: messageData.chatId,
          messageId: messageData.id,
          deleted: true,
        });
      }

      // Update chat last message
      commit("UPDATE_CHAT_LAST_MESSAGE", {
        chatId: messageData.chatId,
        lastMessage: formattedMessage,
      });

      // Update the unread count
      if (messageData.senderID !== rootState.auth.currentUser.userID) {
        const chat = state.chats.find((chat) => chat.id === messageData.chatId);
        if (chat) {
          chat.unreadCount = chat.unreadCount ? chat.unreadCount + 1 : 1;
        }
      }
    } catch (error) {
      console.error("Error handling new message:", error);
      commit("SET_ERROR", "Failed to handle new message");
    }
  },

  handleGlobalMessage({ commit }, data) {
    switch (data.type) {
      case "NEW_CHAT":
        commit("ADD_CHAT", data.chat);
        break;
      case "CHAT_UPDATE":
        commit("UPDATE_CHAT", data.chat);
        break;
    }
  },
  async fetchAndStoreOrganizations({ dispatch, commit }) {
    try {
      const organizations = await dispatch(
        "organizations/fetchOrganizations",
        null,
        { root: true },
      );
      commit("SET_ORGANIZATIONS", organizations);
    } catch (error) {
      console.error("Error fetching organizations: ", error);
      commit("SET_ERROR", "Failed to fetch organizations");
    }
  },
};

const getters = {
  getChatMessages: (state) => (chatId) => {
    return state.messages[chatId] || [];
  },
  getMessageById: (state) => (chatId, messageId) => {
    const chatMessages = state.messages[chatId] || [];
    return chatMessages.find((message) => message.id === messageId);
  },
  getLastMessage: (state) => (chatId) => {
    const chatMessages = state.messages[chatId] || [];
    return chatMessages[chatMessages.length - 1];
  },
  getCurrentUser: (rootState) => rootState.auth.currentUser,

  getActiveChat: (state) => state.activeChat,
  getAllUsers: (state) => state.users || [],
  getUserById: (state) => (userId) => {
    return state.users.find((user) => user.userID === userId);
  },
};

export default {
  state,
  namespaced: true,
  actions,
  getters,
  mutations,
};
