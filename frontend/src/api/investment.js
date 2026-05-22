import request from './index'

// 获取投资列表
export function getInvestmentList(params) {
  return request.get('/investments', { params })
}

// 获取我的投资记录
export function getMyInvestments(params) {
  return request.get('/investments/my', { params })
}

// 创建投资意向
export function createInvestment(data) {
  return request.post('/investments', data)
}

// 更新投资状态
export function updateInvestment(id, data) {
  return request.put(`/investments/${id}`, data)
}

// 获取投资详情
export function getInvestmentDetail(id) {
  return request.get(`/investments/${id}`)
}
