import request from './index'

// 获取平台统计数据
export function getStatistics() {
  return request.get('/statistics/overview')
}

// 获取项目统计
export function getProjectStats() {
  return request.get('/statistics/projects')
}

// 获取用户统计
export function getUserStats() {
  return request.get('/statistics/users')
}

// 获取月度趋势
export function getMonthlyTrend() {
  return request.get('/statistics/monthly-trend')
}
