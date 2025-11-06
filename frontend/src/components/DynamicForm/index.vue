<template>
  <div class="dynamic-form">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      v-loading="loading"
    >
      <el-form-item
        v-for="field in fields"
        :key="field.field"
        :label="field.label"
        :prop="field.field"
      >
        <!-- 单行文本 -->
        <el-input
          v-if="field.type === 'input'"
          v-model="formData[field.field]"
          :placeholder="field.placeholder"
          :disabled="readonly"
        />
        
        <!-- 多行文本 -->
        <el-input
          v-else-if="field.type === 'textarea'"
          v-model="formData[field.field]"
          type="textarea"
          :rows="field.rows || 4"
          :placeholder="field.placeholder"
          :disabled="readonly"
        />
        
        <!-- 数字输入 -->
        <el-input-number
          v-else-if="field.type === 'number'"
          v-model="formData[field.field]"
          :min="field.min"
          :max="field.max"
          :step="field.step || 1"
          :disabled="readonly"
          style="width: 100%"
        />
        
        <!-- 日期选择 -->
        <el-date-picker
          v-else-if="field.type === 'date'"
          v-model="formData[field.field]"
          type="date"
          :placeholder="field.placeholder"
          :disabled="readonly"
          style="width: 100%"
        />
        
        <!-- 下拉选择 -->
        <el-select
          v-else-if="field.type === 'select'"
          v-model="formData[field.field]"
          :placeholder="field.placeholder"
          :disabled="readonly"
          style="width: 100%"
        >
          <el-option
            v-for="option in field.options"
            :key="option.value"
            :label="option.label"
            :value="option.value"
          />
        </el-select>
        
        <!-- 单选框 -->
        <el-radio-group
          v-else-if="field.type === 'radio'"
          v-model="formData[field.field]"
          :disabled="readonly"
        >
          <el-radio
            v-for="option in field.options"
            :key="option.value"
            :label="option.value"
          >
            {{ option.label }}
          </el-radio>
        </el-radio-group>
        
        <!-- 复选框 -->
        <el-checkbox-group
          v-else-if="field.type === 'checkbox'"
          v-model="formData[field.field]"
          :disabled="readonly"
        >
          <el-checkbox
            v-for="option in field.options"
            :key="option.value"
            :label="option.value"
          >
            {{ option.label }}
          </el-checkbox>
        </el-checkbox-group>
        
        <!-- 文件上传 -->
        <el-upload
          v-else-if="field.type === 'file'"
          :disabled="readonly"
          :accept="field.accept"
          :multiple="field.multiple"
          :limit="field.multiple ? 10 : 1"
          action="#"
          :auto-upload="false"
        >
          <el-button size="small" type="primary" :disabled="readonly">点击上传</el-button>
          <template #tip>
            <div class="el-upload__tip">
              文件大小不超过{{ field.maxSize }}MB
            </div>
          </template>
        </el-upload>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted } from 'vue'
import { FormInstance } from 'element-plus'

interface Props {
  formConfig: string  // 表单配置JSON
  modelValue?: any    // 表单数据
  readonly?: boolean  // 只读模式
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: () => ({}),
  readonly: false
})

const emit = defineEmits(['update:modelValue', 'submit'])

const loading = ref(false)
const formRef = ref<FormInstance>()
const fields = ref<any[]>([])
const formData = reactive<any>({})
const formRules = reactive<any>({})

// 解析表单配置
const parseFormConfig = () => {
  try {
    const config = JSON.parse(props.formConfig)
    if (config.fields && Array.isArray(config.fields)) {
      fields.value = config.fields
      
      // 初始化表单数据和验证规则
      fields.value.forEach(field => {
        // 设置默认值
        if (props.modelValue && props.modelValue[field.field] !== undefined) {
          formData[field.field] = props.modelValue[field.field]
        } else {
          formData[field.field] = field.value || ''
        }
        
        // 设置验证规则
        if (field.required) {
          formRules[field.field] = [
            { required: true, message: `请输入${field.label}`, trigger: 'blur' }
          ]
        }
      })
    }
  } catch (error) {
    console.error('解析表单配置失败:', error)
  }
}

// 监听formData变化，向外emit
watch(() => formData, (newVal) => {
  emit('update:modelValue', { ...newVal })
}, { deep: true })

// 验证表单
const validate = async (): Promise<boolean> => {
  if (!formRef.value) return false
  try {
    await formRef.value.validate()
    return true
  } catch {
    return false
  }
}

// 获取表单数据
const getFormData = () => {
  return { ...formData }
}

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields()
}

// 暴露方法给父组件
defineExpose({
  validate,
  getFormData,
  resetForm
})

// 初始化
onMounted(() => {
  parseFormConfig()
})

// 监听配置变化
watch(() => props.formConfig, () => {
  parseFormConfig()
}, { immediate: false })
</script>

<style scoped lang="scss">
.dynamic-form {
  padding: 20px 0;
}
</style>

