package com.agricultural.products.controller;

import com.agricultural.products.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/api/upload")
public class UploadController {
    
    @Value("${file.upload-path}")
    private String uploadPath;
    
    @PostMapping
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择文件");
        }
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID().toString().replace("-", "") + extension;
            String datePath = java.time.LocalDate.now().toString().replace("-", "/");
            File dir = new File(uploadPath + "upload/" + datePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File destFile = new File(dir, newFilename);
            file.transferTo(destFile);
            Map<String, String> data = new HashMap<>();
            data.put("url", "/upload/" + datePath + "/" + newFilename);
            data.put("filename", newFilename);
            return Result.success("上传成功", data);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}
