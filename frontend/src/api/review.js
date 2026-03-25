import request from '@/utils/request'

export function getProductReviews(productId) {
  return request.get(`/review/product/${productId}`)
}

export function getUserReviews() {
  return request.get('/review/user')
}

export function addReview(data) {
  return request.post('/review/add', data)
}

export function replyReview(data) {
  return request.post('/review/reply', data)
}

export function updateReview(id, data) {
  return request.put(`/review/${id}`, data)
}

export function updateReviewStatus(id, status) {
  return request.put(`/review/status/${id}`, null, { params: { status } })
}

export function deleteReview(id) {
  return request.delete(`/review/${id}`)
}

export function toggleReviewLike(id) {
  return request.post(`/review/like/${id}`)
}

export function getReviewStats(productId) {
  return request.get(`/review/stats/${productId}`)
}
