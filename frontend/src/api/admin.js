import request from './request'

// 用户管理
export const getUserList = (params) => {
  return request({
    url: '/cc-api/admin/user',
    method: 'get',
    params
  })
}

export const addUser = (data) => {
  return request({
    url: '/cc-api/admin/user',
    method: 'post',
    data
  })
}

export const updateUser = (data) => {
  return request({
    url: '/cc-api/admin/user',
    method: 'put',
    data
  })
}

export const deleteUser = (id) => {
  return request({
    url: `/cc-api/admin/user/${id}`,
    method: 'delete'
  })
}

// 角色管理
export const getRoleList = (params) => {
  return request({
    url: '/cc-api/admin/role',
    method: 'get',
    params
  })
}

export const addRole = (data) => {
  return request({
    url: '/cc-api/admin/role',
    method: 'post',
    data
  })
}

export const updateRole = (data) => {
  return request({
    url: '/cc-api/admin/role',
    method: 'put',
    data
  })
}

export const deleteRole = (id) => {
  return request({
    url: `/cc-api/admin/role/${id}`,
    method: 'delete'
  })
}

export const getRoleMenus = (id) => {
  return request({
    url: `/cc-api/admin/role/${id}`,
    method: 'get'
  })
}

export const bindRoleMenus = (data) => {
  return request({
    url: '/cc-api/admin/roleMenu',
    method: 'post',
    data
  })
}

// 菜单管理
export const getMenuList = (params) => {
  return request({
    url: '/cc-api/admin/menu',
    method: 'get',
    params
  })
}

export const addMenu = (data) => {
  return request({
    url: '/cc-api/admin/menu',
    method: 'post',
    data
  })
}

export const updateMenu = (data) => {
  return request({
    url: '/cc-api/admin/menu',
    method: 'put',
    data
  })
}

export const deleteMenu = (id) => {
  return request({
    url: `/cc-api/admin/menu/${id}`,
    method: 'delete'
  })
}

// 企业管理
export const getCompanyList = (params) => {
  return request({
    url: '/cc-api/admin/company',
    method: 'get',
    params
  })
}

export const addCompany = (data) => {
  return request({
    url: '/cc-api/admin/company',
    method: 'post',
    data
  })
}

export const updateCompany = (data) => {
  return request({
    url: '/cc-api/admin/company',
    method: 'put',
    data
  })
}

export const deleteCompany = (id) => {
  return request({
    url: `/cc-api/admin/company/${id}`,
    method: 'delete'
  })
}

