package com.lsb.kkirikkiri.services;

import com.lsb.kkirikkiri.entities.ArticleEntity;
import com.lsb.kkirikkiri.entities.FileEntity;
import com.lsb.kkirikkiri.entities.user.UserEntity;
import com.lsb.kkirikkiri.mappers.FileMapper;
import com.lsb.kkirikkiri.results.CommonResult;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.CSS;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileMapper fileMapper;

    public List<Map<String, CommonResult>> postFile(UserEntity userEntity,
                                                             ArticleEntity articleEntity,
                                                             List<MultipartFile> files) {
        List<Map<String, CommonResult>> fileResult = new ArrayList<>();
        if (files == null) {
            System.out.println("null");
            return null;
        }
        for (MultipartFile file : files) {
            if (file.getContentType() == null || !file.getContentType().startsWith("image")) {
                System.out.println("content type");
                return null;
            }
            if (file.isEmpty()) {
                System.out.println("empty");
                return null;
            }
            if (file.getOriginalFilename() == null) {
                System.out.println("original filename");
                return null;
            }
            // DB fileEntity insert + userEmail + articleId
            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String savedFileName = UUID.randomUUID().toString() + extension;
            long size = file.getSize();

            Path firstPath = Paths.get("C:/upload/image");
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
            Path basePath = firstPath.resolve(datePath);
            Path savedPath = basePath.resolve(savedFileName);
            if (!Files.exists(basePath)) {
                try {
                    Files.createDirectories(savedPath);
                    file.transferTo(basePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            FileEntity fileEntity = new FileEntity();
            fileEntity.setUserId(userEntity.getEmail());
            fileEntity.setArticleId(articleEntity.getId());
            fileEntity.setOriginalFilename(originalFileName);
            fileEntity.setSavedFilename(savedFileName);
            fileEntity.setSavedFilepath(savedPath.toString());
            fileEntity.setSize(size);
            Map<String, CommonResult> result = new HashMap<>();
            if (this.fileMapper.insert(fileEntity) > 0) {
                result.put(originalFileName, CommonResult.SUCCESS);
            } else {
                result.put(originalFileName, CommonResult.FAILURE);
            }
            fileResult.add(result);
        }
        return fileResult;
    }
}
