package com.pmportfolio.hub.controller;

import com.pmportfolio.hub.service.UploadService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
public class UploadController {
    @Autowired
    private UploadService uploadService;
    
    // 上传封面图片
    @PostMapping("/cover")
    public Map<String, Object> uploadCover(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        try {
            String url = uploadService.uploadCover(file);
            Map<String, String> data = new HashMap<>();
            data.put("url", url);
            response.put("code", 200);
            response.put("message", "上传成功");
            response.put("data", data);
        } catch (IOException e) {
            response.put("code", 500);
            response.put("message", "上传失败，请重试");
            response.put("data", null);
        } catch (Exception e) {
            response.put("code", 400);
            response.put("message", e.getMessage());
            response.put("data", null);
        }
        return response;
    }
}