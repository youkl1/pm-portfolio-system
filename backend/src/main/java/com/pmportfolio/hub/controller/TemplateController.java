package com.pmportfolio.hub.controller;

import com.pmportfolio.hub.common.PermissionCheck;
import com.pmportfolio.hub.model.Template;
import com.pmportfolio.hub.model.TemplateCategory;
import com.pmportfolio.hub.service.TemplateService;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/template")
public class TemplateController {

    @Autowired
    private TemplateService templateService;

    // 模板管理接口

    @GetMapping("/list")
    public ResponseEntity<?> getTemplateList(
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        List<Template> templates = templateService.getTemplateList(categoryId, keyword, page, size);
        long total = templateService.getTemplateCount(categoryId, keyword);

        Map<String, Object> result = Map.of(
                "total", total,
                "items", templates
        );

        return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "成功",
                "data", result
        ));
    }

    @PostMapping("/import")
    @PermissionCheck("TEMPLATE_IMPORT")
    public ResponseEntity<?> importTemplate(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds,
            @RequestAttribute("userId") Long userId) {
        System.out.println("开始导入模板：");
        System.out.println("文件名：" + file.getOriginalFilename());
        System.out.println("文件大小：" + file.getSize());
        System.out.println("模板名称：" + name);
        System.out.println("模板描述：" + description);
        System.out.println("分类ID：" + (categoryIds != null ? categoryIds : "无"));
        System.out.println("用户ID：" + userId);
        
        try {
            Template template = templateService.importTemplate(file, name, description, categoryIds, userId);
            System.out.println("导入成功，模板ID：" + template.getId());
            // 重新加载模板，确保包含分类信息
            Template fullTemplate = templateService.getTemplateById(template.getId());
            System.out.println("重新加载模板，分类数量：" + (fullTemplate != null && fullTemplate.getCategories() != null ? fullTemplate.getCategories().size() : 0));
            if (fullTemplate == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                        "code", 500,
                        "message", "模板导入成功，但无法加载模板信息",
                        "data", template
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "code", 200,
                    "message", "导入成功",
                    "data", fullTemplate
            ));
        } catch (Exception e) {
            System.err.println("导入模板失败：" + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "导入失败：" + e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.ok(errorResponse);
        }
    }

    @GetMapping("/download/{id}")
    @PermissionCheck("TEMPLATE_DOWNLOAD")
    public ResponseEntity<?> downloadTemplate(@PathVariable Long id) {
        // 参数校验
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body(Map.of(
                    "code", 400,
                    "message", "模板ID无效",
                    "data", null
            ));
        }

        byte[] fileBytes = templateService.downloadTemplate(id);
        Template template = templateService.getTemplateById(id);
        if (template == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "code", 404,
                    "message", "模板不存在",
                    "data", null
            ));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", template.getName() + "." + template.getFormat());

        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<?> viewTemplate(@PathVariable Long id) {
        // 参数校验
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body(Map.of(
                    "code", 400,
                    "message", "模板ID无效",
                    "data", null
            ));
        }

        Map<String, Object> result = templateService.viewTemplate(id);

        return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "成功",
                "data", result
        ));
    }

    @GetMapping("/preview/{id}")
    public ResponseEntity<?> previewTemplate(@PathVariable Long id) {
        try {
            // 参数校验
            if (id == null || id <= 0) {
                System.out.println("模板ID无效: " + id);
                return ResponseEntity.badRequest().body(Map.of(
                        "code", 400,
                        "message", "模板ID无效",
                        "data", null
                ));
            }
            
            // 1. 查询模板
            System.out.println("开始预览模板，ID: " + id);
            Template template = templateService.getTemplateById(id);
            if (template == null) {
                System.out.println("模板不存在，ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "code", 404,
                        "message", "模板不存在",
                        "data", null
                ));
            }
            String filePath = template.getFilePath();
            System.out.println("模板文件路径: " + filePath);
            File file = new File(filePath);
            
            if (!file.exists()) {
                System.out.println("文件不存在: " + filePath);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "code", 404,
                        "message", "文件不存在",
                        "data", null
                ));
            }
            System.out.println("文件存在，大小: " + file.length() + " 字节");

            String format = template.getFormat().toLowerCase();
            System.out.println("预览文件格式: " + format);
            
            // 1. PDF 文件：直接输出二进制流
            if ("pdf".equals(format)) {
                System.out.println("处理PDF文件: " + filePath);
                byte[] fileBytes = templateService.downloadTemplate(id);
                System.out.println("PDF文件大小: " + (fileBytes != null ? fileBytes.length : 0) + " 字节");
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("inline", URLEncoder.encode(template.getName(), "UTF-8") + ".pdf");
                headers.setContentLength(fileBytes.length);
                // 添加缓存控制头
                headers.setCacheControl("no-cache, no-store, must-revalidate");
                headers.setPragma("no-cache");
                headers.setExpires(0);
                System.out.println("返回PDF文件，长度: " + fileBytes.length + " 字节");
                return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
            }
            
            // 2. 图片文件：直接输出二进制流
            else if (format.matches("(jpg|jpeg|png|gif|bmp|webp)")) {
                System.out.println("处理图片文件: " + filePath);
                byte[] fileBytes = templateService.downloadTemplate(id);
                System.out.println("图片文件大小: " + (fileBytes != null ? fileBytes.length : 0) + " 字节");
                HttpHeaders headers = new HttpHeaders();
                // 根据文件格式设置对应的Content-Type
                if ("jpg".equals(format) || "jpeg".equals(format)) {
                    headers.setContentType(MediaType.IMAGE_JPEG);
                } else if ("png".equals(format)) {
                    headers.setContentType(MediaType.IMAGE_PNG);
                } else if ("gif".equals(format)) {
                    headers.setContentType(MediaType.IMAGE_GIF);
                } else {
                    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                }
                headers.setContentDispositionFormData("inline", URLEncoder.encode(template.getName(), "UTF-8") + "." + format);
                headers.setContentLength(fileBytes.length);
                System.out.println("返回图片文件，长度: " + fileBytes.length + " 字节");
                return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
            }
            
            // 3. Word 文件：解析并返回HTML内容
            else if ("docx".equals(format) || "doc".equals(format)) {
                System.out.println("处理Word文件: " + filePath);
                try {
                    // 解析DOCX文件
                    String htmlContent = templateService.parseDocxFile(file);
                    System.out.println("Word文件解析成功，HTML长度: " + (htmlContent != null ? htmlContent.length() : 0) + " 字符");
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(new MediaType("text", "html", java.nio.charset.StandardCharsets.UTF_8));
                    return new ResponseEntity<>(htmlContent, headers, HttpStatus.OK);
                } catch (ClassNotFoundException e) {
                    // 缺少Apache POI依赖，返回基本信息
                    System.err.println("缺少Apache POI依赖: " + e.getMessage());
                    Map<String, Object> result = templateService.viewTemplate(id);
                    return ResponseEntity.ok(Map.of(
                            "code", 200,
                            "message", "成功",
                            "data", result
                    ));
                }
            }
            
            // 4. Excel 文件：解析并返回HTML内容
            else if ("xlsx".equals(format) || "xls".equals(format)) {
                System.out.println("处理Excel文件: " + filePath);
                try {
                    // 解析Excel文件
                    String htmlContent = templateService.parseExcelFile(file);
                    System.out.println("Excel文件解析成功，HTML长度: " + (htmlContent != null ? htmlContent.length() : 0) + " 字符");
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(new MediaType("text", "html", java.nio.charset.StandardCharsets.UTF_8));
                    return new ResponseEntity<>(htmlContent, headers, HttpStatus.OK);
                } catch (ClassNotFoundException e) {
                    // 缺少Apache POI依赖，返回基本信息
                    System.err.println("缺少Apache POI依赖: " + e.getMessage());
                    Map<String, Object> result = templateService.viewTemplate(id);
                    return ResponseEntity.ok(Map.of(
                            "code", 200,
                            "message", "成功",
                            "data", result
                    ));
                }
            }

            // 4. 其他文件：使用viewTemplate方法的逻辑
            System.out.println("处理其他格式文件: " + format);
            Map<String, Object> result = templateService.viewTemplate(id);
            return ResponseEntity.ok(Map.of(
                    "code", 200,
                    "message", "成功",
                    "data", result
            ));

        } catch (Exception e) {
            System.err.println("预览失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "code", 500,
                    "message", "预览失败：" + e.getMessage(),
                    "data", null
            ));
        }
    }

    @PutMapping("/edit/{id}")
    @PermissionCheck("TEMPLATE_EDIT")
    public ResponseEntity<?> editTemplate(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request,
            @RequestAttribute("userId") Long userId) {
        // 参数校验
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body(Map.of(
                    "code", 400,
                    "message", "模板ID无效",
                    "data", null
            ));
        }

        String content = (String) request.get("content");
        String name = (String) request.get("name");
        String description = (String) request.get("description");
        List<?> categoryIdObjects = (List<?>) request.get("categoryIds");
        List<Long> categoryIds = convertToLongList(categoryIdObjects);

        Template template = templateService.updateTemplate(id, content, name, description, categoryIds, userId);

        return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "编辑成功",
                "data", template
        ));
    }

    /**
     * 将对象列表转换为Long列表，处理Integer和String类型
     */
    private List<Long> convertToLongList(List<?> objects) {
        if (objects == null) {
            return null;
        }
        return objects.stream()
                .map(obj -> {
                    if (obj instanceof Integer) {
                        return ((Integer) obj).longValue();
                    } else if (obj instanceof Long) {
                        return (Long) obj;
                    } else if (obj instanceof String) {
                        return Long.parseLong((String) obj);
                    }
                    return null;
                })
                .filter(obj -> obj != null)
                .toList();
    }

    @DeleteMapping("/delete/{id}")
    @PermissionCheck("TEMPLATE_EDIT")
    public ResponseEntity<?> deleteTemplate(@PathVariable Long id) {
        // 参数校验
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body(Map.of(
                    "code", 400,
                    "message", "模板ID无效",
                    "data", null
            ));
        }
        
        System.out.println("开始删除模板，ID: " + id);
        
        try {
            templateService.deleteTemplate(id);
            System.out.println("删除模板成功，ID: " + id);
            return ResponseEntity.ok(Map.of(
                    "code", 200,
                    "message", "删除成功",
                    "data", null
            ));
        } catch (Exception e) {
            System.err.println("删除模板失败，ID: " + id + "，错误: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "删除失败：" + e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.ok(errorResponse);
        }
    }
    
    @PostMapping("/update-attachment")
    @PermissionCheck("TEMPLATE_EDIT")
    public ResponseEntity<?> updateAttachment(
            @RequestParam("file") MultipartFile file,
            @RequestParam("templateId") Long templateId,
            @RequestAttribute("userId") Long userId) {
        System.out.println("开始更新附件：");
        System.out.println("模板ID：" + templateId);
        System.out.println("文件名：" + file.getOriginalFilename());
        System.out.println("文件大小：" + file.getSize());
        System.out.println("用户ID：" + userId);
        
        try {
            Template template = templateService.updateAttachment(file, templateId, userId);
            System.out.println("更新附件成功，模板ID：" + template.getId());
            return ResponseEntity.ok(Map.of(
                    "code", 200,
                    "message", "更新成功",
                    "data", template
            ));
        } catch (Exception e) {
            System.err.println("更新附件失败：" + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "更新失败：" + e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.ok(errorResponse);
        }
    }

    // 分类管理接口

    @GetMapping("/category/list")
    public ResponseEntity<?> getCategoryList() {

        List<Map<String, Object>> categories = templateService.getCategoryList();

        return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "成功",
                "data", categories
        ));
    }

    @PostMapping("/category/create")
    @PermissionCheck("TEMPLATE_CATEGORY_MANAGE")
    public ResponseEntity<?> createCategory(
            @RequestBody Map<String, String> request,
            @RequestAttribute("userId") Long userId) {

        String name = request.get("name");
        String description = request.get("description");

        TemplateCategory category = templateService.createCategory(name, description, userId);

        return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "创建成功",
                "data", category
        ));
    }

    @PutMapping("/category/edit/{id}")
    @PermissionCheck("TEMPLATE_CATEGORY_MANAGE")
    public ResponseEntity<?> updateCategory(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            @RequestAttribute("userId") Long userId) {

        String name = request.get("name");
        String description = request.get("description");

        TemplateCategory category = templateService.updateCategory(id, name, description, userId);

        return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "编辑成功",
                "data", category
        ));
    }

    @DeleteMapping("/category/delete/{id}")
    @PermissionCheck("TEMPLATE_CATEGORY_MANAGE")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {

        templateService.deleteCategory(id);

        return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "删除成功",
                "data", null
        ));
    }

    // 版本管理接口

    @GetMapping("/versions/{templateId}")
    public ResponseEntity<?> getTemplateVersions(@PathVariable Long templateId) {

        List<?> versions = templateService.getTemplateVersions(templateId);

        return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "成功",
                "data", versions
        ));
    }
}