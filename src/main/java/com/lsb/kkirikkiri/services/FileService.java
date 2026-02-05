package com.lsb.kkirikkiri.services;

import com.lsb.kkirikkiri.results.CommonResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class FileService {
    public CommonResult postFile(List<MultipartFile> files) {
        if (files == null) {
            System.out.println("null");
            return CommonResult.FAILURE;
        }
        for (MultipartFile file : files) {
            if (file.getContentType() == null || !file.getContentType().startsWith("image")) {
                System.out.println("content type");
                return CommonResult.FAILURE;
            }
            if (file.isEmpty()) {
                System.out.println("empty");
                return CommonResult.FAILURE;
            }
            if (file.getOriginalFilename() == null) {
                System.out.println("original filename");
                return CommonResult.FAILURE;
            }
            // DB fileEntity insert + userEmail + articleId
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            String fileName = UUID.randomUUID().toString() + "." + extension;
            long size = file.getSize();
            System.out.println(fileName);

        }
        return CommonResult.SUCCESS;
    }
}
