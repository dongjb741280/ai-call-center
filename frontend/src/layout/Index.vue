<template>
  <div class="layout-container">
    <!-- 头部 -->
    <header class="layout-header">
      <div class="header-left">
        <el-icon class="menu-toggle" @click="toggleSidebar">
          <Fold v-if="!collapsed" />
          <Expand v-else />
        </el-icon>
        <div class="brand">
          <span class="brand-icon">&#9670;</span>
          <h1 class="title">AI 呼叫中心</h1>
        </div>
      </div>
      <div class="header-right">
        <el-dropdown @command="handleCommand">
          <span class="user-info">
            <el-avatar :size="32" :src="userStore.userInfo?.avatar">
              {{ userStore.userInfo?.username?.charAt(0) }}
            </el-avatar>
            <span class="username">{{ userStore.userInfo?.username }}</span>
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人设置</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>

    <!-- 主体 -->
    <div class="layout-main">
      <!-- 侧边栏 -->
      <aside class="layout-sidebar" :class="{ collapsed }">
        <el-menu
          :default-active="activeMenu"
          :collapse="collapsed"
          :unique-opened="true"
          router
          background-color="transparent"
          text-color="#94a3b8"
          active-text-color="#a5b4fc"
        >
          <template v-for="route in menuRoutes" :key="route.path">
            <el-sub-menu v-if="route.children && route.children.length > 0" :index="route.path">
              <template #title>
                <el-icon><component :is="route.meta?.icon" /></el-icon>
                <span>{{ route.meta?.title }}</span>
              </template>
              <el-menu-item
                v-for="child in route.children"
                :key="child.path"
                :index="child.path.startsWith('/') ? child.path : `${route.path.replace(/\/$/, '')}/${child.path}`"
              >
                <el-icon><component :is="child.meta?.icon" /></el-icon>
                <span>{{ child.meta?.title }}</span>
              </el-menu-item>
            </el-sub-menu>
            <el-menu-item v-else :index="route.path || '/'">
              <el-icon><component :is="route.meta?.icon" /></el-icon>
              <span>{{ route.meta?.title }}</span>
            </el-menu-item>
          </template>
        </el-menu>
      </aside>

      <!-- 内容区域 -->
      <main class="layout-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const collapsed = ref(false)

// 当前激活的菜单
const activeMenu = computed(() => route.path)

// 菜单路由（从路由配置读取，getRoutes() 不包含嵌套 children）
const menuRoutes = computed(() => {
  const root = router.options?.routes?.find(r => r.path === '/')
  return root?.children || []
})

// 切换侧边栏
const toggleSidebar = () => {
  collapsed.value = !collapsed.value
}

// 处理用户操作
const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      // 个人设置
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await userStore.userLogout()
        router.push('/login')
      } catch (error) {
        // 用户取消
      }
      break
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

/* ---- Header ---- */
.layout-header {
  height: var(--header-height);
  background: var(--bg-header);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 var(--space-lg);
  z-index: 100;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.menu-toggle {
  font-size: 20px;
  cursor: pointer;
  color: var(--text-regular);
  padding: 6px;
  border-radius: var(--radius-sm);
  transition: all var(--transition-base);
}

.menu-toggle:hover {
  color: var(--color-primary);
  background: var(--color-primary-bg);
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
}

.brand-icon {
  font-size: 20px;
  color: var(--color-primary);
  line-height: 1;
}

.title {
  font-size: var(--font-size-lg);
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: -0.01em;
  margin: 0;
  white-space: nowrap;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: var(--radius-md);
  transition: all var(--transition-base);
}

.user-info:hover {
  background: var(--bg-body);
}

.username {
  font-size: var(--font-size-sm);
  color: var(--text-primary);
  font-weight: 500;
}

/* ---- Body ---- */
.layout-main {
  flex: 1;
  display: flex;
  overflow: hidden;
}

/* ---- Sidebar ---- */
.layout-sidebar {
  width: var(--sidebar-width);
  background: var(--bg-sidebar);
  overflow-y: auto;
  overflow-x: hidden;
  transition: width var(--transition-smooth);
  flex-shrink: 0;
  padding: 8px 0;
}

.layout-sidebar.collapsed {
  width: var(--sidebar-collapsed-width);
}

.layout-sidebar::-webkit-scrollbar {
  width: 3px;
}

.layout-sidebar::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 10px;
}

/* ---- Content ---- */
.layout-content {
  flex: 1;
  background: var(--bg-body);
  overflow-y: auto;
  padding: var(--space-lg);
}

/* ---- Menu overrides ---- */
:deep(.el-menu) {
  border-right: none !important;
  background: transparent !important;
}

:deep(.el-menu--collapse) {
  width: var(--sidebar-collapsed-width);
}

:deep(.el-sub-menu .el-sub-menu__title),
:deep(.el-menu-item) {
  height: 44px !important;
  line-height: 44px !important;
  margin: 2px 8px;
  border-radius: var(--radius-sm);
  font-size: var(--font-size-sm);
}

:deep(.el-sub-menu .el-sub-menu__title:hover),
:deep(.el-menu-item:hover) {
  background: var(--bg-sidebar-hover) !important;
}

:deep(.el-menu-item.is-active) {
  background: var(--bg-sidebar-active) !important;
  color: var(--color-primary-light) !important;
  font-weight: 600;
  border-right: 2px solid var(--color-primary);
}

:deep(.el-sub-menu.is-active > .el-sub-menu__title) {
  color: var(--text-inverse) !important;
  font-weight: 600;
}

:deep(.el-menu .el-icon) {
  font-size: 18px;
}

/* ---- Responsive ---- */
@media (max-width: 768px) {
  .layout-content {
    padding: var(--space-md);
  }
}
</style>


