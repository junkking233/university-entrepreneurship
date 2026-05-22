import request from './index'

// 获取培训列表
export function getTrainingList(params) {
  return request.get('/trainings', { params })
}

// 获取培训详情
export function getTrainingDetail(id) {
  return request.get(`/trainings/${id}`)
}

// 报名培训
export function enrollTraining(id) {
  return request.post(`/trainings/${id}/enroll`)
}

// 取消报名
export function cancelEnrollTraining(id) {
  return request.delete(`/trainings/${id}/enroll`)
}

// 获取我的培训
export function getMyTrainings(params) {
  return request.get('/trainings/my', { params })
}
