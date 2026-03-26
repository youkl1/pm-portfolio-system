/**
 * 格式化日期
 * @param {string} dateString - 日期字符串
 * @returns {string} 格式化后的日期
 */
export function formatDate(dateString) {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

/**
 * 根据分类ID获取分类名称
 * @param {number} categoryId - 分类ID
 * @param {Array} categories - 分类列表
 * @returns {string} 分类名称
 */
export function getCategoryName(categoryId, categories) {
  if (!categoryId) return ''
  const category = categories.find(cat => cat.id === categoryId)
  return category ? category.name : ''
}

/**
 * 根据分类名称获取作品数量
 * @param {string} categoryName - 分类名称
 * @param {Array} categories - 分类列表
 * @param {Array} projects - 作品列表
 * @returns {number} 作品数量
 */
export function getProjectByCategory(categoryName, categories, projects) {
  const category = categories.find(cat => cat.name === categoryName)
  if (!category) return 0
  return projects.filter(project => project.categoryId === category.id).length
}
