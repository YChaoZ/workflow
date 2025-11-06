<template>
  <div class="process-instance">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="流程KEY">
          <el-input
            v-model="queryForm.processKey"
            placeholder="请输入流程KEY"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="业务KEY">
          <el-input
            v-model="queryForm.businessKey"
            placeholder="请输入业务KEY"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="发起人">
          <el-input
            v-model="queryForm.startUser"
            placeholder="请输入发起人"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="queryForm.state"
            placeholder="请选择状态"
            clearable
            style="width: 200px"
          >
            <el-option label="运行中" value="active" />
            <el-option label="已挂起" value="suspended" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">
            查询
          </el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>流程实例列表</span>
          <el-button type="primary" :icon="Plus" @click="handleStartProcess">启动流程</el-button>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="instanceId" label="实例ID" width="180" show-overflow-tooltip />
        <el-table-column prop="processKey" label="流程KEY" width="150" />
        <el-table-column prop="processName" label="流程名称" width="150" />
        <el-table-column prop="businessKey" label="业务KEY" width="150" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'RUNNING'" type="success">运行中</el-tag>
            <el-tag v-else-if="row.status === 'SUSPENDED'" type="warning">已挂起</el-tag>
            <el-tag v-else type="info">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startUser" label="发起人" width="120" />
        <el-table-column prop="startTime" label="发起时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              size="small"
              :icon="View"
              @click="handleView(row)"
            >
              查看
            </el-button>
            <el-button
              v-if="row.status === 'RUNNING'"
              link
              type="warning"
              size="small"
              :icon="VideoPause"
              @click="handleSuspend(row)"
            >
              挂起
            </el-button>
            <el-button
              v-if="row.status === 'SUSPENDED'"
              link
              type="success"
              size="small"
              :icon="VideoPlay"
              @click="handleActivate(row)"
            >
              激活
            </el-button>
            <el-button
              link
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

      <el-pagination
        v-model:current-page="queryForm.pageNum"
        v-model:page-size="queryForm.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; justify-content: flex-end"
        @size-change="handleQuery"
        @current-change="handleQuery"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, View, VideoPause, VideoPlay, Delete, Plus } from '@element-plus/icons-vue'
import { processApi, ProcessInstance, ProcessInstanceQuery } from '@/api/process'
import { formatDateTime } from '@/utils/date'

const router = useRouter()

// 查询表单
const queryForm = reactive<ProcessInstanceQuery>({
  processKey: '',
  businessKey: '',
  startUser: '',
  state: undefined,
  pageNum: 1,
  pageSize: 10
})

// 表格数据
const tableData = ref<ProcessInstance[]>([])
const total = ref(0)
const loading = ref(false)

// 查询流程实例列表
const handleQuery = async () => {
  loading.value = true
  try {
    const response = await processApi.queryProcessInstances(queryForm)
    tableData.value = response.data.list
    total.value = response.data.total
  } catch (error) {
    console.error('查询失败:', error)
    ElMessage.error('查询流程实例列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  queryForm.pageNum = 1
  handleQuery()
}

// 重置
const handleReset = () => {
  queryForm.processKey = ''
  queryForm.businessKey = ''
  queryForm.startUser = ''
  queryForm.state = undefined
  queryForm.pageNum = 1
  handleQuery()
}

// 查看详情
const handleView = (row: ProcessInstance) => {
  ElMessage.info('查看功能待实现')
  console.log('查看流程实例:', row)
}

// 挂起流程
const handleSuspend = async (row: ProcessInstance) => {
  try {
    await ElMessageBox.confirm('确定要挂起该流程实例吗？', '提示', {
      type: 'warning'
    })
    
    await processApi.suspendProcess(row.instanceId)
    ElMessage.success('挂起成功')
    handleQuery()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('挂起失败:', error)
    }
  }
}

// 激活流程
const handleActivate = async (row: ProcessInstance) => {
  try {
    await processApi.activateProcess(row.instanceId)
    ElMessage.success('激活成功')
    handleQuery()
  } catch (error) {
    console.error('激活失败:', error)
  }
}

// 删除流程
const handleDelete = async (row: ProcessInstance) => {
  try {
    await ElMessageBox.confirm('确定要删除该流程实例吗？此操作不可恢复！', '警告', {
      type: 'error'
    })
    
    await processApi.deleteProcess(row.instanceId, '用户删除')
    ElMessage.success('删除成功')
    handleQuery()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

// 启动流程
const handleStartProcess = async () => {
  // 跳转到流程定义列表让用户选择
  router.push('/process/definition')
}

// 页面加载时查询数据
onMounted(() => {
  handleQuery()
})
</script>

<style scoped lang="scss">
.process-instance {
  padding: 20px;

  .search-card {
    .search-form {
      margin-bottom: -18px;
    }
  }

  .table-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }
}
</style>
