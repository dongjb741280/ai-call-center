import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, logout } from '@/api/auth'
import { ElMessage } from 'element-plus'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)
  const menus = ref([])

  const isLoggedIn = computed(() => !!token.value)

  // 初始化用户信息
  const initUser = () => {
    if (token.value) {
      const storedUserInfo = localStorage.getItem('userInfo')
      const storedMenus = localStorage.getItem('menus')
      
      if (storedUserInfo) {
        userInfo.value = JSON.parse(storedUserInfo)
      }
      if (storedMenus) {
        menus.value = JSON.parse(storedMenus)
      }
    }
  }

  // 登录
  const userLogin = async (loginForm) => {
    try {
      const response = await login(loginForm)
      if (response.code === 0) {
        token.value = response.data.token
        userInfo.value = response.data.userInfo
        menus.value = response.data.menus || []
        
        // 存储到本地
        localStorage.setItem('token', token.value)
        localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
        localStorage.setItem('menus', JSON.stringify(menus.value))
        
        ElMessage.success('登录成功')
        return true
      } else {
        ElMessage.error(response.message || '登录失败')
        return false
      }
    } catch (error) {
      ElMessage.error('登录失败：' + error.message)
      return false
    }
  }

  // 登出
  const userLogout = async () => {
    try {
      if (token.value) {
        await logout(token.value)
      }
    } catch (error) {
      console.error('登出失败：', error)
    } finally {
      // 清除本地存储
      token.value = ''
      userInfo.value = null
      menus.value = []
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      localStorage.removeItem('menus')
    }
  }

  return {
    token,
    userInfo,
    menus,
    isLoggedIn,
    initUser,
    userLogin,
    userLogout
  }
})


