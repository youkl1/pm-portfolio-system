package com.pmportfolio.hub.controller;

import com.pmportfolio.hub.model.PmResume;
import com.pmportfolio.hub.service.ResumeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {
    @Autowired
    private ResumeService resumeService;
    
    // 获取个人简历列表
    @GetMapping
    public Map<String, Object> getResumes() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<PmResume> resumes = resumeService.getResumes();
            response.put("code", 200);
            response.put("message", "成功");
            response.put("data", resumes);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", e.getMessage());
            response.put("data", null);
        }
        return response;
    }
    
    // 获取个人简历详情
    @GetMapping("/{id}")
    public Map<String, Object> getResume(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            PmResume resume = resumeService.getResume(id);
            response.put("code", 200);
            response.put("message", "成功");
            response.put("data", resume);
        } catch (Exception e) {
            response.put("code", 400);
            response.put("message", e.getMessage());
            response.put("data", null);
        }
        return response;
    }
    
    // 新增个人简历
    @PostMapping
    public Map<String, Object> addResume(@RequestBody PmResume resume) {
        Map<String, Object> response = new HashMap<>();
        try {
            PmResume newResume = resumeService.addResume(resume);
            response.put("code", 200);
            response.put("message", "新增成功");
            response.put("data", newResume);
        } catch (Exception e) {
            response.put("code", 400);
            response.put("message", e.getMessage());
            response.put("data", null);
        }
        return response;
    }
    
    // 编辑个人简历
    @PutMapping("/{id}")
    public Map<String, Object> updateResume(@PathVariable Long id, @RequestBody PmResume resume) {
        Map<String, Object> response = new HashMap<>();
        try {
            PmResume updatedResume = resumeService.updateResume(id, resume);
            response.put("code", 200);
            response.put("message", "更新成功");
            response.put("data", updatedResume);
        } catch (Exception e) {
            response.put("code", 400);
            response.put("message", e.getMessage());
            response.put("data", null);
        }
        return response;
    }
    
    // 删除个人简历
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteResume(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            resumeService.deleteResume(id);
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