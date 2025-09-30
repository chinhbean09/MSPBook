package com.chinhbean.file.controller;

import com.chinhbean.file.dto.ApiResponse;
import com.chinhbean.file.service.FileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/media/download/{fileName}")

    //Resource which represents a file or binary data. In Spring, Resource is used to handle files, streams, or other data sources.
    ResponseEntity<Resource> downloadMedia(@PathVariable String fileName) throws IOException {
        var fileData = fileService.download(fileName);

        return ResponseEntity.ok()
                //header for browser to understand the type of content being sent and render it appropriately.
                .header(HttpHeaders.CONTENT_TYPE, fileData.contentType())
                .body(fileData.resource());
    }

}
