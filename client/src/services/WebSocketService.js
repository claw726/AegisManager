// src/services/WebSocketService.js
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

class WebSocketService {
  constructor() {
    this.client = null;
    this.subscription = null;
  }

  connect(onMessageReceived) {
    this.client = new Client({
      webSocketFactory: () => new SockJS("http://localhost:8080/api/ws"),
      debug: (str) => {
        console.log(str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    this.client.onConnect = () => {
      console.log("Connected to WebSocket");
      this.subscription = this.client.subscribe("/topic/public", (message) => {
        const receivedMessage = JSON.parse(message.body);
        onMessageReceived(receivedMessage);
      });
    };

    this.client.onStompError = (frame) => {
      console.error("WebSocket error:", frame);
    };

    this.client.activate();
  }

  disconnect() {
    if (this.client) {
      this.client.deactivate();
    }
  }

  sendMessage(message) {
    if (this.client && this.client.connected) {
      this.client.publish({
        destination: "/app/chat.sendMessage",
        body: JSON.stringify(message),
      });
    }
  }
}

export default new WebSocketService();
