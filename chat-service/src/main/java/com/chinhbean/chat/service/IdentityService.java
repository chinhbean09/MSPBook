package com.chinhbean.chat.service;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.chinhbean.chat.dto.request.IntrospectRequest;
import com.chinhbean.chat.dto.response.IntrospectResponse;
import com.chinhbean.chat.repository.httpclient.IdentityClient;

import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdentityService {
    IdentityClient identityClient;

    // Synchronous call to introspect token
    public IntrospectResponse introspect(IntrospectRequest request) {
        try {
            // Call identity service to introspect token synchronously
            var result = identityClient.introspect(request).getResult();
            // If result is null, return invalid response
            if (Objects.isNull(result)) {
                return IntrospectResponse.builder().valid(false).build();
            }
            return result;
            // If FeignException occurs, log error and return invalid response
        } catch (FeignException e) {
            log.error("Introspect failed: {}", e.getMessage(), e);
            return IntrospectResponse.builder().valid(false).build();
        }
    }
}
