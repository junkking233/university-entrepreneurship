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

// 更新路演
export function updateRoadshow(id, data) {
  return request.put(`/roadshow/${id}`, data)
}

// 删除路演
export function deleteRoadshow(id) {
  return request.delete(`/roadshow/${id}`)
}

// 更新路演状态
export function updateRoadshowStatus(id, data) {
  return request.put(`/roadshow/${id}/status`, data)
}

// 添加路演项目
export function addRoadshowProject(id, data) {
  return request.post(`/roadshow/${id}/projects`, data)
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

// 移除路演项目
export function removeRoadshowProject(id, projectId) {
  return request.delete(`/roadshow/${id}/projects/${projectId}`)
}
