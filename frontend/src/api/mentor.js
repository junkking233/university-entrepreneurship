import request from './index'

function normalizeParams(params) {
  if (!params) return params
  return { ...params, size: params.size || params.pageSize }
}

// 获取导师列表
export function getMentorList(params) {
  return request.get('/mentor/list', { params: normalizeParams(params) })
}

// 获取导师详情
export function getMentorDetail(id) {
  return request.get(`/mentor/${id}`)
}

// 更新导师资料
export function updateMentorProfile(data) {
  return request.put('/mentor/profile', data)
}

// 获取导师咨询列表
export function getMentorConsultations(params) {
  return request.get('/consultation/mentor/my', { params: normalizeParams(params) })
}

// 更新咨询状态
export function updateConsultationStatus(id, data) {
  return request.put(`/consultation/${id}/status`, {
    ...data,
    feedback: data.feedback || data.reply
  })
}

// 导师培训管理
export function getMentorTrainings(params) {
  return request.get('/training/list', { params: normalizeParams(params) })
}

export function createTraining(data) {
  return request.post('/training', data)
}

export function updateTraining(id, data) {
  return request.put(`/training/${id}`, data)
}

export function deleteTraining(id) {
  return request.delete(`/training/${id}`)
}
