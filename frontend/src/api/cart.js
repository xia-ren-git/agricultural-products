import request from '@/utils/request'

export function getCartList() {
  return request.get('/cart/list')
}

export function addToCart(data) {
  return request.post('/cart/add', data)
}

export function updateCartQuantity(id, quantity) {
  return request.put(`/cart/quantity/${id}`, null, { params: { quantity } })
}

export function updateCartSelected(id, selected) {
  return request.put(`/cart/selected/${id}`, null, { params: { selected } })
}

export function updateAllCartSelected(selected) {
  return request.put('/cart/selected/all', null, { params: { selected } })
}

export function deleteCartItem(id) {
  return request.delete(`/cart/${id}`)
}

export function deleteSelectedCart() {
  return request.delete('/cart/selected')
}

export function getCartTotal() {
  return request.get('/cart/total')
}
