import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import store from "../store/index.js";

class WebSocketService {
  constructor() {
    this.stompClient = null;
    this.connected = false;
  }

  connect() {
    this.stompClient = new Client({
      webSocketFactory: () => new SockJS("https://localhost:8444/ws"),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: (frame) => {
        this.connected = true;
        store.commit("chat/SET_WS_CONNECTION_STATUS", true);
        console.log("Connected to WebSocket:", frame);

        // Subscribe to global messages
        this.stompClient.subscribe("/topic/messages", (message) => {
          const messageData = JSON.parse(message.body);
          store.commit("chat/ADD_MESSAGE", {
            chatId: messageData.chatId,
            message: messageData,
          });
          store.commit("chat/UPDATE_LAST_MESSAGE", {
            chatId: messageData.chatId,
            content: messageData.content,
          });
        });

        // Subscribe to specific chat room
        if (store.state.chat.activeChat) {
          this.subscribeToChatRoom(store.state.chat.activeChat.id);
        }
      },
      onDisconnect: () => {
        console.log("Disconnected from WebSocket");
        store.commit("chat/SET_WS_CONNECTION_STATUS", false);
        this.connected = false;
      },
      onStompError: (frame) => {
        console.error("STOMP error:", frame);
      },
    });

    this.stompClient.activate();
  }

  subscribeToChatRoom(chatId) {
    if (this.stompClient && this.connected) {
      this.stompClient.subscribe(`/topic/chat.${chatId}`, (message) => {
        const messageData = JSON.parse(message.body);
        // Handle chat-specific messages
      });
    }
  }

  sendMessage(message) {
    if (this.stompClient && this.connected) {
      this.stompClient.publish({
        destination: "/app/chat.send",
        body: JSON.stringify(message),
      });
    } else {
      console.error("WebSocket connection not established");
    }
  }

  disconnect() {
    if (this.stompClient) {
      this.stompClient.disconnect();
      this.connected = false;
    }
  }
}

export default new WebSocketService();
