<template>
  <div class="permission-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>权限管理</span>
          <el-button type="primary" @click="handleAdd(null)">
            <el-icon><Plus /></el-icon>
            新增根权限
          </el-button>
        </div>
      </template>

      <el-tree
        :data="permissionTree"
        :props="treeProps"
        node-key="id"
        default-expand-all
        :expand-on-click-node="false"
      >
        <template #default="{ node, data }">
          <div class="tree-node">
            <span class="node-label">
              <el-icon><Lock /></el-icon>
              {{ data.permissionName }}
              <el-tag :type="getTypeTagType(data.permissionType)" size="small">
                {{ getTypeText(data.permissionType) }}
              </el-tag>
              <el-tag v-if="data.status === 0" type="danger" size="small">停用</el-tag>
            </span>
            <span class="node-actions">
              <el-button 
                v-if="data.permissionType === 'MENU'" 
                link 
                type="primary" 
                size="small" 
                @click.stop="handleAdd(data)"
              >
                <el-icon><Plus /></el-icon>
                添加子权限
              </el-button>
              <el-button link type="primary" size="small" @click.stop="handleEdit(data)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button link type="danger" size="small" @click.stop="handleDelete(data)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </span>
          </div>
        </template>
      </el-tree>
    </el-card>

    <!-- 添加/编辑权限对话框 -->
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
        <el-form-item label="权限编码" prop="permissionCode">
          <el-input v-model="form.permissionCode" placeholder="请输入权限编码" />
        </el-form-item>
        
        <el-form-item label="权限名称" prop="permissionName">
          <el-input v-model="form.permissionName" placeholder="请输入权限名称" />
        </el-form-item>
        
        <el-form-item label="权限类型" prop="permissionType">
          <el-select v-model="form.permissionType" placeholder="请选择权限类型">
            <el-option label="菜单" value="MENU" />
            <el-option label="按钮" value="BUTTON" />
            <el-option label="接口" value="API" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="父权限">
          <el-input v-model="parentPermissionName" disabled />
        </el-form-item>
        
        <el-form-item label="资源路径">
          <el-input v-model="form.resourcePath" placeholder="菜单路由或API路径" />
        </el-form-item>
        
        <el-form-item label="请求方法" v-if="form.permissionType === 'API'">
          <el-select v-model="form.resourceMethod" placeholder="请选择请求方法">
            <el-option label="GET" value="GET" />
            <el-option label="POST" value="POST" />
            <el-option label="PUT" value="PUT" />
            <el-option label="DELETE" value="DELETE" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="图标" v-if="form.permissionType === 'MENU'">
          <el-input v-model="form.icon" placeholder="请输入图标类名" />
        </el-form-item>
        
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        
        <el-form-item label="描述">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入权限描述"
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import { Plus, Edit, Delete, Lock } from '@element-plus/icons-vue'
import { permissionApi } from '@/api/organization'

// 树形数据
const permissionTree = ref<any[]>([])
const treeProps = {
  children: 'children',
  label: 'permissionName'
}

// 对话框
const dialogVisible = ref(false)
const dialogTitle = computed(() => form.id ? '编辑权限' : '新增权限')
const formRef = ref<FormInstance>()
const submitting = ref(false)

// 表单数据
const form = reactive({
  id: null as number | null,
  permissionCode: '',
  permissionName: '',
  permissionType: 'MENU',
  parentId: 0,
  resourcePath: '',
  resourceMethod: '',
  icon: '',
  sortOrder: 0,
  description: '',
  status: 1
})

const parentPermissionName = ref('无')

// 表单验证规则
const rules = {
  permissionCode: [
    { required: true, message: '请输入权限编码', trigger: 'blur' }
  ],
  permissionName: [
    { required: true, message: '请输入权限名称', trigger: 'blur' }
  ],
  permissionType: [
    { required: true, message: '请选择权限类型', trigger: 'change' }
  ]
}

// 获取类型标签样式
const getTypeTagType = (type: string) => {
  const typeMap: Record<string, any> = {
    'MENU': 'primary',
    'BUTTON': 'success',
    'API': 'warning'
  }
  return typeMap[type] || 'info'
}

// 获取类型文本
const getTypeText = (type: string) => {
  const textMap: Record<string, string> = {
    'MENU': '菜单',
    'BUTTON': '按钮',
    'API': '接口'
  }
  return textMap[type] || type
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

// 添加权限
const handleAdd = (parent: any) => {
  resetForm()
  if (parent) {
    form.parentId = parent.id
    parentPermissionName.value = parent.permissionName
    // 如果父节点是菜单，默认子节点也是菜单
    form.permissionType = 'MENU'
  } else {
    form.parentId = 0
    parentPermissionName.value = '无'
    form.permissionType = 'MENU'
  }
  dialogVisible.value = true
}

// 编辑权限
const handleEdit = async (permission: any) => {
  try {
    const res = await permissionApi.getById(permission.id)
    if (res.code === 200) {
      const data = res.data
      form.id = data.id
      form.permissionCode = data.permissionCode
      form.permissionName = data.permissionName
      form.permissionType = data.permissionType
      form.parentId = data.parentId || 0
      form.resourcePath = data.resourcePath || ''
      form.resourceMethod = data.resourceMethod || ''
      form.icon = data.icon || ''
      form.sortOrder = data.sortOrder || 0
      form.description = data.description || ''
      form.status = data.status
      
      // 设置父权限名称
      if (data.parentId && data.parentId !== 0) {
        const parent = findPermissionById(permissionTree.value, data.parentId)
        parentPermissionName.value = parent ? parent.permissionName : '无'
      } else {
        parentPermissionName.value = '无'
      }
      
      dialogVisible.value = true
    }
  } catch (error) {
    console.error('加载权限详情失败:', error)
    ElMessage.error('加载权限详情失败')
  }
}

// 删除权限
const handleDelete = async (permission: any) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除权限"${permission.permissionName}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const res = await permissionApi.delete(permission.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadPermissionTree()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除权限失败:', error)
      ElMessage.error(error.response?.data?.message || '删除失败')
    }
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
          res = await permissionApi.update(form.id, data)
        } else {
          res = await permissionApi.create(data)
        }
        
        if (res.code === 200) {
          ElMessage.success(form.id ? '更新成功' : '创建成功')
          dialogVisible.value = false
          loadPermissionTree()
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
  form.permissionCode = ''
  form.permissionName = ''
  form.permissionType = 'MENU'
  form.parentId = 0
  form.resourcePath = ''
  form.resourceMethod = ''
  form.icon = ''
  form.sortOrder = 0
  form.description = ''
  form.status = 1
  parentPermissionName.value = '无'
  formRef.value?.clearValidate()
}

// 递归查找权限
const findPermissionById = (tree: any[], id: number): any => {
  for (const node of tree) {
    if (node.id === id) return node
    if (node.children) {
      const found = findPermissionById(node.children, id)
      if (found) return found
    }
  }
  return null
}

onMounted(() => {
  loadPermissionTree()
})
</script>

<style scoped lang="scss">
.permission-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-right: 20px;
  
  .node-label {
    display: flex;
    align-items: center;
    gap: 8px;
  }
  
  .node-actions {
    display: none;
  }
  
  &:hover .node-actions {
    display: flex;
    gap: 8px;
  }
}
</style>

