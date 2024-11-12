import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import store from "../store/index.js";

class WebSocketService {
  constructor() {
    this.stompClient = null;
    this.connected = false;
    this.subscriptions = new Map();
    this.connectionPromise = null;
    this.reconnectionAttempts = 0;
    this.maxReconnectionAttempts = 5;
  }

  connect() {
    if (this.connectionPromise) {
      return this.connectionPromise;
    }

    console.log("Connecting to WebSocket");

    this.connectionPromise = new Promise((resolve, reject) => {
      const authToken = store.state.auth.authToken;
      const socket = new SockJS("https://localhost:8443/api/ws");

      console.log("Auth token:", authToken);

      this.stompClient = new Client({
        webSocketFactory: () => socket,
        connectHeaders: {
          Authorization: `Bearer ${authToken}`,
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
        debug: function (str) {
          console.log("STOMP: " + str);
        },

        beforeConnect: () => {
          console.log("Attempting to connect to ws.");
        },

        onConnect: (frame) => {
          this.connected = true;
          this.reconnectionAttempts = 0;
          store.commit("auth/SET_WS_STATUS", true);
          console.log("WebSocket connected");
          resolve();
        },

        onDisconnect: () => {
          this.connected = false;
          store.commit("auth/SET_WS_STATUS", false);
          console.log("WebSocket disconnected");
          this.handleDisconnect();
        },

        onStompError: (frame) => {
          console.error("STOMP error:", frame);
          this.handleError(frame);
        },

        onWebSocketClose: () => {
          console.log("WebSocket closed");
          this.handleDisconnect();
        },

        onWebSocketError: (event) => {
          console.error("WebSocket error:", event);
          this.handleError(event);
        },
      });

      console.log("Activating STOMP client");

      try {
        this.stompClient.activate();
      } catch (error) {
        console.error("Error activating STOMP client:", error);
        reject(error);
      }

      console.log("STOMP client activated");
    });

    console.log("Connection promise:", this.connectionPromise);

    return this.connectionPromise;
  }

  handleError(error) {
    console.error("WebSocket error:", error);
    this.disconnect();
  }

  handleDisconnect() {
    this.connected = false;
    store.commit("auth/SET_WS_STATUS", false);
    this.connectionPromise = null;

    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++;
      console.log(
        `Attempting reconnect ${this.reconnectAttempts}/${this.maxReconnectAttempts}`,
      );
      setTimeout(() => this.connect(), 5000);
    } else {
      console.error("Max reconnection attempts reached");
      store.commit("SET_ERROR", "Unable to establish WebSocket connection");
    }
  }

  resubscribeAll() {
    this.subscriptions.forEach((callback, destination) => {
      this.subscribe(destination, callback);
    });
  }

  subscribe(destination, callback) {
    if (this.stompClient && this.connected) {
      const subscription = this.stompClient.subscribe(
        destination,
        (message) => {
          callback(message);
        },
      );
      this.subscriptions.set(destination, callback);
      return subscription;
    }
  }

  unsubscribe(destination) {
    if (this.subscriptions.has(destination)) {
      this.subscriptions.delete(destination);
      // Find and unsubscribe from the STOMP subscription
      this.stompClient?.unsubscribe(destination);
    }
  }

  subscribeToChatRoom(chatId) {
    return this.subscribe(`/topic/chat.${chatId}`, (message) => {
      const messageData = JSON.parse(message.body);
      store.commit("chat/ADD_MESSAGE", {
        chatId,
        message: messageData,
      });
    });
  }

  sendMessage(destination, message) {
    if (this.stompClient && this.connected) {
      this.stompClient.publish({
        destination,
        body: JSON.stringify(message),
      });
    } else {
      console.error("WebSocket connection not established");
    }
  }

  disconnect() {
    if (this.stompClient) {
      this.stompClient.deactivate();
      this.connected = false;
      this.subscriptions.clear();
    }
  }
}

export default new WebSocketService();
