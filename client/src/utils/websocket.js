// utils/websocket.js
import { SockJS } from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

var socket = new SockJS('http://localhost:8081/api/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function (frame) {
  console.log('Connected: ' + frame);

  // Subscribe to a specific destination
  stompClient.subscribe('/user/queue/task-updates', function (message) {
    const taskUpdate = JSON.parse(message.body);
    console.log('Task updated:', taskUpdate);
  });
});

export { stompClient };