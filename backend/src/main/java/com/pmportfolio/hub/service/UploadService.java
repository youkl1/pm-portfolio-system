package com.pmportfolio.hub.service;

import com.pmportfolio.hub.common.ThreadLocalUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class UploadService {
    // 检查是否为admin角色
    private void checkAdminPermission() {
        ThreadLocalUtil.UserInfo userInfo = ThreadLocalUtil.getUserInfo();
        if (!"admin".equals(userInfo.getRole())) {
            throw new RuntimeException("权限不足，无法执行此操作");
        }
    }
    
    // 上传封面图片
    public String uploadCover(MultipartFile file) throws IOException {
        checkAdminPermission();
        
        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
            throw new RuntimeException("请上传jpg或png格式的图片");
        }
        
        // 生成文件名
        String fileName = UUID.randomUUID().toString() + "-" + System.currentTimeMillis() + ".jpg";
        
        // 存储路径 - 使用绝对路径，确保在任何环境下都能正确创建
        String uploadDir = System.getProperty("user.dir") + "/uploads";
        java.io.File dir = new java.io.File(uploadDir);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new RuntimeException("创建上传目录失败");
            }
        }
        
        // 保存文件
        java.io.File dest = new java.io.File(dir, fileName);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException("保存文件失败: " + e.getMessage());
        }
        
        // 根据环境返回正确的URL
        String baseUrl = getBaseUrl();
        return baseUrl + "/uploads/" + fileName;
    }
    
    // 上传简历文件
    public String uploadResume(MultipartFile file) throws IOException {
        checkAdminPermission();
        
        // 检查文件类型
        String contentType = file.getContentType();
        String originalFileName = file.getOriginalFilename();
        
        // 基于文件名扩展名进行检查
        boolean isAllowed = false;
        if (originalFileName != null) {
            String lowerFileName = originalFileName.toLowerCase();
            isAllowed = lowerFileName.endsWith(".pdf") || 
                       lowerFileName.endsWith(".doc") || 
                       lowerFileName.endsWith(".docx");
        }
        
        // 同时检查contentType作为辅助
        if (contentType != null) {
            String lowerContentType = contentType.toLowerCase();
            isAllowed = isAllowed || 
                       lowerContentType.contains("pdf") || 
                       lowerContentType.contains("word") || 
                       lowerContentType.contains("msword") || 
                       lowerContentType.contains("openxmlformats");
        }
        
        if (!isAllowed) {
            throw new RuntimeException("请上传PDF或Word格式的文件");
        }
        
        // 生成文件名
        String extension = originalFileName != null ? originalFileName.substring(originalFileName.lastIndexOf(".")) : ".pdf";
        String fileName = UUID.randomUUID().toString() + "-" + System.currentTimeMillis() + extension;
        
        // 存储路径 - 使用绝对路径，确保在任何环境下都能正确创建
        String uploadDir = System.getProperty("user.dir") + "/uploads";
        java.io.File dir = new java.io.File(uploadDir);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new RuntimeException("创建上传目录失败");
            }
        }
        
        // 保存文件
        java.io.File dest = new java.io.File(dir, fileName);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException("保存文件失败: " + e.getMessage());
        }
        
        // 根据环境返回正确的URL
        String baseUrl = getBaseUrl();
        return baseUrl + "/uploads/" + fileName;
    }
    
    // 根据环境获取基础URL
    private String getBaseUrl() {
        // 优先检查环境变量
        String env = System.getenv("SPRING_PROFILES_ACTIVE");
        if ("prod".equals(env)) {
            return "http://8.148.235.131:8080";
        }
        
        // 检查系统属性
        String systemEnv = System.getProperty("spring.profiles.active");
        if ("prod".equals(systemEnv)) {
            return "http://8.148.235.131:8080";
        }
        
        // 默认返回生产环境地址，确保线上部署时始终使用正确地址
        return "http://8.148.235.131:8080";
    }
}