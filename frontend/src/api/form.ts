import request from './request'

// 表单定义管理API
export const formApi = {
  // 查询所有表单定义
  getList: () => request.get('/forms/list'),
  
  // 根据ID查询表单定义
  getById: (id: number) => request.get(`/forms/${id}`),
  
  // 根据formKey查询表单定义
  getByKey: (formKey: string) => request.get(`/forms/key/${formKey}`),
  
  // 根据分类查询表单定义
  getByCategory: (categoryId: number) => request.get(`/forms/category/${categoryId}`),
  
  // 根据状态查询表单定义
  getByStatus: (status: number) => request.get(`/forms/status/${status}`),
  
  // 查询表单所有版本
  getVersions: (formKey: string) => request.get(`/forms/versions/${formKey}`),
  
  // 创建表单定义
  create: (data: any) => request.post('/forms', data),
  
  // 更新表单定义
  update: (id: number, data: any) => request.put(`/forms/${id}`, data),
  
  // 删除表单定义
  delete: (id: number) => request.delete(`/forms/${id}`),
  
  // 发布表单（创建新版本）
  publish: (formKey: string) => request.post(`/forms/publish/${formKey}`)
}

// 表单分类管理API
export const formCategoryApi = {
  // 查询分类树
  getTree: () => request.get('/form-categories/tree'),
  
  // 查询所有分类（列表）
  getList: () => request.get('/form-categories/list'),
  
  // 根据ID查询分类
  getById: (id: number) => request.get(`/form-categories/${id}`),
  
  // 创建分类
  create: (data: any) => request.post('/form-categories', data),
  
  // 更新分类
  update: (id: number, data: any) => request.put(`/form-categories/${id}`, data),
  
  // 删除分类
  delete: (id: number) => request.delete(`/form-categories/${id}`)
}

// 表单数据管理API
export const formDataApi = {
  // 保存表单数据
  save: (data: any) => request.post('/form-data', data),
  
  // 根据ID查询表单数据
  getById: (id: number) => request.get(`/form-data/${id}`),
  
  // 根据流程实例ID查询表单数据
  getByProcessInstance: (processInstanceId: string) => 
    request.get(`/form-data/process-instance/${processInstanceId}`),
  
  // 根据任务ID查询表单数据
  getByTask: (taskId: string) => request.get(`/form-data/task/${taskId}`),
  
  // 根据表单ID查询所有数据
  getByFormId: (formId: number) => request.get(`/form-data/form/${formId}`),
  
  // 删除表单数据
  delete: (id: number) => request.delete(`/form-data/${id}`)
}

// 流程表单关联API
export const processFormApi = {
  // 绑定表单到流程
  bind: (data: any) => request.post('/process-forms', data),
  
  // 更新流程表单关联
  update: (id: number, data: any) => request.put(`/process-forms/${id}`, data),
  
  // 解绑表单
  unbind: (id: number) => request.delete(`/process-forms/${id}`),
  
  // 根据ID查询流程表单关联
  getById: (id: number) => request.get(`/process-forms/${id}`),
  
  // 根据流程定义Key查询所有表单关联
  getByProcessKey: (processKey: string) => request.get(`/process-forms/process/${processKey}`),
  
  // 根据流程定义Key和节点ID查询表单关联
  getByProcessKeyAndNode: (processKey: string, nodeId: string) => 
    request.get(`/process-forms/process/${processKey}/node/${nodeId}`),
  
  // 查询流程的全局表单
  getGlobalForm: (processKey: string) => request.get(`/process-forms/process/${processKey}/global`)
}

