-- 模板库功能数据库脚本
-- 日期：2026-03-27

-- 创建模板分类表
CREATE TABLE IF NOT EXISTS `template_category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `description` TEXT,
  `created_by` BIGINT,
  `updated_by` BIGINT,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  KEY `idx_created_by` (`created_by`),
  KEY `idx_updated_by` (`updated_by`),
  CONSTRAINT `fk_template_category_created_by` FOREIGN KEY (`created_by`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_template_category_updated_by` FOREIGN KEY (`updated_by`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建模板表
CREATE TABLE IF NOT EXISTS `template` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `description` TEXT,
  `file_path` VARCHAR(512) NOT NULL,
  `format` VARCHAR(50) NOT NULL,
  `size` BIGINT NOT NULL,
  `content_hash` VARCHAR(128),
  `is_encrypted` BOOLEAN NOT NULL DEFAULT TRUE,
  `created_by` BIGINT,
  `updated_by` BIGINT,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`),
  KEY `idx_format` (`format`),
  KEY `idx_created_by` (`created_by`),
  KEY `idx_updated_by` (`updated_by`),
  CONSTRAINT `fk_template_created_by` FOREIGN KEY (`created_by`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_template_updated_by` FOREIGN KEY (`updated_by`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建模板分类关联表
CREATE TABLE IF NOT EXISTS `template_category_relation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `template_id` BIGINT NOT NULL,
  `category_id` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_template_category` (`template_id`, `category_id`),
  KEY `idx_template_id` (`template_id`),
  KEY `idx_category_id` (`category_id`),
  CONSTRAINT `fk_template_category_relation_template` FOREIGN KEY (`template_id`) REFERENCES `template` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_template_category_relation_category` FOREIGN KEY (`category_id`) REFERENCES `template_category` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建模板版本表
CREATE TABLE IF NOT EXISTS `template_version` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `template_id` BIGINT NOT NULL,
  `version` VARCHAR(50) NOT NULL,
  `file_path` VARCHAR(512) NOT NULL,
  `description` TEXT,
  `created_by` BIGINT,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_template_id` (`template_id`),
  KEY `idx_version` (`version`),
  KEY `idx_created_by` (`created_by`),
  CONSTRAINT `fk_template_version_template` FOREIGN KEY (`template_id`) REFERENCES `template` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_template_version_created_by` FOREIGN KEY (`created_by`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入默认分类
INSERT INTO `template_category` (`name`, `description`) VALUES
('需求文档', '产品需求相关文档模板'),
('模型理论', '产品模型和理论相关文档模板')
ON DUPLICATE KEY UPDATE `description` = VALUES(`description`);

-- 插入模板库相关权限
INSERT INTO `sys_permission` (`permission_name`, `permission_code`, `description`, `permission_type`, `is_enabled`) VALUES
('模板查看权限', 'TEMPLATE_VIEW', '查看模板的权限', 'function', TRUE),
('模板编辑权限', 'TEMPLATE_EDIT', '编辑模板的权限', 'function', TRUE),
('模板导入权限', 'TEMPLATE_IMPORT', '导入模板的权限', 'function', TRUE),
('模板下载权限', 'TEMPLATE_DOWNLOAD', '下载模板的权限', 'function', TRUE),
('分类管理权限', 'TEMPLATE_CATEGORY_MANAGE', '管理模板分类的权限', 'function', TRUE)
ON DUPLICATE KEY UPDATE `description` = VALUES(`description`), `permission_type` = VALUES(`permission_type`), `is_enabled` = VALUES(`is_enabled`);

-- 为admin角色分配模板库权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id
FROM `sys_role` r
CROSS JOIN `sys_permission` p
WHERE r.role_name = 'admin' AND p.permission_code IN ('TEMPLATE_VIEW', 'TEMPLATE_EDIT', 'TEMPLATE_IMPORT', 'TEMPLATE_DOWNLOAD', 'TEMPLATE_CATEGORY_MANAGE')
ON DUPLICATE KEY UPDATE `role_id` = VALUES(`role_id`), `permission_id` = VALUES(`permission_id`);

-- 为test角色分配模板查看权限（仅当test角色存在时）
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id
FROM `sys_role` r
CROSS JOIN `sys_permission` p
WHERE r.role_name = 'test' AND p.permission_code = 'TEMPLATE_VIEW'
ON DUPLICATE KEY UPDATE `role_id` = VALUES(`role_id`), `permission_id` = VALUES(`permission_id`);
