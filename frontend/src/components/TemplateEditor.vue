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
                <el-button size="small" type="success" plain @click="showFileUpload = true" v-if="isEdit">
                  <el-icon><Upload /></el-icon>
                  更换
                </el-button>
              </div>
              <div class="preview-image" v-if="isImageFile(templateInfo.format) && props.templateId && props.templateId !== 'undefined'">
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

    <!-- ====================== 修复开始 ====================== -->
    <!-- 1. PDF格式：使用API获取数据并在前端显示 -->
    <div 
      class="document-content" 
      v-if="templateData && templateData.format && templateData.format.toLowerCase() === 'pdf'"
    >
      <div v-if="props.templateId && props.templateId !== 'undefined'">
        <!-- PDF预览卡片 -->
        <div class="pdf-preview-card">
          <!-- 卡片头部 -->
          <div class="pdf-card-header">
            <div class="pdf-card-title">
              <el-icon class="title-icon"><Document /></el-icon>
              <h2 class="pdf-title">{{ templateForm.name }}</h2>
              <span class="pdf-format-tag">PDF</span>
            </div>
            <div class="pdf-card-actions">
              <el-button size="small" type="primary" @click="handleDownload">
                <el-icon><Download /></el-icon>
                下载
              </el-button>
              <el-button size="small" @click="handlePrint">
                <el-icon><Printer /></el-icon>
                打印
              </el-button>
            </div>
          </div>
          
          <!-- 卡片内容 -->
          <div class="pdf-card-content">
            <!-- 文档信息 -->
            <div class="pdf-meta-info">
              <div class="meta-item">
                <el-icon class="meta-icon"><Timer /></el-icon>
                <span>{{ formatDate(new Date()) }}</span>
              </div>
              <div class="meta-item">
                <el-icon class="meta-icon"><Document /></el-icon>
                <span>预览模式</span>
              </div>
              <div class="meta-item" v-if="templateInfo">
                <el-icon class="meta-icon"><InfoFilled /></el-icon>
                <span>{{ formatSize(templateInfo.size) }}</span>
              </div>
            </div>
            
            <!-- PDF内容区域 -->
            <div v-loading="pdfLoading" element-loading-text="加载PDF中..." class="pdf-viewer-container">
              <iframe 
                :src="pdfDataUrl" 
                width="100%" 
                height="100%" 
                frameborder="0"
                class="pdf-iframe"
                v-if="pdfDataUrl"
              ></iframe>
              <div v-else class="pdf-loading-state">
                <div class="loading-animation">
                  <div class="loading-circle"></div>
                </div>
                <p class="loading-text">正在加载PDF文件，请稍候...</p>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div v-else>
        <div class="pdf-error-state">
          <div class="error-icon-container">
            <el-icon class="error-icon"><CircleClose /></el-icon>
          </div>
          <h3 class="error-title">预览失败</h3>
          <p class="error-message">模板ID无效，无法预览PDF文件</p>
        </div>
      </div>
    </div>

    <!-- 2. Word格式：使用iframe加载预览接口 -->
    <div 
      class="document-content" 
      v-else-if="templateData && templateData.format && (templateData.format.toLowerCase() === 'docx' || templateData.format.toLowerCase() === 'doc')"
    >
      <div v-loading="wordLoading" element-loading-text="加载Word文档中..." class="word-preview-container">
        <iframe 
          :src="wordPreviewUrl" 
          width="100%" 
          height="800px" 
          frameborder="0"
          style="min-height: 600px; border: none;"
          v-if="props.templateId && props.templateId !== 'undefined'"
          @load="handleWordLoad"
          @error="handleWordPreviewError"
        ></iframe>
      </div>
    </div>
    
    <!-- 3. Excel格式：使用iframe加载预览接口 -->
    <div 
      class="document-content" 
      v-else-if="templateData && templateData.format && (templateData.format.toLowerCase() === 'xlsx' || templateData.format.toLowerCase() === 'xls')"
    >
      <div v-loading="wordLoading" element-loading-text="加载Excel文档中..." class="word-preview-container">
        <iframe 
          :src="wordPreviewUrl" 
          width="100%" 
          height="800px" 
          frameborder="0"
          style="min-height: 600px; border: none;"
          v-if="props.templateId && props.templateId !== 'undefined'"
          @load="handleWordLoad"
          @error="handleWordPreviewError"
        ></iframe>
      </div>
    </div>

    <!-- 3. 其他格式：显示提示信息 -->
    <div 
      class="document-content" 
      v-else 
      v-html="templateContent"
    ></div>
    <!-- ====================== 修复结束 ====================== -->

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
    
    <!-- 文件上传对话框 -->
    <el-dialog
      v-model="showFileUpload"
      title="更换附件"
      width="500px"
      center
    >
      <el-form>
        <el-form-item label="选择文件" required>
          <el-upload
            class="upload-demo"
            action=""
            :auto-upload="false"
            :on-change="handleFileChange"
            :show-file-list="true"
            :file-list="fileList"
          >
            <el-button type="primary">
              <el-icon><Upload /></el-icon>
              选择文件
            </el-button>
            <template #tip>
              <div class="el-upload__tip">
                支持上传 Word(.docx)、PDF(.pdf)、Excel(.xlsx, .xls)、Markdown(.md)、TXT(.txt) 格式的文件，单个文件大小不超过50MB
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showFileUpload = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitFileUpload" :loading="saveLoading">
            确认更换
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { ElMessage, ElLoading } from 'element-plus'
import { Download, Printer, Timer, Document, CircleClose, InfoFilled } from '@element-plus/icons-vue'
import apiClient from '../utils/api'
import axios from 'axios'

// API基础URL - 与前端页面同域名
const apiBaseUrl = window.location.origin;

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
const pdfLoading = ref(false)
const wordLoading = ref(true)
const showFileUpload = ref(false)
const fileList = ref([])
const templateContent = ref('')
const templateInfo = ref(null)
const templateData = ref(null)
const pdfDataUrl = ref('')

// 表单数据
const templateForm = reactive({
  id: null,
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
  // 校验 templateId，避免传递 undefined 或无效值
  if (!props.templateId || props.templateId === 'undefined') {
    console.error('templateId 无效，无法加载模板数据')
    return
  }
  
  loading.value = true
  try {
    // 加载分类列表（先加载，确保选项可用）
    await loadCategories()
    
    // 加载模板信息
    const templateResponse = await apiClient.get(`/api/template/view/${props.templateId}`)
    templateData.value = templateResponse.data.data
    
    templateForm.id = props.templateId
    templateForm.name = templateData.value.name
    
    // 后端不再返回content字段，根据文件类型设置不同的预览内容
    if (templateData.value.format) {
      const format = templateData.value.format.toLowerCase()
      
      if (format === 'pdf') {
        // PDF文件使用API获取数据并显示
        templateContent.value = ''
        await loadPdfData()
      } else if (format === 'docx' || format === 'doc') {
        // Word文件使用iframe预览
        templateContent.value = ''
        wordLoading.value = true
      } else if (format === 'xlsx' || format === 'xls') {
        // Excel文件使用iframe预览
        templateContent.value = ''
        wordLoading.value = true
      } else {
        // 其他文件类型，显示提示信息
        templateContent.value = '<div style="text-align: center; padding: 40px; color: #86909c;"><h3>该文件类型暂不支持在线预览</h3><p>请点击下载按钮查看完整内容</p></div>'
      }
    } else {
      // 没有格式信息，显示提示信息
      templateContent.value = '<div style="text-align: center; padding: 40px; color: #86909c;"><h3>文件格式未知</h3><p>请点击下载按钮查看完整内容</p></div>'
    }
    
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

// 加载PDF数据
const loadPdfData = async () => {
  if (!props.templateId || props.templateId === 'undefined') {
    console.error('templateId 无效，无法加载PDF数据')
    return
  }
  
  pdfLoading.value = true
  try {
    // 直接使用预览URL作为iframe的src，不需要先获取二进制数据
    // 这样可以避免触发下载，让浏览器在iframe中正常预览PDF
    pdfDataUrl.value = `${apiBaseUrl}/api/template/preview/${props.templateId}`
  } catch (error) {
    console.error('加载PDF数据失败:', error)
    console.error('错误详情:', {
      message: error.message,
      response: error.response ? {
        status: error.response.status,
        data: error.response.data,
        headers: error.response.headers
      } : null
    })
    ElMessage.error('加载PDF文件失败，请重试')
  } finally {
    pdfLoading.value = false
  }
}

// 保存模板
const handleSave = async () => {
  if (!templateForm.name) {
    ElMessage.warning('请输入模板名称')
    return
  }
  
  // 校验 templateId
  if (!props.templateId || props.templateId === 'undefined') {
    ElMessage.error('模板ID无效，无法保存')
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
const handleDownload = async () => {
  if (!props.templateId || props.templateId === 'undefined') {
    ElMessage.error('模板ID无效，无法下载')
    return
  }
  
  try {
    const response = await axios({
      url: `${apiBaseUrl}/api/template/download/${props.templateId}`,
      method: 'GET',
      responseType: 'blob',
      withCredentials: true
    })
    
    // 获取文件名
    const contentDisposition = response.headers['content-disposition']
    let fileName = 'template'
    if (contentDisposition) {
      const matches = contentDisposition.match(/filename="([^"]+)"/)
      if (matches && matches[1]) {
        fileName = matches[1]
      }
    } else if (templateForm.name && templateData.value?.format) {
      fileName = `${templateForm.name}.${templateData.value.format}`
    }
    
    // 创建下载链接
    const blob = new Blob([response.data])
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = fileName
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    URL.revokeObjectURL(url)
    
    ElMessage.success('下载成功')
  } catch (error) {
    console.error('下载模板失败:', error)
    ElMessage.error('下载失败，请重试')
  }
}

// 打印预览
const handlePrint = () => {
  window.print()
}

// Word预览URL计算属性
const wordPreviewUrl = computed(() => {
  if (!props.templateId || props.templateId === 'undefined') {
    return ''
  }
  return `${apiBaseUrl}/api/template/preview/${props.templateId}`
})

// 处理Word加载完成
const handleWordLoad = () => {
  wordLoading.value = false
}

// 处理Word预览错误
const handleWordPreviewError = (error) => {
  console.error('Word预览加载失败:', error)
  wordLoading.value = false
  ElMessage.error('Word文档预览加载失败，请尝试下载查看')
}

// 处理文件选择
const handleFileChange = (file) => {
  fileList.value = [file]
}

// 提交文件上传
const handleSubmitFileUpload = async () => {
  if (fileList.value.length === 0) {
    ElMessage.warning('请选择文件')
    return
  }
  
  if (!props.templateId || props.templateId === 'undefined') {
    ElMessage.error('模板ID无效，无法更换附件')
    return
  }
  
  saveLoading.value = true
  try {
    const formData = new FormData()
    formData.append('file', fileList.value[0].raw)
    formData.append('templateId', props.templateId)
    
    const response = await apiClient.post('/api/template/update-attachment', formData)
    
    if (response.data.code === 200) {
      ElMessage.success('附件更换成功')
      showFileUpload.value = false
      fileList.value = []
      // 重新加载模板数据
      await loadTemplateData()
    } else {
      ElMessage.error('附件更换失败: ' + response.data.message)
    }
  } catch (error) {
    console.error('更换附件失败:', error)
    ElMessage.error('更换附件失败，请重试')
  } finally {
    saveLoading.value = false
  }
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
  // 如果 templateId 不存在或为 undefined，返回空字符串避免请求错误
  if (!props.templateId || props.templateId === 'undefined') {
    return ''
  }
  return `${apiBaseUrl}/api/template/preview/${props.templateId}`
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

/* PDF预览样式 - 基于UI/UX Pro Max设计规范 */

/* PDF预览卡片 */
.pdf-preview-card {
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  margin: 0 auto;
  max-width: 900px;
  width: 100%;
  transition: all 0.3s ease;
}

.pdf-preview-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

/* 卡片头部 */
.pdf-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 32px;
  border-bottom: 1px solid #f0f2f5;
  background: #fafafa;
}

.pdf-card-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.title-icon {
  font-size: 24px;
  color: #165dff;
}

.pdf-title {
  font-size: 20px;
  font-weight: 600;
  color: #1d2129;
  margin: 0;
  line-height: 1.3;
}

.pdf-format-tag {
  background: #e6f7ff;
  color: #165dff;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.pdf-card-actions {
  display: flex;
  gap: 12px;
}

/* 卡片内容 */
.pdf-card-content {
  padding: 0;
}

/* 文档信息 */
.pdf-meta-info {
  display: flex;
  gap: 24px;
  padding: 16px 32px;
  background: #fafafa;
  border-bottom: 1px solid #f0f2f5;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #4e5969;
}

.meta-icon {
  font-size: 16px;
  color: #86909c;
}

/* PDF内容区域 */
.pdf-viewer-container {
  position: relative;
  height: 700px;
  min-height: 600px;
}

.pdf-iframe {
  width: 100%;
  height: 100%;
  border: none;
}

/* 加载状态 */
.pdf-loading-state {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
  text-align: center;
  padding: 40px;
  background: #fafafa;
}

.loading-animation {
  margin-bottom: 16px;
}

.loading-circle {
  width: 48px;
  height: 48px;
  border: 3px solid #f0f2f5;
  border-top: 3px solid #165dff;
  border-radius: 50%;
  animation: loading-spin 1s linear infinite;
}

@keyframes loading-spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-text {
  font-size: 16px;
  color: #86909c;
  margin: 0;
}

/* 错误状态 */
.pdf-error-state {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 600px;
  text-align: center;
  padding: 40px;
  background: #fff2f0;
  border-radius: 12px;
  margin: 0 auto;
  max-width: 600px;
  width: 100%;
}

.error-icon-container {
  margin-bottom: 16px;
}

.error-icon {
  font-size: 64px;
  color: #f53f3f;
}

.error-title {
  font-size: 20px;
  font-weight: 600;
  color: #f53f3f;
  margin: 0 0 8px 0;
}

.error-message {
  font-size: 16px;
  color: #4e5969;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .pdf-preview-card {
    margin: 0;
    border-radius: 8px;
  }
  
  .pdf-card-header {
    padding: 16px 20px;
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .pdf-card-actions {
    align-self: flex-end;
  }
  
  .pdf-meta-info {
    padding: 12px 20px;
    gap: 16px;
  }
  
  .pdf-viewer-container {
    height: 500px;
  }
  
  .pdf-title {
    font-size: 18px;
  }
}

@media (max-width: 480px) {
  .pdf-card-header {
    padding: 12px 16px;
  }
  
  .pdf-meta-info {
    padding: 10px 16px;
    gap: 12px;
  }
  
  .pdf-viewer-container {
    height: 400px;
  }
  
  .pdf-title {
    font-size: 16px;
  }
  
  .pdf-card-actions {
    gap: 8px;
  }
  
  .el-button {
    padding: 6px 12px;
    font-size: 12px;
  }
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