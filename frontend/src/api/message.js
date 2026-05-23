import request from './index'

function normalizeParams(params) {
  if (!params) return params
  return { ...params, size: params.size || params.pageSize }
}

// 获取消息列表
export function getMessageList(params) {
  return request.get('/message/inbox', { params: normalizeParams(params) })
}

export function getInbox(params) {
  return request.get('/message/inbox', { params: normalizeParams(params) })
}

export function getSent(params) {
  return request.get('/message/sent', { params: normalizeParams(params) })
}

// 获取未读消息数
export function getUnreadCount() {
  return request.get('/message/unread/count')
}

// 标记消息已读
export function markAsRead(id) {
  return request.put(`/message/${id}/read`)
}

// 标记全部已读
export function markAllAsRead() {
  return request.put('/message/read-all')
}

// 删除消息
export function deleteMessage(id) {
  return request.delete(`/message/${id}`)
}

// 发送消息
export function sendMessage(data) {
  return request.post('/message/send', data)
}
