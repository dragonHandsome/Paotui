import api from "../../api/api.js";
import { token} from "../../utils/wxApi.js"
const app = getApp()
let that;
const swiper_config = {
  cardCur: 0,
  rankList: []
}
const iconList = [];
Page({
  data: {
    ...swiper_config,
    root: app.getRoot(),
    iconList,
    StatusBar: app.globalData.StatusBar,
    CustomBar: app.globalData.CustomBar,
    show: false,
    actions: [{
      name: "选项"
    }],
  },
  onLoad() {
    that = app.proxyPageTakeAny(this);
    
  },
  cardSwiper(e) {
    this.setData({
      cardCur: e.detail.current
    })
  },
  onShow(){
    that.switchTab();
    this.getIssueCategory()
    app.initBack(this);
    app.api.getRankList(this);
    if (token.getToken() == null){
      that.toLoginPage()
    }else {
      getApp().startWebSocket()
    }
  },
  toLoginPage() {
    const LOGIN = "/pages/login/login";
    wx.reLaunch({
      url: LOGIN,
    })
  },
  switchTab(){
    if (typeof this.getTabBar === 'function' &&
      this.getTabBar()) {
      this.getTabBar().setData({
        selected: 0,
        newsCount: app.getNoReadCount()
      })
    }
  },
  randInt(min, max){
    return (min + (max - min) * Math.random()) >> 0;
  },
  issue(){
    that.show = true;
  },
  onClose() {
    that.show = false;
  },

  onSelect(event) {
    wx.navigateTo({
      url: app.url("/pages/issue/issue", event.detail)
    })
  },
  goTask(e){
    let categoryId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: app.parseUrl("/pages/task/task", { id: categoryId}),
    })
  },
  getTaskDetail(e) {
    console.log("getTaskDetail")
    let taskId = e.currentTarget.dataset.id;
    let taskDetail = api.getTaskDetail(taskId);
    that.data = { taskDetail }
    console.dir(this.data.taskDetail)
  },
  //获取发布的所有类型
  getIssueCategory(){
    app.getIssueCategory(this);
  }
})