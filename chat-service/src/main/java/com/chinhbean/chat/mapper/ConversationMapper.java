package com.chinhbean.chat.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.chinhbean.chat.dto.response.ConversationResponse;
import com.chinhbean.chat.entity.Conversation;

@Mapper(componentModel = "spring")
public interface ConversationMapper {
    ConversationResponse toConversationResponse(Conversation conversation);

    List<ConversationResponse> toConversationResponseList(List<Conversation> conversations);
}
