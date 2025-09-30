package com.chinhbean.profile.configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
// Interceptor to pass the Authorization header from incoming requests to outgoing Feign client requests
/*
* user gọi API /profile với header Authorization: Bearer abc123.
Controller  xử lý và cần gọi sang service khác qua FeignClient.
nhờ vào  AuthenticationRequestInterceptor, FeignClient sẽ tự động thêm header Authorization: Bearer abc123 vào request gửi sang service kia.
* */

public class AuthenticationRequestInterceptor implements RequestInterceptor {
    @Override
    // This method is called for every Feign client request
    public void apply(RequestTemplate template) {
        // Get the current HTTP request attributes
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        // Extract the Authorization header from the current request
        assert servletRequestAttributes != null;
        // Get the "Authorization" header from the incoming HTTP request
        var authHeader = servletRequestAttributes.getRequest().getHeader("Authorization");

        log.info("Header: {}", authHeader);
        // If the Authorization header is present, add it to the Feign client request
        if (StringUtils.hasText(authHeader))
            template.header("Authorization", authHeader);
    }
}
