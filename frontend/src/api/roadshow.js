import request from './index'

function normalizeParams(params) {
  if (!params) return params
  return { ...params, size: params.size || params.pageSize }
}

// 获取路演列表
export function getRoadshowList(params) {
  return request.get('/roadshow/list', { params: normalizeParams(params) })
}

// 获取路演详情
export function getRoadshowDetail(id) {
  return request.get(`/roadshow/${id}`)
}

// 报名路演
export function enrollRoadshow(id) {
  return request.post(`/roadshow/${id}/enroll`)
}

// 取消报名
export function cancelEnrollRoadshow(id) {
  return request.delete(`/roadshow/${id}/enroll`)
}

// 创建路演（导师/管理员）
export function createRoadshow(data) {
  return request.post('/roadshow/create', data)
}
