let socket = null;
let reconnectTimer = null;
let shouldReconnect = true;
const messageHandlers = new Set();

const getWebSocketUrl = () => {
    if (import.meta.env.VITE_WS_URL) return import.meta.env.VITE_WS_URL;

    const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';
    const absoluteApiUrl = new URL(apiUrl, window.location.origin);
    absoluteApiUrl.protocol = absoluteApiUrl.protocol === 'https:' ? 'wss:' : 'ws:';
    absoluteApiUrl.pathname = '/ws/conversations';
    absoluteApiUrl.search = '';
    return absoluteApiUrl.toString();
};

const WebSocketService = {
    connect(onMessage, onError) {
        shouldReconnect = true;

        return new Promise((resolve, reject) => {
            if (socket?.readyState === WebSocket.OPEN) {
                resolve();
                return;
            }

            try {
                socket = new WebSocket(getWebSocketUrl());
                socket.onopen = () => resolve();
                socket.onmessage = (event) => {
                    try {
                        const message = JSON.parse(event.data);
                        messageHandlers.forEach((handler) => handler(message));
                        onMessage?.(message);
                    } catch (error) {
                        console.error('Message WebSocket invalide', error);
                    }
                };
                socket.onerror = (error) => {
                    onError?.(error);
                    reject(error);
                };
                socket.onclose = () => {
                    socket = null;
                    if (shouldReconnect) {
                        reconnectTimer = window.setTimeout(() => {
                            this.connect(onMessage, onError).catch(() => {});
                        }, 3000);
                    }
                };
            } catch (error) {
                reject(error);
            }
        });
    },

    send(message) {
        if (socket?.readyState !== WebSocket.OPEN) return false;
        socket.send(JSON.stringify(message));
        return true;
    },

    joinConversation(conversationId, userId) {
        return this.send({ action: 'JOIN_CONVERSATION', conversationId, userId });
    },

    sendMessage(conversationId, userId, content) {
        return this.send({ action: 'SEND_MESSAGE', conversationId, userId, content });
    },

    leaveConversation(conversationId, userId) {
        return this.send({ action: 'LEAVE_CONVERSATION', conversationId, userId });
    },

    onMessage(handler) { messageHandlers.add(handler); },
    offMessage(handler) { messageHandlers.delete(handler); },

    disconnect() {
        shouldReconnect = false;
        window.clearTimeout(reconnectTimer);
        socket?.close();
        socket = null;
    },

    isConnected() { return socket?.readyState === WebSocket.OPEN; },
};

export default WebSocketService;
