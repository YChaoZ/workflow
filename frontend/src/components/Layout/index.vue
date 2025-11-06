<template>
  <div class="layout-container">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside :width="collapse ? '64px' : '200px'" class="layout-aside">
        <div class="logo-container">
          <span v-if="!collapse" class="logo-title">工作流系统</span>
          <span v-else class="logo-title-short">WF</span>
        </div>
        <el-menu
          :default-active="activeMenu"
          :collapse="collapse"
          :unique-opened="true"
          router
        >
          <template v-for="route in routes" :key="route.path">
            <el-sub-menu v-if="route.children && route.children.length > 0" :index="route.path">
              <template #title>
                <el-icon v-if="route.meta?.icon"><component :is="route.meta.icon" /></el-icon>
                <span>{{ route.meta?.title }}</span>
              </template>
              <el-menu-item
                v-for="child in route.children"
                :key="child.path"
                :index="route.path + '/' + child.path"
              >
                <el-icon v-if="child.meta?.icon"><component :is="child.meta.icon" /></el-icon>
                <span>{{ child.meta?.title }}</span>
              </el-menu-item>
            </el-sub-menu>
            <el-menu-item v-else :index="route.path">
              <el-icon v-if="route.meta?.icon"><component :is="route.meta.icon" /></el-icon>
              <span>{{ route.meta?.title }}</span>
            </el-menu-item>
          </template>
        </el-menu>
      </el-aside>

      <!-- 主内容区 -->
      <el-container class="layout-main">
        <!-- 顶部导航栏 -->
        <el-header class="layout-header">
          <div class="header-left">
            <el-icon class="collapse-icon" @click="toggleCollapse">
              <Fold v-if="!collapse" />
              <Expand v-else />
            </el-icon>
          </div>
          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <span class="user-dropdown">
                <el-avatar :size="32" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
                <span class="user-name">{{ userStore.realName || userStore.username }}</span>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                  <el-dropdown-item command="settings">系统设置</el-dropdown-item>
                  <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <!-- 内容区域 -->
        <el-main class="layout-content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/store'
import { useUserStore } from '@/store/modules/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Fold, Expand } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()

const collapse = computed(() => appStore.collapse)
const activeMenu = computed(() => route.path)

// 过滤路由（不显示隐藏的路由）
const routes = computed(() => {
  return router.getRoutes().filter(r => 
    r.meta && !r.meta.hidden && r.path !== '/' && r.path !== '/login'
  )
})

const toggleCollapse = () => {
  appStore.toggleCollapse()
}

const handleCommand = (command: string) => {
  switch (command) {
    case 'profile':
      ElMessage.info('个人信息功能开发中...')
      break
    case 'settings':
      ElMessage.info('系统设置功能开发中...')
      break
    case 'logout':
      ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        userStore.logout()
        router.push('/login')
        ElMessage.success('退出登录成功')
      })
      break
  }
}
</script>

<style scoped lang="scss">
.layout-container {
  width: 100%;
  height: 100%;

  .el-container {
    height: 100%;
  }

  .layout-aside {
    background-color: #304156;
    transition: width 0.3s;
    overflow-x: hidden;

    .logo-container {
      height: 50px;
      line-height: 50px;
      text-align: center;
      color: #fff;
      font-size: 18px;
      font-weight: bold;
      background-color: #2b2f3a;

      .logo-title-short {
        font-size: 20px;
      }
    }

    .el-menu {
      border-right: none;
      background-color: #304156;

      :deep(.el-menu-item) {
        color: #bfcbd9;

        &:hover {
          background-color: #263445 !important;
        }

        &.is-active {
          color: #409eff !important;
          background-color: #001528 !important;
        }
      }

      :deep(.el-sub-menu__title) {
        color: #bfcbd9;

        &:hover {
          background-color: #263445 !important;
        }
      }
    }
  }

  .layout-main {
    background-color: #f0f2f5;

    .layout-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      background-color: #fff;
      box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
      padding: 0 20px;

      .header-left {
        .collapse-icon {
          font-size: 20px;
          cursor: pointer;
          transition: all 0.3s;

          &:hover {
            color: #409eff;
          }
        }
      }

      .header-right {
        .user-dropdown {
          display: flex;
          align-items: center;
          cursor: pointer;

          .user-name {
            margin-left: 10px;
          }
        }
      }
    }

    .layout-content {
      padding: 20px;
      overflow-y: auto;
    }
  }
}
</style>

