package com.pmportfolio.hub.service;

import com.pmportfolio.hub.common.ThreadLocalUtil;
import com.pmportfolio.hub.model.PmResume;
import com.pmportfolio.hub.repository.PmResumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResumeService {
    @Autowired
    private PmResumeRepository resumeRepository;
    
    // 检查是否为admin角色
    private void checkAdminPermission() {
        ThreadLocalUtil.UserInfo userInfo = ThreadLocalUtil.getUserInfo();
        if (!"admin".equals(userInfo.getRole())) {
            throw new RuntimeException("权限不足，无法执行此操作");
        }
    }
    
    // 获取个人简历列表
    public List<PmResume> getResumes() {
        return resumeRepository.findAll();
    }
    
    // 获取个人简历详情
    public PmResume getResume(Long id) {
        return resumeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("简历不存在"));
    }
    
    // 新增个人简历
    public PmResume addResume(PmResume resume) {
        checkAdminPermission();
        return resumeRepository.save(resume);
    }
    
    // 编辑个人简历
    public PmResume updateResume(Long id, PmResume resume) {
        checkAdminPermission();
        PmResume existingResume = resumeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("简历不存在"));
        
        existingResume.setName(resume.getName());
        existingResume.setEmail(resume.getEmail());
        existingResume.setPhone(resume.getPhone());
        existingResume.setEducation(resume.getEducation());
        existingResume.setWorkExperience(resume.getWorkExperience());
        existingResume.setSkills(resume.getSkills());
        existingResume.setProjects(resume.getProjects());
        existingResume.setSelfIntroduction(resume.getSelfIntroduction());
        existingResume.setResumeFile(resume.getResumeFile());
        existingResume.setGender(resume.getGender());
        existingResume.setBirthDate(resume.getBirthDate());
        existingResume.setWorkStartDate(resume.getWorkStartDate());
        existingResume.setJobStatus(resume.getJobStatus());
        existingResume.setUserType(resume.getUserType());
        existingResume.setWechat(resume.getWechat());
        existingResume.setPersonalAdvantage(resume.getPersonalAdvantage());
        existingResume.setExpectedPosition(resume.getExpectedPosition());
        
        return resumeRepository.save(existingResume);
    }
    
    // 删除个人简历
    public void deleteResume(Long id) {
        checkAdminPermission();
        if (!resumeRepository.existsById(id)) {
            throw new RuntimeException("简历不存在");
        }
        resumeRepository.deleteById(id);
    }
}