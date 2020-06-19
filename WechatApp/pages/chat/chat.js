const app = getApp();
let that;
import { formatTime } from '../../utils/util'
import news from '../../api/websocket'
import Dialog from '@vant/weapp/dialog/dialog';
Page({

  /**
   * 页面的初始数据
   */
  data: {
    root: app.getRoot(),
    InputBottom: 0
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    that = app.proxy(this);
    that.userId = options.id;
    that.getUserNotifies()
    that.readAllNotSpecialNews();
    app.registerNotify(this);
  },
  disableChat(){
    that.loading = true;
  },
  updateNotify(){
    console.log("update")
    that.getUserNotifies()
    let pages = getCurrentPages();
    if(pages[pages.length - 1] == this)
    that.readAllNotSpecialNews();
  },
  enableChat(isClear = false){
    if(isClear) that.content = '';
    that.loading = false;
  },
  // news.createChatNotify(fromUserId, toUserId, content,id)
  chat(){
    const reqOpt = {};
    reqOpt.toUserId = +that.userId;
    reqOpt.content = that.content;
    reqOpt.type = 'CHAT';
  if(!reqOpt.content || reqOpt.content.length < 0) {
    wx.showToast({
      title: '内容为空',
    })
    return;
  }
  that.disableChat()
  news.send(reqOpt)
  .catch(error => {
    wx.showToast({
      title: '发送失败,系统忙',
    })
  })
  .then(()=>{
    that.enableChat(true); 
  })
  },
  getUserNotifies(){
    let id = that.userId;
    const userNotifies = news.getUser(id);
    that.data = {
      userNotifies
    }
  },
  readAllNotSpecialNews(){
    let userNotifies = that.userNotifies;
    const ids = [];
    userNotifies.forEach(cur => {
      let isSpecial = cur.type == "TASK_OPERATE";
      if(!isSpecial && !cur.isRead){
        ids.push(cur.id);
      }
    })
    ids.length  && app.api.readAllNotSpecialNews({
      ids
    })
    .then(res => {
      if(res.data.errCode == 0){
        news.readByIds(ids);
      }
    })
  },
  dealWasIssueTask(e){
    let {taskid:taskId, userid: fromUserId, notifyid: notifyId, issystem : isSystem} = e.currentTarget.dataset;
    //是否接收
    let isAccept = e.target.dataset.istaken;
    let tip = isAccept?"接收":"拒绝";
    Dialog.confirm({
      title: '您确定要' + tip + "吗?"
    })
    .then(()=>{
    app.api.dealWasIssuedTask({
      taskId,
      notifyId,
      fromUserId,
      isAccept
    })
    .then(res => {
      if(res.data.errCode == 0){
        let id = isSystem ? 0 : fromUserId;
        news.read(id, notifyId);
        that.getUserNotifies()
      }else {
        wx.showToast({
          title: '服务器繁忙',
        })
      }
    })
    })
  },

  onShow: function () {
    app.initBack(this);
  },
  toTaskDetail(e){
    let taskId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: '/pages/issueDetail/issueDetail?taskId='+taskId,
    })
  },
  inputChange(e){
    that.content = e.detail.value;
  },
  InputFocus(e) {
    this.setData({
      InputBottom: e.detail.height
    })
  },
  InputBlur(e) {
    this.setData({
      InputBottom: 0
    })
  }
  
})