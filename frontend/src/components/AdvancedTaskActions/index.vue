<template>
  <div class="advanced-task-actions">
    <el-button-group>
      <el-button 
        type="primary" 
        :icon="UserFilled" 
        @click="showAddSignDialog"
      >
        加签
      </el-button>
      
      <el-button 
        type="warning" 
        :icon="Switch" 
        @click="showTransferDialog"
      >
        转办
      </el-button>
      
      <el-button 
        type="danger" 
        :icon="Back" 
        @click="showRejectDialog"
      >
        回退
      </el-button>
      
      <el-button 
        v-if="showWithdraw" 
        type="info" 
        :icon="RefreshLeft" 
        @click="showWithdrawDialog"
      >
        撤回
      </el-button>
    </el-button-group>

    <!-- 加签对话框 -->
    <el-dialog
      v-model="addSignDialogVisible"
      title="任务加签"
      width="500px"
    >
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
              :label="user.name"
              :value="user.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="addSignDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAddSign" :loading="loading">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 转办对话框 -->
    <el-dialog
      v-model="transferDialogVisible"
      title="任务转办"
      width="500px"
    >
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
              :label="user.name"
              :value="user.id"
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
        <el-button type="primary" @click="handleTransfer" :loading="loading">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 回退对话框 -->
    <el-dialog
      v-model="rejectDialogVisible"
      title="任务回退"
      width="600px"
    >
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
        <el-button type="primary" @click="handleReject" :loading="loading">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 撤回对话框 -->
    <el-dialog
      v-model="withdrawDialogVisible"
      title="撤回"
      width="500px"
    >
      <el-form :model="withdrawForm" label-width="100px">
        <el-form-item label="撤回类型">
          <el-radio-group v-model="withdrawForm.withdrawType">
            <el-radio label="process">撤回流程</el-radio>
            <el-radio label="task">撤回任务</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="撤回原因">
          <el-input
            v-model="withdrawForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入撤回原因（必填）"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="withdrawDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleWithdraw" :loading="loading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  UserFilled, 
  Switch, 
  Back, 
  RefreshLeft 
} from '@element-plus/icons-vue'
import { advancedTaskApi, type RejectableNode } from '@/api/advancedTask'
import type { User } from '@/api/user'

// Props
const props = defineProps<{
  taskId: string
  processInstanceId?: string
  showWithdraw?: boolean
}>()

// Emits
const emit = defineEmits<{
  success: []
}>()

// 状态
const loading = ref(false)
const loadingNodes = ref(false)

// 用户列表（模拟数据，实际应该从API获取）
const userList = ref<User[]>([
  { id: 'user1', name: '张三', username: 'zhangsan', email: 'zhangsan@bank.com' },
  { id: 'user2', name: '李四', username: 'lisi', email: 'lisi@bank.com' },
  { id: 'user3', name: '王五', username: 'wangwu', email: 'wangwu@bank.com' },
])

// 可回退节点列表
const rejectableNodes = ref<RejectableNode[]>([])

// ========== 加签 ==========
const addSignDialogVisible = ref(false)
const addSignForm = reactive({
  type: 'AND' as 'AND' | 'OR',
  assignees: [] as string[]
})

const showAddSignDialog = () => {
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
    loading.value = true
    await advancedTaskApi.addSign(props.taskId, {
      assignees: addSignForm.assignees,
      type: addSignForm.type
    })
    
    ElMessage.success('加签成功')
    addSignDialogVisible.value = false
    emit('success')
  } catch (error: any) {
    ElMessage.error(error.message || '加签失败')
  } finally {
    loading.value = false
  }
}

// ========== 转办 ==========
const transferDialogVisible = ref(false)
const transferForm = reactive({
  targetUser: '',
  comment: ''
})

const showTransferDialog = () => {
  transferForm.targetUser = ''
  transferForm.comment = ''
  transferDialogVisible.value = true
}

const handleTransfer = async () => {
  if (!transferForm.targetUser) {
    ElMessage.warning('请选择转办人')
    return
  }

  try {
    loading.value = true
    await advancedTaskApi.transfer(props.taskId, {
      targetUser: transferForm.targetUser,
      comment: transferForm.comment
    })
    
    ElMessage.success('转办成功')
    transferDialogVisible.value = false
    emit('success')
  } catch (error: any) {
    ElMessage.error(error.message || '转办失败')
  } finally {
    loading.value = false
  }
}

// ========== 回退 ==========
const rejectDialogVisible = ref(false)
const rejectForm = reactive({
  rejectType: 'previous' as 'previous' | 'node' | 'start',
  targetNodeId: '',
  comment: ''
})

const showRejectDialog = async () => {
  rejectForm.rejectType = 'previous'
  rejectForm.targetNodeId = ''
  rejectForm.comment = ''
  rejectDialogVisible.value = true
}

const handleRejectTypeChange = async (value: string) => {
  if (value === 'node') {
    // 加载可回退节点
    await loadRejectableNodes()
  }
}

const loadRejectableNodes = async () => {
  try {
    loadingNodes.value = true
    const res = await advancedTaskApi.getRejectableNodes(props.taskId)
    rejectableNodes.value = res.data || []
  } catch (error: any) {
    ElMessage.error(error.message || '加载可回退节点失败')
  } finally {
    loadingNodes.value = false
  }
}

const handleReject = async () => {
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

    loading.value = true
    
    if (rejectForm.rejectType === 'previous') {
      await advancedTaskApi.rejectToPrevious(props.taskId, {
        comment: rejectForm.comment
      })
    } else if (rejectForm.rejectType === 'node') {
      await advancedTaskApi.rejectToNode(props.taskId, {
        targetNodeId: rejectForm.targetNodeId,
        comment: rejectForm.comment
      })
    } else {
      await advancedTaskApi.rejectToStart(props.taskId, {
        comment: rejectForm.comment
      })
    }
    
    ElMessage.success('回退成功')
    rejectDialogVisible.value = false
    emit('success')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '回退失败')
    }
  } finally {
    loading.value = false
  }
}

// ========== 撤回 ==========
const withdrawDialogVisible = ref(false)
const withdrawForm = reactive({
  withdrawType: 'process' as 'process' | 'task',
  reason: ''
})

const showWithdrawDialog = () => {
  withdrawForm.withdrawType = 'process'
  withdrawForm.reason = ''
  withdrawDialogVisible.value = true
}

const handleWithdraw = async () => {
  if (!withdrawForm.reason) {
    ElMessage.warning('请输入撤回原因')
    return
  }

  try {
    await ElMessageBox.confirm('确定要撤回吗？此操作不可恢复！', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    loading.value = true
    
    if (withdrawForm.withdrawType === 'process' && props.processInstanceId) {
      await advancedTaskApi.withdrawProcess(props.processInstanceId, {
        reason: withdrawForm.reason
      })
    } else {
      await advancedTaskApi.withdrawTask(props.taskId, {
        reason: withdrawForm.reason
      })
    }
    
    ElMessage.success('撤回成功')
    withdrawDialogVisible.value = false
    emit('success')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '撤回失败')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.advanced-task-actions {
  margin: 10px 0;
}
</style>

