<template>
  <div class="designer-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>表单设计器 - {{ formName }}</span>
          <div>
            <el-button @click="handlePreview">预览</el-button>
            <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
            <el-button @click="handleBack">返回</el-button>
          </div>
        </div>
      </template>

      <div class="designer-content">
        <el-row :gutter="20">
          <!-- 组件库 -->
          <el-col :span="6">
            <el-card shadow="never" class="component-panel">
              <template #header>
                <span>组件库</span>
              </template>
              <div class="component-list">
                <div
                  v-for="component in componentList"
                  :key="component.type"
                  class="component-item"
                  draggable="true"
                  @dragstart="handleDragStart($event, component)"
                >
                  <el-icon><component :is="component.icon" /></el-icon>
                  <span>{{ component.label }}</span>
                </div>
              </div>
            </el-card>
          </el-col>

          <!-- 画布区域 -->
          <el-col :span="12">
            <el-card shadow="never" class="canvas-panel">
              <template #header>
                <span>表单画布</span>
              </template>
              <div
                class="canvas-area"
                @drop="handleDrop"
                @dragover.prevent
              >
                <el-empty v-if="formFields.length === 0" description="拖拽左侧组件到这里" />
                
                <div
                  v-for="(field, index) in formFields"
                  :key="field.id"
                  class="field-item"
                  :class="{ 'field-item-active': selectedField === field }"
                  @click="selectField(field)"
                >
                  <div class="field-content">
                    <el-form-item :label="field.label">
                      <component
                        :is="getFieldComponent(field.type)"
                        v-model="field.value"
                        :placeholder="field.placeholder"
                        disabled
                      />
                    </el-form-item>
                  </div>
                  <div class="field-actions">
                    <el-button link type="primary" size="small" @click.stop="moveUp(index)">
                      <el-icon><Top /></el-icon>
                    </el-button>
                    <el-button link type="primary" size="small" @click.stop="moveDown(index)">
                      <el-icon><Bottom /></el-icon>
                    </el-button>
                    <el-button link type="danger" size="small" @click.stop="removeField(index)">
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </div>
                </div>
              </div>
            </el-card>
          </el-col>

          <!-- 属性配置 -->
          <el-col :span="6">
            <el-card shadow="never" class="property-panel">
              <template #header>
                <span>属性配置</span>
              </template>
              <div v-if="selectedField" class="property-form">
                <el-form label-width="80px" size="small">
                  <el-form-item label="字段名">
                    <el-input v-model="selectedField.field" placeholder="字段名" />
                  </el-form-item>
                  <el-form-item label="标题">
                    <el-input v-model="selectedField.label" placeholder="标题" />
                  </el-form-item>
                  <el-form-item label="占位符">
                    <el-input v-model="selectedField.placeholder" placeholder="占位符" />
                  </el-form-item>
                  <el-form-item label="必填">
                    <el-switch v-model="selectedField.required" />
                  </el-form-item>
                  <el-form-item label="默认值" v-if="selectedField.type !== 'date'">
                    <el-input v-model="selectedField.value" placeholder="默认值" />
                  </el-form-item>
                  
                  <!-- 数字输入特有属性 -->
                  <template v-if="selectedField.type === 'number'">
                    <el-form-item label="最小值">
                      <el-input-number v-model="selectedField.min" :controls="false" style="width: 100%" />
                    </el-form-item>
                    <el-form-item label="最大值">
                      <el-input-number v-model="selectedField.max" :controls="false" style="width: 100%" />
                    </el-form-item>
                    <el-form-item label="步长">
                      <el-input-number v-model="selectedField.step" :controls="false" style="width: 100%" />
                    </el-form-item>
                  </template>
                  
                  <!-- 选择器/单选/复选特有属性 -->
                  <template v-if="['select', 'radio', 'checkbox'].includes(selectedField.type)">
                    <el-form-item label="选项">
                      <el-button size="small" @click="addOption">添加选项</el-button>
                      <div v-for="(option, idx) in selectedField.options" :key="idx" style="margin-top: 8px">
                        <el-input
                          v-model="option.label"
                          placeholder="选项文本"
                          size="small"
                          style="width: 45%; margin-right: 5px"
                        />
                        <el-input
                          v-model="option.value"
                          placeholder="选项值"
                          size="small"
                          style="width: 40%; margin-right: 5px"
                        />
                        <el-button link type="danger" size="small" @click="removeOption(idx)">
                          <el-icon><Delete /></el-icon>
                        </el-button>
                      </div>
                    </el-form-item>
                  </template>
                  
                  <!-- 文件上传特有属性 -->
                  <template v-if="selectedField.type === 'file'">
                    <el-form-item label="接受类型">
                      <el-input v-model="selectedField.accept" placeholder="如: .pdf,.doc" />
                    </el-form-item>
                    <el-form-item label="多文件">
                      <el-switch v-model="selectedField.multiple" />
                    </el-form-item>
                    <el-form-item label="大小限制(MB)">
                      <el-input-number v-model="selectedField.maxSize" :min="1" :max="100" style="width: 100%" />
                    </el-form-item>
                  </template>
                </el-form>
              </div>
              <el-empty v-else description="请选择一个字段" :image-size="80" />
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-card>

    <!-- 预览对话框 -->
    <el-dialog v-model="previewVisible" title="表单预览" width="600px">
      <el-form :model="previewData" label-width="120px">
        <el-form-item
          v-for="field in formFields"
          :key="field.id"
          :label="field.label"
          :required="field.required"
        >
          <component
            :is="getFieldComponent(field.type)"
            v-model="previewData[field.field]"
            :placeholder="field.placeholder"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="previewVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Edit, Calendar, Select, List, Document, Top, Bottom, Delete, NumberSign
} from '@element-plus/icons-vue'
import { formApi } from '@/api/form'

const route = useRoute()
const router = useRouter()

// 数据
const formId = ref<number>()
const formName = ref('新表单')
const formFields = ref<any[]>([])
const selectedField = ref<any>(null)
const previewVisible = ref(false)
const previewData = reactive<any>({})
const saving = ref(false)
let fieldIdCounter = 1

// 组件库
const componentList = [
  { type: 'input', label: '单行文本', icon: Edit },
  { type: 'textarea', label: '多行文本', icon: Document },
  { type: 'number', label: '数字', icon: NumberSign },
  { type: 'date', label: '日期', icon: Calendar },
  { type: 'select', label: '下拉选择', icon: Select },
  { type: 'radio', label: '单选框', icon: Select },
  { type: 'checkbox', label: '复选框', icon: List },
  { type: 'file', label: '文件上传', icon: Document }
]

// 获取字段组件
const getFieldComponent = (type: string) => {
  const map: any = {
    input: 'el-input',
    textarea: 'el-input',
    number: 'el-input-number',
    date: 'el-date-picker',
    select: 'el-select'
  }
  return map[type] || 'el-input'
}

// 拖拽开始
const handleDragStart = (event: DragEvent, component: any) => {
  event.dataTransfer!.effectAllowed = 'copy'
  event.dataTransfer!.setData('component', JSON.stringify(component))
}

// 拖拽放置
const handleDrop = (event: DragEvent) => {
  event.preventDefault()
  const componentData = JSON.parse(event.dataTransfer!.getData('component'))
  
  const newField: any = {
    id: `field_${fieldIdCounter++}`,
    type: componentData.type,
    field: `field_${fieldIdCounter}`,
    label: componentData.label,
    placeholder: `请输入${componentData.label}`,
    required: false,
    value: ''
  }
  
  // 特定类型的默认属性
  if (componentData.type === 'number') {
    newField.min = 0
    newField.max = 100
    newField.step = 1
  } else if (componentData.type === 'select') {
    newField.options = [
      { label: '选项1', value: 'option1' },
      { label: '选项2', value: 'option2' }
    ]
  } else if (componentData.type === 'textarea') {
    newField.rows = 4
  } else if (componentData.type === 'radio') {
    newField.options = [
      { label: '选项1', value: 'option1' },
      { label: '选项2', value: 'option2' }
    ]
  } else if (componentData.type === 'checkbox') {
    newField.options = [
      { label: '选项1', value: 'option1' },
      { label: '选项2', value: 'option2' }
    ]
    newField.value = []
  } else if (componentData.type === 'file') {
    newField.accept = '*'
    newField.multiple = false
    newField.maxSize = 10 // MB
  }
  
  formFields.value.push(newField)
  selectedField.value = newField
}

// 选择字段
const selectField = (field: any) => {
  selectedField.value = field
}

// 移除字段
const removeField = (index: number) => {
  if (formFields.value[index] === selectedField.value) {
    selectedField.value = null
  }
  formFields.value.splice(index, 1)
}

// 上移字段
const moveUp = (index: number) => {
  if (index > 0) {
    const temp = formFields.value[index]
    formFields.value[index] = formFields.value[index - 1]
    formFields.value[index - 1] = temp
  }
}

// 下移字段
const moveDown = (index: number) => {
  if (index < formFields.value.length - 1) {
    const temp = formFields.value[index]
    formFields.value[index] = formFields.value[index + 1]
    formFields.value[index + 1] = temp
  }
}

// 添加选项
const addOption = () => {
  if (!selectedField.value.options) {
    selectedField.value.options = []
  }
  selectedField.value.options.push({
    label: `选项${selectedField.value.options.length + 1}`,
    value: `option${selectedField.value.options.length + 1}`
  })
}

// 移除选项
const removeOption = (index: number) => {
  selectedField.value.options.splice(index, 1)
}

// 预览
const handlePreview = () => {
  // 初始化预览数据
  formFields.value.forEach(field => {
    previewData[field.field] = field.value
  })
  previewVisible.value = true
}

// 保存
const handleSave = async () => {
  if (!formId.value) {
    ElMessage.warning('表单ID不存在')
    return
  }
  
  saving.value = true
  try {
    // 构建formConfig
    const formConfig = {
      fields: formFields.value.map(field => ({
        type: field.type,
        field: field.field,
        label: field.label,
        placeholder: field.placeholder,
        required: field.required,
        value: field.value,
        min: field.min,
        max: field.max,
        step: field.step,
        rows: field.rows,
        options: field.options
      }))
    }
    
    const data = {
      formConfig: JSON.stringify(formConfig),
      updateBy: 'admin'
    }
    
    const res = await formApi.update(formId.value, data)
    if (res.code === 200) {
      ElMessage.success('保存成功')
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (error: any) {
    console.error('保存表单失败:', error)
    ElMessage.error(error.response?.data?.message || '保存失败')
  } finally {
    saving.value = false
  }
}

// 返回
const handleBack = () => {
  router.back()
}

// 加载表单数据
const loadFormData = async () => {
  if (!formId.value) return
  
  try {
    const res = await formApi.getById(formId.value)
    if (res.code === 200) {
      const data = res.data
      formName.value = data.formName
      
      // 解析formConfig
      if (data.formConfig) {
        const config = JSON.parse(data.formConfig)
        if (config.fields && Array.isArray(config.fields)) {
          formFields.value = config.fields.map((field: any, index: number) => ({
            id: `field_${index + 1}`,
            ...field
          }))
          fieldIdCounter = formFields.value.length + 1
        }
      }
    }
  } catch (error) {
    console.error('加载表单数据失败:', error)
    ElMessage.error('加载表单数据失败')
  }
}

// 初始化
onMounted(() => {
  const id = route.query.id
  if (id) {
    formId.value = Number(id)
    loadFormData()
  }
})
</script>

<style scoped lang="scss">
.designer-container {
  padding: 20px;
  height: calc(100vh - 100px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.designer-content {
  height: calc(100vh - 240px);
}

.component-panel,
.canvas-panel,
.property-panel {
  height: 100%;
  
  :deep(.el-card__body) {
    height: calc(100% - 56px);
    overflow-y: auto;
  }
}

.component-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.component-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 15px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: move;
  transition: all 0.3s;
  
  &:hover {
    border-color: #409eff;
    background-color: #ecf5ff;
  }
  
  .el-icon {
    font-size: 24px;
    margin-bottom: 5px;
    color: #409eff;
  }
  
  span {
    font-size: 12px;
    color: #606266;
  }
}

.canvas-area {
  min-height: 400px;
  border: 2px dashed #dcdfe6;
  border-radius: 4px;
  padding: 20px;
}

.field-item {
  position: relative;
  margin-bottom: 15px;
  padding: 15px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  
  &:hover {
    border-color: #409eff;
    box-shadow: 0 2px 12px 0 rgba(64, 158, 255, 0.3);
  }
  
  &.field-item-active {
    border-color: #409eff;
    background-color: #ecf5ff;
  }
  
  .field-actions {
    position: absolute;
    top: 50%;
    right: 10px;
    transform: translateY(-50%);
    display: flex;
    gap: 5px;
  }
}

.property-form {
  padding: 10px 0;
}
</style>

