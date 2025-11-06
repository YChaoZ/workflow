<template>
  <div class="role-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增角色
          </el-button>
        </div>
      </template>

      <el-table :data="roleList" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="roleName" label="角色名称" width="150" />
        <el-table-column prop="roleCode" label="角色编码" width="150" />
        <el-table-column prop="roleType" label="角色类型" width="120">
          <template #default="{ row }">
            <el-tag :type="row.roleType === 'SYSTEM' ? 'danger' : 'primary'">
              {{ row.roleType === 'SYSTEM' ? '系统角色' : '自定义角色' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleAssignPermissions(row)">
              <el-icon><Key /></el-icon>
              分配权限
            </el-button>
            <el-button link type="primary" size="small" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button 
              link 
              type="danger" 
              size="small" 
              @click="handleDelete(row)"
              :disabled="row.roleType === 'SYSTEM'"
            >
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑角色对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" placeholder="请输入角色编码" />
        </el-form-item>
        
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        
        <el-form-item label="角色类型">
          <el-radio-group v-model="form.roleType">
            <el-radio label="CUSTOM">自定义角色</el-radio>
            <el-radio label="SYSTEM">系统角色</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="描述">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入角色描述"
          />
        </el-form-item>
        
        <el-form-item label="状态">
          <el-switch
            v-model="form.status"
            :active-value="1"
            :inactive-value="0"
            active-text="启用"
            inactive-text="停用"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 分配权限对话框 -->
    <el-dialog
      v-model="permissionDialogVisible"
      title="分配权限"
      width="600px"
    >
      <el-tree
        ref="permissionTreeRef"
        :data="permissionTree"
        :props="permissionTreeProps"
        node-key="id"
        show-checkbox
        default-expand-all
      >
        <template #default="{ data }">
          <span>{{ data.permissionName }}</span>
        </template>
      </el-tree>

      <template #footer>
        <el-button @click="permissionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitPermissions" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import { Plus, Edit, Delete, Key } from '@element-plus/icons-vue'
import { roleApi, permissionApi } from '@/api/organization'

// 角色列表
const roleList = ref<any[]>([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = computed(() => form.id ? '编辑角色' : '新增角色')
const formRef = ref<FormInstance>()
const submitting = ref(false)

// 表单数据
const form = reactive({
  id: null as number | null,
  roleCode: '',
  roleName: '',
  roleType: 'CUSTOM',
  description: '',
  status: 1
})

// 表单验证规则
const rules = {
  roleCode: [
    { required: true, message: '请输入角色编码', trigger: 'blur' }
  ],
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' }
  ]
}

// 权限分配
const permissionDialogVisible = ref(false)
const permissionTree = ref<any[]>([])
const permissionTreeRef = ref()
const permissionTreeProps = {
  children: 'children',
  label: 'permissionName'
}
const currentRoleId = ref<number | null>(null)

// 加载角色列表
const loadRoleList = async () => {
  try {
    const res = await roleApi.getList()
    if (res.code === 200) {
      roleList.value = res.data || []
    }
  } catch (error) {
    console.error('加载角色列表失败:', error)
    ElMessage.error('加载角色列表失败')
  }
}

// 加载权限树
const loadPermissionTree = async () => {
  try {
    const res = await permissionApi.getTree()
    if (res.code === 200) {
      permissionTree.value = res.data || []
    }
  } catch (error) {
    console.error('加载权限树失败:', error)
    ElMessage.error('加载权限树失败')
  }
}

// 添加角色
const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

// 编辑角色
const handleEdit = async (role: any) => {
  try {
    const res = await roleApi.getById(role.id)
    if (res.code === 200) {
      const data = res.data
      form.id = data.id
      form.roleCode = data.roleCode
      form.roleName = data.roleName
      form.roleType = data.roleType || 'CUSTOM'
      form.description = data.description || ''
      form.status = data.status
      dialogVisible.value = true
    }
  } catch (error) {
    console.error('加载角色详情失败:', error)
    ElMessage.error('加载角色详情失败')
  }
}

// 删除角色
const handleDelete = async (role: any) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除角色"${role.roleName}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const res = await roleApi.delete(role.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadRoleList()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除角色失败:', error)
      ElMessage.error(error.response?.data?.message || '删除失败')
    }
  }
}

// 分配权限
const handleAssignPermissions = async (role: any) => {
  currentRoleId.value = role.id
  
  // 加载权限树
  await loadPermissionTree()
  
  // 加载角色已有权限
  try {
    const res = await roleApi.getPermissions(role.id)
    if (res.code === 200) {
      const permissionIds = res.data || []
      // 设置选中的权限
      permissionTreeRef.value?.setCheckedKeys(permissionIds)
    }
  } catch (error) {
    console.error('加载角色权限失败:', error)
  }
  
  permissionDialogVisible.value = true
}

// 提交权限分配
const handleSubmitPermissions = async () => {
  if (!currentRoleId.value) return
  
  submitting.value = true
  try {
    const checkedKeys = permissionTreeRef.value.getCheckedKeys()
    const halfCheckedKeys = permissionTreeRef.value.getHalfCheckedKeys()
    const permissionIds = [...checkedKeys, ...halfCheckedKeys]
    
    const res = await roleApi.assignPermissions(currentRoleId.value, permissionIds)
    if (res.code === 200) {
      ElMessage.success('权限分配成功')
      permissionDialogVisible.value = false
    } else {
      ElMessage.error(res.message || '权限分配失败')
    }
  } catch (error: any) {
    console.error('权限分配失败:', error)
    ElMessage.error(error.response?.data?.message || '权限分配失败')
  } finally {
    submitting.value = false
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const data = { ...form }
        delete data.id
        
        let res
        if (form.id) {
          res = await roleApi.update(form.id, data)
        } else {
          res = await roleApi.create(data)
        }
        
        if (res.code === 200) {
          ElMessage.success(form.id ? '更新成功' : '创建成功')
          dialogVisible.value = false
          loadRoleList()
        } else {
          ElMessage.error(res.message || '操作失败')
        }
      } catch (error: any) {
        console.error('提交失败:', error)
        ElMessage.error(error.response?.data?.message || '操作失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  form.id = null
  form.roleCode = ''
  form.roleName = ''
  form.roleType = 'CUSTOM'
  form.description = ''
  form.status = 1
  formRef.value?.clearValidate()
}

onMounted(() => {
  loadRoleList()
})
</script>

<style scoped lang="scss">
.role-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>

