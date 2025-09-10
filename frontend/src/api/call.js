import request from './request'

// 呼叫管理
export const makeCall = (data) => {
  return request({
    url: '/fs-api/cti/call/makeCall',
    method: 'post',
    data
  })
}

export const hangupCall = (data) => {
  return request({
    url: '/fs-api/cti/call/hangup',
    method: 'post',
    data
  })
}

export const answerCall = (data) => {
  return request({
    url: '/fs-api/cti/call/answer',
    method: 'post',
    data
  })
}

export const transferCall = (data) => {
  return request({
    url: '/fs-api/cti/call/transfer',
    method: 'post',
    data
  })
}

// 通话记录
export const getCallLogs = (params) => {
  return request({
    url: '/fs-api/cti/admin/call',
    method: 'get',
    params
  })
}

export const getCallDetail = (callId) => {
  return request({
    url: `/fs-api/cti/admin/call`,
    method: 'get',
    params: { callId }
  })
}

// 呼叫统计
export const getCallStatistics = (params) => {
  return request({
    url: '/fs-api/statistics/call',
    method: 'get',
    params
  })
}


