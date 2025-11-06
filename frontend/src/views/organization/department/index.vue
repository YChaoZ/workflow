<template>
  <div class="department-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>部门管理</span>
          <el-button type="primary" @click="handleAdd(null)">
            <el-icon><Plus /></el-icon>
            新增根部门
          </el-button>
        </div>
      </template>

      <el-tree
        :data="departmentTree"
        :props="treeProps"
        node-key="id"
        default-expand-all
        :expand-on-click-node="false"
      >
        <template #default="{ node, data }">
          <div class="tree-node">
            <span class="node-label">
              <el-icon><OfficeBuilding /></el-icon>
              {{ data.deptName }}
              <el-tag v-if="data.status === 0" type="danger" size="small">停用</el-tag>
            </span>
            <span class="node-actions">
              <el-button link type="primary" size="small" @click.stop="handleAdd(data)">
                <el-icon><Plus /></el-icon>
                添加子部门
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

    <!-- 添加/编辑部门对话框 -->
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
        <el-form-item label="部门编码" prop="deptCode">
          <el-input v-model="form.deptCode" placeholder="请输入部门编码" />
        </el-form-item>
        
        <el-form-item label="部门名称" prop="deptName">
          <el-input v-model="form.deptName" placeholder="请输入部门名称" />
        </el-form-item>
        
        <el-form-item label="父部门">
          <el-input v-model="parentDeptName" disabled />
        </el-form-item>
        
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        
        <el-form-item label="描述">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入部门描述"
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
import { Plus, Edit, Delete, OfficeBuilding } from '@element-plus/icons-vue'
import { departmentApi } from '@/api/organization'

// 树形数据
const departmentTree = ref<any[]>([])
const treeProps = {
  children: 'children',
  label: 'deptName'
}

// 对话框
const dialogVisible = ref(false)
const dialogTitle = computed(() => form.id ? '编辑部门' : '新增部门')
const formRef = ref<FormInstance>()
const submitting = ref(false)

// 表单数据
const form = reactive({
  id: null as number | null,
  deptCode: '',
  deptName: '',
  parentId: 0,
  sortOrder: 0,
  description: '',
  status: 1
})

const parentDeptName = ref('无')

// 表单验证规则
const rules = {
  deptCode: [
    { required: true, message: '请输入部门编码', trigger: 'blur' }
  ],
  deptName: [
    { required: true, message: '请输入部门名称', trigger: 'blur' }
  ]
}

// 加载部门树
const loadDepartmentTree = async () => {
  try {
    const res = await departmentApi.getTree()
    if (res.code === 200) {
      departmentTree.value = res.data || []
    }
  } catch (error) {
    console.error('加载部门树失败:', error)
    ElMessage.error('加载部门树失败')
  }
}

// 添加部门
const handleAdd = (parent: any) => {
  resetForm()
  if (parent) {
    form.parentId = parent.id
    parentDeptName.value = parent.deptName
  } else {
    form.parentId = 0
    parentDeptName.value = '无'
  }
  dialogVisible.value = true
}

// 编辑部门
const handleEdit = async (dept: any) => {
  try {
    const res = await departmentApi.getById(dept.id)
    if (res.code === 200) {
      const data = res.data
      form.id = data.id
      form.deptCode = data.deptCode
      form.deptName = data.deptName
      form.parentId = data.parentId || 0
      form.sortOrder = data.sortOrder || 0
      form.description = data.description || ''
      form.status = data.status
      
      // 设置父部门名称
      if (data.parentId && data.parentId !== 0) {
        const parent = findDeptById(departmentTree.value, data.parentId)
        parentDeptName.value = parent ? parent.deptName : '无'
      } else {
        parentDeptName.value = '无'
      }
      
      dialogVisible.value = true
    }
  } catch (error) {
    console.error('加载部门详情失败:', error)
    ElMessage.error('加载部门详情失败')
  }
}

// 删除部门
const handleDelete = async (dept: any) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除部门"${dept.deptName}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const res = await departmentApi.delete(dept.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadDepartmentTree()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除部门失败:', error)
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
          res = await departmentApi.update(form.id, data)
        } else {
          res = await departmentApi.create(data)
        }
        
        if (res.code === 200) {
          ElMessage.success(form.id ? '更新成功' : '创建成功')
          dialogVisible.value = false
          loadDepartmentTree()
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
  form.deptCode = ''
  form.deptName = ''
  form.parentId = 0
  form.sortOrder = 0
  form.description = ''
  form.status = 1
  parentDeptName.value = '无'
  formRef.value?.clearValidate()
}

// 递归查找部门
const findDeptById = (tree: any[], id: number): any => {
  for (const node of tree) {
    if (node.id === id) return node
    if (node.children) {
      const found = findDeptById(node.children, id)
      if (found) return found
    }
  }
  return null
}

onMounted(() => {
  loadDepartmentTree()
})
</script>

<style scoped lang="scss">
.department-container {
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

