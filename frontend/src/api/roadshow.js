import request from './index'

// 获取路演列表
export function getRoadshowList(params) {
  return request.get('/roadshows', { params })
}

// 获取路演详情
export function getRoadshowDetail(id) {
  return request.get(`/roadshows/${id}`)
}

// 报名路演
export function enrollRoadshow(id) {
  return request.post(`/roadshows/${id}/enroll`)
}

// 取消报名
export function cancelEnrollRoadshow(id) {
  return request.delete(`/roadshows/${id}/enroll`)
}

// 创建路演（导师/管理员）
export function createRoadshow(data) {
  return request.post('/roadshows', data)
}
