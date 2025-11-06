<template>
  <div class="user-management">
    <el-card>
      <!-- 搜索表单 -->
      <el-form :model="queryForm" inline>
        <el-form-item label="用户名">
          <el-input v-model="queryForm.username" placeholder="请输入用户名" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="queryForm.realName" placeholder="请输入真实姓名" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="部门">
          <el-tree-select
            v-model="queryForm.departmentId"
            :data="departmentTree"
            placeholder="请选择部门"
            clearable
            check-strictly
            :render-after-expand="false"
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable style="width: 150px">
            <el-option label="启用" :value="true" />
            <el-option label="停用" :value="false" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery" :icon="Search">查询</el-button>
          <el-button @click="handleReset" :icon="Refresh">重置</el-button>
          <el-button type="success" @click="handleAdd" :icon="Plus">新增用户</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="userList" style="width: 100%; margin-top: 20px" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="realName" label="真实姓名" width="150" />
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column prop="phone" label="手机号" width="150" />
        <el-table-column prop="departmentId" label="部门" width="150">
          <template #default="{ row }">
            <span>{{ getDepartmentName(row.departmentId) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status ? 'success' : 'danger'">
              {{ row.status ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="角色" width="200">
          <template #default="{ row }">
            <el-tag v-for="roleId in row.roleIds" :key="roleId" style="margin-right: 5px">
              {{ getRoleName(roleId) }}
            </el-tag>
            <span v-if="!row.roleIds || row.roleIds.length === 0" style="color: #999">未分配</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="250">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)" :icon="Edit">编辑</el-button>
            <el-button type="warning" size="small" @click="handleAssignRoles(row)" :icon="UserFilled">分配角色</el-button>
            <el-popconfirm title="确定删除该用户吗？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button type="danger" size="small" :icon="Delete">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryForm.page"
        v-model:page-size="queryForm.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleQuery"
        @current-change="handleQuery"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="请输入用户名" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="formData.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="formData.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="部门" prop="departmentId">
          <el-tree-select
            v-model="formData.departmentId"
            :data="departmentTree"
            placeholder="请选择部门"
            check-strictly
            :render-after-expand="false"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="true">启用</el-radio>
            <el-radio :label="false">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="角色" prop="roleIds">
          <el-select v-model="formData.roleIds" multiple placeholder="请选择角色" style="width: 100%">
            <el-option
              v-for="role in roleList"
              :key="role.id"
              :label="role.roleName"
              :value="role.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配角色对话框 -->
    <el-dialog
      v-model="roleDialogVisible"
      title="分配角色"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form label-width="80px">
        <el-form-item label="用户">
          <el-input :value="currentUser?.realName || currentUser?.username" disabled />
        </el-form-item>
        <el-form-item label="角色">
          <el-checkbox-group v-model="selectedRoleIds">
            <el-checkbox v-for="role in roleList" :key="role.id" :label="role.id">
              {{ role.roleName }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitRoles" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, UserFilled } from '@element-plus/icons-vue'
import { organizationApi } from '@/api/organization'
import request from '@/api/request'

// 查询表单
const queryForm = reactive({
  username: '',
  realName: '',
  departmentId: null as number | null,
  status: null as boolean | null,
  page: 1,
  size: 10
})

// 数据列表
const userList = ref<any[]>([])
const total = ref(0)
const loading = ref(false)

// 部门和角色数据
const departmentTree = ref<any[]>([])
const departmentMap = ref<Map<number, string>>(new Map())
const roleList = ref<any[]>([])
const roleMap = ref<Map<number, string>>(new Map())

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref()
const submitLoading = ref(false)

// 表单数据
const formData = reactive({
  id: null as number | null,
  username: '',
  realName: '',
  password: '',
  email: '',
  phone: '',
  departmentId: null as number | null,
  status: true,
  roleIds: [] as number[]
})

// 表单验证规则
const formRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }]
}

// 分配角色对话框
const roleDialogVisible = ref(false)
const currentUser = ref<any>(null)
const selectedRoleIds = ref<number[]>([])

// 加载用户列表
const loadUserList = async () => {
  loading.value = true
  try {
    const params = {
      ...queryForm,
      departmentId: queryForm.departmentId || undefined,
      status: queryForm.status !== null ? queryForm.status : undefined
    }
    const response = await request.get('/users/page', { params })
    if (response.code === 200) {
      userList.value = response.data.list || []
      total.value = response.data.total || 0
    }
  } catch (error) {
    console.error('加载用户列表失败:', error)
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

// 加载部门树
const loadDepartments = async () => {
  try {
    const response = await organizationApi.getDepartmentTree()
    if (response.code === 200 && response.data) {
      departmentTree.value = response.data
      buildDepartmentMap(response.data)
    }
  } catch (error) {
    console.error('加载部门列表失败:', error)
  }
}

// 构建部门映射
const buildDepartmentMap = (departments: any[]) => {
  departments.forEach(dept => {
    departmentMap.value.set(dept.value, dept.label)
    if (dept.children && dept.children.length > 0) {
      buildDepartmentMap(dept.children)
    }
  })
}

// 加载角色列表
const loadRoles = async () => {
  try {
    const response = await organizationApi.getRoleList()
    if (response.code === 200 && response.data) {
      roleList.value = response.data
      roleList.value.forEach(role => {
        roleMap.value.set(role.id, role.roleName)
      })
    }
  } catch (error) {
    console.error('加载角色列表失败:', error)
  }
}

// 获取部门名称
const getDepartmentName = (deptId: number | null) => {
  if (!deptId) return '-'
  return departmentMap.value.get(deptId) || '-'
}

// 获取角色名称
const getRoleName = (roleId: number) => {
  return roleMap.value.get(roleId) || '-'
}

// 查询
const handleQuery = () => {
  queryForm.page = 1
  loadUserList()
}

// 重置
const handleReset = () => {
  queryForm.username = ''
  queryForm.realName = ''
  queryForm.departmentId = null
  queryForm.status = null
  queryForm.page = 1
  loadUserList()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增用户'
  Object.assign(formData, {
    id: null,
    username: '',
    realName: '',
    password: '',
    email: '',
    phone: '',
    departmentId: null,
    status: true,
    roleIds: []
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: any) => {
  isEdit.value = true
  dialogTitle.value = '编辑用户'
  Object.assign(formData, {
    id: row.id,
    username: row.username,
    realName: row.realName,
    email: row.email,
    phone: row.phone,
    departmentId: row.departmentId,
    status: row.status,
    roleIds: row.roleIds || []
  })
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await request.put(`/users/${formData.id}`, {
        realName: formData.realName,
        email: formData.email,
        phone: formData.phone,
        departmentId: formData.departmentId,
        status: formData.status,
        roleIds: formData.roleIds
      })
      ElMessage.success('更新成功')
    } else {
      await request.post('/users', formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadUserList()
  } catch (error: any) {
    console.error('提交失败:', error)
    ElMessage.error(error.response?.data?.message || '操作失败')
  } finally {
    submitLoading.value = false
  }
}

// 删除
const handleDelete = async (userId: number) => {
  try {
    await request.delete(`/users/${userId}`)
    ElMessage.success('删除成功')
    loadUserList()
  } catch (error: any) {
    console.error('删除失败:', error)
    ElMessage.error(error.response?.data?.message || '删除失败')
  }
}

// 分配角色
const handleAssignRoles = async (row: any) => {
  currentUser.value = row
  selectedRoleIds.value = [...(row.roleIds || [])]
  roleDialogVisible.value = true
}

// 提交角色分配
const handleSubmitRoles = async () => {
  submitLoading.value = true
  try {
    await request.post(`/users/${currentUser.value.id}/roles`, {
      roleIds: selectedRoleIds.value
    })
    ElMessage.success('角色分配成功')
    roleDialogVisible.value = false
    loadUserList()
  } catch (error: any) {
    console.error('分配角色失败:', error)
    ElMessage.error(error.response?.data?.message || '分配角色失败')
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  loadDepartments()
  loadRoles()
  loadUserList()
})
</script>

<style scoped lang="scss">
.user-management {
  padding: 20px;
}
</style>

