import request from '@/utils/request'

export function login(data) {
  return request.POST({
    url: '/auth/signin',
    data
  })
}

export function getInfo() {
  return request.GET({
    url: '/adminPage/admin/info'
  })
}

export function logout() {
  return new Promise((res, rej) => {
    res({ errCode: 0 })
  })
}

export function getUsers() {
  return request.GET({
    url: '/adminPage/admin'
  })
}

export function addUser(user) {
  return request.PUT({
    url: '/adminPage/admin',
    data: user
  })
}

export function updateUser(user) {
  return request.POST({
    url: '/adminPage/admin',
    data: user
  })
}
