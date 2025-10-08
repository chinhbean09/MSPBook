package com.chinhbean.chat.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.chinhbean.chat.entity.WebSocketSession;

@Repository
//
public interface WebSocketSessionRepository extends MongoRepository<WebSocketSession, String> {
    // Find all WebSocket sessions by user ID
    void deleteBySocketSessionId(String socketId);

    // Find all WebSocket sessions by a list of user IDs
    List<WebSocketSession> findAllByUserIdIn(List<String> userIds);
}
