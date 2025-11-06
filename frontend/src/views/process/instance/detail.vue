<template>
  <div class="process-instance-detail-page">
    <el-page-header @back="handleBack" title="返回">
      <template #content>
        <span class="page-title">流程实例详情</span>
      </template>
    </el-page-header>

    <el-card class="info-card" v-loading="loading">
      <template #header>
        <span>基本信息</span>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="流程实例ID">
          {{ instanceInfo.id }}
        </el-descriptions-item>
        <el-descriptions-item label="流程名称">
          {{ instanceInfo.name }}
        </el-descriptions-item>
        <el-descriptions-item label="业务Key">
          {{ instanceInfo.businessKey || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="流程状态">
          <el-tag :type="instanceInfo.ended ? 'info' : 'success'">
            {{ instanceInfo.ended ? '已结束' : '进行中' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="开始时间">
          {{ instanceInfo.startTime }}
        </el-descriptions-item>
        <el-descriptions-item label="结束时间">
          {{ instanceInfo.endTime || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="发起人">
          {{ instanceInfo.startUserId || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="持续时间">
          {{ formatDuration(instanceInfo.durationInMillis) }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card class="diagram-card">
      <template #header>
        <span>流程图</span>
      </template>
      <div class="diagram-container" v-loading="diagramLoading">
        <BpmnViewer
          v-if="processXml"
          :xml="processXml"
          :highlighted-activities="highlightedActivities"
          :highlighted-flows="highlightedFlows"
        />
      </div>
    </el-card>

    <el-card class="history-card">
      <template #header>
        <span>任务历史</span>
      </template>
      <el-timeline>
        <el-timeline-item
          v-for="item in taskHistory"
          :key="item.id"
          :timestamp="item.endTime"
          placement="top"
          :type="getTimelineType(item)"
        >
          <el-card>
            <div class="task-item">
              <div class="task-header">
                <span class="task-name">{{ item.name }}</span>
                <el-tag :type="item.deleteReason ? 'danger' : 'success'" size="small">
                  {{ item.deleteReason || '已完成' }}
                </el-tag>
              </div>
              <div class="task-info">
                <div><strong>办理人：</strong>{{ item.assignee || '-' }}</div>
                <div><strong>开始时间：</strong>{{ item.startTime }}</div>
                <div><strong>持续时间：</strong>{{ formatDuration(item.durationInMillis) }}</div>
                <div v-if="item.deleteReason">
                  <strong>删除原因：</strong>{{ item.deleteReason }}
                </div>
              </div>
            </div>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import BpmnViewer from '@/components/BpmnViewer/index.vue'
import { getProcessInstanceById } from '@/api/process'
import { getProcessDefinitionXml } from '@/api/definition'

const route = useRoute()
const router = useRouter()

interface ProcessInstance {
  id: string
  name: string
  businessKey?: string
  ended: boolean
  startTime: string
  endTime?: string
  startUserId?: string
  durationInMillis?: number
}

interface TaskHistory {
  id: string
  name: string
  assignee?: string
  startTime: string
  endTime: string
  durationInMillis?: number
  deleteReason?: string
  activityId: string
}

const loading = ref(false)
const diagramLoading = ref(false)
const instanceInfo = ref<ProcessInstance>({
  id: '',
  name: '',
  ended: false,
  startTime: ''
})
const taskHistory = ref<TaskHistory[]>([])
const processXml = ref('')
const highlightedActivities = ref<string[]>([])
const highlightedFlows = ref<string[]>([])

// 加载流程实例详情
const loadInstanceDetail = async () => {
  const instanceId = route.params.id as string
  if (!instanceId) {
    ElMessage.error('流程实例ID不能为空')
    return
  }

  loading.value = true
  try {
    const response = await getProcessInstanceById(instanceId)
    instanceInfo.value = response as any
    
    // 加载任务历史
    // TODO: 调用任务历史API
    taskHistory.value = []
    
    // 加载流程图
    await loadProcessDiagram(response.processDefinitionId)
    
    // 提取已完成的活动节点
    highlightedActivities.value = taskHistory.value.map(t => t.activityId)
  } catch (error: any) {
    console.error('加载流程实例详情失败:', error)
    ElMessage.error(error.message || '加载失败')
  } finally {
    loading.value = false
  }
}

// 加载流程图
const loadProcessDiagram = async (definitionId: string) => {
  diagramLoading.value = true
  try {
    const response = await getProcessDefinitionXml(definitionId)
    processXml.value = response.xml
  } catch (error: any) {
    console.error('加载流程图失败:', error)
    ElMessage.error(error.message || '加载流程图失败')
  } finally {
    diagramLoading.value = false
  }
}

// 格式化持续时间
const formatDuration = (millis?: number): string => {
  if (!millis) return '-'
  
  const seconds = Math.floor(millis / 1000)
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

// 获取时间线类型
const getTimelineType = (item: TaskHistory): string => {
  if (item.deleteReason) return 'danger'
  return 'success'
}

// 返回
const handleBack = () => {
  router.back()
}

onMounted(() => {
  loadInstanceDetail()
})
</script>

<style scoped lang="scss">
.process-instance-detail-page {
  padding: 20px;

  .page-title {
    font-size: 18px;
    font-weight: 500;
  }

  .info-card {
    margin-top: 20px;
  }

  .diagram-card {
    margin-top: 20px;

    .diagram-container {
      width: 100%;
      height: 600px;
      border: 1px solid #e4e7ed;
      border-radius: 4px;
      overflow: hidden;
    }
  }

  .history-card {
    margin-top: 20px;

    .task-item {
      .task-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 10px;

        .task-name {
          font-size: 16px;
          font-weight: 500;
        }
      }

      .task-info {
        font-size: 14px;
        color: #666;
        line-height: 2;

        div {
          margin-bottom: 5px;
        }
      }
    }
  }
}
</style>

