//index.js
//获取应用实例
import api from "../../api/api.js";
import { t, token} from "../../utils/wxApi.js";
import {
  Theme,
  ThemeBlack,
  ThemeFactory
} from "../../utils/myTheme.js";
const app = getApp()
let that;
Page({
  data: {
    motto: 'Hello World',
    userInfo: {},
    hasUserInfo: false,
    msg: "msg",
    show: false,
    active: false,
    isLogin: true,
    icon: "cuIcon-roundright",
    canIUse: wx.canIUse('button.open-type.getUserInfo')
  },
  //事件处理函数
  submit(e) {
    this.data.formData = e.detail.value;
  },
  bindViewTap: function() {

  },
  onLoad: function() {
    that = app.proxy(this);
  },
  formSubmit(e) {
    let form = e.detail.value;
    console.log(form)
    if (this.data.isLogin) {
      this.login(form);
    } else {
      this.register(form);
    }
  },
  login(form) {
    app.api.login(form)
      .then(res => {
        app.dealResponse(res, res => {
          token.saveToken(res.token);
          token.saveTokenType(res.tokenType);
          wx.showToast({
            title: '登录成功~',
          })
          setTimeout(()=>{
            wx.switchTab({
              url: '/pages/index/index',
            })
          }, 500);
        })
      })
  },
  register(form) {
    app.api.register(form)
      .then(res => {
        console.log(res)
        if(res.data.errCode == 0){
          wx.showToast({
            title: '注册成功,'+res.data.data,
          })
        }else {
          wx.showToast({
            title: res.data.msg + res.data.data,
          })
        }
      })
  },
  showLogin() {
    that.show = !that.data.show;
  },
  toggleLogin() {
    that.isLogin = !that.data.isLogin;
    this.toggleAdmin();
  },
  toggleAdmin() {
    that.active = !that.data.active;
  },
  getUserInfo: function(e) {
    let userInfo = app.globalData.userInfo = e.detail.userInfo;

    that.normalLogin()
      .catch(() => {
        wx.showToast({
          title: '登录失败,服务器繁忙！',
        })
      })
      .then(res => {
        app.updatePageUserInfo(userInfo);
        wx.showLoading({
          title: '登录中...',
        })
        api.updateUser(userInfo).catch(() => {
            wx.hideLoading()
          })
          .then(res => {
            if (res.data.errCode == 0) {
              wx.showToast({
                title: '登录成功',
              })
              setTimeout(() => wx.switchTab({
                url: '/pages/index/index',
              }), 500)
            } else {
              wx.showToast({
                title: '登录失败',
              })
            }
            wx.hideLoading()
          })
      })

  },
  onReady() {
    const query = wx.createSelectorQuery()
    query.select('#myCanvas')
      .fields({
        node: true,
        size: true
      })
      .exec((res) => {
        const canvas = res[0].node
        const ctx = canvas.getContext('2d')
        new Theme(canvas)
          .init()
          .animate();
      })
  },
  normalLogin() {
    // 登录
    return t.login()
      .then(resp => {
        return api.signin(resp.code)
      })
      .then(resp => {
        resp = resp.data;
        if (resp.errCode === 0) {
          token.saveToken(resp.data.token)
          token.saveTokenType(resp.data.tokenType)
        } else throw new Error("登录失败")
      })
  }
})