<template>
  <div class="process-design-page">
    <BpmnDesigner
      ref="designerRef"
      :xml="processXml"
      @save="handleSave"
      @change="handleChange"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElLoading } from 'element-plus'
import BpmnDesigner from '@/components/BpmnDesigner/index.vue'

const route = useRoute()
const router = useRouter()

const designerRef = ref<InstanceType<typeof BpmnDesigner>>()
const processXml = ref('')
const processKey = ref('')
const processName = ref('')

// 加载流程定义
const loadProcess = async () => {
  const id = route.query.id as string
  if (!id) {
    return
  }

  const loading = ElLoading.service({
    lock: true,
    text: '加载中...',
    background: 'rgba(0, 0, 0, 0.7)'
  })

  try {
    // TODO: 调用API加载流程定义
    // const response = await processApi.getProcessXml(id)
    // processXml.value = response.xml
    // processKey.value = response.key
    // processName.value = response.name
    
    ElMessage.success('流程加载成功')
  } catch (error: any) {
    console.error('加载流程失败:', error)
    ElMessage.error(error.message || '加载失败')
  } finally {
    loading.close()
  }
}

// 保存流程
const handleSave = async (xml: string) => {
  const loading = ElLoading.service({
    lock: true,
    text: '保存中...',
    background: 'rgba(0, 0, 0, 0.7)'
  })

  try {
    // TODO: 调用API保存流程
    // if (processKey.value) {
    //   // 更新现有流程
    //   await processApi.updateProcess({
    //     key: processKey.value,
    //     name: processName.value,
    //     xml
    //   })
    // } else {
    //   // 创建新流程
    //   await processApi.createProcess({
    //     name: '新流程',
    //     xml
    //   })
    // }
    
    console.log('保存的XML:', xml)
    ElMessage.success('保存成功')
  } catch (error: any) {
    console.error('保存失败:', error)
    ElMessage.error(error.message || '保存失败')
  } finally {
    loading.close()
  }
}

// 监听变化
const handleChange = (xml: string) => {
  console.log('流程已变化')
}

onMounted(() => {
  loadProcess()
})
</script>

<style scoped lang="scss">
.process-design-page {
  height: calc(100vh - 60px);
  background: #fff;
}
</style>
