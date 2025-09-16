package com.chinhbean.file.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileService {
    public Object uploadFile(MultipartFile file) throws IOException {
        //upload file to D:/upload
        Path folder = Paths.get("D:/upload");
        if (Files.notExists(folder)) {
            Files.createDirectories(folder);
        }
        //get file extension (đuôi file)
        String fileExtension = StringUtils
                .getFilenameExtension(file.getOriginalFilename());

        //create file name with UUID to avoid trùng tên
        String fileName = Objects.isNull(fileExtension)
                ? UUID.randomUUID().toString()
                : UUID.randomUUID() + "." + fileExtension;

        //save file to folder D:/upload
        //resolve: ghép path với fileName thành D:/upload/tenfile (VD: D:/upload/abc.png)
        Path filePath = folder.resolve(fileName).normalize().toAbsolutePath();

        //copy file to folder (ghi đè nếu trùng tên)
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return null;
    }
}
