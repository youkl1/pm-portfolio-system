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
        
        // 存储路径
        String uploadDir = "/opt/service/pm-portfolio-system/uploads";
        java.io.File dir = new java.io.File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        // 保存文件
        java.io.File dest = new java.io.File(dir, fileName);
        file.transferTo(dest);
        
        // 返回真实的URL
        return "http://8.148.235.131:8080/uploads/" + fileName;
    }
}