import { request } from './request'

/**
 * 登录请求参数
 */
export interface LoginParams {
  username: string
  password: string
}

/**
 * 登录响应数据
 */
export interface LoginResult {
  accessToken: string
  tokenType: string
  expiresIn: number
  userInfo: {
    userId: number
    username: string
    realName: string
    deptId: number
    position: string
  }
}

/**
 * 用户信息
 */
export interface UserInfo {
  username: string
  realName?: string
  roles?: string[]
}

/**
 * 认证相关 API
 */
export const authApi = {
  /**
   * 用户登录
   */
  login(params: LoginParams): Promise<{ data: LoginResult }> {
    return request.post('/auth/login', params)
  },

  /**
   * 用户登出
   */
  logout(): Promise<any> {
    return request.post('/auth/logout')
  },

  /**
   * 获取当前用户信息
   */
  getCurrentUser(): Promise<{ data: UserInfo }> {
    return request.get('/auth/current')
  }
}

