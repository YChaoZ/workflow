<template>
  <div class="params-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="参数名称">
          <el-input v-model="searchForm.paramName" placeholder="请输入参数名称" clearable />
        </el-form-item>
        <el-form-item label="参数分组">
          <el-select v-model="searchForm.paramGroup" placeholder="请选择参数分组" clearable>
            <el-option label="全部" value="" />
            <el-option label="工作流" value="workflow" />
            <el-option label="文件上传" value="upload" />
            <el-option label="系统" value="system" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
          <el-button type="success" :icon="Plus" @click="handleCreate">新增参数</el-button>
          <el-button type="warning" :icon="RefreshRight" @click="handleRefreshCache">刷新缓存</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 参数列表 -->
    <el-card class="table-card">
      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="paramKey" label="参数键" width="200" />
        <el-table-column prop="paramName" label="参数名称" width="150" />
        <el-table-column prop="paramValue" label="参数值" width="200" show-overflow-tooltip />
        <el-table-column prop="paramType" label="参数类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.paramType)">
              {{ row.paramType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="paramGroup" label="参数分组" width="120" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="isSystem" label="系统参数" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isSystem ? 'danger' : 'info'">
              {{ row.isSystem ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" :icon="View" @click="handleView(row)">查看</el-button>
            <el-button type="warning" size="small" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button
              v-if="!row.isSystem"
              type="danger"
              size="small"
              :icon="Delete"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="参数键" prop="paramKey">
          <el-input
            v-model="formData.paramKey"
            placeholder="请输入参数键（如: system.timeout）"
            :disabled="isEdit"
          />
        </el-form-item>
        <el-form-item label="参数名称" prop="paramName">
          <el-input v-model="formData.paramName" placeholder="请输入参数名称" />
        </el-form-item>
        <el-form-item label="参数值" prop="paramValue">
          <el-input
            v-model="formData.paramValue"
            :type="formData.paramType === 'JSON' ? 'textarea' : 'text'"
            :rows="4"
            placeholder="请输入参数值"
          />
        </el-form-item>
        <el-form-item label="参数类型" prop="paramType">
          <el-select v-model="formData.paramType" placeholder="请选择参数类型">
            <el-option label="字符串" value="STRING" />
            <el-option label="数字" value="NUMBER" />
            <el-option label="布尔" value="BOOLEAN" />
            <el-option label="JSON" value="JSON" />
          </el-select>
        </el-form-item>
        <el-form-item label="参数分组" prop="paramGroup">
          <el-select v-model="formData.paramGroup" placeholder="请选择参数分组">
            <el-option label="工作流" value="workflow" />
            <el-option label="文件上传" value="upload" />
            <el-option label="系统" value="system" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入参数描述"
          />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="formData.sortOrder" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import { Search, Refresh, Plus, RefreshRight, View, Edit, Delete } from '@element-plus/icons-vue'
import {
  listParams,
  createParam,
  updateParam,
  deleteParam,
  refreshParamCache,
  SystemParam
} from '@/api/config'

// 搜索表单
const searchForm = reactive({
  paramName: '',
  paramGroup: ''
})

// 表格数据
const tableData = ref<SystemParam[]>([])
const loading = ref(false)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新增参数')
const isEdit = ref(false)
const submitLoading = ref(false)

// 表单
const formRef = ref<FormInstance>()
const formData = reactive<SystemParam>({
  paramKey: '',
  paramValue: '',
  paramName: '',
  paramType: 'STRING',
  paramGroup: '',
  description: '',
  sortOrder: 0,
  remark: ''
})

// 表单校验规则
const formRules: FormRules = {
  paramKey: [
    { required: true, message: '请输入参数键', trigger: 'blur' }
  ],
  paramName: [
    { required: true, message: '请输入参数名称', trigger: 'blur' }
  ],
  paramValue: [
    { required: true, message: '请输入参数值', trigger: 'blur' }
  ],
  paramType: [
    { required: true, message: '请选择参数类型', trigger: 'change' }
  ]
}

// 查询参数列表
const fetchParams = async () => {
  loading.value = true
  try {
    const res = await listParams(searchForm.paramGroup || undefined)
    if (res.code === 200) {
      let data = res.data || []
      
      // 根据参数名称过滤
      if (searchForm.paramName) {
        data = data.filter((item: SystemParam) =>
          item.paramName.includes(searchForm.paramName) ||
          item.paramKey.includes(searchForm.paramName)
        )
      }
      
      tableData.value = data
    }
  } catch (error: any) {
    console.error('加载参数列表失败:', error)
    ElMessage.error(error.message || '加载失败')
  } finally {
    loading.value = false
  }
}

// 获取类型标签颜色
const getTypeTagType = (type: string) => {
  const typeMap: Record<string, any> = {
    STRING: 'primary',
    NUMBER: 'success',
    BOOLEAN: 'warning',
    JSON: 'info'
  }
  return typeMap[type] || 'info'
}

// 搜索
const handleSearch = () => {
  fetchParams()
}

// 重置
const handleReset = () => {
  searchForm.paramName = ''
  searchForm.paramGroup = ''
  fetchParams()
}

// 新增
const handleCreate = () => {
  dialogTitle.value = '新增参数'
  isEdit.value = false
  dialogVisible.value = true
  
  // 重置表单
  Object.assign(formData, {
    paramKey: '',
    paramValue: '',
    paramName: '',
    paramType: 'STRING',
    paramGroup: '',
    description: '',
    sortOrder: 0,
    remark: ''
  })
  
  formRef.value?.clearValidate()
}

// 查看
const handleView = (row: SystemParam) => {
  dialogTitle.value = '查看参数'
  isEdit.value = true
  dialogVisible.value = true
  
  Object.assign(formData, row)
}

// 编辑
const handleEdit = (row: SystemParam) => {
  dialogTitle.value = '编辑参数'
  isEdit.value = true
  dialogVisible.value = true
  
  Object.assign(formData, row)
}

// 删除
const handleDelete = async (row: SystemParam) => {
  try {
    await ElMessageBox.confirm('确定要删除该参数吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await deleteParam(row.id!)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchParams()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除参数失败:', error)
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const res = isEdit.value
          ? await updateParam(formData.id!, formData)
          : await createParam(formData)
        
        if (res.code === 200) {
          ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
          dialogVisible.value = false
          fetchParams()
        } else {
          ElMessage.error(res.message || '操作失败')
        }
      } catch (error: any) {
        console.error('提交失败:', error)
        ElMessage.error(error.message || '操作失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 刷新缓存
const handleRefreshCache = async () => {
  try {
    const res = await refreshParamCache()
    if (res.code === 200) {
      ElMessage.success('缓存刷新成功')
    } else {
      ElMessage.error(res.message || '刷新失败')
    }
  } catch (error: any) {
    console.error('刷新缓存失败:', error)
    ElMessage.error(error.message || '刷新失败')
  }
}

// 初始化
onMounted(() => {
  fetchParams()
})
</script>

<style scoped lang="scss">
.params-container {
  .search-card {
    margin-bottom: 20px;
  }
  
  .table-card {
    .el-table {
      margin-bottom: 20px;
    }
  }
}
</style>

