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
