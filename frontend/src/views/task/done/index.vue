<template>
  <div class="done-task">
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
          <span>已办任务列表</span>
          <el-tag type="success">共 {{ total }} 个</el-tag>
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
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="完成时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag type="success">已完成</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
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
import { ElMessage } from 'element-plus'
import { Search, Refresh, View } from '@element-plus/icons-vue'
import { taskApi, Task, TaskQuery } from '@/api/task'
import { formatDateTime } from '@/utils/date'
import { useUserStore } from '@/store/modules/user'

const userStore = useUserStore()

// 查询表单
const queryForm = reactive<TaskQuery>({
  assignee: userStore.username, // 查询当前用户的已办任务
  taskStatus: 'done',
  taskName: '',
  processKey: '',
  pageNum: 1,
  pageSize: 10
})

// 表格数据
const tableData = ref<Task[]>([])
const total = ref(0)
const loading = ref(false)

// 查询任务列表
const handleQuery = async () => {
  loading.value = true
  try {
    const response = await taskApi.queryTasks(queryForm)
    tableData.value = response.data.list
    total.value = response.data.total
  } catch (error) {
    console.error('查询失败:', error)
    ElMessage.error('查询已办任务列表失败')
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

// 页面加载时查询数据
onMounted(() => {
  handleQuery()
})
</script>

<style scoped lang="scss">
.done-task {
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
