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

