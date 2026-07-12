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

// ==================== 号码归属地 ====================
export const getPhoneAreaList = (params) => {
  return request({ url: '/cc-api/admin/area', method: 'get', params })
}
