<template>
  <div class="dict-container">
    <el-row :gutter="20">
      <!-- 左侧：字典类型 -->
      <el-col :span="8">
        <el-card class="type-card">
          <template #header>
            <div class="card-header">
              <span>字典类型</span>
              <el-button type="primary" size="small" :icon="Plus" @click="handleCreateType">
                新增
              </el-button>
            </div>
          </template>
          
          <el-table
            :data="dictTypes"
            v-loading="typeLoading"
            highlight-current-row
            @current-change="handleTypeChange"
            style="width: 100%"
          >
            <el-table-column prop="dictName" label="字典名称" />
            <el-table-column prop="dictCode" label="字典编码" width="120" />
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button type="primary" size="small" link @click="handleEditType(row)">
                  编辑
                </el-button>
                <el-button
                  v-if="!row.isSystem"
                  type="danger"
                  size="small"
                  link
                  @click="handleDeleteType(row)"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 右侧：字典数据 -->
      <el-col :span="16">
        <el-card class="data-card">
          <template #header>
            <div class="card-header">
              <span>字典数据 {{ selectedType ? `（${selectedType.dictName}）` : '' }}</span>
              <div>
                <el-button
                  type="primary"
                  size="small"
                  :icon="Plus"
                  :disabled="!selectedType"
                  @click="handleCreateData"
                >
                  新增
                </el-button>
                <el-button
                  type="warning"
                  size="small"
                  :icon="RefreshRight"
                  @click="handleRefreshCache"
                >
                  刷新缓存
                </el-button>
              </div>
            </div>
          </template>
          
          <el-table :data="dictDataList" v-loading="dataLoading" border>
            <el-table-column prop="dictLabel" label="字典标签" width="120" />
            <el-table-column prop="dictValue" label="字典值" width="120" />
            <el-table-column prop="tagType" label="标签类型" width="100">
              <template #default="{ row }">
                <el-tag :type="row.tagType">{{ row.tagType }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status ? 'success' : 'danger'">
                  {{ row.status ? '启用' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="sortOrder" label="排序" width="80" />
            <el-table-column prop="remark" label="备注" show-overflow-tooltip />
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button type="warning" size="small" :icon="Edit" @click="handleEditData(row)">
                  编辑
                </el-button>
                <el-button type="danger" size="small" :icon="Delete" @click="handleDeleteData(row)">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 字典类型对话框 -->
    <el-dialog
      v-model="typeDialogVisible"
      :title="typeDialogTitle"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="typeFormRef"
        :model="typeFormData"
        :rules="typeFormRules"
        label-width="100px"
      >
        <el-form-item label="字典编码" prop="dictCode">
          <el-input
            v-model="typeFormData.dictCode"
            placeholder="请输入字典编码"
            :disabled="isTypeEdit"
          />
        </el-form-item>
        <el-form-item label="字典名称" prop="dictName">
          <el-input v-model="typeFormData.dictName" placeholder="请输入字典名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="typeFormData.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="typeFormData.sortOrder" :min="0" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="typeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitType" :loading="typeSubmitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 字典数据对话框 -->
    <el-dialog
      v-model="dataDialogVisible"
      :title="dataDialogTitle"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="dataFormRef"
        :model="dataFormData"
        :rules="dataFormRules"
        label-width="100px"
      >
        <el-form-item label="字典标签" prop="dictLabel">
          <el-input v-model="dataFormData.dictLabel" placeholder="请输入字典标签" />
        </el-form-item>
        <el-form-item label="字典值" prop="dictValue">
          <el-input v-model="dataFormData.dictValue" placeholder="请输入字典值" />
        </el-form-item>
        <el-form-item label="标签类型" prop="tagType">
          <el-select v-model="dataFormData.tagType" placeholder="请选择标签类型">
            <el-option label="主要" value="primary" />
            <el-option label="成功" value="success" />
            <el-option label="信息" value="info" />
            <el-option label="警告" value="warning" />
            <el-option label="危险" value="danger" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="dataFormData.status" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="dataFormData.sortOrder" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="dataFormData.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dataDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitData" :loading="dataSubmitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import { Plus, RefreshRight, Edit, Delete } from '@element-plus/icons-vue'
import {
  listDictTypes,
  createDictType,
  updateDictType,
  deleteDictType,
  listDictDataByTypeCode,
  createDictData,
  updateDictData,
  deleteDictData,
  refreshDictCache,
  DictType,
  DictData
} from '@/api/config'

// 字典类型
const dictTypes = ref<DictType[]>([])
const typeLoading = ref(false)
const selectedType = ref<DictType | null>(null)

// 字典数据
const dictDataList = ref<DictData[]>([])
const dataLoading = ref(false)

// 字典类型对话框
const typeDialogVisible = ref(false)
const typeDialogTitle = ref('新增字典类型')
const isTypeEdit = ref(false)
const typeSubmitLoading = ref(false)
const typeFormRef = ref<FormInstance>()
const typeFormData = reactive<DictType>({
  dictCode: '',
  dictName: '',
  description: '',
  sortOrder: 0
})
const typeFormRules: FormRules = {
  dictCode: [{ required: true, message: '请输入字典编码', trigger: 'blur' }],
  dictName: [{ required: true, message: '请输入字典名称', trigger: 'blur' }]
}

// 字典数据对话框
const dataDialogVisible = ref(false)
const dataDialogTitle = ref('新增字典数据')
const isDataEdit = ref(false)
const dataSubmitLoading = ref(false)
const dataFormRef = ref<FormInstance>()
const dataFormData = reactive<DictData>({
  dictCode: '',
  dictLabel: '',
  dictValue: '',
  tagType: 'primary',
  status: true,
  sortOrder: 0,
  remark: ''
})
const dataFormRules: FormRules = {
  dictLabel: [{ required: true, message: '请输入字典标签', trigger: 'blur' }],
  dictValue: [{ required: true, message: '请输入字典值', trigger: 'blur' }]
}

// 查询字典类型列表
const fetchDictTypes = async () => {
  typeLoading.value = true
  try {
    const res = await listDictTypes()
    if (res.code === 200) {
      dictTypes.value = res.data || []
    }
  } catch (error: any) {
    console.error('加载字典类型失败:', error)
    ElMessage.error(error.message || '加载失败')
  } finally {
    typeLoading.value = false
  }
}

// 查询字典数据列表
const fetchDictData = async (typeCode: string) => {
  dataLoading.value = true
  try {
    const res = await listDictDataByTypeCode(typeCode)
    if (res.code === 200) {
      dictDataList.value = res.data || []
    }
  } catch (error: any) {
    console.error('加载字典数据失败:', error)
    ElMessage.error(error.message || '加载失败')
  } finally {
    dataLoading.value = false
  }
}

// 字典类型选择变化
const handleTypeChange = (row: DictType | null) => {
  if (!row) return
  selectedType.value = row
  fetchDictData(row.dictCode)
}

// 新增字典类型
const handleCreateType = () => {
  typeDialogTitle.value = '新增字典类型'
  isTypeEdit.value = false
  typeDialogVisible.value = true
  
  Object.assign(typeFormData, {
    dictCode: '',
    dictName: '',
    description: '',
    sortOrder: 0
  })
  
  typeFormRef.value?.clearValidate()
}

// 编辑字典类型
const handleEditType = (row: DictType) => {
  typeDialogTitle.value = '编辑字典类型'
  isTypeEdit.value = true
  typeDialogVisible.value = true
  
  Object.assign(typeFormData, row)
}

// 删除字典类型
const handleDeleteType = async (row: DictType) => {
  try {
    await ElMessageBox.confirm('确定要删除该字典类型吗？删除后相关字典数据也会被删除！', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await deleteDictType(row.id!)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchDictTypes()
      if (selectedType.value?.id === row.id) {
        selectedType.value = null
        dictDataList.value = []
      }
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除字典类型失败:', error)
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 提交字典类型
const handleSubmitType = async () => {
  if (!typeFormRef.value) return
  
  await typeFormRef.value.validate(async (valid) => {
    if (valid) {
      typeSubmitLoading.value = true
      try {
        const res = isTypeEdit.value
          ? await updateDictType(typeFormData.id!, typeFormData)
          : await createDictType(typeFormData)
        
        if (res.code === 200) {
          ElMessage.success(isTypeEdit.value ? '更新成功' : '创建成功')
          typeDialogVisible.value = false
          fetchDictTypes()
        } else {
          ElMessage.error(res.message || '操作失败')
        }
      } catch (error: any) {
        console.error('提交失败:', error)
        ElMessage.error(error.message || '操作失败')
      } finally {
        typeSubmitLoading.value = false
      }
    }
  })
}

// 新增字典数据
const handleCreateData = () => {
  if (!selectedType.value) return
  
  dataDialogTitle.value = '新增字典数据'
  isDataEdit.value = false
  dataDialogVisible.value = true
  
  Object.assign(dataFormData, {
    dictCode: selectedType.value.dictCode,
    dictTypeId: selectedType.value.id,
    dictLabel: '',
    dictValue: '',
    tagType: 'primary',
    status: true,
    sortOrder: 0,
    remark: ''
  })
  
  dataFormRef.value?.clearValidate()
}

// 编辑字典数据
const handleEditData = (row: DictData) => {
  dataDialogTitle.value = '编辑字典数据'
  isDataEdit.value = true
  dataDialogVisible.value = true
  
  Object.assign(dataFormData, row)
}

// 删除字典数据
const handleDeleteData = async (row: DictData) => {
  try {
    await ElMessageBox.confirm('确定要删除该字典数据吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await deleteDictData(row.id!)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      if (selectedType.value) {
        fetchDictData(selectedType.value.dictCode)
      }
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除字典数据失败:', error)
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 提交字典数据
const handleSubmitData = async () => {
  if (!dataFormRef.value) return
  
  await dataFormRef.value.validate(async (valid) => {
    if (valid) {
      dataSubmitLoading.value = true
      try {
        const res = isDataEdit.value
          ? await updateDictData(dataFormData.id!, dataFormData)
          : await createDictData(dataFormData)
        
        if (res.code === 200) {
          ElMessage.success(isDataEdit.value ? '更新成功' : '创建成功')
          dataDialogVisible.value = false
          if (selectedType.value) {
            fetchDictData(selectedType.value.dictCode)
          }
        } else {
          ElMessage.error(res.message || '操作失败')
        }
      } catch (error: any) {
        console.error('提交失败:', error)
        ElMessage.error(error.message || '操作失败')
      } finally {
        dataSubmitLoading.value = false
      }
    }
  })
}

// 刷新缓存
const handleRefreshCache = async () => {
  try {
    const res = await refreshDictCache()
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
  fetchDictTypes()
})
</script>

<style scoped lang="scss">
.dict-container {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .type-card {
    height: calc(100vh - 180px);
    
    :deep(.el-card__body) {
      height: calc(100% - 60px);
      overflow-y: auto;
    }
  }
  
  .data-card {
    height: calc(100vh - 180px);
    
    :deep(.el-card__body) {
      height: calc(100% - 60px);
      overflow-y: auto;
    }
  }
}
</style>

