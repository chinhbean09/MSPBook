package com.chinhbean.identity.mapper;

import com.chinhbean.identity.dto.request.ProfileCreationRequest;
import com.chinhbean.identity.dto.request.UserCreationRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    //MapStruct sẽ tự động map các trường có cùng tên giữa UserCreationRequest và ProfileCreationRequest
    ProfileCreationRequest toProfileCreationRequest(UserCreationRequest request);
}
