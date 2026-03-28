<template>
  <div class="tenant-form">
    <el-form :model="localTenant" label-width="120px">
      <el-form-item label="租户名称" required>
        <el-input v-model="localTenant.tenantName" placeholder="请输入租户名称"></el-input>
      </el-form-item>
      <el-form-item label="租户类型" required>
        <el-select v-model="localTenant.tenantType" placeholder="请选择租户类型" @change="handleTenantTypeChange">
          <el-option label="个人" value="personal"></el-option>
          <el-option label="团队" value="team"></el-option>
          <el-option label="企业" value="enterprise"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="状态" required>
        <el-select v-model="localTenant.status" placeholder="请选择状态">
          <el-option label="启用" value="active"></el-option>
          <el-option label="禁用" value="inactive"></el-option>
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
import { ref, reactive, watch } from 'vue';

// 接收props
const props = defineProps({
  tenant: {
    type: Object,
    default: () => ({
      id: null,
      tenantName: '',
      tenantType: 'personal',
      status: 'active'
    })
  },
  packages: {
    type: Array,
    default: () => []
  }
});

// 定义事件
const emit = defineEmits(['save', 'cancel']);

// 创建本地响应式对象
const localTenant = reactive({ ...props.tenant });

// 监听props变化，更新本地对象
watch(() => props.tenant, (newTenant) => {
  Object.assign(localTenant, newTenant);
}, { deep: true });

// 处理租户类型变更
const handleTenantTypeChange = (type) => {
  console.log('Tenant type changed:', type);
};

// 处理保存
const handleSave = () => {
  emit('save', { ...localTenant });
};
</script>

<style scoped>
.tenant-form {
  padding: 20px 0;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>