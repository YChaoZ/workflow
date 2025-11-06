import request from './request'

// 流程定义相关API

/**
 * 查询流程定义列表
 */
export interface ProcessDefinitionQuery {
  key?: string
  name?: string
  category?: string
  page?: number
  size?: number
}

export interface ProcessDefinition {
  id: string
  key: string
  name: string
  version: number
  category?: string
  description?: string
  deploymentId: string
  deploymentTime?: string
  suspended: boolean
  tenantId?: string
}

export interface ProcessDefinitionListResponse {
  total: number
  list: ProcessDefinition[]
}

/**
 * 获取流程定义列表
 */
export function getProcessDefinitionList(params: ProcessDefinitionQuery) {
  return request<ProcessDefinitionListResponse>({
    url: '/process/definition/list',
    method: 'get',
    params
  })
}

/**
 * 部署流程定义
 */
export interface DeployProcessRequest {
  name: string
  category?: string
  tenantId?: string
  xml: string
}

export function deployProcess(data: DeployProcessRequest) {
  return request<ProcessDefinition>({
    url: '/process/definitions/deploy',
    method: 'post',
    data
  })
}

/**
 * 删除流程定义
 */
export function deleteProcessDefinition(id: string, cascade: boolean = false) {
  return request({
    url: `/process/definitions/${id}`,
    method: 'delete',
    params: { cascade }
  })
}

/**
 * 挂起流程定义
 */
export function suspendProcessDefinition(id: string) {
  return request({
    url: `/process/definitions/${id}/suspend`,
    method: 'put'
  })
}

/**
 * 激活流程定义
 */
export function activateProcessDefinition(id: string) {
  return request({
    url: `/process/definitions/${id}/activate`,
    method: 'put'
  })
}

/**
 * 获取流程定义XML
 */
export function getProcessDefinitionXml(id: string) {
  return request<{ xml: string }>({
    url: `/process/definitions/${id}/xml`,
    method: 'get'
  })
}

/**
 * 获取流程定义图片
 */
export function getProcessDefinitionImage(id: string) {
  return request<Blob>({
    url: `/process/definitions/${id}/diagram`,
    method: 'get',
    responseType: 'blob'
  })
}

/**
 * 获取流程定义详情
 */
export function getProcessDefinitionDetail(id: string) {
  return request<ProcessDefinition>({
    url: `/process/definitions/${id}`,
    method: 'get'
  })
}

/**
 * 根据ID获取流程定义（兼容旧API）
 */
export function getProcessDefinitionById(id: string) {
  return getProcessDefinitionDetail(id)
}

