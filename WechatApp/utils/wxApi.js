const TOKEN = "token";
const TOKEN_EXPIRED = "token_expired";
const TOKEN_TYPE = "tokenType";
const SERVER_BASE_URL = "http://127.0.0.1";
let duration_time = 86400000;
function saveToken(data) {
  console.log("save token",data)
  let expiredTime = new Date().getTime() + duration_time;
  wx.setStorageSync(TOKEN_EXPIRED, expiredTime);
  wx.setStorageSync(TOKEN, data)
}
function clearToken() {
  wx.removeStorageSync(TOKEN_EXPIRED)
  wx.removeStorageSync(TOKEN)
}
function getToken() {
  let expiredTime = wx.getStorageSync(TOKEN_EXPIRED);
  let isExpired = expiredTime - new Date().getTime() <= 0;
  if (isExpired){
    clearToken();
    return null;
  }
  return wx.getStorageSync(TOKEN)
}
function saveTokenType(data) {
  wx.setStorageSync(TOKEN_TYPE, data)
}
function getTokenType() {
  return wx.getStorageSync(TOKEN_TYPE)
}
//深合并 属性相同后面会覆盖前面
function isEmptyObj(obj){
  return obj == null || Object.keys(obj).length == 0
}
function deepMerge(...obj){
  if(obj == null) return null;
  if (obj.length < 2) return obj[0];
  let data = obj.shift();
  obj.forEach(cur => {
    !isEmptyObj(cur) && Object.keys(cur).forEach(key =>{
      if (typeof data[key] === "object" && typeof cur[key] === "object"){
        deepMerge(data[key], cur[key]);
      }else data[key] = cur[key];
    })
  })
  return data;
}
//用法 load中that = proxyPageTakeAny(this);
//之后 that.data = 1 => this.setData({data: 1})
//     that.data = {prop: 1} => this.setData({prop: 1})
function proxyPageTakeAny(page) {
  return new Proxy(page, {
    set(target, name, any) {
      let data = {};
      if (typeof any == "object")
        data = any;
      else data[name] = any;
      target.setData(data);
    }
  })
}
/**
 * isJSON=true 则上传的数据转为json字符串
 * extra 初始化option配置参数
 * opt 调用的时候也可传参
 * data 要传的数据
 * callback 接收promise resolve可处理好数据给下次Then
 */
function to(fn, extra = {}, isJSON = true) {
  return function (data = {}, opt = {}, callback) {
    if (isJSON) data = JSON.stringify(data);
    if (!callback && opt instanceof Function) callback = opt;
    opt.data = data;
    return new Promise((res, rej) => {
      let option = {
        ...deepMerge(extra, opt),
        success: function (curData) {
          //可用于处理数据]
          if (callback instanceof Function) {
            callback(res, curData)
          } else res(curData);
        },
        fail: rej
      }
      fn(option);
    })
  }
}
//包装wxapi
const t = {
  login: to(wx.login),
  getUserInfo: to(wx.getUserInfo),
  request: to(wx.request),
  getSetting: to(wx.getSetting)
}
function dealToRestURL(url, method){
  if (/(\w|\/)+\?/.test(url)) {
    url += "&"
  }else url += "?"
  url += `_method=${method}`
  return url;
}

function request_init(option, isJSON) {
  return function (url) {
    let token = getToken();
    if (token != null && token.length > 0) {
      option.header = {
        Authorization: getTokenType() + " " + token
      }
    }
    if (option && option.data && option.data._method)
      url = dealToRestURL(url, option.data._method);
    return to(wx.request, {
      ...option,
      url: SERVER_BASE_URL + url
    }, isJSON)
  }
}
function uploadFile_init(option = {}, isJSON) {
  return function (url) {
    let token = getToken();
    if (token != null && token.length > 0) {
      option.header = {
        Authorization: getTokenType() + " " + token
      }
    }
    return to(wx.uploadFile, {
      ...option,
      url: SERVER_BASE_URL + url
    }, isJSON)
  }
}
const fetch = {
  GET: request_init({
    method: "GET"
  }, false),
  POST: request_init({
    method: "POST"
  }),
  PUT: request_init({
    method: "POST",
    data: {
      _method: "PUT"
    }
  }),
  DELETE: request_init({
    method: "POST",
    data: {
      _method: "DELETE"
    }
  }),
  UPLOAD: uploadFile_init()
}

module.exports = {
  SERVER_BASE_URL,
  token:{
    saveToken,
    getToken,
    saveTokenType,
    getTokenType,
    clearToken
  },
  deepMerge,
  proxyPageTakeAny,
  to,
  t,
  fetch
}