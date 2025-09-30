package com.chinhbean.chat.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "conversation")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Conversation {
    //id cho mỗi cuộc trò chuyện
    @MongoId
    String id;

    // GROUP, DIRECT chat 1-1.
    String type;

//chuỗi hash đại diện cho tổ hợp duy nhất các user trong conversation → tránh tạo trùng nhiều cuộc chat 1-1 giữa cùng 2 user.
    @Indexed(unique = true)
    String participantsHash;

    //danh sách thông tin của những người trong cuộc trò chuyện
    // 1 conversation can have many participants (1-1, 1-n or n-n)
    List<ParticipantInfo> participants;

    Instant createdDate;

    Instant modifiedDate;
}
