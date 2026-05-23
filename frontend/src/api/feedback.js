import request from './index'

function normalizeParams(params) {
  if (!params) return params
  return { ...params, size: params.size || params.pageSize }
}

// 提交反馈
export function submitFeedback(data) {
  return request.post('/feedback/create', data)
}

// 获取我的反馈列表
export function getMyFeedbacks(params) {
  return request.get('/feedback/my', { params: normalizeParams(params) })
}

// 获取反馈列表
export function getFeedbackList(params) {
  return request.get('/feedback/list', { params: normalizeParams(params) })
}

// 获取反馈详情
export function getFeedbackDetail(id) {
  return request.get(`/feedback/${id}`)
}

// 回复反馈
export function replyFeedback(id, data) {
  return request.put(`/feedback/${id}/reply`, data)
}

// 关闭反馈
export function closeFeedback(id) {
  return request.put(`/feedback/${id}/status`, { status: 'closed' })
}
