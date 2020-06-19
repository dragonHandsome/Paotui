import { constantRoutes } from '@/router'
import { getRoutes } from '@/api/role'
import Layout from '@/layout'
const req = require.context('@/views', true, /\.vue$/)

/**
 * 
 * @param roles
 * @param route
 */
function hasPermission(roles, route) {
  if (route.meta && route.meta.roles) {
    return roles.some(role => route.meta.roles.includes(role))
  } else {
    return true
  }
}

function loadModule(route) {
  if(route instanceof Object) {
    const moduleName = route.component
    if(moduleName === '@/layout') {
        route.component = Layout
    } else route.component = req('./' + moduleName + '.vue').default
  }
}

//处理路由
function handleAsyncRoutes(asyncRoutes) {
    if(asyncRoutes instanceof Array && asyncRoutes.length > 0) {
      asyncRoutes.forEach(route => {
        loadModule(route)
        handleAsyncRoutes(route.children)
      })
    }
}

/**
 * 
 * @param routes asyncRoutes
 * @param roles
 */
export function filterAsyncRoutes(routes, roles) {
  const res = []
  // asyncRoutes
  routes.forEach(route => {
    const tmp = { ...route }
    if (hasPermission(roles, tmp)) {
      if (tmp.children) {
        tmp.children = filterAsyncRoutes(tmp.children, roles)
      }
      res.push(tmp)
    }
  })

  return res
}

const state = {
  routes: [],
  addRoutes: []
}

const mutations = {
  SET_ROUTES: (state, routes) => {
    state.addRoutes = routes
    state.routes = constantRoutes.concat(routes)
  }
}

const actions = {
  generateRoutes({ commit }, roles) {
    return new Promise(async resolve => {
      const {data: asyncRoutes} = await getRoutes()
      handleAsyncRoutes(asyncRoutes)
      //添加404
      asyncRoutes.push({ path: '*', redirect: '/404', hidden: true })
      let accessedRoutes = null
      if (roles.includes('超管-孨龘')) {
        accessedRoutes = asyncRoutes || []
      } else {
        accessedRoutes = filterAsyncRoutes(asyncRoutes, roles)
      }
      commit('SET_ROUTES', accessedRoutes)
      resolve(accessedRoutes)
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
