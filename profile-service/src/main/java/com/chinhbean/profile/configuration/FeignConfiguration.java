package com.chinhbean.profile.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;

@Configuration
public class FeignConfiguration {
    @Bean
    // To support file upload with Feign Client
    // Encoder is used to encode the request body for what ?
    // Encoder sẽ chuyển file và các thông tin khác thành dạng multipart/form-data để server có thể nhận được file đúng
    // cách. Nếu không có Encoder phù hợp, server sẽ không hiểu dữ liệu bạn gửi lên.
    public Encoder multipartFormEncoder() {
        return new SpringFormEncoder();
    }
}
