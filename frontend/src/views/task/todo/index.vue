<template>
  <div class="todo-task">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="任务名称">
          <el-input
            v-model="queryForm.taskName"
            placeholder="请输入任务名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="流程KEY">
          <el-input
            v-model="queryForm.processKey"
            placeholder="请输入流程KEY"
            clearable
            style="width: 200px"
          />
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
          <span>待办任务列表</span>
          <el-tag type="warning">{{ total }} 个待办</el-tag>
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
        <el-table-column prop="taskName" label="任务名称" width="150" />
        <el-table-column prop="processKey" label="流程KEY" width="150" />
        <el-table-column prop="businessKey" label="业务KEY" width="150" />
        <el-table-column prop="assignee" label="办理人" width="120" />
        <el-table-column prop="priority" label="优先级" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.priority >= 80" type="danger">高</el-tag>
            <el-tag v-else-if="row.priority >= 50" type="warning">中</el-tag>
            <el-tag v-else type="info">低</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" fixed="right">
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
              link
              type="success"
              size="small"
              :icon="Check"
              @click="handleComplete(row)"
            >
              办理
            </el-button>
            <el-button
              link
              type="warning"
              size="small"
              :icon="Share"
              @click="handleDelegate(row)"
            >
              委派
            </el-button>
            <el-button
              link
              type="info"
              size="small"
              :icon="Switch"
              @click="handleTransfer(row)"
            >
              转办
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

    <!-- 办理任务对话框 -->
    <el-dialog
      v-model="completeDialogVisible"
      title="办理任务"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="completeFormRef"
        :model="completeForm"
        :rules="completeRules"
        label-width="100px"
      >
        <el-form-item label="任务名称">
          <span>{{ currentTask?.taskName }}</span>
        </el-form-item>
        <el-form-item label="办理意见" prop="comment">
          <el-input
            v-model="completeForm.comment"
            type="textarea"
            :rows="4"
            placeholder="请输入办理意见"
          />
        </el-form-item>
        <el-form-item label="审批结果" prop="approved">
          <el-radio-group v-model="completeForm.approved">
            <el-radio :label="true">同意</el-radio>
            <el-radio :label="false">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="completeDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmitComplete">
          提交
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, FormInstance } from 'element-plus'
import { Search, Refresh, View, Check, Share, Switch } from '@element-plus/icons-vue'
import { taskApi, Task, TaskQuery } from '@/api/task'
import { formatDateTime } from '@/utils/date'
import { useUserStore } from '@/store/modules/user'

const userStore = useUserStore()

// 查询表单
const queryForm = reactive<TaskQuery>({
  assignee: userStore.username, // 查询当前用户的待办任务
  taskStatus: 'todo',
  taskName: '',
  processKey: '',
  pageNum: 1,
  pageSize: 10
})

// 表格数据
const tableData = ref<Task[]>([])
const total = ref(0)
const loading = ref(false)

// 办理任务对话框
const completeDialogVisible = ref(false)
const completeFormRef = ref<FormInstance>()
const submitting = ref(false)
const currentTask = ref<Task | null>(null)

const completeForm = reactive({
  comment: '',
  approved: true
})

const completeRules = {
  comment: [
    { required: true, message: '请输入办理意见', trigger: 'blur' }
  ]
}

// 查询任务列表
const handleQuery = async () => {
  loading.value = true
  try {
    const response = await taskApi.queryTasks(queryForm)
    tableData.value = response.data.list
    total.value = response.data.total
  } catch (error) {
    console.error('查询失败:', error)
    ElMessage.error('查询待办任务列表失败')
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
  queryForm.taskName = ''
  queryForm.processKey = ''
  queryForm.pageNum = 1
  handleQuery()
}

// 查看详情
const handleView = (row: Task) => {
  ElMessage.info('查看功能待实现')
  console.log('查看任务:', row)
}

// 办理任务
const handleComplete = (row: Task) => {
  currentTask.value = row
  completeForm.comment = ''
  completeForm.approved = true
  completeDialogVisible.value = true
}

// 提交办理
const handleSubmitComplete = async () => {
  if (!completeFormRef.value || !currentTask.value) return

  await completeFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        await taskApi.completeTask({
          taskId: currentTask.value!.taskId,
          assignee: userStore.username,
          comment: completeForm.comment,
          variables: {
            approved: completeForm.approved,
            approveComment: completeForm.comment
          }
        })
        
        ElMessage.success('任务办理成功')
        completeDialogVisible.value = false
        handleQuery()
      } catch (error) {
        console.error('办理失败:', error)
        ElMessage.error('任务办理失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 关闭对话框
const handleDialogClose = () => {
  completeFormRef.value?.resetFields()
  currentTask.value = null
}

// 委派任务
const handleDelegate = async (row: Task) => {
  try {
    const { value } = await ElMessageBox.prompt('请输入被委派人用户名', '委派任务', {
      inputPlaceholder: '请输入用户名',
      inputPattern: /^.+$/,
      inputErrorMessage: '用户名不能为空'
    })
    
    await taskApi.delegateTask(row.taskId, value)
    ElMessage.success('委派成功')
    handleQuery()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('委派失败:', error)
    }
  }
}

// 转办任务
const handleTransfer = async (row: Task) => {
  try {
    const { value } = await ElMessageBox.prompt('请输入目标用户名', '转办任务', {
      inputPlaceholder: '请输入用户名',
      inputPattern: /^.+$/,
      inputErrorMessage: '用户名不能为空'
    })
    
    await taskApi.transferTask(row.taskId, value)
    ElMessage.success('转办成功')
    handleQuery()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('转办失败:', error)
    }
  }
}

// 页面加载时查询数据
onMounted(() => {
  handleQuery()
})
</script>

<style scoped lang="scss">
.todo-task {
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
