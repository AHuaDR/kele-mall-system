package com.my.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@CrossOrigin // 允许跨域
public class UploadController {

    // 读取 yml 中的存储路径
    @Value("${file.upload-path}")
    private String uploadPath;

    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();

        try {
            if (file.isEmpty()) {
                throw new RuntimeException("文件不能为空");
            }

            // 1. 获取原文件名和后缀 (例如: logo.png -> .png)
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

            // 2. 生成唯一文件名 (UUID)，防止覆盖
            String newFileName = UUID.randomUUID().toString() + suffix;

            // 3. 确保目录存在
            File dir = new File(uploadPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 4. 保存文件到本地磁盘
            File dest = new File(uploadPath + newFileName);
            file.transferTo(dest);

            // 5. 返回访问 URL
            // 假设你的服务端口是 8080，我们映射一个虚拟路径 /files/
            String fileUrl = "http://localhost:8080/files/" + newFileName;

            // 构造前端需要的 JSON 格式
            result.put("code", 200);
            result.put("message", "上传成功");
            result.put("data", fileUrl); // 这里直接返回 URL 字符串

        } catch (IOException e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("message", "上传失败: " + e.getMessage());
        }

        return result;
    }
}
