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
        <el-table-column label="操作" width="400" fixed="right">
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
              @click="handleHandle(row)"
            >
              办理
            </el-button>
            
            <el-dropdown @command="(cmd: string) => handleAdvancedAction(cmd, row)">
              <el-button link type="warning" size="small">
                更多操作
                <el-icon class="el-icon--right"><arrow-down /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="addSign" :icon="UserFilled">
                    加签
                  </el-dropdown-item>
                  <el-dropdown-item command="transfer" :icon="Switch">
                    转办
                  </el-dropdown-item>
                  <el-dropdown-item command="delegate" :icon="Share">
                    委派
                  </el-dropdown-item>
                  <el-dropdown-item command="reject" :icon="Back" divided>
                    回退
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
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

    <!-- 加签对话框 -->
    <el-dialog v-model="addSignDialogVisible" title="任务加签" width="500px">
      <el-form :model="addSignForm" label-width="100px">
        <el-form-item label="加签类型">
          <el-radio-group v-model="addSignForm.type">
            <el-radio label="AND">会签（所有人必须审批）</el-radio>
            <el-radio label="OR">或签（任一人审批即可）</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="加签人">
                 <el-select
                   v-model="addSignForm.assignees"
                   multiple
                   filterable
                   placeholder="请选择加签人"
                   style="width: 100%"
                 >
                   <el-option
                     v-for="user in userList"
                     :key="user.id"
                     :label="user.realName || user.username"
                     :value="user.username"
                   />
                 </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="addSignDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAddSign" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 转办对话框 -->
    <el-dialog v-model="transferDialogVisible" title="任务转办" width="500px">
      <el-form :model="transferForm" label-width="100px">
        <el-form-item label="转办给">
                 <el-select
                   v-model="transferForm.targetUser"
                   filterable
                   placeholder="请选择转办人"
                   style="width: 100%"
                 >
                   <el-option
                     v-for="user in userList"
                     :key="user.id"
                     :label="user.realName || user.username"
                     :value="user.username"
                   />
                 </el-select>
        </el-form-item>
        
        <el-form-item label="转办说明">
          <el-input
            v-model="transferForm.comment"
            type="textarea"
            :rows="3"
            placeholder="请输入转办说明"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="transferDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitTransfer" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 回退对话框 -->
    <el-dialog v-model="rejectDialogVisible" title="任务回退" width="600px">
      <el-form :model="rejectForm" label-width="100px">
        <el-form-item label="回退方式">
          <el-radio-group v-model="rejectForm.rejectType" @change="handleRejectTypeChange">
            <el-radio label="previous">回退到上一节点</el-radio>
            <el-radio label="node">回退到指定节点</el-radio>
            <el-radio label="start">回退到流程发起人</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item 
          v-if="rejectForm.rejectType === 'node'" 
          label="目标节点"
        >
          <el-select 
            v-model="rejectForm.targetNodeId" 
            placeholder="请选择目标节点"
            style="width: 100%"
            :loading="loadingNodes"
          >
            <el-option
              v-for="node in rejectableNodes"
              :key="node.activityId"
              :label="`${node.activityName} (${node.assignee})`"
              :value="node.activityId"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="回退原因">
          <el-input
            v-model="rejectForm.comment"
            type="textarea"
            :rows="3"
            placeholder="请输入回退原因（必填）"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitReject" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, FormInstance } from 'element-plus'
import { 
  Search, 
  Refresh, 
  View, 
  Check, 
  Share, 
  Switch, 
  ArrowDown, 
  UserFilled, 
  Back 
} from '@element-plus/icons-vue'
import { taskApi, Task, TaskQuery } from '@/api/task'
import { formatDateTime } from '@/utils/date'
import { useUserStore } from '@/store/modules/user'
import { advancedTaskApi, type RejectableNode } from '@/api/advancedTask'
import request from '@/api/request'

const router = useRouter()
const userStore = useUserStore()

// 查询表单
const queryForm = reactive<TaskQuery>({
  assignee: userStore.username || 'admin', // 查询当前用户的待办任务，默认admin
  taskStatus: 'todo',
  taskName: '',
  processKey: '',
  pageNum: 1,
  pageSize: 10
})

// 调试日志
console.log('初始化queryForm:', {
  assignee: queryForm.assignee,
  userStoreUsername: userStore.username,
  taskStatus: queryForm.taskStatus
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

// 跳转到任务办理页面
const handleHandle = (row: Task) => {
  router.push({ path: '/task/handle', query: { taskId: row.taskId } })
}

// 办理任务（原有方法保留）
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

// ========== 高级操作 ==========
// 用户列表
const userList = ref<any[]>([])

// 加载用户列表
const loadUsers = async () => {
  try {
    const res = await request.get('/users/list')
    if (res.code === 200) {
      userList.value = res.data || []
      console.log('用户列表加载成功:', userList.value.length)
    }
  } catch (error) {
    console.error('加载用户列表失败:', error)
    // 使用模拟数据作为备用
    userList.value = [
      { id: '1', name: '张三', username: 'zhangsan', realName: '张三' },
      { id: '2', name: '李四', username: 'lisi', realName: '李四' },
      { id: '3', name: '王五', username: 'wangwu', realName: '王五' },
    ]
  }
}

// 可回退节点列表
const rejectableNodes = ref<RejectableNode[]>([])
const loadingNodes = ref(false)

// 加签对话框
const addSignDialogVisible = ref(false)
const addSignForm = reactive({
  taskId: '',
  type: 'AND' as 'AND' | 'OR',
  assignees: [] as string[]
})

// 转办对话框
const transferDialogVisible = ref(false)
const transferForm = reactive({
  taskId: '',
  targetUser: '',
  comment: ''
})

// 回退对话框
const rejectDialogVisible = ref(false)
const rejectForm = reactive({
  taskId: '',
  rejectType: 'previous' as 'previous' | 'node' | 'start',
  targetNodeId: '',
  comment: ''
})

const handleAdvancedAction = (command: string, row: Task) => {
  switch (command) {
    case 'addSign':
      showAddSignDialog(row)
      break
    case 'transfer':
      showTransferDialog(row)
      break
    case 'delegate':
      handleDelegate(row)
      break
    case 'reject':
      showRejectDialog(row)
      break
  }
}

// 加签
const showAddSignDialog = (row: Task) => {
  addSignForm.taskId = row.taskId
  addSignForm.type = 'AND'
  addSignForm.assignees = []
  addSignDialogVisible.value = true
}

const handleAddSign = async () => {
  if (addSignForm.assignees.length === 0) {
    ElMessage.warning('请选择加签人')
    return
  }

  try {
    submitting.value = true
    await advancedTaskApi.addSign(addSignForm.taskId, {
      assignees: addSignForm.assignees,
      type: addSignForm.type
    })
    
    ElMessage.success('加签成功')
    addSignDialogVisible.value = false
    handleQuery()
  } catch (error: any) {
    ElMessage.error(error.message || '加签失败')
  } finally {
    submitting.value = false
  }
}

// 转办（使用新的对话框）
const showTransferDialog = (row: Task) => {
  transferForm.taskId = row.taskId
  transferForm.targetUser = ''
  transferForm.comment = ''
  transferDialogVisible.value = true
}

const handleSubmitTransfer = async () => {
  if (!transferForm.targetUser) {
    ElMessage.warning('请选择转办人')
    return
  }

  try {
    submitting.value = true
    await advancedTaskApi.transfer(transferForm.taskId, {
      targetUser: transferForm.targetUser,
      comment: transferForm.comment
    })
    
    ElMessage.success('转办成功')
    transferDialogVisible.value = false
    handleQuery()
  } catch (error: any) {
    ElMessage.error(error.message || '转办失败')
  } finally {
    submitting.value = false
  }
}

// 回退
const showRejectDialog = async (row: Task) => {
  rejectForm.taskId = row.taskId
  rejectForm.rejectType = 'previous'
  rejectForm.targetNodeId = ''
  rejectForm.comment = ''
  rejectDialogVisible.value = true
}

const handleRejectTypeChange = async (value: string) => {
  if (value === 'node') {
    // 加载可回退节点
    await loadRejectableNodes(rejectForm.taskId)
  }
}

const loadRejectableNodes = async (taskId: string) => {
  try {
    loadingNodes.value = true
    const res = await advancedTaskApi.getRejectableNodes(taskId)
    rejectableNodes.value = res.data || []
  } catch (error: any) {
    ElMessage.error(error.message || '加载可回退节点失败')
  } finally {
    loadingNodes.value = false
  }
}

const handleSubmitReject = async () => {
  if (!rejectForm.comment) {
    ElMessage.warning('请输入回退原因')
    return
  }

  if (rejectForm.rejectType === 'node' && !rejectForm.targetNodeId) {
    ElMessage.warning('请选择目标节点')
    return
  }

  try {
    await ElMessageBox.confirm('确定要回退此任务吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    submitting.value = true
    
    if (rejectForm.rejectType === 'previous') {
      await advancedTaskApi.rejectToPrevious(rejectForm.taskId, {
        comment: rejectForm.comment
      })
    } else if (rejectForm.rejectType === 'node') {
      await advancedTaskApi.rejectToNode(rejectForm.taskId, {
        targetNodeId: rejectForm.targetNodeId,
        comment: rejectForm.comment
      })
    } else {
      await advancedTaskApi.rejectToStart(rejectForm.taskId, {
        comment: rejectForm.comment
      })
    }
    
    ElMessage.success('回退成功')
    rejectDialogVisible.value = false
    handleQuery()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '回退失败')
    }
  } finally {
    submitting.value = false
  }
}

// 页面加载时查询数据
onMounted(() => {
  handleQuery()
  loadUsers()  // 加载用户列表
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
