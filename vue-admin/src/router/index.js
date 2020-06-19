import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/layout'

/**
 * Note: sub-menu only appear when route children.length >= 1
 * Detail see: https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 *
 * hidden: true                   if set true, item will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu
 *                                if not set alwaysShow, when item has more than one children route,
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noRedirect           if set noRedirect will no redirect in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    roles: ['admin','editor']    control the page roles (you can set multiple roles)
    title: 'title'               the name show in sidebar and breadcrumb (recommend set)
    icon: 'svg-name'             the icon show in the sidebar
    breadcrumb: false            if set false, the item will hidden in breadcrumb(default is true)
    activeMenu: '/example/list'  if set path, the sidebar will highlight the path you set
  }
 */

/**
 * constantRoutes
 * a base page that does not have permission requirements
 * all roles can be accessed
 */
export const constantRoutes = [
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },

  {
    path: '/404',
    component: () => import('@/views/404'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/401'),
    hidden: true
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [{
      path: 'dashboard',
      name: 'Dashboard',
      component: () => import('@/views/dashboard/index'),
      meta: { title: 'Dashboard', icon: 'dashboard' }
    }]
  },
  // {
  //   path: '/user',
  //   component: Layout,
  //   redirect: '/user/role',
  //   name: 'Manage',
  //   meta: { title: '用户管理', icon: 'example' },
  //   children: [
  //     {
  //       path: 'user',
  //       name: 'user',
  //       component: () => import('@/views/user/user'),
  //       meta: { title: 'user', icon: 'table' }
  //     },
  //     {
  //       path: 'role',
  //       name: 'role',
  //       component: () => import('@/views/user/role'),
  //       meta: { title: 'role', icon: 'table' }
  //     },
  //     {
  //       path: 'menu',
  //       name: 'menu',
  //       component: () => import('@/views/user/menu'),
  //       meta: { title: 'menu', icon: 'table' }
  //     }
  //   ]
  // },
  // {
  //   path: '/platform',
  //   component: Layout,
  //   redirect: '/platform/task',
  //   name: 'platform',
  //   meta: { title: 'Task Manager', icon: 'dashboard' },
  //   children: [{
  //     path: 'task',
  //     name: 'Task Manager',
  //     component: () => import('@/views/platform/task'),
  //     meta: { title: 'Task', icon: 'dashboard' }
  //   },
  //   {
  //     path: 'apply_cancel',
  //     name: 'ApplyCancelTask Manager',
  //     component: () => import('@/views/platform/taskCancelApply'),
  //     meta: { title: 'ApplyCancelTask', icon: 'dashboard' }
  //   }
  // ]
  // },
  // {
  //   path: '/user-service',
  //   name: 'user-service',
  //   redirect: '/user-service/user-server',
  //   component: Layout,
  //   meta: { title: 'user-service', icon: 'dashboard' },
  //   children: [{
  //     path: 'user-server',
  //     name: 'user-server',
  //     component: () => import('@/views/user-service/user-service'),
  //     meta: { title: 'user-service', icon: 'dashboard' }
  //   }
  // ]
  // },
  // { path: '*', redirect: '/404', hidden: true }
]

export const asyncRoutes = [
  {
    path: '/permission',
    component: Layout,
    redirect: '/permission/page',
    alwaysShow: true, // will always show the root menu
    name: 'Permission',
    meta: {
      title: 'Permission',
      icon: 'lock',
      roles: ['admin', 'editor'] // you can set roles in root nav
    }
  }
]

const createRouter = () => new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router
