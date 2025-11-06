import type { Directive, DirectiveBinding } from 'vue'
import { useUserStore } from '@/store/modules/user'

/**
 * 权限指令
 * 用法：v-permission="['permission:create', 'permission:update']"
 * 或者：v-permission="'permission:delete'"
 */
export const permission: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    const { value } = binding
    const userStore = useUserStore()
    const permissions = userStore.permissions || []

    if (value && value instanceof Array && value.length > 0) {
      const permissionRoles = value
      const hasPermission = permissions.some((permission: string) => {
        return permissionRoles.includes(permission)
      })

      if (!hasPermission) {
        el.style.display = 'none'
        // 或者直接移除元素
        // el.parentNode && el.parentNode.removeChild(el)
      }
    } else if (value && typeof value === 'string') {
      const hasPermission = permissions.includes(value)
      
      if (!hasPermission) {
        el.style.display = 'none'
      }
    } else {
      throw new Error('权限指令需要传入权限编码！如 v-permission="[\'permission:create\']"')
    }
  }
}

/**
 * 角色指令
 * 用法：v-role="['admin', 'manager']"
 */
export const role: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    const { value } = binding
    const userStore = useUserStore()
    const roles = userStore.roles || []

    if (value && value instanceof Array && value.length > 0) {
      const permissionRoles = value
      const hasRole = roles.some((role: string) => {
        return permissionRoles.includes(role)
      })

      if (!hasRole) {
        el.style.display = 'none'
      }
    } else if (value && typeof value === 'string') {
      const hasRole = roles.includes(value)
      
      if (!hasRole) {
        el.style.display = 'none'
      }
    }
  }
}

export default {
  permission,
  role
}

