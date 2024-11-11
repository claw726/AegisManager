import WebSocketService from "@/services/websocket.js";
import axios from "axios";

const mockUsers = [
  {
    id: 1,
    name: "Dr. Gregory House",
    email: "house@ppth.edu",
    orgId: "ppth",
    title: "Head of Diagnostic Medicine",
  },
  {
    id: 2,
    name: "Dr. Lisa Cuddy",
    email: "cuddy@ppth.edu",
    orgId: "ppth",
    title: "Dean of Medicine",
  },
  {
    id: 3,
    name: "Dr. James Wilson",
    email: "wilson@ppth.edu",
    orgId: "ppth",
    title: "Head of Oncology",
  },
  {
    id: 4,
    name: "Dr. Eric Foreman",
    email: "foreman@ppth.edu",
    orgId: "ppth",
    title: "Neurologist",
  },
  {
    id: 5,
    name: "Dr. Robert Chase",
    email: "chase@ppth.edu",
    orgId: "ppth",
    title: "Surgeon/Intensivist",
  },
  {
    id: 6,
    name: "Dr. Allison Cameron",
    email: "cameron@ppth.edu",
    orgId: "ppth",
    title: "Immunologist",
  },
  {
    id: 7,
    name: "Dr. Chris Taub",
    email: "taub@ppth.edu",
    orgId: "ppth",
    title: "Plastic Surgeon",
  },
  {
    id: 8,
    name: "Dr. Remy Hadley",
    email: "thirteen@ppth.edu",
    orgId: "ppth",
    title: "Internist",
  },
  {
    id: 9,
    name: "Dr. Lawrence Kutner",
    email: "kutner@ppth.edu",
    orgId: "ppth",
    title: "Sports Medicine",
  },
];

const mockOrganizations = [
  {
    id: "ppth",
    name: "Princeton-Plainsboro Teaching Hospital",
    department: "Diagnostic Medicine",
  },
];

// Let's also update some mock chats to reflect these characters
const mockChats = [
  {
    id: "direct-2",
    type: "direct",
    participants: [1, 2], // House and Cuddy
    title: "Dr. Lisa Cuddy",
    lastMessage: "Your clinic hours are still mandatory, House.",
    unreadCount: 2,
  },
  {
    id: "direct-3",
    type: "direct",
    participants: [1, 3], // House and Wilson
    title: "Dr. James Wilson",
    lastMessage: "Want to get lunch?",
    unreadCount: 0,
  },
  {
    id: "group-1",
    type: "group",
    participants: [1, 4, 5, 6], // House and original team
    title: "Original Diagnostic Team",
    lastMessage: "Differential diagnosis, people!",
    unreadCount: 1,
  },
  {
    id: "group-2",
    type: "group",
    participants: [1, 7, 8, 9], // House and new team
    title: "New Diagnostic Team",
    lastMessage: "The patient is getting worse.",
    unreadCount: 3,
  },
  {
    id: "org-ppth",
    type: "organization",
    orgId: "ppth",
    participants: [1, 2, 3, 4, 5, 6, 7, 8, 9],
    title: "PPTH General",
    lastMessage: "Remember the department meeting tomorrow.",
    unreadCount: 0,
  },
];

// You might want to update the mock messages as well with some House-style dialogue
const mockMessages = {
  "direct-2": [
    // House and Cuddy
    {
      id: 1,
      content: "House, where are you? You have clinic duty.",
      senderId: 2,
      senderName: "Dr. Lisa Cuddy",
      timestamp: new Date("2024-01-20T09:00:00").toISOString(),
    },
    {
      id: 2,
      content: "Coma guy needs me. Very critical. He might wake up any second.",
      senderId: 1,
      senderName: "Dr. Gregory House",
      timestamp: new Date("2024-01-20T09:01:00").toISOString(),
    },
    {
      id: 3,
      content:
        "You're watching General Hospital in his room again, aren't you?",
      senderId: 2,
      senderName: "Dr. Lisa Cuddy",
      timestamp: new Date("2024-01-20T09:01:30").toISOString(),
    },
    {
      id: 4,
      content: "Your clinic hours are still mandatory, House.",
      senderId: 2,
      senderName: "Dr. Lisa Cuddy",
      timestamp: new Date("2024-01-20T09:02:00").toISOString(),
    },
  ],

  "direct-3": [
    // House and Wilson
    {
      id: 5,
      content: "Want to get lunch?",
      senderId: 3,
      senderName: "Dr. James Wilson",
      timestamp: new Date("2024-01-20T11:30:00").toISOString(),
    },
    {
      id: 6,
      content: "Only if you're buying. And I'm taking half your sandwich.",
      senderId: 1,
      senderName: "Dr. Gregory House",
      timestamp: new Date("2024-01-20T11:31:00").toISOString(),
    },
    {
      id: 7,
      content: "So... the usual then?",
      senderId: 3,
      senderName: "Dr. James Wilson",
      timestamp: new Date("2024-01-20T11:31:30").toISOString(),
    },
    {
      id: 8,
      content:
        "Cuddy's looking for you, by the way. Something about clinic hours.",
      senderId: 3,
      senderName: "Dr. James Wilson",
      timestamp: new Date("2024-01-20T11:32:00").toISOString(),
    },
    {
      id: 9,
      content: "Lunch first. Hide from Cuddy later.",
      senderId: 1,
      senderName: "Dr. Gregory House",
      timestamp: new Date("2024-01-20T11:32:30").toISOString(),
    },
  ],

  "group-1": [
    // Original team
    {
      id: 10,
      content:
        "Differential diagnosis, people! 16-year-old male, seizures, rash, and now kidney failure.",
      senderId: 1,
      senderName: "Dr. Gregory House",
      timestamp: new Date("2024-01-20T14:00:00").toISOString(),
    },
    {
      id: 11,
      content: "Could be lupus.",
      senderId: 6,
      senderName: "Dr. Allison Cameron",
      timestamp: new Date("2024-01-20T14:01:00").toISOString(),
    },
    {
      id: 12,
      content: "It's never lupus.",
      senderId: 1,
      senderName: "Dr. Gregory House",
      timestamp: new Date("2024-01-20T14:01:30").toISOString(),
    },
    {
      id: 13,
      content: "Autoimmune response to a viral infection?",
      senderId: 4,
      senderName: "Dr. Eric Foreman",
      timestamp: new Date("2024-01-20T14:02:00").toISOString(),
    },
    {
      id: 14,
      content: "Drug use could explain all the symptoms.",
      senderId: 5,
      senderName: "Dr. Robert Chase",
      timestamp: new Date("2024-01-20T14:02:30").toISOString(),
    },
    {
      id: 15,
      content:
        "Chase, go break into the patient's house. Foreman, run the blood cultures. Cameron, get a detailed family history.",
      senderId: 1,
      senderName: "Dr. Gregory House",
      timestamp: new Date("2024-01-20T14:03:00").toISOString(),
    },
  ],

  "group-2": [
    // New team
    {
      id: 16,
      content: "Patient started coughing up blood during the MRI.",
      senderId: 8,
      senderName: "Dr. Remy Hadley",
      timestamp: new Date("2024-01-20T15:00:00").toISOString(),
    },
    {
      id: 17,
      content: "Interesting. Now we know it's not boring.",
      senderId: 1,
      senderName: "Dr. Gregory House",
      timestamp: new Date("2024-01-20T15:01:00").toISOString(),
    },
    {
      id: 18,
      content: "Could be Wegener's granulomatosis.",
      senderId: 7,
      senderName: "Dr. Chris Taub",
      timestamp: new Date("2024-01-20T15:01:30").toISOString(),
    },
    {
      id: 19,
      content: "Or pulmonary embolism with paraneoplastic syndrome.",
      senderId: 9,
      senderName: "Dr. Lawrence Kutner",
      timestamp: new Date("2024-01-20T15:02:00").toISOString(),
    },
    {
      id: 20,
      content:
        "Run the tests for both. And someone figure out why this patient is lying about their medical history.",
      senderId: 1,
      senderName: "Dr. Gregory House",
      timestamp: new Date("2024-01-20T15:02:30").toISOString(),
    },
    {
      id: 21,
      content: "The patient is getting worse.",
      senderId: 8,
      senderName: "Dr. Remy Hadley",
      timestamp: new Date("2024-01-20T15:45:00").toISOString(),
    },
  ],

  "org-ppth": [
    // Hospital-wide chat
    {
      id: 22,
      content: "Reminder: Department heads meeting tomorrow at 9 AM.",
      senderId: 2,
      senderName: "Dr. Lisa Cuddy",
      timestamp: new Date("2024-01-20T16:00:00").toISOString(),
    },
    {
      id: 23,
      content: "Will there be coffee?",
      senderId: 1,
      senderName: "Dr. Gregory House",
      timestamp: new Date("2024-01-20T16:01:00").toISOString(),
    },
    {
      id: 24,
      content: "Yes, House, there will be coffee.",
      senderId: 2,
      senderName: "Dr. Lisa Cuddy",
      timestamp: new Date("2024-01-20T16:02:00").toISOString(),
    },
    {
      id: 25,
      content: "Great, then I'll make sure to miss it.",
      senderId: 1,
      senderName: "Dr. Gregory House",
      timestamp: new Date("2024-01-20T16:03:00").toISOString(),
    },
    {
      id: 26,
      content:
        "Don't forget to submit your department budget reports by Friday.",
      senderId: 2,
      senderName: "Dr. Lisa Cuddy",
      timestamp: new Date("2024-01-20T16:04:00").toISOString(),
    },
    {
      id: 27,
      content: "Remember the department meeting tomorrow.",
      senderId: 2,
      senderName: "Dr. Lisa Cuddy",
      timestamp: new Date("2024-01-20T16:05:00").toISOString(),
    },
  ],
};

const state = {
  currentUser: null,
  activeChat: null,
  chats: [],
  messages: {},
  wsConnected: false,
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
    // In Vue 3, you can directly assign to reactive objects
    state.messages[chatId] = messages;
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
  SET_WS_CONNECTED_STATUS(state, status) {
    state.wsConnected = status;
  },
  SET_USERS(state, users) {
    state.users = users;
  },
  SET_LOADING(state, loading) {
    state.loading = loading;
  },
  SET_ERROR(state, error) {
    state.error = error;
  },
};

const actions = {
  async initializeWebSocket({ commit, state }) {
    WebSocketService.connect();

    // Subscribe to personal messages
    WebSocketService.subscribe(
      `/user/${state.currentUser.id}/chats`,
      (message) => {
        commit("ADD_CHAT", JSON.parse(message.body));
      },
    );

    // Subscribe to global messages
    WebSocketService.subscribe("/topic/messages", (message) => {
      const messageData = JSON.parse(message.body);
      commit("ADD_MESSAGE", {
        chatId: messageData.chatId,
        message: messageData,
      });
    });
  },

  async fetchUsers({ rootState }, { commit }) {
    try {
      commit("SET_LOADING", true);
      const response = await axios.get("/api/users/getAllUsers", {
        headers: {
          Authorization: `Bearer ${rootState.auth.authToken}`,
          "Content-Type": "application/json",
        },
      });
      console.log("Fetched users:", response.data);
      commit("SET_USERS", response.data);
    } catch (error) {
      console.error("Error fetching users:", error);
      commit("SET_ERROR", "Failed to fetch users");
      throw error;
    } finally {
      commit("SET_LOADING", false);
    }
  },

  async selectChat({ commit }, chatId) {
    if (!chatId) return;

    try {
      const [chatResponse, messagesResponse] = await Promise.all([
        axios.get(`/api/chats/${chatId}/get`),
        axios.get(`/api/messages/${chatId}/getMessages`),
      ]);

      commit("SET_ACTIVE_CHAT", chatResponse.data);
      commit("SET_MESSAGES", {
        chatId,
        messages: messagesResponse.data,
      });

      WebSocketService.subscribeToChatRoom(chatId);
    } catch (error) {
      console.error("Error selecting chat:", error);
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

    WebSocketService.sendMessage("/app/chat.message", message);
  },

  async createNewChat({ state }, { type, participants, title }) {
    const newChat = {
      type,
      participants: [...participants, state.currentUser.id],
      title,
      timestamp: new Date().toISOString(),
    };

    WebSocketService.sendMessage("/app/chat.create", newChat);
  },
};

const getters = {
  getChatMessages: (state) => (chatId) => {
    return state.messages[chatId] || [];
  },
  getCurrentUser: (rootState) => rootState.auth.currentUser,

  getActiveChat: (state) => state.activeChat,
  getFilteredUsers:
    (state) =>
    (searchQuery = "", selectedOrg = "") => {
      if (!Array.isArray(state.users)) return [];

      let filtered = [...state.users];

      if (selectedOrg) {
        filtered = filtered.filter((user) => user.orgId === selectedOrg);
      }

      if (searchQuery.trim()) {
        const query = searchQuery.toLowerCase();
        filtered = filtered.filter(
          (user) =>
            user.userName?.toLowerCase().includes(query) ||
            user.email?.toLowerCase().includes(query),
        );
      }

      return filtered;
    },
};

export default {
  state,
  namespaced: true,
  actions,
  getters,
  mutations,
};
