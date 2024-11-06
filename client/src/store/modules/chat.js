// import axios from "@/utils/axios.js";
// import { Buffer } from "buffer";

const mockUsers = [
  { id: 1, name: "John Doe", avatar: null },
  { id: 2, name: "Jane Smith", avatar: null },
  { id: 3, name: "Bob Johnson", avatar: null },
];

// Don't forget to update the mockChats array to include the new chat references:
const mockChats = [
  {
    id: "direct-2",
    type: "direct",
    participants: [1, 2],
    title: "Jane Smith",
    lastMessage: "Should we schedule a team meeting to discuss implementation?",
    unreadCount: 2,
  },
  {
    id: "group-1",
    type: "group",
    participants: [1, 2, 3],
    title: "Project Team",
    lastMessage: "Don't forget to bring your project updates!",
    unreadCount: 0,
  },
  {
    id: "org-1",
    type: "organization",
    orgId: "1",
    participants: [1, 2, 3],
    title: "Organization Chat",
    lastMessage: "Perfect, thanks!",
    unreadCount: 1,
  },
  {
    id: "project-1",
    type: "project",
    orgId: "1",
    participants: [1, 2, 3],
    title: "Website Redesign",
    lastMessage: "Looking forward to seeing them!",
    unreadCount: 0,
  },
  {
    id: "task-1",
    type: "task",
    orgId: "1",
    projectId: "project-1",
    participants: [1, 2],
    title: "Design Homepage",
    lastMessage: "That should work, thanks!",
    unreadCount: 0,
  },
];

const mockMessages = {
  "direct-2": [
    {
      id: 1,
      content: "Hey, how are you?",
      senderId: 2,
      senderName: "Jane Smith",
      timestamp: new Date("2024-01-20T10:00:00").toISOString(),
    },
    {
      id: 2,
      content: "I'm good, thanks! How about you?",
      senderId: 1,
      senderName: "John Doe",
      timestamp: new Date("2024-01-20T10:01:00").toISOString(),
    },
    {
      id: 3,
      content:
        "Doing well! Did you get a chance to review the project proposal?",
      senderId: 2,
      senderName: "Jane Smith",
      timestamp: new Date("2024-01-20T10:03:00").toISOString(),
    },
    {
      id: 4,
      content: "Yes, I just finished looking it over. Great work!",
      senderId: 1,
      senderName: "John Doe",
      timestamp: new Date("2024-01-20T10:05:00").toISOString(),
    },
    {
      id: 5,
      content: "I particularly liked the innovation section",
      senderId: 1,
      senderName: "John Doe",
      timestamp: new Date("2024-01-20T10:05:30").toISOString(),
    },
    {
      id: 6,
      content: "Thanks! I spent extra time on that part",
      senderId: 2,
      senderName: "Jane Smith",
      timestamp: new Date("2024-01-20T10:07:00").toISOString(),
    },
    {
      id: 7,
      content: "Should we schedule a team meeting to discuss implementation?",
      senderId: 2,
      senderName: "Jane Smith",
      timestamp: new Date("2024-01-20T10:10:00").toISOString(),
    },
  ],

  "group-1": [
    {
      id: 8,
      content: "Good morning everyone! Meeting at 3 PM today",
      senderId: 3,
      senderName: "Bob Johnson",
      timestamp: new Date("2024-01-20T09:00:00").toISOString(),
    },
    {
      id: 9,
      content: "I'll be there!",
      senderId: 1,
      senderName: "John Doe",
      timestamp: new Date("2024-01-20T09:05:00").toISOString(),
    },
    {
      id: 10,
      content:
        "Can we make it 3:30? I have another meeting that might run long",
      senderId: 2,
      senderName: "Jane Smith",
      timestamp: new Date("2024-01-20T09:10:00").toISOString(),
    },
    {
      id: 11,
      content: "3:30 works for me",
      senderId: 3,
      senderName: "Bob Johnson",
      timestamp: new Date("2024-01-20T09:12:00").toISOString(),
    },
    {
      id: 12,
      content: "Great, I'll update the calendar invite",
      senderId: 1,
      senderName: "John Doe",
      timestamp: new Date("2024-01-20T09:15:00").toISOString(),
    },
    {
      id: 13,
      content: "Don't forget to bring your project updates!",
      senderId: 3,
      senderName: "Bob Johnson",
      timestamp: new Date("2024-01-20T09:20:00").toISOString(),
    },
  ],

  "org-1": [
    {
      id: 14,
      content: "Welcome everyone to our new organizational chat!",
      senderId: 1,
      senderName: "John Doe",
      timestamp: new Date("2024-01-19T11:00:00").toISOString(),
    },
    {
      id: 15,
      content: "Excited to be here!",
      senderId: 2,
      senderName: "Jane Smith",
      timestamp: new Date("2024-01-19T11:02:00").toISOString(),
    },
    {
      id: 16,
      content: "Quick reminder: Company meeting next Tuesday",
      senderId: 3,
      senderName: "Bob Johnson",
      timestamp: new Date("2024-01-19T14:00:00").toISOString(),
    },
    {
      id: 17,
      content: "Will it be in-person or virtual?",
      senderId: 2,
      senderName: "Jane Smith",
      timestamp: new Date("2024-01-19T14:05:00").toISOString(),
    },
    {
      id: 18,
      content: "Hybrid - you can choose either option",
      senderId: 3,
      senderName: "Bob Johnson",
      timestamp: new Date("2024-01-19T14:10:00").toISOString(),
    },
    {
      id: 19,
      content: "Perfect, thanks!",
      senderId: 2,
      senderName: "Jane Smith",
      timestamp: new Date("2024-01-19T14:12:00").toISOString(),
    },
  ],

  "project-1": [
    {
      id: 20,
      content:
        "Project kickoff meeting notes are now available in the shared drive",
      senderId: 1,
      senderName: "John Doe",
      timestamp: new Date("2024-01-18T15:00:00").toISOString(),
    },
    {
      id: 21,
      content: "Thanks for sharing! I'll review them today",
      senderId: 2,
      senderName: "Jane Smith",
      timestamp: new Date("2024-01-18T15:30:00").toISOString(),
    },
    {
      id: 22,
      content: "Has anyone started on the design mockups?",
      senderId: 3,
      senderName: "Bob Johnson",
      timestamp: new Date("2024-01-18T16:00:00").toISOString(),
    },
    {
      id: 23,
      content:
        "I'm working on them now, should have something to share by tomorrow",
      senderId: 2,
      senderName: "Jane Smith",
      timestamp: new Date("2024-01-18T16:05:00").toISOString(),
    },
    {
      id: 24,
      content: "Looking forward to seeing them!",
      senderId: 1,
      senderName: "John Doe",
      timestamp: new Date("2024-01-18T16:10:00").toISOString(),
    },
  ],

  "task-1": [
    {
      id: 25,
      content: "I've updated the task description with more details",
      senderId: 1,
      senderName: "John Doe",
      timestamp: new Date("2024-01-17T13:00:00").toISOString(),
    },
    {
      id: 26,
      content: "Got it, reviewing now",
      senderId: 2,
      senderName: "Jane Smith",
      timestamp: new Date("2024-01-17T13:15:00").toISOString(),
    },
    {
      id: 27,
      content: "Quick question - what's the deadline for this?",
      senderId: 2,
      senderName: "Jane Smith",
      timestamp: new Date("2024-01-17T13:20:00").toISOString(),
    },
    {
      id: 28,
      content: "End of next week, but let me know if you need more time",
      senderId: 1,
      senderName: "John Doe",
      timestamp: new Date("2024-01-17T13:25:00").toISOString(),
    },
    {
      id: 29,
      content: "That should work, thanks!",
      senderId: 2,
      senderName: "Jane Smith",
      timestamp: new Date("2024-01-17T13:30:00").toISOString(),
    },
  ],
};

const state = {
  currentUser: { id: 1, name: "John Doe" },
  activeChat: null,
  chats: mockChats,
  messages: mockMessages,
  users: mockUsers,
  chatMembers: {},
  unreadCounts: {},
};

const mutations = {
  SET_ACTIVE_CHAT(state, chatId) {
    console.log("SET_ACTIVE_CHAT mutation called with:", chatId);
    state.activeChat = state.chats.find((chat) => chat.id === chatId) || null;
    console.log("Active chat set to:", state.activeChat);
  },
  ADD_MESSGAE(state, { chatId, message }) {
    if (!state.messages[chatId]) {
      state.messages[chatId] = [];
    }
    state.messages[chatId].push(message);
  },
  SET_CHAT_MEMBERS(state, { chatId, members }) {
    state.chatMembers[chatId] = members;
  },
};

const actions = {
  selectChat({ commit }, chatId) {
    console.log("selectChat action called with:", chatId);
    if (!chatId) return;
    commit("SET_ACTIVE_CHAT", chatId);
  },
  async fetchMessages({ commit }, chatId) {
    // Fetch messages from the server
  },
  sendMessage({ commit, state }, { chatId, content }) {
    if (!chatId || !content) return;

    const newMessage = {
      id: Date.now(),
      content,
      senderId: state.currentUser.id,
      senderName: state.currentUser.name,
      timestamp: new Date().toISOString(),
    };

    commit("ADD_MESSAGE", { chatId, message: newMessage });
    return newMessage;
  },
  async createChat({ commit }, { type, participants }) {
    // Create a new chat
  },
};

const getters = {
  getChatMessages: (state) => (chatId) => {
    return state.messages[chatId] || [];
  },
  getCurrentUser: (state, getters, rootState) => authState.currentUser,

  getActiveChat: (state) => state.activeChat,
};

export default {
  state,
  namespaced: true,
  actions,
  getters,
  mutations,
};
