import request from './index'

function normalizeParams(params) {
  if (!params) return params
  return { ...params, size: params.size || params.pageSize }
}

// 创建咨询
export function createConsultation(data) {
  return request.post('/consultation', {
    ...data,
    description: data.description || data.content
  })
}

// 获取我的咨询列表（学生端）
export function getMyConsultations(params) {
  return request.get('/consultation/my', { params: normalizeParams(params) })
}

// 获取咨询详情
export function getConsultationDetail(id) {
  return request.get(`/consultation/${id}`)
}

// 更新咨询
export function updateConsultation(id, data) {
  return request.put(`/consultation/${id}`, data)
}

// 删除咨询
export function deleteConsultation(id) {
  return request.delete(`/consultation/${id}`)
}

// 获取指定导师咨询列表
export function getConsultationsByMentor(mentorId, params) {
  return request.get(`/consultation/mentor/${mentorId}`, { params: normalizeParams(params) })
}

// 回复咨询
export function replyConsultation(id, data) {
  return request.put(`/consultation/${id}/status`, {
    ...data,
    feedback: data.feedback || data.reply
  })
}

// 评价咨询
export function rateConsultation(id, data) {
  return request.put(`/consultation/${id}/status`, data)
}
