import request from './index'

// 创建咨询
export function createConsultation(data) {
  return request.post('/consultations', data)
}

// 获取我的咨询列表（学生端）
export function getMyConsultations(params) {
  return request.get('/consultations/my', { params })
}

// 获取咨询详情
export function getConsultationDetail(id) {
  return request.get(`/consultations/${id}`)
}

// 回复咨询
export function replyConsultation(id, data) {
  return request.put(`/consultations/${id}/reply`, data)
}

// 评价咨询
export function rateConsultation(id, data) {
  return request.put(`/consultations/${id}/rate`, data)
}
