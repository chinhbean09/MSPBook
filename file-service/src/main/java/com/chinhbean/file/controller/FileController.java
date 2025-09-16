package com.chinhbean.file.controller;

import com.chinhbean.file.dto.ApiResponse;
import com.chinhbean.file.service.FileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileController {
    FileService fileService;

    @PostMapping("/media/upload")
    ApiResponse<Object> uploadMedia(@RequestParam("file") MultipartFile file) throws IOException {
        return ApiResponse.builder()
                .result(fileService.uploadFile(file))
                .build();
    }
}
