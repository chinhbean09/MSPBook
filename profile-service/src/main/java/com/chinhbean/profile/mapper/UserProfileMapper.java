package com.chinhbean.profile.mapper;

import com.chinhbean.profile.dto.request.ProfileCreationRequest;
import com.chinhbean.profile.dto.response.UserProfileReponse;
import com.chinhbean.profile.entity.UserProfile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring") //component bao cho mapstruct biet day la bean va no se init cai bean cho chung ta
public interface UserProfileMapper {
    UserProfile toUserProfile(ProfileCreationRequest request);

    UserProfileReponse toUserProfileReponse(UserProfile entity);
}
