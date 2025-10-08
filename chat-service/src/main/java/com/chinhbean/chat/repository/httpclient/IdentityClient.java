package com.chinhbean.chat.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.chinhbean.chat.dto.ApiResponse;
import com.chinhbean.chat.dto.request.IntrospectRequest;
import com.chinhbean.chat.dto.response.IntrospectResponse;

@FeignClient(name = "identity-client", url = "${app.services.identity.url}")
public interface IdentityClient {
    // Validate token and get user info from Identity service
    @PostMapping("/auth/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request);
}
