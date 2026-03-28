package com.pmportfolio.hub.service.impl;

import com.pmportfolio.hub.model.SysUser;
import com.pmportfolio.hub.model.Role;
import com.pmportfolio.hub.model.UserRole;
import com.pmportfolio.hub.repository.UserRepository;
import com.pmportfolio.hub.repository.RoleRepository;
import com.pmportfolio.hub.repository.UserRoleRepository;
import com.pmportfolio.hub.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Transactional
    @Override
    public void assignRoles(Long userId, List<Long> roleIds) {
        SysUser user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        
        // 先删除用户的所有角色
        userRoleRepository.deleteByUser(user);
        
        // 再添加新的角色
        for (Long roleId : roleIds) {
            Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));
            
            // 检查是否已存在该用户和角色的关联
            if (userRoleRepository.findByUserAndRole(user, role).isEmpty()) {
                UserRole userRole = new UserRole();
                userRole.setUser(user);
                userRole.setRole(role);
                userRoleRepository.save(userRole);
            }
        }
    }

    @Override
    public List<Role> getUserRoles(Long userId) {
        SysUser user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        List<UserRole> userRoles = userRoleRepository.findByUser(user);
        return userRoles.stream().map(UserRole::getRole).toList();
    }

    @Override
    public List<SysUser> getUsersByRole(Long roleId) {
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));
        List<UserRole> userRoles = userRoleRepository.findByRole(role);
        return userRoles.stream().map(UserRole::getUser).toList();
    }
}