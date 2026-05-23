import request from './index'

function normalizeParams(params) {
  if (!params) return params
  return { ...params, size: params.size || params.pageSize }
}

// 获取项目列表
export function getProjectList(params) {
  return request.get('/project/list', { params: normalizeParams(params) })
}

// 获取项目详情
export function getProjectDetail(id) {
  return request.get(`/project/${id}`)
}

// 创建项目
export function createProject(data) {
  return request.post('/project', data)
}

// 更新项目
export function updateProject(id, data) {
  return request.put(`/project/${id}`, data)
}

// 删除项目
export function deleteProject(id) {
  return request.delete(`/project/${id}`)
}

// 获取我的项目列表
export function getMyProjects(params) {
  return request.get('/project/my', { params: normalizeParams(params) })
}

// 审核项目（管理员）
export function auditProject(id, data) {
  const status = data?.status
  if (status === 'approved') {
    return request.put(`/admin/projects/${id}/approve`)
  }
  return request.put(`/admin/projects/${id}/reject`, data)
}

// 获取待审核项目
export function getPendingProjects(params) {
  return request.get('/admin/projects/pending', { params: normalizeParams(params) })
}

// 获取所有项目（管理员）
export function getAllProjects(params) {
  return request.get('/admin/projects', { params: normalizeParams(params) })
}
