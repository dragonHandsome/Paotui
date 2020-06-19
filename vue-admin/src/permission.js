import router from './router'
import store from './store'
import { Message } from 'element-ui'
import NProgress from 'nprogress' 
import 'nprogress/nprogress.css'
import { getToken } from '@/utils/auth' // 
import getPageTitle from '@/utils/get-page-title'

NProgress.configure({ showSpinner: false }) 

const whiteList = ['/login'] 

router.beforeEach(async(to, from, next) => {

  NProgress.start()

  document.title = getPageTitle(to.meta.title)

  const hasToken = getToken()

  if (hasToken) {
    if (to.path === '/login') {

      next({ path: '/' })
      NProgress.done()
    } else {
      console.log("roles")
      const hasRoles = store.getters.roles
      console.log(store.getters)
      if (hasRoles) {
        next()
      } else {
        try {
          // get user info
          // await store.dispatch('user/getInfo')
          
          // const accessRoutes = await store.dispatch('permission/generateRoutes', roles)
            
          // next()
          let { roles } = await store.dispatch('user/getInfo')
          roles = roles.reduce((acc, cur) => {
            acc.push(cur.roleName)
            return acc
          },[])
          const accessRoutes = await store.dispatch('permission/generateRoutes', roles)
          console.log("动态路由")
          console.log(accessRoutes)
          //动态添加路由
          router.addRoutes(accessRoutes)
          
          next({ ...to, replace: true })
        } catch (error) {
          // remove token and go to login page to re-login
          await store.dispatch('user/resetToken')
          Message.error(error || 'Has Error')
          next(`/login?redirect=${to.path}`)
          NProgress.done()
        }
      }
    }
  } else {
    /* has no token*/

    if (whiteList.indexOf(to.path) !== -1) {
      // in the free login whitelist, go directly
      next()
    } else {
      // other pages that do not have permission to access are redirected to the login page.
      next(`/login?redirect=${to.path}`)
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  // finish progress bar
  NProgress.done()
})
