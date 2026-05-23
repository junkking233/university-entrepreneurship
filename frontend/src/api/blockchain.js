import request from './index'

// 获取区块链交易记录
export function getTransactions(params) {
  return request.get(`/blockchain/project/${params.projectId}`)
}

// 获取项目信任评分
export function getTrustScore(projectId) {
  return request.get(`/blockchain/project/${projectId}/trust-score`)
}

// 获取区块链验证状态
export function verifyCertificate(projectId) {
  return request.get(`/blockchain/verify/${projectId}`)
}

// 上链存证
export function createCertificate(data) {
  return request.post(`/blockchain/project/${data.projectId}/record`, data)
}
