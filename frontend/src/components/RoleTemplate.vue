<template>
  <div class="role-template">
    <h3>角色模板库</h3>
    <div class="template-tabs">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="行业模板" name="industry">
          <div class="template-list">
            <el-card 
              v-for="template in industryTemplates" 
              :key="template.id" 
              class="template-card"
            >
              <template #header>
                <div class="template-header">
                  <span>{{ template.name }}</span>
                  <el-button size="small" type="primary" @click="applyTemplate(template)">应用模板</el-button>
                </div>
              </template>
              <div class="template-content">
                <p>{{ template.description }}</p>
                <div class="template-roles">
                  <h4>包含角色：</h4>
                  <ul>
                    <li v-for="role in template.roles" :key="role.name">{{ role.name }} - {{ role.description }}</li>
                  </ul>
                </div>
              </div>
            </el-card>
          </div>
        </el-tab-pane>
        <el-tab-pane label="团队规模模板" name="teamSize">
          <div class="template-list">
            <el-card 
              v-for="template in teamSizeTemplates" 
              :key="template.id" 
              class="template-card"
            >
              <template #header>
                <div class="template-header">
                  <span>{{ template.name }}</span>
                  <el-button size="small" type="primary" @click="applyTemplate(template)">应用模板</el-button>
                </div>
              </template>
              <div class="template-content">
                <p>{{ template.description }}</p>
                <div class="template-roles">
                  <h4>包含角色：</h4>
                  <ul>
                    <li v-for="role in template.roles" :key="role.name">{{ role.name }} - {{ role.description }}</li>
                  </ul>
                </div>
              </div>
            </el-card>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 应用模板对话框 -->
    <el-dialog
      v-model="applyDialogVisible"
      title="应用角色模板"
      width="600px"
    >
      <div v-if="selectedTemplate">
        <p>您正在应用 {{ selectedTemplate.name }} 模板</p>
        <p>{{ selectedTemplate.description }}</p>
        <div class="template-preview">
          <h4>包含角色：</h4>
          <ul>
            <li v-for="role in selectedTemplate.roles" :key="role.name">{{ role.name }} - {{ role.description }}</li>
          </ul>
        </div>
        <el-form-item label="应用方式">
          <el-radio-group v-model="applyMode">
            <el-radio label="1">覆盖现有角色</el-radio>
            <el-radio label="2">新增角色</el-radio>
          </el-radio-group>
        </el-form-item>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="applyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmApplyTemplate">确认应用</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import apiClient from '../utils/api'

// 状态管理
const activeTab = ref('industry')
const applyDialogVisible = ref(false)
const selectedTemplate = ref(null)
const applyMode = ref('2') // 默认新增角色

// 行业模板数据
const industryTemplates = ref([
  {
    id: 1,
    name: '电商行业',
    description: '适用于电商平台的角色权限模板',
    roles: [
      { name: '电商管理员', description: '负责电商平台整体管理' },
      { name: '商品运营', description: '负责商品管理和运营' },
      { name: '客服专员', description: '负责客户服务' },
      { name: '财务人员', description: '负责财务相关操作' }
    ]
  },
  {
    id: 2,
    name: '教育行业',
    description: '适用于教育机构的角色权限模板',
    roles: [
      { name: '教育管理员', description: '负责教育平台整体管理' },
      { name: '教师', description: '负责课程管理和教学' },
      { name: '学生', description: '学习课程' },
      { name: '家长', description: '查看学生学习情况' }
    ]
  },
  {
    id: 3,
    name: '金融行业',
    description: '适用于金融机构的角色权限模板',
    roles: [
      { name: '金融管理员', description: '负责金融平台整体管理' },
      { name: '理财顾问', description: '负责客户理财咨询' },
      { name: '风控专员', description: '负责风险控制' },
      { name: '合规专员', description: '负责合规管理' }
    ]
  }
])

// 团队规模模板数据
const teamSizeTemplates = ref([
  {
    id: 1,
    name: '小型团队（1-10人）',
    description: '适用于小型团队的角色权限模板',
    roles: [
      { name: '团队负责人', description: '负责团队整体管理' },
      { name: '开发人员', description: '负责开发工作' },
      { name: '测试人员', description: '负责测试工作' }
    ]
  },
  {
    id: 2,
    name: '中型团队（11-50人）',
    description: '适用于中型团队的角色权限模板',
    roles: [
      { name: '部门经理', description: '负责部门整体管理' },
      { name: '项目经理', description: '负责项目管理' },
      { name: '高级开发', description: '负责核心开发工作' },
      { name: '初级开发', description: '负责基础开发工作' },
      { name: '测试工程师', description: '负责测试工作' },
      { name: '产品经理', description: '负责产品规划' }
    ]
  },
  {
    id: 3,
    name: '大型团队（50人以上）',
    description: '适用于大型团队的角色权限模板',
    roles: [
      { name: '技术总监', description: '负责技术团队整体管理' },
      { name: '部门经理', description: '负责部门管理' },
      { name: '项目经理', description: '负责项目管理' },
      { name: '架构师', description: '负责系统架构设计' },
      { name: '高级开发', description: '负责核心开发工作' },
      { name: '中级开发', description: '负责重要开发工作' },
      { name: '初级开发', description: '负责基础开发工作' },
      { name: '测试主管', description: '负责测试团队管理' },
      { name: '测试工程师', description: '负责测试工作' },
      { name: '产品经理', description: '负责产品规划' },
      { name: 'UI设计师', description: '负责界面设计' }
    ]
  }
])

// 处理应用模板
const applyTemplate = (template) => {
  selectedTemplate.value = template
  applyDialogVisible.value = true
}

// 确认应用模板
const confirmApplyTemplate = async () => {
  if (!selectedTemplate.value) return

  try {
    // 这里应该调用API来应用模板
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('模板应用成功')
    applyDialogVisible.value = false
  } catch (error) {
    console.error('应用模板失败:', error)
    ElMessage.error('应用模板失败')
  }
}

// 组件挂载时的初始化
onMounted(() => {
  // 这里可以加载模板数据
})
</script>

<style scoped>
.role-template {
  margin-top: 20px;
}

.template-tabs {
  margin-top: 20px;
}

.template-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.template-card {
  margin-bottom: 20px;
}

.template-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.template-content {
  margin-top: 10px;
}

.template-content p {
  margin-bottom: 15px;
  color: #666;
}

.template-roles h4 {
  margin-bottom: 10px;
  font-size: 14px;
  color: #333;
}

.template-roles ul {
  padding-left: 20px;
  margin: 0;
}

.template-roles li {
  margin-bottom: 5px;
  font-size: 13px;
  color: #666;
}

.template-preview {
  margin: 20px 0;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.template-preview h4 {
  margin-bottom: 10px;
  font-size: 14px;
  color: #333;
}

.template-preview ul {
  padding-left: 20px;
  margin: 0;
}

.template-preview li {
  margin-bottom: 5px;
  font-size: 13px;
  color: #666;
}

.dialog-footer {
  width: 100%;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>