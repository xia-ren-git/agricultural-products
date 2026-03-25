import request from '@/utils/request'

export function getFavoriteList() {
  return request.get('/favorite/list')
}

export function addToFavorite(productId) {
  return request.post('/favorite/add', { productId })
}

export function removeFromFavorite(productId) {
  return request.delete(`/favorite/${productId}`)
}

export function checkFavorite(productId) {
  return request.get(`/favorite/check/${productId}`)
}

export function getFavoriteCount(productId) {
  return request.get(`/favorite/count/${productId}`)
}
