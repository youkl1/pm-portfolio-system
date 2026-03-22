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

-- 创建系统分类表
CREATE TABLE IF NOT EXISTS `sys_category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` VARCHAR(100) NOT NULL COMMENT '分类名称',
  `description` TEXT COMMENT '分类描述',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统分类表';

-- 创建作品表
CREATE TABLE IF NOT EXISTS `pm_project` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '作品ID',
  `title` VARCHAR(255) NOT NULL COMMENT '标题',
  `description` TEXT COMMENT '描述',
  `cover_image` VARCHAR(500) COMMENT '封面图链接',
  `detail_link` VARCHAR(500) COMMENT '详情跳转链接',
  `github_link` VARCHAR(500) COMMENT 'GitHub地址',
  `category_id` BIGINT COMMENT '分类ID',
  `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_sort` (`sort`),
  INDEX `idx_category` (`category_id`),
  FOREIGN KEY (`category_id`) REFERENCES `sys_category` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作品表';

-- 初始化默认用户数据
INSERT INTO `sys_user` (`username`, `password`, `role`, `expire_time`) VALUES
('admin', 'admin123', 'admin', DATE_ADD(NOW(), INTERVAL 10 YEAR)),
('test', 'test123', 'test', DATE_ADD(NOW(), INTERVAL 10 YEAR));

-- 初始化系统分类数据（2026-03-22）
INSERT INTO `sys_category` (`name`, `description`) VALUES
('核心业务系统', '支撑机构主营业务运转、直接创造营收 / 履行核心法定职能的系统，是机构的 "业务生命线"'),
('管理支撑系统', '支撑内部管理、提升运营效率、规范组织管控流程的系统，不直接产生营收，但保障组织高效运转'),
('运营与营销系统', '面向客户 / 用户、市场推广、客户运营、销售转化的系统，直接影响机构获客、留存与营收增长'),
('数据与智能决策系统', '基于全量业务数据完成采集、加工、分析、建模，支撑经营决策与智能化应用的系统，是机构的 "数字大脑"'),
('协同与生态系统', '打通上下游产业链、合作伙伴、跨部门 / 跨机构的系统，实现跨组织的业务协同与数据互通'),
('基础设施与运维管控系统', '支撑所有上层系统稳定运行的底层技术平台与运维管理系统，是 IT 体系的 "数字地基"'),
('合规与风控系统', '满足监管合规要求、防范经营 / 业务 / 技术风险的系统，是机构的 "安全与合规防线"');

-- 初始化默认作品数据
INSERT INTO `pm_project` (`title`, `description`, `cover_image`, `detail_link`, `github_link`, `category_id`, `sort`) VALUES
('智能客服系统', '基于AI技术的智能客服系统，支持自然语言处理和多渠道接入', 'https://example.com/uploads/ai-customer-service.jpg', 'https://example.com/projects/ai-customer-service', 'https://github.com/example/ai-customer-service', 3, 1),
('电商平台重构', '对现有电商平台进行全面重构，提升用户体验和系统性能', 'https://example.com/uploads/ecommerce-platform.jpg', 'https://example.com/projects/ecommerce-platform', 'https://github.com/example/ecommerce-platform', 1, 2),
('健康管理App', '个人健康数据管理应用，支持运动追踪和健康建议', 'https://example.com/uploads/health-app.jpg', 'https://example.com/projects/health-app', 'https://github.com/example/health-app', 3, 3),
('企业内部协作系统', '为企业打造的内部协作平台，提高团队工作效率', 'https://example.com/uploads/collaboration-system.jpg', 'https://example.com/projects/collaboration-system', 'https://github.com/example/collaboration-system', 2, 4),
('在线教育平台', '提供在线课程和学习管理功能的教育平台', 'https://example.com/uploads/online-education.jpg', 'https://example.com/projects/online-education', 'https://github.com/example/online-education', 1, 5);
