-- 创建系统分类表
CREATE TABLE IF NOT EXISTS `sys_category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` VARCHAR(100) NOT NULL COMMENT '分类名称',
  `description` TEXT COMMENT '分类描述',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统分类表';

-- 为 pm_project 表添加分类ID字段
ALTER TABLE `pm_project` ADD COLUMN IF NOT EXISTS `category_id` BIGINT COMMENT '分类ID';

-- 初始化系统分类数据（2026-03-22）
INSERT INTO `sys_category` (`name`, `description`) VALUES
('核心业务系统', '支撑机构主营业务运转、直接创造营收 / 履行核心法定职能的系统，是机构的 "业务生命线"'),
('管理支撑系统', '支撑内部管理、提升运营效率、规范组织管控流程的系统，不直接产生营收，但保障组织高效运转'),
('运营与营销系统', '面向客户 / 用户、市场推广、客户运营、销售转化的系统，直接影响机构获客、留存与营收增长'),
('数据与智能决策系统', '基于全量业务数据完成采集、加工、分析、建模，支撑经营决策与智能化应用的系统，是机构的 "数字大脑"'),
('协同与生态系统', '打通上下游产业链、合作伙伴、跨部门 / 跨机构的系统，实现跨组织的业务协同与数据互通'),
('基础设施与运维管控系统', '支撑所有上层系统稳定运行的底层技术平台与运维管理系统，是 IT 体系的 "数字地基"'),
('合规与风控系统', '满足监管合规要求、防范经营 / 业务 / 技术风险的系统，是机构的 "安全与合规防线"');

-- 创建个人简历表
CREATE TABLE IF NOT EXISTS `pm_resume` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '简历ID',
  `name` VARCHAR(100) NOT NULL COMMENT '姓名',
  `email` VARCHAR(100) COMMENT '邮箱',
  `phone` VARCHAR(20) COMMENT '电话',
  `education` LONGTEXT COMMENT '教育背景',
  `work_experience` LONGTEXT COMMENT '工作经验',
  `skills` LONGTEXT COMMENT '技能',
  `projects` LONGTEXT COMMENT '项目经验',
  `self_introduction` LONGTEXT COMMENT '自我介绍',
  `resume_file` VARCHAR(255) COMMENT '简历文件URL',
  `gender` VARCHAR(10) COMMENT '性别',
  `birth_date` VARCHAR(20) COMMENT '出生年月',
  `work_start_date` VARCHAR(20) COMMENT '参加工作时间',
  `job_status` VARCHAR(50) COMMENT '求职状态',
  `user_type` VARCHAR(50) COMMENT '牛人身份',
  `wechat` VARCHAR(100) COMMENT '微信号',
  `personal_advantage` LONGTEXT COMMENT '个人优势',
  `expected_position` LONGTEXT COMMENT '期望职位',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='个人简历表';

-- 2026-03-22 添加个人简历表字段

-- 插入模拟简历数据（2026-03-22）
INSERT INTO `pm_resume` (`name`, `email`, `phone`, `education`, `work_experience`, `skills`, `projects`, `self_introduction`, `gender`, `birth_date`, `work_start_date`, `job_status`, `user_type`, `wechat`, `personal_advantage`, `expected_position`) VALUES
('游开龙', '693*****09@qq.com', '185******25', '本科，计算机相关专业', '厦门普标医疗科技有限公司 产品经理（2023.05-至今）\n标签：B端产品、AI产品经理、物联网\n内容：主导医疗设备管理AI智能体0-1设计：搭建设备管理专属知识库与模板库，实现采购调研分析报告、供应商智能推荐等功能，同时设计AI数字人覆盖主流设备操作咨询，完成医疗AI产品与产业互联网设备管理场景的融合落地。\n负责CRM系统全流程优化：覆盖客户拜访、私域运营、需求跟进、报价单、成交、合同、开票回款全链路，打通销售、订单、服务、回款的产业互联网业务闭环，实现客户资源集中管控与业务数据化管理。\n搭建医疗设备效益分析系统：落地传感器设备数据平台，输出多维度设备效益分析报告，为医院设备配置优化、闲置设备盘活提供数据支撑，助力医疗产业互联网设备资源的高效配置。\n0-1搭建医疗EAM系统与物联网平台：完成电脑/手机/PDA多端产品设计，开展20余次医院实施调研，实现医疗设备从采购、使用、维护到报废的全生命周期信息化管控，打通医疗设备产业互联网核心数据链路。\n业绩：1.CRM系统优化后，客户跟进效率提升50%，销售线索转化率提升25%，维保服务复购率提升30%，医疗设备产业互联网业务闭环运营效率显著提升；\n2.2家试点医院交付设备效益分析系统，帮助医院降低低效运维成本15%，实现医疗设备资源的数字化优化配置；\n3.落地福建省3家医院物联网医疗装备系统核心版本，设备盘点效率提升60%，设备全生命周期追溯率100%，打通医疗设备产业互联网全流程数据链路。\n\n厦门石头城信息服务有限公司 产品经理（2019.07-2023.04）\n标签：数据产品、产品设计\n内容：深耕财务中台核心赛道：主导企业财务中台0-1架构设计与建设，覆盖收付款平台、收银台、发票中心、账户体系、资金管理等核心模块，结合产业互联网中台思维，实现企业财务数据与业务数据的全域打通。\n多部门对接协调：对接企业财务、法务、贸易、物流等多业务部门，开展全维度需求调研与分析，协助编写BRD文档，输出包含注释、流程图、交互逻辑的完整产品原型，搭建物流贸易产业互联网全流程信息化架构。\n产品设计：主导企业数据中台及贸易、物流系统等核心产品的架构设计与0-1建设。编写标准化PRD文档并组织产品、技术双评审，参与技术返讲，协同UI团队完成页面设计，保障财务中台、数据中台及产业互联网系统设计方案的精准落地。\n业绩：财务中台：完成与建设银行、平安银行等多平台对接，实现企业财务全流程数字化管理，财务管理效率提升50%，管理成本降低20%，为产业互联网财务数字化奠定核心基础；\n数据中台：落地MDM系统、ODS基础层建设、ETL数据治理及报表、大屏等数据应用，打破企业数据壁垒，支撑厦门本地物流贸易产业互联网企业业务高效决策；\n贸易系统：完成贸易系统（销售/采购订单）与物流系统（运单管理、调度中心）核心模块建设，实现厦门本地企业物流贸易产业互联网业务全流程信息化，运营效率提升40%，打通物流贸易从采购到配送的全链路数字化；\n\n厦门巨龙信息科技有限公司 Java（2018.08-2019.07）\n标签：Linux、中间件、后端开发\n内容：1.参与公司内部信息系统开发，负责后端业务逻辑和数据库设计。\n2.使用Java、SpringBoot、MyBatisPlus等技术，开发和维护系统功能和性能优化。\n3.负责公司数据中台的建设和维护，包括ETL流程、数据仓库、数据治理等方面。\n4.编写ETL脚本，实现数据的抽取、转换和加载，确保数据的准确性和完整性。\n5.熟悉SQL语言，能够熟练编写SQL查询和操作数据库。\n\n厦门中特医疗投资管理有限公司 Java（2016.03-2018.07）\n标签：CSS、SQL、Oracle\n内容：1.熟练掌握Java等编程语言，熟悉Spring、MyBatis等开发框架；\n2.熟悉MySQL、Oracle等数据库技术，能够熟练运用SQL语言进行数据处理；\n3.熟悉前端技术，熟练掌握HTML、CSS、JavaScript等技术；\n4.具有良好的沟通能力和团队协作能力，能够有效地与产品经理、设计师、测试人员等进行协作；\n5.具有丰富的产品策划经验，能够充分理解用户需求并提供专业的解决方案。', 'Java、SpringBoot、MyBatis、MySQL、Oracle、HTML、CSS、JavaScript、产品设计、项目管理、AI产品、数据中台、财务中台、物联网', '医疗设备管理AI智能体系统、CRM系统全流程优化、医疗设备效益分析系统、医疗EAM系统与物联网平台、企业财务中台、数据中台、贸易系统、物流系统', '拥有10年+产研全链路从业经验，兼具3年Java开发与7年B端/AI产品管理经验，兼备技术实现与产品设计双维度思维，可全流程主导产研项目从0到1落地。', '男', '1992-12', '2016-03', '离职-随时到岗', '职场人', 'yki******09', '全栈产研能力：拥有10年+产研全链路从业经验，兼具3年Java开发与7年B端/AI产品管理经验，兼备技术实现与产品设计双维度思维，可全流程主导产研项目从0到1落地。\n产品知识面广：深耕B端产业数字化转型，主导多个领域的产品设计（如AI智能体应用、CRM、TMS、ERP、数据中台、财务中台等）\n管理能力灵活：熟练掌握多种项目管理技巧，能够全面负责产研的整个生命周期。\n自我驱动力强：能够快速适应变化，并在不断学习中改进和提升自己的工作效率和质量。', 'AI产品经理 | 面议 | 厦门\n产品经理 | 面议 | 厦门');
