import request from '@/utils/request'

export function getRoutes() {
  return request.GET({
    url: '/adminPage/routes'
  })
}

export function getRoles() {
  return request.GET({
    url: '/adminPage/role'
  })
}

export function addRole(role) {
  role = { ...role }
  role.routes = null
  return request.PUT({
    url: '/adminPage/role',
    data: role
  })
}

export function deleteRole(roleId) {
  return request.DELETE({
    url: '/adminPage/role/' + roleId
  })
}

export function updateRole(role) {
  role = { ...role }
  role.routes = null
  return request.POST({
    url: '/adminPage/role',
    data: role
  })
}
