package com.chinhbean.gateway.repository;

import com.chinhbean.gateway.dto.response.IntrospectResponse;
import com.chinhbean.gateway.dto.ApiResponse;
import com.chinhbean.gateway.dto.request.IntrospectRequest;
import com.chinhbean.gateway.dto.response.IntrospectResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

public interface IdentityClient {
    //không giống như feign client thì http interface của spring thì ta cần phải khai báo thêm 1 web client và register
    //interface này với HttpServiceProxy thì nó mới implement với chúng ta
    //=> Feign tự động tạo proxy và cấu hình, trong khi HTTP Interface yêu cầu bạn
    // tự tạo WebClient và đăng ký interface với HttpServiceProxyFactory
    @PostExchange(url = "/auth/introspect", contentType = MediaType.APPLICATION_JSON_VALUE)
    Mono<ApiResponse<IntrospectResponse>> introspect(@RequestBody IntrospectRequest request);
}
