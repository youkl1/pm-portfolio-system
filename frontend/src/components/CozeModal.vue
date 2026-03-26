<template>
  <div v-if="showCozeModal" class="modal-overlay">
    <div class="modal coze-modal">
      <div class="modal-header">
        <h3>AI生成结果</h3>
        <button @click="closeModal" :disabled="isGeneratingTitle" class="close-btn" :class="{ disabled: isGeneratingTitle }">
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
          <button type="button" @click="closeModal" :disabled="isGeneratingTitle" class="cancel-btn" :class="{ disabled: isGeneratingTitle }">
            取消
          </button>
          <button type="button" @click="confirmCozeResult" :disabled="isGeneratingTitle" class="save-btn" :class="{ disabled: isGeneratingTitle }">
            确定
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CozeModal',
  props: {
    showCozeModal: {
      type: Boolean,
      default: false
    },
    isGeneratingTitle: {
      type: Boolean,
      default: false
    },
    cozeResult: {
      type: Object,
      required: true
    }
  },
  methods: {
    confirmCozeResult() {
      this.$emit('confirm-coze-result')
    },
    closeModal() {
      if (!this.isGeneratingTitle) {
        this.$emit('close-modal')
      }
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

.close-btn:hover:not(.disabled) {
  color: #666;
}

.close-btn.disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.modal-body {
  padding: 24px;
}

/* Coze结果样式 */
.coze-result {
  display: flex;
  flex-direction: column;
  gap: 24px;
  margin-bottom: 24px;
}

.coze-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.coze-section h4 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.coze-content {
  padding: 16px;
  background: #f6f8fa;
  border-radius: 4px;
  font-size: 14px;
  line-height: 1.5;
  color: #333;
  min-height: 100px;
  position: relative;
}

.loading-indicator {
  position: absolute;
  bottom: 16px;
  right: 16px;
}

.loading-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid #f3f3f3;
  border-top: 2px solid #1890ff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.coze-image-container {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
  background: #f6f8fa;
  border-radius: 4px;
  min-height: 200px;
}

.coze-image {
  max-width: 100%;
  max-height: 300px;
  object-fit: contain;
  border-radius: 4px;
}

.coze-image-placeholder {
  color: #999;
  font-size: 14px;
}

/* 按钮样式 */
.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
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

.cancel-btn:hover:not(.disabled) {
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

.save-btn:hover:not(.disabled) {
  background: #40a9ff;
}

.cancel-btn.disabled,
.save-btn.disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.save-btn.disabled {
  background: #d9d9d9;
}
</style>