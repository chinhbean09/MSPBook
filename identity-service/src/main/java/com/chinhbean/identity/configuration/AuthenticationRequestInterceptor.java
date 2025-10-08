package com.chinhbean.identity.configuration;

import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// @Component // nếu có mong muốn phục vụ cho tất cả request (global) thì biến nó thành 1 bean nguoc lai thi cau hinh
// cho tung feign client.
// Ex: @FeignClient(name = "profile-service", url = "${app.services.profile}"
//    ,configuration = {AuthenticationRequestInterceptor.class})
public class AuthenticationRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        var authHeader = servletRequestAttributes.getRequest().getHeader("Authorization");

        log.info("Header: {}", authHeader);
        if (StringUtils.hasText(authHeader)) template.header("Authorization", authHeader);
    }
}
