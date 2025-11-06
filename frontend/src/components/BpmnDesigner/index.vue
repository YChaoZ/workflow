<template>
  <div class="bpmn-designer">
    <div class="designer-header">
      <div class="header-left">
        <h3>{{ modelName || '流程设计器' }}</h3>
      </div>
      <div class="header-right">
        <el-button @click="handleNew" :icon="Plus">新建</el-button>
        <el-button @click="handleOpen" :icon="FolderOpened">打开</el-button>
        <el-button type="primary" @click="handleSave" :icon="Check">保存</el-button>
        <el-button @click="handleExportXml" :icon="Download">导出XML</el-button>
        <el-button @click="handleExportSvg" :icon="Picture">导出SVG</el-button>
        <el-button @click="handlePreview" :icon="View">预览</el-button>
      </div>
    </div>

    <div class="designer-container">
      <!-- 工具栏区域 -->
      <Toolbar @dragStart="handleToolbarDragStart" />
      
      <!-- BPMN画布区域 -->
      <div class="canvas-container" ref="canvasRef"></div>
      
      <!-- 属性面板区域 -->
      <div class="properties-panel-container" ref="propertiesPanelRef"></div>
    </div>

    <!-- 上传对话框 -->
    <el-dialog v-model="uploadDialogVisible" title="打开流程文件" width="500px">
      <el-upload
        drag
        :auto-upload="false"
        :on-change="handleFileChange"
        :limit="1"
        accept=".bpmn,.xml"
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          拖拽文件到这里或 <em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            仅支持 .bpmn 或 .xml 格式的文件
          </div>
        </template>
      </el-upload>
    </el-dialog>

    <!-- 预览对话框 -->
    <el-dialog v-model="previewDialogVisible" title="流程预览" width="80%" top="5vh">
      <div class="preview-container" v-html="previewContent"></div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  FolderOpened,
  Check,
  Download,
  Picture,
  View,
  UploadFilled
} from '@element-plus/icons-vue'
import Toolbar from './Toolbar.vue'

// 导入bpmn-js
import BpmnModeler from 'bpmn-js/lib/Modeler'
import {
  BpmnPropertiesPanelModule,
  BpmnPropertiesProviderModule
} from 'bpmn-js-properties-panel'
import 'bpmn-js/dist/assets/diagram-js.css'
import 'bpmn-js/dist/assets/bpmn-font/css/bpmn.css'
// properties-panel样式已包含在bpmn-js中

// Props
interface Props {
  xml?: string
  readOnly?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  xml: '',
  readOnly: false
})

// Emits
const emit = defineEmits<{
  save: [xml: string]
  change: [xml: string]
}>()

// Refs
const canvasRef = ref<HTMLDivElement>()
const propertiesPanelRef = ref<HTMLDivElement>()
let modeler: BpmnModeler | null = null

// State
const modelName = ref('新建流程')
const uploadDialogVisible = ref(false)
const previewDialogVisible = ref(false)
const previewContent = ref('')

// 默认的BPMN XML模板
const defaultXML = `<?xml version="1.0" encoding="UTF-8"?>
<bpmn:definitions xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL"
                  xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI"
                  xmlns:dc="http://www.omg.org/spec/DD/20100524/DC"
                  xmlns:di="http://www.omg.org/spec/DD/20100524/DI"
                  id="Definitions_1"
                  targetNamespace="http://bpmn.io/schema/bpmn">
  <bpmn:process id="Process_1" isExecutable="true">
    <bpmn:startEvent id="StartEvent_1"/>
  </bpmn:process>
  <bpmndi:BPMNDiagram id="BPMNDiagram_1">
    <bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="Process_1">
      <bpmndi:BPMNShape id="_BPMNShape_StartEvent_2" bpmnElement="StartEvent_1">
        <dc:Bounds x="173" y="102" width="36" height="36"/>
      </bpmndi:BPMNShape>
    </bpmndi:BPMNPlane>
  </bpmndi:BPMNDiagram>
</bpmn:definitions>`

// 初始化BPMN建模器
const initModeler = () => {
  if (!canvasRef.value || !propertiesPanelRef.value) return

  modeler = new BpmnModeler({
    container: canvasRef.value,
    propertiesPanel: {
      parent: propertiesPanelRef.value
    },
    additionalModules: [
      BpmnPropertiesPanelModule,
      BpmnPropertiesProviderModule
    ],
    keyboard: {
      bindTo: document
    }
  })

  // 加载初始XML
  const initialXML = props.xml || defaultXML
  loadXML(initialXML)

  // 监听变化事件
  modeler.on('commandStack.changed', () => {
    saveXMLToState()
  })
}

// 加载XML
const loadXML = async (xml: string) => {
  if (!modeler) return

  try {
    await modeler.importXML(xml)
    const canvas = modeler.get('canvas')
    canvas.zoom('fit-viewport')
    ElMessage.success('流程加载成功')
  } catch (err: any) {
    console.error('加载流程失败:', err)
    ElMessage.error(`加载失败: ${err.message}`)
  }
}

// 保存XML到状态
const saveXMLToState = async () => {
  if (!modeler) return

  try {
    const { xml } = await modeler.saveXML({ format: true })
    emit('change', xml || '')
  } catch (err: any) {
    console.error('保存XML失败:', err)
  }
}

// 新建流程
const handleNew = async () => {
  try {
    await ElMessageBox.confirm(
      '新建流程将清空当前画布，是否继续？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    modelName.value = '新建流程'
    loadXML(defaultXML)
  } catch {
    // 用户取消
  }
}

// 打开流程文件
const handleOpen = () => {
  uploadDialogVisible.value = true
}

// 文件选择变化
const handleFileChange = (file: any) => {
  const reader = new FileReader()
  reader.onload = (e: any) => {
    const xml = e.target.result
    modelName.value = file.name.replace(/\.(bpmn|xml)$/, '')
    loadXML(xml)
    uploadDialogVisible.value = false
  }
  reader.readAsText(file.raw)
}

// 保存流程
const handleSave = async () => {
  if (!modeler) return

  try {
    const { xml } = await modeler.saveXML({ format: true })
    emit('save', xml || '')
    ElMessage.success('保存成功')
  } catch (err: any) {
    console.error('保存失败:', err)
    ElMessage.error(`保存失败: ${err.message}`)
  }
}

// 导出XML
const handleExportXml = async () => {
  if (!modeler) return

  try {
    const { xml } = await modeler.saveXML({ format: true })
    const blob = new Blob([xml || ''], { type: 'application/xml' })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `${modelName.value || 'process'}.bpmn`
    link.click()
    URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (err: any) {
    console.error('导出失败:', err)
    ElMessage.error(`导出失败: ${err.message}`)
  }
}

// 导出SVG
const handleExportSvg = async () => {
  if (!modeler) return

  try {
    const { svg } = await modeler.saveSVG()
    const blob = new Blob([svg || ''], { type: 'image/svg+xml' })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `${modelName.value || 'process'}.svg`
    link.click()
    URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (err: any) {
    console.error('导出SVG失败:', err)
    ElMessage.error(`导出失败: ${err.message}`)
  }
}

// 预览流程
const handlePreview = async () => {
  if (!modeler) return

  try {
    const { svg } = await modeler.saveSVG()
    previewContent.value = svg || ''
    previewDialogVisible.value = true
  } catch (err: any) {
    console.error('预览失败:', err)
    ElMessage.error(`预览失败: ${err.message}`)
  }
}

// 处理工具栏拖拽
const handleToolbarDragStart = (item: any, event: DragEvent) => {
  console.log('开始拖拽:', item.label)
}

// 获取XML（供外部调用）
const getXML = async (): Promise<string> => {
  if (!modeler) return ''
  const { xml } = await modeler.saveXML({ format: true })
  return xml || ''
}

// 暴露方法给父组件
defineExpose({
  getXML,
  loadXML
})

onMounted(() => {
  initModeler()
})

onBeforeUnmount(() => {
  if (modeler) {
    modeler.destroy()
  }
})
</script>

<style scoped lang="scss">
.bpmn-designer {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #fff;

  .designer-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 12px 16px;
    border-bottom: 1px solid #e4e7ed;

    .header-left {
      h3 {
        margin: 0;
        font-size: 16px;
        font-weight: 500;
      }
    }

    .header-right {
      display: flex;
      gap: 8px;
    }
  }

  .designer-container {
    flex: 1;
    display: flex;
    position: relative;
    overflow: hidden;

    .canvas-container {
      flex: 1;
      height: 100%;
      background: #f5f5f5;
    }

    .properties-panel-container {
      width: 300px;
      height: 100%;
      overflow: auto;
      border-left: 1px solid #e4e7ed;
      background: #fff;

      :deep(.bio-properties-panel) {
        padding: 10px;
      }
    }
  }

  .preview-container {
    width: 100%;
    height: 70vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f5f5f5;

    :deep(svg) {
      max-width: 100%;
      max-height: 100%;
    }
  }
}
</style>

