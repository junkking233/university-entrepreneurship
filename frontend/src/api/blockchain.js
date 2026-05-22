import request from './index'

// 获取区块链交易记录
export function getTransactions(params) {
  return request.get('/blockchain/transactions', { params })
}

// 获取区块链验证状态
export function verifyCertificate(hash) {
  return request.get(`/blockchain/verify/${hash}`)
}

// 上链存证
export function createCertificate(data) {
  return request.post('/blockchain/certificate', data)
}
