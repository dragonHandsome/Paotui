const app  = getApp();
let that;
Page({

  data: {
    root:app.getRoot(),
    likeUserList:[]
  },

  onLoad(){
    that = app.proxy(this);
  },
  onShow() {
    this.switchTab();
    app.initBack(this);
    app.api.getLikeUserList(this);
  },
  tochat(e){
    let id = e.currentTarget.dataset.id;
    id && wx.navigateTo({
      url: '/pages/chat/chat?id='+id,
    })
  },
  toUserDetail(e){
    let id = e.currentTarget.dataset.id;
    id && wx.navigateTo({
      url: '/pages/ta/ta?id='+id,
    })
    console.log(e.currentTarget.dataset.id)
  },
  toIssueTask(e){
    let toUserId = e.currentTarget.dataset.id;
    if(this.getTabBar()){
      const tabBar = this.getTabBar();
      tabBar.data.toUserId = toUserId;
      tabBar.issue();
    }else {
      wx.showToast({
        title: '不兼容,请升级微信',
      })
    }
  },
  switchTab() {
    if (typeof this.getTabBar === 'function' &&
      this.getTabBar()) {
      this.getTabBar().setData({
        selected: 1
      })
    }
  },
  // ListTouch触摸开始
  ListTouchStart(e) {
    this.setData({
      ListTouchStart: e.touches[0].pageX
    })
  },

  // ListTouch计算方向
  ListTouchMove(e) {
    this.setData({
      ListTouchDirection: e.touches[0].pageX - this.data.ListTouchStart > 0 ? 'right' : 'left'
    })
  },

  // ListTouch计算滚动
  ListTouchEnd(e) {
    if (this.data.ListTouchDirection == 'left') {
      this.setData({
        modalName: e.currentTarget.dataset.target
      })
    } else {
      this.setData({
        modalName: null
      })
    }
    this.setData({
      ListTouchDirection: null
    })
  },
})