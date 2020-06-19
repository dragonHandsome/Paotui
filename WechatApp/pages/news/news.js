const app = getApp();
let that;
import news from '../../api/websocket'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    root: app.getRoot()
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    that = app.proxy(this);
    app.registerNotify(this);
  },
  onShow() {
    console.log('show')
    this.switchTab();
    app.initBack(this);
    that.updateNotify();
  },
  switchTab() {
    if (typeof this.getTabBar === 'function' &&
      this.getTabBar()) {
      this.getTabBar().setData({
        selected: 2
      })
      this.getTabBar().updateCount()
    }
  },
  toChat(e){
    let opt = e.currentTarget.dataset;
    wx.navigateTo({
      url: app.parseUrl("/pages/chat/chat", opt),
    })
  },
  updateNotify(){
    console.log("update NOtify")
    const notifies = news.getNewsArr();
    if(!this.compare(notifies, that.news)){
      that.data = {news: notifies}
    }
    console.log(that.news);
  },
  compare(a, b){
    if(a == null || b == null || a.length != b.length) return false;
    let isDiff = true;
    a.forEach((cur, i) => {
      Object.keys(cur).forEach(prop => {
        if(typeof cur[prop] != 'object'){
          if(cur[prop] != b[i][prop]) {
            isDiff = false;
          }
        }
      })
    })
    return isDiff;
  }

})