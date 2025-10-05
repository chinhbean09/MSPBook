package com.chinhbean.chat.controller;

import com.chinhbean.chat.dto.request.IntrospectRequest;
import com.chinhbean.chat.entity.WebSocketSession;
import com.chinhbean.chat.service.IdentityService;
import com.chinhbean.chat.service.WebSocketSessionService;
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

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SocketHandler {
    // Injected SocketIOServer instance
    SocketIOServer server;
    IdentityService identityService;
    WebSocketSessionService webSocketSessionService;

    @OnConnect
    //OnConnect annotation to indicate that this method should be called when a client connects to the server
    // Method to handle client connection events
    public void clientConnected(SocketIOClient client) {
 // token from the client's handshake data
        String token = client.getHandshakeData().getSingleUrlParam("token");
        // Verify token using the IdentityService
        //builder pattern to create an IntrospectRequest object with the token, why ? because IntrospectRequest has only one field
        var introspectResponse = identityService.introspect(IntrospectRequest.builder()
                .token(token)
                .build());
        // If Token is invalid disconnect the client
        if (introspectResponse.isValid()) {
            // Log the client connection event with the session ID
            //builder for design pattern that allows for the step-by-step construction of complex objects

            WebSocketSession webSocketSession = WebSocketSession.builder()
                    .socketSessionId(client.getSessionId().toString())
                    .userId(introspectResponse.getUserId())
                    .createdAt(Instant.now())
                    .build();
            webSocketSession = webSocketSessionService.create(webSocketSession);

            log.info("WebSocketSession created with id: {}", webSocketSession.getId());
        } else {
            // Log an error message and disconnect the client
            log.error("Authentication fail: {}", client.getSessionId());
            client.disconnect();
        }
    }

    @OnDisconnect
    public void clientDisconnected(SocketIOClient client) {
        log.info("Client disConnected: {}", client.getSessionId());
        webSocketSessionService.deleteSession(client.getSessionId().toString());

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
