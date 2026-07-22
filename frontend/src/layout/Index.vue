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
          <img src="/logo.svg" class="brand-logo" alt="AI呼叫中心" />
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

    <!-- 个人设置弹窗 -->
    <el-dialog v-model="profileVisible" title="个人设置" width="520px" @close="resetProfile">
      <el-tabs v-model="profileTab">
        <el-tab-pane label="个人信息" name="info">
          <el-form ref="profileFormRef" :model="profileForm" :rules="profileRules" label-width="80px">
            <el-form-item label="头像">
              <div class="avatar-row">
                <el-avatar :size="64" :src="profileForm.avatar">
                  {{ userStore.userInfo?.username?.charAt(0) }}
                </el-avatar>
                <el-upload
                  :show-file-list="false"
                  :http-request="handleAvatarUpload"
                  accept="image/*"
                >
                  <el-button size="small" type="primary" plain>更换头像</el-button>
                </el-upload>
              </div>
            </el-form-item>
            <el-form-item label="账号">
              <el-input :model-value="userStore.userInfo?.username" disabled />
            </el-form-item>
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="profileForm.nickname" placeholder="请输入昵称" maxlength="32" />
            </el-form-item>
            <el-form-item label="电话" prop="phone">
              <el-input v-model="profileForm.phone" placeholder="请输入电话号码" maxlength="20" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="profileForm.email" placeholder="请输入邮箱" maxlength="64" />
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="修改密码" name="password">
          <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="80px">
            <el-form-item label="新密码" prop="newPasswd">
              <el-input v-model="pwdForm.newPasswd" type="password" placeholder="请输入新密码" show-password />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPasswd">
              <el-input v-model="pwdForm.confirmPasswd" type="password" placeholder="请再次输入新密码" show-password />
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
      <template #footer>
        <el-button @click="profileVisible = false">取消</el-button>
        <el-button type="primary" @click="handleProfileSubmit" :loading="profileLoading">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { updateUser, uploadUserAvatar } from '@/api/admin'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const collapsed = ref(false)

// 个人设置
const profileVisible = ref(false)
const profileTab = ref('info')
const profileLoading = ref(false)
const profileFormRef = ref()
const pwdFormRef = ref()
const profileForm = reactive({ nickname: '', phone: '', email: '', avatar: '' })
const pwdForm = reactive({ newPasswd: '', confirmPasswd: '' })
const profileRules = {
  email: [{ type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }]
}
const validateConfirmPasswd = (rule, value, callback) => {
  if (value !== pwdForm.newPasswd) callback(new Error('两次输入的密码不一致'))
  else callback()
}
const pwdRules = {
  newPasswd: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPasswd: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPasswd, trigger: 'blur' }
  ]
}

const initProfileForm = () => {
  const info = userStore.userInfo || {}
  Object.assign(profileForm, {
    nickname: info.nickname || '',
    phone: info.phone || '',
    email: info.email || '',
    avatar: info.avatar || ''
  })
  pwdForm.newPasswd = ''
  pwdForm.confirmPasswd = ''
  profileTab.value = 'info'
}

const resetProfile = () => {
  profileFormRef.value?.resetFields()
  pwdFormRef.value?.resetFields()
}

const handleAvatarUpload = async ({ file }) => {
  try {
    const res = await uploadUserAvatar(file)
    if (res.code === 0) {
      profileForm.avatar = res.data || ''
      ElMessage.success('头像上传成功')
    }
  } catch { ElMessage.error('头像上传失败') }
}

const handleProfileSubmit = async () => {
  if (profileTab.value === 'info') {
    if (!profileFormRef.value) return
    try { await profileFormRef.value.validate() } catch { return }
  } else {
    if (!pwdFormRef.value) return
    try { await pwdFormRef.value.validate() } catch { return }
  }
  profileLoading.value = true
  try {
    const data = { id: userStore.userInfo?.id }
    if (profileTab.value === 'info') {
      data.nickname = profileForm.nickname || undefined
      data.phone = profileForm.phone || undefined
      data.email = profileForm.email || undefined
      data.avatar = profileForm.avatar || undefined
    } else {
      data.passwd = pwdForm.newPasswd
    }
    const res = await updateUser(data)
    if (res.code === 0) {
      if (profileTab.value === 'info') {
        const info = { ...userStore.userInfo, ...data }
        userStore.userInfo = info
        localStorage.setItem('userInfo', JSON.stringify(info))
      }
      ElMessage.success('保存成功')
      profileVisible.value = false
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch { ElMessage.error('保存失败') }
  finally { profileLoading.value = false }
}

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
      initProfileForm()
      profileVisible.value = true
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

.brand-logo {
  width: 32px;
  height: 32px;
  flex-shrink: 0;
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

/* ---- Profile Dialog ---- */
.avatar-row {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* ---- Responsive ---- */
@media (max-width: 768px) {
  .layout-content {
    padding: var(--space-md);
  }
}
</style>


