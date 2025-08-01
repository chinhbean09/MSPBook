package com.chinhbean.gateway.service;

import com.chinhbean.gateway.dto.ApiResponse;
import com.chinhbean.gateway.dto.request.IntrospectRequest;
import com.chinhbean.gateway.dto.response.IntrospectResponse;
import com.chinhbean.gateway.repository.IdentityClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdentityService {
    IdentityClient identityClient;
// for validation and return the result as a reactive stream.
    public Mono<ApiResponse<IntrospectResponse>> introspect(String token){
        return identityClient.introspect(IntrospectRequest.builder()
                        .token(token)
                .build());
    }
}
