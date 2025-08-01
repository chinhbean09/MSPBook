package com.chinhbean.gateway.configuration;

import com.chinhbean.gateway.repository.IdentityClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
//WebClientConfiguration cấu hình WebClient và tạo proxy cho IdentityClient thông qua HttpServiceProxyFactory.
// Sự kết hợp này cho phép Gateway giao tiếp với Identity Service một cách reactive.
public class WebClientConfiguration {
    @Bean
    //Tạo một instance WebClient để thực hiện các cuộc gọi HTTP. Đây là bước bắt buộc trong HTTP Interface, khác với Feign Client, nơi bạn không cần tự tạo client mà chỉ cần định nghĩa @FeignClient.
    WebClient webClient(){
        //Tạo và cấu hình một instance của WebClient, là client HTTP phản ứng trong Spring WebFlux.
        return WebClient.builder()
//        Đặt URL cơ sở, nơi Identity Service đang chạy. Tất cả các request từ IdentityClient sẽ được ghép với URL này
                .baseUrl("http://localhost:8080/identity")
                .build();
    }

    @Bean
    //Tạo proxy cho interface IdentityClient dựa trên WebClient
    IdentityClient identityClient(WebClient webClient){
        //Tạo một proxy cho interface IdentityClient dựa trên WebClient, cho phép gọi API của Identity Service một cách dễ dàng.
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient)).build();

        return httpServiceProxyFactory.createClient(IdentityClient.class);
    }
}
