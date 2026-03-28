<template>
  <div class="organization-form">
    <el-form :model="organization" label-width="120px">
      <el-form-item label="组织名称" required>
        <el-input v-model="organization.orgName" placeholder="请输入组织名称" />
      </el-form-item>
      <el-form-item label="组织类型" required>
        <el-select v-model="organization.orgType" placeholder="请选择组织类型">
          <el-option label="集团" value="group" />
          <el-option label="部门" value="department" />
          <el-option label="项目组" value="project" />
        </el-select>
      </el-form-item>
      <el-form-item label="上级组织">
        <el-select v-model="organization.parentId" placeholder="请选择上级组织">
          <el-option label="无（根组织）" value="null" />
          <el-option 
            v-for="org in organizations" 
            :key="org.id" 
            :label="org.orgName" 
            :value="org.id" 
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <div class="form-actions">
          <el-button @click="$emit('cancel')">取消</el-button>
          <el-button type="primary" @click="handleSave">保存</el-button>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'

// 接收props
const props = defineProps({
  organization: {
    type: Object,
    default: () => ({
      id: null,
      tenantId: 1,
      parentId: null,
      orgName: '',
      orgType: 'department'
    })
  },
  organizations: {
    type: Array,
    default: () => []
  }
})

// 定义事件
const emit = defineEmits(['save', 'cancel'])

// 处理保存
const handleSave = () => {
  // 处理parentId为null的情况
  const org = { ...props.organization }
  if (org.parentId === 'null') {
    org.parentId = null
  }
  emit('save', org)
}
</script>

<style scoped>
.organization-form {
  padding: 20px 0;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>