import { defineStore } from 'pinia'
import { authApi } from '@/api/auth'
import type { LoginParams, LoginResult } from '@/api/auth'

interface UserState {
  token: string
  userId: number | null
  username: string
  realName: string
  deptId: number | null
  position: string
  roles: string[]
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    token: localStorage.getItem('token') || '',
    userId: null,
    username: localStorage.getItem('username') || '',
    realName: localStorage.getItem('realName') || '',
    deptId: null,
    position: '',
    roles: []
  }),
  
  getters: {
    isLoggedIn: (state) => !!state.token
  },
  
  actions: {
    /**
     * 登录
     */
    async login(loginParams: LoginParams) {
      try {
        const response = await authApi.login(loginParams)
        const { accessToken, userInfo } = response.data
        
        // 保存 token
        this.token = accessToken
        localStorage.setItem('token', accessToken)
        
        // 保存用户信息
        this.userId = userInfo.userId
        this.username = userInfo.username
        this.realName = userInfo.realName
        this.deptId = userInfo.deptId
        this.position = userInfo.position
        
        // 保存到 localStorage 用于页面刷新
        localStorage.setItem('username', userInfo.username)
        localStorage.setItem('realName', userInfo.realName)
        
        return response
      } catch (error) {
        console.error('登录失败:', error)
        throw error
      }
    },
    
    /**
     * 登出
     */
    async logout() {
      try {
        await authApi.logout()
      } catch (error) {
        console.error('登出失败:', error)
      } finally {
        // 清除状态
        this.token = ''
        this.userId = null
        this.username = ''
        this.realName = ''
        this.deptId = null
        this.position = ''
        this.roles = []
        
        // 清除 localStorage
        localStorage.removeItem('token')
        localStorage.removeItem('username')
        localStorage.removeItem('realName')
      }
    },
    
    /**
     * 获取当前用户信息
     */
    async getUserInfo() {
      try {
        const response = await authApi.getCurrentUser()
        this.username = response.data.username
        this.realName = response.data.realName || ''
        this.roles = response.data.roles || []
        return response.data
      } catch (error) {
        console.error('获取用户信息失败:', error)
        throw error
      }
    },
    
    /**
     * 设置 Token
     */
    setToken(token: string) {
      this.token = token
      localStorage.setItem('token', token)
    },
    
    /**
     * 设置用户信息
     */
    setUserInfo(userInfo: Partial<UserState>) {
      if (userInfo.userId !== undefined) this.userId = userInfo.userId
      if (userInfo.username) this.username = userInfo.username
      if (userInfo.realName) this.realName = userInfo.realName
      if (userInfo.deptId !== undefined) this.deptId = userInfo.deptId
      if (userInfo.position) this.position = userInfo.position
      if (userInfo.roles) this.roles = userInfo.roles
    }
  }
})

