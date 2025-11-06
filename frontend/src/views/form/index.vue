<template>
  <div class="form-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>表单管理</span>
          <el-button type="primary" @click="handleCreate">
            <el-icon><Plus /></el-icon>
            新建表单
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" class="search-form">
        <el-form-item label="分类">
          <el-select v-model="searchForm.categoryId" placeholder="请选择分类" clearable style="width: 200px">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.categoryName"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 150px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
            <el-option label="草稿" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表单列表 -->
      <el-table :data="formList" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="formName" label="表单名称" min-width="150" />
        <el-table-column prop="formKey" label="表单Key" min-width="150" />
        <el-table-column prop="formType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.formType === 'CUSTOM' ? 'primary' : 'info'" size="small">
              {{ row.formType === 'CUSTOM' ? '自定义' : '内置' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="version" label="版本" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" size="small" @click="handleDesign(row)">设计</el-button>
            <el-button link type="warning" size="small" @click="handlePublish(row)" v-if="row.status === 2">
              发布
            </el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)" v-if="!row.builtIn">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新建/编辑表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="表单名称" prop="formName">
          <el-input v-model="form.formName" placeholder="请输入表单名称" />
        </el-form-item>
        <el-form-item label="表单Key" prop="formKey">
          <el-input v-model="form.formKey" placeholder="请输入表单Key（唯一标识）" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item label="表单类型" prop="formType">
          <el-select v-model="form.formType" placeholder="请选择类型" style="width: 100%">
            <el-option label="自定义" value="CUSTOM" />
            <el-option label="内置" value="BUILT_IN" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" clearable style="width: 100%">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.categoryName"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="2">草稿</el-radio>
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入表单描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, FormInstance } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { formApi, formCategoryApi } from '@/api/form'
import { useRouter } from 'vue-router'

const router = useRouter()

// 数据
const loading = ref(false)
const formList = ref<any[]>([])
const categories = ref<any[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('新建表单')
const submitting = ref(false)
const formRef = ref<FormInstance>()

// 搜索表单
const searchForm = reactive({
  categoryId: null,
  status: null
})

// 表单数据
const form = reactive({
  id: null,
  formName: '',
  formKey: '',
  formType: 'CUSTOM',
  categoryId: null,
  status: 2,
  description: '',
  formConfig: '{"fields":[]}'
})

// 表单验证规则
const rules = {
  formName: [{ required: true, message: '请输入表单名称', trigger: 'blur' }],
  formKey: [{ required: true, message: '请输入表单Key', trigger: 'blur' }],
  formType: [{ required: true, message: '请选择表单类型', trigger: 'change' }]
}

// 获取状态类型
const getStatusType = (status: number) => {
  const map: any = { 0: 'info', 1: 'success', 2: 'warning' }
  return map[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: number) => {
  const map: any = { 0: '禁用', 1: '启用', 2: '草稿' }
  return map[status] || '未知'
}

// 加载表单列表
const loadFormList = async () => {
  loading.value = true
  try {
    let res
    if (searchForm.categoryId) {
      res = await formApi.getByCategory(searchForm.categoryId)
    } else if (searchForm.status !== null) {
      res = await formApi.getByStatus(searchForm.status)
    } else {
      res = await formApi.getList()
    }
    
    if (res.code === 200) {
      formList.value = res.data || []
    }
  } catch (error) {
    console.error('加载表单列表失败:', error)
    ElMessage.error('加载表单列表失败')
  } finally {
    loading.value = false
  }
}

// 加载分类列表
const loadCategories = async () => {
  try {
    const res = await formCategoryApi.getList()
    if (res.code === 200) {
      categories.value = res.data || []
    }
  } catch (error) {
    console.error('加载分类列表失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  loadFormList()
}

// 重置
const handleReset = () => {
  searchForm.categoryId = null
  searchForm.status = null
  loadFormList()
}

// 新建表单
const handleCreate = () => {
  dialogTitle.value = '新建表单'
  dialogVisible.value = true
}

// 查看表单
const handleView = (row: any) => {
  ElMessageBox.alert(
    `<pre>${JSON.stringify(row, null, 2)}</pre>`,
    '表单详情',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '关闭'
    }
  )
}

// 编辑表单
const handleEdit = async (row: any) => {
  try {
    const res = await formApi.getById(row.id)
    if (res.code === 200) {
      const data = res.data
      form.id = data.id
      form.formName = data.formName
      form.formKey = data.formKey
      form.formType = data.formType
      form.categoryId = data.categoryId
      form.status = data.status
      form.description = data.description
      
      dialogTitle.value = '编辑表单'
      dialogVisible.value = true
    }
  } catch (error) {
    console.error('加载表单详情失败:', error)
    ElMessage.error('加载表单详情失败')
  }
}

// 设计表单
const handleDesign = (row: any) => {
  router.push({ path: '/form/designer', query: { id: row.id } })
}

// 发布表单
const handlePublish = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要发布该表单吗？发布后将创建新版本。', '提示', {
      type: 'warning'
    })
    
    const res = await formApi.publish(row.formKey)
    if (res.code === 200) {
      ElMessage.success('发布成功')
      loadFormList()
    } else {
      ElMessage.error(res.message || '发布失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('发布表单失败:', error)
      ElMessage.error(error.response?.data?.message || '发布失败')
    }
  }
}

// 删除表单
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该表单吗？此操作不可恢复。', '提示', {
      type: 'warning'
    })
    
    const res = await formApi.delete(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadFormList()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除表单失败:', error)
      ElMessage.error(error.response?.data?.message || '删除失败')
    }
  }
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitting.value = true
    try {
      const data = {
        formName: form.formName,
        formKey: form.formKey,
        formType: form.formType,
        categoryId: form.categoryId,
        status: form.status,
        description: form.description,
        formConfig: form.formConfig,
        createBy: 'admin',
        updateBy: 'admin'
      }
      
      let res
      if (form.id) {
        res = await formApi.update(form.id, data)
      } else {
        res = await formApi.create(data)
      }
      
      if (res.code === 200) {
        ElMessage.success(form.id ? '更新成功' : '创建成功')
        dialogVisible.value = false
        loadFormList()
      } else {
        ElMessage.error(res.message || '操作失败')
      }
    } catch (error: any) {
      console.error('提交表单失败:', error)
      ElMessage.error(error.response?.data?.message || '操作失败')
    } finally {
      submitting.value = false
    }
  })
}

// 重置表单
const resetForm = () => {
  form.id = null
  form.formName = ''
  form.formKey = ''
  form.formType = 'CUSTOM'
  form.categoryId = null
  form.status = 2
  form.description = ''
  form.formConfig = '{"fields":[]}'
  formRef.value?.resetFields()
}

// 初始化
onMounted(() => {
  loadCategories()
  loadFormList()
})
</script>

<style scoped lang="scss">
.form-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}
</style>

