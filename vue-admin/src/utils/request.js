import axios from 'axios'
import { MessageBox, Message } from 'element-ui'
import store from '@/store'
import { getToken } from '@/utils/auth'
import router from '@/router'

// create an axios instance
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API, // url = base url + request url
  // withCredentials: true, // send cookies when cross-domain requests
  timeout: 5000 // request timeout
})

// request interceptor
service.interceptors.request.use(
  config => {
    // do something before request is sent

    if (store.getters.token) {
      config.headers['Authorization'] = 'Bearer ' + getToken()
    }
    return config
  },
  error => {
    // do something with request error
    console.log(error) // for debug
    return Promise.reject(error)
  }
)

// response interceptor
service.interceptors.response.use(
  /**
   * If you want to get http information such as headers or status
   * Please return  response => response
  */

  /**
   * Determine the request status by custom code
   * Here is just an example
   * You can also judge the status by HTTP Status Code
   */
  response => {
    const res = response.data

    if (res.errCode !== 0) {
      Message({
        message: res.msg || 'Error',
        type: 'error',
        duration: 5 * 1000
      })
      if (res.errCode === -11 ||　res.errCode === 403) {
        router.replace('/401')
      }
      if (res.errCode === 401){
        // to re-login
        MessageBox.confirm('You have been logged out, you can cancel to stay on this page, or log in again', 'Confirm logout', {
          confirmButtonText: 'Re-Login',
          cancelButtonText: 'Cancel',
          type: 'warning'
        }).then(() => {
          store.dispatch('user/resetToken').then(() => {
            location.reload()
          })
        })
      }
      return Promise.reject(new Error(res.msg || 'Error'))
    } else {
      return res
    }
  },
  error => {
    console.log('err' + error) // for debug
    Message({
      message: error.message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)
function dealUrl(url, opt) {
  const isHasParameter = url.includes('?')
  if (!isHasParameter) {
    url += '?'
  } else url += '&'
  Object.keys(opt).forEach(key => {
    if (opt[key] != null) {
      url += `${key}=${opt[key]}&`
    }
  })
  return url
}
const request = {
  GET: (opt) => {
    opt.method = 'get'
    typeof opt.data === 'object' && (opt.url = dealUrl(opt.url, opt.data))
    return service(opt)
  },
  POST: (opt) => {
    opt.method = 'post'
    return service(opt)
  },
  PUT: (opt) => {
    opt.method = 'post'
    opt.url = dealUrl(opt.url, { _method: 'PUT' })
    return service(opt)
  },
  DELETE: (opt) => {
    opt.method = 'post'
    opt.url = dealUrl(opt.url, { _method: 'DELETE' })
    return service(opt)
  }

}
export default request
