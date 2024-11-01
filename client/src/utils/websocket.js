// utils/websocket.js
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import { useRoute } from 'vue-router';

let stompClient = null;

export function connect() {
  console.log("Connecting to WebSocket");
    const socket = () => new SockJS('http://localhost:8080/api/ws');
    stompClient = Stomp.over(socket);

    console.log("Connecting with Stomp client");
    stompClient.connect({}, onConnected, onError);
}

function onConnected() {
    console.log("Connected to WebSocket");
    // Subscribe to a specific destination
    stompClient.subscribe('/user/queue/task-updates', onTaskUpdate);
    console.log("Subscribed to /user/queue/task-updates");
}

function onTaskUpdate(message) {
    const taskUpdate = JSON.parse(message.body);
    console.log('Task updated:', taskUpdate);

    const route = useRoute();
    if ((route.name === 'TaskDetails' && route.params.id === taskUpdate.id)
        || route.name === 'TDList' || route.name === 'KB' || route.name === 'ProjectDashboard') {
        console.log('Task details page is open');
        window.location.reload();
    }
}

function onError(error) {
    console.error('Error connecting to WebSocket:', error);
}

export { stompClient };