package com.chinhbean.chat.mapper;

import com.chinhbean.chat.dto.response.ConversationResponse;
import com.chinhbean.chat.entity.Conversation;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConversationMapper {
    ConversationResponse toConversationResponse(Conversation conversation);

    List<ConversationResponse> toConversationResponseList(List<Conversation> conversations);
}
