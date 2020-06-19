import request from '@/utils/request'

export function getMenu() {
  return request.GET({
    url: '/adminPage/menu'
  })
}

export function addMenu(menu) {
  return request.PUT({
    url: '/adminPage/menu',
    data: menu
  })
}

export function updateMenu(menu) {
  return request.POST({
    url: '/adminPage/menu',
    data: menu
  })
}

export function deleteMenu(id) {
  return request.DELETE({
    url: '/adminPage/menu/' + id
  })
}
