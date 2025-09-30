package com.chinhbean.file.repository;

import com.chinhbean.file.dto.FileInfo;
import com.chinhbean.file.entity.FileMgmt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Repository
public class FileRepository {
    @Value("${app.file.storage-dir}")
    String storageDir;

    @Value("${app.file.download-prefix}")
    String urlPrefix;

    public FileInfo store(MultipartFile file) throws IOException {
        //upload file to D:/upload

        Path folder = Paths.get(storageDir).normalize().toAbsolutePath();
        Files.createDirectories(folder);

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

        return FileInfo.builder()
                .name(fileName)
                .size(file.getSize())
                .contentType(file.getContentType())
                .md5Checksum(DigestUtils.md5DigestAsHex(file.getInputStream()))
                .path(filePath.toString())
                .url(urlPrefix + fileName)
                .build();
    }
    public Resource read(FileMgmt fileMgmt) throws IOException {
        var data = Files.readAllBytes(Path.of(fileMgmt.getPath()));
        return new ByteArrayResource(data);
    }
}
