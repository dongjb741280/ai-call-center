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
    path: '/softphone',
    name: 'SoftPhone',
    component: () => import('@/views/SoftPhone.vue'),
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
        meta: { title: '控制台', icon: 'Odometer' }
      },
      {
        path: '/admin',
        name: 'Admin',
        meta: { title: '系统管理', icon: 'Setting' },
        children: [
          {
            path: 'roles',
            name: 'AdminRoles',
            component: () => import('@/views/admin/Roles.vue'),
            meta: { title: '系统角色', icon: 'UserFilled' }
          },
          {
            path: 'users',
            name: 'AdminUsers',
            component: () => import('@/views/admin/Users.vue'),
            meta: { title: '账号管理', icon: 'User' }
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
        path: '/service',
        name: 'Service',
        meta: { title: '服务配置', icon: 'SetUp' },
        children: [
          {
            path: 'sip-numbers',
            name: 'ServiceSipNumbers',
            component: () => import('@/views/admin/SipNumbers.vue'),
            meta: { title: 'sip分机', icon: 'Phone' }
          },
          {
            path: 'display-numbers',
            name: 'ServiceDisplayNumbers',
            component: () => import('@/views/admin/DisplayNumbers.vue'),
            meta: { title: '号码管理', icon: 'PhoneFilled' }
          },
          {
            path: 'number-pool',
            name: 'ServiceNumberPool',
            component: () => import('@/views/service/NumberPool.vue'),
            meta: { title: '号码池', icon: 'Collection' }
          },
          {
            path: 'skills',
            name: 'ServiceSkills',
            component: () => import('@/views/admin/Skills.vue'),
            meta: { title: '技能', icon: 'MagicStick' }
          },
          {
            path: 'voice-file',
            name: 'ServiceVoiceFile',
            component: () => import('@/views/service/VoiceFile.vue'),
            meta: { title: '语音文件', icon: 'Headset' }
          },
          {
            path: 'agent-list',
            name: 'ServiceAgentList',
            component: () => import('@/views/agent/List.vue'),
            meta: { title: '坐席管理', icon: 'User' }
          },
          {
            path: 'agent-groups',
            name: 'ServiceAgentGroups',
            component: () => import('@/views/agent/Groups.vue'),
            meta: { title: '技能组', icon: 'UserFilled' }
          }
        ]
      },
      {
        path: '/incoming',
        name: 'Incoming',
        meta: { title: '呼入管理', icon: 'PhoneFilled' },
        children: [
          {
            path: 'route-code',
            name: 'IncomingRouteCode',
            component: () => import('@/views/incoming/RouteCode.vue'),
            meta: { title: '路由子码', icon: 'Switch' }
          },
          {
            path: 'schedule',
            name: 'IncomingSchedule',
            component: () => import('@/views/incoming/Schedule.vue'),
            meta: { title: '日程管理', icon: 'Calendar' }
          },
          {
            path: 'access-number',
            name: 'IncomingAccessNumber',
            component: () => import('@/views/incoming/AccessNumber.vue'),
            meta: { title: '接入号码', icon: 'Phone' }
          },
          {
            path: 'queue-strategy',
            name: 'IncomingQueueStrategy',
            component: () => import('@/views/incoming/QueueStrategy.vue'),
            meta: { title: '排队策略', icon: 'List' }
          },
          {
            path: 'ivr',
            name: 'IncomingIvr',
            component: () => import('@/views/incoming/IvrManage.vue'),
            meta: { title: 'IVR管理', icon: 'Menu' }
          }
        ]
      },
      {
        path: '/outbound',
        name: 'Outbound',
        meta: { title: '外呼任务', icon: 'Phone' },
        children: [
          {
            path: 'task',
            name: 'OutboundTask',
            component: () => import('@/views/outbound/Task.vue'),
            meta: { title: '任务管理', icon: 'List' }
          },
          {
            path: 'datasource',
            name: 'OutboundDatasource',
            component: () => import('@/views/outbound/Datasource.vue'),
            meta: { title: '数据源', icon: 'Coin' }
          },
          {
            path: 'field',
            name: 'OutboundField',
            component: () => import('@/views/outbound/Field.vue'),
            meta: { title: '字段管理', icon: 'Grid' }
          },
          {
            path: 'monitor',
            name: 'OutboundMonitor',
            component: () => import('@/views/outbound/Monitor.vue'),
            meta: { title: '任务监控', icon: 'Monitor' }
          }
        ]
      },
      {
        path: '/softphone',
        name: 'SoftPhoneMenu',
        component: () => import('@/views/SoftPhone.vue'),
        meta: { title: '软电话', icon: 'PhoneFilled' }
      },
      {
        path: '/report',
        name: 'Report',
        meta: { title: '统计报表', icon: 'TrendCharts' },
        children: [
          {
            path: 'operation-log',
            name: 'ReportOperationLog',
            component: () => import('@/views/report/OperationLog.vue'),
            meta: { title: '操作日志', icon: 'Document' }
          },
          {
            path: 'agent-hour',
            name: 'ReportAgentHour',
            component: () => import('@/views/report/AgentHour.vue'),
            meta: { title: '坐席时报统计', icon: 'TrendCharts' }
          },
          {
            path: 'agent-day',
            name: 'ReportAgentDay',
            component: () => import('@/views/report/AgentDay.vue'),
            meta: { title: '坐席日报统计', icon: 'TrendCharts' }
          },
          {
            path: 'company-call',
            name: 'ReportCompanyCall',
            component: () => import('@/views/report/CompanyCall.vue'),
            meta: { title: '企业话务统计', icon: 'TrendCharts' }
          },
          {
            path: 'agent-monitor',
            name: 'ReportAgentMonitor',
            component: () => import('@/views/report/AgentMonitor.vue'),
            meta: { title: '坐席监控', icon: 'Monitor' }
          },
          {
            path: 'call-monitor',
            name: 'ReportCallMonitor',
            component: () => import('@/views/report/CallMonitor.vue'),
            meta: { title: '话务监控', icon: 'Monitor' }
          }
        ]
      },
      {
        path: '/call',
        name: 'Call',
        meta: { title: '话单报表', icon: 'Document' },
        children: [
          {
            path: 'inbound-report',
            name: 'CallInboundReport',
            component: () => import('@/views/call/InboundReport.vue'),
            meta: { title: '呼入报表', icon: 'TrendCharts' }
          },
          {
            path: 'outbound-report',
            name: 'CallOutboundReport',
            component: () => import('@/views/call/OutboundReport.vue'),
            meta: { title: '外呼报表', icon: 'TrendCharts' }
          },
          {
            path: 'ivr-report',
            name: 'CallIvrReport',
            component: () => import('@/views/call/IvrReport.vue'),
            meta: { title: 'IVR报表', icon: 'TrendCharts' }
          },
          {
            path: 'predict-outbound',
            name: 'CallPredictOutbound',
            component: () => import('@/views/call/PredictOutbound.vue'),
            meta: { title: '预出外呼', icon: 'Warning' }
          },
          {
            path: 'push',
            name: 'CallPush',
            component: () => import('@/views/call/Push.vue'),
            meta: { title: '话单推送', icon: 'Bell' }
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
