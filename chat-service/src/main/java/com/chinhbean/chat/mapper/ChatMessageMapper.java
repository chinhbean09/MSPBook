package com.chinhbean.chat.mapper;

import com.chinhbean.chat.dto.request.ChatMessageRequest;
import com.chinhbean.chat.dto.response.ChatMessageResponse;
import com.chinhbean.chat.entity.ChatMessage;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {
    ChatMessageResponse toChatMessageResponse(ChatMessage chatMessage);

    ChatMessage toChatMessage(ChatMessageRequest request);

    List<ChatMessageResponse> toChatMessageResponses(List<ChatMessage> chatMessages);
}
