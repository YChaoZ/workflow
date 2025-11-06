import request from './request'

// 部门管理API
export const departmentApi = {
  // 查询部门树
  getTree: () => request.get('/departments/tree'),
  
  // 查询所有部门（平铺）
  getList: () => request.get('/departments/list'),
  
  // 查询部门详情
  getById: (id: number) => request.get(`/departments/${id}`),
  
  // 创建部门
  create: (data: any) => request.post('/departments', data),
  
  // 更新部门
  update: (id: number, data: any) => request.put(`/departments/${id}`, data),
  
  // 删除部门
  delete: (id: number) => request.delete(`/departments/${id}`),
  
  // 查询子部门
  getChildren: (parentId: number) => request.get(`/departments/children/${parentId}`)
}

// 角色管理API
export const roleApi = {
  // 查询所有角色
  getList: () => request.get('/roles/list'),
  
  // 查询角色详情
  getById: (id: number) => request.get(`/roles/${id}`),
  
  // 根据类型查询角色
  getByType: (type: string) => request.get(`/roles/type/${type}`),
  
  // 创建角色
  create: (data: any) => request.post('/roles', data),
  
  // 更新角色
  update: (id: number, data: any) => request.put(`/roles/${id}`, data),
  
  // 删除角色
  delete: (id: number) => request.delete(`/roles/${id}`),
  
  // 分配权限
  assignPermissions: (roleId: number, permissionIds: number[]) =>
    request.post(`/roles/${roleId}/permissions`, { permissionIds }),
  
  // 查询角色权限
  getPermissions: (roleId: number) => request.get(`/roles/${roleId}/permissions`)
}

// 权限管理API
export const permissionApi = {
  // 查询权限树
  getTree: () => request.get('/permissions/tree'),
  
  // 查询所有权限（平铺）
  getList: () => request.get('/permissions/list'),
  
  // 查询权限详情
  getById: (id: number) => request.get(`/permissions/${id}`),
  
  // 根据类型查询权限
  getByType: (type: string) => request.get(`/permissions/type/${type}`),
  
  // 创建权限
  create: (data: any) => request.post('/permissions', data),
  
  // 更新权限
  update: (id: number, data: any) => request.put(`/permissions/${id}`, data),
  
  // 删除权限
  delete: (id: number) => request.delete(`/permissions/${id}`)
}

// 用户组管理API
export const userGroupApi = {
  // 查询所有用户组
  getList: () => request.get('/user-groups/list'),
  
  // 查询用户组详情
  getById: (id: number) => request.get(`/user-groups/${id}`),
  
  // 创建用户组
  create: (data: any) => request.post('/user-groups', data),
  
  // 更新用户组
  update: (id: number, data: any) => request.put(`/user-groups/${id}`, data),
  
  // 删除用户组
  delete: (id: number) => request.delete(`/user-groups/${id}`),
  
  // 添加成员
  addMembers: (groupId: number, userIds: number[]) =>
    request.post(`/user-groups/${groupId}/members`, { userIds }),
  
  // 移除成员
  removeMembers: (groupId: number, userIds: number[]) =>
    request.delete(`/user-groups/${groupId}/members`, { data: { userIds } }),
  
  // 查询成员
  getMembers: (groupId: number) => request.get(`/user-groups/${groupId}/members`)
}

// 用户管理API
export const userApi = {
  // 查询所有用户
  getList: () => request.get('/users/list'),
  
  // 查询用户详情
  getById: (id: number) => request.get(`/users/${id}`),
  
  // 分配角色
  assignRoles: (userId: number, roleIds: number[]) =>
    request.post(`/users/${userId}/roles`, { roleIds }),
  
  // 查询用户角色
  getRoles: (userId: number) => request.get(`/users/${userId}/roles`)
}

// 统一导出（兼容性）
export const organizationApi = {
  getDepartmentTree: () => departmentApi.getTree(),
  getDepartmentList: () => departmentApi.getList(),
  getRoleList: () => roleApi.getList(),
  getPermissionTree: () => permissionApi.getTree()
}

