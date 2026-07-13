import request from './request'

// ==================== SIP管理 ====================
export const getSipList = (params) => {
  return request({ url: '/cc-api/config/sip', method: 'get', params })
}

export const addSip = (data) => {
  return request({ url: '/cc-api/config/sip', method: 'post', data })
}

export const updateSip = (id, data) => {
  return request({ url: `/cc-api/config/sip/${id}`, method: 'put', data })
}

export const deleteSip = (id) => {
  return request({ url: `/cc-api/config/sip/${id}`, method: 'delete' })
}

// ==================== 显号管理 ====================
export const getPhoneList = (params) => {
  return request({ url: '/cc-api/config/phone', method: 'get', params })
}

export const addPhone = (data) => {
  return request({ url: '/cc-api/config/phone', method: 'post', data })
}

export const updatePhone = (id, data) => {
  return request({ url: `/cc-api/config/phone/${id}`, method: 'put', data })
}

export const deletePhone = (id) => {
  return request({ url: `/cc-api/config/phone/${id}`, method: 'delete' })
}

// ==================== 显号池管理 ====================
export const getDisplayList = (params) => {
  return request({ url: '/cc-api/config/display', method: 'get', params })
}

export const getDisplayDetail = (id) => {
  return request({ url: `/cc-api/config/display/${id}`, method: 'get' })
}

export const addDisplay = (data) => {
  return request({ url: '/cc-api/config/display', method: 'post', data })
}

export const updateDisplay = (id, data) => {
  return request({ url: `/cc-api/config/display/${id}`, method: 'put', data })
}

export const deleteDisplay = (id) => {
  return request({ url: `/cc-api/config/display/${id}`, method: 'delete' })
}

// ==================== 路由网关管理 ====================
export const getRouteGatewayList = (params) => {
  return request({ url: '/cc-api/config/routegetway', method: 'get', params })
}

export const getRouteGatewayDetail = (id) => {
  return request({ url: `/cc-api/config/routegetway/${id}`, method: 'get' })
}

export const addRouteGateway = (data) => {
  return request({ url: '/cc-api/config/routegetway', method: 'post', data })
}

export const updateRouteGateway = (id, data) => {
  return request({ url: `/cc-api/config/routegetway/${id}`, method: 'put', data })
}

export const deleteRouteGateway = (id) => {
  return request({ url: `/cc-api/config/routegetway/${id}`, method: 'delete' })
}

// ==================== 路由组管理 ====================
export const getRouteGroupList = (params) => {
  return request({ url: '/cc-api/config/routegroup', method: 'get', params })
}

export const getRouteGroupDetail = (id) => {
  return request({ url: `/cc-api/config/routegroup/${id}`, method: 'get' })
}

export const addRouteGroup = (data) => {
  return request({ url: '/cc-api/config/routegroup', method: 'post', data })
}

export const updateRouteGroup = (id, data) => {
  return request({ url: `/cc-api/config/routegroup/${id}`, method: 'put', data })
}

export const deleteRouteGroup = (id) => {
  return request({ url: `/cc-api/config/routegroup/${id}`, method: 'delete' })
}

// ==================== 字冠管理 ====================
export const getRouteCallList = (params) => {
  return request({ url: '/cc-api/config/routecall', method: 'get', params })
}

export const getRouteCallDetail = (id) => {
  return request({ url: `/cc-api/config/routecall/${id}`, method: 'get' })
}

export const addRouteCall = (data) => {
  return request({ url: '/cc-api/config/routecall', method: 'post', data })
}

export const updateRouteCall = (id, data) => {
  return request({ url: `/cc-api/config/routecall/${id}`, method: 'put', data })
}

export const deleteRouteCall = (id) => {
  return request({ url: `/cc-api/config/routecall/${id}`, method: 'delete' })
}

// ==================== 技能管理 ====================
export const getSkillList = (params) => {
  return request({ url: '/cc-api/config/skill', method: 'get', params })
}

export const getSkillDetail = (id) => {
  return request({ url: `/cc-api/config/skill/${id}`, method: 'get' })
}

export const addSkill = (data) => {
  return request({ url: '/cc-api/config/skill', method: 'post', data })
}

export const updateSkill = (id, data) => {
  return request({ url: `/cc-api/config/skill/${id}`, method: 'put', data })
}

export const deleteSkill = (id) => {
  return request({ url: `/cc-api/config/skill/${id}`, method: 'delete' })
}

export const addSkillAgent = (skillId, data) => {
  return request({ url: `/cc-api/config/skill/agent/${skillId}`, method: 'post', data })
}

export const updateSkillAgent = (id, data) => {
  return request({ url: `/cc-api/config/skill/agent/${id}`, method: 'put', data })
}

export const deleteSkillAgent = (id, data) => {
  return request({ url: `/cc-api/config/skill/agent/${id}`, method: 'delete', data })
}

// ==================== 坐席配置管理 ====================
export const getAgentConfigList = (params) => {
  return request({ url: '/cc-api/config/agent', method: 'get', params })
}

export const getAgentConfigDetail = (id) => {
  return request({ url: `/cc-api/config/agent/${id}`, method: 'get' })
}

export const addAgentConfig = (data) => {
  return request({ url: '/cc-api/config/agent', method: 'post', data })
}

export const updateAgentConfig = (id, data) => {
  return request({ url: `/cc-api/config/agent/${id}`, method: 'put', data })
}

export const deleteAgentConfig = (id) => {
  return request({ url: `/cc-api/config/agent/${id}`, method: 'delete' })
}

export const batchAddAgents = (data) => {
  return request({ url: '/cc-api/config/agent/batch', method: 'post', data })
}

// ==================== 技能组配置管理 ====================
export const getGroupConfigList = (params) => {
  return request({ url: '/cc-api/config/group', method: 'get', params })
}

export const getGroupConfigDetail = (id) => {
  return request({ url: `/cc-api/config/group/${id}`, method: 'get' })
}

export const addGroupConfig = (data) => {
  return request({ url: '/cc-api/config/group', method: 'post', data })
}

export const deleteGroupConfig = (id) => {
  return request({ url: `/cc-api/config/group/${id}`, method: 'delete' })
}

export const getGroupAgents = (id) => {
  return request({ url: `/cc-api/config/group/agent/${id}`, method: 'get' })
}

// ==================== AI引擎 ====================
export const getEngineList = (params) => {
  return request({ url: '/cc-api/admin/engine', method: 'get', params })
}

export const saveEngine = (data) => {
  return request({ url: '/cc-api/admin/engine', method: 'post', data })
}

export const deleteEngine = (id) => {
  return request({ url: `/cc-api/admin/engine/${id}`, method: 'delete' })
}

// ==================== 模块站点 ====================
export const getStationList = (params) => {
  return request({ url: '/cc-api/admin/station', method: 'get', params })
}

export const saveStation = (data) => {
  return request({ url: '/cc-api/admin/station', method: 'post', data })
}

export const deleteStation = (id) => {
  return request({ url: `/cc-api/admin/station/${id}`, method: 'delete' })
}

// ==================== 黑名单 ====================
export const getBlackPhoneList = (params) => {
  return request({ url: '/cc-api/admin/blackPhone', method: 'get', params })
}

export const saveBlackPhone = (data) => {
  return request({ url: '/cc-api/admin/blackPhone', method: 'post', data })
}

export const deleteBlackPhone = (id) => {
  return request({ url: `/cc-api/admin/blackPhone/${id}`, method: 'delete' })
}

// ==================== 语音文件 ====================
export const getPlaybackList = (params) => {
  return request({ url: '/cc-api/admin/playback', method: 'get', params })
}

// ==================== 号码归属地 ====================
export const getPhoneAreaList = (params) => {
  return request({ url: '/cc-api/admin/area', method: 'get', params })
}

// ==================== 路由子码(VDN)管理 ====================
export const getVdnList = (params) => {
  return request({ url: '/cc-api/config/vdn', method: 'get', params })
}

export const getVdnDetail = (id) => {
  return request({ url: `/cc-api/config/vdn/${id}`, method: 'get' })
}

export const saveVdn = (data) => {
  return request({ url: '/cc-api/config/vdn', method: 'post', data })
}

export const updateVdn = (id, data) => {
  return request({ url: `/cc-api/config/vdn/${id}`, method: 'put', data })
}

export const deleteVdn = (id) => {
  return request({ url: `/cc-api/config/vdn/${id}`, method: 'delete' })
}

// ==================== VDN子码配置管理 ====================
export const getVdnConfigList = (vdnId) => {
  return request({ url: `/cc-api/config/vdn/${vdnId}/config`, method: 'get' })
}

export const saveVdnConfig = (vdnId, data) => {
  return request({ url: `/cc-api/config/vdn/${vdnId}/config`, method: 'post', data })
}

export const updateVdnConfig = (id, data) => {
  return request({ url: `/cc-api/config/vdn/config/${id}`, method: 'put', data })
}

export const deleteVdnConfig = (id) => {
  return request({ url: `/cc-api/config/vdn/config/${id}`, method: 'delete' })
}

// ==================== 日程管理 ====================
export const getScheduleList = (params) => {
  return request({ url: '/cc-api/config/schedule', method: 'get', params })
}

export const getScheduleDetail = (id) => {
  return request({ url: `/cc-api/config/schedule/${id}`, method: 'get' })
}

export const saveSchedule = (data) => {
  return request({ url: '/cc-api/config/schedule', method: 'post', data })
}

export const updateSchedule = (id, data) => {
  return request({ url: `/cc-api/config/schedule/${id}`, method: 'put', data })
}

export const deleteSchedule = (id) => {
  return request({ url: `/cc-api/config/schedule/${id}`, method: 'delete' })
}

// ==================== 接入号码管理 ====================
export const getVdnPhoneList = (params) => {
  return request({ url: '/cc-api/config/vdnPhone', method: 'get', params })
}

export const getVdnPhoneDetail = (id) => {
  return request({ url: `/cc-api/config/vdnPhone/${id}`, method: 'get' })
}

export const saveVdnPhone = (data) => {
  return request({ url: '/cc-api/config/vdnPhone', method: 'post', data })
}

export const updateVdnPhone = (id, data) => {
  return request({ url: `/cc-api/config/vdnPhone/${id}`, method: 'put', data })
}

export const deleteVdnPhone = (id) => {
  return request({ url: `/cc-api/config/vdnPhone/${id}`, method: 'delete' })
}

// ==================== 排队策略管理 ====================
export const getOverflowList = (params) => {
  return request({ url: '/cc-api/config/overflow', method: 'get', params })
}

export const getOverflowDetail = (id) => {
  return request({ url: `/cc-api/config/overflow/${id}`, method: 'get' })
}

export const saveOverflow = (data) => {
  return request({ url: '/cc-api/config/overflow', method: 'post', data })
}

export const updateOverflow = (id, data) => {
  return request({ url: `/cc-api/config/overflow/${id}`, method: 'put', data })
}

export const deleteOverflow = (id) => {
  return request({ url: `/cc-api/config/overflow/${id}`, method: 'delete' })
}

// ==================== 排队策略前置条件 ====================
export const getOverflowExpList = (overflowId) => {
  return request({ url: `/cc-api/config/overflow/${overflowId}/exp`, method: 'get' })
}

export const saveOverflowExp = (overflowId, data) => {
  return request({ url: `/cc-api/config/overflow/${overflowId}/exp`, method: 'post', data })
}

export const updateOverflowExp = (id, data) => {
  return request({ url: `/cc-api/config/overflow/exp/${id}`, method: 'put', data })
}

export const deleteOverflowExp = (id) => {
  return request({ url: `/cc-api/config/overflow/exp/${id}`, method: 'delete' })
}

// ==================== IVR管理 ====================
export const getIvrList = (params) => {
  return request({ url: '/cc-api/config/ivr', method: 'get', params })
}

export const getIvrDetail = (id) => {
  return request({ url: `/cc-api/config/ivr/${id}`, method: 'get' })
}

export const saveIvr = (data) => {
  return request({ url: '/cc-api/config/ivr', method: 'post', data })
}

export const updateIvr = (id, data) => {
  return request({ url: `/cc-api/config/ivr/${id}`, method: 'put', data })
}

export const deleteIvr = (id) => {
  return request({ url: `/cc-api/config/ivr/${id}`, method: 'delete' })
}

// ==================== 外呼任务管理 ====================
export const getTaskList = (params) => {
  return request({ url: '/cc-api/config/task', method: 'get', params })
}

export const getTaskDetail = (id) => {
  return request({ url: `/cc-api/config/task/${id}`, method: 'get' })
}

export const saveTask = (data) => {
  return request({ url: '/cc-api/config/task', method: 'post', data })
}

export const updateTask = (id, data) => {
  return request({ url: `/cc-api/config/task/${id}`, method: 'put', data })
}

export const deleteTask = (id) => {
  return request({ url: `/cc-api/config/task/${id}`, method: 'delete' })
}

export const getTaskContactList = (taskId) => {
  return request({ url: `/cc-api/config/task/${taskId}/contact`, method: 'get' })
}

export const getTaskPauseList = (taskId) => {
  return request({ url: `/cc-api/config/task/${taskId}/pause`, method: 'get' })
}

// ==================== 数据源管理 ====================
export const getTaskSourceList = (params) => {
  return request({ url: '/cc-api/config/taskSource', method: 'get', params })
}
export const saveTaskSource = (data) => {
  return request({ url: '/cc-api/config/taskSource', method: 'post', data })
}
export const updateTaskSource = (id, data) => {
  return request({ url: `/cc-api/config/taskSource/${id}`, method: 'put', data })
}
export const deleteTaskSource = (id) => {
  return request({ url: `/cc-api/config/taskSource/${id}`, method: 'delete' })
}

// ==================== 字段管理 ====================
export const getTaskFieldList = (params) => {
  return request({ url: '/cc-api/config/taskField', method: 'get', params })
}
export const saveTaskField = (data) => {
  return request({ url: '/cc-api/config/taskField', method: 'post', data })
}
export const updateTaskField = (id, data) => {
  return request({ url: `/cc-api/config/taskField/${id}`, method: 'put', data })
}
export const deleteTaskField = (id) => {
  return request({ url: `/cc-api/config/taskField/${id}`, method: 'delete' })
}

export const getSourceFieldList = (sourceId) => {
  return request({ url: `/cc-api/config/taskSource/${sourceId}/fields`, method: 'get' })
}
export const updateSourceFields = (sourceId, data) => {
  return request({ url: `/cc-api/config/taskSource/${sourceId}/fields`, method: 'put', data })
}

// ==================== 任务监控 ====================
export const getTaskStatList = (params) => {
  return request({ url: '/cc-api/config/taskStat', method: 'get', params })
}
