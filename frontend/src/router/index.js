import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/demo',
    name: 'Demo',
    component: () => import('@/views/Demo.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/layout/Index.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '仪表板', icon: 'Odometer' }
      },
      {
        path: '/admin',
        name: 'Admin',
        meta: { title: '系统管理', icon: 'Setting' },
        children: [
          {
            path: 'users',
            name: 'AdminUsers',
            component: () => import('@/views/admin/Users.vue'),
            meta: { title: '账号管理', icon: 'User' }
          },
          {
            path: 'roles',
            name: 'AdminRoles',
            component: () => import('@/views/admin/Roles.vue'),
            meta: { title: '系统角色', icon: 'UserFilled' }
          },
          {
            path: 'menus',
            name: 'AdminMenus',
            component: () => import('@/views/admin/Menus.vue'),
            meta: { title: '菜单管理', icon: 'Menu' }
          },
          {
            path: 'companies',
            name: 'AdminCompanies',
            component: () => import('@/views/admin/Companies.vue'),
            meta: { title: '企业管理', icon: 'OfficeBuilding' }
          },
          {
            path: 'gateways',
            name: 'AdminGateways',
            component: () => import('@/views/admin/Gateways.vue'),
            meta: { title: '网关管理', icon: 'Link' }
          },
          {
            path: 'number-route',
            name: 'AdminNumberRoute',
            component: () => import('@/views/admin/NumberRoute.vue'),
            meta: { title: '号码路由', icon: 'Switch' }
          },
          {
            path: 'number-location',
            name: 'AdminNumberLocation',
            component: () => import('@/views/admin/NumberLocation.vue'),
            meta: { title: '号码归属地', icon: 'Location' }
          },
          {
            path: 'blacklist',
            name: 'AdminBlacklist',
            component: () => import('@/views/admin/Blacklist.vue'),
            meta: { title: '黑名单', icon: 'RemoveFilled' }
          },
          {
            path: 'modules',
            name: 'AdminModules',
            component: () => import('@/views/admin/Modules.vue'),
            meta: { title: '模块站点', icon: 'Grid' }
          },
          {
            path: 'sip-gateway',
            name: 'AdminSipGateway',
            component: () => import('@/views/admin/SipGateway.vue'),
            meta: { title: 'sip网关', icon: 'Connection' }
          },
          {
            path: 'ai-engine',
            name: 'AdminAiEngine',
            component: () => import('@/views/admin/AiEngine.vue'),
            meta: { title: 'ai引擎', icon: 'Cpu' }
          }
        ]
      },
      {
        path: '/agent',
        name: 'Agent',
        meta: { title: '坐席管理', icon: 'Headset' },
        children: [
          {
            path: 'list',
            name: 'AgentList',
            component: () => import('@/views/agent/List.vue'),
            meta: { title: '坐席列表', icon: 'User' }
          },
          {
            path: 'groups',
            name: 'AgentGroups',
            component: () => import('@/views/agent/Groups.vue'),
            meta: { title: '技能组管理', icon: 'UserFilled' }
          }
        ]
      },
      {
        path: '/call',
        name: 'Call',
        meta: { title: '呼叫管理', icon: 'Phone' },
        children: [
          {
            path: 'logs',
            name: 'CallLogs',
            component: () => import('@/views/call/Logs.vue'),
            meta: { title: '通话记录', icon: 'Document' }
          },
          {
            path: 'statistics',
            name: 'CallStatistics',
            component: () => import('@/views/call/Statistics.vue'),
            meta: { title: '呼叫统计', icon: 'TrendCharts' }
          }
        ]
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next('/login')
  } else if (to.path === '/login' && userStore.isLoggedIn) {
    next('/')
  } else {
    next()
  }
})

export default router
