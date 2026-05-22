import request from './index'

// 获取用户列表
export function getUserList(params) {
  return request.get('/admin/users', { params })
}

// 更新用户状态
export function updateUserStatus(id, data) {
  return request.put(`/admin/users/${id}/status`, data)
}

// 删除用户
export function deleteUser(id) {
  return request.delete(`/admin/users/${id}`)
}

// 获取用户详情
export function getUserDetail(id) {
  return request.get(`/admin/users/${id}`)
}

// 获取所有反馈（管理员）
export function getAllFeedbacks(params) {
  return request.get('/admin/feedbacks', { params })
}

// 获取所有项目（管理员）
export function getAllProjects(params) {
  return request.get('/admin/projects', { params })
}
