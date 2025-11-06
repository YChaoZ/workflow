<template>
  <div class="process-start-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>启动流程</span>
          <el-button @click="handleBack">返回</el-button>
        </div>
      </template>

      <div class="process-info" v-if="processDefinition">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="流程名称">{{ processDefinition.name }}</el-descriptions-item>
          <el-descriptions-item label="流程标识">{{ processDefinition.key }}</el-descriptions-item>
          <el-descriptions-item label="版本号">V{{ processDefinition.version }}</el-descriptions-item>
          <el-descriptions-item label="分类">{{ processDefinition.category || '-' }}</el-descriptions-item>
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
      <el-empty v-else description="该流程未配置表单" />

      <div class="action-buttons">
        <el-button @click="handleBack">取消</el-button>
        <el-button type="primary" @click="handleStart" :loading="starting">启动流程</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import DynamicForm from '@/components/DynamicForm/index.vue'
import { getProcessDefinitionById } from '@/api/definition'
import { formApi, processFormApi, formDataApi } from '@/api/form'
import { processApi } from '@/api/process'

const route = useRoute()
const router = useRouter()

const processDefinitionId = ref('')
const processDefinition = ref<any>(null)
const formConfig = ref('')
const formData = ref<any>({})
const dynamicFormRef = ref()
const starting = ref(false)

// 加载流程定义信息
const loadProcessDefinition = async () => {
  try {
    const res = await getProcessDefinitionById(processDefinitionId.value)
    if (res.code === 200) {
      processDefinition.value = res.data
      
      // 查询全局表单
      await loadGlobalForm()
    }
  } catch (error) {
    console.error('加载流程定义失败:', error)
    ElMessage.error('加载流程定义失败')
  }
}

// 加载全局表单
const loadGlobalForm = async () => {
  if (!processDefinition.value) return
  
  try {
    const res = await processFormApi.getGlobalForm(processDefinition.value.key)
    if (res.code === 200 && res.data) {
      const processForm = res.data
      
      // 根据formKey查询表单定义
      const formRes = await formApi.getByKey(processForm.formKey)
      if (formRes.code === 200 && formRes.data) {
        formConfig.value = formRes.data.formConfig
      }
    }
  } catch (error) {
    console.error('加载表单配置失败:', error)
  }
}

// 启动流程
const handleStart = async () => {
  // 如果有表单，先验证
  if (formConfig.value && dynamicFormRef.value) {
    const valid = await dynamicFormRef.value.validate()
    if (!valid) {
      ElMessage.warning('请正确填写表单')
      return
    }
  }
  
  starting.value = true
  try {
    // 获取当前用户信息（从localStorage或全局状态）
    const currentUser = localStorage.getItem('username') || 'testUser'
    
    // 启动流程实例
    const startRes = await processApi.startProcess({
      processKey: processDefinition.value.key,
      businessKey: `BK_${Date.now()}`,
      startUser: currentUser,
      variables: formData.value
    })
    
    if (startRes.code === 200) {
      const processInstanceId = startRes.data.id
      
      // 如果有表单数据，保存表单数据
      if (formConfig.value && dynamicFormRef.value) {
        const formDataToSave = dynamicFormRef.value.getFormData()
        
        // 查询表单ID
        const processFormRes = await processFormApi.getGlobalForm(processDefinition.value.key)
        if (processFormRes.code === 200 && processFormRes.data) {
          const formKeyToSave = processFormRes.data.formKey
          const formRes = await formApi.getByKey(formKeyToSave)
          
          if (formRes.code === 200 && formRes.data) {
            await formDataApi.save({
              formId: formRes.data.id,
              processInstanceId: processInstanceId,
              taskId: null,
              formData: JSON.stringify(formDataToSave),
              createBy: 'admin',
              submitUser: 'admin'
            })
          }
        }
      }
      
      ElMessage.success('流程启动成功')
      router.push('/process/instance')
    } else {
      ElMessage.error(startRes.message || '启动失败')
    }
  } catch (error: any) {
    console.error('启动流程失败:', error)
    ElMessage.error(error.response?.data?.message || '启动失败')
  } finally {
    starting.value = false
  }
}

// 返回
const handleBack = () => {
  router.back()
}

// 初始化
onMounted(() => {
  const id = route.query.id as string
  if (id) {
    processDefinitionId.value = id
    loadProcessDefinition()
  } else {
    ElMessage.error('缺少流程定义ID')
    router.back()
  }
})
</script>

<style scoped lang="scss">
.process-start-page {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.process-info {
  margin-top: 20px;
}

.action-buttons {
  margin-top: 30px;
  text-align: right;
  padding-top: 20px;
  border-top: 1px solid #eee;
}
</style>

