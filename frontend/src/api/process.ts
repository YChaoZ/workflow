import { request } from './request'

/**
 * 启动流程请求参数
 */
export interface StartProcessParams {
  processKey: string
  businessKey?: string
  startUser: string
  title?: string
  variables?: Record<string, any>
}

/**
 * 流程实例查询参数
 */
export interface ProcessInstanceQuery {
  processKey?: string
  businessKey?: string
  startUser?: string
  state?: 'active' | 'suspended'
  pageNum?: number
  pageSize?: number
}

/**
 * 流程实例
 */
export interface ProcessInstance {
  instanceId: string
  processDefinitionId: string
  processKey: string
  businessKey?: string
  processName?: string
  status: string
  startTime: string
  startUser?: string
  variables?: Record<string, any>
}

/**
 * 分页结果
 */
export interface PageResult<T> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
  totalPages: number
}

/**
 * 流程管理相关 API
 */
export const processApi = {
  /**
   * 启动流程
   */
  startProcess(params: StartProcessParams): Promise<{ data: string }> {
    return request.post('/process/start', params)
  },

  /**
   * 查询流程实例列表
   */
  queryProcessInstances(params: ProcessInstanceQuery): Promise<{ data: PageResult<ProcessInstance> }> {
    return request.get('/process/instances', { params })
  },

  /**
   * 查询单个流程实例
   */
  getProcessInstance(instanceId: string): Promise<{ data: ProcessInstance }> {
    return request.get(`/process/instance/${instanceId}`)
  },

  /**
   * 挂起流程实例
   */
  suspendProcess(instanceId: string): Promise<any> {
    return request.post(`/process/instance/${instanceId}/suspend`)
  },

  /**
   * 激活流程实例
   */
  activateProcess(instanceId: string): Promise<any> {
    return request.post(`/process/instance/${instanceId}/activate`)
  },

  /**
   * 删除流程实例
   */
  deleteProcess(instanceId: string, reason?: string): Promise<any> {
    return request.delete(`/process/instance/${instanceId}`, {
      params: { reason }
    })
  }
}

