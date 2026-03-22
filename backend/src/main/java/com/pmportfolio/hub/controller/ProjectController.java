package com.pmportfolio.hub.controller;

import com.pmportfolio.hub.model.PmProject;
import com.pmportfolio.hub.model.SysCategory;
import com.pmportfolio.hub.service.ProjectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    @Autowired
    private ProjectService projectService;
    
    // 获取作品列表
    @GetMapping
    public Map<String, Object> getProjects(@RequestParam(required = false) String keyword, @RequestParam(required = false) Long categoryId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<PmProject> projects = projectService.getProjects(keyword, categoryId);
            response.put("code", 200);
            response.put("message", "成功");
            response.put("data", projects);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", e.getMessage());
            response.put("data", null);
        }
        return response;
    }
    
    // 获取分类列表
    @GetMapping("/categories")
    public Map<String, Object> getCategories() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<SysCategory> categories = projectService.getCategories();
            response.put("code", 200);
            response.put("message", "成功");
            response.put("data", categories);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", e.getMessage());
            response.put("data", null);
        }
        return response;
    }
    
    // 新增作品
    @PostMapping
    public Map<String, Object> addProject(@RequestBody PmProject project) {
        Map<String, Object> response = new HashMap<>();
        try {
            PmProject newProject = projectService.addProject(project);
            response.put("code", 200);
            response.put("message", "新增成功");
            response.put("data", newProject);
        } catch (Exception e) {
            response.put("code", 400);
            response.put("message", e.getMessage());
            response.put("data", null);
        }
        return response;
    }
    
    // 编辑作品
    @PutMapping("/{id}")
    public Map<String, Object> updateProject(@PathVariable Long id, @RequestBody PmProject project) {
        Map<String, Object> response = new HashMap<>();
        try {
            PmProject updatedProject = projectService.updateProject(id, project);
            response.put("code", 200);
            response.put("message", "更新成功");
            response.put("data", updatedProject);
        } catch (Exception e) {
            response.put("code", 400);
            response.put("message", e.getMessage());
            response.put("data", null);
        }
        return response;
    }
    
    // 删除作品
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteProject(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            projectService.deleteProject(id);
            response.put("code", 200);
            response.put("message", "删除成功");
            response.put("data", null);
        } catch (Exception e) {
            response.put("code", 400);
            response.put("message", e.getMessage());
            response.put("data", null);
        }
        return response;
    }
}