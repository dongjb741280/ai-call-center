import request from './request'

// 坐席管理
export const getAgentList = (params) => {
  return request({
    url: '/fs-api/agent/list',
    method: 'get',
    params
  })
}

export const addAgent = (data) => {
  return request({
    url: '/fs-api/agent',
    method: 'post',
    data
  })
}

export const updateAgent = (data) => {
  return request({
    url: '/fs-api/agent',
    method: 'put',
    data
  })
}

export const deleteAgent = (id) => {
  return request({
    url: `/fs-api/agent/${id}`,
    method: 'delete'
  })
}

// 坐席状态管理
export const agentReady = () => {
  return request({
    url: '/fs-api/cti/agent/ready',
    method: 'post'
  })
}

export const agentNotReady = () => {
  return request({
    url: '/fs-api/cti/agent/notReady',
    method: 'post'
  })
}

export const agentLogout = () => {
  return request({
    url: '/fs-api/cti/agent/logout',
    method: 'post'
  })
}

// 技能组管理
export const getGroupList = (params) => {
  return request({
    url: '/fs-api/group/list',
    method: 'get',
    params
  })
}

export const addGroup = (data) => {
  return request({
    url: '/fs-api/group',
    method: 'post',
    data
  })
}

export const updateGroup = (data) => {
  return request({
    url: '/fs-api/group',
    method: 'put',
    data
  })
}

export const deleteGroup = (id) => {
  return request({
    url: `/fs-api/group/${id}`,
    method: 'delete'
  })
}


