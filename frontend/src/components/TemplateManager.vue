<template>
  <div class="template-manager">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-info">
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item>系统管理</el-breadcrumb-item>
            <el-breadcrumb-item>模板库</el-breadcrumb-item>
          </el-breadcrumb>
          <h1 class="page-title">模板库</h1>
          <p class="page-description">管理和使用模板文档</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" size="default" @click="handleImportTemplate" class="action-button" v-if="hasImportPermission">
            <el-icon><Plus /></el-icon>
            导入模板
          </el-button>
        </div>
      </div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <el-card class="main-card" shadow="hover">
        <!-- 搜索和筛选 -->
        <div class="search-filter">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索模板名称或描述"
            prefix-icon="Search"
            class="search-input"
            @keyup.enter="loadTemplates"
          />
          <el-select
            v-model="selectedCategoryId"
            placeholder="选择分类"
            class="category-select"
            @change="loadTemplates"
          >
            <el-option label="全部分类" value="" />
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
          <el-button type="primary" @click="loadTemplates" class="search-button">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
        </div>

        <!-- 模板列表 -->
        <div class="template-list">
          <el-empty v-if="templates.length === 0" description="暂无模板" />
          <div v-else class="template-cards">
            <el-card
              v-for="template in templates"
              :key="template.id"
              class="template-card"
              shadow="hover"
              @click="handleViewTemplate(template)"
            >
              <div class="card-header">
                <h3 class="template-name">{{ template.name }}</h3>
                <el-tag size="small" :type="getFormatTagType(template.format)">
                  {{ template.format.toUpperCase() }}
                </el-tag>
              </div>
              <div class="card-body">
                <p class="template-description">{{ template.description || '无描述' }}</p>
                <div class="template-info">
                  <span class="info-item">
                    <el-icon><Timer /></el-icon>
                    {{ formatDate(template.createdAt) }}
                  </span>
                  <span class="info-item">
                    <el-icon><Document /></el-icon>
                    {{ formatSize(template.size) }}
                  </span>
                </div>
                <div class="template-categories">
                  <template v-if="template.categories && template.categories.length > 0">
                    <el-tag
                      v-for="category in template.categories"
                      :key="category.id"
                      size="small"
                      type="info"
                      effect="plain"
                    >
                      {{ category.name }}
                    </el-tag>
                  </template>
                  <template v-else>
                    <el-tag size="small" type="info" effect="plain">无</el-tag>
                  </template>
                </div>
              </div>
              <div class="card-footer">
                <el-button
                  size="small"
                  type="primary"
                  link
                  @click.stop="handleViewTemplate(template)"
                >
                  <el-icon><View /></el-icon>
                  查看
                </el-button>
                <el-button
                  size="small"
                  type="success"
                  link
                  @click.stop="handleEditTemplate(template)"
                  v-if="hasEditPermission"
                >
                  <el-icon><Edit /></el-icon>
                  编辑
                </el-button>
                <el-button
                  size="small"
                  type="warning"
                  link
                  @click.stop="handleDownloadTemplate(template.id)"
                  v-if="hasDownloadPermission"
                >
                  <el-icon><Download /></el-icon>
                  下载
                </el-button>
                <el-button
                  size="small"
                  type="danger"
                  link
                  @click.stop="handleDeleteTemplate(template.id)"
                  v-if="hasEditPermission"
                >
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </div>
            </el-card>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination" v-if="total > 0">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>

      <!-- 分类管理按钮 -->
      <el-card class="category-card" shadow="hover" v-if="hasCategoryManagePermission">
        <div class="category-header">
          <h3 class="category-title">分类管理</h3>
          <el-button type="primary" size="small" @click="handleManageCategories">
            <el-icon><Setting /></el-icon>
            管理分类
          </el-button>
        </div>
        <div class="category-list">
          <el-tag
            v-for="category in categories"
            :key="category.id"
            size="default"
            class="category-tag"
            @click="handleSelectCategory(category.id)"
            :effect="selectedCategoryId === category.id ? 'dark' : 'plain'"
          >
            {{ category.name }}
            <span class="category-count">({{ category.templateCount || 0 }})</span>
          </el-tag>
        </div>
      </el-card>
    </div>

    <!-- 导入模板弹窗 -->
    <el-dialog
      v-model="importDialogVisible"
      title="导入模板"
      width="600px"
      class="import-dialog"
      center
    >
      <el-form :model="importForm" label-width="100px">
        <el-form-item label="模板名称" required>
          <el-input v-model="importForm.name" placeholder="请输入模板名称" />
        </el-form-item>
        <el-form-item label="模板描述">
          <el-input
            v-model="importForm.description"
            type="textarea"
            placeholder="请输入模板描述"
            :rows="3"
          />
        </el-form-item>
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
                支持上传 Word(.docx)、PDF(.pdf)、Markdown(.md)、TXT(.txt) 格式的文件，单个文件大小不超过50MB
              </div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="选择分类">
          <el-select
            v-model="importForm.categoryIds"
            multiple
            placeholder="请选择分类"
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
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="importDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitImport" :loading="importLoading">
            导入
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 删除确认弹窗 -->
    <el-dialog
      v-model="deleteDialogVisible"
      title="删除模板"
      width="400px"
      center
    >
      <span>确定要删除这个模板吗？此操作不可撤销。</span>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="deleteDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="handleConfirmDelete" :loading="deleteLoading">
            删除
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 模板编辑器 -->
    <TemplateEditor
      v-model:visible="editorVisible"
      :template-id="currentTemplateId"
      :is-edit="isEditMode"
      @save-success="handleEditorSaveSuccess"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { Plus, Search, Timer, Document, View, Edit, Download, Delete, Upload, Setting } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import apiClient from '../utils/api'
import TemplateEditor from './TemplateEditor.vue'

// Props
const props = defineProps({
  userInfo: {
    type: Object,
    required: true
  }
})

// 状态管理
const importDialogVisible = ref(false)
const deleteDialogVisible = ref(false)
const editorVisible = ref(false)
const importLoading = ref(false)
const deleteLoading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const currentTemplateId = ref(null)
const isEditMode = ref(false)

// 搜索和筛选
const searchKeyword = ref('')
const selectedCategoryId = ref('')

// 数据
const templates = ref([])
const categories = ref([])
const fileList = ref([])

// 导入表单
const importForm = reactive({
  name: '',
  description: '',
  categoryIds: []
})

// 权限判断
const hasImportPermission = computed(() => {
  // 只有admin角色拥有导入权限
  return props.userInfo.role === 'admin'
})

const hasEditPermission = computed(() => {
  // 只有admin角色拥有编辑权限
  return props.userInfo.role === 'admin'
})

const hasDownloadPermission = computed(() => {
  // 所有角色都有下载权限
  return true
})

const hasCategoryManagePermission = computed(() => {
  // 只有admin角色拥有分类管理权限
  return props.userInfo.role === 'admin'
})

// 加载模板列表
const loadTemplates = async () => {
  try {
    const response = await apiClient.get('/api/template/list', {
      params: {
        categoryId: selectedCategoryId.value || undefined,
        keyword: searchKeyword.value || undefined,
        page: currentPage.value,
        size: pageSize.value
      }
    })
    console.log('模板列表响应:', response.data)
    console.log('模板列表数据:', response.data.data.items)
    // 打印每个模板的分类信息
    response.data.data.items.forEach((template, index) => {
      console.log(`模板${index + 1}分类:`, template.categories)
    })
    templates.value = response.data.data.items
    total.value = response.data.data.total
  } catch (error) {
    console.error('加载模板列表失败:', error)
  }
}

// 加载分类列表
const loadCategories = async () => {
  try {
    const response = await apiClient.get('/api/template/category/list')
    categories.value = response.data.data
    // 如果没有分类，创建一个默认分类
    if (categories.value.length === 0) {
      try {
        await apiClient.post('/api/template/category/create', {
          name: '默认分类',
          description: '系统默认分类'
        })
        // 重新加载分类列表
        const newResponse = await apiClient.get('/api/template/category/list')
        categories.value = newResponse.data.data
      } catch (createError) {
        console.error('创建默认分类失败:', createError)
        // 创建失败时，使用本地默认分类
        categories.value = [{
          id: 0,
          name: '默认分类',
          description: '系统默认分类',
          templateCount: 0
        }]
      }
    }
  } catch (error) {
    console.error('加载分类列表失败:', error)
    // 加载失败时，使用默认分类
    categories.value = [{
      id: 0,
      name: '默认分类',
      description: '系统默认分类',
      templateCount: 0
    }]
  }
}

// 处理文件选择
const handleFileChange = (file) => {
  fileList.value = [file]
}

// 处理导入模板
const handleImportTemplate = () => {
  importForm.name = ''
  importForm.description = ''
  importForm.categoryIds = []
  fileList.value = []
  importDialogVisible.value = true
}

// 提交导入
const handleSubmitImport = async () => {
  if (!importForm.name) {
    ElMessage.warning('请输入模板名称')
    return
  }
  if (fileList.value.length === 0) {
    ElMessage.warning('请选择文件')
    return
  }

  console.log('开始导入模板')
  console.log('文件信息:', fileList.value[0])
  console.log('模板名称:', importForm.name)
  console.log('模板描述:', importForm.description)
  console.log('分类ID:', importForm.categoryIds)

  importLoading.value = true
  try {
    const formData = new FormData()
    formData.append('file', fileList.value[0].raw)
    formData.append('name', importForm.name)
    formData.append('description', importForm.description)
    // 处理分类ID，确保即使没有选择分类也能正确提交
    if (importForm.categoryIds && importForm.categoryIds.length > 0) {
      importForm.categoryIds.forEach(id => {
        formData.append('categoryIds', id)
      })
    }

    console.log('准备发送请求')
    const response = await apiClient.post('/api/template/import', formData)
    console.log('请求成功:', response.data)

    importDialogVisible.value = false
    ElMessage.success('导入成功')
    loadTemplates()
  } catch (error) {
    console.error('导入模板失败:', error)
    console.error('错误详情:', {
      status: error.response?.status,
      data: error.response?.data,
      message: error.message
    })
    ElMessage.error('导入失败，请重试')
  } finally {
    importLoading.value = false
  }
}

// 处理查看模板
const handleViewTemplate = (template) => {
  currentTemplateId.value = template.id
  isEditMode.value = false
  editorVisible.value = true
}

// 处理编辑模板
const handleEditTemplate = (template) => {
  currentTemplateId.value = template.id
  isEditMode.value = true
  editorVisible.value = true
}

// 处理编辑器保存成功
const handleEditorSaveSuccess = () => {
  loadTemplates()
}

// 处理下载模板
const handleDownloadTemplate = async (id) => {
  try {
    const response = await apiClient.get(`/api/template/download/${id}`, {
      responseType: 'blob'
    })
    const blob = new Blob([response.data])
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `template_${id}.${templates.value.find(t => t.id === id)?.format || 'txt'}`
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    window.URL.revokeObjectURL(url)
  } catch (error) {
    console.error('下载模板失败:', error)
    ElMessage.error('下载失败，请重试')
  }
}

// 处理删除模板
const handleDeleteTemplate = (id) => {
  deleteDialogVisible.value = true
  // 存储要删除的模板ID
  window.deleteTemplateId = id
}

// 确认删除
const handleConfirmDelete = async () => {
  const id = window.deleteTemplateId
  if (!id) return

  deleteLoading.value = true
  try {
    await apiClient.delete(`/api/template/delete/${id}`)
    deleteDialogVisible.value = false
    ElMessage.success('删除成功')
    loadTemplates()
  } catch (error) {
    console.error('删除模板失败:', error)
    ElMessage.error('删除失败，请重试')
  } finally {
    deleteLoading.value = false
  }
}

// 处理分类管理
const handleManageCategories = () => {
  // 跳转到分类管理页
  console.log('管理分类')
}

// 处理选择分类
const handleSelectCategory = (categoryId) => {
  selectedCategoryId.value = categoryId
  loadTemplates()
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadTemplates()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  loadTemplates()
}

// 工具函数
const formatDate = (date) => {
  return new Date(date).toLocaleString()
}

const formatSize = (size) => {
  if (size < 1024) return size + ' B'
  if (size < 1024 * 1024) return (size / 1024).toFixed(2) + ' KB'
  return (size / (1024 * 1024)).toFixed(2) + ' MB'
}

const getFormatTagType = (format) => {
  const typeMap = {
    'docx': 'primary',
    'pdf': 'warning',
    'md': 'success',
    'txt': 'info'
  }
  return typeMap[format.toLowerCase()] || 'info'
}

// 组件挂载时加载数据
onMounted(() => {
  loadCategories()
  loadTemplates()
})
</script>

<style scoped>
/* 全局样式变量 */
:root {
  /* 色彩系统 */
  --primary-color: #165DFF;
  --success-color: #00B42A;
  --warning-color: #FF7D00;
  --danger-color: #F53F3F;
  --info-color: #86909C;
  
  /* 中性色 */
  --text-primary: #1D2129;
  --text-secondary: #4E5969;
  --text-tertiary: #86909C;
  --text-quaternary: #C9CDD4;
  
  --border-light: #E5E6EB;
  --border-medium: #C9CDD4;
  
  --bg-white: #FFFFFF;
  --bg-light: #F2F3F5;
  --bg-medium: #E5E6EB;
  
  /* 间距系统 */
  --spacing-xs: 8px;
  --spacing-sm: 16px;
  --spacing-md: 24px;
  --spacing-lg: 32px;
  --spacing-xl: 48px;
  
  /* 圆角系统 */
  --border-radius-sm: 4px;
  --border-radius-md: 8px;
  --border-radius-lg: 12px;
  --border-radius-xl: 16px;
  
  /* 阴影系统 */
  --shadow-sm: 0 2px 8px rgba(0, 0, 0, 0.08);
  --shadow-md: 0 4px 16px rgba(0, 0, 0, 0.12);
  --shadow-lg: 0 8px 32px rgba(0, 0, 0, 0.16);
  
  /* 字体系统 */
  --font-size-xs: 12px;
  --font-size-sm: 14px;
  --font-size-md: 16px;
  --font-size-lg: 18px;
  --font-size-xl: 20px;
  --font-size-xxl: 24px;
  
  --font-weight-normal: 400;
  --font-weight-medium: 500;
  --font-weight-semibold: 600;
  --font-weight-bold: 700;
}

.template-manager {
  padding: var(--spacing-lg);
  background-color: var(--bg-light);
  min-height: calc(100vh - 80px);
}

/* 页面头部样式 */
.page-header {
  margin-bottom: var(--spacing-lg);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  background-color: var(--bg-white);
  padding: var(--spacing-lg);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
}

.header-info {
  flex: 1;
}

.breadcrumb {
  margin-bottom: var(--spacing-sm);
  font-size: var(--font-size-sm);
}

.breadcrumb .el-breadcrumb__item:last-child .el-breadcrumb__inner {
  color: var(--text-primary);
  font-weight: var(--font-weight-medium);
}

.page-title {
  margin: 0 0 var(--spacing-xs) 0;
  color: var(--text-primary);
  font-size: var(--font-size-xxl);
  font-weight: var(--font-weight-semibold);
  line-height: 1.2;
}

.page-description {
  margin: 0;
  color: var(--text-tertiary);
  font-size: var(--font-size-md);
  line-height: 1.5;
}

.header-actions {
  display: flex;
  gap: var(--spacing-sm);
  align-items: center;
}

.action-button {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  padding: 10px 20px;
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-medium);
  border-radius: var(--border-radius-md);
  transition: all 0.2s ease;
}

.action-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* 主内容区域 */
.main-content {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.main-card,
.category-card {
  border-radius: var(--border-radius-lg);
  overflow: hidden;
}

/* 搜索和筛选 */
.search-filter {
  display: flex;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-md);
  align-items: center;
  flex-wrap: wrap;
}

.search-input {
  width: 300px;
  border-radius: var(--border-radius-md);
  border: 1px solid var(--border-light);
  transition: all 0.2s ease;
}

.search-input:focus-within {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(22, 93, 255, 0.1);
}

.category-select {
  width: 200px;
}

.search-button {
  white-space: nowrap;
}

/* 模板列表 */
.template-list {
  margin-bottom: var(--spacing-md);
}

.template-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: var(--spacing-md);
}

.template-card {
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: var(--border-radius-lg);
  overflow: hidden;
}

.template-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--spacing-sm);
}

.template-name {
  margin: 0;
  color: var(--text-primary);
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  flex: 1;
  margin-right: var(--spacing-sm);
}

.card-body {
  margin-bottom: var(--spacing-sm);
}

.template-description {
  margin: 0 0 var(--spacing-sm) 0;
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.template-info {
  display: flex;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-sm);
  font-size: var(--font-size-xs);
  color: var(--text-tertiary);
}

.info-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.template-categories {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  margin-bottom: var(--spacing-sm);
}

.card-footer {
  display: flex;
  gap: var(--spacing-xs);
  justify-content: flex-start;
  padding-top: var(--spacing-sm);
  border-top: 1px solid var(--border-light);
}

/* 分类管理 */
.category-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-sm);
}

.category-title {
  margin: 0;
  color: var(--text-primary);
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
}

.category-list {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
}

.category-tag {
  cursor: pointer;
  padding: 8px 16px;
  border-radius: var(--border-radius-md);
  transition: all 0.2s ease;
}

.category-tag:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-sm);
}

.category-count {
  font-size: var(--font-size-xs);
  opacity: 0.7;
  margin-left: 4px;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  margin-top: var(--spacing-md);
}

/* 弹窗样式 */
.import-dialog {
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-lg);
  overflow: hidden;
  max-width: 600px;
}

.import-dialog .el-dialog__header {
  background-color: var(--bg-white);
  border-bottom: 1px solid var(--border-light);
  padding: var(--spacing-md);
}

.import-dialog .el-dialog__title {
  color: var(--text-primary);
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
}

.import-dialog .el-dialog__body {
  padding: var(--spacing-md);
  background-color: var(--bg-white);
}

.import-dialog .el-dialog__footer {
  background-color: var(--bg-white);
  border-top: 1px solid var(--border-light);
  padding: var(--spacing-md);
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-sm);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-md);
  }
  
  .header-actions {
    width: 100%;
    justify-content: flex-start;
  }
  
  .search-filter {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-input,
  .category-select {
    width: 100%;
  }
  
  .template-cards {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  }
}

@media (max-width: 768px) {
  .template-manager {
    padding: var(--spacing-md);
  }
  
  .header-content {
    padding: var(--spacing-md);
  }
  
  .page-title {
    font-size: var(--font-size-xl);
  }
  
  .action-button {
    padding: 8px 16px;
    font-size: var(--font-size-sm);
  }
  
  .template-cards {
    grid-template-columns: 1fr;
  }
  
  .card-footer {
    flex-wrap: wrap;
  }
  
  .category-list {
    justify-content: center;
  }
}
</style>
