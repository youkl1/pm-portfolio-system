package com.pmportfolio.hub.service;

import com.pmportfolio.hub.common.ThreadLocalUtil;
import com.pmportfolio.hub.model.PmProject;
import com.pmportfolio.hub.model.SysCategory;
import com.pmportfolio.hub.repository.CategoryRepository;
import com.pmportfolio.hub.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    // 检查是否为admin角色
    private void checkAdminPermission() {
        ThreadLocalUtil.UserInfo userInfo = ThreadLocalUtil.getUserInfo();
        if (!"admin".equals(userInfo.getRole())) {
            throw new RuntimeException("权限不足，无法执行此操作");
        }
    }
    
    // 获取作品列表（按排序字段排序，支持搜索和分类筛选）
    public List<PmProject> getProjects(String keyword, Long categoryId) {
        if (categoryId != null) {
            if (keyword != null && !keyword.isEmpty()) {
                return projectRepository.findByCategoryIdAndTitleContainingOrCategoryIdAndDescriptionContainingOrderBySortAsc(categoryId, keyword, categoryId, keyword);
            }
            return projectRepository.findByCategoryIdOrderBySortAsc(categoryId);
        } else if (keyword != null && !keyword.isEmpty()) {
            return projectRepository.findByTitleContainingOrDescriptionContainingOrderBySortAsc(keyword, keyword);
        }
        return projectRepository.findAllByOrderBySortAsc();
    }
    
    // 获取所有分类
    public List<SysCategory> getCategories() {
        return categoryRepository.findAll();
    }
    
    // 新增作品
    public PmProject addProject(PmProject project) {
        checkAdminPermission();
        return projectRepository.save(project);
    }
    
    // 编辑作品
    public PmProject updateProject(Long id, PmProject project) {
        checkAdminPermission();
        PmProject existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("作品不存在"));
        
        existingProject.setTitle(project.getTitle());
        existingProject.setDescription(project.getDescription());
        existingProject.setCoverImage(project.getCoverImage());
        existingProject.setDetailLink(project.getDetailLink());
        existingProject.setGithubLink(project.getGithubLink());
        existingProject.setCategoryId(project.getCategoryId());
        existingProject.setSort(project.getSort());
        
        return projectRepository.save(existingProject);
    }
    
    // 删除作品
    public void deleteProject(Long id) {
        checkAdminPermission();
        if (!projectRepository.existsById(id)) {
            throw new RuntimeException("作品不存在");
        }
        projectRepository.deleteById(id);
    }
}