<template>
  <div class="process-definition-page">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="流程标识">
          <el-input v-model="searchForm.key" placeholder="请输入流程标识" clearable />
        </el-form-item>
        <el-form-item label="流程名称">
          <el-input v-model="searchForm.name" placeholder="请输入流程名称" clearable />
        </el-form-item>
        <el-form-item label="流程分类">
          <el-input v-model="searchForm.category" placeholder="请输入流程分类" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
          <el-button type="success" :icon="Plus" @click="handleDeploy">部署流程</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 流程定义列表 -->
    <el-card class="table-card">
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="key" label="流程标识" min-width="150" />
        <el-table-column prop="name" label="流程名称" min-width="150" />
        <el-table-column prop="version" label="版本" width="80" align="center">
          <template #default="{ row }">
            <el-tag>V{{ row.version }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="deploymentTime" label="部署时间" width="170" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.suspended ? 'danger' : 'success'">
              {{ row.suspended ? '已挂起' : '已激活' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="450" fixed="right" align="center">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              :icon="View"
              @click="handleViewDiagram(row)"
            >
              流程图
            </el-button>
            <el-button
              link
              type="primary"
              :icon="Edit"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              link
              type="primary"
              @click="handleBindForm(row)"
            >
              绑定表单
            </el-button>
            <el-button
              link
              type="success"
              @click="handleStartFromDefinition(row)"
            >
              启动
            </el-button>
            <el-button
              v-if="row.suspended"
              link
              type="success"
              :icon="VideoPlay"
              @click="handleActivate(row)"
            >
              激活
            </el-button>
            <el-button
              v-else
              link
              type="warning"
              :icon="VideoPause"
              @click="handleSuspend(row)"
            >
              挂起
            </el-button>
            <el-button
              link
              type="danger"
              :icon="Delete"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
        class="pagination"
      />
    </el-card>

    <!-- 部署流程对话框 -->
    <el-dialog
      v-model="deployDialogVisible"
      title="部署流程"
      width="600px"
      @close="handleDeployDialogClose"
    >
      <el-form
        ref="deployFormRef"
        :model="deployForm"
        :rules="deployRules"
        label-width="100px"
      >
        <el-form-item label="流程名称" prop="name">
          <el-input v-model="deployForm.name" placeholder="请输入流程名称" />
        </el-form-item>
        <el-form-item label="流程分类" prop="category">
          <el-input v-model="deployForm.category" placeholder="请输入流程分类（可选）" />
        </el-form-item>
        <el-form-item label="选择文件" prop="file">
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :limit="1"
            :on-change="handleFileSelect"
            :on-remove="handleFileRemove"
            accept=".bpmn,.xml"
            drag
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              拖拽文件到这里或 <em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                仅支持 .bpmn 或 .xml 格式的文件
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="deployDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleDeployConfirm" :loading="deploying">
          确认部署
        </el-button>
      </template>
    </el-dialog>

    <!-- 流程图预览对话框 -->
    <el-dialog
      v-model="diagramDialogVisible"
      title="流程图预览"
      width="80%"
      top="5vh"
    >
      <div class="diagram-container" v-loading="diagramLoading">
        <img v-if="diagramUrl" :src="diagramUrl" alt="流程图" />
      </div>
    </el-dialog>

    <!-- 绑定表单对话框 -->
    <el-dialog
      v-model="bindFormDialogVisible"
      title="绑定表单"
      width="600px"
      @close="handleBindFormDialogClose"
    >
      <el-form :model="bindFormData" label-width="120px">
        <el-form-item label="流程定义">
          <el-input v-model="currentProcessKey" disabled />
        </el-form-item>
        <el-form-item label="绑定类型">
          <el-radio-group v-model="bindFormData.bindType">
            <el-radio value="global">全局表单（流程启动时）</el-radio>
            <el-radio value="node">节点表单（任务办理时）</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="节点ID" v-if="bindFormData.bindType === 'node'">
          <el-input v-model="bindFormData.nodeId" placeholder="请输入节点ID（如：UserTask_1）" />
        </el-form-item>
        <el-form-item label="选择表单">
          <el-select v-model="bindFormData.formKey" placeholder="请选择表单" style="width: 100%">
            <el-option
              v-for="form in availableForms"
              :key="form.formKey"
              :label="`${form.formName} (${form.formKey})`"
              :value="form.formKey"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="表单版本">
          <el-input-number v-model="bindFormData.formVersion" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="是否必填">
          <el-switch v-model="bindFormData.isRequired" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="bindFormDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBindFormConfirm" :loading="bindingForm">
          确认绑定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, FormInstance } from 'element-plus'
import type { UploadFile } from 'element-plus'
import {
  Search,
  Refresh,
  Plus,
  View,
  Edit,
  Delete,
  VideoPlay,
  VideoPause,
  UploadFilled
} from '@element-plus/icons-vue'
import {
  getProcessDefinitionList,
  deployProcess,
  deleteProcessDefinition,
  suspendProcessDefinition,
  activateProcessDefinition,
  getProcessDefinitionXml,
  getProcessDefinitionImage,
  type ProcessDefinition,
  type ProcessDefinitionQuery,
  type DeployProcessRequest
} from '@/api/definition'
import { formApi, processFormApi } from '@/api/form'

const router = useRouter()

// 搜索表单
const searchForm = reactive<ProcessDefinitionQuery>({
  key: '',
  name: '',
  category: '',
  page: 1,
  size: 10
})

// 表格数据
const tableData = ref<ProcessDefinition[]>([])
const loading = ref(false)

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 部署对话框
const deployDialogVisible = ref(false)
const deploying = ref(false)
const deployFormRef = ref<FormInstance>()
const uploadRef = ref()
const deployForm = reactive<DeployProcessRequest>({
  name: '',
  category: '',
  tenantId: '',
  xml: ''
})

const deployRules = {
  name: [{ required: true, message: '请输入流程名称', trigger: 'blur' }],
  file: [{ required: true, message: '请选择流程文件', trigger: 'change' }]
}

// 流程图预览
const diagramDialogVisible = ref(false)
const diagramLoading = ref(false)
const diagramUrl = ref('')

// 绑定表单对话框
const bindFormDialogVisible = ref(false)
const bindingForm = ref(false)
const currentProcessKey = ref('')
const availableForms = ref<any[]>([])
const bindFormData = reactive({
  bindType: 'global',
  nodeId: '',
  formKey: '',
  formVersion: 1,
  isRequired: 1
})

// 加载流程定义列表
const loadProcessDefinitions = async () => {
  loading.value = true
  try {
    const response = await getProcessDefinitionList({
      ...searchForm,
      page: pagination.page,
      size: pagination.size
    })
    // 修复：后端返回的数据在response.data中
    const data = response.data || response
    tableData.value = data.list || []
    pagination.total = data.total || 0
  } catch (error: any) {
    console.error('加载流程定义列表失败:', error)
    ElMessage.error(error.message || '加载失败')
  } finally {
    loading.value = false
  }
}

// 查询
const handleSearch = () => {
  pagination.page = 1
  loadProcessDefinitions()
}

// 重置
const handleReset = () => {
  searchForm.key = ''
  searchForm.name = ''
  searchForm.category = ''
  pagination.page = 1
  loadProcessDefinitions()
}

// 部署流程
const handleDeploy = () => {
  deployDialogVisible.value = true
}

// 文件选择
const handleFileSelect = (file: UploadFile) => {
  const reader = new FileReader()
  reader.onload = (e: any) => {
    deployForm.xml = e.target.result
  }
  reader.readAsText(file.raw!)
}

// 文件移除
const handleFileRemove = () => {
  deployForm.xml = ''
}

// 确认部署
const handleDeployConfirm = async () => {
  if (!deployFormRef.value) return

  await deployFormRef.value.validate(async (valid) => {
    if (!valid) return

    if (!deployForm.xml) {
      ElMessage.error('请选择流程文件')
      return
    }

    deploying.value = true
    try {
      await deployProcess(deployForm)
      ElMessage.success('部署成功')
      deployDialogVisible.value = false
      loadProcessDefinitions()
    } catch (error: any) {
      console.error('部署失败:', error)
      ElMessage.error(error.message || '部署失败')
    } finally {
      deploying.value = false
    }
  })
}

// 关闭部署对话框
const handleDeployDialogClose = () => {
  deployFormRef.value?.resetFields()
  deployForm.xml = ''
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
}

// 查看流程图
const handleViewDiagram = async (row: ProcessDefinition) => {
  diagramDialogVisible.value = true
  diagramLoading.value = true
  try {
    const blob = await getProcessDefinitionImage(row.id)
    diagramUrl.value = URL.createObjectURL(blob)
  } catch (error: any) {
    console.error('加载流程图失败:', error)
    ElMessage.error(error.message || '加载流程图失败')
    diagramDialogVisible.value = false
  } finally {
    diagramLoading.value = false
  }
}

// 编辑流程
const handleEdit = async (row: ProcessDefinition) => {
  try {
    const response = await getProcessDefinitionXml(row.id)
    router.push({
      path: '/process/design',
      query: {
        id: row.id,
        name: row.name
      }
    })
  } catch (error: any) {
    console.error('加载流程XML失败:', error)
    ElMessage.error(error.message || '加载失败')
  }
}

// 激活流程
const handleActivate = async (row: ProcessDefinition) => {
  try {
    await activateProcessDefinition(row.id)
    ElMessage.success('激活成功')
    loadProcessDefinitions()
  } catch (error: any) {
    console.error('激活失败:', error)
    ElMessage.error(error.message || '激活失败')
  }
}

// 挂起流程
const handleSuspend = async (row: ProcessDefinition) => {
  try {
    await ElMessageBox.confirm(
      '挂起后该流程将无法启动新的实例，是否继续？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await suspendProcessDefinition(row.id)
    ElMessage.success('挂起成功')
    loadProcessDefinitions()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('挂起失败:', error)
      ElMessage.error(error.message || '挂起失败')
    }
  }
}

// 删除流程
const handleDelete = async (row: ProcessDefinition) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除流程 "${row.name}" 吗？此操作不可恢复！`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await deleteProcessDefinition(row.id, true)
    ElMessage.success('删除成功')
    loadProcessDefinitions()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 绑定表单
const handleBindForm = async (row: ProcessDefinition) => {
  currentProcessKey.value = row.key
  bindFormData.bindType = 'global'
  bindFormData.nodeId = ''
  bindFormData.formKey = ''
  bindFormData.formVersion = 1
  bindFormData.isRequired = 1
  
  // 加载可用表单列表
  try {
    const res = await formApi.getList()
    if (res.code === 200) {
      availableForms.value = res.data || []
    }
  } catch (error) {
    console.error('加载表单列表失败:', error)
  }
  
  bindFormDialogVisible.value = true
}

// 确认绑定表单
const handleBindFormConfirm = async () => {
  if (!bindFormData.formKey) {
    ElMessage.warning('请选择表单')
    return
  }
  
  if (bindFormData.bindType === 'node' && !bindFormData.nodeId) {
    ElMessage.warning('请输入节点ID')
    return
  }
  
  bindingForm.value = true
  try {
    const data = {
      processDefinitionKey: currentProcessKey.value,
      nodeId: bindFormData.bindType === 'global' ? null : bindFormData.nodeId,
      formKey: bindFormData.formKey,
      formVersion: bindFormData.formVersion,
      isRequired: bindFormData.isRequired
    }
    
    const res = await processFormApi.bind(data)
    if (res.code === 200) {
      ElMessage.success('绑定成功')
      bindFormDialogVisible.value = false
    } else {
      ElMessage.error(res.message || '绑定失败')
    }
  } catch (error: any) {
    console.error('绑定表单失败:', error)
    ElMessage.error(error.response?.data?.message || '绑定失败')
  } finally {
    bindingForm.value = false
  }
}

// 关闭绑定表单对话框
const handleBindFormDialogClose = () => {
  bindFormData.bindType = 'global'
  bindFormData.nodeId = ''
  bindFormData.formKey = ''
  bindFormData.formVersion = 1
  bindFormData.isRequired = 1
}

// 从流程定义启动流程
const handleStartFromDefinition = (row: ProcessDefinition) => {
  router.push({ path: '/process/start', query: { id: row.id } })
}

// 分页变化
const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.page = 1
  loadProcessDefinitions()
}

const handlePageChange = (page: number) => {
  pagination.page = page
  loadProcessDefinitions()
}

onMounted(() => {
  loadProcessDefinitions()
})
</script>

<style scoped lang="scss">
.process-definition-page {
  padding: 20px;

  .search-card {
    margin-bottom: 20px;
  }

  .table-card {
    .pagination {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }

  .diagram-container {
    width: 100%;
    min-height: 500px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f5f5f5;
    border-radius: 4px;
    padding: 20px;

    img {
      max-width: 100%;
      max-height: 70vh;
      object-fit: contain;
    }
  }
}
</style>
