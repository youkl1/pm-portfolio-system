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
    
    // 从URL上传封面图片
    public String uploadCoverFromUrl(String imageUrl) throws IOException {
        checkAdminPermission();
        
        // 从URL下载图片
        java.io.InputStream inputStream = null;
        java.io.File tempFile = null;
        try {
            // 创建URL对象
            java.net.URL url = new java.net.URL(imageUrl);
            // 打开连接
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000); // 10秒超时
            connection.setReadTimeout(10000);
            
            // 添加浏览器模拟头信息
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
            connection.setRequestProperty("Connection", "keep-alive");
            
            // 检查响应状态
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                // 如果403错误，尝试使用原始链接
                if (responseCode == 403) {
                    return imageUrl;
                }
                throw new RuntimeException("下载图片失败，HTTP状态码: " + responseCode);
            }
            
            // 获取输入流
            inputStream = connection.getInputStream();
            
            // 创建临时文件
            tempFile = java.io.File.createTempFile("temp", ".jpg");
            
            // 保存到临时文件
            try (java.io.FileOutputStream outputStream = new java.io.FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
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
            java.nio.file.Files.copy(tempFile.toPath(), dest.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            
            // 根据环境返回正确的URL
            String baseUrl = getBaseUrl();
            return baseUrl + "/uploads/" + fileName;
        } catch (java.net.MalformedURLException e) {
            throw new RuntimeException("无效的图片URL: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException("下载或保存图片失败: " + e.getMessage());
        } finally {
            // 关闭资源
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // 忽略关闭异常
                }
            }
            // 删除临时文件
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
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