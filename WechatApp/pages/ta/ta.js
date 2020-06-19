// pages/my/my.js
const app = getApp();
import api from "../../api/api.js"
import {
  to
} from "../../utils/wxApi.js"
let that;
import Dialog from '@vant/weapp/dialog/dialog';
let taskStatus = [{
  status: "ALL",
  text: '全部'
}]
taskStatus = taskStatus.concat(app.globalData.taskStatus);
Page({

  /**
   * 页面的初始数据
   */
  data: {
    active: "ALL",
    root: app.getServerUrl(),
    taskStatus,
    isCurUser: true,
    titleActive: true,
    isLike: false,
    backgroundDefault: "https://img.yzcdn.cn/vant/cat.jpeg"
  },
  onLoad: function(options) {
    that = app.proxyPageTakeAny(this);
    this.data.userId = options.id;
  },
  onShow() {
    that.init();
  },
  downLoad() {
    Dialog.confirm({
        title: '您要下载这张背景吗?'
      })
      .then(() => {
        return to(wx.downloadFile, {
            url: this.data.backUrl
          })()
          .then(res => {
            let tempPath = res.tempFilePath;
            wx.saveImageToPhotosAlbum({
              filePath: tempPath,
              success() {
                wx.showToast({
                  title: '下载成功'
                })
              }
            })
          })
      })
      .catch(() => {})
  },
  like() {
    app.api.updateTogglelike(that.data.user.id)
      .then(({
        data: res
      }) => {
        if (res.errCode == 0) {
          that.isLike = !that.data.isLike;
          let msg = that.data.isLike ? '关注TA成功' : "取消关注成功";
          wx.showToast({
            title: msg
          })
        } else throw new Error();
      })
      .catch(error => {
        wx.showToast({
          title: '关注失败,繁忙..',
        })
      })
  },
  switchTitle() {
    that.titleActive = !that.data.titleActive;
  },
  goTaskDetail(e) {
    console.log(e)
    let taskId = e.detail.id;
    let fromUser = this.data.taskList.find(item => {
      return item.task.id == taskId;
    }).fromUser;
    let options = {
      taskId
    };
    Object.keys(fromUser).forEach(key => {
      if (fromUser[key]) options[key] = fromUser[key];
    })
    wx.navigateTo({
      url: app.url("/pages/issueDetail/issueDetail", options),
    })
  },
  getIsLike() {
    app.api.getIsLike(this.data.user.id)
      .then(res => {
        this.setData({
          isLike: res
        })
      })
  },
  init() {

  },
  getTask(e) {
    let status = e.detail.name;
    let reqObj = status != "ALL" ? {
      status
    } : {};
    that.active = status;
    app.api.getTask({
        ...reqObj,
        fromUserId: this.data.userId
      })
      .then(res => {
        if (res.tasks)
          res.tasks.forEach(task =>
            task.task.categoryName =
            res.categories.find(item => item.id == task.task.categoryId)
            .info
          )
        that.data = {
          taskList: res.tasks
        };
      })
  },
  isCurrentUser(toUserId) {
    app.api.getUserDetail()
      .then(res => {
        that.isCurUser = res.user.id == toUserId;
      })
  },
  getUserDetail(id) {
    return app.api.getUserDetail({
        id
      })
      .then((res) => {
        that.data = res;
        this.data.backUrl = getApp().getRoot() + res.userDetail.backgroundImage;
        this.getIsLike();
        this.isCurrentUser(res.user.id);
        wx.setStorageSync("taBackImg", getApp().getRoot() + res.userDetail.backgroundImage)
        this.getTask({
          detail: {
            name: "AWAIT_TAKEN",
            special: true
          },
        })
      })
  },
  receiveUserInfo(userInfo) {
    that.data = userInfo;
  },
})