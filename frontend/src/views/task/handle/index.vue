<template>
  <div class="task-handle-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>办理任务</span>
          <el-button @click="handleBack">返回</el-button>
        </div>
      </template>

      <div class="task-info" v-if="taskInfo">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="任务名称">{{ taskInfo.taskName }}</el-descriptions-item>
          <el-descriptions-item label="流程名称">{{ taskInfo.processKey }}</el-descriptions-item>
          <el-descriptions-item label="业务KEY">{{ taskInfo.businessKey ||'-' }}</el-descriptions-item>
          <el-descriptions-item label="办理人">{{ taskInfo.assignee }}</el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="2">{{ taskInfo.createTime }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <el-divider />

      <div v-if="formConfig">
        <h3>填写表单</h3>
        <DynamicForm
          ref="dynamicFormRef"
          :formConfig="formConfig"
          v-model="formData"
          :readonly="false"
        />
      </div>
      <el-empty v-else description="该任务未配置表单" />

      <el-divider />

      <div class="comment-section">
        <h3>审批意见</h3>
        <el-input
          v-model="comment"
          type="textarea"
          :rows="4"
          placeholder="请输入审批意见（可选）"
        />
      </div>

      <div class="action-buttons">
        <el-button @click="handleBack">取消</el-button>
        <el-button type="danger" @click="handleReject" :loading="submitting">驳回</el-button>
        <el-button type="success" @click="handleApprove" :loading="submitting">通过</el-button>
        <el-button type="primary" @click="handleComplete" :loading="submitting">完成</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import DynamicForm from '@/components/DynamicForm/index.vue'
import { taskApi } from '@/api/task'
import { formApi, processFormApi, formDataApi } from '@/api/form'

const route = useRoute()
const router = useRouter()

const taskId = ref('')
const taskInfo = ref<any>(null)
const formConfig = ref('')
const formData = ref<any>({})
const dynamicFormRef = ref()
const comment = ref('')
const submitting = ref(false)

// 加载任务信息
const loadTaskInfo = async () => {
  try {
    const res = await taskApi.getTaskById(taskId.value)
    if (res.code === 200) {
      taskInfo.value = res.data
      
      // 查询任务关联的表单
      await loadTaskForm()
      
      // 加载已保存的表单数据
      await loadSavedFormData()
    }
  } catch (error) {
    console.error('加载任务信息失败:', error)
    ElMessage.error('加载任务信息失败')
  }
}

// 加载任务关联的表单
const loadTaskForm = async () => {
  if (!taskInfo.value) return
  
  try {
    // 先尝试查询节点表单
    const res = await processFormApi.getByProcessKeyAndNode(
      taskInfo.value.processKey,
      taskInfo.value.taskDefKey
    )
    
    let formKey = null
    
    if (res.code === 200 && res.data) {
      formKey = res.data.formKey
    } else {
      // 如果没有节点表单，查询全局表单
      const globalRes = await processFormApi.getGlobalForm(taskInfo.value.processKey)
      if (globalRes.code === 200 && globalRes.data) {
        formKey = globalRes.data.formKey
      }
    }
    
    if (formKey) {
      // 根据formKey查询表单定义
      const formRes = await formApi.getByKey(formKey)
      if (formRes.code === 200 && formRes.data) {
        formConfig.value = formRes.data.formConfig
      }
    }
  } catch (error) {
    console.error('加载表单配置失败:', error)
  }
}

// 加载已保存的表单数据
const loadSavedFormData = async () => {
  try {
    const res = await formDataApi.getByTask(taskId.value)
    if (res.code === 200 && res.data) {
      formData.value = JSON.parse(res.data.formData)
    }
  } catch (error) {
    console.error('加载表单数据失败:', error)
  }
}

// 完成任务
const handleComplete = async () => {
  await submitTask({})
}

// 通过
const handleApprove = async () => {
  await submitTask({ approved: true, action: 'approve' })
}

// 驳回
const handleReject = async () => {
  await submitTask({ approved: false, action: 'reject' })
}

// 提交任务
const submitTask = async (variables: any) => {
  // 如果有表单，先验证
  if (formConfig.value && dynamicFormRef.value) {
    const valid = await dynamicFormRef.value.validate()
    if (!valid) {
      ElMessage.warning('请正确填写表单')
      return
    }
  }
  
  submitting.value = true
  try {
    // 如果有表单数据，先保存
    if (formConfig.value && dynamicFormRef.value) {
      const formDataToSave = dynamicFormRef.value.getFormData()
      
      // 查询表单ID
      const processFormRes = await processFormApi.getByProcessKeyAndNode(
        taskInfo.value.processKey,
        taskInfo.value.taskDefKey
      )
      
      let formKey = null
      if (processFormRes.code === 200 && processFormRes.data) {
        formKey = processFormRes.data.formKey
      } else {
        const globalRes = await processFormApi.getGlobalForm(taskInfo.value.processKey)
        if (globalRes.code === 200 && globalRes.data) {
          formKey = globalRes.data.formKey
        }
      }
      
      if (formKey) {
        const formRes = await formApi.getByKey(formKey)
        if (formRes.code === 200 && formRes.data) {
          await formDataApi.save({
            formId: formRes.data.id,
            processInstanceId: taskInfo.value.processInstanceId,
            taskId: taskId.value,
            formData: JSON.stringify(formDataToSave),
            createBy: 'admin',
            submitUser: 'admin'
          })
        }
      }
      
      // 将表单数据作为变量传递
      Object.assign(variables, formDataToSave)
    }
    
    // 完成任务
    const res = await taskApi.completeTask({
      taskId: taskId.value,
      variables,
      comment: comment.value
    })
    
    if (res.code === 200) {
      ElMessage.success('任务办理成功')
      router.push('/task/todo')
    } else {
      ElMessage.error(res.message || '办理失败')
    }
  } catch (error: any) {
    console.error('办理任务失败:', error)
    ElMessage.error(error.response?.data?.message || '办理失败')
  } finally {
    submitting.value = false
  }
}

// 返回
const handleBack = () => {
  router.back()
}

// 初始化
onMounted(() => {
  const id = route.query.taskId as string
  if (id) {
    taskId.value = id
    loadTaskInfo()
  } else {
    ElMessage.error('缺少任务ID')
    router.back()
  }
})
</script>

<style scoped lang="scss">
.task-handle-page {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.task-info {
  margin-top: 20px;
}

.comment-section {
  margin-top: 20px;
  
  h3 {
    margin-bottom: 10px;
  }
}

.action-buttons {
  margin-top: 30px;
  text-align: right;
  padding-top: 20px;
  border-top: 1px solid #eee;
}
</style>

