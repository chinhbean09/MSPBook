package com.chinhbean.gateway.configuration;

import com.chinhbean.gateway.dto.ApiResponse;
import com.chinhbean.gateway.service.IdentityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class AuthenticationFilter implements GlobalFilter, Ordered {
    IdentityService identityService;
    ObjectMapper objectMapper;

    @Value("${app.api-prefix}")
    @NonFinal
    private String apiPrefix;

    @NonFinal
    private String[] publicEndpoints = {
            "/identity/auth/.*",
            "/identity/users/registration"
    };

    private boolean isPublicEndpoint(ServerHttpRequest request){
        //chuyển mảng này thành một luồng (stream) để xử lý theo phong cách Java 8.
        return Arrays.stream(publicEndpoints)
                //Kiểm tra xem có ít nhất một phần tử trong luồng thỏa mãn điều kiện trong lambda expression.
                .anyMatch(s -> request.getURI().getPath().matches(apiPrefix + s));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        if (isPublicEndpoint(exchange.getRequest()))
            return chain.filter(exchange);

        //get jwt token from header request
        List<String> authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (CollectionUtils.isEmpty(authHeader))
            return unauthenticated(exchange.getResponse());
        //Take the first token and remove the Bearer prefix (JWT standard)
        String token = authHeader.getFirst().replace("Bearer ", "");
        log.info("Token: {}", token);

        //call introspect to check token validity
        return identityService.introspect(token)
                //convert the result from Mono<IntrospectResponse> to further processing.
                .flatMap(introspectResponse -> {
            if (introspectResponse.getResult().isValid())
                return chain.filter(exchange);
            else
                return unauthenticated(exchange.getResponse());
        })
                //if there is an exception (e.g. error calling introspect), return unauthenticated error.
                .onErrorResume(throwable -> unauthenticated(exchange.getResponse()));
    }

    @Override
    public int getOrder() {
        return -1;
    }

    //generate and send a 401 Unauthorized error response when the token is invalid or missing.
    Mono<Void> unauthenticated(ServerHttpResponse response){
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(1401)
                .message("Unauthenticated")
                .build();

        String body = null;
        try {
            body = objectMapper.writeValueAsString(apiResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }
}
