<template>
  <div class="home-container">
    <el-card class="welcome-card">
      <h2>欢迎使用工作流系统！</h2>
      <p>这是基于 Vue 3 + Element Plus + Flowable 构建的企业级工作流系统</p>
    </el-card>

    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background-color: #409eff">
            <el-icon :size="30"><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.processCount }}</div>
            <div class="stat-label">流程定义</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background-color: #67c23a">
            <el-icon :size="30"><Clock /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.todoCount }}</div>
            <div class="stat-label">待办任务</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background-color: #e6a23c">
            <el-icon :size="30"><Check /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.doneCount }}</div>
            <div class="stat-label">已办任务</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background-color: #f56c6c">
            <el-icon :size="30"><Monitor /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.runningCount }}</div>
            <div class="stat-label">运行中流程</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="content-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>快捷入口</span>
          </template>
          <div class="quick-links">
            <el-button type="primary" @click="$router.push('/process/design')">
              <el-icon><Edit /></el-icon>
              <span>流程设计</span>
            </el-button>
            <el-button type="success" @click="$router.push('/task/todo')">
              <el-icon><Clock /></el-icon>
              <span>待办任务</span>
            </el-button>
            <el-button type="warning" @click="$router.push('/process/instance')">
              <el-icon><Monitor /></el-icon>
              <span>流程实例</span>
            </el-button>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>系统信息</span>
          </template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="系统版本">v1.0.0</el-descriptions-item>
            <el-descriptions-item label="工作流引擎">Flowable 7.0.1</el-descriptions-item>
            <el-descriptions-item label="前端框架">Vue 3 + TypeScript</el-descriptions-item>
            <el-descriptions-item label="UI组件库">Element Plus</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { reactive, onMounted } from 'vue'
import { Document, Clock, Check, Monitor, Edit } from '@element-plus/icons-vue'
import { getHomeStatistics } from '@/api/home'
import { ElMessage } from 'element-plus'

const stats = reactive({
  processCount: 0,
  todoCount: 0,
  doneCount: 0,
  runningCount: 0
})

// 加载统计数据
const loadStatistics = async () => {
  try {
    const response = await getHomeStatistics()
    if (response.code === 200 && response.data) {
      stats.processCount = response.data.processCount || 0
      stats.todoCount = response.data.todoCount || 0
      stats.doneCount = response.data.doneCount || 0
      stats.runningCount = response.data.runningCount || 0
    } else {
      ElMessage.warning('获取统计数据失败')
    }
  } catch (error) {
    console.error('加载首页统计数据失败:', error)
    ElMessage.error('加载统计数据失败，请稍后重试')
  }
}

// 页面加载时获取数据
onMounted(() => {
  loadStatistics()
})
</script>

<style scoped lang="scss">
.home-container {
  .welcome-card {
    margin-bottom: 20px;
    text-align: center;

    h2 {
      margin: 0;
      font-size: 28px;
      color: #303133;
    }

    p {
      margin: 10px 0 0;
      font-size: 16px;
      color: #909399;
    }
  }

  .stats-row {
    margin-bottom: 20px;

    .stat-card {
      display: flex;
      align-items: center;
      padding: 20px;

      .stat-icon {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 60px;
        height: 60px;
        border-radius: 10px;
        color: #fff;
        margin-right: 15px;
      }

      .stat-info {
        flex: 1;

        .stat-value {
          font-size: 28px;
          font-weight: bold;
          color: #303133;
        }

        .stat-label {
          font-size: 14px;
          color: #909399;
          margin-top: 5px;
        }
      }
    }
  }

  .content-row {
    .quick-links {
      display: flex;
      gap: 15px;

      .el-button {
        flex: 1;
        height: 60px;
        flex-direction: column;
        gap: 5px;
      }
    }
  }
}
</style>

