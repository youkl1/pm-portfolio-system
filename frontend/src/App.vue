<template>
  <div class="app">
    <!-- 登录页面 -->
    <Login v-if="!userInfo" 
      :loginForm="loginForm"
      :captchaImage="captchaImage"
      :errorMessage="errorMessage"
      @login="login"
      @get-captcha="getCaptcha"
    />
    
    <!-- 作品展示页面 -->
    <div v-else class="project-container">
      <!-- 头部 -->
      <Header 
        :userInfo="userInfo"
        :activeTab="activeTab"
        @switch-tab="switchTab"
        @logout="logout"
      />
      
      <main class="main-content">
        <!-- 作品管理标签页 -->
        <Projects v-if="activeTab === 'projects' " 
          :userInfo="userInfo"
          :projects="projects"
          :categories="categories"
          v-model:selectedCategoryIds="selectedCategoryIds"
          :allProjects="allProjects"
          @filter-projects="filterProjects"
          @edit-project="editProject"
          @show-delete-confirm="showDeleteConfirm"
          @copy-github-link="copyGithubLink"
          @copy-description="copyDescription"
          @open-add-modal="openAddModal"
          @search-projects="handleSearchProjects"
        />
        
        <!-- 个人简历标签页 -->
        <Resumes v-else-if="activeTab === 'resumes'" 
          :userInfo="userInfo"
          :resumes="resumes"
          @open-add-resume-modal="openAddResumeModal"
          @edit-resume="editResume"
          @show-delete-resume-confirm="showDeleteResumeConfirm"
        />
      </main>
      
      <!-- 新增/编辑作品弹窗 -->
      <ProjectModal 
        :showAddModal="showAddModal"
        :showEditModal="showEditModal"
        :projectForm="projectForm"
        :categories="categories"
        :isGeneratingTitle="isGeneratingTitle"
        @close-modal="closeModal"
        @save-project="saveProject"
        @generate-title="generateTitle"
        @handle-file-change="handleFileChange"
      />
      
      <!-- 删除作品确认弹窗 -->
      <DeleteModal 
        :showDeleteModal="showDeleteModal"
        type="作品"
        @close-delete-modal="closeDeleteModal"
        @confirm-delete="confirmDelete"
      />
      
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
      <CozeModal 
        :showCozeModal="showCozeModal"
        :isGeneratingTitle="isGeneratingTitle"
        :cozeResult="cozeResult"
        @confirm-coze-result="confirmCozeResult"
        @close-modal="closeCozeModal"
      />
      
      <!-- 新增/编辑简历弹窗 -->
      <ResumeModal 
        :showAddResumeModal="showAddResumeModal"
        :showEditResumeModal="showEditResumeModal"
        :resumeForm="resumeForm"
        :userInfo="userInfo"
        @close-resume-modal="closeResumeModal"
        @save-resume="saveResume"
        @handle-resume-file-change="handleResumeFileChange"
        @remove-resume-file="removeResumeFile"
      />
      
      <!-- 删除简历确认弹窗 -->
      <DeleteModal 
        :showDeleteModal="showDeleteResumeModal"
        type="简历"
        @close-delete-modal="closeDeleteResumeModal"
        @confirm-delete="confirmDeleteResume"
      />
    </div>
  </div>
</template>

<script>
import Login from './components/Login.vue'
import Header from './components/Header.vue'
import Projects from './components/Projects.vue'
import Resumes from './components/Resumes.vue'
import ProjectModal from './components/ProjectModal.vue'
import ResumeModal from './components/ResumeModal.vue'
import CozeModal from './components/CozeModal.vue'
import DeleteModal from './components/DeleteModal.vue'
import apiClient from './utils/api.js'
import { formatDate, getCategoryName, getProjectByCategory } from './utils/utils.js'

export default {
  name: 'App',
  components: {
    Login,
    Header,
    Projects,
    Resumes,
    ProjectModal,
    ResumeModal,
    CozeModal,
    DeleteModal
  },
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
      searchKeyword: '',
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
        title: '',
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
      // 先根据分类筛选
      let filteredProjects = []
      if (this.selectedCategoryIds.includes('')) {
        // 全选被选中，清空其他选择
        this.selectedCategoryIds = ['']
        filteredProjects = this.allProjects
      } else if (this.selectedCategoryIds.length === 0) {
        // 没有选择任何分类，显示所有作品
        filteredProjects = this.allProjects
      } else {
        // 筛选选中的分类
        filteredProjects = this.allProjects.filter(project => 
          this.selectedCategoryIds.includes(project.categoryId.toString())
        )
      }
      
      // 再根据搜索关键词筛选
      if (this.searchKeyword) {
        const keyword = this.searchKeyword.toLowerCase()
        filteredProjects = filteredProjects.filter(project => 
          project.title.toLowerCase().includes(keyword) ||
          project.description.toLowerCase().includes(keyword)
        )
      }
      
      this.projects = filteredProjects
    },
    
    // 切换分类
    changeCategory(categoryId) {
      this.selectedCategoryId = categoryId
      this.getProjects()
    },
    
    // 打开新增作品弹窗
    openAddModal() {
      this.showAddModal = true
    },
    
    // 处理作品搜索
    handleSearchProjects(keyword) {
      this.searchKeyword = keyword
      this.filterProjects()
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
      // 初始化状态
      this.isGeneratingTitle = true
      this.showCozeModal = true
      this.cozeResult = {
        projectIntro: 'AI正在生成项目信息...',
        coverImage: ''
      }

      // 请求头配置
      const headers = {
        "Authorization": "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjJlYzY4MDQ4LTUwN2MtNDgzOS1hZmQxLTM1YmJhODcyMmRkZCJ9.eyJpc3MiOiJodHRwczovL2FwaS5jb3plLmNuIiwiYXVkIjpbIjFiUTlaT1hRRmJ3cDJ0QXRiaTQxb1lVRmJ2SXNBOTIxIl0sImV4cCI6ODIxMDI2Njg3Njc5OSwiaWF0IjoxNzc0MzU0NzEwLCJzdWIiOiJzcGlmZmU6Ly9hcGkuY296ZS5jbi93b3JrbG9hZF9pZGVudGl0eS9pZDo3NjIwNzUwMjM3MzAwMTYyNTc5Iiwic3JjIjoiaW5ib3VuZF9hdXRoX2FjY2Vzc190b2tlbl9pZDo3NjIwNzk1NDU0MTM3MDQwOTM4In0.Qxg97tC8NoNGs7Ddk4rle1MsTvj1uLPyIRRSEw0otqgaZULPoLRKX7ZoqJyviP8WKBkHubQxVh_FT9G88TkRR4yXmm1M4HYFxAxg117QS7rxqvpYw5MWBkmfSVKMnP66qQU2OGpMrbPfaJbosfAP8e80DjtVR0Kl9V4CSB1JSogebgWMCzf4cI_3UNIjNgBGXO5WrhM5NWzOrtskPrwE2vU5kaLol0Wx1azp3s0cjW91Y1cAkUiaGeRuozFgi8pFeTXKoxVlc4fw2z-O8qej9TWeG-dJPtf_YM-EW4kDidDL1Z_z6FkjPsnv88xTLRrGJ4EEoPQTPFzK66rE514sjw",
        "Content-Type": "application/json",
        "Accept": "text/event-stream"
      };

      // 生成prompt
      const prompt = this.projectForm.title || "为产品经理个人作品生成项目信息";
      let fullContent = '';
      let isStreamFinished = false;

      console.log('开始请求Coze API，prompt:', prompt);

      try {
        // 发起fetch请求，完全移除AbortController，不主动终止请求
        const res = await fetch('/coze-api/stream_run', {
          method: "POST",
          headers: headers,
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
            "session_id": "rHmLKRn48rDOPtf0n0E1q",
            "project_id": "7620746694535381034"
          })
        });

        // 响应异常处理
        if (!res.ok) {
          const errText = await res.text();
          console.error('接口请求失败，响应内容:', errText);
          this.cozeResult.projectIntro = '生成项目信息失败，请重试';
          return;
        }
        if (!res.body) {
          console.error('当前环境不支持流式响应');
          this.cozeResult.projectIntro = '生成项目信息失败，请重试';
          return;
        }

        // 初始化流读取器
        const reader = res.body.getReader();
        const decoder = new TextDecoder();
        let buffer = "";

        // 循环读取流式数据
        while (true) {
          // 如果流已经标记结束，直接退出循环
          if (isStreamFinished) break;

          // 读取流数据
          const { done, value } = await reader.read();

          // 流正常结束，处理最后残留的buffer
          if (done) {
            if (buffer.trim()) {
              try {
                const dataText = buffer.trim().replace(/^data:/, "").trim();
                const parsed = JSON.parse(dataText);
                if (parsed.content?.answer) {
                  fullContent += parsed.content.answer;
                  this.cozeResult.projectIntro = fullContent;
                }
              } catch (e) {
                console.debug("最后一块数据解析失败", e);
              }
            }
            break;
          }

          // 解码流式数据，追加到buffer
          buffer += decoder.decode(value, { stream: true });
          // 按SSE标准分割数据包
          const blocks = buffer.split("\n\n");
          // 保留不完整的最后一块，下次循环继续处理
          buffer = blocks.pop() || "";

          // 遍历处理完整的数据包
          for (const block of blocks) {
            if (isStreamFinished) break;

            // 过滤出SSE的data行
            const dataLines = block
              .split("\n")
              .filter(line => line.trim().startsWith("data:"))
              .map(line => line.slice(5).trim());

            // 无有效数据，跳过
            if (dataLines.length === 0) continue;
            const dataText = dataLines.join("\n");

            // 处理Coze的结束标记
            if (dataText === "[DONE]") {
              isStreamFinished = true;
              break;
            }

            // 解析JSON数据
            try {
              const parsed = JSON.parse(dataText);
              console.log("收到流式数据:", parsed);

              // 提取回答内容，实时更新到页面
              if (parsed.content?.answer) {
                const answer = parsed.content.answer;
                fullContent += answer;
                this.cozeResult.projectIntro = fullContent;
                console.log('实时提取到answer:', answer);
              }

              // 识别Coze的结束事件，标记流完成，不主动cancel
              if (parsed.type === "message_end" || parsed.event === "message.finish") {
                isStreamFinished = true;
                break;
              }
            } catch (e) {
              // 不完整的JSON块是正常的，仅调试打印，不报错
              console.debug("跳过不完整的JSON块", dataText);
            }
          }
        }

        // 流处理完成，释放资源
        reader.releaseLock();
        console.log('流处理完成，最终完整内容:', fullContent);

        // 最终内容处理
        if (fullContent.trim()) {
          this.cozeResult.projectIntro = fullContent;
          // 解析封面图片
          await this.parseCozeResult(fullContent);
        } else {
          this.cozeResult.projectIntro = '生成项目信息失败，未获取到有效内容';
        }

      } catch (err) {
        // 只有真正的异常才报错，正常结束不会走到这里
        console.error('请求Coze API时发生错误:', err);
        this.cozeResult.projectIntro = '生成项目信息失败，请重试';
      } finally {
        // 最终重置加载状态
        this.isGeneratingTitle = false;
      }
    },
    
    // 解析Coze返回的结果
    async parseCozeResult(content) {
      console.log('开始解析Coze结果，内容:', content);
      
      // 确保项目简介不为空
      if (!content || content.trim() === '') {
        this.cozeResult.projectIntro = '生成的项目信息为空，请重试';
        return;
      }
      
      // 提取项目简介部分
      let projectIntro = content;
      let coverImageUrl = '';
      
      // 处理项目简介部分
      if (content.includes('# 项目简介')) {
        const introStart = content.indexOf('# 项目简介') + '# 项目简介'.length;
        let introEnd = content.indexOf('# 封面图片', introStart);
        if (introEnd === -1) {
          introEnd = content.length;
        }
        projectIntro = content.substring(introStart, introEnd).trim();
      }
      
      // 提取封面图片链接
      if (content.includes('# 封面图片')) {
        const imageStart = content.indexOf('# 封面图片') + '# 封面图片'.length;
        const imageMatch = content.substring(imageStart).match(/!\[.*?\]\((https?:\/\/[^\)]+)\)/);
        if (imageMatch && imageMatch[1]) {
          coverImageUrl = imageMatch[1];
          console.log('提取到封面图片链接:', coverImageUrl);
          // 下载并上传图片
          await this.downloadAndUploadImage(coverImageUrl);
        }
      }
      
      // 设置项目简介
      this.cozeResult.projectIntro = projectIntro;
      
      console.log('解析完成，项目简介:', this.cozeResult.projectIntro);
      console.log('解析完成，封面图片链接:', coverImageUrl);
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
    
    // 关闭Coze弹窗
    closeCozeModal() {
      this.showCozeModal = false;
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
  background-color: var(--background-light);
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

/* 作品展示页面 */
.project-container {
  min-height: 100vh;
}

.main-content {
  padding: var(--spacing-lg);
  max-width: 1440px;
  margin: 0 auto;
}

/* 成功提示弹窗 */
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
  background-color: var(--background-white);
  border-radius: var(--border-radius-lg);
  width: 100%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: var(--shadow-lg);
  animation: slideIn 0.3s ease-in-out;
  outline: none;
}

.success-modal {
  max-width: 400px;
}

.modal-header {
  padding: var(--spacing-md);
  border-bottom: 1px solid var(--border-light);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
  color: var(--text-primary);
  font-size: var(--font-size-lg);
  font-weight: 600;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: var(--text-tertiary);
  transition: all 0.2s ease;
  padding: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--border-radius-sm);
}

.close-btn:hover {
  color: var(--text-primary);
  background-color: var(--border-light);
}

.modal-body {
  padding: var(--spacing-md);
}

.success-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: var(--spacing-lg) 0;
}

.success-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background-color: var(--success-color);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  font-weight: bold;
  margin-bottom: var(--spacing-md);
  box-shadow: 0 4px 12px rgba(0, 180, 42, 0.3);
}

.success-message {
  text-align: center;
  font-size: var(--font-size-md);
  color: var(--text-primary);
  margin: 0;
  line-height: 1.5;
}

.form-actions {
  display: flex;
  gap: var(--spacing-sm);
  justify-content: flex-end;
  margin-top: var(--spacing-md);
  padding-top: var(--spacing-md);
  border-top: 1px solid var(--border-light);
}

.save-btn {
  padding: var(--spacing-sm) var(--spacing-md);
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: var(--border-radius-md);
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: var(--font-size-sm);
  font-weight: 500;
  box-shadow: 0 2px 4px rgba(22, 93, 255, 0.2);
}

.save-btn:hover {
  background-color: var(--primary-hover);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(22, 93, 255, 0.2);
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

/* 响应式设计 */
@media (max-width: 768px) {
  .main-content {
    padding: var(--spacing-md);
  }
  
  .modal {
    margin: 20px;
  }
}
</style>