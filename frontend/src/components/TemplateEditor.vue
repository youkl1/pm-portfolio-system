<template>
  <div class="template-editor">
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑模板' : '查看模板'"
      width="90%"
      max-width="1200px"
      class="editor-dialog"
      center
    >
      <div class="editor-header">
        <el-form :model="templateForm" label-width="80px" class="form-container">
          <el-form-item label="模板名称" required>
            <el-input
              v-model="templateForm.name"
              placeholder="请输入模板名称"
              class="name-input"
              :disabled="!isEdit"
              size="large"
            />
          </el-form-item>
          <el-form-item label="模板描述">
            <el-input
              v-model="templateForm.description"
              placeholder="请输入模板描述"
              type="textarea"
              :rows="3"
              class="description-input"
              :disabled="!isEdit"
              size="large"
            />
          </el-form-item>
          <el-form-item label="分类">
            <el-select
              v-model="templateForm.categoryIds"
              multiple
              placeholder="请选择分类"
              class="category-select"
              :disabled="!isEdit"
              size="large"
              style="width: 100%"
            >
              <el-option
                v-for="category in categories"
                :key="category.id"
                :label="category.name"
                :value="category.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="附件">
            <div class="attachment-info" v-if="templateInfo">
              <div class="attachment-item">
                <el-icon class="attachment-icon"><Document /></el-icon>
                <span class="attachment-name">{{ templateForm.name }}.{{ templateInfo.format }}</span>
                <el-button size="small" type="primary" plain @click="handleDownload">
                  <el-icon><Download /></el-icon>
                  下载
                </el-button>
              </div>
              <div class="preview-image" v-if="isImageFile(templateInfo.format)">
                <img :src="getPreviewImageUrl()" alt="预览图" class="preview-img" />
              </div>
            </div>
          </el-form-item>
        </el-form>
      </div>
      
      <div class="editor-content" v-loading="loading" element-loading-text="加载中...">
          <div class="preview-header">
            <el-button-group>
              <el-button size="small" type="primary" plain @click="handleDownload">
                <el-icon><Download /></el-icon>
                下载模板
              </el-button>
              <el-button size="small" plain @click="handlePrint">
                <el-icon><Printer /></el-icon>
                打印预览
              </el-button>
            </el-button-group>
            <div class="preview-info">
              <span class="info-item">
                <el-icon class="info-icon"><Timer /></el-icon>
                {{ formatDate(new Date()) }}
              </span>
              <span class="info-item">
                <el-icon class="info-icon"><Document /></el-icon>
                {{ isEdit ? '编辑模式' : '预览模式' }}
              </span>
            </div>
          </div>
          <div class="document-preview">
            <div class="document-header">
              <h1 class="document-title">{{ templateForm.name }}</h1>
              <div class="document-meta">
                <span class="meta-item">描述: {{ templateForm.description || '无' }}</span>
                <span class="meta-item">分类: {{ getCategoryNames() }}</span>
                <span class="meta-item" v-if="templateInfo">格式: {{ templateInfo.format || '无' }}</span>
                <span class="meta-item" v-if="templateInfo">大小: {{ formatSize(templateInfo.size) }}</span>
              </div>
            </div>
            <div class="document-content" v-html="templateContent"></div>
          </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button size="large" @click="dialogVisible = false">关闭</el-button>
          <el-button
            v-if="isEdit"
            type="primary"
            size="large"
            @click="handleSave"
            :loading="saveLoading"
          >
            保存
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { ElMessage, ElLoading } from 'element-plus'
import { Download, Printer, Timer, Document } from '@element-plus/icons-vue'
import apiClient from '../utils/api'

// Props
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  templateId: {
    type: Number,
    default: null
  },
  isEdit: {
    type: Boolean,
    default: false
  }
})

// Emits
const emit = defineEmits(['update:visible', 'save-success'])

// 响应式数据
const dialogVisible = ref(props.visible)
const saveLoading = ref(false)
const loading = ref(false)
const templateContent = ref('')
const templateInfo = ref(null)

// 表单数据
const templateForm = reactive({
  name: '',
  description: '',
  categoryIds: []
})

// 分类列表
const categories = ref([])

// 监听visible变化
watch(() => props.visible, (newVal) => {
  dialogVisible.value = newVal
  if (newVal && props.templateId) {
    loadTemplateData()
  }
})

// 监听dialogVisible变化
watch(() => dialogVisible.value, (newVal) => {
  emit('update:visible', newVal)
})

// 加载模板数据
const loadTemplateData = async () => {
  loading.value = true
  try {
    // 加载分类列表（先加载，确保选项可用）
    await loadCategories()
    
    // 加载模板信息
    const templateResponse = await apiClient.get(`/api/template/view/${props.templateId}`)
    const templateData = templateResponse.data.data
    
    templateForm.name = templateData.name
    templateContent.value = templateData.content
    
    // 加载模板详情（包含描述和分类）
    const detailResponse = await apiClient.get(`/api/template/list`, {
      params: {
        page: 1,
        size: 100
      }
    })
    
    console.log('模板列表数据:', detailResponse.data.data.items)
    
    const template = detailResponse.data.data.items.find(t => t.id === props.templateId)
    if (template) {
      console.log('找到模板:', template)
      console.log('模板分类:', template.categories)
      templateForm.description = template.description || ''
      templateForm.categoryIds = template.categories?.map(c => c.id) || []
      // 存储模板信息（包含附件信息）
      templateInfo.value = template
    }
  } catch (error) {
    console.error('加载模板数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 加载分类列表
const loadCategories = async () => {
  try {
    const response = await apiClient.get('/api/template/category/list')
    categories.value = response.data.data
    console.log('分类列表:', categories.value)
  } catch (error) {
    console.error('加载分类列表失败:', error)
  }
}

// 保存模板
const handleSave = async () => {
  if (!templateForm.name) {
    ElMessage.warning('请输入模板名称')
    return
  }
  
  saveLoading.value = true
  try {
    await apiClient.put(`/api/template/edit/${props.templateId}`, {
      name: templateForm.name,
      description: templateForm.description,
      categoryIds: templateForm.categoryIds
    })
    
    ElMessage.success('保存成功')
    dialogVisible.value = false
    emit('save-success')
  } catch (error) {
    console.error('保存模板失败:', error)
    ElMessage.error('保存失败，请重试')
  } finally {
    saveLoading.value = false
  }
}

// 下载模板
const handleDownload = () => {
  // 这里可以实现模板下载逻辑
  ElMessage.info('下载功能开发中...')
}

// 打印预览
const handlePrint = () => {
  window.print()
}

// 格式化日期
const formatDate = (date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

// 获取分类名称
const getCategoryNames = () => {
  if (!templateForm.categoryIds || templateForm.categoryIds.length === 0) {
    return '无'
  }
  return categories.value
    .filter(category => templateForm.categoryIds.includes(category.id))
    .map(category => category.name)
    .join(', ')
}

// 格式化文件大小
const formatSize = (size) => {
  if (!size) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB']
  let unitIndex = 0
  let currentSize = size
  while (currentSize >= 1024 && unitIndex < units.length - 1) {
    currentSize /= 1024
    unitIndex++
  }
  return `${currentSize.toFixed(2)} ${units[unitIndex]}`
}

// 判断是否为图片文件
const isImageFile = (format) => {
  const imageFormats = ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp']
  return imageFormats.includes(format.toLowerCase())
}

// 获取预览图片URL
const getPreviewImageUrl = () => {
  // 使用后端提供的图片预览接口
  return `http://localhost:8080/api/template/preview/${props.templateId}`
}

// 组件挂载时
onMounted(() => {
  if (props.visible && props.templateId) {
    loadTemplateData()
  }
})
</script>

<style scoped>
.template-editor {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

.editor-dialog {
  border-radius: 16px;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.16);
  max-width: 1200px;
}

.editor-header {
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid #e8e9ed;
}

.form-container {
  width: 100%;
}

.form-container .el-form-item {
  margin-bottom: 24px;
}

.form-container .el-form-item__label {
  font-weight: 600;
  font-size: 16px;
  color: #1d2129;
}

.editor-content {
  min-height: 500px;
  max-height: 700px;
  overflow-y: auto;
  border: 1px solid #e8e9ed;
  border-radius: 12px;
  background-color: #ffffff;
  transition: all 0.3s ease;
}

.editor-content:hover {
  border-color: #c9cdd4;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.view-only {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e8e9ed;
}

.preview-info {
  display: flex;
  gap: 20px;
  align-items: center;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #86909c;
}

.info-icon {
  font-size: 14px;
}

.document-preview {
  flex: 1;
  overflow-y: auto;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  padding: 40px;
  max-width: 800px;
  margin: 0 auto;
  min-height: 500px;
}

.document-header {
  margin-bottom: 40px;
  text-align: center;
  padding-bottom: 24px;
  border-bottom: 2px solid #f0f2f5;
}

.document-title {
  font-size: 28px;
  font-weight: 700;
  color: #000000;
  margin-bottom: 16px;
  line-height: 1.3;
}

.document-meta {
  display: flex;
  justify-content: center;
  gap: 32px;
  font-size: 14px;
  color: #86909c;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.document-content {
  line-height: 1.8;
  color: #1d2129;
  font-size: 16px;
}

.document-content h1, .document-content h2, .document-content h3 {
  margin: 32px 0 16px 0;
  font-weight: 600;
  color: #000000;
}

.document-content h1 {
  font-size: 24px;
  border-bottom: 1px solid #e8e9ed;
  padding-bottom: 8px;
}

.document-content h2 {
  font-size: 20px;
}

.document-content h3 {
  font-size: 18px;
}

.document-content p {
  margin-bottom: 16px;
  text-align: justify;
}

.document-content ul, .document-content ol {
  margin-bottom: 16px;
  padding-left: 32px;
}

.document-content li {
  margin-bottom: 8px;
}

.document-content img {
  max-width: 100%;
  height: auto;
  margin: 20px 0;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.document-content table {
  width: 100%;
  border-collapse: collapse;
  margin: 20px 0;
  font-size: 14px;
}

.document-content table th, .document-content table td {
  padding: 12px;
  border: 1px solid #e8e9ed;
  text-align: left;
}

.document-content table th {
  background-color: #f7f8fa;
  font-weight: 600;
  color: #1d2129;
}

.document-content code {
  background-color: #f7f8fa;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Courier New', Courier, monospace;
  font-size: 14px;
  color: #f53f3f;
}

.document-content pre {
  background-color: #f7f8fa;
  padding: 16px;
  border-radius: 8px;
  overflow-x: auto;
  margin: 16px 0;
}

.document-content pre code {
  background-color: transparent;
  padding: 0;
  color: #1d2129;
}

.document-content blockquote {
  border-left: 4px solid #165dff;
  padding-left: 16px;
  margin: 16px 0;
  color: #4e5969;
  font-style: italic;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .preview-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .preview-info {
    width: 100%;
    justify-content: space-between;
  }
  
  .document-preview {
    padding: 24px;
    max-width: 100%;
  }
  
  .document-title {
    font-size: 24px;
  }
  
  .document-meta {
    flex-direction: column;
    gap: 8px;
    align-items: center;
  }
  
  .document-content {
    font-size: 14px;
    line-height: 1.6;
  }
  
  .document-content h1 {
    font-size: 20px;
  }
  
  .document-content h2 {
    font-size: 18px;
  }
  
  .document-content h3 {
    font-size: 16px;
  }
}

.quill-editor-container {
  height: 500px;
  display: flex;
  flex-direction: column;
}

.editor-meta-info {
  display: flex;
  gap: 20px;
  padding: 16px;
  background-color: #f7f8fa;
  border-bottom: 1px solid #e8e9ed;
  font-size: 14px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.meta-label {
  color: #86909c;
  font-weight: 500;
}

.meta-value {
  color: #1d2129;
  font-weight: 500;
}

.quill-editor {
  flex: 1;
  height: 100%;
}

/* Quill编辑器样式覆盖 */
:deep(.ql-container) {
  height: 100%;
  font-size: 16px;
  border: none;
  border-radius: 0 0 12px 12px;
}

:deep(.ql-editor) {
  min-height: 480px;
  line-height: 1.7;
  padding: 24px;
}

:deep(.ql-toolbar.ql-snow) {
  border: 1px solid #e8e9ed;
  border-bottom: 1px solid #e8e9ed;
  border-radius: 12px 12px 0 0;
  background-color: #f7f8fa;
  padding: 12px 16px;
}

:deep(.ql-toolbar.ql-snow .ql-formats) {
  margin-right: 16px;
}

:deep(.ql-toolbar.ql-snow button) {
  width: 28px;
  height: 28px;
  border-radius: 4px;
  transition: all 0.2s ease;
}

:deep(.ql-toolbar.ql-snow button:hover) {
  background-color: rgba(0, 0, 0, 0.05);
}

:deep(.ql-toolbar.ql-snow button.ql-active) {
  background-color: rgba(22, 93, 255, 0.1);
  color: #165dff;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  padding-top: 24px;
  border-top: 1px solid #e8e9ed;
}

.attachment-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.attachment-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background-color: #f7f8fa;
  border-radius: 8px;
  border: 1px solid #e8e9ed;
}

.attachment-icon {
  font-size: 24px;
  color: #165dff;
}

.attachment-name {
  flex: 1;
  font-weight: 500;
  color: #1d2129;
}

.preview-image {
  display: flex;
  justify-content: center;
  padding: 16px;
  background-color: #f7f8fa;
  border-radius: 8px;
  border: 1px solid #e8e9ed;
}

.preview-img {
  max-width: 200px;
  max-height: 200px;
  object-fit: contain;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .editor-dialog {
    width: 98% !important;
    max-width: 98%;
  }
  
  .form-container .el-form-item {
    margin-bottom: 16px;
  }
  
  .form-container .el-form-item__label {
    font-size: 14px;
  }
  
  .editor-content {
    min-height: 400px;
    max-height: 500px;
  }
  
  .quill-editor-container {
    height: 400px;
  }
  
  :deep(.ql-editor) {
    min-height: 380px;
    padding: 16px;
  }
  
  .view-only {
    padding: 16px;
  }
  
  .content-preview {
    font-size: 14px;
    line-height: 1.6;
  }
}

@media (max-width: 480px) {
  .editor-content {
    min-height: 300px;
    max-height: 400px;
  }
  
  .quill-editor-container {
    height: 300px;
  }
  
  :deep(.ql-editor) {
    min-height: 280px;
  }
}
</style>