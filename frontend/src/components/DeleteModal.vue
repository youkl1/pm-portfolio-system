<template>
  <div v-if="showDeleteModal" class="modal-overlay">
    <div class="modal delete-modal">
      <div class="modal-header">
        <h3>确认删除</h3>
        <button @click="closeDeleteModal" class="close-btn">&times;</button>
      </div>
      <div class="modal-body">
        <p class="delete-message">确定要删除这个{{ type }}吗？此操作不可撤销。</p>
        <div class="form-actions">
          <button type="button" @click="closeDeleteModal" class="cancel-btn">取消</button>
          <button type="button" @click="confirmDelete" class="delete-confirm-btn">确认删除</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'DeleteModal',
  props: {
    showDeleteModal: {
      type: Boolean,
      default: false
    },
    type: {
      type: String,
      default: '项目'
    }
  },
  methods: {
    closeDeleteModal() {
      this.$emit('close-delete-modal')
    },
    confirmDelete() {
      this.$emit('confirm-delete')
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
  max-width: 400px;
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

.delete-message {
  margin: 0 0 24px 0;
  font-size: 14px;
  color: #666;
  line-height: 1.5;
  text-align: center;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
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

.delete-confirm-btn {
  padding: 8px 16px;
  background: #ff4d4f;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
}

.delete-confirm-btn:hover {
  background: #ff7875;
}
</style>