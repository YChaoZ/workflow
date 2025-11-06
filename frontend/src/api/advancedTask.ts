import request from './request'

/**
 * 加签请求
 */
export interface AddSignRequest {
  assignees: string[]
  type: 'AND' | 'OR' // AND-会签, OR-或签
}

/**
 * 转办请求
 */
export interface TransferRequest {
  targetUser: string
  comment?: string
}

/**
 * 委派请求
 */
export interface DelegateRequest {
  targetUser: string
}

/**
 * 回退请求
 */
export interface RejectRequest {
  comment: string
}

/**
 * 回退到指定节点请求
 */
export interface RejectToNodeRequest {
  targetNodeId: string
  comment: string
}

/**
 * 撤回请求
 */
export interface WithdrawRequest {
  reason: string
}

/**
 * 可回退节点
 */
export interface RejectableNode {
  activityId: string
  activityName: string
  assignee: string
  startTime: string
  endTime: string
}

/**
 * 高级任务操作API
 */
export const advancedTaskApi = {
  /**
   * 任务加签
   */
  addSign(taskId: string, data: AddSignRequest) {
    return request.post(`/tasks/advanced/${taskId}/add-sign`, data)
  },

  /**
   * 任务转办
   */
  transfer(taskId: string, data: TransferRequest) {
    return request.post(`/tasks/advanced/${taskId}/transfer`, data)
  },

  /**
   * 任务委派
   */
  delegate(taskId: string, data: DelegateRequest) {
    return request.post(`/tasks/advanced/${taskId}/delegate`, data)
  },

  /**
   * 回退到上一节点
   */
  rejectToPrevious(taskId: string, data: RejectRequest) {
    return request.post(`/tasks/advanced/${taskId}/reject/previous`, data)
  },

  /**
   * 回退到指定节点
   */
  rejectToNode(taskId: string, data: RejectToNodeRequest) {
    return request.post(`/tasks/advanced/${taskId}/reject/node`, data)
  },

  /**
   * 回退到流程发起人
   */
  rejectToStart(taskId: string, data: RejectRequest) {
    return request.post(`/tasks/advanced/${taskId}/reject/start`, data)
  },

  /**
   * 撤回流程
   */
  withdrawProcess(processInstanceId: string, data: WithdrawRequest) {
    return request.post(`/tasks/advanced/process/${processInstanceId}/withdraw`, data)
  },

  /**
   * 撤回任务
   */
  withdrawTask(taskId: string, data: WithdrawRequest) {
    return request.post(`/tasks/advanced/${taskId}/withdraw`, data)
  },

  /**
   * 获取可回退的节点列表
   */
  getRejectableNodes(taskId: string) {
    return request.get<RejectableNode[]>(`/tasks/advanced/${taskId}/rejectable-nodes`)
  },

  /**
   * 获取历史审批人
   */
  getHistoricAssignees(taskId: string) {
    return request.get<string[]>(`/tasks/advanced/${taskId}/historic-assignees`)
  }
}

