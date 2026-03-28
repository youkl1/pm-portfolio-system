-- 多租户权限管理系统SQL脚本
-- 版本：1.0
-- 日期：2026-03-27

-- 1. 租户套餐表
CREATE TABLE IF NOT EXISTS `tenant_package` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '套餐ID',
  `package_name` VARCHAR(100) NOT NULL UNIQUE COMMENT '套餐名称',
  `user_limit` INT NOT NULL COMMENT '用户数量上限',
  `feature_limit` VARCHAR(255) COMMENT '功能限制',
  `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` BIGINT COMMENT '创建人',
  `updated_by` BIGINT COMMENT '更新人'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '租户套餐表';

-- 2. 租户表
CREATE TABLE IF NOT EXISTS `tenant` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '租户ID',
  `tenant_name` VARCHAR(100) NOT NULL UNIQUE COMMENT '租户名称',
  `tenant_type` VARCHAR(20) NOT NULL COMMENT '租户类型（personal/team/enterprise）',
  `status` VARCHAR(20) NOT NULL COMMENT '状态（enabled/disabled/archived/deleted）',
  `package_id` BIGINT COMMENT '套餐ID',
  `expire_time` DATETIME COMMENT '过期时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` BIGINT COMMENT '创建人',
  `updated_by` BIGINT COMMENT '更新人',
  FOREIGN KEY (`package_id`) REFERENCES `tenant_package`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '租户表';

-- 3. 角色表
CREATE TABLE IF NOT EXISTS `sys_role` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
  `role_name` VARCHAR(100) NOT NULL COMMENT '角色名称',
  `description` VARCHAR(255) COMMENT '角色描述',
  `tenant_id` BIGINT COMMENT '租户ID',
  `is_system` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否系统角色',
  `is_enabled` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` BIGINT COMMENT '创建人',
  `updated_by` BIGINT COMMENT '更新人',
  FOREIGN KEY (`tenant_id`) REFERENCES `tenant`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '角色表';

-- 4. 权限表
CREATE TABLE IF NOT EXISTS `sys_permission` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
  `permission_name` VARCHAR(100) NOT NULL COMMENT '权限名称',
  `permission_code` VARCHAR(100) NOT NULL UNIQUE COMMENT '权限代码',
  `description` VARCHAR(255) COMMENT '权限描述',
  `permission_type` VARCHAR(20) NOT NULL COMMENT '权限类型（function/data/operation/resource）',
  `is_enabled` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` BIGINT COMMENT '创建人',
  `updated_by` BIGINT COMMENT '更新人'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '权限表';

-- 5. 角色权限关联表
CREATE TABLE IF NOT EXISTS `sys_role_permission` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `permission_id` BIGINT NOT NULL COMMENT '权限ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` BIGINT COMMENT '创建人',
  `updated_by` BIGINT COMMENT '更新人',
  FOREIGN KEY (`role_id`) REFERENCES `sys_role`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`permission_id`) REFERENCES `sys_permission`(`id`) ON DELETE CASCADE,
  UNIQUE KEY (`role_id`, `permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '角色权限关联表';

-- 6. 用户角色关联表
CREATE TABLE IF NOT EXISTS `sys_user_role` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `effective_time` DATETIME COMMENT '生效时间',
  `expire_time` DATETIME COMMENT '失效时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` BIGINT COMMENT '创建人',
  `updated_by` BIGINT COMMENT '更新人',
  FOREIGN KEY (`user_id`) REFERENCES `sys_user`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`role_id`) REFERENCES `sys_role`(`id`) ON DELETE CASCADE,
  UNIQUE KEY (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '用户角色关联表';

-- 7. 组织表
CREATE TABLE IF NOT EXISTS `organization` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '组织ID',
  `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
  `parent_id` BIGINT COMMENT '父组织ID',
  `org_name` VARCHAR(100) NOT NULL COMMENT '组织名称',
  `org_type` VARCHAR(20) NOT NULL COMMENT '组织类型',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` BIGINT COMMENT '创建人',
  `updated_by` BIGINT COMMENT '更新人',
  FOREIGN KEY (`tenant_id`) REFERENCES `tenant`(`id`),
  FOREIGN KEY (`parent_id`) REFERENCES `organization`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '组织表';

-- 8. 用户组织关联表
CREATE TABLE IF NOT EXISTS `user_organization` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `org_id` BIGINT NOT NULL COMMENT '组织ID',
  `position` VARCHAR(100) COMMENT '职位',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` BIGINT COMMENT '创建人',
  `updated_by` BIGINT COMMENT '更新人',
  FOREIGN KEY (`user_id`) REFERENCES `sys_user`(`id`),
  FOREIGN KEY (`org_id`) REFERENCES `organization`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '用户组织关联表';

-- 9. 登录日志表
CREATE TABLE IF NOT EXISTS `login_log` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
  `user_id` BIGINT COMMENT '用户ID',
  `tenant_id` BIGINT COMMENT '租户ID',
  `login_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
  `login_ip` VARCHAR(50) NOT NULL COMMENT '登录IP',
  `login_device` VARCHAR(100) COMMENT '登录设备',
  `login_result` VARCHAR(20) NOT NULL COMMENT '登录结果（success/fail）',
  `fail_reason` VARCHAR(255) COMMENT '失败原因',
  FOREIGN KEY (`user_id`) REFERENCES `sys_user`(`id`),
  FOREIGN KEY (`tenant_id`) REFERENCES `tenant`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '登录日志表';

-- 10. 操作日志表
CREATE TABLE IF NOT EXISTS `operation_log` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
  `user_id` BIGINT COMMENT '用户ID',
  `tenant_id` BIGINT COMMENT '租户ID',
  `operation_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `operation_type` VARCHAR(50) NOT NULL COMMENT '操作类型',
  `operation_target` VARCHAR(100) NOT NULL COMMENT '操作目标',
  `operation_content` TEXT COMMENT '操作内容',
  `result` VARCHAR(20) NOT NULL COMMENT '操作结果（success/fail）',
  FOREIGN KEY (`user_id`) REFERENCES `sys_user`(`id`),
  FOREIGN KEY (`tenant_id`) REFERENCES `tenant`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '操作日志表';

-- 11. 检查并创建sys_user表（如果不存在）
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(100) NOT NULL UNIQUE COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码',
  `email` VARCHAR(100) COMMENT '邮箱',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户ID',
  `is_admin` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否管理员',
  `status` VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '用户状态',
  `last_login_time` DATETIME COMMENT '最后登录时间',
  `last_login_ip` VARCHAR(50) COMMENT '最后登录IP',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` BIGINT COMMENT '创建人',
  `updated_by` BIGINT COMMENT '更新人',
  FOREIGN KEY (`tenant_id`) REFERENCES `tenant`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '用户表';

-- 12. 如果sys_user表已存在，仅添加缺失的字段
-- 注意：以下语句在字段已存在时会失败，实际执行时请根据需要选择性执行
-- ALTER TABLE `sys_user` ADD COLUMN IF NOT EXISTS `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户ID';
-- ALTER TABLE `sys_user` ADD COLUMN IF NOT EXISTS `is_admin` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否管理员';
-- ALTER TABLE `sys_user` ADD COLUMN IF NOT EXISTS `status` VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '用户状态';
-- ALTER TABLE `sys_user` ADD COLUMN IF NOT EXISTS `last_login_time` DATETIME COMMENT '最后登录时间';
-- ALTER TABLE `sys_user` ADD COLUMN IF NOT EXISTS `last_login_ip` VARCHAR(50) COMMENT '最后登录IP';
-- ALTER TABLE `sys_user` ADD COLUMN IF NOT EXISTS `created_by` BIGINT COMMENT '创建人';
-- ALTER TABLE `sys_user` ADD COLUMN IF NOT EXISTS `updated_by` BIGINT COMMENT '更新人';
-- ALTER TABLE `sys_user` ADD FOREIGN KEY IF NOT EXISTS (`tenant_id`) REFERENCES `tenant`(`id`);

-- 12. 初始化租户套餐数据
INSERT INTO `tenant_package` (`package_name`, `user_limit`, `feature_limit`, `price`) VALUES
('免费版', 1, '{"projects": 5, "resumes": 10, "storage": 1024}', 0.00),
('团队版', 10, '{"projects": 50, "resumes": 100, "storage": 10240}', 99.00),
('企业版', 100, '{"projects": 500, "resumes": 1000, "storage": 102400, "enterprise_features": true}', 999.00);

-- 13. 初始化租户数据
INSERT INTO `tenant` (`tenant_name`, `tenant_type`, `status`, `package_id`, `expire_time`) VALUES
('平台默认租户', 'system', 'enabled', 1, '2099-12-31 23:59:59'),
('个人用户租户', 'personal', 'enabled', 1, '2027-03-26 23:59:59'),
('测试团队租户', 'team', 'enabled', 2, '2027-03-26 23:59:59'),
('测试企业租户', 'enterprise', 'enabled', 3, '2027-03-26 23:59:59');

-- 14. 初始化权限数据
INSERT INTO `sys_permission` (`permission_code`, `permission_name`, `description`, `permission_type`) VALUES
-- 平台级权限
('PLATFORM_TENANT_MANAGE', '租户管理', '管理所有租户', 'function'),
('PLATFORM_PACKAGE_MANAGE', '套餐管理', '管理租户套餐', 'function'),
('PLATFORM_USER_MANAGE', '用户管理', '管理平台用户', 'function'),
('PLATFORM_ROLE_MANAGE', '角色管理', '管理平台角色', 'function'),
('PLATFORM_PERMISSION_MANAGE', '权限管理', '管理平台权限', 'function'),
-- 租户级权限
('TENANT_USER_MANAGE', '用户管理', '管理租户内用户', 'function'),
('TENANT_ROLE_MANAGE', '角色管理', '管理租户内角色', 'function'),
('TENANT_PERMISSION_MANAGE', '权限管理', '管理租户内权限', 'function'),
('TENANT_ORGANIZATION_MANAGE', '组织架构管理', '管理租户组织架构', 'function'),
('TENANT_PROJECT_MANAGE', '项目管理', '管理租户内项目', 'function'),
('TENANT_RESUME_MANAGE', '简历管理', '管理租户内简历', 'function'),
-- 操作权限
('PROJECT_CREATE', '创建项目', '创建新项目', 'operation'),
('PROJECT_EDIT', '编辑项目', '编辑现有项目', 'operation'),
('PROJECT_DELETE', '删除项目', '删除项目', 'operation'),
('PROJECT_VIEW', '查看项目', '查看项目详情', 'operation'),
('RESUME_CREATE', '创建简历', '创建新简历', 'operation'),
('RESUME_EDIT', '编辑简历', '编辑现有简历', 'operation'),
('RESUME_DELETE', '删除简历', '删除简历', 'operation'),
('RESUME_VIEW', '查看简历', '查看简历详情', 'operation'),
-- 数据权限
('DATA_OWN', '仅查看自己的数据', '只能查看自己创建的数据', 'data'),
('DATA_DEPARTMENT', '查看部门数据', '查看所在部门的数据', 'data'),
('DATA_TENANT', '查看租户数据', '查看租户内所有数据', 'data'),
-- 资源权限
('RESOURCE_STORAGE', '存储资源', '使用存储资源', 'resource'),
('RESOURCE_API', 'API资源', '使用API资源', 'resource');

-- 15. 初始化角色数据
INSERT INTO `sys_role` (`tenant_id`, `role_name`, `description`, `is_system`) VALUES
(NULL, '平台管理员', '平台最高权限角色', true),
(1, '租户管理员', '租户最高权限角色', true),
(1, '普通用户', '普通用户角色', true),
(2, '租户管理员', '个人租户管理员', true),
(2, '普通用户', '个人租户普通用户', true),
(3, '租户管理员', '团队租户管理员', true),
(3, '项目管理员', '团队租户项目管理员', false),
(3, '普通用户', '团队租户普通用户', true),
(4, '租户管理员', '企业租户管理员', true),
(4, '部门管理员', '企业租户部门管理员', false),
(4, '项目管理员', '企业租户项目管理员', false),
(4, '普通用户', '企业租户普通用户', true);

-- 16. 初始化用户数据
INSERT INTO `sys_user` (`username`, `password`, `email`, `tenant_id`, `is_admin`, `status`) VALUES
('admin', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'admin@example.com', 1, true, 'active'),
('user1', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'user1@example.com', 2, true, 'active'),
('user2', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'user2@example.com', 3, true, 'active'),
('user3', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'user3@example.com', 3, false, 'active'),
('user4', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'user4@example.com', 4, true, 'active'),
('user5', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'user5@example.com', 4, false, 'active');

-- 17. 初始化用户角色关联
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
(1, 1), -- admin 用户关联平台管理员角色
(2, 4), -- user1 用户关联个人租户管理员角色
(3, 6), -- user2 用户关联团队租户管理员角色
(4, 8), -- user3 用户关联团队租户普通用户角色
(5, 9), -- user4 用户关联企业租户管理员角色
(6, 12); -- user5 用户关联企业租户普通用户角色

-- 18. 初始化角色权限关联
-- 平台管理员权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), -- 平台级权限
(1, 6), (1, 7), (1, 8), (1, 9), (1, 10), (1, 11), -- 租户级权限
(1, 12), (1, 13), (1, 14), (1, 15), (1, 16), (1, 17), (1, 18), (1, 19), -- 操作权限
(1, 20), (1, 21), (1, 22), -- 数据权限
(1, 23), (1, 24); -- 资源权限

-- 租户管理员权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(2, 6), (2, 7), (2, 8), (2, 9), (2, 10), (2, 11), -- 租户级权限
(2, 12), (2, 13), (2, 14), (2, 15), (2, 16), (2, 17), (2, 18), (1, 19), -- 操作权限
(2, 22), -- 数据权限
(2, 23), (2, 24); -- 资源权限

-- 普通用户权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(3, 10), (3, 11), -- 租户级权限
(3, 15), (3, 19), -- 操作权限（仅查看）
(3, 20), -- 数据权限
(3, 23), (3, 24); -- 资源权限

-- 19. 初始化组织架构数据（企业租户）
INSERT INTO `organization` (`tenant_id`, `parent_id`, `org_name`, `org_type`) VALUES
(4, NULL, '测试企业', 'group'),
(4, 1, '技术部', 'department'),
(4, 1, '市场部', 'department'),
(4, 2, '开发组', 'project'),
(4, 2, '测试组', 'project'),
(4, 3, '市场策划组', 'project'),
(4, 3, '销售组', 'project');

-- 20. 初始化用户组织关联
INSERT INTO `user_organization` (`user_id`, `org_id`, `position`) VALUES
(5, 1, 'CEO'), -- user4 是企业租户管理员，职位CEO
(6, 4, '开发工程师'); -- user5 是普通用户，职位开发工程师

-- 21. 初始化登录日志
INSERT INTO `login_log` (`user_id`, `tenant_id`, `login_ip`, `login_device`, `login_result`) VALUES
(1, 1, '127.0.0.1', 'Windows PC', 'success'),
(2, 2, '127.0.0.1', 'MacBook', 'success'),
(3, 3, '127.0.0.1', 'Windows PC', 'success'),
(4, 3, '127.0.0.1', 'iPhone', 'success'),
(5, 4, '127.0.0.1', 'Windows PC', 'success'),
(6, 4, '127.0.0.1', 'Android', 'success');
