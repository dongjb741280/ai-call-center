import request from './request'

// 用户登录
export const login = (data) => {
  return request({
    url: '/cc-api/index/login',
    method: 'post',
    data
  })
}

// 用户登出
export const logout = (token) => {
  return request({
    url: '/cc-api/index/logout',
    method: 'get',
    params: { token }
  })
}

// 获取用户菜单
export const getUserMenus = () => {
  return request({
    url: '/cc-api/admin/menu',
    method: 'get'
  })
}

