-- 产品经理个人作品展示系统数据库脚本

-- 创建用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '账号',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `role` VARCHAR(20) NOT NULL COMMENT '角色（admin或test）',
  `expire_time` DATETIME NOT NULL COMMENT '过期时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 创建作品表
CREATE TABLE IF NOT EXISTS `pm_project` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '作品ID',
  `title` VARCHAR(255) NOT NULL COMMENT '标题',
  `description` TEXT COMMENT '描述',
  `cover_image` VARCHAR(500) COMMENT '封面图链接',
  `detail_link` VARCHAR(500) COMMENT '详情跳转链接',
  `github_link` VARCHAR(500) COMMENT 'GitHub地址',
  `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作品表';

-- 初始化默认用户数据
INSERT INTO `sys_user` (`username`, `password`, `role`, `expire_time`) VALUES
('admin', 'admin123', 'admin', DATE_ADD(NOW(), INTERVAL 10 YEAR)),
('test', 'test123', 'test', DATE_ADD(NOW(), INTERVAL 10 YEAR));

-- 初始化默认作品数据
INSERT INTO `pm_project` (`title`, `description`, `cover_image`, `detail_link`, `github_link`, `sort`) VALUES
('智能客服系统', '基于AI技术的智能客服系统，支持自然语言处理和多渠道接入', 'https://example.com/uploads/ai-customer-service.jpg', 'https://example.com/projects/ai-customer-service', 'https://github.com/example/ai-customer-service', 1),
('电商平台重构', '对现有电商平台进行全面重构，提升用户体验和系统性能', 'https://example.com/uploads/ecommerce-platform.jpg', 'https://example.com/projects/ecommerce-platform', 'https://github.com/example/ecommerce-platform', 2),
('健康管理App', '个人健康数据管理应用，支持运动追踪和健康建议', 'https://example.com/uploads/health-app.jpg', 'https://example.com/projects/health-app', 'https://github.com/example/health-app', 3),
('企业内部协作系统', '为企业打造的内部协作平台，提高团队工作效率', 'https://example.com/uploads/collaboration-system.jpg', 'https://example.com/projects/collaboration-system', 'https://github.com/example/collaboration-system', 4),
('在线教育平台', '提供在线课程和学习管理功能的教育平台', 'https://example.com/uploads/online-education.jpg', 'https://example.com/projects/online-education', 'https://github.com/example/online-education', 5);