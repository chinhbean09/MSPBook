package com.chinhbean.chat.controller;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SocketHandler {
    // Injected SocketIOServer instance
    SocketIOServer server;

    @OnConnect
    //OnConnect annotation to indicate that this method should be called when a client connects to the server
    // Method to handle client connection events
    public void clientConnected(SocketIOClient client) {
        log.info("Client connected: {}", client.getSessionId());
    }

    @OnDisconnect
    public void clientDisconnected(SocketIOClient client) {
        log.info("Client disConnected: {}", client.getSessionId());
    }

    @PostConstruct
    //PostConstruct annotation to indicate that this method should be called after the bean's properties have been set
    // Method to start the Socket.IO server
    public void startServer() {
        server.start();
        //addListeners method to register this class as a listener for Socket.IO events for the server instance
        server.addListeners(this);
        log.info("Socket server started");
    }

    @PreDestroy
    // Method to stop the Socket.IO server
    public void stopServer() {
        server.stop();
        log.info("Socket server stoped");
    }
}
