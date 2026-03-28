package com.pmportfolio.hub.controller;

import com.pmportfolio.hub.common.PermissionCheck;
import com.pmportfolio.hub.model.Template;
import com.pmportfolio.hub.model.TemplateCategory;
import com.pmportfolio.hub.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            System.out.println("重新加载模板，分类数量：" + (fullTemplate.getCategories() != null ? fullTemplate.getCategories().size() : 0));
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

        byte[] fileBytes = templateService.downloadTemplate(id);
        Template template = templateService.getTemplateById(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", template.getName() + "." + template.getFormat());

        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<?> viewTemplate(@PathVariable Long id) {

        Map<String, Object> result = templateService.viewTemplate(id);

        return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "成功",
                "data", result
        ));
    }

    @GetMapping("/preview/{id}")
    public ResponseEntity<?> previewTemplate(@PathVariable Long id) {

        byte[] fileBytes = templateService.downloadTemplate(id);
        Template template = templateService.getTemplateById(id);

        // 根据文件格式设置不同的Content-Type
        MediaType mediaType;
        String format = template.getFormat().toLowerCase();
        switch (format) {
            case "jpg":
            case "jpeg":
                mediaType = MediaType.IMAGE_JPEG;
                break;
            case "png":
                mediaType = MediaType.IMAGE_PNG;
                break;
            case "gif":
                mediaType = MediaType.IMAGE_GIF;
                break;
            case "webp":
                mediaType = MediaType.parseMediaType("image/webp");
                break;
            case "bmp":
                mediaType = MediaType.parseMediaType("image/bmp");
                break;
            default:
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);

        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }

    @PutMapping("/edit/{id}")
    @PermissionCheck("TEMPLATE_EDIT")
    public ResponseEntity<?> editTemplate(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request,
            @RequestAttribute("userId") Long userId) {

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