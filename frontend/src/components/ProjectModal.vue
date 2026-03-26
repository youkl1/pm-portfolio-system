<template>
  <div v-if="showAddModal || showEditModal" class="modal-overlay">
    <div class="modal">
      <div class="modal-header">
        <h3>{{ showEditModal ? '编辑作品' : '新增作品' }}</h3>
        <button @click="closeModal" class="close-btn">&times;</button>
      </div>
      <div class="modal-body">
        <form @submit.prevent="saveProject" class="project-form">
          <div class="form-group">
            <label for="title">标题<span class="required-mark">*</span></label>
            <div class="input-with-button">
              <input type="text" id="title" v-model="projectForm.title" required placeholder="请输入作品标题">
              <button type="button" @click="generateTitle" class="ai-button" :disabled="isGeneratingTitle">
                {{ isGeneratingTitle ? '生成中...' : 'AI' }}
              </button>
            </div>
          </div>
          <div class="form-group">
            <label for="description">描述<span class="required-mark">*</span></label>
            <textarea id="description" v-model="projectForm.description" required placeholder="请输入作品描述"></textarea>
          </div>
          <div class="form-group">
            <label>封面图片<span class="required-mark">*</span></label>
            <div class="upload-area" @click="triggerFileInput">
              <input type="file" ref="fileInput" accept="image/jpeg,image/png" @change="handleFileChange" style="display: none;">
              <img v-if="projectForm.coverImage" :src="projectForm.coverImage" alt="预览" class="preview-image">
              <div v-else class="upload-placeholder">点击上传图片</div>
            </div>
          </div>
          <div class="form-group">
            <label for="detailLink">详情链接</label>
            <input type="url" id="detailLink" v-model="projectForm.detailLink" placeholder="请输入作品详情链接">
          </div>
          <div class="form-group">
            <label for="githubLink">GitHub地址</label>
            <input type="url" id="githubLink" v-model="projectForm.githubLink" placeholder="请输入GitHub仓库地址">
          </div>
          <div class="form-group">
            <label for="categoryId">系统分类<span class="required-mark">*</span></label>
            <select id="categoryId" v-model="projectForm.categoryId" class="category-select" required>
              <option value="">请选择分类</option>
              <option v-for="category in categories" :key="category.id" :value="category.id">{{ category.name }}</option>
            </select>
          </div>
          <div class="form-group">
            <label for="sort">排序<span class="required-mark">*</span></label>
            <input type="number" id="sort" v-model.number="projectForm.sort" min="0" required>
          </div>
          <div class="form-actions">
            <button type="button" @click="closeModal" class="cancel-btn">取消</button>
            <button type="submit" class="save-btn">保存</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ProjectModal',
  props: {
    showAddModal: {
      type: Boolean,
      default: false
    },
    showEditModal: {
      type: Boolean,
      default: false
    },
    projectForm: {
      type: Object,
      required: true
    },
    categories: {
      type: Array,
      default: () => []
    },
    isGeneratingTitle: {
      type: Boolean,
      default: false
    }
  },
  methods: {
    closeModal() {
      this.$emit('close-modal')
    },
    saveProject() {
      // 验证封面图片
      if (!this.projectForm.coverImage) {
        alert('请上传封面图片');
        return;
      }
      this.$emit('save-project')
    },
    generateTitle() {
      this.$emit('generate-title')
    },
    triggerFileInput() {
      this.$refs.fileInput.click()
    },
    handleFileChange(event) {
      this.$emit('handle-file-change', event)
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
  max-width: 500px;
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
.project-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.required-mark {
  color: #ff4d4f;
  margin-left: 4px;
  font-size: 14px;
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
  min-height: 100px;
}

.input-with-button {
  display: flex;
  gap: 8px;
}

.input-with-button input {
  flex: 1;
}

.ai-button {
  padding: 0 16px;
  background: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
}

.ai-button:hover:not(:disabled) {
  background: #40a9ff;
}

.ai-button:disabled {
  background: #d9d9d9;
  cursor: not-allowed;
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

.preview-image {
  max-width: 100%;
  max-height: 200px;
  object-fit: contain;
  border-radius: 4px;
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

/* 禁用状态 */
.disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>