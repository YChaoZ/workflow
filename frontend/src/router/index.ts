import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import Layout from '@/components/Layout/index.vue'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: {
      title: '登录',
      hidden: true
    }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/home/index.vue'),
        meta: {
          title: '首页',
          icon: 'HomeFilled'
        }
      }
    ]
  },
  {
    path: '/process',
    component: Layout,
    redirect: '/process/design',
    meta: {
      title: '流程管理',
      icon: 'Connection'
    },
    children: [
      {
        path: 'design',
        name: 'ProcessDesign',
        component: () => import('@/views/process/design/index.vue'),
        meta: {
          title: '流程设计',
          icon: 'Edit'
        }
      },
      {
        path: 'definition',
        name: 'ProcessDefinition',
        component: () => import('@/views/process/definition/index.vue'),
        meta: {
          title: '流程定义',
          icon: 'Document'
        }
      },
      {
        path: 'instance',
        name: 'ProcessInstance',
        component: () => import('@/views/process/instance/index.vue'),
        meta: {
          title: '流程实例',
          icon: 'Monitor'
        }
      },
      {
        path: 'instance/:id',
        name: 'ProcessInstanceDetail',
        component: () => import('@/views/process/instance/detail.vue'),
        meta: {
          title: '流程实例详情',
          hidden: true
        }
      },
      {
        path: 'start',
        name: 'ProcessStart',
        component: () => import('@/views/process/start/index.vue'),
        meta: {
          title: '启动流程',
          hidden: true
        }
      }
    ]
  },
  {
    path: '/task',
    component: Layout,
    redirect: '/task/todo',
    meta: {
      title: '任务管理',
      icon: 'List'
    },
    children: [
      {
        path: 'todo',
        name: 'TodoTask',
        component: () => import('@/views/task/todo/index.vue'),
        meta: {
          title: '待办任务',
          icon: 'Clock'
        }
      },
      {
        path: 'done',
        name: 'DoneTask',
        component: () => import('@/views/task/done/index.vue'),
        meta: {
          title: '已办任务',
          icon: 'Check'
        }
      },
      {
        path: 'handle',
        name: 'TaskHandle',
        component: () => import('@/views/task/handle/index.vue'),
        meta: {
          title: '办理任务',
          hidden: true
        }
      }
    ]
  },
  {
    path: '/form',
    component: Layout,
    redirect: '/form/list',
    meta: {
      title: '表单管理',
      icon: 'Document'
    },
    children: [
      {
        path: 'list',
        name: 'FormList',
        component: () => import('@/views/form/index.vue'),
        meta: {
          title: '表单列表',
          icon: 'List'
        }
      },
      {
        path: 'designer',
        name: 'FormDesigner',
        component: () => import('@/views/form/designer.vue'),
        meta: {
          title: '表单设计器',
          hidden: true
        }
      }
    ]
  },
  {
    path: '/organization',
    component: Layout,
    redirect: '/organization/department',
    meta: {
      title: '组织管理',
      icon: 'OfficeBuilding'
    },
    children: [
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/organization/user/index.vue'),
        meta: {
          title: '用户管理',
          icon: 'User'
        }
      },
      {
        path: 'department',
        name: 'Department',
        component: () => import('@/views/organization/department/index.vue'),
        meta: {
          title: '部门管理',
          icon: 'OfficeBuilding'
        }
      },
      {
        path: 'role',
        name: 'Role',
        component: () => import('@/views/organization/role/index.vue'),
        meta: {
          title: '角色管理',
          icon: 'UserFilled'
        }
      },
      {
        path: 'permission',
        name: 'Permission',
        component: () => import('@/views/organization/permission/index.vue'),
        meta: {
          title: '权限管理',
          icon: 'Lock'
        }
      }
    ]
  },
  {
    path: '/monitor',
    component: Layout,
    redirect: '/monitor/dashboard',
    meta: {
      title: '流程监控',
      icon: 'DataAnalysis'
    },
    children: [
      {
        path: 'dashboard',
        name: 'MonitorDashboard',
        component: () => import('@/views/monitor/dashboard.vue'),
        meta: {
          title: '监控大屏',
          icon: 'Monitor'
        }
      }
    ]
  },
  {
    path: '/statistics',
    component: Layout,
    redirect: '/statistics/analysis',
    meta: {
      title: '流程统计',
      icon: 'DataLine'
    },
    children: [
      {
        path: 'analysis',
        name: 'StatisticsAnalysis',
        component: () => import('@/views/statistics/analysis.vue'),
        meta: {
          title: '统计分析',
          icon: 'TrendCharts'
        }
      }
    ]
  },
  {
    path: '/config',
    component: Layout,
    redirect: '/config/params',
    meta: {
      title: '系统配置',
      icon: 'Setting'
    },
    children: [
      {
        path: 'params',
        name: 'SystemParams',
        component: () => import('@/views/config/params.vue'),
        meta: {
          title: '参数管理',
          icon: 'Tools'
        }
      },
      {
        path: 'dict',
        name: 'SystemDict',
        component: () => import('@/views/config/dict.vue'),
        meta: {
          title: '字典管理',
          icon: 'Collection'
        }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  if (to.path === '/login') {
    next()
  } else {
    if (token) {
      next()
    } else {
      next('/login')
    }
  }
})

export default router

