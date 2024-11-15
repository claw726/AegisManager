// utils/websocket.js
import io from "socket.io-client";
import store from "@/store/index.js";

let socket = null;

export function connect(email) {

  if (socket?.connected) {
    console.log('Socket already connected');
    return socket;
  }

  if (!email || !store.state.auth.authToken) {
    console.error('Email or auth token missing');
    return null;
  }


  console.log("Connecting to WebSocket");

  const socketConfig = {
    path: "/socket.io",
    query: {
      email: email,
      token: store.state.auth.authToken
    },
    reconnection: true,
    reconnectionAttempts: 10,
    reconnectionDelay: 1000,
    timeout: 10000,
    transports: ["websocket"]
  };

  const SOCKET_URL = "http://localhost:8080";

  socket = io(SOCKET_URL, socketConfig);

  socket.on("connect", () => {
    store.commit('chat/SET_WS_CONNECTED', true);
    console.log("Connected to WebSocket");
    console.log("[WebSocket] Socket ID:", socket.id);
  });

  // Disconnection handling
  socket.on("disconnect", (reason) => {
    store.commit('chat/SET_WS_CONNECTED', false);
    console.log("[WebSocket] Disconnected:", reason);
    
    // Handle specific disconnect reasons
    if (reason === 'io server disconnect') {
      // Server initiated disconnect, attempt reconnection
      socket.connect();
    }
  });

  // Unified message handling
  socket.on("chat_message", (messageData) => {
    console.log('[WebSocket] Received chat message:', messageData);
    if (messageData && messageData.content) {
      store.dispatch('chat/handleNewMessage', messageData);
    } else {
      console.warn('[WebSocket] Received malformed message:', messageData);
    }
  });

  // Connection error handling
  socket.on("connect_error", (error) => {
    console.error("[WebSocket] Connection error:", error.message);
    store.commit('chat/SET_WS_CONNECTED', false);
    console.log("[WebSocket] Connection details:", {
      url: SOCKET_URL,
      path: socketConfig.path,
      email: email
    });
  });

  // Error handling
  socket.on("error", (error) => {
    console.error("[WebSocket] Socket error:", error);
  });

  socket.on('auth_error', (error) => {
    console.error('[WebSocket] Authentication error:', error);
    store.commit('chat/SET_WS_CONNECTED', false);
  });

  // Reconnection handling
  socket.on("reconnect_attempt", (attemptNumber) => {
    console.log(`[WebSocket] Reconnection attempt ${attemptNumber}`);
  });

  socket.on("reconnect", (attemptNumber) => {
    console.log(`[WebSocket] Reconnected after ${attemptNumber} attempts`);
    store.commit('chat/SET_WS_CONNECTED', true);
  });

  return socket;
}

export function disconnect() {
  if (socket) {
    console.log('[WebSocket] Initiating disconnect');
    socket.disconnect();
    socket = null;
    store.commit('chat/SET_WS_CONNECTED', false);
  }
}

export function isConnected() {
  return socket?.connected || false;
}

export function emitEvent(eventName, data) {
  return new Promise((resolve, reject) => {
    if (!socket?.connected) {
      console.error('[WebSocket] Cannot emit event: socket not connected');
      reject(new Error('Socket is not connected'));
      return;
    }

    console.log(`[WebSocket] Emitting event "${eventName}":`, data);
    
    socket.emit(eventName, data, (response) => {
      if (response && typeof response === 'string' && response.startsWith('Error:')) {
        console.error(`[WebSocket] Event "${eventName}" failed:`, response);
        reject(new Error(response.substring(7)));
      } else {
        console.log(`[WebSocket] Event "${eventName}" succeeded:`, response);
        resolve(response);
      }
    });
  });
}

// Add a helper function to send chat messages specifically
export function sendChatMessage(message) {
  return emitEvent('chat_message', message)
    .catch(error => {
      console.error('[WebSocket] Failed to send chat message:', error);
      throw error;
    });
}

export { socket };