import request from './index'

// 获取平台统计数据
export function getStatistics() {
  return request.get('/statistics/dashboard')
}

// 获取项目统计
export function getProjectStats() {
  return request.get('/statistics/projects/category')
}

// 获取用户统计
export function getUserStats() {
  return request.get('/statistics/users/role')
}

// 获取月度趋势
export function getMonthlyTrend() {
  return request.get('/statistics/projects/monthly')
}

// 获取月度投资趋势
export function getInvestmentMonthlyTrend() {
  return request.get('/statistics/investments/monthly')
}
