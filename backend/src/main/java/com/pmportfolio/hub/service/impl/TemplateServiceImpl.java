package com.pmportfolio.hub.service.impl;

import com.pmportfolio.hub.model.Template;
import com.pmportfolio.hub.model.TemplateCategory;
import com.pmportfolio.hub.model.TemplateVersion;
import com.pmportfolio.hub.repository.TemplateCategoryRepository;
import com.pmportfolio.hub.repository.TemplateRepository;
import com.pmportfolio.hub.repository.TemplateVersionRepository;
import com.pmportfolio.hub.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TemplateServiceImpl implements TemplateService {

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private TemplateCategoryRepository templateCategoryRepository;

    @Autowired
    private TemplateVersionRepository templateVersionRepository;

    private final String STORAGE_PATH = "d:/temp/templates/";

    @Override
    @org.springframework.transaction.annotation.Transactional
    public Template importTemplate(MultipartFile file, String name, String description, List<Long> categoryIds, Long userId) {
        System.out.println("TemplateServiceImpl.importTemplate 开始");
        try {
            // 创建存储目录
            System.out.println("检查存储目录: " + STORAGE_PATH);
            File storageDir = new File(STORAGE_PATH);
            if (!storageDir.exists()) {
                System.out.println("创建存储目录");
                storageDir.mkdirs();
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            System.out.println("原始文件名: " + originalFilename);
            String format = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
            System.out.println("文件格式: " + format);
            String fileName = UUID.randomUUID().toString() + "." + format;
            String filePath = STORAGE_PATH + fileName;
            System.out.println("保存路径: " + filePath);

            // 保存文件
            System.out.println("开始保存文件");
            file.transferTo(new File(filePath));
            System.out.println("文件保存成功");

            // 计算文件哈希值
            System.out.println("计算文件哈希值");
            String contentHash = calculateFileHash(new File(filePath));
            System.out.println("哈希值: " + contentHash);

            // 创建模板实体
            System.out.println("创建模板实体");
            Template template = new Template();
            template.setName(name);
            template.setDescription(description);
            template.setFilePath(filePath);
            template.setFormat(format);
            template.setSize(file.getSize());
            template.setContentHash(contentHash);
            template.setIsEncrypted(true);
            // 这里需要根据userId获取SysUser对象，暂时设置为null
            template.setCreatedBy(null);
            template.setUpdatedBy(null);
            // createdAt和updatedAt会通过@PrePersist自动设置

            // 保存模板
            System.out.println("保存模板到数据库");
            template = templateRepository.save(template);
            System.out.println("模板保存成功，ID: " + template.getId());

            // 关联分类
            if (categoryIds != null && !categoryIds.isEmpty()) {
                System.out.println("关联分类，分类ID: " + categoryIds);
                List<TemplateCategory> categories = templateCategoryRepository.findAllById(categoryIds);
                System.out.println("找到分类数量: " + categories.size());
                template.setCategories(categories);
                templateRepository.save(template);
                System.out.println("分类关联成功");
            } else {
                System.out.println("无分类关联");
            }

            // 创建初始版本
            System.out.println("创建初始版本");
            createTemplateVersion(template, "初始版本", userId);
            System.out.println("初始版本创建成功");

            System.out.println("TemplateServiceImpl.importTemplate 完成");
            return template;
        } catch (Exception e) {
            System.err.println("TemplateServiceImpl.importTemplate 失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("导入模板失败: " + e.getMessage());
        }
    }

    @Override
    public Template getTemplateById(Long id) {
        return templateRepository.findById(id).orElse(null);
    }

    @Override
    public List<Template> getTemplateList(Long categoryId, String keyword, int page, int size) {
        int offset = (page - 1) * size;
        List<Template> templates;
        
        // 这里需要根据categoryId和keyword的不同组合调用不同的方法
        if (categoryId != null && keyword != null) {
            templates = templateRepository.findByCategoryAndKeyword(categoryId, keyword);
        } else if (categoryId != null) {
            templates = templateRepository.findByCategoryId(categoryId);
        } else if (keyword != null) {
            templates = templateRepository.findByKeyword(keyword);
        } else {
            templates = templateRepository.findAllWithCategories();
        }
        
        // 内存中进行分页
        if (templates.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        int end = Math.min(offset + size, templates.size());
        if (offset >= templates.size()) {
            return new java.util.ArrayList<>();
        }
        return templates.subList(offset, end);
    }

    @Override
    public long getTemplateCount(Long categoryId, String keyword) {
        // 这里需要根据categoryId和keyword的不同组合调用不同的方法
        if (categoryId != null && keyword != null) {
            return templateRepository.countByCategoryAndKeyword(categoryId, keyword);
        } else if (categoryId != null) {
            return templateRepository.findByCategoryId(categoryId).size();
        } else if (keyword != null) {
            return templateRepository.findByKeyword(keyword).size();
        } else {
            return templateRepository.count();
        }
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public Template updateTemplate(Long id, String content, String name, String description, List<Long> categoryIds, Long userId) {
        Template template = templateRepository.findById(id).orElseThrow(() -> new RuntimeException("模板不存在"));

        // 创建版本记录
        createTemplateVersion(template, "编辑更新", userId);

        // 更新模板信息
        template.setName(name);
        template.setDescription(description);
        // 这里需要根据userId获取SysUser对象，暂时设置为null
        template.setUpdatedBy(null);
        // updatedAt会通过@PreUpdate自动设置

        // 更新分类关联
        if (categoryIds != null) {
            List<TemplateCategory> categories = templateCategoryRepository.findAllById(categoryIds);
            template.setCategories(categories);
        }

        return templateRepository.save(template);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void deleteTemplate(Long id) {
        System.out.println("TemplateServiceImpl.deleteTemplate 开始，ID: " + id);
        
        try {
            Template template = templateRepository.findById(id).orElseThrow(() -> new RuntimeException("模板不存在"));
            System.out.println("找到模板: " + template.getName());

            // 删除文件
            if (template.getFilePath() != null) {
                File file = new File(template.getFilePath());
                if (file.exists()) {
                    boolean deleted = file.delete();
                    System.out.println("文件删除结果: " + deleted);
                } else {
                    System.out.println("文件不存在，跳过删除: " + template.getFilePath());
                }
            }

            // 删除版本记录
            System.out.println("删除版本记录");
            templateVersionRepository.deleteByTemplateId(id);

            // 删除模板
            System.out.println("删除模板");
            templateRepository.delete(template);
            System.out.println("TemplateServiceImpl.deleteTemplate 完成");
        } catch (Exception e) {
            System.err.println("TemplateServiceImpl.deleteTemplate 失败: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public byte[] downloadTemplate(Long id) {
        Template template = templateRepository.findById(id).orElseThrow(() -> new RuntimeException("模板不存在"));

        try {
            File file = new File(template.getFilePath());
            if (!file.exists()) {
                throw new RuntimeException("文件不存在");
            }

            byte[] bytes = new byte[(int) file.length()];
            try (FileInputStream fis = new FileInputStream(file)) {
                fis.read(bytes);
            }

            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("下载模板失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> viewTemplate(Long id) {
        Template template = templateRepository.findById(id).orElseThrow(() -> new RuntimeException("模板不存在"));

        Map<String, Object> result = new HashMap<>();
        result.put("id", template.getId());
        result.put("name", template.getName());
        result.put("format", template.getFormat());
        
        // 尝试读取模板文件内容
        String content = "<html><body>模板内容预览</body></html>";
        try {
            if (template.getFilePath() != null) {
                File file = new File(template.getFilePath());
                if (file.exists()) {
                    String format = template.getFormat().toLowerCase();
                    if ("docx".equals(format)) {
                        // 处理DOCX文件，添加fallback机制
                        try {
                            System.out.println("开始解析DOCX文件: " + file.getAbsolutePath());
                            content = parseDocxFile(file);
                            System.out.println("DOCX文件解析完成，内容长度: " + content.length());
                        } catch (ClassNotFoundException e) {
                            // Apache POI依赖不可用，使用默认预览
                            System.err.println("Apache POI依赖不可用，使用默认预览: " + e.getMessage());
                            content = "<html><body><p>DOCX文件预览需要Apache POI依赖</p></body></html>";
                        } catch (Exception e) {
                            System.err.println("解析DOCX文件失败: " + e.getMessage());
                            e.printStackTrace();
                            content = "<html><body><p>解析DOCX文件失败: " + e.getMessage() + "</p></body></html>";
                        }
                    } else {
                        // 处理其他文本文件
                        StringBuilder sb = new StringBuilder();
                        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
                            String line;
                            while ((line = br.readLine()) != null) {
                                sb.append(line).append("\n");
                            }
                        }
                        content = "<html><body><pre>" + sb.toString() + "</pre></body></html>";
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("读取模板文件失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        result.put("content", content);
        return result;
    }
    
    /**
     * 解析DOCX文件，提取文本内容和图片
     */
    private String parseDocxFile(File file) throws Exception {
        // 尝试加载Apache POI类
        try {
            Class.forName("org.apache.poi.xwpf.usermodel.XWPFDocument");
        } catch (ClassNotFoundException e) {
            throw e;
        }
        
        StringBuilder content = new StringBuilder();
        content.append("<html><body>");
        int imageCount = 0;
        
        try (org.apache.poi.xwpf.usermodel.XWPFDocument document = new org.apache.poi.xwpf.usermodel.XWPFDocument(new FileInputStream(file))) {
            // 提取段落文本和图片
            for (org.apache.poi.xwpf.usermodel.XWPFParagraph paragraph : document.getParagraphs()) {
                // 处理段落中的文本和图片
                StringBuilder paraContent = new StringBuilder();
                boolean hasContent = false;
                
                for (org.apache.poi.xwpf.usermodel.XWPFRun run : paragraph.getRuns()) {
                    // 处理文本
                    String text = run.getText(0);
                    if (text != null && !text.isEmpty()) {
                        paraContent.append(text);
                        hasContent = true;
                    }
                    
                    // 处理图片
                    if (run.getEmbeddedPictures() != null && !run.getEmbeddedPictures().isEmpty()) {
                        System.out.println("发现图片数量: " + run.getEmbeddedPictures().size());
                        for (org.apache.poi.xwpf.usermodel.XWPFPicture picture : run.getEmbeddedPictures()) {
                            org.apache.poi.xwpf.usermodel.XWPFPictureData pictureData = picture.getPictureData();
                            if (pictureData != null) {
                                imageCount++;
                                System.out.println("处理第 " + imageCount + " 张图片");
                                byte[] imageBytes = pictureData.getData();
                                System.out.println("图片大小: " + imageBytes.length + " 字节");
                                String base64Image = java.util.Base64.getEncoder().encodeToString(imageBytes);
                                System.out.println("Base64编码后长度: " + base64Image.length());
                                // 获取图片内容类型
                                String contentType = "image/jpeg"; // 默认类型
                                try {
                                    // 尝试通过packagePart获取内容类型
                                    if (pictureData.getPackagePart() != null) {
                                        contentType = pictureData.getPackagePart().getContentType();
                                        System.out.println("图片内容类型: " + contentType);
                                    }
                                } catch (Exception e) {
                                    // 如果获取失败，使用默认类型
                                    System.err.println("获取图片内容类型失败: " + e.getMessage());
                                }
                                
                                // 如果有之前的文本，先输出文本
                                if (paraContent.length() > 0) {
                                    content.append("<p>").append(paraContent.toString()).append("</p>");
                                    paraContent.setLength(0);
                                }
                                
                                content.append("<div style='margin: 10px 0; text-align: center;'>");
                                content.append("<img src='data:").append(contentType).append(";base64,").append(base64Image).append("' alt='图片' style='max-width: 100%; height: auto;' />");
                                content.append("</div>");
                            }
                        }
                    }
                }
                
                // 输出剩余的文本
                if (paraContent.length() > 0) {
                    content.append("<p>").append(paraContent.toString()).append("</p>");
                }
            }
            
            // 提取表格内容
            for (org.apache.poi.xwpf.usermodel.XWPFTable table : document.getTables()) {
                content.append("<table border='1' style='border-collapse: collapse; margin: 10px 0;'>");
                for (org.apache.poi.xwpf.usermodel.XWPFTableRow row : table.getRows()) {
                    content.append("<tr>");
                    for (org.apache.poi.xwpf.usermodel.XWPFTableCell cell : row.getTableCells()) {
                        content.append("<td style='padding: 5px;'>");
                        // 处理表格单元格中的文本和图片
                        for (org.apache.poi.xwpf.usermodel.XWPFParagraph cellPara : cell.getParagraphs()) {
                            StringBuilder cellContent = new StringBuilder();
                            
                            for (org.apache.poi.xwpf.usermodel.XWPFRun run : cellPara.getRuns()) {
                                // 处理文本
                                String text = run.getText(0);
                                if (text != null && !text.isEmpty()) {
                                    cellContent.append(text);
                                }
                                
                                // 处理图片
                                if (run.getEmbeddedPictures() != null && !run.getEmbeddedPictures().isEmpty()) {
                                    System.out.println("表格中发现图片数量: " + run.getEmbeddedPictures().size());
                                    for (org.apache.poi.xwpf.usermodel.XWPFPicture picture : run.getEmbeddedPictures()) {
                                        org.apache.poi.xwpf.usermodel.XWPFPictureData pictureData = picture.getPictureData();
                                        if (pictureData != null) {
                                            imageCount++;
                                            System.out.println("处理表格中第 " + imageCount + " 张图片");
                                            byte[] imageBytes = pictureData.getData();
                                            System.out.println("图片大小: " + imageBytes.length + " 字节");
                                            String base64Image = java.util.Base64.getEncoder().encodeToString(imageBytes);
                                            System.out.println("Base64编码后长度: " + base64Image.length());
                                            // 获取图片内容类型
                                            String contentType = "image/jpeg"; // 默认类型
                                            try {
                                                // 尝试通过packagePart获取内容类型
                                                if (pictureData.getPackagePart() != null) {
                                                    contentType = pictureData.getPackagePart().getContentType();
                                                    System.out.println("图片内容类型: " + contentType);
                                                }
                                            } catch (Exception e) {
                                                // 如果获取失败，使用默认类型
                                                System.err.println("获取图片内容类型失败: " + e.getMessage());
                                            }
                                            
                                            // 如果有之前的文本，先输出文本
                                            if (cellContent.length() > 0) {
                                                content.append(cellContent.toString());
                                                cellContent.setLength(0);
                                            }
                                            
                                            content.append("<div style='margin: 5px 0; text-align: center;'>");
                                            content.append("<img src='data:").append(contentType).append(";base64,").append(base64Image).append("' alt='图片' style='max-width: 100%; height: auto;' />");
                                            content.append("</div>");
                                        }
                                    }
                                }
                            }
                            
                            // 输出剩余的文本
                            if (cellContent.length() > 0) {
                                content.append(cellContent.toString());
                            }
                        }
                        content.append("</td>");
                    }
                    content.append("</tr>");
                }
                content.append("</table><br/>");
            }
        }
        
        content.append("</body></html>");
        System.out.println("总共处理了 " + imageCount + " 张图片");
        return content.toString();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public TemplateCategory createCategory(String name, String description, Long userId) {
        TemplateCategory category = new TemplateCategory();
        category.setName(name);
        category.setDescription(description);
        // 这里需要根据userId获取SysUser对象，暂时设置为null
        category.setCreatedBy(null);
        category.setUpdatedBy(null);
        // createdAt和updatedAt会通过@PrePersist自动设置

        return templateCategoryRepository.save(category);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public TemplateCategory updateCategory(Long id, String name, String description, Long userId) {
        TemplateCategory category = templateCategoryRepository.findById(id).orElseThrow(() -> new RuntimeException("分类不存在"));

        category.setName(name);
        category.setDescription(description);
        // 这里需要根据userId获取SysUser对象，暂时设置为null
        category.setUpdatedBy(null);
        // updatedAt会通过@PreUpdate自动设置

        return templateCategoryRepository.save(category);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void deleteCategory(Long id) {
        TemplateCategory category = templateCategoryRepository.findById(id).orElseThrow(() -> new RuntimeException("分类不存在"));
        templateCategoryRepository.delete(category);
    }

    @Override
    public List<Map<String, Object>> getCategoryList() {
        List<TemplateCategory> categories = templateCategoryRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (TemplateCategory category : categories) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", category.getId());
            map.put("name", category.getName());
            map.put("description", category.getDescription());
            map.put("templateCount", 0);
            result.add(map);
        }
        return result;
    }

    @Override
    public List<TemplateVersion> getTemplateVersions(Long templateId) {
        return templateVersionRepository.findByTemplateIdOrderByCreatedAtDesc(templateId);
    }

    private void createTemplateVersion(Template template, String description, Long userId) {
        TemplateVersion version = new TemplateVersion();
        version.setTemplate(template);
        version.setVersion(UUID.randomUUID().toString().substring(0, 8));
        version.setFilePath(template.getFilePath());
        version.setDescription(description);
        // 这里需要根据userId获取SysUser对象，暂时设置为null
        version.setCreatedBy(null);
        // createdAt会通过@PrePersist自动设置

        templateVersionRepository.save(version);
    }

    private String calculateFileHash(File file) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }
        }
        byte[] hashBytes = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}