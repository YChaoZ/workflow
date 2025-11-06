import { ElMessage, ElNotification } from 'element-plus'

/**
 * 统一错误处理
 */
export class ErrorHandler {
  /**
   * 处理API错误
   */
  static handleApiError(error: any, defaultMessage: string = '操作失败'): void {
    console.error('API Error:', error)
    
    if (error.response) {
      // 服务器返回的错误
      const status = error.response.status
      const message = error.response.data?.message || defaultMessage
      
      switch (status) {
        case 400:
          ElMessage.error(`请求参数错误: ${message}`)
          break
        case 401:
          ElMessage.error('未授权，请重新登录')
          // 可以在这里跳转到登录页
          break
        case 403:
          ElMessage.error('没有权限执行此操作')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error(`服务器错误: ${message}`)
          break
        default:
          ElMessage.error(message)
      }
    } else if (error.request) {
      // 请求已发送但没有收到响应
      ElMessage.error('网络连接失败，请检查网络设置')
    } else {
      // 其他错误
      ElMessage.error(error.message || defaultMessage)
    }
  }

  /**
   * 处理业务错误
   */
  static handleBusinessError(message: string, duration: number = 3000): void {
    ElMessage.warning({
      message,
      duration,
      showClose: true
    })
  }

  /**
   * 显示成功消息
   */
  static showSuccess(message: string, duration: number = 3000): void {
    ElMessage.success({
      message,
      duration,
      showClose: true
    })
  }

  /**
   * 显示信息消息
   */
  static showInfo(message: string, duration: number = 3000): void {
    ElMessage.info({
      message,
      duration,
      showClose: true
    })
  }

  /**
   * 显示通知
   */
  static showNotification(title: string, message: string, type: 'success' | 'warning' | 'info' | 'error' = 'info'): void {
    ElNotification({
      title,
      message,
      type,
      duration: 4500,
      position: 'top-right'
    })
  }

  /**
   * 显示加载中消息
   */
  static showLoading(message: string = '加载中...'): void {
    ElMessage({
      message,
      type: 'info',
      duration: 0, // 不自动关闭
      showClose: false
    })
  }
}

/**
 * 表单验证错误处理
 */
export class FormErrorHandler {
  /**
   * 处理表单验证错误
   */
  static handleValidationError(errors: any): void {
    const firstError = Object.values(errors)[0] as any
    if (firstError && firstError[0]) {
      ElMessage.warning(firstError[0].message || '表单验证失败')
    } else {
      ElMessage.warning('请检查表单填写是否正确')
    }
  }

  /**
   * 检查必填字段
   */
  static checkRequiredFields(data: any, requiredFields: string[]): { valid: boolean; message: string } {
    for (const field of requiredFields) {
      if (!data[field] || data[field] === '') {
        return {
          valid: false,
          message: `请填写 ${field}`
        }
      }
    }
    return { valid: true, message: '' }
  }
}

/**
 * 文件上传错误处理
 */
export class FileErrorHandler {
  /**
   * 检查文件大小
   */
  static checkFileSize(file: File, maxSize: number): { valid: boolean; message: string } {
    const sizeMB = file.size / 1024 / 1024
    if (sizeMB > maxSize) {
      return {
        valid: false,
        message: `文件大小不能超过 ${maxSize}MB，当前文件大小为 ${sizeMB.toFixed(2)}MB`
      }
    }
    return { valid: true, message: '' }
  }

  /**
   * 检查文件类型
   */
  static checkFileType(file: File, acceptTypes: string[]): { valid: boolean; message: string } {
    if (acceptTypes.length === 0 || acceptTypes.includes('*')) {
      return { valid: true, message: '' }
    }

    const fileType = file.name.split('.').pop()?.toLowerCase() || ''
    const isValid = acceptTypes.some(type => 
      type.toLowerCase().includes(fileType) || type === '*'
    )

    if (!isValid) {
      return {
        valid: false,
        message: `只支持 ${acceptTypes.join(', ')} 类型的文件`
      }
    }
    return { valid: true, message: '' }
  }
}

