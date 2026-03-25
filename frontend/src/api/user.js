import request from '@/utils/request'

export function getUserList(params) {
  return request.get('/user/list', { params })
}

export function getUserById(id) {
  return request.get(`/user/${id}`)
}

export function addUser(data) {
  return request.post('/user', data)
}

export function updateUser(data) {
  return request.put('/user', data)
}

export function deleteUser(id) {
  return request.delete(`/user/${id}`)
}

export function updateUserStatus(id, status) {
  return request.put(`/user/status/${id}/${status}`)
}

export function getUserCount() {
  return request.get('/user/count')
}

export function updateUserInfo(data) {
  return request.put('/user/info', data)
}

export function updatePassword(data) {
  return request.put('/user/password', data)
}
