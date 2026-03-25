import request from '@/utils/request'

export function getOrderPage(params) {
  return request.get('/order/page', { params })
}

export function getOrderById(id) {
  return request.get(`/order/${id}`)
}

export function addOrder(data) {
  return request.post('/order', data)
}

export function updateOrder(data) {
  return request.put('/order', data)
}

export function deleteOrder(id) {
  return request.delete(`/order/${id}`)
}

export function updateOrderStatus(id, status) {
  return request.put(`/order/status/${id}/${status}`)
}

export function getOrderCount() {
  return request.get('/order/count')
}

export function getUserOrders() {
  return request.get('/order/user')
}

export function getOrderDetail(id) {
  return request.get(`/order/detail/${id}`)
}

export function createOrder(data) {
  return request.post('/order/create', data)
}

export function payOrder(id) {
  return request.put(`/order/pay/${id}`)
}

export function cancelOrder(id) {
  return request.put(`/order/cancel/${id}`)
}

export function confirmOrder(id) {
  return request.put(`/order/confirm/${id}`)
}
