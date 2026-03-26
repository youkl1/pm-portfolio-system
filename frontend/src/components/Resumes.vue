<template>
  <div class="resumes-container">
    <div class="toolbar">
      <div class="toolbar-left">
        <h2>个人简历</h2>
      </div>
      <button v-if="userInfo.role === 'admin'" @click="openAddResumeModal" class="add-btn">新增简历</button>
    </div>
    
    <div class="resume-container">
      <div v-if="resumes.length > 0" class="resume-card">
        <!-- 简历头部 -->
        <div class="resume-header">
          <div class="resume-header-top">
            <h3 class="resume-name">{{ resumes[0].name }}</h3>
            <div v-if="resumes[0].jobStatus" class="job-status-badge">{{ resumes[0].jobStatus }}</div>
          </div>
          <div class="resume-contact">
            <div v-if="resumes[0].email" class="contact-item">
              <span class="contact-icon">📧</span>
              <span class="contact-value">{{ resumes[0].email }}</span>
            </div>
            <div v-if="resumes[0].phone" class="contact-item">
              <span class="contact-icon">📞</span>
              <span class="contact-value">{{ resumes[0].phone }}</span>
            </div>
            <div v-if="resumes[0].wechat" class="contact-item">
              <span class="contact-icon">💬</span>
              <span class="contact-value">{{ resumes[0].wechat }}</span>
            </div>
          </div>
          <div class="resume-basic-info">
            <div v-if="resumes[0].gender" class="basic-info-item">
              <span class="info-label">性别：</span>
              <span class="info-value">{{ resumes[0].gender }}</span>
            </div>
            <div v-if="resumes[0].birthDate" class="basic-info-item">
              <span class="info-label">出生年月：</span>
              <span class="info-value">{{ resumes[0].birthDate }}</span>
            </div>
            <div v-if="resumes[0].workStartDate" class="basic-info-item">
              <span class="info-label">参加工作时间：</span>
              <span class="info-value">{{ resumes[0].workStartDate }}</span>
            </div>
            <div v-if="resumes[0].userType" class="basic-info-item">
              <span class="info-label">身份：</span>
              <span class="info-value">{{ resumes[0].userType }}</span>
            </div>
          </div>
        </div>
        
        <!-- 简历内容 -->
        <div class="resume-content">
          <!-- 个人优势 -->
          <div v-if="resumes[0].personalAdvantage" class="resume-section">
            <div class="section-header">
              <div class="section-icon">✨</div>
              <h4 class="section-title">个人优势</h4>
            </div>
            <div class="section-content">
              <p v-for="(advantage, index) in resumes[0].personalAdvantage.split('\n')" :key="index" class="advantage-item">
                {{ advantage }}
              </p>
            </div>
          </div>
          
          <!-- 教育背景 -->
          <div v-if="resumes[0].education" class="resume-section">
            <div class="section-header">
              <div class="section-icon">🎓</div>
              <h4 class="section-title">教育背景</h4>
            </div>
            <div class="section-content">
              <p>{{ resumes[0].education }}</p>
            </div>
          </div>
          
          <!-- 工作经验 -->
          <div v-if="resumes[0].workExperience" class="resume-section">
            <div class="section-header">
              <div class="section-icon">💼</div>
              <h4 class="section-title">工作经验</h4>
            </div>
            <div class="section-content">
              <div class="work-experience-list">
                <div v-for="(experience, index) in resumes[0].workExperience.split('\n\n')" :key="index" class="work-experience-item">
                  <p>{{ experience }}</p>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 技能 -->
          <div v-if="resumes[0].skills" class="resume-section">
            <div class="section-header">
              <div class="section-icon">🛠️</div>
              <h4 class="section-title">技能</h4>
            </div>
            <div class="section-content">
              <div class="skills-tag-list">
                <span v-for="(skill, index) in resumes[0].skills.split('、')" :key="index" class="skill-tag">
                  {{ skill }}
                </span>
              </div>
            </div>
          </div>
          
          <!-- 期望职位 -->
          <div v-if="resumes[0].expectedPosition" class="resume-section">
            <div class="section-header">
              <div class="section-icon">🎯</div>
              <h4 class="section-title">期望职位</h4>
            </div>
            <div class="section-content">
              <p v-for="(position, index) in resumes[0].expectedPosition.split('\n')" :key="index" class="position-item">
                {{ position }}
              </p>
            </div>
          </div>
          
          <!-- 简历文件 -->
          <div v-if="resumes[0].resumeFile" class="resume-section">
            <div class="section-header">
              <div class="section-icon">📄</div>
              <h4 class="section-title">简历文件</h4>
            </div>
            <div class="section-content">
              <a :href="resumes[0].resumeFile" target="_blank" class="resume-file-link">
                <span class="file-icon">📁</span>
                <span>{{ resumes[0].resumeFileName || '查看简历文件' }}</span>
              </a>
            </div>
          </div>
        </div>
        
        <!-- 操作按钮 -->
        <div v-if="userInfo.role === 'admin'" class="resume-actions">
          <button @click="editResume(resumes[0])" class="edit-btn">
            <span class="btn-icon">✏️</span>
            <span>编辑</span>
          </button>
          <button @click="showDeleteResumeConfirm(resumes[0].id)" class="delete-btn">
            <span class="btn-icon">🗑️</span>
            <span>删除</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Resumes',
  props: {
    userInfo: {
      type: Object,
      required: true
    },
    resumes: {
      type: Array,
      default: () => []
    }
  },
  methods: {
    openAddResumeModal() {
      this.$emit('open-add-resume-modal')
    },
    editResume(resume) {
      this.$emit('edit-resume', resume)
    },
    showDeleteResumeConfirm(id) {
      this.$emit('show-delete-resume-confirm', id)
    }
  }
}
</script>

<style scoped>
/* 个人简历页面 */
.resumes-container {
  padding: 24px;
}

/* 工具栏样式 */
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 0 8px;
}

.toolbar-left h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.add-btn {
  padding: 8px 16px;
  background: #52c41a;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
}

.add-btn:hover {
  background: #73d13d;
}

/* 简历容器 */
.resume-container {
  display: flex;
  justify-content: center;
  gap: 24px;
}

.resume-card {
  width: 100%;
  max-width: 1200px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: all 0.3s;
}

.resume-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

/* 简历头部 */
.resume-header {
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  background: #fafafa;
}

.resume-header-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.resume-name {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.job-status-badge {
  padding: 4px 12px;
  background: #1890ff;
  color: white;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.resume-contact {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: #666;
}

.contact-icon {
  font-size: 14px;
}

.resume-basic-info {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.basic-info-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: #666;
}

.info-label {
  font-weight: 500;
}

/* 简历内容 */
.resume-content {
  padding: 20px;
}

.resume-section {
  margin-bottom: 20px;
}

.resume-section:last-child {
  margin-bottom: 0;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.section-icon {
  font-size: 16px;
}

.section-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.section-content {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
}

.advantage-item {
  margin: 0 0 8px 0;
  padding-left: 16px;
  position: relative;
}

.advantage-item:before {
  content: '•';
  position: absolute;
  left: 0;
  color: #1890ff;
}

.work-experience-item {
  margin-bottom: 12px;
}

.work-experience-item:last-child {
  margin-bottom: 0;
}

.skills-tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.skill-tag {
  padding: 4px 12px;
  background: #f0f0f0;
  border-radius: 12px;
  font-size: 12px;
  color: #666;
}

.position-item {
  margin: 0 0 8px 0;
  padding-left: 16px;
  position: relative;
}

.position-item:before {
  content: '•';
  position: absolute;
  left: 0;
  color: #1890ff;
}

.resume-file-link {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #1890ff;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s;
}

.resume-file-link:hover {
  text-decoration: underline;
  color: #40a9ff;
}

.file-icon {
  font-size: 16px;
}

/* 操作按钮 */
.resume-actions {
  display: flex;
  gap: 8px;
  padding: 16px 20px;
  border-top: 1px solid #f0f0f0;
  background: #fafafa;
}

.edit-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 8px 16px;
  background: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.edit-btn:hover {
  background: #40a9ff;
}

.delete-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 8px 16px;
  background: #ff4d4f;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.delete-btn:hover {
  background: #ff7875;
}

.btn-icon {
  font-size: 14px;
}
</style>