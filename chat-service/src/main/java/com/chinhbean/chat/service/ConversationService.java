package com.chinhbean.chat.service;

import com.chinhbean.chat.dto.request.ConversationRequest;
import com.chinhbean.chat.dto.response.ConversationResponse;
import com.chinhbean.chat.entity.Conversation;
import com.chinhbean.chat.entity.ParticipantInfo;
import com.chinhbean.chat.exception.AppException;
import com.chinhbean.chat.exception.ErrorCode;
import com.chinhbean.chat.mapper.ConversationMapper;
import com.chinhbean.chat.repository.ConversationRepository;
import com.chinhbean.chat.repository.httpclient.ProfileClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConversationService {
    ConversationRepository conversationRepository;
    ProfileClient profileClient;

    ConversationMapper conversationMapper;

    public List<ConversationResponse> myConversations() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
       //Query all conversations that contains userId in participantIds
        List<Conversation> conversations = conversationRepository.findAllByParticipantIdsContains(userId);

        return conversations.stream().map(this::toConversationResponse).toList();
    }

    public ConversationResponse create(ConversationRequest request) {
        // get current user and remaining user infor from PS
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        var userInfoResponse = profileClient.getProfile(userId);
        var participantInfoResponse = profileClient.getProfile(
                request.getParticipantIds().getFirst());

        if (Objects.isNull(userInfoResponse) || Objects.isNull(participantInfoResponse)) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        var userInfo = userInfoResponse.getResult();
        var participantInfo = participantInfoResponse.getResult();

        List<String> userIds = new ArrayList<>();
        userIds.add(userId);
        userIds.add(participantInfo.getUserId());

        var sortedIds = userIds.stream().sorted().toList();
        String userIdHash = generateParticipantHash(sortedIds);

        //ParticipantInfo cho từng user
        List<ParticipantInfo> participantInfos = List.of(
            ParticipantInfo.builder()
                    .userId(userInfo.getUserId())
                    .username(userInfo.getUsername())
                    .firstName(userInfo.getFirstName())
                    .lastName(userInfo.getLastName())
                    .avatar(userInfo.getAvatar())
                  .build(),
                ParticipantInfo.builder()
                        .userId(participantInfo.getUserId())
                        .username(participantInfo.getUsername())
                        .firstName(participantInfo.getFirstName())
                        .lastName(participantInfo.getLastName())
                        .avatar(participantInfo.getAvatar())
                        .build()
        );

        // Build conversation info
        Conversation conversation = Conversation.builder()
                .type(request.getType())
                .participantsHash(userIdHash)
                .createdDate(Instant.now())
                .modifiedDate(Instant.now())
                .participants(participantInfos)
                .build();

        conversation = conversationRepository.save(conversation);

        return toConversationResponse(conversation);
    }

    private String generateParticipantHash(List<String> ids) {
        StringJoiner stringJoiner = new StringJoiner("_");
        ids.forEach(stringJoiner::add);

        // SHA 256

        return stringJoiner.toString();
    }

    private ConversationResponse toConversationResponse(Conversation conversation) {
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

        ConversationResponse conversationResponse = conversationMapper.toConversationResponse(conversation);

        conversation.getParticipants().stream()
                .filter(participantInfo -> !participantInfo.getUserId().equals(currentUserId))
                .findFirst().ifPresent(participantInfo -> {
                    conversationResponse.setConversationName(participantInfo.getUsername());
                    conversationResponse.setConversationAvatar(participantInfo.getAvatar());
                });

        return conversationResponse;
    }
}
