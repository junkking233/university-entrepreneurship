import request from './index'

// 提交反馈
export function submitFeedback(data) {
  return request.post('/feedbacks', data)
}

// 获取反馈列表
export function getFeedbackList(params) {
  return request.get('/feedbacks', { params })
}

// 获取反馈详情
export function getFeedbackDetail(id) {
  return request.get(`/feedbacks/${id}`)
}

// 回复反馈
export function replyFeedback(id, data) {
  return request.put(`/feedbacks/${id}/reply`, data)
}

// 关闭反馈
export function closeFeedback(id) {
  return request.put(`/feedbacks/${id}/close`)
}
