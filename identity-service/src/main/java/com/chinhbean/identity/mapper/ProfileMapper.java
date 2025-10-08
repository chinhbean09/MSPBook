package com.chinhbean.identity.mapper;

import org.mapstruct.Mapper;

import com.chinhbean.identity.dto.request.ProfileCreationRequest;
import com.chinhbean.identity.dto.request.UserCreationRequest;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    // MapStruct sẽ tự động map các trường có cùng tên giữa UserCreationRequest và ProfileCreationRequest
    ProfileCreationRequest toProfileCreationRequest(UserCreationRequest request);
}
