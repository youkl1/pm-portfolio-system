package com.pmportfolio.hub.service;

import com.pmportfolio.hub.model.Template;
import com.pmportfolio.hub.model.TemplateCategory;
import com.pmportfolio.hub.model.TemplateVersion;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface TemplateService {
    // 模板管理
    Template importTemplate(MultipartFile file, String name, String description, List<Long> categoryIds, Long userId);
    Template getTemplateById(Long id);
    List<Template> getTemplateList(Long categoryId, String keyword, int page, int size);
    long getTemplateCount(Long categoryId, String keyword);
    Template updateTemplate(Long id, String content, String name, String description, List<Long> categoryIds, Long userId);
    void deleteTemplate(Long id);
    byte[] downloadTemplate(Long id);
    Map<String, Object> viewTemplate(Long id);
    
    // 分类管理
    TemplateCategory createCategory(String name, String description, Long userId);
    TemplateCategory updateCategory(Long id, String name, String description, Long userId);
    void deleteCategory(Long id);
    List<Map<String, Object>> getCategoryList();
    
    // 版本管理
    List<TemplateVersion> getTemplateVersions(Long templateId);
}