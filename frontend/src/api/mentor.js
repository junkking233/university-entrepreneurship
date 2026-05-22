import request from './index'

// 获取导师列表
export function getMentorList(params) {
  return request.get('/mentors', { params })
}

// 获取导师详情
export function getMentorDetail(id) {
  return request.get(`/mentors/${id}`)
}

// 更新导师资料
export function updateMentorProfile(data) {
  return request.put('/mentor/profile', data)
}

// 获取导师咨询列表
export function getMentorConsultations(params) {
  return request.get('/mentor/consultations', { params })
}

// 更新咨询状态
export function updateConsultationStatus(id, data) {
  return request.put(`/mentor/consultations/${id}`, data)
}

// 导师培训管理
export function getMentorTrainings(params) {
  return request.get('/mentor/trainings', { params })
}

export function createTraining(data) {
  return request.post('/mentor/trainings', data)
}

export function updateTraining(id, data) {
  return request.put(`/mentor/trainings/${id}`, data)
}

export function deleteTraining(id) {
  return request.delete(`/mentor/trainings/${id}`)
}
