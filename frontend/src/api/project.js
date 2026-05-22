import request from './index'

// 获取项目列表
export function getProjectList(params) {
  return request.get('/projects', { params })
}

// 获取项目详情
export function getProjectDetail(id) {
  return request.get(`/projects/${id}`)
}

// 创建项目
export function createProject(data) {
  return request.post('/projects', data)
}

// 更新项目
export function updateProject(id, data) {
  return request.put(`/projects/${id}`, data)
}

// 删除项目
export function deleteProject(id) {
  return request.delete(`/projects/${id}`)
}

// 获取我的项目列表
export function getMyProjects(params) {
  return request.get('/projects/my', { params })
}

// 审核项目（管理员）
export function auditProject(id, data) {
  return request.put(`/projects/${id}/audit`, data)
}

// 获取待审核项目
export function getPendingProjects(params) {
  return request.get('/projects/pending', { params })
}
