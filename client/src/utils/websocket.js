import { io } from 'socket.io-client';
import { store } from '@/store/index.js';

class WebSocketService {
    #socket = null;
    #messageHandlers = new Set();
    #config = {
        SOCKET_URL: 'http://localhost:8080',
        SOCKET_OPTIONS: {
            transports: ['websocket'],
            autoConnect: true,
            reconnection: true,
            reconnectionDelay: 1000,
            reconnectionAttempts: 5
        }
    };

    // Define if we want debug logging (you can set this based on your needs)
    #isDebugMode = true;

    #log(type, ...args) {
        const timestamp = new Date().toISOString();
        const prefix = `[WebSocket ${type}] ${timestamp}:`;
        
        switch(type.toLowerCase()) {
            case 'error':
                console.error(prefix, ...args);
                break;
            case 'warn':
                console.warn(prefix, ...args);
                break;
            case 'info':
                console.info(prefix, ...args);
                break;
            case 'debug':
                if (this.#isDebugMode) {
                    console.debug(prefix, ...args);
                }
                break;
            default:
                console.log(prefix, ...args);
        }
    }

    connect(email) {
        try {
            const socketUrl = `${this.#config.SOCKET_URL}?email=${email}`;
            this.#log('info', 'Attempting connection to', socketUrl);
            
            this.#socket = io(socketUrl, this.#config.SOCKET_OPTIONS);
            this.#setupEventListeners();
            
            this.#log('info', 'Socket instance created');
            return this.#socket;
        } catch (error) {
            this.#log('error', 'Connection error:', error);
            throw error;
        }
    }

    #setupEventListeners() {
        if (!this.#socket) {
            this.#log('error', 'No socket instance available for setting up listeners');
            return;
        }

        // Connection events
        this.#socket.on('connect', () => {
            store.commit("chat/SET_WS_CONNECTED", true);
            this.#log('info', 'Connected successfully', {
                socketId: this.#socket.id,
                transport: this.#socket.io.engine.transport.name
            });
        });

        // Message events
        this.#socket.on('message', (messageData) => {
            this.#log('info', 'Received message:', messageData);
            
            try {
                store.dispatch('chat/handleNewMessage', {
                    id: messageData.id,
                    chatId: messageData.chatId,
                    content: messageData.content,
                    senderId: messageData.senderId,
                    senderEmail: messageData.senderEmail,
                    timestamp: messageData.timestamp || new Date().toISOString(),
                    status: 'delivered',
                    type: messageData.type || 'text',
                    chatTitle: messageData.chatTitle,
                    participants: messageData.participants
                });
                this.#log('debug', 'Message processed successfully');
            } catch (error) {
                this.#log('error', 'Failed to process message:', error);
            }

            // Custom handlers
            this.#messageHandlers.forEach(handler => {
                try {
                    handler(messageData);
                } catch (error) {
                    this.#log('error', 'Custom handler failed:', error);
                }
            });
        });

        // Chat-specific messages
        this.#socket.on('chat-message', (messageData) => {
            this.#log('info', 'Received chat message:', messageData);
            try {
                store.dispatch('chat/handleNewMessage', messageData);
                this.#log('debug', 'Chat message processed');
            } catch (error) {
                this.#log('error', 'Failed to process chat message:', error);
            }
        });

        // Global messages
        this.#socket.on('global-message', (data) => {
            this.#log('info', 'Received global message:', data);
            try {
                store.dispatch('chat/handleGlobalMessage', data);
                this.#log('debug', 'Global message processed');
            } catch (error) {
                this.#log('error', 'Failed to process global message:', error);
            }
        });

        // Connection events
        this.#socket.on('disconnect', (reason) => {
            store.commit('chat/SET_WS_CONNECTED', false);
            this.#log('warn', 'Disconnected:', reason);
        });

        this.#socket.on('connect_error', (error) => {
            store.commit('chat/SET_WS_CONNECTED', false);
            this.#log('error', 'Connection error:', error);
        });

        // Reconnection events
        this.#socket.on('reconnect', (attemptNumber) => {
            store.commit('chat/SET_WS_CONNECTED', true);
            this.#log('info', 'Reconnected after', attemptNumber, 'attempts');
        });

        this.#socket.on('reconnect_attempt', (attemptNumber) => {
            this.#log('info', 'Attempting reconnection:', attemptNumber);
        });

        this.#socket.on('reconnect_error', (error) => {
            store.commit('chat/SET_WS_CONNECTED', false);
            this.#log('error', 'Reconnection error:', error);
        });

        this.#socket.on('reconnect_failed', () => {
            store.commit('chat/SET_WS_CONNECTED', false);
            this.#log('error', 'Reconnection failed after all attempts');
        });

        // Debug events
        this.#socket.on('ping', () => {
            this.#log('debug', 'Ping sent');
        });

        this.#socket.on('pong', (latency) => {
            this.#log('debug', 'Pong received, latency:', latency, 'ms');
        });

        // Log all events if debug mode is enabled
        if (this.#isDebugMode) {
            this.#socket.onAny((eventName, ...args) => {
                this.#log('debug', 'Event received:', eventName, args);
            });
        }
    }

    disconnect() {
        if (this.#socket?.connected) {
            this.#log('info', 'Initiating disconnect');
            this.#socket.disconnect();
            store.commit('chat/SET_WS_CONNECTED', false);
            this.#socket = null;
            this.#log('info', 'Disconnected successfully');
        } else {
            this.#log('warn', 'Disconnect called but socket was not connected');
        }
    }

    sendMessage(message) {
        if (!this.#socket?.connected) {
            const error = new Error('Socket not connected');
            this.#log('error', 'Cannot send message:', error);
            throw error;
        }
        
        this.#log('info', 'Sending message:', message);
        
        return new Promise((resolve, reject) => {
            this.#socket.emit('messageSendToUser', message, (response) => {
                if (response?.error) {
                    this.#log('error', 'Message send failed:', response.error);
                    reject(new Error(response.error));
                } else {
                    this.#log('info', 'Message sent successfully:', response);
                    
                    // Add to store
                    try {
                        store.dispatch('chat/handleNewMessage', {
                            ...message,
                            id: response.id || `temp-${Date.now()}`,
                            timestamp: response.timestamp || new Date().toISOString(),
                            status: 'sent'
                        });
                        this.#log('debug', 'Message added to store');
                    } catch (error) {
                        this.#log('error', 'Failed to add message to store:', error);
                    }
                    
                    resolve(response);
                }
            });
        });
    }

    onMessage(handler) {
        this.#log('debug', 'Adding message handler');
        this.#messageHandlers.add(handler);
        return () => {
            this.#log('debug', 'Removing message handler');
            this.#messageHandlers.delete(handler);
        };
    }

    get isConnected() {
        return this.#socket?.connected ?? false;
    }
}

// Create and export a single instance
const webSocketService = new WebSocketService();

export const connect = (email) => webSocketService.connect(email);
export const disconnect = () => webSocketService.disconnect();
export const sendMessage = (message) => webSocketService.sendMessage(message);
export const onMessage = (handler) => webSocketService.onMessage(handler);
export const isConnected = () => webSocketService.isConnected;

export default webSocketService;