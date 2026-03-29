# 产品经理个人作品展示系统

## 项目简介

产品经理个人作品展示系统是一个专为产品经理设计的个人作品管理平台，支持作品的添加、编辑、删除和展示，以及个人简历的管理和文件上传功能。系统采用现代化的全栈技术栈，提供了美观、响应式的用户界面，以及安全、高效的后端服务。

## 技术栈

### 前端
- Vue 3 (Composition API)
- Vite 4.5+
- Axios
- 原生 CSS

### 后端
- Spring Boot 3.2.0
- Spring Data JPA
- MySQL 8.0+
- SpringDoc OpenAPI
- Lombok

## 功能特性

1. **用户认证**：
   - 支持账号密码登录
   - 包含验证码验证
   - 基于角色的权限控制

2. **作品管理**：
   - 新增作品（标题、描述、封面图片、详情链接、GitHub地址、排序）
   - 编辑作品
   - 删除作品（带确认弹窗）
   - 作品列表展示
   - 分类筛选功能
   - 搜索功能
   - 作品创建时间显示

3. **个人简历管理**：
   - 新增简历（个人信息、教育背景、工作经验、技能等）
   - 编辑简历
   - 删除简历
   - 简历文件上传（支持PDF、Word格式）
   - 简历文件删除（仅admin权限）
   - 上传文件后展示原始文件名

4. **模板库管理**：
   - 模板列表展示
   - 模板新增
   - 模板编辑
   - 模板删除
   - 模板导入（支持多种格式）
   - 模板预览
   - 模板分类管理
   - 模板搜索和筛选

5. **GitHub链接**：
   - 支持添加和展示GitHub地址
   - 带有复制功能

6. **AI集成**：
   - 支持使用AI生成项目信息
   - 支持生成项目简介和封面图片

7. **响应式设计**：
   - 适配不同设备屏幕
   - 科技风界面

8. **API文档**：
   - 集成Swagger，提供API文档

## 项目结构

```
pm-portfolio-system/
├── backend/                # 后端代码
│   ├── src/main/java/com/pmportfolio/hub/  # 后端主要代码
│   │   ├── controller/     # 控制器
│   │   │   ├── AuthController.java       # 认证控制器
│   │   │   ├── ProjectController.java    # 作品控制器
│   │   │   ├── ResumeController.java     # 简历控制器
│   │   │   └── UploadController.java     # 上传控制器
│   │   ├── service/        # 服务层
│   │   │   ├── AuthService.java          # 认证服务
│   │   │   ├── ProjectService.java       # 作品服务
│   │   │   ├── ResumeService.java        # 简历服务
│   │   │   └── UploadService.java        # 上传服务
│   │   ├── model/          # 数据模型
│   │   │   ├── PmProject.java           # 作品模型
│   │   │   ├── PmResume.java            # 简历模型
│   │   │   ├── SysCategory.java         # 分类模型
│   │   │   └── SysUser.java             # 用户模型
│   │   ├── common/         # 公共组件
│   │   │   ├── CaptchaUtil.java         # 验证码工具
│   │   │   ├── CorsConfig.java          # CORS配置
│   │   │   ├── LoginInterceptor.java    # 登录拦截器
│   │   │   ├── SwaggerConfig.java       # Swagger配置
│   │   │   ├── ThreadLocalUtil.java      # ThreadLocal工具
│   │   │   └── WebMvcConfig.java        # WebMvc配置
│   │   └── repository/     # 数据访问层
│   │       ├── CategoryRepository.java  # 分类仓库
│   │       ├── PmResumeRepository.java  # 简历仓库
│   │       ├── ProjectRepository.java    # 作品仓库
│   │       └── UserRepository.java       # 用户仓库
│   ├── src/main/resources/ # 配置文件
│   │   ├── application.yml # 应用配置
│   │   └── schema.sql      # 数据库脚本
│   ├── uploads/            # 上传文件存储
│   └── pom.xml             # Maven依赖配置
├── frontend/               # 前端代码
│   ├── src/                # 前端主要代码
│   │   ├── components/     # 组件
│   │   │   ├── CozeModal.vue            # AI结果弹窗
│   │   │   ├── Dashboard.vue            # 仪表盘组件
│   │   │   ├── DeleteModal.vue          # 删除确认弹窗
│   │   │   ├── Header.vue               # 头部组件
│   │   │   ├── InviteForm.vue           # 邀请表单组件
│   │   │   ├── Login.vue                # 登录组件
│   │   │   ├── OrganizationForm.vue     # 组织表单组件
│   │   │   ├── OrganizationManager.vue  # 组织管理组件
│   │   │   ├── PackageForm.vue          # 套餐表单组件
│   │   │   ├── PermissionForm.vue       # 权限表单组件
│   │   │   ├── PermissionList.vue       # 权限列表组件
│   │   │   ├── PermissionManager.vue    # 权限管理组件
│   │   │   ├── ProjectModal.vue         # 作品弹窗
│   │   │   ├── Projects.vue             # 作品列表组件
│   │   │   ├── ResumeModal.vue          # 简历弹窗
│   │   │   ├── Resumes.vue              # 简历列表组件
│   │   │   ├── RoleAssignForm.vue       # 角色分配表单组件
│   │   │   ├── RoleForm.vue             # 角色表单组件
│   │   │   ├── RoleList.vue             # 角色列表组件
│   │   │   ├── RolePermission.vue       # 角色权限组件
│   │   │   ├── RoleTemplate.vue         # 角色模板组件
│   │   │   ├── TemplateEditor.vue       # 模板编辑组件
│   │   │   ├── TemplateManager.vue      # 模板管理组件
│   │   │   ├── TenantForm.vue           # 租户表单组件
│   │   │   ├── TenantManager.vue        # 租户管理组件
│   │   │   ├── UserForm.vue             # 用户表单组件
│   │   │   └── UserManager.vue          # 用户管理组件
│   │   ├── assets/         # 静态资源
│   │   │   └── style.css               # 全局样式
│   │   ├── utils/           # 工具类
│   │   │   ├── api.js                   # API客户端
│   │   │   └── utils.js                 # 工具函数
│   │   ├── App.vue         # 主应用
│   │   └── main.js         # 入口文件
│   ├── dist/               # 构建输出
│   ├── index.html          # 前端入口HTML
│   ├── package.json        # npm依赖配置
│   └── vite.config.js      # Vite配置
├── docs/                   # 项目文档
│   └── pm-portfolio-hub/   # 文档目录
└── schema.sql              # 数据库脚本
```

## 环境要求

- Java 17+
- Node.js 14+
- MySQL 8.0+
- Maven 3.6+

## 安装与部署

### 后端部署

1. **数据库准备**
   - 创建数据库 `pm_portfolio_hub`
   - 执行 `schema.sql` 脚本创建表结构和初始数据

2. **配置修改**
   - 修改 `backend/src/main/resources/application.yml` 中的数据库连接信息

3. **构建与运行**
   ```bash
   # 构建
   cd backend
   mvn clean package
   
   # 运行
   java -jar target/hub-1.0.0.jar
   ```

### 前端部署

1. **安装依赖**
   ```bash
   cd frontend
   npm install
   ```

2. **开发模式运行**
   ```bash
   npm run dev
   ```

3. **构建**
   ```bash
   npm run build
   ```

4. **部署**
   - 将 `dist` 目录部署到 web 服务器
   - 配置反向代理，将 `/api` 请求转发到后端服务

## 访问地址

- 前端开发地址：http://localhost:3001
- 后端API：http://localhost:8080
- API文档：http://localhost:8080/swagger-ui.html
- 线上地址：http://8.148.235.131

## 默认账号

- 管理员账号：admin / admin123
- 测试账号：test / test123

## 注意事项

1. 确保后端服务在8080端口运行
2. 确保前端配置中的API基础URL正确
3. 部署到生产环境时，建议修改默认账号密码
4. 上传的文件会存储在服务器的 `uploads` 目录，确保有足够的存储空间
5. 生产环境部署时，系统会自动使用线上服务器地址：http://8.148.235.131:8080
6. AI功能需要配置Coze API token，请在前端代码中修改相关配置

## 项目维护

- **前端开发**：在 `frontend` 目录运行 `npm run dev` 启动开发服务器
- **后端开发**：在 `backend` 目录运行 `mvn spring-boot:run` 启动开发服务器
- **代码规范**：遵循项目内的代码规范，保持代码风格一致
- **文档更新**：每次修改功能后，同步更新相关文档
- **数据库备份**：定期备份数据库，确保数据安全

## 文档结构

项目文档位于 `docs/pm-portfolio-hub/` 目录，包含以下文档：

- **DOCUMENT_REVIEW_pm-portfolio-hub.md**：文档读取记录
- **ALIGNMENT_pm-portfolio-hub.md**：需求对齐文档
- **CONSENSUS_pm-portfolio-hub.md**：需求共识文档
- **DESIGN_pm-portfolio-hub.md**：技术架构设计文档
- **PRD_pm-portfolio-hub.md**：产品需求文档
- **UI_SPEC_pm-portfolio-hub.md**：UI设计规范文档
- **TASK_pm-portfolio-hub.md**：原子任务文档
- **ACCEPTANCE_pm-portfolio-hub.md**：验收记录文档
- **FINAL_pm-portfolio-hub.md**：最终交付报告
- **TODO_pm-portfolio-hub.md**：待办事项文档

## 许可证

MIT

## 版本历史

- v1.0.0：初始版本，包含作品管理和简历管理功能
- v1.1.0：新增简历文件上传和删除功能
- v1.2.0：优化文件上传地址生成逻辑，确保线上环境使用正确的服务器地址
- v1.3.0：新增分类筛选和搜索功能
- v1.4.0：集成AI功能，支持生成项目信息
- v1.5.0：优化UI界面，添加必填项标识
- v1.6.0：新增模板库管理系统，支持模板的添加、编辑、删除、导入、预览和分类管理等功能

## 联系信息

如有问题或建议，请联系项目维护人员。
