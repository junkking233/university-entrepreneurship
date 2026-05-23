import request from './index'

function normalizeParams(params) {
  if (!params) return params
  return { ...params, size: params.size || params.pageSize }
}

// 获取投资列表
export function getInvestmentList(params) {
  return request.get('/investment/list', { params: normalizeParams(params) })
}

// 获取我的投资记录
export function getMyInvestments(params) {
  return request.get('/investment/my', { params: normalizeParams(params) })
}

// 获取项目投资记录
export function getProjectInvestments(projectId, params) {
  return request.get(`/investment/project/${projectId}`, { params: normalizeParams(params) })
}

// 获取投资人资料
export function getInvestorProfile() {
  return request.get('/investment/investor/profile')
}

// 更新投资人资料
export function updateInvestorProfile(data) {
  return request.put('/investment/investor/profile', data)
}

// 创建投资意向
export function createInvestment(data) {
  return request.post('/investment', data)
}

// 更新投资状态
export function updateInvestment(id, data) {
  return request.put(`/investment/${id}`, data)
}

// 获取投资详情
export function getInvestmentDetail(id) {
  return request.get(`/investment/${id}`)
}
