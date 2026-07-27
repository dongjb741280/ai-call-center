import request from './request'

// 用户管理
export const getUserList = (params) => {
  return request({
    url: '/voxai-admin/admin/user',
    method: 'get',
    params
  })
}

export const addUser = (data) => {
  return request({
    url: '/voxai-admin/admin/user',
    method: 'post',
    data
  })
}

export const updateUser = (data) => {
  return request({
    url: '/voxai-admin/admin/user',
    method: 'put',
    data
  })
}

export const deleteUser = (id) => {
  return request({
    url: `/voxai-admin/admin/user/${id}`,
    method: 'delete'
  })
}

// 文件上传：用户头像
export const uploadUserAvatar = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/voxai-admin/admin/user/avatar',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// 角色管理
export const getRoleList = (params) => {
  return request({
    url: '/voxai-admin/admin/role',
    method: 'get',
    params
  })
}

export const addRole = (data) => {
  return request({
    url: '/voxai-admin/admin/role',
    method: 'post',
    data
  })
}

export const updateRole = (data) => {
  return request({
    url: '/voxai-admin/admin/role',
    method: 'post',
    data
  })
}

export const deleteRole = (id) => {
  return request({
    url: `/voxai-admin/admin/role/${id}`,
    method: 'delete'
  })
}

export const getRoleMenus = (id) => {
  return request({
    url: `/voxai-admin/admin/role/${id}`,
    method: 'get'
  })
}

export const bindRoleMenus = (data) => {
  return request({
    url: '/voxai-admin/admin/roleMenu',
    method: 'post',
    data
  })
}

// 菜单管理
export const getMenuList = (params) => {
  return request({
    url: '/voxai-admin/admin/menu',
    method: 'get',
    params
  })
}

export const getMenuTree = (params) => {
  return request({
    url: '/voxai-admin/admin/menu/tree',
    method: 'get',
    params
  })
}

export const addMenu = (data) => {
  return request({
    url: '/voxai-admin/admin/menu',
    method: 'post',
    data
  })
}

export const updateMenu = (data) => {
  return request({
    url: '/voxai-admin/admin/menu',
    method: 'put',
    data
  })
}

export const deleteMenu = (id) => {
  return request({
    url: `/voxai-admin/admin/menu/${id}`,
    method: 'delete'
  })
}

// 企业管理
export const getCompanyList = (params) => {
  return request({
    url: '/voxai-admin/admin/company',
    method: 'get',
    params
  })
}

export const addCompany = (data) => {
  return request({
    url: '/voxai-admin/admin/company',
    method: 'post',
    data
  })
}

export const updateCompany = (id, data) => {
  return request({
    url: `/voxai-admin/admin/company/${id}`,
    method: 'put',
    data
  })
}

export const deleteCompany = (id) => {
  return request({
    url: `/voxai-admin/admin/company/${id}`,
    method: 'delete'
  })
}

// SIP网关管理
export const getSipGatewayList = (params) => {
  return request({
    url: '/voxai-admin/admin/sipGateway',
    method: 'get',
    params
  })
}

export const saveSipGateway = (data) => {
  return request({
    url: '/voxai-admin/admin/sipGateway',
    method: 'post',
    data
  })
}

export const deleteSipGateway = (ids) => {
  return request({
    url: '/voxai-admin/admin/sipGateway',
    method: 'delete',
    data: ids
  })
}

