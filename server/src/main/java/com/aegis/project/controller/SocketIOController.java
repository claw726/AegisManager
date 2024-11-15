package com.aegis.project.controller;

import com.aegis.project.dto.MessageDTO;
import com.aegis.project.model.SocketMessageModel;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class SocketIOController {
    private static final Logger logger = LoggerFactory.getLogger(SocketIOController.class);

    @Autowired
    private SocketIOServer socketServer;

    SocketIOController(SocketIOServer socketServer) {
        this.socketServer = socketServer;

        this.socketServer.addConnectListener(onUserConnectWithSocket);
        this.socketServer.addDisconnectListener(onUserDisconnectWithSocket);
        this.socketServer.addEventListener("messageSendToUser", SocketMessageModel.class, onSendMessage);
    }

    public boolean isUserConnected(String email) {
        Collection<SocketIOClient> clients = socketServer.getRoomOperations(email).getClients();
        return !clients.isEmpty();
    }

    public ConnectListener onUserConnectWithSocket = new ConnectListener() {
        @Override
        public void onConnect(SocketIOClient client) {
            String email = client.getHandshakeData().getSingleUrlParam("email");
            client.joinRoom(email);
            logger.info("User connected: " + email);
        }
    };

    public DisconnectListener onUserDisconnectWithSocket = new DisconnectListener() {
        @Override
        public void onDisconnect(SocketIOClient client) {
            String email = client.getHandshakeData().getSingleUrlParam("email");
            client.leaveRoom(email);
            logger.info("User disconnected: " + email);
        }
    };

    public DataListener<SocketMessageModel> onSendMessage = new DataListener<SocketMessageModel>() {
        @Override
        public void onData(SocketIOClient client, SocketMessageModel message, AckRequest acknowledge) throws Exception {
            message.setType("message");
            logger.info(message.getSenderEmail() + " sent message to " + message.getTargetEmail() + ": " + message.getMessage());
            socketServer.getRoomOperations(message.getTargetEmail()).sendEvent("message", message);
            acknowledge.sendAckData("Message sent to " + message.getTargetEmail());
        }
    };

    public void sendMessage(SocketMessageModel message) {
        logger.info(message.getSenderEmail() + " sent message to " + message.getTargetEmail() + ": " + message.getMessage());
        socketServer.getRoomOperations(message.getTargetEmail()).sendEvent("message", message);
    }

    public void sendMessageWithDTO(SocketMessageModel message, MessageDTO messageDTO) {
        logger.info(message.getSenderEmail() + " sent message to " + message.getTargetEmail() + ": " + message.getMessage());
        socketServer.getRoomOperations(message.getTargetEmail()).sendEvent("message", messageDTO);
    }
}