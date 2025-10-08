package com.chinhbean.chat.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.corundumstudio.socketio.SocketIOServer;

@Configuration
public class SocketIOConfig {
    @Bean
    // Create and configure the SocketIOServer bean
    public SocketIOServer socketIOServer() {
        // Set up the configuration for the Socket.IO server
        com.corundumstudio.socketio.Configuration configuration = new com.corundumstudio.socketio.Configuration();
        configuration.setPort(8099);
        // Allow all origins for CORS ()
        configuration.setOrigin("*");

        return new SocketIOServer(configuration);
    }
}
