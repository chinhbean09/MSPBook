package com.chinhbean.identity.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.chinhbean.identity.configuration.AuthenticationRequestInterceptor;
import com.chinhbean.identity.dto.request.ApiResponse;
import com.chinhbean.identity.dto.request.ProfileCreationRequest;
import com.chinhbean.identity.dto.response.UserProfileResponse;

@FeignClient(
        name = "profile-service",
        url = "${app.services.profile}",
        configuration = {AuthenticationRequestInterceptor.class})
public interface ProfileClient {
    @PostMapping(value = "/internal/users", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<UserProfileResponse> createProfile(
            //            @RequestHeader("Authorization") String token,
            @RequestBody ProfileCreationRequest request);
}
