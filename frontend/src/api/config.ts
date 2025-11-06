/**
 * 系统配置API
 */
import request from './request'

// ==================== 类型定义 ====================

export interface SystemParam {
  id?: number
  paramKey: string
  paramValue: string
  paramName: string
  paramType: 'STRING' | 'NUMBER' | 'BOOLEAN' | 'JSON'
  paramGroup?: string
  description?: string
  isSystem?: boolean
  sortOrder?: number
  createdBy?: string
  createdTime?: string
  updatedBy?: string
  updatedTime?: string
  remark?: string
}

export interface DictType {
  id?: number
  dictCode: string
  dictName: string
  description?: string
  isSystem?: boolean
  status?: boolean
  sortOrder?: number
  createdBy?: string
  createdTime?: string
  updatedBy?: string
  updatedTime?: string
  remark?: string
  dictDataList?: DictData[]
}

export interface DictData {
  id?: number
  dictTypeId?: number
  dictCode: string
  dictLabel: string
  dictValue: string
  tagType?: 'primary' | 'success' | 'info' | 'warning' | 'danger'
  cssClass?: string
  isDefault?: boolean
  status?: boolean
  sortOrder?: number
  createdBy?: string
  createdTime?: string
  updatedBy?: string
  updatedTime?: string
  remark?: string
}

// ==================== 系统参数API ====================

/**
 * 查询参数列表
 */
export function listParams(paramGroup?: string) {
  return request.get<SystemParam[]>('/config/params', {
    params: { paramGroup }
  })
}

/**
 * 根据ID查询参数
 */
export function getParamById(id: number) {
  return request.get<SystemParam>(`/config/params/${id}`)
}

/**
 * 根据参数键查询
 */
export function getParamByKey(key: string) {
  return request.get<SystemParam>(`/config/params/key/${key}`)
}

/**
 * 创建参数
 */
export function createParam(data: SystemParam) {
  return request.post('/config/params', data)
}

/**
 * 更新参数
 */
export function updateParam(id: number, data: SystemParam) {
  return request.put(`/config/params/${id}`, data)
}

/**
 * 删除参数
 */
export function deleteParam(id: number) {
  return request.delete(`/config/params/${id}`)
}

/**
 * 刷新参数缓存
 */
export function refreshParamCache() {
  return request.post('/config/params/refresh-cache')
}

// ==================== 数据字典API ====================

/**
 * 查询所有字典类型
 */
export function listDictTypes() {
  return request.get<DictType[]>('/config/dict/types')
}

/**
 * 根据ID查询字典类型
 */
export function getDictTypeById(id: number) {
  return request.get<DictType>(`/config/dict/types/${id}`)
}

/**
 * 创建字典类型
 */
export function createDictType(data: DictType) {
  return request.post('/config/dict/types', data)
}

/**
 * 更新字典类型
 */
export function updateDictType(id: number, data: DictType) {
  return request.put(`/config/dict/types/${id}`, data)
}

/**
 * 删除字典类型
 */
export function deleteDictType(id: number) {
  return request.delete(`/config/dict/types/${id}`)
}

/**
 * 根据字典类型编码查询字典数据
 */
export function listDictDataByTypeCode(typeCode: string) {
  return request.get<DictData[]>(`/config/dict/data/type/${typeCode}`)
}

/**
 * 根据ID查询字典数据
 */
export function getDictDataById(id: number) {
  return request.get<DictData>(`/config/dict/data/${id}`)
}

/**
 * 创建字典数据
 */
export function createDictData(data: DictData) {
  return request.post('/config/dict/data', data)
}

/**
 * 更新字典数据
 */
export function updateDictData(id: number, data: DictData) {
  return request.put(`/config/dict/data/${id}`, data)
}

/**
 * 删除字典数据
 */
export function deleteDictData(id: number) {
  return request.delete(`/config/dict/data/${id}`)
}

/**
 * 刷新字典缓存
 */
export function refreshDictCache() {
  return request.post('/config/dict/refresh-cache')
}

