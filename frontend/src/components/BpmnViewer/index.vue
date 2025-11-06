<template>
  <div class="bpmn-viewer">
    <div class="viewer-container" ref="viewerRef"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import { ElMessage } from 'element-plus'
import BpmnViewer from 'bpmn-js/lib/Viewer'
import 'bpmn-js/dist/assets/diagram-js.css'
import 'bpmn-js/dist/assets/bpmn-font/css/bpmn.css'

// Props
interface Props {
  xml: string
  highlightedActivities?: string[]  // 需要高亮的活动节点ID列表
  highlightedFlows?: string[]       // 需要高亮的连线ID列表
}

const props = withDefaults(defineProps<Props>(), {
  xml: '',
  highlightedActivities: () => [],
  highlightedFlows: () => []
})

// Refs
const viewerRef = ref<HTMLDivElement>()
let viewer: BpmnViewer | null = null

// 初始化BPMN查看器
const initViewer = () => {
  if (!viewerRef.value) return

  viewer = new BpmnViewer({
    container: viewerRef.value
  })

  // 加载XML
  if (props.xml) {
    loadXML(props.xml)
  }
}

// 加载XML
const loadXML = async (xml: string) => {
  if (!viewer || !xml) return

  try {
    await viewer.importXML(xml)
    const canvas = viewer.get('canvas')
    canvas.zoom('fit-viewport')
    
    // 应用高亮
    applyHighlight()
  } catch (err: any) {
    console.error('加载流程图失败:', err)
    ElMessage.error(`加载失败: ${err.message}`)
  }
}

// 应用高亮效果
const applyHighlight = () => {
  if (!viewer) return

  const canvas = viewer.get('canvas')
  
  // 清除之前的高亮
  const elementRegistry = viewer.get('elementRegistry')
  const allElements = elementRegistry.getAll()
  allElements.forEach((element: any) => {
    canvas.removeMarker(element.id, 'highlight-activity')
    canvas.removeMarker(element.id, 'highlight-flow')
  })

  // 高亮活动节点（已完成的任务）
  props.highlightedActivities.forEach((activityId) => {
    try {
      canvas.addMarker(activityId, 'highlight-activity')
    } catch (err) {
      console.warn(`无法高亮节点: ${activityId}`, err)
    }
  })

  // 高亮连线（已完成的流）
  props.highlightedFlows.forEach((flowId) => {
    try {
      canvas.addMarker(flowId, 'highlight-flow')
    } catch (err) {
      console.warn(`无法高亮连线: ${flowId}`, err)
    }
  })
}

// 监听props变化
watch(() => props.xml, (newXml) => {
  if (newXml && viewer) {
    loadXML(newXml)
  }
})

watch([() => props.highlightedActivities, () => props.highlightedFlows], () => {
  applyHighlight()
}, { deep: true })

onMounted(() => {
  initViewer()
})

onBeforeUnmount(() => {
  if (viewer) {
    viewer.destroy()
  }
})
</script>

<style scoped lang="scss">
.bpmn-viewer {
  width: 100%;
  height: 100%;
  background: #f5f5f5;

  .viewer-container {
    width: 100%;
    height: 100%;
  }

  // 高亮样式
  :deep(.highlight-activity) {
    .djs-visual > :nth-child(1) {
      fill: #52c41a !important;
      fill-opacity: 0.3 !important;
      stroke: #52c41a !important;
      stroke-width: 3px !important;
    }
  }

  :deep(.highlight-flow) {
    .djs-visual > :nth-child(1) {
      stroke: #52c41a !important;
      stroke-width: 3px !important;
      marker-end: url(#sequenceflow-end-white-green) !important;
    }
  }
}
</style>

