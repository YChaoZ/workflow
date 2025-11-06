<template>
  <div class="monitor-dashboard">
    <el-page-header @back="goBack" content="流程监控大屏" />

    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card running">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="40"><Operation /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.runningProcessCount || 0 }}</div>
              <div class="stat-label">运行中流程</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" class="stat-card pending">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="40"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.pendingTaskCount || 0 }}</div>
              <div class="stat-label">待办任务</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" class="stat-card timeout">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="40"><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.timeoutTaskCount || 0 }}</div>
              <div class="stat-label">超时任务</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" class="stat-card exception">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="40"><CircleClose /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.exceptionProcessCount || 0 }}</div>
              <div class="stat-label">异常流程</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 今日统计 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>今日流程统计</span>
            </div>
          </template>
          <div class="today-stats">
            <div class="today-item">
              <div class="today-label">今日启动</div>
              <div class="today-value started">{{ statistics.todayStartedCount || 0 }}</div>
            </div>
            <div class="today-item">
              <div class="today-label">今日完成</div>
              <div class="today-value completed">{{ statistics.todayCompletedCount || 0 }}</div>
            </div>
            <div class="today-item">
              <div class="today-label">平均耗时</div>
              <div class="today-value duration">{{ formatDuration(statistics.avgProcessDuration) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>任务处理效率</span>
            </div>
          </template>
          <div class="today-stats">
            <div class="today-item">
              <div class="today-label">平均任务耗时</div>
              <div class="today-value duration">{{ formatDuration(statistics.avgTaskDuration) }}</div>
            </div>
            <div class="today-item">
              <div class="today-label">待办任务</div>
              <div class="today-value pending">{{ statistics.pendingTaskCount || 0 }}</div>
            </div>
            <div class="today-item">
              <div class="today-label">超时任务</div>
              <div class="today-value timeout">{{ statistics.timeoutTaskCount || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 运行中流程列表 -->
    <el-card shadow="hover" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>运行中流程</span>
          <el-button type="primary" size="small" @click="refreshRunningProcesses">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>

      <el-table :data="runningProcesses" stripe style="width: 100%">
        <el-table-column prop="processDefinitionName" label="流程名称" width="200" />
        <el-table-column prop="processName" label="流程实例名称" width="200" />
        <el-table-column prop="startUser" label="发起人" width="120" />
        <el-table-column prop="startTime" label="开始时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="currentNodeName" label="当前节点" width="150" />
        <el-table-column prop="currentAssignee" label="当前处理人" width="120" />
        <el-table-column prop="duration" label="运行时长" width="120">
          <template #default="scope">
            {{ formatDuration(scope.row.duration) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 'running'" type="success">运行中</el-tag>
            <el-tag v-else-if="scope.row.status === 'suspended'" type="warning">挂起</el-tag>
            <el-tag v-else type="info">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="超时" width="80">
          <template #default="scope">
            <el-tag v-if="scope.row.timeout" type="danger">是</el-tag>
            <el-tag v-else type="success">否</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="runningProcesses.length === 0" class="empty-data">
        <el-empty description="暂无运行中的流程" />
      </div>
    </el-card>

    <!-- 超时任务列表 -->
    <el-card shadow="hover" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>超时任务预警</span>
          <el-button type="danger" size="small" @click="refreshTimeoutTasks">
            <el-icon><Warning /></el-icon>
            刷新
          </el-button>
        </div>
      </template>

      <el-table :data="timeoutTasks" stripe style="width: 100%">
        <el-table-column prop="taskName" label="任务名称" width="180" />
        <el-table-column prop="processDefinitionName" label="流程名称" width="180" />
        <el-table-column prop="assignee" label="处理人" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="waitingDuration" label="等待时长" width="120">
          <template #default="scope">
            {{ formatDuration(scope.row.waitingDuration) }}
          </template>
        </el-table-column>
        <el-table-column prop="dueDate" label="到期时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.dueDate) || '未设置' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag type="danger">超时</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="timeoutTasks.length === 0" class="empty-data">
        <el-empty description="暂无超时任务" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Operation,
  Document,
  Warning,
  CircleClose,
  Refresh
} from '@element-plus/icons-vue'
import request from '@/api/request'

const router = useRouter()

// 数据
const statistics = ref({
  runningProcessCount: 0,
  suspendedProcessCount: 0,
  exceptionProcessCount: 0,
  pendingTaskCount: 0,
  timeoutTaskCount: 0,
  todayStartedCount: 0,
  todayCompletedCount: 0,
  avgProcessDuration: 0,
  avgTaskDuration: 0
})
const runningProcesses = ref([])
const timeoutTasks = ref([])

let refreshTimer = null

// 方法
const goBack = () => {
  router.back()
}

const loadStatistics = async () => {
  try {
    const res = await request.get('/monitor/statistics')
    if (res.code === 200) {
      statistics.value = res.data
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const loadRunningProcesses = async () => {
  try {
    const res = await request.get('/monitor/processes/running')
    if (res.code === 200) {
      runningProcesses.value = res.data || []
    }
  } catch (error) {
    console.error('加载运行中流程失败:', error)
  }
}

const loadTimeoutTasks = async () => {
  try {
    const res = await request.get('/monitor/tasks/timeout')
    if (res.code === 200) {
      timeoutTasks.value = res.data || []
    }
  } catch (error) {
    console.error('加载超时任务失败:', error)
  }
}

const refreshAll = async () => {
  await Promise.all([
    loadStatistics(),
    loadRunningProcesses(),
    loadTimeoutTasks()
  ])
}

const refreshRunningProcesses = async () => {
  await loadRunningProcesses()
  ElMessage.success('刷新成功')
}

const refreshTimeoutTasks = async () => {
  await loadTimeoutTasks()
  ElMessage.success('刷新成功')
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  return new Date(dateTime).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const formatDuration = (milliseconds) => {
  if (!milliseconds || milliseconds === 0) return '0秒'
  
  const seconds = Math.floor(milliseconds / 1000)
  const minutes = Math.floor(seconds / 60)
  const hours = Math.floor(minutes / 60)
  const days = Math.floor(hours / 24)
  
  if (days > 0) {
    return `${days}天${hours % 24}小时`
  } else if (hours > 0) {
    return `${hours}小时${minutes % 60}分钟`
  } else if (minutes > 0) {
    return `${minutes}分钟${seconds % 60}秒`
  } else {
    return `${seconds}秒`
  }
}

// 生命周期
onMounted(() => {
  refreshAll()
  
  // 每30秒自动刷新
  refreshTimer = setInterval(() => {
    refreshAll()
  }, 30000)
})

onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
  }
})
</script>

<style scoped lang="scss">
.monitor-dashboard {
  padding: 20px;

  .stat-card {
    .stat-content {
      display: flex;
      align-items: center;
      padding: 10px;

      .stat-icon {
        margin-right: 20px;
      }

      .stat-info {
        flex: 1;

        .stat-value {
          font-size: 32px;
          font-weight: bold;
          margin-bottom: 5px;
        }

        .stat-label {
          font-size: 14px;
          color: #999;
        }
      }
    }

    &.running {
      .stat-icon {
        color: #67c23a;
      }
      .stat-value {
        color: #67c23a;
      }
    }

    &.pending {
      .stat-icon {
        color: #409eff;
      }
      .stat-value {
        color: #409eff;
      }
    }

    &.timeout {
      .stat-icon {
        color: #f56c6c;
      }
      .stat-value {
        color: #f56c6c;
      }
    }

    &.exception {
      .stat-icon {
        color: #e6a23c;
      }
      .stat-value {
        color: #e6a23c;
      }
    }
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .today-stats {
    display: flex;
    justify-content: space-around;
    padding: 20px 0;

    .today-item {
      text-align: center;

      .today-label {
        font-size: 14px;
        color: #999;
        margin-bottom: 10px;
      }

      .today-value {
        font-size: 28px;
        font-weight: bold;

        &.started {
          color: #67c23a;
        }

        &.completed {
          color: #409eff;
        }

        &.duration {
          color: #e6a23c;
        }

        &.pending {
          color: #409eff;
        }

        &.timeout {
          color: #f56c6c;
        }
      }
    }
  }

  .empty-data {
    padding: 40px 0;
  }
}
</style>

