import request from '@/utils/request'

export function getOverview() {
  return request.get('/statistics/overview')
}

export function getSalesTrend() {
  return request.get('/statistics/sales-trend')
}

export function getCategoryDistribution() {
  return request.get('/statistics/category-distribution')
}

export function getOrderStatus() {
  return request.get('/statistics/order-status')
}

export function getUserGrowth() {
  return request.get('/statistics/user-growth')
}

export function getHotProducts() {
  return request.get('/statistics/hot-products')
}

export function getRevenue() {
  return request.get('/statistics/revenue')
}
