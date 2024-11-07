// utils/websocket.js
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";
import { useRoute } from "vue-router";

let stompClient = null;

export function connect() {
  console.log("Connecting to WebSocket");
  const socket = () => new SockJS("https://localhost:8444/ws");
  console.log("Stomping over")
  stompClient = Stomp.over(socket);

  console.log("Connecting with Stomp client");
  stompClient.connect({}, onConnected, onError);
}

function onConnected() {
  console.log("Connected to WebSocket");
  // Subscribe to a specific destination
  stompClient.subscribe("/user/queue/task-updates", onTaskUpdate);
  console.log("Subscribed to /user/queue/task-updates");
}

function onTaskUpdate(message) {
  try {
    const taskUpdate = JSON.parse(message.body);
    console.log("Task updated:", taskUpdate);

    // Don't use useRoute() here - it should only be used in Vue components
    // Instead, you could emit an event or use a callback
    window.dispatchEvent(
      new CustomEvent("task-updated", {
        detail: taskUpdate,
      }),
    );
  } catch (error) {
    console.error("Error processing task update:", error);
  }
}

function onError(error) {
  console.error("Error connecting to WebSocket:", error);
  // 4. Add reconnection logic
  setTimeout(() => {
    console.log("Attempting to reconnect...");
    connect();
  }, 5000);
}

export function disconnect() {
  if (stompClient) {
    stompClient.disconnect();
    stompClient = null;
  }
}

export { stompClient };
