import { request } from './request'

/**
 * 任务查询参数
 */
export interface TaskQuery {
  assignee?: string
  candidateUser?: string
  candidateGroup?: string
  processKey?: string
  processInstanceId?: string
  businessKey?: string
  taskName?: string
  taskStatus?: 'todo' | 'done'
  pageNum?: number
  pageSize?: number
}

/**
 * 任务
 */
export interface Task {
  taskId: string
  taskName: string
  taskKey: string
  processInstanceId: string
  processDefinitionId?: string
  processKey?: string
  businessKey?: string
  assignee?: string
  owner?: string
  createTime: string
  endTime?: string
  status: string
  priority?: number
  description?: string
  formKey?: string
  variables?: Record<string, any>
}

/**
 * 完成任务参数
 */
export interface CompleteTaskParams {
  taskId: string
  assignee: string
  comment?: string
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
 * 任务管理相关 API
 */
export const taskApi = {
  /**
   * 查询任务列表
   */
  queryTasks(params: TaskQuery): Promise<{ data: PageResult<Task> }> {
    return request.get('/tasks/list', { params })
  },

  /**
   * 查询单个任务
   */
  getTask(taskId: string): Promise<{ data: Task }> {
    return request.get(`/tasks/${taskId}`)
  },

  /**
   * 完成任务
   */
  completeTask(params: CompleteTaskParams): Promise<any> {
    return request.post('/tasks/complete', params)
  },

  /**
   * 认领任务
   */
  claimTask(taskId: string, userId: string): Promise<any> {
    return request.post(`/tasks/${taskId}/claim`, null, {
      params: { userId }
    })
  },

  /**
   * 委派任务
   */
  delegateTask(taskId: string, delegateUserId: string): Promise<any> {
    return request.post(`/tasks/${taskId}/delegate`, null, {
      params: { delegateUserId }
    })
  },

  /**
   * 转办任务
   */
  transferTask(taskId: string, targetUserId: string): Promise<any> {
    return request.post(`/tasks/${taskId}/transfer`, null, {
      params: { targetUserId }
    })
  }
}

