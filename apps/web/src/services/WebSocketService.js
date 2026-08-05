let ws = null;
let messageHandlers = [];

const WebSocketService = {
    /**
     * Connecte au serveur WebSocket
     * @param {Function} onMessage - Callback quand un message est reçu
     * @param {Function} onError - Callback en cas d'erreur
     * @returns {Promise}
     */
    connect(onMessage, onError) {
        return new Promise((resolve, reject) => {
            if (ws && ws.readyState === WebSocket.OPEN) {
                resolve();
                return;
            }

            try {
                const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
                const wsUrl = `${protocol}//localhost:8080/ws/conversations`;

                console.log('📡 Connexion WebSocket:', wsUrl);
                ws = new WebSocket(wsUrl);

                ws.onopen = () => {
                    console.log('✅ WebSocket connecté');
                    resolve();
                };

                ws.onmessage = (event) => {
                    try {
                        const message = JSON.parse(event.data);
                        console.log('📨 Message reçu:', message);

                        // Appeler tous les handlers enregistrés
                        messageHandlers.forEach(handler => {
                            try {
                                handler(message);
                            } catch (err) {
                                console.error('Erreur handler message:', err);
                            }
                        });

                        if (onMessage) onMessage(message);
                    } catch (err) {
                        console.error('Erreur parsing message:', err);
                    }
                };

                ws.onerror = (error) => {
                    console.error('❌ Erreur WebSocket:', error);
                    if (onError) onError(error);
                    reject(error);
                };

                ws.onclose = () => {
                    console.log('🔌 WebSocket fermé');
                    ws = null;
                    // Tentative de reconnexion après 3s
                    setTimeout(() => {
                        if (!ws) {
                            console.log('🔄 Tentative de reconnexion...');
                            this.connect(onMessage, onError).catch(() => { });
                        }
                    }, 3000);
                };
            } catch (err) {
                console.error('Erreur création WebSocket:', err);
                reject(err);
            }
        });
    },

    /**
     * Envoie un message via WebSocket
     * @param {Object} message - Message à envoyer
     */
    send(message) {
        if (!ws || ws.readyState !== WebSocket.OPEN) {
            console.warn('⚠️ WebSocket non connecté');
            return false;
        }

        try {
            ws.send(JSON.stringify(message));
            console.log('📤 Message envoyé:', message);
            return true;
        } catch (err) {
            console.error('Erreur envoi message:', err);
            return false;
        }
    },

    /**
     * Rejoint une conversation
     * @param {String} conversationId 
     * @param {Integer} userId 
     */
    joinConversation(conversationId, userId) {
        return this.send({
            action: 'JOIN_CONVERSATION',
            conversationId,
            userId
        });
    },

    /**
     * Envoie un message dans la conversation
     * @param {String} conversationId 
     * @param {Integer} userId 
     * @param {String} content 
     */
    sendMessage(conversationId, userId, content) {
        return this.send({
            action: 'SEND_MESSAGE',
            conversationId,
            userId,
            content
        });
    },

    /**
     * Quitte la conversation
     * @param {String} conversationId 
     * @param {Integer} userId 
     */
    leaveConversation(conversationId, userId) {
        return this.send({
            action: 'LEAVE_CONVERSATION',
            conversationId,
            userId
        });
    },

    /**
     * S'enregistre pour recevoir les messages
     * @param {Function} handler 
     */
    onMessage(handler) {
        messageHandlers.push(handler);
    },

    /**
     * Se désenregistre des messages
     * @param {Function} handler 
     */
    offMessage(handler) {
        messageHandlers = messageHandlers.filter(h => h !== handler);
    },

    /**
     * Ferme la connexion WebSocket
     */
    disconnect() {
        if (ws) {
            ws.close();
            ws = null;
        }
    },

    /**
     * Vérifie si connecté
     */
    isConnected() {
        return ws && ws.readyState === WebSocket.OPEN;
    }
};

export default WebSocketService;
