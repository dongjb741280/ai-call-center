import request from './request'
import CryptoJS from 'crypto-js'

// 坐席登录（fs-api）
export const agentLogin = (data) => {
  const payload = {
    agentKey: data.agentKey,
    passwd: CryptoJS.SHA256(data.passwd).toString(),
    loginType: data.loginType,
    workType: data.workType,
    forceLogin: data.forceLogin || false
  }
  return request({
    url: '/fs-api/index/login',
    method: 'post',
    data: payload
  })
}

// 坐席空闲
export const agentReady = (token) => {
  return request({
    url: '/fs-api/cti/agent/ready',
    method: 'post',
    headers: { token }
  })
}

// 坐席忙碌
export const agentNotReady = (token) => {
  return request({
    url: '/fs-api/cti/agent/notReady',
    method: 'post',
    headers: { token }
  })
}

// 坐席登出
export const agentLogout = (token) => {
  return request({
    url: '/fs-api/cti/agent/logout',
    method: 'post',
    headers: { token }
  })
}

// 发起呼叫
export const makeCall = (token, data) => {
  return request({
    url: '/fs-api/cti/call/makeCall',
    method: 'post',
    headers: { token },
    data
  })
}

// 挂断电话
export const hangupCall = (token) => {
  return request({
    url: '/fs-api/cti/call/hangup',
    method: 'post',
    headers: { token }
  })
}

// 保持
export const holdCall = (token) => {
  return request({
    url: '/fs-api/cti/call/hold',
    method: 'post',
    headers: { token }
  })
}

// 取消保持
export const cancelHold = (token) => {
  return request({
    url: '/fs-api/cti/call/cancelHold',
    method: 'post',
    headers: { token }
  })
}

// 静音
export const readyMute = (token) => {
  return request({
    url: '/fs-api/cti/call/readyMute',
    method: 'post',
    headers: { token }
  })
}

// 取消静音
export const cancelMute = (token) => {
  return request({
    url: '/fs-api/cti/call/cancelMute',
    method: 'post',
    headers: { token }
  })
}
