<template>
  <div class="app">
    <!-- 登录页面 -->
    <div v-if="!userInfo" class="login-container">
      <h1>产品经理个人作品展示系统</h1>
      <form @submit.prevent="login" class="login-form">
        <div class="form-group">
          <label for="username">用户名</label>
          <input type="text" id="username" v-model="loginForm.username" required>
        </div>
        <div class="form-group">
          <label for="password">密码</label>
          <input type="password" id="password" v-model="loginForm.password" required>
        </div>
        <div class="form-group">
          <label for="captcha">验证码</label>
          <div class="captcha-container">
            <input type="text" id="captcha" v-model="loginForm.captcha" required placeholder="请输入验证码">
            <img :src="captchaImage" @click="getCaptcha" alt="验证码" class="captcha-image">
          </div>
        </div>
        <button type="submit" class="login-btn">登录</button>
        <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
      </form>
    </div>
    
    <!-- 作品展示页面 -->
    <div v-else class="project-container">
      <header class="header">
        <h1>产品经理个人作品展示系统</h1>
        <div class="header-actions">
          <span>欢迎，{{ userInfo.username }} ({{ userInfo.role }})</span>
          <nav class="nav-menu">
            <button @click="switchTab('projects')" :class="{ active: activeTab === 'projects' }" class="nav-btn">作品管理</button>
            <button @click="switchTab('resumes')" :class="{ active: activeTab === 'resumes' }" class="nav-btn">个人简历</button>
          </nav>
          <button @click="logout" class="logout-btn">退出登录</button>
        </div>
      </header>
      
      <main class="main-content">
        <!-- 作品管理标签页 -->
        <div v-if="activeTab === 'projects'">
          <!-- 首页看板 -->
          <div class="dashboard">
            <div class="dashboard-card">
              <div class="dashboard-card-header">
                <h3>作品统计</h3>
              </div>
              <div class="dashboard-card-body">
                <div class="dashboard-stats">
                  <div class="stat-item">
                    <div class="stat-value">{{ projects.length }}</div>
                    <div class="stat-label">总作品数</div>
                  </div>
                  <div class="stat-item">
                    <div class="stat-value">{{ categories.length }}</div>
                    <div class="stat-label">分类数</div>
                  </div>
                  <div class="stat-item">
                    <div class="stat-value">{{ getProjectByCategory('核心业务系统') }}</div>
                    <div class="stat-label">核心业务系统</div>
                  </div>
                  <div class="stat-item">
                    <div class="stat-value">{{ getProjectByCategory('管理支撑系统') }}</div>
                    <div class="stat-label">管理支撑系统</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <div class="toolbar">
            <div class="toolbar-left">
              <h2>作品列表</h2>
              <div class="category-filter">
                <label>分类筛选：</label>
                <div class="category-checkbox-group">
                  <label class="category-checkbox-item">
                    <input type="checkbox" v-model="selectedCategoryIds" :value="''" @change="filterProjects">
                    <span>全选</span>
                  </label>
                  <label v-for="category in categories" :key="category.id" class="category-checkbox-item">
                    <input type="checkbox" v-model="selectedCategoryIds" :value="category.id.toString()" @change="filterProjects">
                    <span>{{ category.name }}</span>
                  </label>
                </div>
              </div>
            </div>
            <button v-if="userInfo.role === 'admin'" @click="showAddModal = true" class="add-btn">新增作品</button>
          </div>
          
          <div class="project-grid">
            <div v-for="project in projects" :key="project.id" class="project-card">
              <a :href="project.detailLink" target="_blank" class="project-cover-link">
                <div class="project-cover-container">
                  <img :src="project.coverImage" alt="封面" class="project-cover">
                  <div v-if="getCategoryName(project.categoryId)" class="project-category-badge">
                    {{ getCategoryName(project.categoryId) }}
                  </div>
                </div>
              </a>
              <div class="project-info">
                <h3 class="project-title">{{ project.title }}</h3>
                <div class="description-container" :data-description="project.description">
                  <p class="project-description">{{ project.description }}</p>
                  <div class="description-tooltip">
                    <div class="tooltip-content">{{ project.description }}</div>
                    <button class="tooltip-copy-btn" @click.stop="copyDescription(project.description)">
                      <span class="copy-icon">📋</span>
                    </button>
                  </div>
                </div>
                <div class="project-actions-row">
                  <a :href="project.detailLink" target="_blank" class="detail-link">查看详情</a>
                </div>
                <div class="project-meta">
                  <span class="project-created-at">{{ formatDate(project.createdAt) }}</span>
                </div>
                <div v-if="project.githubLink" class="github-container">
                  <div class="github-header">
                    <span class="github-icon">📂</span>
                    <span class="github-label">GitHub</span>
                  </div>
                  <div class="github-url-container">
                    <a :href="project.githubLink" target="_blank" class="github-url">{{ project.githubLink }}</a>
                    <button @click="copyGithubLink(project.githubLink)" class="copy-btn">
                      <span class="copy-icon">📋</span>
                    </button>
                  </div>
                </div>
              </div>
              <div v-if="userInfo.role === 'admin'" class="project-admin-actions">
                <button @click="editProject(project)" class="edit-btn">编辑</button>
                <button @click="showDeleteConfirm(project.id)" class="delete-btn">删除</button>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 个人简历标签页 -->
        <div v-else-if="activeTab === 'resumes'">
          <div class="toolbar">
            <div class="toolbar-left">
              <h2>个人简历</h2>
            </div>
            <button v-if="userInfo.role === 'admin'" @click="openAddResumeModal" class="add-btn">新增简历</button>
          </div>
          
          <div class="resume-container">
            <div v-for="resume in resumes" :key="resume.id" class="resume-card">
              <!-- 简历头部 -->
              <div class="resume-header">
                <div class="resume-header-top">
                  <h3 class="resume-name">{{ resume.name }}</h3>
                  <div v-if="resume.jobStatus" class="job-status-badge">{{ resume.jobStatus }}</div>
                </div>
                <div class="resume-contact">
                  <div v-if="resume.email" class="contact-item">
                    <span class="contact-icon">📧</span>
                    <span class="contact-value">{{ resume.email }}</span>
                  </div>
                  <div v-if="resume.phone" class="contact-item">
                    <span class="contact-icon">📞</span>
                    <span class="contact-value">{{ resume.phone }}</span>
                  </div>
                  <div v-if="resume.wechat" class="contact-item">
                    <span class="contact-icon">💬</span>
                    <span class="contact-value">{{ resume.wechat }}</span>
                  </div>
                </div>
                <div class="resume-basic-info">
                  <div v-if="resume.gender" class="basic-info-item">
                    <span class="info-label">性别：</span>
                    <span class="info-value">{{ resume.gender }}</span>
                  </div>
                  <div v-if="resume.birthDate" class="basic-info-item">
                    <span class="info-label">出生年月：</span>
                    <span class="info-value">{{ resume.birthDate }}</span>
                  </div>
                  <div v-if="resume.workStartDate" class="basic-info-item">
                    <span class="info-label">参加工作时间：</span>
                    <span class="info-value">{{ resume.workStartDate }}</span>
                  </div>
                  <div v-if="resume.userType" class="basic-info-item">
                    <span class="info-label">身份：</span>
                    <span class="info-value">{{ resume.userType }}</span>
                  </div>
                </div>
              </div>
              
              <!-- 简历内容 -->
              <div class="resume-content">
                <!-- 个人优势 -->
                <div v-if="resume.personalAdvantage" class="resume-section">
                  <div class="section-header">
                    <div class="section-icon">✨</div>
                    <h4 class="section-title">个人优势</h4>
                  </div>
                  <div class="section-content">
                    <p v-for="(advantage, index) in resume.personalAdvantage.split('\n')" :key="index" class="advantage-item">
                      {{ advantage }}
                    </p>
                  </div>
                </div>
                
                <!-- 教育背景 -->
                <div v-if="resume.education" class="resume-section">
                  <div class="section-header">
                    <div class="section-icon">🎓</div>
                    <h4 class="section-title">教育背景</h4>
                  </div>
                  <div class="section-content">
                    <p>{{ resume.education }}</p>
                  </div>
                </div>
                
                <!-- 工作经验 -->
                <div v-if="resume.workExperience" class="resume-section">
                  <div class="section-header">
                    <div class="section-icon">💼</div>
                    <h4 class="section-title">工作经验</h4>
                  </div>
                  <div class="section-content">
                    <div class="work-experience-list">
                      <div v-for="(experience, index) in resume.workExperience.split('\n\n')" :key="index" class="work-experience-item">
                        <p>{{ experience }}</p>
                      </div>
                    </div>
                  </div>
                </div>
                
                <!-- 技能 -->
                <div v-if="resume.skills" class="resume-section">
                  <div class="section-header">
                    <div class="section-icon">🛠️</div>
                    <h4 class="section-title">技能</h4>
                  </div>
                  <div class="section-content">
                    <div class="skills-tag-list">
                      <span v-for="(skill, index) in resume.skills.split('、')" :key="index" class="skill-tag">
                        {{ skill }}
                      </span>
                    </div>
                  </div>
                </div>
                
                <!-- 期望职位 -->
                <div v-if="resume.expectedPosition" class="resume-section">
                  <div class="section-header">
                    <div class="section-icon">🎯</div>
                    <h4 class="section-title">期望职位</h4>
                  </div>
                  <div class="section-content">
                    <p v-for="(position, index) in resume.expectedPosition.split('\n')" :key="index" class="position-item">
                      {{ position }}
                    </p>
                  </div>
                </div>
                
                <!-- 简历文件 -->
                <div v-if="resume.resumeFile" class="resume-section">
                  <div class="section-header">
                    <div class="section-icon">📄</div>
                    <h4 class="section-title">简历文件</h4>
                  </div>
                  <div class="section-content">
                    <a :href="resume.resumeFile" target="_blank" class="resume-file-link">
                      <span class="file-icon">📁</span>
                      <span>{{ resume.resumeFileName || '查看简历文件' }}</span>
                    </a>
                  </div>
                </div>
              </div>
              
              <!-- 操作按钮 -->
              <div v-if="userInfo.role === 'admin'" class="resume-actions">
                <button @click="editResume(resume)" class="edit-btn">
                  <span class="btn-icon">✏️</span>
                  <span>编辑</span>
                </button>
                <button @click="showDeleteResumeConfirm(resume.id)" class="delete-btn">
                  <span class="btn-icon">🗑️</span>
                  <span>删除</span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </main>
      
      <!-- 新增/编辑弹窗 -->
      <div v-if="showAddModal || showEditModal" class="modal-overlay">
        <div class="modal">
          <div class="modal-header">
            <h3>{{ showEditModal ? '编辑作品' : '新增作品' }}</h3>
            <button @click="closeModal" class="close-btn">&times;</button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="saveProject" class="project-form">
              <div class="form-group">
                <label for="title">标题</label>
                <div class="input-with-button">
                  <input type="text" id="title" v-model="projectForm.title" required>
                  <button type="button" @click="generateTitle" class="ai-button" :disabled="isGeneratingTitle">
                    {{ isGeneratingTitle ? '生成中...' : 'AI' }}
                  </button>
                </div>
              </div>
              <div class="form-group">
                <label for="description">描述</label>
                <textarea id="description" v-model="projectForm.description"></textarea>
              </div>
              <div class="form-group">
                <label>封面图片</label>
                <div class="upload-area" @click="triggerFileInput">
                  <input type="file" ref="fileInput" accept="image/jpeg,image/png" @change="handleFileChange" style="display: none;">
                  <img v-if="projectForm.coverImage" :src="projectForm.coverImage" alt="预览" class="preview-image">
                  <div v-else class="upload-placeholder">点击上传图片</div>
                </div>
              </div>
              <div class="form-group">
                <label for="detailLink">详情链接</label>
                <input type="url" id="detailLink" v-model="projectForm.detailLink">
              </div>
              <div class="form-group">
                <label for="githubLink">GitHub地址</label>
                <input type="url" id="githubLink" v-model="projectForm.githubLink">
              </div>
              <div class="form-group">
                <label for="categoryId">系统分类</label>
                <select id="categoryId" v-model="projectForm.categoryId" class="category-select">
                  <option value="">请选择分类</option>
                  <option v-for="category in categories" :key="category.id" :value="category.id">{{ category.name }}</option>
                </select>
              </div>
              <div class="form-group">
                <label for="sort">排序</label>
                <input type="number" id="sort" v-model.number="projectForm.sort" min="0">
              </div>
              <div class="form-actions">
                <button type="button" @click="closeModal" class="cancel-btn">取消</button>
                <button type="submit" class="save-btn">保存</button>
              </div>
            </form>
          </div>
        </div>
      </div>
      
      <!-- 删除确认弹窗 -->
      <div v-if="showDeleteModal" class="modal-overlay">
        <div class="modal delete-modal">
          <div class="modal-header">
            <h3>确认删除</h3>
            <button @click="closeDeleteModal" class="close-btn">&times;</button>
          </div>
          <div class="modal-body">
            <p class="delete-message">确定要删除这个作品吗？此操作不可撤销。</p>
            <div class="form-actions">
              <button type="button" @click="closeDeleteModal" class="cancel-btn">取消</button>
              <button type="button" @click="confirmDelete" class="delete-confirm-btn">确认删除</button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 成功提示弹窗 -->
      <div v-if="showSuccessModal" class="modal-overlay">
        <div class="modal success-modal">
          <div class="modal-header">
            <h3>操作成功</h3>
            <button @click="closeSuccessModal" class="close-btn">&times;</button>
          </div>
          <div class="modal-body">
            <div class="success-content">
              <div class="success-icon">✓</div>
              <p class="success-message">{{ successMessage }}</p>
            </div>
            <div class="form-actions">
              <button type="button" @click="closeSuccessModal" class="save-btn">确定</button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Coze结果弹窗 -->
      <div v-if="showCozeModal" class="modal-overlay">
        <div class="modal coze-modal">
          <div class="modal-header">
            <h3>AI生成结果</h3>
            <button @click="!isGeneratingTitle && (showCozeModal = false)" :disabled="isGeneratingTitle" class="close-btn" :class="{ disabled: isGeneratingTitle }">
              &times;
            </button>
          </div>
          <div class="modal-body">
            <div class="coze-result">
              <div class="coze-section">
                <h4>项目简介</h4>
                <div class="coze-content">
                  {{ cozeResult.projectIntro }}
                  <div v-if="isGeneratingTitle" class="loading-indicator">
                    <div class="loading-spinner"></div>
                  </div>
                </div>
              </div>
              <div class="coze-section">
                <h4>封面图片</h4>
                <div class="coze-image-container">
                  <img v-if="cozeResult.coverImage" :src="cozeResult.coverImage" alt="封面图片" class="coze-image">
                  <div v-else class="coze-image-placeholder">无图片</div>
                </div>
              </div>
            </div>
            <div class="form-actions">
              <button type="button" @click="!isGeneratingTitle && (showCozeModal = false)" :disabled="isGeneratingTitle" class="cancel-btn" :class="{ disabled: isGeneratingTitle }">
                取消
              </button>
              <button type="button" @click="confirmCozeResult" :disabled="isGeneratingTitle" class="save-btn" :class="{ disabled: isGeneratingTitle }">
                确定
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 新增/编辑简历弹窗 -->
      <div v-if="showAddResumeModal || showEditResumeModal" class="modal-overlay">
        <div class="modal">
          <div class="modal-header">
            <h3>{{ showEditResumeModal ? '编辑简历' : '新增简历' }}</h3>
            <button @click="closeResumeModal" class="close-btn">&times;</button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="saveResume" class="resume-form">
              <div class="form-group">
                <label for="resumeName">姓名</label>
                <input type="text" id="resumeName" v-model="resumeForm.name" required>
              </div>
              <div class="form-group">
                <label for="resumeEmail">邮箱</label>
                <input type="email" id="resumeEmail" v-model="resumeForm.email">
              </div>
              <div class="form-group">
                <label for="resumePhone">电话</label>
                <input type="tel" id="resumePhone" v-model="resumeForm.phone">
              </div>
              <div class="form-group">
                <label for="resumeEducation">教育背景</label>
                <textarea id="resumeEducation" v-model="resumeForm.education"></textarea>
              </div>
              <div class="form-group">
                <label for="resumeWorkExperience">工作经验</label>
                <textarea id="resumeWorkExperience" v-model="resumeForm.workExperience"></textarea>
              </div>
              <div class="form-group">
                <label for="resumeSkills">技能</label>
                <textarea id="resumeSkills" v-model="resumeForm.skills"></textarea>
              </div>
              <div class="form-group">
                <label for="resumeProjects">项目经验</label>
                <textarea id="resumeProjects" v-model="resumeForm.projects"></textarea>
              </div>
              <div class="form-group">
                <label for="resumeSelfIntroduction">自我介绍</label>
                <textarea id="resumeSelfIntroduction" v-model="resumeForm.selfIntroduction"></textarea>
              </div>
              <div class="form-group">
                <label>简历文件</label>
                <div class="upload-area" @click="$refs.resumeFileInput.click()">
                  <input type="file" ref="resumeFileInput" accept="application/pdf,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document" @change="handleResumeFileChange" style="display: none;">
                  <div v-if="resumeForm.resumeFile" class="file-preview">
                    <div class="file-info">
                      <a :href="resumeForm.resumeFile" target="_blank">{{ resumeForm.resumeFileName || '查看已上传的文件' }}</a>
                    </div>
                    <button v-if="userInfo.role === 'admin'" @click.stop="removeResumeFile" class="delete-file-btn">删除</button>
                  </div>
                  <div v-else class="upload-placeholder">点击上传简历文件</div>
                </div>
              </div>
              <div class="form-row">
                <div class="form-group half">
                  <label for="resumeGender">性别</label>
                  <div class="gender-options">
                    <label class="radio-label">
                      <input type="radio" v-model="resumeForm.gender" value="男">
                      <span>男</span>
                    </label>
                    <label class="radio-label">
                      <input type="radio" v-model="resumeForm.gender" value="女">
                      <span>女</span>
                    </label>
                  </div>
                </div>
                <div class="form-group half">
                  <label for="resumeBirthDate">出生年月</label>
                  <input type="text" id="resumeBirthDate" v-model="resumeForm.birthDate" placeholder="例如：1992-12">
                </div>
              </div>
              <div class="form-row">
                <div class="form-group half">
                  <label for="resumeWorkStartDate">参加工作时间</label>
                  <input type="text" id="resumeWorkStartDate" v-model="resumeForm.workStartDate" placeholder="例如：2016-03">
                </div>
                <div class="form-group half">
                  <label for="resumeJobStatus">求职状态</label>
                  <select id="resumeJobStatus" v-model="resumeForm.jobStatus">
                    <option value="">请选择</option>
                    <option value="离职-随时到岗">离职-随时到岗</option>
                    <option value="在职-考虑机会">在职-考虑机会</option>
                    <option value="在职-不考虑">在职-不考虑</option>
                  </select>
                </div>
              </div>
              <div class="form-row">
                <div class="form-group half">
                  <label for="resumeUserType">牛人身份</label>
                  <select id="resumeUserType" v-model="resumeForm.userType">
                    <option value="">请选择</option>
                    <option value="职场人">职场人</option>
                    <option value="学生">学生</option>
                    <option value="自由职业">自由职业</option>
                  </select>
                </div>
                <div class="form-group half">
                  <label for="resumeWechat">微信号</label>
                  <input type="text" id="resumeWechat" v-model="resumeForm.wechat" placeholder="选填">
                </div>
              </div>
              <div class="form-group">
                <label for="resumePersonalAdvantage">个人优势</label>
                <textarea id="resumePersonalAdvantage" v-model="resumeForm.personalAdvantage" rows="4"></textarea>
              </div>
              <div class="form-group">
                <label for="resumeExpectedPosition">期望职位</label>
                <textarea id="resumeExpectedPosition" v-model="resumeForm.expectedPosition" rows="2"></textarea>
              </div>
              <div class="form-actions">
                <button type="button" @click="closeResumeModal" class="cancel-btn">取消</button>
                <button type="submit" class="save-btn">保存</button>
              </div>
            </form>
          </div>
        </div>
      </div>
      
      <!-- 删除简历确认弹窗 -->
      <div v-if="showDeleteResumeModal" class="modal-overlay">
        <div class="modal delete-modal">
          <div class="modal-header">
            <h3>确认删除</h3>
            <button @click="closeDeleteResumeModal" class="close-btn">&times;</button>
          </div>
          <div class="modal-body">
            <p class="delete-message">确定要删除这个简历吗？此操作不可撤销。</p>
            <div class="form-actions">
              <button type="button" @click="closeDeleteResumeModal" class="cancel-btn">取消</button>
              <button type="button" @click="confirmDeleteResume" class="delete-confirm-btn">确认删除</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import config from './config'

// 创建axios实例
const apiClient = axios.create({
  baseURL: config.apiBaseUrl,
  withCredentials: true
})

// 添加响应拦截器处理未登录情况
apiClient.interceptors.response.use(
  response => response,
  error => {
    if (error.response && error.response.status === 401) {
      // 未登录，跳转到登录页面
      localStorage.removeItem('userInfo')
      window.location.reload()
    }
    return Promise.reject(error)
  }
)

export default {
  name: 'App',
  data() {
    return {
      userInfo: null,
      loginForm: {
        username: 'test',
        password: 'test123',
        captcha: ''
      },
      captchaImage: '',
      errorMessage: '',
      projects: [],
      categories: [],
      selectedCategoryIds: [],
      allProjects: [], // 存储所有作品，用于筛选
      showAddModal: false,
      showEditModal: false,
      showDeleteModal: false,
      showSuccessModal: false,
      successMessage: '',
      currentProjectId: null,
      isGeneratingTitle: false,
      showCozeModal: false,
      cozeResult: {
        projectIntro: '',
        coverImage: ''
      },
      projectForm: {
        title: 'test',
        description: '',
        coverImage: '',
        detailLink: '',
        githubLink: '',
        categoryId: null,
        sort: 0
      },
      // 个人简历相关
      activeTab: 'projects',
      resumes: [],
      showAddResumeModal: false,
      showEditResumeModal: false,
      showDeleteResumeModal: false,
      currentResumeId: null,
      resumeForm: {
        name: '',
        email: '',
        phone: '',
        education: '',
        workExperience: '',
        skills: '',
        projects: '',
        selfIntroduction: '',
        resumeFile: '',
        resumeFileName: '',
        gender: '',
        birthDate: '',
        workStartDate: '',
        jobStatus: '',
        userType: '',
        wechat: '',
        personalAdvantage: '',
        expectedPosition: ''
      }
    }
  },
  mounted() {
    // 检查本地存储中的用户信息
    const storedUser = localStorage.getItem('userInfo')
    if (storedUser) {
      try {
        this.userInfo = JSON.parse(storedUser)
        this.getCategories()
        this.getProjects()
        this.getResumes()
      } catch (error) {
        console.error('解析用户信息失败:', error)
        localStorage.removeItem('userInfo')
        this.getCaptcha()
      }
    } else {
      // 未登录时获取验证码
      this.getCaptcha()
    }
  },
  methods: {
    // 获取验证码
    async getCaptcha() {
      try {
        const response = await apiClient.get('/api/auth/captcha')
        if (response.data.code === 200) {
          this.captchaImage = response.data.data.image
        } else {
          this.errorMessage = '获取验证码失败'
        }
      } catch (error) {
        this.errorMessage = '获取验证码失败，请重试'
      }
    },
    
    // 登录
    async login() {
      try {
        const response = await apiClient.post('/api/auth/login', this.loginForm)
        if (response.data.code === 200) {
          this.userInfo = response.data.data
          localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
          this.errorMessage = ''
          this.getCategories()
          this.getProjects()
        } else {
          this.errorMessage = response.data.message
          // 登录失败时重新获取验证码
          this.getCaptcha()
        }
      } catch (error) {
        this.errorMessage = '登录失败，请重试'
        // 登录失败时重新获取验证码
        this.getCaptcha()
      }
    },
    
    // 退出登录
    logout() {
      this.userInfo = null
      localStorage.removeItem('userInfo')
      this.getCaptcha()
    },
    
    // 获取分类列表
    async getCategories() {
      try {
        const response = await apiClient.get('/api/projects/categories')
        if (response.data.code === 200) {
          this.categories = response.data.data
        }
      } catch (error) {
        console.error('获取分类列表失败:', error)
      }
    },
    
    // 获取作品列表
    async getProjects() {
      try {
        const response = await apiClient.get('/api/projects')
        console.log('获取到的作品列表:', response.data.data)
        if (response.data.code === 200) {
          this.allProjects = response.data.data
          this.filterProjects()
        }
      } catch (error) {
        console.error('获取作品列表失败:', error)
      }
    },
    
    // 筛选作品
    filterProjects() {
      // 处理全选逻辑
      if (this.selectedCategoryIds.includes('')) {
        // 全选被选中，清空其他选择
        this.selectedCategoryIds = ['']
        this.projects = this.allProjects
      } else if (this.selectedCategoryIds.length === 0) {
        // 没有选择任何分类，显示所有作品
        this.projects = this.allProjects
      } else {
        // 筛选选中的分类
        this.projects = this.allProjects.filter(project => 
          this.selectedCategoryIds.includes(project.categoryId.toString())
        )
      }
    },
    
    // 切换分类
    changeCategory(categoryId) {
      this.selectedCategoryId = categoryId
      this.getProjects()
    },
    
    // 触发文件选择
    triggerFileInput() {
      this.$refs.fileInput.click()
    },
    
    // 处理文件选择
    async handleFileChange(event) {
      const file = event.target.files[0]
      if (file) {
        const formData = new FormData()
        formData.append('file', file)
        try {
          const response = await apiClient.post('/api/upload/cover', formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          })
          if (response.data.code === 200) {
            this.projectForm.coverImage = response.data.data.url
          }
        } catch (error) {
          console.error('上传图片失败:', error)
        }
      }
    },
    
    // 打开编辑弹窗
    editProject(project) {
      this.currentProjectId = project.id
      this.projectForm = {
        title: project.title,
        description: project.description,
        coverImage: project.coverImage,
        detailLink: project.detailLink,
        githubLink: project.githubLink || '',
        categoryId: project.categoryId || null,
        sort: project.sort
      }
      this.showEditModal = true
    },
    
    // 保存作品
    async saveProject() {
      try {
        console.log('发送的projectForm:', this.projectForm)
        let response
        if (this.showEditModal) {
          response = await apiClient.put(`/api/projects/${this.currentProjectId}`, this.projectForm)
        } else {
          response = await apiClient.post('/api/projects', this.projectForm)
        }
        console.log('接收到的响应:', response.data)
        if (response.data.code === 200) {
          this.closeModal()
          this.getProjects()
        }
      } catch (error) {
        console.error('保存作品失败:', error)
      }
    },
    
    // 显示删除确认弹窗
    showDeleteConfirm(id) {
      this.currentProjectId = id
      this.showDeleteModal = true
    },
    
    // 关闭删除确认弹窗
    closeDeleteModal() {
      this.showDeleteModal = false
      this.currentProjectId = null
    },
    
    // 确认删除
    async confirmDelete() {
      try {
        const response = await apiClient.delete(`/api/projects/${this.currentProjectId}`)
        if (response.data.code === 200) {
          this.getProjects()
          this.closeDeleteModal()
          this.successMessage = '删除成功！'
          this.showSuccessModal = true
        }
      } catch (error) {
        console.error('删除作品失败:', error)
        this.successMessage = '删除失败，请重试'
        this.showSuccessModal = true
      }
    },
    
    // 关闭弹窗
    closeModal() {
      this.showAddModal = false
      this.showEditModal = false
      this.currentProjectId = null
      this.projectForm = {
        title: '',
        description: '',
        coverImage: '',
        detailLink: '',
        githubLink: '',
        categoryId: null,
        sort: 0
      }
    },
    
    // 关闭成功提示弹窗
    closeSuccessModal() {
      this.showSuccessModal = false
      this.successMessage = ''
    },
    
    // 复制GitHub链接
    copyGithubLink(url) {
      navigator.clipboard.writeText(url).then(() => {
        this.successMessage = 'GitHub链接已复制到剪贴板！'
        this.showSuccessModal = true
      }).catch(err => {
        console.error('复制失败:', err)
        this.successMessage = '复制失败，请手动复制'
        this.showSuccessModal = true
      })
    },
    
    // 复制项目描述
    copyDescription(description) {
      navigator.clipboard.writeText(description).then(() => {
        this.successMessage = '项目描述已复制到剪贴板！'
        this.showSuccessModal = true
      }).catch(err => {
        console.error('复制失败:', err)
        this.successMessage = '复制失败，请手动复制'
        this.showSuccessModal = true
      })
    },
    
    // AI生成项目信息
    async generateTitle() {
      this.isGeneratingTitle = true
      // 直接显示弹窗
      this.showCozeModal = true
      // 清空之前的结果
      this.cozeResult = {
        projectIntro: 'AI正在生成项目信息...',
        coverImage: ''
      }
      
      try {
        const url = "/coze-api/stream_run";
        const headers = {
          "Authorization": "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjJlYzY4MDQ4LTUwN2MtNDgzOS1hZmQxLTM1YmJhODcyMmRkZCJ9.eyJpc3MiOiJodHRwczovL2FwaS5jb3plLmNuIiwiYXVkIjpbIjFiUTlaT1hRRmJ3cDJ0QXRiaTQxb1lVRmJ2SXNBOTIxIl0sImV4cCI6ODIxMDI2Njg3Njc5OSwiaWF0IjoxNzc0MzU0NzEwLCJzdWIiOiJzcGlmZmU6Ly9hcGkuY296ZS5jbi93b3JrbG9hZF9pZGVudGl0eS9pZDo3NjIwNzUwMjM3MzAwMTYyNTc5Iiwic3JjIjoiaW5ib3VuZF9hdXRoX2FjY2Vzc190b2tlbl9pZDo3NjIwNzk1NDU0MTM3MDQwOTM4In0.Qxg97tC8NoNGs7Ddk4rle1MsTvj1uLPyIRRSEw0otqgaZULPoLRKX7ZoqJyviP8WKBkHubQxVh_FT9G88TkRR4yXmm1M4HYFxAxg117QS7rxqvpYw5MWBkmfSVKMnP66qQU2OGpMrbPfaJbosfAP8e80DjtVR0Kl9V4CSB1JSogebgWMCzf4cI_3UNIjNgBGXO5WrhM5NWzOrtskPrwE2vU5kaLol0Wx1azp3s0cjW91Y1cAkUiaGeRuozFgi8pFeTXKoxVlc4fw2z-O8qej9TWeG-dJPtf_YM-EW4kDidDL1Z_z6FkjPsnv88xTLRrGJ4EEoPQTPFzK66rE514sjw",
          "Content-Type": "application/json",
          "Accept": "text/event-stream"
        };
        
        // 将标题作为参数传入
        const prompt = this.projectForm.title || "为产品经理个人作品生成项目信息";
        
        // 增加请求时长
        const controller = new AbortController();
        const timeoutId = setTimeout(() => {
          controller.abort();
        }, 120000); // 120秒超时
        
        const res = await fetch(url, {
          method: "POST",
          headers,
          body: JSON.stringify({
            "content": {
              "query": {
                "prompt": [
                  {
                    "type": "text",
                    "content": {
                      "text": prompt
                    }
                  }
                ]
              }
            },
            "type": "query",
            "session_id": "tY0VUHcfW9yADT5sajLUx",
            "project_id": "7620746694535381034"
          }),
          signal: controller.signal
        });
        
        clearTimeout(timeoutId);
        
        if (!res.ok) {
          const errText = await res.text();
          this.cozeResult.projectIntro = '生成项目信息失败，请重试';
          return;
        }
        
        if (!res.body) {
          this.cozeResult.projectIntro = '生成项目信息失败，请重试';
          return;
        }
        
        const reader = res.body.getReader();
        const decoder = new TextDecoder();
        let buffer = "";
        let fullContent = "";
        let lastUpdateTime = 0;
        const updateInterval = 100; // 限制更新频率
        
        while (true) {
          try {
            const { done, value } = await reader.read();
            
            if (done) break;
            
            if (value) {
              buffer += decoder.decode(value, { stream: true });
              
              // 处理接收到的数据
              const blocks = buffer.split("\n\n");
              buffer = blocks.pop() ?? "";
              
              for (const block of blocks) {
                const dataLines = block
                  .split("\n")
                  .filter(line => line.startsWith("data:"))
                  .map(line => line.slice(5).trim());
                
                if (dataLines.length === 0) continue;
                
                for (const dataLine of dataLines) {
                  if (dataLine === '[DONE]') {
                    // 流结束标记
                    continue;
                  }
                  
                  try {
                    const parsed = JSON.parse(dataLine);
                    
                    // 处理不同类型的数据
                    if (parsed.type === 'answer' && parsed.content && parsed.content.answer) {
                      // 直接包含content.answer的情况
                      const answer = parsed.content.answer;
                      fullContent += answer;
                      
                      // 限制更新频率
                      const now = Date.now();
                      if (now - lastUpdateTime > updateInterval) {
                        this.cozeResult.projectIntro = fullContent;
                        lastUpdateTime = now;
                      }
                    } else if (parsed.content && parsed.content.answer) {
                      // 包含answer子字段的情况
                      const answer = parsed.content.answer;
                      fullContent += answer;
                      
                      // 限制更新频率
                      const now = Date.now();
                      if (now - lastUpdateTime > updateInterval) {
                        this.cozeResult.projectIntro = fullContent;
                        lastUpdateTime = now;
                      }
                    } else if (parsed.content && typeof parsed.content === 'string') {
                      // 直接包含content字段的情况
                      fullContent += parsed.content;
                      
                      // 限制更新频率
                      const now = Date.now();
                      if (now - lastUpdateTime > updateInterval) {
                        this.cozeResult.projectIntro = fullContent;
                        lastUpdateTime = now;
                      }
                    }
                  } catch (e) {
                    // 解析错误时不直接添加到内容中，避免出现[object Object]
                  }
                }
              }
            }
          } catch (readError) {
            // 继续处理，不要中断整个流程
          }
        }
        
        // 确保最后一次更新
        if (fullContent) {
          this.cozeResult.projectIntro = fullContent;
          // 解析返回的内容，提取项目简介和图片
          await this.parseCozeResult(fullContent);
        } else {
          this.cozeResult.projectIntro = '生成项目信息失败，请重试';
        }
      } catch (err) {
        this.cozeResult.projectIntro = '生成项目信息失败，请重试';
      } finally {
        this.isGeneratingTitle = false;
      }
    },
    
    // 解析Coze返回的结果
    async parseCozeResult(content) {
      // 尝试多种方式解析内容
      
      // 方式1：按标记分割
      if (content.includes('项目简介') && content.includes('封面图片')) {
        const projectIntroMatch = content.match(/项目简介[\s\S]*?封面图片/);
        const coverImageMatch = content.match(/封面图片[\s\S]*/);
        
        if (projectIntroMatch) {
          this.cozeResult.projectIntro = projectIntroMatch[0].replace('项目简介', '').replace('封面图片', '').trim();
        }
        
        if (coverImageMatch) {
          // 提取Markdown格式中的图片URL，即![封面图片](...)格式，处理URL中包含括号的情况
          const imageUrlMatch = coverImageMatch[0].match(/!\[.*?\]\((https?:\/\/[^\)]+)\)/);
          if (imageUrlMatch && imageUrlMatch[1]) {
            const aiImageUrl = imageUrlMatch[1];
            console.log('AI生成的封面图片链接:', aiImageUrl);
            // 下载并上传图片
            await this.downloadAndUploadImage(aiImageUrl);
          }
        }
      } 
      // 方式2：直接提取图片URL
      else {
        // 尝试直接从内容中提取Markdown格式的图片URL，即![封面图片](...)格式，处理URL中包含括号的情况
        const imageUrlMatch = content.match(/!\[封面图片\]\((https?:\/\/.*?)\)(?=\s|$)/);
        if (imageUrlMatch && imageUrlMatch[1]) {
          const aiImageUrl = imageUrlMatch[1];
          console.log('AI生成的封面图片链接:', aiImageUrl);
          // 下载并上传图片
          await this.downloadAndUploadImage(aiImageUrl);
          // 剩余部分作为项目简介
          this.cozeResult.projectIntro = content.replace(imageUrlMatch[0], '').trim();
        } else {
          // 全部作为项目简介
          this.cozeResult.projectIntro = content;
        }
      }
      
      // 确保项目简介不为空
      if (!this.cozeResult.projectIntro || this.cozeResult.projectIntro === 'AI正在生成项目信息...') {
        this.cozeResult.projectIntro = '生成的项目信息为空，请重试';
      }
    },
    
    // 通过后端API上传图片链接
    async downloadAndUploadImage(imageUrl) {
      try {
        console.log('开始处理图片链接:', imageUrl);
        
        // 通过后端API处理图片
        const response = await apiClient.post('/api/upload/cover-from-url', {
          imageUrl: imageUrl
        });
        
        if (response.data.code === 200) {
          const uploadedUrl = response.data.data.url;
          console.log('图片上传成功，上传后的链接:', uploadedUrl);
          this.cozeResult.coverImage = uploadedUrl;
        } else {
          console.error('上传图片失败:', response.data.message);
          // 如果上传失败，使用原始链接
          this.cozeResult.coverImage = imageUrl;
        }
      } catch (error) {
        console.error('处理图片失败:', error);
        // 如果出错，使用原始链接
        this.cozeResult.coverImage = imageUrl;
      }
    },
    
    // 确认Coze结果并反写回表单
    confirmCozeResult() {
      if (this.cozeResult.projectIntro) {
        this.projectForm.description = this.cozeResult.projectIntro;
      }
      if (this.cozeResult.coverImage) {
        this.projectForm.coverImage = this.cozeResult.coverImage;
      }
      this.showCozeModal = false;
      this.successMessage = '项目信息已更新！';
      this.showSuccessModal = true;
    },
    
    // 格式化日期
    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    },
    
    // 根据分类ID获取分类名称
    getCategoryName(categoryId) {
      if (!categoryId) return ''
      const category = this.categories.find(cat => cat.id === categoryId)
      return category ? category.name : ''
    },
    
    // 根据分类名称获取作品数量
    getProjectByCategory(categoryName) {
      const category = this.categories.find(cat => cat.name === categoryName)
      if (!category) return 0
      return this.projects.filter(project => project.categoryId === category.id).length
    },
    
    // 获取个人简历列表
    async getResumes() {
      try {
        const response = await apiClient.get('/api/resumes')
        if (response.data.code === 200) {
          this.resumes = response.data.data
        }
      } catch (error) {
        console.error('获取个人简历列表失败:', error)
      }
    },
    
    // 打开新增简历弹窗
    openAddResumeModal() {
      this.resumeForm = {
        name: '',
        email: '',
        phone: '',
        education: '',
        workExperience: '',
        skills: '',
        projects: '',
        selfIntroduction: '',
        resumeFile: '',
        resumeFileName: ''
      }
      this.showAddResumeModal = true
    },
    
    // 打开编辑简历弹窗
    editResume(resume) {
      this.currentResumeId = resume.id
      this.resumeForm = {
        name: resume.name,
        email: resume.email,
        phone: resume.phone,
        education: resume.education,
        workExperience: resume.workExperience,
        skills: resume.skills,
        projects: resume.projects,
        selfIntroduction: resume.selfIntroduction,
        resumeFile: resume.resumeFile,
        resumeFileName: resume.resumeFileName || '简历文件',
        gender: resume.gender,
        birthDate: resume.birthDate,
        workStartDate: resume.workStartDate,
        jobStatus: resume.jobStatus,
        userType: resume.userType,
        wechat: resume.wechat,
        personalAdvantage: resume.personalAdvantage,
        expectedPosition: resume.expectedPosition
      }
      this.showEditResumeModal = true
    },
    
    // 保存简历
    async saveResume() {
      try {
        let response
        if (this.showEditResumeModal) {
          response = await apiClient.put(`/api/resumes/${this.currentResumeId}`, this.resumeForm)
        } else {
          response = await apiClient.post('/api/resumes', this.resumeForm)
        }
        if (response.data.code === 200) {
          this.closeResumeModal()
          this.getResumes()
        }
      } catch (error) {
        console.error('保存个人简历失败:', error)
      }
    },
    
    // 显示删除简历确认弹窗
    showDeleteResumeConfirm(id) {
      this.currentResumeId = id
      this.showDeleteResumeModal = true
    },
    
    // 关闭简历弹窗
    closeResumeModal() {
      this.showAddResumeModal = false
      this.showEditResumeModal = false
      this.currentResumeId = null
      this.resumeForm = {
        name: '',
        email: '',
        phone: '',
        education: '',
        workExperience: '',
        skills: '',
        projects: '',
        selfIntroduction: '',
        resumeFile: ''
      }
    },
    
    // 关闭删除简历确认弹窗
    closeDeleteResumeModal() {
      this.showDeleteResumeModal = false
      this.currentResumeId = null
    },
    
    // 确认删除简历
    async confirmDeleteResume() {
      try {
        const response = await apiClient.delete(`/api/resumes/${this.currentResumeId}`)
        if (response.data.code === 200) {
          this.getResumes()
          this.closeDeleteResumeModal()
          this.successMessage = '删除成功！'
          this.showSuccessModal = true
        }
      } catch (error) {
        console.error('删除个人简历失败:', error)
        this.successMessage = '删除失败，请重试'
        this.showSuccessModal = true
      }
    },
    
    // 切换标签页
    switchTab(tab) {
      this.activeTab = tab
      if (tab === 'resumes') {
        this.getResumes()
      }
    },
    
    // 上传简历文件
    async handleResumeFileChange(event) {
      const file = event.target.files[0]
      if (file) {
        const formData = new FormData()
        formData.append('file', file)
        try {
          const response = await apiClient.post('/api/upload/resume', formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          })
          if (response.data.code === 200) {
            this.resumeForm.resumeFile = response.data.data.url
            this.resumeForm.resumeFileName = file.name
          }
        } catch (error) {
          console.error('上传简历文件失败:', error)
        }
      }
    },
    
    // 删除简历文件
    removeResumeFile() {
      this.resumeForm.resumeFile = ''
      this.resumeForm.resumeFileName = ''
    }
  }
}
</script>

<style scoped>
/* 全局样式 */
.app {
  min-height: 100vh;
  background-color: #f5f7fa;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

/* 登录页面 */
.login-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-container h1 {
  color: white;
  margin-bottom: 32px;
  font-size: 28px;
  font-weight: 600;
  text-align: center;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.login-form {
  background-color: white;
  padding: 32px;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  width: 100%;
  max-width: 420px;
  animation: slideIn 0.3s ease-out;
}

.form-group {
  margin-bottom: 24px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #212529;
  font-size: 14px;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  font-size: 16px;
  transition: all 0.2s ease;
  background-color: #fafafa;
}

/* 带按钮的输入框 */
.input-with-button {
  position: relative;
  display: flex;
  align-items: center;
}

.input-with-button input {
  flex: 1;
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
}

.ai-button {
  position: absolute;
  right: 0;
  top: 0;
  bottom: 0;
  padding: 0 16px;
  background-color: #165dff;
  color: white;
  border: none;
  border-top-right-radius: 8px;
  border-bottom-right-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 14px;
  font-weight: 500;
}

.ai-button:hover:not(:disabled) {
  background-color: #0a46d4;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(22, 93, 255, 0.2);
}

.ai-button:disabled {
  background-color: #c0c4cc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

/* Loading效果 */
.loading-indicator {
  display: inline-block;
  margin-left: 8px;
  vertical-align: middle;
}

.loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid #f3f3f3;
  border-top: 2px solid #165dff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 禁用状态 */
.close-btn.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.cancel-btn.disabled,
.save-btn.disabled {
  background-color: #c0c4cc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.cancel-btn.disabled:hover,
.save-btn.disabled:hover {
  background-color: #c0c4cc;
  transform: none;
  box-shadow: none;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #165dff;
  box-shadow: 0 0 0 2px rgba(22, 93, 255, 0.1);
  background-color: white;
}

.captcha-container {
  display: flex;
  gap: 12px;
  align-items: center;
}

.captcha-container input {
  flex: 1;
}

.captcha-image {
  width: 120px;
  height: 48px;
  cursor: pointer;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.captcha-image:hover {
  border-color: #165dff;
}

.form-group textarea {
  resize: vertical;
  min-height: 120px;
}

.login-btn {
  width: 100%;
  padding: 14px;
  background-color: #165dff;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  margin-top: 8px;
}

.login-btn:hover {
  background-color: #0a46d4;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(22, 93, 255, 0.2);
}

.error-message {
  margin-top: 16px;
  color: #f53f3f;
  font-size: 14px;
  text-align: center;
}

/* 作品展示页面 */
.project-container {
  min-height: 100vh;
}

.header {
  background: linear-gradient(135deg, #165dff 0%, #0f46c7 100%);
  color: white;
  padding: 24px 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(22, 93, 255, 0.15);
}

.header h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.logout-btn {
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 14px;
  font-weight: 500;
}

.logout-btn:hover {
  background-color: rgba(255, 255, 255, 0.3);
  transform: translateY(-1px);
}

.main-content {
  padding: 32px;
  max-width: 1440px;
  margin: 0 auto;
}

/* 首页看板 */
.dashboard {
  margin-bottom: 32px;
}

.dashboard-card {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: all 0.3s ease;
}

.dashboard-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.dashboard-card-header {
  padding: 20px 24px;
  border-bottom: 1px solid #f0f2f5;
  background-color: #f8f9fa;
}

.dashboard-card-header h3 {
  margin: 0;
  color: #212529;
  font-size: 16px;
  font-weight: 600;
}

.dashboard-card-body {
  padding: 24px;
}

.dashboard-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 24px;
}

.stat-item {
  text-align: center;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.stat-item:hover {
  background-color: #f0f5ff;
  transform: translateY(-2px);
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #165dff;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .dashboard-stats {
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }
  
  .stat-item {
    padding: 16px;
  }
  
  .stat-value {
    font-size: 24px;
  }
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  flex-wrap: wrap;
  gap: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e4e7ed;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 24px;
  flex-wrap: wrap;
}

.toolbar h2 {
  margin: 0;
  color: #212529;
  font-size: 20px;
  font-weight: 600;
}

.category-filter {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  flex-wrap: wrap;
}

.category-filter > label {
  font-size: 14px;
  font-weight: 500;
  color: #4e5969;
  margin-top: 4px;
}

.category-checkbox-group {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: center;
}

.category-checkbox-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #4e5969;
  cursor: pointer;
  transition: all 0.2s ease;
  padding: 4px 12px;
  border-radius: 16px;
  background-color: #f8f9fa;
  border: 1px solid #e4e7ed;
}

.category-checkbox-item:hover {
  background-color: #f0f5ff;
  border-color: #165dff;
  color: #165dff;
}

.category-checkbox-item input[type="checkbox"] {
  width: 16px;
  height: 16px;
  cursor: pointer;
}

.category-checkbox-item input[type="checkbox"]:checked + span {
  font-weight: 500;
  color: #165dff;
}

.category-checkbox-item input[type="checkbox"]:checked {
  accent-color: #165dff;
}

.add-btn {
  background-color: #00b42a;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 14px;
  font-weight: 500;
  box-shadow: 0 2px 4px rgba(0, 180, 42, 0.2);
}

.add-btn:hover {
  background-color: #00a126;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 180, 42, 0.25);
}

.project-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 24px;
}

.project-card {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
}

.project-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.project-cover-container {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.project-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.project-card:hover .project-cover {
  transform: scale(1.05);
}

/* 项目封面链接 */
.project-cover-link {
  display: block;
  text-decoration: none;
  color: inherit;
}

.project-cover-link:hover {
  text-decoration: none;
}

.project-category-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  background-color: rgba(22, 93, 255, 0.9);
  color: white;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 500;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.project-info {
  padding: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.project-title {
  margin: 0 0 12px 0;
  color: #212529;
  font-size: 18px;
  font-weight: 600;
  line-height: 1.4;
}

.project-description {
  margin: 0 0 16px 0;
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  flex: 1;
}

.description-container {
  position: relative;
  margin-bottom: 16px;
}

.description-tooltip {
  position: absolute;
  top: 100%;
  left: 0;
  width: 100%;
  max-width: 400px;
  background-color: rgba(0, 0, 0, 0.9);
  color: white;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 100;
  display: none;
  flex-direction: column;
  gap: 12px;
  margin-top: 8px;
}

.description-container:hover .description-tooltip {
  display: flex;
}

.tooltip-content {
  font-size: 14px;
  line-height: 1.5;
  white-space: pre-wrap;
  max-height: 300px;
  overflow-y: auto;
}

.tooltip-copy-btn {
  align-self: flex-end;
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
}

.tooltip-copy-btn:hover {
  background-color: rgba(255, 255, 255, 0.3);
  transform: translateY(-1px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .description-tooltip {
    max-width: 100%;
    left: 0;
    right: 0;
  }
}

.project-actions-row {
  margin-bottom: 16px;
}

.detail-link {
  color: #165dff;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  border-radius: 4px;
}

.detail-link:hover {
  color: #0a46d4;
  background-color: #f0f5ff;
}

.github-container {
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #e9ecef;
}

.github-header {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 8px;
}

.github-icon {
  font-size: 16px;
}

.github-label {
  font-size: 12px;
  font-weight: 600;
  color: #6c757d;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.github-url-container {
  display: flex;
  align-items: center;
  gap: 8px;
  background-color: white;
  padding: 10px 12px;
  border-radius: 6px;
  border: 1px solid #dee2e6;
}

.github-url {
  flex: 1;
  color: #24292e;
  text-decoration: none;
  font-size: 13px;
  font-family: 'Courier New', monospace;
  word-break: break-all;
  white-space: normal;
  line-height: 1.4;
}

.github-url:hover {
  color: #0366d6;
  text-decoration: underline;
}

/* 项目元信息 */
.project-meta {
  margin: 12px 0;
  font-size: 12px;
  color: #86909c;
  display: flex;
  align-items: center;
  gap: 12px;
}

.project-created-at {
  display: flex;
  align-items: center;
  gap: 4px;
}

.project-created-at::before {
  content: "📅";
  font-size: 12px;
}

.copy-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.copy-btn:hover {
  background-color: #e9ecef;
  transform: translateY(-1px);
}

.copy-icon {
  font-size: 14px;
}

.project-admin-actions {
  padding: 0 20px 20px;
  display: flex;
  gap: 12px;
}

.edit-btn {
  flex: 1;
  background-color: #ffc107;
  color: #212529;
  border: none;
  padding: 10px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 14px;
  font-weight: 500;
  box-shadow: 0 2px 4px rgba(255, 193, 7, 0.2);
}

.edit-btn:hover {
  background-color: #e0a800;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(255, 193, 7, 0.25);
}

.delete-btn {
  flex: 1;
  background-color: #f53f3f;
  color: white;
  border: none;
  padding: 10px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 14px;
  font-weight: 500;
  box-shadow: 0 2px 4px rgba(245, 63, 63, 0.2);
}

.delete-btn:hover {
  background-color: #e53935;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(245, 63, 63, 0.25);
}

/* 弹窗 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.2s ease-in-out;
}

.modal {
  background-color: white;
  border-radius: 12px;
  width: 100%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
  animation: slideIn 0.3s ease-in-out;
  outline: none;
}

.delete-modal {
  max-width: 400px;
}

.delete-message {
  text-align: center;
  padding: 32px 0;
  font-size: 16px;
  color: #212529;
  margin: 0;
  line-height: 1.5;
}

.success-modal {
  max-width: 400px;
}

.success-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 32px 0;
}

.success-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background-color: #00b42a;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 24px;
  box-shadow: 0 4px 12px rgba(0, 180, 42, 0.3);
}

/* Coze结果弹窗 */
.coze-modal {
  max-width: 600px;
}

.coze-result {
  margin-bottom: 24px;
}

.coze-section {
  margin-bottom: 24px;
}

.coze-section h4 {
  margin: 0 0 12px 0;
  color: #212529;
  font-size: 16px;
  font-weight: 600;
}

.coze-content {
  padding: 16px;
  background-color: #f8f9fa;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.6;
  color: #4e5969;
  min-height: 120px;
}

.coze-image-container {
  padding: 16px;
  background-color: #f8f9fa;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 200px;
}

.coze-image {
  max-width: 100%;
  max-height: 300px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.coze-image-placeholder {
  color: #86909c;
  font-size: 14px;
}

/* 确保弹窗在小屏幕上也能正常显示 */
@media (max-width: 768px) {
  .coze-modal {
    max-width: 90vw;
    margin: 20px;
  }
  
  .coze-content {
    min-height: 100px;
  }
  
  .coze-image-container {
    min-height: 150px;
  }
  
  .coze-image {
    max-height: 200px;
  }
}

.success-message {
  text-align: center;
  font-size: 16px;
  color: #212529;
  margin: 0;
  line-height: 1.5;
}

.delete-confirm-btn {
  padding: 12px 24px;
  background-color: #f53f3f;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 14px;
  font-weight: 500;
  box-shadow: 0 2px 4px rgba(245, 63, 63, 0.2);
}

.delete-confirm-btn:hover {
  background-color: #e53935;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(245, 63, 63, 0.2);
}

.delete-confirm-btn:active {
  transform: translateY(0);
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideIn {
  from {
    transform: translateY(-24px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.modal-header {
  padding: 24px;
  border-bottom: 1px solid #f0f2f5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
  color: #212529;
  font-size: 18px;
  font-weight: 600;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #86909c;
  transition: all 0.2s ease;
  padding: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
}

.close-btn:hover {
  color: #212529;
  background-color: #f0f2f5;
}

.modal-body {
  padding: 24px;
}

.project-form {
  width: 100%;
}

.upload-area {
  border: 2px dashed #e4e7ed;
  border-radius: 8px;
  padding: 32px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background-color: #fafafa;
}

.upload-area:hover {
  border-color: #165dff;
  background-color: #f0f5ff;
}

.preview-image {
  max-width: 100%;
  max-height: 200px;
  object-fit: contain;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.upload-placeholder {
  color: #86909c;
  font-size: 14px;
}

.file-preview {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background-color: #f8f9fa;
  border-radius: 6px;
  border: 1px solid #e9ecef;
}

.file-info {
  flex: 1;
}

.file-info a {
  color: #165dff;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
}

.file-info a:hover {
  color: #0a46d4;
  text-decoration: underline;
}

.delete-file-btn {
  background-color: #f53f3f;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.delete-file-btn:hover {
  background-color: #e53935;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(245, 63, 63, 0.2);
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #f0f2f5;
}

.cancel-btn {
  padding: 12px 24px;
  background-color: white;
  color: #4e5969;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 14px;
  font-weight: 500;
}

.cancel-btn:hover {
  background-color: #f5f7fa;
  border-color: #c0c4cc;
}

.save-btn {
  padding: 12px 24px;
  background-color: #165dff;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 14px;
  font-weight: 500;
  box-shadow: 0 2px 4px rgba(22, 93, 255, 0.2);
}

.save-btn:hover {
  background-color: #0a46d4;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(22, 93, 255, 0.2);
}

.save-btn:active {
  transform: translateY(0);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
    padding: 16px 24px;
  }
  
  .main-content {
    padding: 24px;
  }
  
  .toolbar {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .project-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .project-cover-container {
    height: 180px;
  }
  
  .project-info {
    padding: 16px;
  }
  
  .project-admin-actions {
    padding: 0 16px 16px;
  }
  
  .modal {
    margin: 20px;
  }
}

/* 导航菜单 */
.nav-menu {
  display: flex;
  gap: 8px;
  margin-left: 24px;
}

.nav-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 6px;
  background-color: rgba(255, 255, 255, 0.1);
  color: white;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 14px;
  font-weight: 500;
}

.nav-btn:hover {
  background-color: rgba(255, 255, 255, 0.2);
  transform: translateY(-1px);
}

.nav-btn.active {
  background-color: white;
  color: #165dff;
}

/* 个人简历 */
.resume-container {
  display: flex;
  justify-content: center;
  padding: 0 16px;
}

.resume-card {
  background-color: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: all 0.3s ease;
  max-width: 800px;
  width: 100%;
  margin-bottom: 32px;
}

.resume-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
}

/* 简历头部 */
.resume-header {
  padding: 32px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-bottom: 1px solid #dee2e6;
}

.resume-header-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 16px;
}

.resume-name {
  margin: 0;
  color: #212529;
  font-size: 24px;
  font-weight: 700;
  line-height: 1.2;
}

.job-status-badge {
  background-color: #165dff;
  color: white;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  box-shadow: 0 2px 8px rgba(22, 93, 255, 0.2);
}

.resume-contact {
  display: flex;
  gap: 24px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #495057;
}

.contact-icon {
  font-size: 16px;
}

.contact-value {
  font-weight: 500;
  color: #212529;
}

.resume-basic-info {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}

.basic-info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.info-label {
  color: #6c757d;
  font-weight: 500;
}

.info-value {
  color: #212529;
  font-weight: 500;
}

/* 简历内容 */
.resume-content {
  padding: 32px;
}

.resume-section {
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f0f2f5;
}

.resume-section:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.section-icon {
  font-size: 20px;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f0f5ff;
  border-radius: 8px;
  color: #165dff;
}

.section-title {
  margin: 0;
  color: #212529;
  font-size: 18px;
  font-weight: 600;
  line-height: 1.3;
}

.section-content {
  color: #495057;
  font-size: 14px;
  line-height: 1.6;
}

/* 个人优势 */
.advantage-item {
  margin: 0 0 12px 0;
  padding-left: 20px;
  position: relative;
}

.advantage-item:last-child {
  margin-bottom: 0;
}

.advantage-item::before {
  content: "•";
  position: absolute;
  left: 0;
  color: #165dff;
  font-weight: bold;
}

/* 工作经验 */
.work-experience-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.work-experience-item {
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 12px;
  border-left: 4px solid #165dff;
}

.work-experience-item p {
  margin: 0;
  line-height: 1.6;
}

/* 技能 */
.skills-tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.skill-tag {
  background-color: #f0f5ff;
  color: #165dff;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.skill-tag:hover {
  background-color: #165dff;
  color: white;
  transform: translateY(-2px);
}

/* 期望职位 */
.position-item {
  margin: 0 0 8px 0;
  padding-left: 20px;
  position: relative;
}

.position-item:last-child {
  margin-bottom: 0;
}

.position-item::before {
  content: "🎯";
  position: absolute;
  left: 0;
  font-size: 14px;
}

/* 简历文件 */
.resume-file-link {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: #165dff;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  padding: 12px 20px;
  background-color: #f0f5ff;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.resume-file-link:hover {
  background-color: #165dff;
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(22, 93, 255, 0.2);
}

.file-icon {
  font-size: 16px;
}

/* 操作按钮 */
.resume-actions {
  padding: 0 32px 32px;
  display: flex;
  gap: 16px;
  justify-content: flex-end;
}

.resume-actions .edit-btn,
.resume-actions .delete-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
  border: none;
  cursor: pointer;
}

.resume-actions .edit-btn {
  background-color: #ffc107;
  color: #212529;
  box-shadow: 0 2px 8px rgba(255, 193, 7, 0.2);
}

.resume-actions .edit-btn:hover {
  background-color: #e0a800;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 193, 7, 0.3);
}

.resume-actions .delete-btn {
  background-color: #f53f3f;
  color: white;
  box-shadow: 0 2px 8px rgba(245, 63, 63, 0.2);
}

.resume-actions .delete-btn:hover {
  background-color: #e53935;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(245, 63, 63, 0.3);
}

.btn-icon {
  font-size: 16px;
}

.file-preview {
  padding: 12px;
  background-color: #f0f5ff;
  border-radius: 6px;
  text-align: center;
}

.file-preview a {
  color: #165dff;
  text-decoration: none;
  font-weight: 500;
}

.file-preview a:hover {
  text-decoration: underline;
}

/* 表单行 */
.form-row {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.form-group.half {
  flex: 1;
  min-width: 200px;
}

/* 性别选择 */
.gender-options {
  display: flex;
  gap: 24px;
}

.radio-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 14px;
  color: #212529;
}

.radio-label input[type="radio"] {
  width: 16px;
  height: 16px;
  accent-color: #165dff;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .resume-header {
    padding: 24px;
  }
  
  .resume-header-top {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .resume-contact {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .resume-basic-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .resume-content {
    padding: 24px;
  }
  
  .resume-actions {
    padding: 0 24px 24px;
    flex-direction: column;
  }
  
  .resume-actions .edit-btn,
  .resume-actions .delete-btn {
    justify-content: center;
  }
  
  .skills-tag-list {
    gap: 8px;
  }
  
  .skill-tag {
    padding: 6px 12px;
    font-size: 13px;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .nav-menu {
    margin-left: 0;
    margin-top: 12px;
  }
  
  .resume-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .resume-header,
  .resume-content,
  .resume-actions {
    padding: 16px;
  }
}

/* 深色模式支持 */
@media (prefers-color-scheme: dark) {
  .app {
    background-color: #1a1a1a;
  }
  
  .login-form {
    background-color: #2d2d2d;
  }
  
  .form-group label {
    color: #e0e0e0;
  }
  
  .form-group input,
  .form-group textarea {
    background-color: #3d3d3d;
    border-color: #4d4d4d;
    color: #e0e0e0;
  }
  
  .project-card,
  .resume-card {
    background-color: #2d2d2d;
  }
  
  .project-title,
  .resume-name,
  .section-title {
    color: #e0e0e0;
  }
  
  .project-description,
  .section-content,
  .contact-value {
    color: #b0b0b0;
  }
  
  .contact-label {
    color: #86909c;
  }
  
  .github-container {
    background-color: #3d3d3d;
    border-color: #4d4d4d;
  }
  
  .github-url-container {
    background-color: #4d4d4d;
    border-color: #5d5d5d;
  }
  
  .github-url {
    color: #e0e0e0;
  }
  
  .modal {
    background-color: #2d2d2d;
  }
  
  .modal-header h3 {
    color: #e0e0e0;
  }
  
  .delete-message,
  .success-message {
    color: #e0e0e0;
  }
  
  .resume-header {
    background-color: #3d3d3d;
    border-bottom-color: #4d4d4d;
  }
  
  .file-preview {
    background-color: #3d3d3d;
  }
}
</style>