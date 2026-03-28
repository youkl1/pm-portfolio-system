package com.pmportfolio.hub.controller;

import com.pmportfolio.hub.common.PermissionCheck;
import com.pmportfolio.hub.model.SysUser;
import com.pmportfolio.hub.model.LoginLog;
import com.pmportfolio.hub.model.Role;
import com.pmportfolio.hub.model.UserRole;
import com.pmportfolio.hub.repository.UserRepository;
import com.pmportfolio.hub.repository.LoginLogRepository;
import com.pmportfolio.hub.repository.UserRoleRepository;
import com.pmportfolio.hub.repository.RoleRepository;
import com.pmportfolio.hub.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LoginLogRepository loginLogRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private RoleRepository roleRepository;

    // 获取用户列表
    @GetMapping("/list")
    @PermissionCheck(roles = {"admin"})
    public Map<String, Object> getUserList() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<SysUser> users = userRepository.findAll();
            List<Map<String, Object>> userList = users.stream().map(user -> {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("id", user.getId());
                userMap.put("username", user.getUsername());
                userMap.put("name", user.getUsername()); // 假设name字段与username相同
                userMap.put("email", ""); // 假设email字段为空
                userMap.put("phone", ""); // 假设phone字段为空
                userMap.put("password", user.getPassword()); // 返回密码字段，用于编辑回显
                userMap.put("status", user.getStatus());
                Map<String, Object> tenant = new HashMap<>();
                tenant.put("tenantName", "默认租户"); // 假设所有用户都属于默认租户
                userMap.put("tenantId", tenant);
                
                // 获取用户角色
                List<UserRole> userRoles = userRoleRepository.findByUser(user);
                List<Map<String, Object>> roles = userRoles.stream().map(userRole -> {
                    Map<String, Object> roleMap = new HashMap<>();
                    roleMap.put("id", userRole.getRole().getId());
                    roleMap.put("roleName", userRole.getRole().getRoleName());
                    return roleMap;
                }).collect(Collectors.toList());
                userMap.put("roles", roles);
                
                return userMap;
            }).collect(Collectors.toList());

            response.put("code", 200);
            response.put("message", "成功");
            response.put("data", userList);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取用户列表失败");
            response.put("data", null);
        }
        return response;
    }

    // 获取邀请列表
    @GetMapping("/invites")
    @PermissionCheck(roles = {"admin"})
    public Map<String, Object> getInviteList() {
        Map<String, Object> response = new HashMap<>();
        try {
            // 暂时返回空列表，因为数据库中可能没有邀请表
            response.put("code", 200);
            response.put("message", "成功");
            response.put("data", new ArrayList<>());
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取邀请列表失败");
            response.put("data", null);
        }
        return response;
    }

    // 获取登录日志
    @GetMapping("/login-logs")
    @PermissionCheck(roles = {"admin"})
    public Map<String, Object> getLoginLogs() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<LoginLog> logs = loginLogRepository.findAll();
            List<Map<String, Object>> logList = logs.stream().map(log -> {
                Map<String, Object> logMap = new HashMap<>();
                logMap.put("id", log.getId());
                logMap.put("loginTime", log.getLoginTime());
                logMap.put("loginIp", log.getLoginIp());
                logMap.put("loginDevice", log.getLoginDevice());
                logMap.put("loginResult", log.getLoginResult());
                logMap.put("failReason", log.getFailReason());
                return logMap;
            }).collect(Collectors.toList());

            response.put("code", 200);
            response.put("message", "成功");
            response.put("data", logList);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取登录日志失败");
            response.put("data", null);
        }
        return response;
    }

    // 创建用户
    @PostMapping("/create")
    @PermissionCheck(roles = {"admin"})
    public Map<String, Object> createUser(@RequestBody Map<String, Object> userData) {
        Map<String, Object> response = new HashMap<>();
        try {
            SysUser user = new SysUser();
            user.setUsername((String) userData.get("username"));
            user.setPassword((String) userData.get("password"));
            user.setStatus((String) userData.get("status"));
            userRepository.save(user);

            response.put("code", 200);
            response.put("message", "成功");
            response.put("data", userData);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "创建用户失败");
            response.put("data", null);
        }
        return response;
    }

    // 更新用户
    @PutMapping("/update")
    @PermissionCheck(roles = {"admin"})
    public Map<String, Object> updateUser(@RequestBody Map<String, Object> userData) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = Long.valueOf(userData.get("id").toString());
            SysUser user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));
            user.setUsername((String) userData.get("username"));
            if (userData.containsKey("password")) {
                user.setPassword((String) userData.get("password"));
            }
            user.setStatus((String) userData.get("status"));
            userRepository.save(user);

            response.put("code", 200);
            response.put("message", "成功");
            response.put("data", userData);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "更新用户失败");
            response.put("data", null);
        }
        return response;
    }

    // 删除用户
    @DeleteMapping("/delete/{id}")
    @PermissionCheck(roles = {"admin"})
    public Map<String, Object> deleteUser(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            userRepository.deleteById(id);
            response.put("code", 200);
            response.put("message", "成功");
            response.put("data", null);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "删除用户失败");
            response.put("data", null);
        }
        return response;
    }

    // 获取用户角色
    @GetMapping("/{userId}/roles")
    @PermissionCheck(roles = {"admin"})
    public Map<String, Object> getUserRoles(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<UserRole> userRoles = userRoleRepository.findByUser(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在")));
            List<Map<String, Object>> roleList = userRoles.stream().map(userRole -> {
                Role role = userRole.getRole();
                Map<String, Object> roleMap = new HashMap<>();
                roleMap.put("id", role.getId());
                roleMap.put("roleName", role.getRoleName());
                roleMap.put("description", role.getDescription());
                return roleMap;
            }).collect(Collectors.toList());

            response.put("code", 200);
            response.put("message", "成功");
            response.put("data", roleList);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取用户角色失败");
            response.put("data", null);
        }
        return response;
    }

    // 分配用户角色
    @PostMapping("/{userId}/roles")
    @PermissionCheck(roles = {"admin"})
    public Map<String, Object> assignUserRoles(@PathVariable Long userId, @RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("开始分配角色，用户ID: {}, 请求数据: {}", userId, request);
            
            SysUser user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));
            logger.info("找到用户: {}", user.getUsername());
            
            // 先删除用户的所有角色
            userRoleRepository.deleteByUser(user);
            logger.info("已删除用户的所有角色");
            
            // 再添加新的角色
            List<?> roleIdObjects = (List<?>) request.get("roleIds");
            logger.info("角色ID列表: {}", roleIdObjects);
            
            if (roleIdObjects != null) {
                for (Object roleIdObj : roleIdObjects) {
                    Long roleId;
                    if (roleIdObj instanceof Integer) {
                        roleId = ((Integer) roleIdObj).longValue();
                    } else if (roleIdObj instanceof Long) {
                        roleId = (Long) roleIdObj;
                    } else if (roleIdObj instanceof String) {
                        try {
                            roleId = Long.parseLong((String) roleIdObj);
                        } catch (NumberFormatException e) {
                            logger.error("角色ID格式错误: {}", roleIdObj);
                            continue;
                        }
                    } else {
                        logger.error("角色ID类型错误: {}", roleIdObj.getClass());
                        continue;
                    }
                    
                    Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("角色不存在"));
                    logger.info("找到角色: {}", role.getRoleName());
                    
                    UserRole userRole = new UserRole();
                    userRole.setUser(user);
                    userRole.setRole(role);
                    userRoleRepository.save(userRole);
                    logger.info("已分配角色: {} 给用户: {}", role.getRoleName(), user.getUsername());
                }
            }

            response.put("code", 200);
            response.put("message", "成功");
            response.put("data", null);
            logger.info("角色分配成功，用户ID: {}", userId);
        } catch (Exception e) {
            logger.error("分配角色失败，用户ID: {}", userId, e);
            response.put("code", 500);
            response.put("message", "分配角色失败: " + e.getMessage());
            response.put("data", null);
        }
        return response;
    }

    // 邀请成员
    @PostMapping("/invite")
    @PermissionCheck(roles = {"admin"})
    public Map<String, Object> inviteUser(@RequestBody Map<String, Object> invite) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 暂时返回成功，因为数据库中可能没有邀请表
            response.put("code", 200);
            response.put("message", "成功");
            response.put("data", invite);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "邀请成员失败");
            response.put("data", null);
        }
        return response;
    }

    // 撤回邀请
    @DeleteMapping("/invite/revoke/{id}")
    @PermissionCheck(roles = {"admin"})
    public Map<String, Object> revokeInvite(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 暂时返回成功，因为数据库中可能没有邀请表
            response.put("code", 200);
            response.put("message", "成功");
            response.put("data", null);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "撤回邀请失败");
            response.put("data", null);
        }
        return response;
    }

    // 重发邀请
    @PostMapping("/invite/resend/{id}")
    @PermissionCheck(roles = {"admin"})
    public Map<String, Object> resendInvite(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 暂时返回成功，因为数据库中可能没有邀请表
            response.put("code", 200);
            response.put("message", "成功");
            response.put("data", null);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "重发邀请失败");
            response.put("data", null);
        }
        return response;
    }
}
