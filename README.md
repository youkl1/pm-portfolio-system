# 产品经理个人作品展示系统

## 项目简介

产品经理个人作品展示系统是一个专为产品经理设计的个人作品管理平台，支持作品的添加、编辑、删除和展示，以及GitHub链接的管理。

## 技术栈

### 前端
- Vue 3
- Vite
- Axios

### 后端
- Spring Boot 3.2.0
- Spring Data JPA
- MySQL
- SpringDoc OpenAPI

## 功能特性

1. **用户登录**：支持账号密码登录，包含验证码验证
2. **作品管理**：
   - 新增作品（标题、描述、封面图片、详情链接、GitHub地址、排序）
   - 编辑作品
   - 删除作品（带确认弹窗）
   - 作品列表展示
3. **GitHub链接**：支持添加和展示GitHub地址，带有复制功能
4. **响应式设计**：适配不同设备屏幕
5. **API文档**：集成Swagger，提供API文档

## 项目结构

```
pm-portfolio-system/
├── backend/                # 后端代码
│   ├── src/main/java/com/pmportfolio/hub/  # 后端主要代码
│   ├── src/main/resources/ # 配置文件
│   └── pom.xml             # Maven依赖配置
├── frontend/               # 前端代码
│   ├── src/                # 前端主要代码
│   ├── index.html          # 前端入口HTML
│   ├── package.json        # npm依赖配置
│   └── vite.config.js      # Vite配置
├── docs/                   # 项目文档
└── schema.sql              # 数据库脚本
```

## 环境要求

- Java 17+
- Node.js 14+
- MySQL 8.0+

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

2. **构建**
   ```bash
   npm run build
   ```

3. **部署**
   - 将 `dist` 目录部署到 web 服务器
   - 配置反向代理，将 `/api` 请求转发到后端服务

## 访问地址

- 前端：http://localhost:3000
- 后端API：http://localhost:8080
- API文档：http://localhost:8080/swagger-ui.html

## 默认账号

- 用户名：test
- 密码：test123

## 注意事项

1. 确保后端服务在8080端口运行
2. 确保前端配置中的API基础URL正确
3. 部署到生产环境时，建议修改默认账号密码
4. 上传的图片会存储在服务器，确保有足够的存储空间

## 项目维护

- **前端开发**：在 `frontend` 目录运行 `npm run dev` 启动开发服务器
- **后端开发**：在 `backend` 目录运行 `mvn spring-boot:run` 启动开发服务器

## 许可证

MIT
