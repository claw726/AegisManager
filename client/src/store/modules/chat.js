import WebSocketService from "@/services/websocket.js";
import axios from "axios";

const state = {
  currentUser: null,
  activeChat: null,
  chats: [],
  messages: {},
  users: [],
  organizations: [],
  loading: false,
  error: null,
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
  ADD_MESSAGE(state, { chatId, message }) {
    if (!state.messages[chatId]) {
      state.messages[chatId] = [];
    }
    state.messages[chatId].push(message);
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
};

const actions = {
  async fetchUsers({ rootState, commit }) {
    try {
      commit("SET_LOADING", true);
      console.log("Fetching users...");
      const response = await axios.get("/api/users/getAllUsers", {
        headers: {
          Authorization: `Bearer ${rootState.auth.authToken}`,
          "Content-Type": "application/json",
        },
      });
      const users = Array.isArray(response.data)
        ? response.data
        : [response.data];
      console.log("Fetched users:", users);
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
      console.log(`Fetching messages for chat ${chatId}`);

      const response = await axios.get(`/api/messages/${numericChatId}/getMessages`, {
        headers: {
          Authorization: `Bearer ${rootState.auth.authToken}`,
        },
      });

      if (!state.messages[chatId]) {
        commit("SET_MESSAGES", { chatId, messages: [] });
      }
      const messages = response.data || [];
      console.log(`Retrieved ${messages.length} messages for chat ${chatId}`);
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
      console.log(`Selecting chat ${chatId}`);

      const numericChatId = parseInt(chatId.match(/\d+/)[0]);

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

  sendMessage({ state }, { chatId, content }) {
    if (!chatId || !content) return;

    const message = {
      chatId,
      content,
      senderId: state.currentUser.id,
      senderName: state.currentUser.name,
      timestamp: new Date().toISOString(),
    };

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

      console.log("Creating chat with params:", {
        type,
        participants: Array.from(participants),
        title,
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

  handleNewMessage({ commit }, messageData) {
    commit("ADD_MESSAGE", {
      chatId: messageData.chatId,
      message: messageData,
    });
    commit("UPDATE_CHAT_LAST_MESSAGE", {
      chatId: messageData.chatId,
      lastMessaeg: messageData.content,
    });
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
};

export default {
  state,
  namespaced: true,
  actions,
  getters,
  mutations,
};
