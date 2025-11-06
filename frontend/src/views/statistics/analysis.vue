<template>
  <div class="statistics-analysis">
    <!-- 页面标题 -->
    <el-page-header @back="goBack">
      <template #content>
        <span class="page-title">流程统计分析</span>
      </template>
    </el-page-header>

    <!-- 时间筛选 -->
    <el-card class="filter-card" shadow="never">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
          />
        </el-form-item>
        <el-form-item label="流程">
          <el-select v-model="filterForm.processKey" placeholder="全部流程" clearable>
            <el-option label="全部流程" value="" />
            <el-option label="简单审批流程" value="simpleProcess" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadAllData">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 完成率统计 -->
    <el-row :gutter="20" class="mb-4">
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <span class="card-title">流程完成率</span>
          </template>
          <div ref="completionChart" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <span class="card-title">流程趋势分析</span>
          </template>
          <div ref="trendChart" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 用户工作量与流程效率 -->
    <el-row :gutter="20" class="mb-4">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span class="card-title">用户工作量排行 TOP10</span>
          </template>
          <div ref="workloadChart" style="height: 350px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span class="card-title">流程效率排行 TOP10</span>
          </template>
          <div ref="efficiencyChart" style="height: 350px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 流程统计详情 -->
    <el-card shadow="hover" class="mb-4">
      <template #header>
        <span class="card-title">流程统计详情</span>
      </template>
      <el-table :data="processStatistics" border>
        <el-table-column prop="processDefinitionName" label="流程名称" width="200" />
        <el-table-column prop="totalStarted" label="启动数量" width="100" align="center" />
        <el-table-column prop="totalCompleted" label="完成数量" width="100" align="center" />
        <el-table-column prop="totalRunning" label="运行中" width="100" align="center" />
        <el-table-column label="完成率" width="120" align="center">
          <template #default="{ row }">
            <el-progress
              :percentage="calculateCompletionRate(row)"
              :color="getProgressColor(calculateCompletionRate(row))"
            />
          </template>
        </el-table-column>
        <el-table-column label="平均耗时" width="150" align="center">
          <template #default="{ row }">
            {{ formatDuration(row.avgDuration) }}
          </template>
        </el-table-column>
        <el-table-column label="最短耗时" width="150" align="center">
          <template #default="{ row }">
            {{ formatDuration(row.minDuration) }}
          </template>
        </el-table-column>
        <el-table-column label="最长耗时" width="150" align="center">
          <template #default="{ row }">
            {{ formatDuration(row.maxDuration) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 任务统计详情 -->
    <el-card shadow="hover">
      <template #header>
        <span class="card-title">任务统计详情</span>
      </template>
      <el-table :data="taskStatistics" border>
        <el-table-column prop="taskName" label="任务名称" width="200" />
        <el-table-column prop="totalTasks" label="总任务数" width="100" align="center" />
        <el-table-column prop="completedTasks" label="已完成" width="100" align="center" />
        <el-table-column prop="pendingTasks" label="待处理" width="100" align="center" />
        <el-table-column label="完成率" width="120" align="center">
          <template #default="{ row }">
            <el-progress
              :percentage="calculateTaskCompletionRate(row)"
              :color="getProgressColor(calculateTaskCompletionRate(row))"
            />
          </template>
        </el-table-column>
        <el-table-column label="平均耗时" width="150" align="center">
          <template #default="{ row }">
            {{ formatDuration(row.avgDuration) }}
          </template>
        </el-table-column>
        <el-table-column label="最短耗时" width="150" align="center">
          <template #default="{ row }">
            {{ formatDuration(row.minDuration) }}
          </template>
        </el-table-column>
        <el-table-column label="最长耗时" width="150" align="center">
          <template #default="{ row }">
            {{ formatDuration(row.maxDuration) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import request from '@/api/request'
import { ElMessage } from 'element-plus'

const router = useRouter()

// 数据
const filterForm = ref({
  processKey: '',
  startTime: '',
  endTime: ''
})
const dateRange = ref([])
const completionRate = ref(null)
const processStatistics = ref([])
const taskStatistics = ref([])
const timeSeries = ref([])
const userWorkload = ref([])
const processEfficiency = ref([])

// 图表实例
const completionChart = ref(null)
const trendChart = ref(null)
const workloadChart = ref(null)
const efficiencyChart = ref(null)

// 方法
const goBack = () => {
  router.back()
}

const handleDateChange = (dates: any) => {
  if (dates && dates.length === 2) {
    filterForm.value.startTime = dates[0] + 'T00:00:00'
    filterForm.value.endTime = dates[1] + 'T23:59:59'
  } else {
    filterForm.value.startTime = ''
    filterForm.value.endTime = ''
  }
}

const resetFilter = () => {
  filterForm.value = {
    processKey: '',
    startTime: '',
    endTime: ''
  }
  dateRange.value = []
  loadAllData()
}

// 加载所有数据
const loadAllData = async () => {
  try {
    await Promise.all([
      loadCompletionRate(),
      loadProcessStatistics(),
      loadTaskStatistics(),
      loadTimeSeries(),
      loadUserWorkload(),
      loadProcessEfficiency()
    ])
    
    // 等待DOM更新后渲染图表
    await nextTick()
    renderCharts()
  } catch (error) {
    console.error('加载统计数据失败:', error)
    ElMessage.error('加载统计数据失败')
  }
}

// 加载完成率统计
const loadCompletionRate = async () => {
  const params: any = {}
  if (filterForm.value.startTime) params.startTime = filterForm.value.startTime
  if (filterForm.value.endTime) params.endTime = filterForm.value.endTime
  
  const res = await request.get('/statistics/completion-rate', { params })
  if (res.code === 200) {
    completionRate.value = res.data
  }
}

// 加载流程统计
const loadProcessStatistics = async () => {
  const params: any = {}
  if (filterForm.value.processKey) params.processDefinitionKey = filterForm.value.processKey
  if (filterForm.value.startTime) params.startTime = filterForm.value.startTime
  if (filterForm.value.endTime) params.endTime = filterForm.value.endTime
  
  const res = await request.get('/statistics/process', { params })
  if (res.code === 200) {
    processStatistics.value = res.data || []
  }
}

// 加载任务统计
const loadTaskStatistics = async () => {
  const params: any = {}
  if (filterForm.value.processKey) params.processDefinitionKey = filterForm.value.processKey
  if (filterForm.value.startTime) params.startTime = filterForm.value.startTime
  if (filterForm.value.endTime) params.endTime = filterForm.value.endTime
  
  const res = await request.get('/statistics/task', { params })
  if (res.code === 200) {
    taskStatistics.value = res.data || []
  }
}

// 加载时间序列
const loadTimeSeries = async () => {
  const params: any = { granularity: 'day' }
  if (filterForm.value.processKey) params.processDefinitionKey = filterForm.value.processKey
  if (filterForm.value.startTime) params.startTime = filterForm.value.startTime
  if (filterForm.value.endTime) params.endTime = filterForm.value.endTime
  
  const res = await request.get('/statistics/process/timeseries', { params })
  if (res.code === 200) {
    timeSeries.value = res.data || []
  }
}

// 加载用户工作量
const loadUserWorkload = async () => {
  const params: any = {}
  if (filterForm.value.startTime) params.startTime = filterForm.value.startTime
  if (filterForm.value.endTime) params.endTime = filterForm.value.endTime
  
  const res = await request.get('/statistics/user/workload', { params })
  if (res.code === 200) {
    userWorkload.value = res.data || []
  }
}

// 加载流程效率排行
const loadProcessEfficiency = async () => {
  const params: any = { topN: 10 }
  if (filterForm.value.startTime) params.startTime = filterForm.value.startTime
  if (filterForm.value.endTime) params.endTime = filterForm.value.endTime
  
  const res = await request.get('/statistics/process/efficiency-ranking', { params })
  if (res.code === 200) {
    processEfficiency.value = res.data || []
  }
}

// 渲染所有图表
const renderCharts = () => {
  renderCompletionChart()
  renderTrendChart()
  renderWorkloadChart()
  renderEfficiencyChart()
}

// 渲染完成率饼图
const renderCompletionChart = () => {
  if (!completionChart.value || !completionRate.value) return
  
  const chart = echarts.init(completionChart.value)
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '流程状态',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 20,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: [
          { value: completionRate.value.totalCompleted, name: '已完成', itemStyle: { color: '#67C23A' } },
          { value: completionRate.value.totalRunning, name: '运行中', itemStyle: { color: '#409EFF' } },
          { value: completionRate.value.totalTerminated, name: '已终止', itemStyle: { color: '#F56C6C' } }
        ]
      }
    ]
  }
  chart.setOption(option)
  
  // 响应式
  window.addEventListener('resize', () => chart.resize())
}

// 渲染趋势折线图
const renderTrendChart = () => {
  if (!trendChart.value || !timeSeries.value || timeSeries.value.length === 0) return
  
  const chart = echarts.init(trendChart.value)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['启动数量', '完成数量']
    },
    xAxis: {
      type: 'category',
      data: timeSeries.value.map((item: any) => item.timeKey)
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '启动数量',
        type: 'line',
        data: timeSeries.value.map((item: any) => item.started),
        smooth: true,
        itemStyle: { color: '#409EFF' }
      },
      {
        name: '完成数量',
        type: 'line',
        data: timeSeries.value.map((item: any) => item.completed),
        smooth: true,
        itemStyle: { color: '#67C23A' }
      }
    ]
  }
  chart.setOption(option)
  
  window.addEventListener('resize', () => chart.resize())
}

// 渲染用户工作量柱状图
const renderWorkloadChart = () => {
  if (!workloadChart.value || !userWorkload.value || userWorkload.value.length === 0) return
  
  const chart = echarts.init(workloadChart.value)
  const data = userWorkload.value.slice(0, 10) // TOP 10
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value'
    },
    yAxis: {
      type: 'category',
      data: data.map((item: any) => item.username)
    },
    series: [
      {
        name: '任务数量',
        type: 'bar',
        data: data.map((item: any) => item.totalTasks),
        itemStyle: { color: '#409EFF' }
      }
    ]
  }
  chart.setOption(option)
  
  window.addEventListener('resize', () => chart.resize())
}

// 渲染流程效率柱状图
const renderEfficiencyChart = () => {
  if (!efficiencyChart.value || !processEfficiency.value || processEfficiency.value.length === 0) return
  
  const chart = echarts.init(efficiencyChart.value)
  const data = processEfficiency.value
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: (params: any) => {
        const item = params[0]
        return `${item.name}<br/>平均耗时: ${formatDuration(item.value)}`
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.map((item: any) => item.processDefinitionName),
      axisLabel: {
        interval: 0,
        rotate: 30
      }
    },
    yAxis: {
      type: 'value',
      name: '平均耗时(秒)',
      axisLabel: {
        formatter: (value: number) => (value / 1000).toFixed(2)
      }
    },
    series: [
      {
        name: '平均耗时',
        type: 'bar',
        data: data.map((item: any) => item.avgDuration),
        itemStyle: { color: '#E6A23C' }
      }
    ]
  }
  chart.setOption(option)
  
  window.addEventListener('resize', () => chart.resize())
}

// 计算完成率
const calculateCompletionRate = (row: any) => {
  if (!row.totalStarted || row.totalStarted === 0) return 0
  return Math.round((row.totalCompleted / row.totalStarted) * 100)
}

const calculateTaskCompletionRate = (row: any) => {
  if (!row.totalTasks || row.totalTasks === 0) return 0
  return Math.round((row.completedTasks / row.totalTasks) * 100)
}

// 获取进度条颜色
const getProgressColor = (percentage: number) => {
  if (percentage >= 80) return '#67C23A'
  if (percentage >= 60) return '#E6A23C'
  return '#F56C6C'
}

// 格式化时长
const formatDuration = (ms: number) => {
  if (!ms || ms === 0) return '0秒'
  const seconds = Math.floor(ms / 1000)
  const minutes = Math.floor(seconds / 60)
  const hours = Math.floor(minutes / 60)
  const days = Math.floor(hours / 24)

  let result = ''
  if (days > 0) result += `${days}天`
  if (hours % 24 > 0) result += `${hours % 24}小时`
  if (minutes % 60 > 0) result += `${minutes % 60}分钟`
  if (seconds % 60 > 0) result += `${seconds % 60}秒`
  return result || '不足1秒'
}

// 初始化
onMounted(() => {
  loadAllData()
})
</script>

<style scoped lang="scss">
.statistics-analysis {
  padding: 20px;

  .page-title {
    font-size: 18px;
    font-weight: bold;
  }

  .filter-card {
    margin-top: 20px;
    margin-bottom: 20px;
  }

  .filter-form {
    display: flex;
    align-items: center;
  }

  .mb-4 {
    margin-bottom: 20px;
  }

  .card-title {
    font-size: 16px;
    font-weight: bold;
  }
}
</style>

