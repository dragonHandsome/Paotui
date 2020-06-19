// pages/my/my.js
const app = getApp();
import api from "../../api/api.js";
import {
  status
} from "../../utils/status.js";
import Dialog from '@vant/weapp/dialog/dialog';
import {
  to,
  token
} from "../../utils/wxApi.js"
let that;
let statusInfo = {};
//初始化statusInfo key value 互换以便
Object.keys(status).forEach(key => statusInfo[status[key]] = key);
let taskStatus = [{
  status: "ALL",
  text: '全部'
}]
//我发布的任务
taskStatus = taskStatus.concat(app.globalData.taskStatus);
//我接收的任务 里没有新发布
let receiveTaskStatus = [].concat(taskStatus)
//删掉新发布
receiveTaskStatus.splice(1, 1)
Page({

  /**
   * 页面的初始数据
   */
  data: {
    active: "ALL",
    active2: "ALL",
    avatar: "",
    root: app.getServerUrl(),
    taskStatus,
    nextPage: 1,
    nextPage2: 1,
    taskList: [],
    receiveTaskList: [],
    receiveTaskStatus,
    titleActive: true,
    // backgroundDefault: "https://img.yzcdn.cn/vant/cat.jpeg"
  },
  onLoad: function(options) {
    that = app.proxyPageTakeAny(this);
    //app.registerGetUserInfo(this);
    // app.updated.my = false;
  },
  onShow() {
    that.switchTab();
    that.init();
  },
  exit(){
    app.globalData.state = {}
    app.signOut();
    //测试
    //wx.clearStorageSync();
    wx.reLaunch({
      url: '/pages/login/login',
    })
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
  },
  switchTab() {
    if (typeof this.getTabBar === 'function' &&
      this.getTabBar()) {
      this.getTabBar().setData({
        selected: 3
      })
    }
  },
  switchTitle() {
    that.titleActive = !that.data.titleActive;
  },
  goTaskDetail(e) {
    console.log(e)
    let taskId = e.detail.id;
    let fromUser = this.data[e.currentTarget.dataset.name].find(item => {
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
  init() {
    this.initAvatar();
    app.getUserDetail(this)
      .then(() => {
        this.data.receiveTaskList = [];
        this.data.nextPage2 = 1;
        this.nextPage2()
        this.data.taskList = [];
        this.data.nextPage = 1;
        this.nextPage()
      })
    app.initBack(this);
    
  },
  initAvatar(){
    let avatarS = wx.getStorageSync("avatar")
    if (avatarS != "")
      that.avatarS = avatarS;
  },
  entrySetting() {
    wx.navigateTo({
      url: '/pages/config/config',
    })
  },
  nextPage2() {
    let nextPage = this.data.nextPage2;
    if (nextPage < 0) return;
    that.loading2 = true;
    let outCome = this.getReceiveTask({
      detail: {
        name: this.data.active2
      }
    })
    if (outCome) {
      outCome.then(() => {
        this.data.nextPage2 += 1;
        that.loading2 = false
      }).catch(error => {
        that.loading2 = false
        this.data.nextPage2 = -1;
      })
    } else {
      that.loading2 = false
      wx.showToast({
        title: '服务器繁忙！',
      })
    }
  },
  nextPage() {
    let nextPage = this.data.nextPage;
    if (nextPage < 0) return;
    that.loading1 = true;
    let outCome = this.getTask({
      detail: {
        name: this.data.active
      }
    })
    if (outCome) {
      outCome.then(() => {
          this.data.nextPage += 1;
          that.loading1 = false;
        }).catch(error => {
          that.loading1 = false
          this.data.nextPage = -1;
        })
        .then(() => that.loading1 = false)
    } else {
      that.loading1 = false
      wx.showToast({
        title: '服务器繁忙！',
      })
    }
  },
  getReceiveTask(e) {
    let status = e.detail.name;
    let reqObj = status != "ALL" ? {
      status
    } : {};
    //表示是否未改变选择状态
    let isConcat = that.data.active2 == status
    that.active2 = status;
    let page = 0;
    if (isConcat) {
      page = this.data.nextPage2;
      if(page < 0) return;
    }
    if (!this.data.user) return;
    return app.api.getTask({
        ...reqObj,
        page,
        toUserId: this.data.user.id,
        special:true
      })
      .then(res => {
        if (res.tasks)
          res.tasks.forEach(task => {
            task.task.categoryName =
              res.categories.find(item => item.id == task.task.categoryId)
              .info;
            task.task.status = statusInfo[task.task.status];
          })
        let receiveTaskList = res.tasks;
        if (isConcat) receiveTaskList = that.data.receiveTaskList.concat(receiveTaskList)
        that.data = {
          receiveTaskList
        };
      })
      .catch(error => {
        if (!isConcat)this.clearTaskList('receiveTaskList');
        else throw new Error("没下页了");
      })
  },
  clearTaskList(key){
    this.setData({
      [key]:[]
    })
  },
  getTask(e) {
    let status = e.detail.name;
    let reqObj = status != "ALL" ? {
      status
    } : {};
    //未切换状态则合并
    let isConcat = that.data.active == status
    that.active = status;
    let page = 0;
    if (isConcat) {
      page = this.data.nextPage;
      if (page < 0) return;
    }
    if (!this.data.user) return;
    return app.api.getTask({
        ...reqObj,
        page,
        fromUserId: this.data.user.id
      })
      .then(res => {
        if (res.tasks)
          res.tasks.forEach(task => {
            task.task.categoryName =
              res.categories.find(item => item.id == task.task.categoryId)
              .info;
            task.task.status = statusInfo[task.task.status];
          })
        let taskList = res.tasks;
        if (isConcat) taskList = that.data.taskList.concat(taskList)
        that.data = {
          taskList
        };
      })
      .catch(error => {
        if (!isConcat) {
          that.data = {
            taskList: []
          }
        }else throw new Error("没下页了");
      })
  },
  getUserDetail() {
    return app.api.getUserDetail((data) => {
      that.data = data;
    });
  },
  receiveUserInfo(userInfo) {
    that.data = userInfo;
  },
})