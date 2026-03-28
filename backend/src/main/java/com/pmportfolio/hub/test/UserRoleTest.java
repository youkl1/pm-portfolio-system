package com.pmportfolio.hub.test;

import com.pmportfolio.hub.service.UserRoleService;
import com.pmportfolio.hub.service.RoleService;
import com.pmportfolio.hub.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserRoleTest implements CommandLineRunner {

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleService roleService;

    @Override
    public void run(String... args) throws Exception {
        // 测试获取用户角色
        System.out.println("=== 测试获取用户角色 ===");
        try {
            List<Role> roles = userRoleService.getUserRoles(1L);
            System.out.println("用户ID 1 的角色数量: " + roles.size());
            for (Role role : roles) {
                System.out.println("角色名称: " + role.getRoleName());
            }
        } catch (Exception e) {
            System.out.println("获取用户角色失败: " + e.getMessage());
        }

        // 测试获取所有角色
        System.out.println("\n=== 测试获取所有角色 ===");
        try {
            List<Role> allRoles = roleService.getAllRoles();
            System.out.println("所有角色数量: " + allRoles.size());
            for (Role role : allRoles) {
                System.out.println("角色ID: " + role.getId() + ", 角色名称: " + role.getRoleName());
            }
        } catch (Exception e) {
            System.out.println("获取所有角色失败: " + e.getMessage());
        }
    }
}
