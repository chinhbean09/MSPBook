package com.chinhbean.profile.repository.httpclient;

import com.chinhbean.profile.configuration.AuthenticationRequestInterceptor;
import com.chinhbean.profile.dto.ApiResponse;
import com.chinhbean.profile.dto.response.FileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "file-service", url = "${app.services.file}",
    //configuration for adding JWT token to the request header
        configuration = { AuthenticationRequestInterceptor.class })

public interface FileClient {
    @PostMapping(value = "/file/media/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // Use @RequestPart for multipart file upload
    ApiResponse<FileResponse> uploadMedia(@RequestPart("file") MultipartFile file);
}
