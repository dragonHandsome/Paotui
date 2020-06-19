import api from "../../api/api.js"
import {
  formatTime
} from "../../utils/util.js"
import Dialog from '@vant/weapp/dialog/dialog';
const app = getApp();
let that;
const DEFAULT_IMG = "https://image.weilanwl.com/img/square-3.jpg";
Page({
  data: {
    root: app.getServerUrl(),
    makeRate: false,
    star: 5,
    sendLoad: false,
    focus: false,
    replyOpt: {},
    avatar: DEFAULT_IMG,
    InputBottom: 0,
    setting: {
      active: 0,
      loading: false,
      icon: "success",
      color: "#f37b1d",
      steps: app.globalData.taskStatus,
      // buttonInfo: {
      //   type: "primary",// info warning danger
      //   text: "我要抢单"
      // }
    },
    settingError: {
      active: 0,
      icon: "success",
      color: "#f00",
      loading: false,
      steps: app.globalData.taskStatusError
    }
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    that = app.proxy(this);
    //测试
    //options.taskId = 59;
    //测试end
    that.data = {
      taskId: options.taskId
    }
    let otherData = options;
    if (options.taskId != null) {
      this.getTaskDetail(options.taskId);
      this.setData({
        otherData: options
      })
      this.getReplyList(options.taskId);
    } else {
      wx.switchTab({
        url: '/pages/index/index',
      })
    }
  },
  onShow() {
    app.initBack(this);
  },

  clearInput() {
    that.focus = false;
    that.data = {
      replyOpt: {}
    };
    that.sendLoad = false;
    that.avatar = DEFAULT_IMG;
  },
  readReply(e) {
    if (that.focus) return;
    that.focus = true;
    that.data = {
      replyOpt: {
        taskId: that.taskDetail.id,
        toUserId: e.detail.user,
        toReplyId: e.detail.reply,
        content: that.replyOpt.content
      }
    }
    that.avatar = e.detail.avatar || DEFAULT_IMG;
    that.replyuser = e.detail.replyuser || '';
    console.log(that.replyOpt);
  },
  reply(e) {
    const replyOpt = that.replyOpt;
    if (!replyOpt.taskId || !replyOpt.content) {
      wx.showToast({
        title: '发表内容不能为空!',
      })
      this.clearInput();
      return;
    }
    that.sendLoad = true;
    app.api.reply(replyOpt)
      .then(({
        data: res
      }) => {
        if (res.errCode == 0) {
          this.getReplyList(replyOpt.taskId);
        }
      })
      .then(res => {
        this.clearInput();
      })
  },

  getReplyList(taskId) {
    app.api.getReplyList({
        taskId
      })
      .then(res => {
        const replyList = that.dealReplyList(res.replyList);
        that.data = {
          replyList
        }
      })
  },
  dealReplyList(list = [{}]) {
    //item.toReplyId == 0 表示顶层回复
    const top = {};
    const childs = [];
    const replyList = [];
    //拿到第一层留言
    list.forEach((item, i) => {
      if (item.toReplyId == 0) {
        item.childs = [];
        top[item.id] = item;
      } else childs.push(item);
    })
    //剩下的就是第二层留言
    childs.forEach((item) => {
      top[item.toReplyId].childs.push(item);
    })
    for (let key in top) {
      //时间降序
      replyList.unshift(top[key])
    }

    //把完成任务的拿到第一个
    let targetI = replyList.findIndex(item => item.toReplyId == 0 && item.toUser != null)
    //找到就放到第一个
    if (targetI in replyList) replyList.unshift(replyList.splice(targetI, 1)[0]);
    console.log(replyList)
    return replyList;
  },
  changeInput(e) {
    //that.focus = false;
    that.InputBottom = e.detail.height;
    return that.replyOpt.content = e.detail.value.slice(0, 100);
  },
  inputblur() {
    that.focus = false;
    that.InputBottom = 0;
  },
  setEvaluate(e) {
    const star = e.detail;
    that.star = star;
  },
  evaluate() {
    const requestData = {
      star: that.data.star,
      id: that.data.taskDetail.id
    }
    app.api.evaluateTask(requestData)
      .then(res => {
        if (res.data.errCode == 0) {
          wx.showToast({
            title: '评分成功',
          })
          setTimeout(wx.navigateBack, 500);
        } else {
          wx.showToast({
            title: "评分失败，服务器繁忙",
          })
          setTimeout(wx.navigateBack, 500);
        }
      })
      .catch(res => {
        wx.showToast({
          title: '检查您的网络。。。',
        })
      })
      .then(res => that.makeRate = false)
  },
  confirmUpdate(e) {
    that.loading = true;
    Dialog.confirm({
        title: '确定吗，您要',
        message: e.detail.name
      })
      .then(() => {
        wx.showLoading({
          title: '更新中...',
        })
        console.log("======")
        this.updateTaskStatus(e)
          .then(res => {
            return app.dealResponse(res, res => {
              if (e.detail.status != "COMPLETE") setTimeout(wx.navigateBack, 1000);
              else that.makeRate = true
              that.loading = false;
              wx.hideLoading();
              wx.showToast({
                title: 'success',
              })
            })
          })
          .catch(() => {
            wx.showToast({
              title: '更新失败，服务器繁忙',
            })
          })
      })
      //取消不干啥
      .catch(() => {})
      //最后取消loading状态
      .then(() => {
        that.loading = false;
        wx.hideLoading();
      })
  },
  updateTaskStatus(e) {
    let _this = this;
    const status = e.detail.status;
    if(status === 'serious') {
      return that.rejectCancel();
    }
    if(status === 'argee') {
      return that.argeeCancelTask();
    }

    //服了下次一个写一个方法 
    console.log(status)
    if (this.data.taskDetail.status === "AWAIT_TAKEN" ||
      status !== "CANCELED" && status !== "REQ_CANCEL" &&
      status !== "AWAIT_CONFIRM") {
      return this.updateTaskStatusNormal(status)
    } else {
      return app.getUserDetail()
        .then(({
          user
        }) => {

          if(user.id == this.data.taskDetail.toUserId && status === "AWAIT_CONFIRM") {
            return that.toCompletePage();
          }

          //当前用户是发起者 取消需要申请
          if (user.id == this.data.taskDetail.fromUserId && status === "REQ_CANCEL") {
            return that.toCancelPage();
          }
          //当前用户是接单者
          if (user.id == this.data.taskDetail.toUserId  && status === "CANCELED") {
            return Dialog.confirm({
                title: '确定取消?',
                message: '取消将影响您的账户。'
              })
              .then(res => {
                return that.cancelTaskByAcceptor();
              })
          }
        })
    }
  },
  rejectCancel(){
    const taskId = this.data.taskDetail.id;
    return app.api.rejectCancel({taskId});
  },
  argeeCancelTask(){
    const taskId = this.data.taskDetail.id;
    return app.api.argeeCancelTask({taskId});
  },
  toCompletePage(){
    wx.navigateTo({
      url: '/pages/issueDetail/commit/commit?id=' + this.data.taskDetail.id
    })
  },
  toCancelPage() {
    wx.navigateTo({
      url: '/pages/issueDetail/cancel/cancel?id=' + this.data.taskDetail.id
    })
  },
  cancelTaskByAcceptor() {
    const taskId = this.data.taskDetail.id;
    if (taskId != null) {
      return app.api.cancelTaskByAcceptor(taskId);
    }
  },
  updateTaskStatusNormal(status) {
    return app.api.updateTaskStatus({
      id: this.data.taskId,
      status: status
    })
  },
  setButtonInfo(fromId, curId, status) {
    //抢单条件
    let statusList = app.globalData.taskStatus;
    let buttonInfo = [];
    //设置订单状态的变化
    let statusData = app.globalData.status;
    let isCurrentUserToIndex = +(fromId == curId);
    buttonInfo = statusData.options[isCurrentUserToIndex][status];
    console.log(buttonInfo);
    if (buttonInfo[0].error == true) {
      that.data = {
        setting: that.data.settingError
      };
    }
    this.setData({
      "setting.buttonInfo": buttonInfo,
      //
      "setting.curUserId": curId
    })
  },
  caina(e) {
    const caiNaOpt = {
      taskId: e.detail.task,
      caiNaUserId: e.detail.touser,
      replyId: e.detail.reply
    };
    console.log(e.detail, caiNaOpt)
    Dialog.confirm({
        title: '确定采纳?',
        message: "采纳后,任务完成,赏金发放给被采纳用户。"
      })
      .then(() => {
        app.api.caina(caiNaOpt)
          .then(res => {
            if (res.data.errCode == 0) {
              wx.showToast({
                title: '采纳成功'
              })
              this.getTaskDetail(that.taskId);
              this.getReplyList(that.taskId);
            }
          })
      })
  },
  dealTaskDetail(taskDetail) {
    let {
      steps
    } = this.data.setting;
    let active = steps.findIndex(cur => cur.status == taskDetail.status);
    if (active != -1) {
      this.setData({
        "setting.active": active
      })
    }
    taskDetail.createTime = taskDetail.createTime.split("T")[0]
    taskDetail.rewardMoney = taskDetail.rewardMoney / 100;
    //主要为了让img排后面 不过没加排序字段 以id为顺序
    //taskDetail.wechatOptions.sort((a, b) => a.id - b.id);
    //处理图片路径 图片路径在数据库中以;分割
    let target = taskDetail.wechatOptions.find(cur => cur.type == "IMAGES");
    if (target != null) {
      //配置图片字符串转图片数组
      target.value = target.value.split(";").map(cur => that.data.root + cur);
      //添加属性imgInfo指向图片信息 便于遍历
      taskDetail.imgInfo = target;
    }
    that.data = {
      taskDetail
    }
  },
  setCategoryName(id) {
    app.api.getIssueCategory()
      .then(res => {
        let categoryName =
          res.find(item => id == item.id)
          .info;
        this.setData({
          "otherData.categoryName": categoryName
        })
      })
  },
  getTaskDetail(taskId) {
    app.api.getTaskDetail(taskId)
      .then(resp => {
        let fromUserId = resp.fromUserId;
        let status = resp.status;
        this.setCategoryName(resp.categoryId);
        app.api.getUserDetail()
          .then(res => {
            let currentUserId = res.user.id;
            //设置当前用户能对订单干嘛
            that.setButtonInfo(fromUserId, currentUserId, status);
            that.dealTaskDetail(resp);
          })
        app.api.getUserDetail({
            id: fromUserId
          })
          .then(res => {
            this.setData({
              fromUser: res
            });
          })
        resp.toUserId &&
          app.api.getUserDetail({
            id: resp.toUserId
          })
          .then(res => {
            this.setData({
              toUser: res
            })
          })
      })
      .catch(error => {
        wx.showToast({
          title: '服务器繁忙'
        })
      })
  }
})