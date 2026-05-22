import request from './index'

// 获取消息列表
export function getMessageList(params) {
  return request.get('/messages', { params })
}

// 获取未读消息数
export function getUnreadCount() {
  return request.get('/messages/unread-count')
}

// 标记消息已读
export function markAsRead(id) {
  return request.put(`/messages/${id}/read`)
}

// 标记全部已读
export function markAllAsRead() {
  return request.put('/messages/read-all')
}

// 删除消息
export function deleteMessage(id) {
  return request.delete(`/messages/${id}`)
}

// 发送消息
export function sendMessage(data) {
  return request.post('/messages', data)
}
