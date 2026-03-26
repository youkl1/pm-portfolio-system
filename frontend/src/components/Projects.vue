<template>
  <div class="projects-container">
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
              <div class="stat-value">{{ getProjectByCategory('核心业务系统', categories, projects) }}</div>
              <div class="stat-label">核心业务系统</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ getProjectByCategory('管理支撑系统', categories, projects) }}</div>
              <div class="stat-label">管理支撑系统</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="toolbar">
      <div class="toolbar-left">
        <div class="toolbar-header">
          <h2>作品列表</h2>
          <button v-if="userInfo.role === 'admin'" @click="openAddModal" class="add-btn">
            <span class="add-icon">+</span>
            新增作品
          </button>
        </div>
        <div class="search-filter-container">
          <div class="search-box">
            <input type="text" v-model="searchKeyword" placeholder="搜索作品标题或描述..." class="search-input">
            <button @click="handleSearch" class="search-btn">搜索</button>
          </div>
          <div class="category-filter">
            <label>分类筛选：</label>
            <div class="category-checkbox-group">
              <label class="category-checkbox-item">
                <input type="checkbox" :checked="localSelectedCategoryIds.includes('')" @change="handleCheckboxChange('')">
                <span>全选</span>
              </label>
              <label v-for="category in categories" :key="category.id" class="category-checkbox-item">
                <input type="checkbox" :checked="localSelectedCategoryIds.includes(category.id.toString())" @change="handleCheckboxChange(category.id.toString())">
                <span>{{ category.name }}</span>
              </label>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="project-grid">
      <div v-for="project in projects" :key="project.id" class="project-card">
        <a :href="project.detailLink" target="_blank" class="project-cover-link">
            <div class="project-cover-container">
              <img :src="project.coverImage" alt="封面" class="project-cover">
              <div v-if="getCategoryName(project.categoryId, categories)" class="project-category-badge">
                {{ getCategoryName(project.categoryId, categories) }}
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
</template>

<script>
import { formatDate, getCategoryName, getProjectByCategory } from '../utils/utils.js'

export default {
  name: 'Projects',
  props: {
    userInfo: {
      type: Object,
      required: true
    },
    projects: {
      type: Array,
      default: () => []
    },
    categories: {
      type: Array,
      default: () => []
    },
    selectedCategoryIds: {
      type: Array,
      default: () => []
    },
    allProjects: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      showAddModal: false,
      localSelectedCategoryIds: [...this.selectedCategoryIds],
      searchKeyword: ''
    }
  },
  watch: {
    selectedCategoryIds: {
      handler(newValue) {
        this.localSelectedCategoryIds = [...newValue]
      },
      deep: true
    }
  },
  methods: {
    handleCheckboxChange(value) {
      const index = this.localSelectedCategoryIds.indexOf(value)
      if (index > -1) {
        this.localSelectedCategoryIds.splice(index, 1)
      } else {
        this.localSelectedCategoryIds.push(value)
      }
      this.$emit('update:selectedCategoryIds', this.localSelectedCategoryIds)
      this.$emit('filter-projects')
    },
    filterProjects() {
      this.$emit('filter-projects')
    },
    editProject(project) {
      this.$emit('edit-project', project)
    },
    showDeleteConfirm(id) {
      this.$emit('show-delete-confirm', id)
    },
    copyGithubLink(url) {
      this.$emit('copy-github-link', url)
    },
    copyDescription(description) {
      this.$emit('copy-description', description)
    },
    openAddModal() {
      this.$emit('open-add-modal')
    },
    handleSearch() {
      this.$emit('search-projects', this.searchKeyword)
    },
    formatDate,
    getCategoryName,
    getProjectByCategory
  }
}
</script>

<style scoped>
/* 作品管理页面 */
.projects-container {
  padding: var(--spacing-md);
}

/* 看板样式 */
.dashboard {
  margin-bottom: var(--spacing-md);
}

.dashboard-card {
  background: var(--background-white);
  border-radius: var(--border-radius-md);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
  transition: all 0.3s ease;
}

.dashboard-card:hover {
  box-shadow: var(--shadow-md);
}

.dashboard-card-header {
  padding: var(--spacing-sm) var(--spacing-md);
  border-bottom: 1px solid var(--border-light);
  background: var(--background-light);
}

.dashboard-card-header h3 {
  margin: 0;
  font-size: var(--font-size-md);
  font-weight: 600;
  color: var(--text-primary);
}

.dashboard-card-body {
  padding: var(--spacing-md);
}

.dashboard-stats {
  display: flex;
  gap: var(--spacing-md);
  flex-wrap: wrap;
}

.stat-item {
  flex: 1;
  min-width: 120px;
  text-align: center;
  padding: var(--spacing-sm);
  background: var(--background-light);
  border-radius: var(--border-radius-sm);
  transition: all 0.3s ease;
}

.stat-item:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-sm);
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: var(--primary-color);
  margin-bottom: var(--spacing-xs);
}

.stat-label {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

/* 工具栏样式 */
.toolbar {
  margin-bottom: var(--spacing-md);
  padding: 0 var(--spacing-xs);
}

.toolbar-left {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.toolbar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.toolbar-header h2 {
  margin: 0;
  font-size: var(--font-size-xl);
  font-weight: 600;
  color: var(--text-primary);
}

/* 新增按钮样式优化 */
.add-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: var(--success-color);
  color: white;
  border: none;
  border-radius: var(--border-radius-md);
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 180, 42, 0.2);
  white-space: nowrap;
}

.add-btn:hover {
  background: #73d13d;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 180, 42, 0.3);
}

.add-icon {
  font-size: 16px;
  font-weight: bold;
}

.category-filter {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
}

.category-filter label {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  white-space: nowrap;
}

.category-checkbox-group {
  display: flex;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
}

.category-checkbox-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  cursor: pointer;
  padding: 4px 8px;
  border-radius: var(--border-radius-sm);
  transition: all 0.2s ease;
}

.category-checkbox-item:hover {
  background-color: var(--border-light);
}

.category-checkbox-item input[type="checkbox"] {
  margin: 0;
  cursor: pointer;
}

/* 搜索框样式 */
.search-box {
  display: flex;
  gap: 8px;
  margin-bottom: var(--spacing-sm);
  max-width: 400px;
}

.search-input {
  flex: 1;
  padding: 10px 16px;
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-md);
  font-size: var(--font-size-sm);
  transition: all 0.3s ease;
}

.search-input:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(22, 93, 255, 0.1);
}

.search-btn {
  padding: 10px 20px;
  background: var(--primary-color);
  color: white;
  border: none;
  border-radius: var(--border-radius-md);
  cursor: pointer;
  font-size: var(--font-size-sm);
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(22, 93, 255, 0.2);
}

.search-btn:hover {
  background: var(--primary-hover);
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(22, 93, 255, 0.2);
}

/* 新增按钮样式优化 */
.add-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  background: var(--success-color);
  color: white;
  border: none;
  border-radius: var(--border-radius-md);
  cursor: pointer;
  font-size: var(--font-size-sm);
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 180, 42, 0.2);
}

.add-icon {
  font-size: 16px;
  font-weight: bold;
}

/* 作品网格 */
.project-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: var(--spacing-md);
}

.project-card {
  background: var(--background-white);
  border-radius: var(--border-radius-md);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
  transition: all 0.3s ease;
}

.project-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.project-cover-link {
  display: block;
  position: relative;
}

.project-cover-container {
  position: relative;
  width: 100%;
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

.project-category-badge {
  position: absolute;
  top: var(--spacing-sm);
  left: var(--spacing-sm);
  background: rgba(22, 93, 255, 0.9);
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: var(--font-size-xs);
  font-weight: 500;
}

.project-info {
  padding: var(--spacing-sm);
}

.project-title {
  margin: 0 0 var(--spacing-sm) 0;
  font-size: var(--font-size-md);
  font-weight: 600;
  color: var(--text-primary);
  line-height: 1.4;
}

.description-container {
  position: relative;
  margin-bottom: var(--spacing-sm);
}

.project-description {
  margin: 0;
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.description-tooltip {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: var(--background-white);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-sm);
  box-shadow: var(--shadow-md);
  padding: var(--spacing-sm);
  z-index: 10;
  margin-top: 8px;
  max-width: 100%;
  max-height: 200px;
  overflow-y: auto;
  display: none;
}

.description-container:hover .description-tooltip {
  display: block;
}

.tooltip-content {
  font-size: var(--font-size-sm);
  color: var(--text-primary);
  line-height: 1.5;
  margin-bottom: var(--spacing-xs);
}

.tooltip-copy-btn {
  background: var(--border-light);
  border: none;
  border-radius: var(--border-radius-sm);
  padding: 4px 8px;
  font-size: var(--font-size-xs);
  cursor: pointer;
  transition: all 0.3s ease;
}

.tooltip-copy-btn:hover {
  background: var(--border-color);
}

.project-actions-row {
  margin-bottom: var(--spacing-sm);
}

.detail-link {
  display: inline-block;
  padding: 6px 12px;
  background: var(--border-light);
  color: var(--text-primary);
  text-decoration: none;
  border-radius: var(--border-radius-sm);
  font-size: var(--font-size-sm);
  transition: all 0.3s ease;
}

.detail-link:hover {
  background: var(--border-color);
  color: var(--primary-color);
}

.project-meta {
  font-size: var(--font-size-xs);
  color: var(--text-tertiary);
  margin-bottom: var(--spacing-sm);
}

.github-container {
  border-top: 1px solid var(--border-light);
  padding-top: var(--spacing-sm);
}

.github-header {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 8px;
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.github-url-container {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.github-url {
  flex: 1;
  font-size: 13px;
  color: var(--primary-color);
  text-decoration: none;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.github-url:hover {
  text-decoration: underline;
}

.copy-btn {
  background: var(--border-light);
  border: none;
  border-radius: var(--border-radius-sm);
  padding: 4px 6px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.copy-btn:hover {
  background: var(--border-color);
}

.copy-icon {
  font-size: var(--font-size-xs);
}

.project-admin-actions {
  display: flex;
  gap: 8px;
  padding: var(--spacing-sm);
  border-top: 1px solid var(--border-light);
  background: var(--background-light);
}

.edit-btn {
  flex: 1;
  padding: 6px 12px;
  background: var(--primary-color);
  color: white;
  border: none;
  border-radius: var(--border-radius-sm);
  cursor: pointer;
  font-size: var(--font-size-sm);
  transition: all 0.3s ease;
}

.edit-btn:hover {
  background: var(--primary-hover);
}

.delete-btn {
  flex: 1;
  padding: 6px 12px;
  background: var(--error-color);
  color: white;
  border: none;
  border-radius: var(--border-radius-sm);
  cursor: pointer;
  font-size: var(--font-size-sm);
  transition: all 0.3s ease;
}

.delete-btn:hover {
  background: #ff7875;
}
</style>