package com.chinhbean.profile.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.chinhbean.profile.dto.request.ProfileCreationRequest;
import com.chinhbean.profile.dto.request.UpdateProfileRequest;
import com.chinhbean.profile.dto.response.UserProfileResponse;
import com.chinhbean.profile.entity.UserProfile;

@Mapper(componentModel = "spring") // component bao cho mapstruct biet day la bean va no se init cai bean cho chung ta
public interface UserProfileMapper {
    UserProfile toUserProfile(ProfileCreationRequest request);

    UserProfileResponse toUserProfileResponse(UserProfile entity);

    void update(@MappingTarget UserProfile entity, UpdateProfileRequest request);
}
