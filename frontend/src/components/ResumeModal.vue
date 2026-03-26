<template>
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
            <input type="text" id="resumeName" v-model="resumeForm.name" required placeholder="请输入姓名">
          </div>
          <div class="form-group">
            <label for="resumeEmail">邮箱</label>
            <input type="email" id="resumeEmail" v-model="resumeForm.email" placeholder="请输入邮箱地址">
          </div>
          <div class="form-group">
            <label for="resumePhone">电话</label>
            <input type="tel" id="resumePhone" v-model="resumeForm.phone" placeholder="请输入手机号码">
          </div>
          <div class="form-group">
            <label for="resumeEducation">教育背景</label>
            <textarea id="resumeEducation" v-model="resumeForm.education" placeholder="请输入教育背景信息"></textarea>
          </div>
          <div class="form-group">
            <label for="resumeWorkExperience">工作经验</label>
            <textarea id="resumeWorkExperience" v-model="resumeForm.workExperience" placeholder="请输入工作经验信息"></textarea>
          </div>
          <div class="form-group">
            <label for="resumeSkills">技能</label>
            <textarea id="resumeSkills" v-model="resumeForm.skills" placeholder="请输入技能信息"></textarea>
          </div>
          <div class="form-group">
            <label for="resumeProjects">项目经验</label>
            <textarea id="resumeProjects" v-model="resumeForm.projects" placeholder="请输入项目经验信息"></textarea>
          </div>
          <div class="form-group">
            <label for="resumeSelfIntroduction">自我介绍</label>
            <textarea id="resumeSelfIntroduction" v-model="resumeForm.selfIntroduction" placeholder="请输入自我介绍信息"></textarea>
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
            <textarea id="resumePersonalAdvantage" v-model="resumeForm.personalAdvantage" rows="4" placeholder="请输入个人优势信息"></textarea>
          </div>
          <div class="form-group">
            <label for="resumeExpectedPosition">期望职位</label>
            <textarea id="resumeExpectedPosition" v-model="resumeForm.expectedPosition" rows="2" placeholder="请输入期望职位信息"></textarea>
          </div>
          <div class="form-actions">
            <button type="button" @click="closeResumeModal" class="cancel-btn">取消</button>
            <button type="submit" class="save-btn">保存</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ResumeModal',
  props: {
    showAddResumeModal: {
      type: Boolean,
      default: false
    },
    showEditResumeModal: {
      type: Boolean,
      default: false
    },
    resumeForm: {
      type: Object,
      required: true
    },
    userInfo: {
      type: Object,
      required: true
    }
  },
  methods: {
    closeResumeModal() {
      this.$emit('close-resume-modal')
    },
    saveResume() {
      this.$emit('save-resume')
    },
    handleResumeFileChange(event) {
      this.$emit('handle-resume-file-change', event)
    },
    removeResumeFile() {
      this.$emit('remove-resume-file')
    }
  }
}
</script>

<style scoped>
/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
  width: 100%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
  background: #fafafa;
}

.modal-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #999;
  transition: color 0.3s;
}

.close-btn:hover {
  color: #666;
}

.modal-body {
  padding: 24px;
}

/* 表单样式 */
.resume-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-row {
  display: flex;
  gap: 16px;
}

.form-group.half {
  flex: 1;
}

.form-group label {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.form-group input,
.form-group textarea,
.form-group select {
  padding: 10px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
  transition: all 0.3s;
}

.form-group input:focus,
.form-group textarea:focus,
.form-group select:focus {
  outline: none;
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.form-group textarea {
  resize: vertical;
  min-height: 80px;
}

.gender-options {
  display: flex;
  gap: 16px;
}

.radio-label {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
}

.radio-label input[type="radio"] {
  margin: 0;
  cursor: pointer;
}

.upload-area {
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  padding: 24px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.upload-area:hover {
  border-color: #1890ff;
  background: #f6f8fa;
}

.file-preview {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #f0f0f0;
  border-radius: 4px;
}

.file-info a {
  color: #1890ff;
  text-decoration: none;
  font-size: 14px;
}

.file-info a:hover {
  text-decoration: underline;
}

.delete-file-btn {
  padding: 4px 8px;
  background: #ff4d4f;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.3s;
}

.delete-file-btn:hover {
  background: #ff7875;
}

.upload-placeholder {
  color: #999;
  font-size: 14px;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 8px;
}

.cancel-btn {
  padding: 8px 16px;
  background: white;
  color: #666;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.cancel-btn:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.save-btn {
  padding: 8px 16px;
  background: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
}

.save-btn:hover {
  background: #40a9ff;
}
</style>