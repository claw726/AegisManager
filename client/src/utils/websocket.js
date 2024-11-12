// utils/websocket.js
import io from "socket.io-client";

let socket = null;

export function connect(email) {
  console.log("Connecting to WebSocket");
  socket = io("http://localhost:8080", {
    query: {
      email: email
    }
  });

  socket.on("connect", () => {
    console.log("Connected to WebSocket");
  });

  socket.on("message", (message) => {
    console.log("Received message:", message);
    if (message.type.contains("message")) {
      // Handle message
    } else if (message.type.contains("task")) {
      // Handle task update
    } else if (message.type.contains("project")) {
      // Handle project update
    }
  });

  socket.on("disconnect", () => {
    console.log("Disconnected from WebSocket");
  });

  socket.on("connect_error", (error) => {
    console.log("Error connecting to WebSocket:", error);
    setTimeout(() => {
      console.log("Attempting to reconnect...");
      connect(email);
    }, 5000);
  });
}

export function disconnect() {
  if (socket) {
    socket.disconnect();
    socket = null;
  }
}

export { socket };