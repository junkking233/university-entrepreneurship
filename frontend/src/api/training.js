import request from './index'

function normalizeParams(params) {
  if (!params) return params
  return { ...params, size: params.size || params.pageSize }
}

// 获取培训列表
export function getTrainingList(params) {
  return request.get('/training/list', { params: normalizeParams(params) })
}

// 获取培训详情
export function getTrainingDetail(id) {
  return request.get(`/training/${id}`)
}

// 报名培训
export function enrollTraining(id) {
  return request.post(`/training/${id}/register`)
}

// 取消报名
export function cancelEnrollTraining(id) {
  return request.delete(`/training/${id}/register`)
}

// 获取我的培训
export function getMyTrainings(params) {
  return request.get('/training/my-registrations', { params: normalizeParams(params) })
}
