let socketOpen = false
import {
  token
} from "../utils/wxApi";
let app;
let NOTIFY_KEY = "notify_key";
let curUserId;
let socket;
let socketMsgQueue = []

export function send(msg) {
  if(typeof msg === 'object') {
    msg = JSON.stringify(msg)
  }
  return new Promise((res, rej) => {
    if (socketOpen) {
      wx.sendSocketMessage({
        data: msg,
        success: res
      })
    } else {
      socketMsgQueue.push(msg)
      res()
    }
  })
}

function notifySort(a, b) {
  let max = -(+b.isRead - +a.isRead) * 10;
  let diff = new Date(b.createdTime.replace(/-/g, "/")).getTime() - new Date(a.createdTime.replace(/-/g, "/")).getTime();
  let min = diff > 0 ? 1 : diff < 0 ? -1 : 0;
  console.log(b.createdTime + a.createdTime + diff)
  return (max + min);
}

function getNewsListSort() {
  let notifies = getNewsNotify();
  const resultList = [];
  //遍历每个用户 系统为用户0
  Object.keys(notifies).forEach(key => {
    let noReadCount = 0;
    let notifyKeys = Object.keys(notifies[key]);
    //获取该用户最新的消息
    let mostNewId = notifyKeys[notifyKeys.length - 1];
    //浅拷贝
    let mostNewNotify = Object.assign({}, notifies[key][mostNewId]);
    //计算noReadCount
    notifyKeys.forEach(i => {
      if (!notifies[key][i].isRead) {
        ++noReadCount;
      }
    })
    if (mostNewNotify.isCurUser) {
      mostNewNotify.fromUser = mostNewNotify.toUser;
      mostNewNotify.fromUserId = mostNewNotify.toUserId;
    }
    //格式化时间
    resultList.push({
      ...mostNewNotify,
      noReadCount,
    })
  })
  resultList.sort(notifySort)
  resultList.forEach(cur => cur.createdTime =
    formatTime(cur.createdTime))
  return resultList;
}
const timeMsg = ["", "昨天", "前天", "三天前", "一周前"]

function formatTime(time, isNeadTime = false) {
  let date = new Date(time);
  let now = new Date();
  let dateDiff = now.getDate() - date.getDate();
  if (date.getFullYear() != now.getFullYear() || dateDiff > 3) {
    return time.split(" ")[0] + isNeadTime ? time.split(" ")[1] : '';
  }
  return timeMsg[dateDiff] + time.split(" ")[1];
}

function getNewsNotify() {
  let notifies = wx.getStorageSync(NOTIFY_KEY);
  if (notifies == "") return {};
  else return JSON.parse(notifies);
}

function getUserNotifies(userId) {
  let notifies = getNewsNotify(NOTIFY_KEY);
  let userNotifies = notifies[userId];
  const result = []
  if (userNotifies) {
    Object.keys(userNotifies).forEach(key => {
      const opt = {
        ...userNotifies[key]
      }
      opt.createdTime = formatTime(opt.createdTime, true)
      //
      if (opt.fromUserId == curUserId) {
        opt.isCurUser = true;
      }
      result.push(opt)
    })
  }
  return result;
}

function getNoReadCount() {
  const NoReadCount = getNewsListSort().reduce((beforeSum, b) => {
    return beforeSum + b.noReadCount
  }, 0)
  console.log(NoReadCount + "noreadcount")
  return NoReadCount;
}
//isSelf 
function addNewsNotify(notify, isSelf = false) {
  if(notify == null || notify.id + '' == 'null' || notify.id + '' === 'undefined') {
    console.dir(notify);
    console.log("插入失败notify")
    return;
  }
  let notifies = getNewsNotify();
  //存成对象形式
  console.log(notify)
  let key = isSelf ? notify.toUserId : notify.fromUserId;
  if (!notifies[key]) {
    notifies[key] = {};
  }
  notifies[key][notify.id] = notify;
  wx.setStorageSync(NOTIFY_KEY, JSON.stringify(notifies));
  console.log(notifies)
  console.log(getNewsListSort())
}

function removeNewsNotify(fromUserId, notifyId) {
  let notifies = getNewsNotify();
  delete notifies[fromUserId][notifyId];
  wx.setStorageSync(NOTIFY_KEY, JSON.stringify(notifies));
}

function readNotify(fromUserId, notifyId) {
  let notifies = getNewsNotify();
  if (notifies[fromUserId] && notifies[fromUserId][notifyId]) {
    notifies[fromUserId][notifyId].isRead = true;
    console.log("set" + fromUserId + "-" + notifyId + " isRead true")
    wx.setStorageSync(NOTIFY_KEY, JSON.stringify(notifies));
  }
}

function readByIds(ids = []) {
  let notifies = getNewsNotify();
  Object.keys(notifies).forEach(uid => {
    Object.keys(notifies[uid]).forEach(id => {
      if (ids.includes(+id)) {
        notifies[uid][id].isRead = true;
      }
    })
  })
  console.log("read success:")
  console.log(ids)
  wx.setStorageSync(NOTIFY_KEY, JSON.stringify(notifies));
}

function notifyStart(ROOT, userId) {
  getApp().joinSignOut(wx.closeSocket);
  if (socketOpen == false) {
    curUserId = userId;
    startWebsocket(ROOT, userId);
  }
}

function startWebsocket(ROOT, userId) {
  app = getApp();
  NOTIFY_KEY = userId + "notify_key";

  function getObj (res) {
    if(typeof res === 'string') {
      return res = JSON.parse(res)
    } else return res
  }
  wx.onSocketMessage(function (res) {
    let message = getObj(res.data);
    let notify = getObj(message.data);
    notify.content = getObj(notify.content) 
    console.log("收到");
    console.log(notify)
    if (message.errCode == 0) {
      addNewsNotify(notify);
      //123是自己的
    } else if (message.errCode == 123) {
      notify.isRead = true
      notify.isCurUser = true
      addNewsNotify(notify, true);
    }
    notifyUpdate();
  })

  function notifyUpdate() {
    app.getNotifyPages().forEach(page => {
      if (page != null && page.updateNotify instanceof Function) {
        page.updateNotify();
      }
    })
    let pages = getCurrentPages();
    pages.forEach(page => {
      let count = app.getNoReadCount();
      if (page != null && page.getTabBar() != null)
        page.getTabBar().setData({
          newsCount: count
        })
    })
  }
  wx.onSocketOpen(function (res) {
    socketOpen = true
    for (let i = 0; i < socketMsgQueue.length; i++){
      send(socketMsgQueue[i])
    }
    socketMsgQueue = []
  })



  let reTryNum = 20;
  wx.onSocketClose(function (error) {
    if (error &&　error.code == 0){
      return;
    }
      reConect();
  })
  getApp().joinSignOut(() => wx.closeSocket({
    code: 0,
  }))

  function reConect() {
    socketOpen = false;
    socket = null;
    if (reTryNum-- > 0) {
      console.log("重新连接")
      connect()
    } else {
      setTimeout(connect, 5000)
    }
  }
  wx.onSocketError(function (error) {
    if (error &&　error.code == 0){
      return;
    }
    reConect();
  })

  function connect() {
    if (!app.isLogin()) return;
    let socket = wx.connectSocket({
      url: app.getRoot().replace(/http|https/, 'ws') + '/websocket/notify/' + userId,
      header: {
        'content-type': 'application/json',
        'Authorization': token.getTokenType() + " " + token.getToken()
      },
      method: 'post',
      success: res => {
        console.log('连接成功')
      },
      fail: err => {
        socket.close()
      }
    })
  }
  connect()

}

import util from "../utils/util";

function createChatNotify(fromUserId, toUserId, content, id) {
  getApp().getUserDetail()
    .then(res => {
      const notify = {
        action: 'call',
        content,
        fromUserId,
        toUserId,
        createdTime: util.formatTime(new Date()),
        fromUser: res,
        type: 'CHAT',
        isRead: true,
        id,
      };
      addNewsNotify(notify);
    })
}
module.exports = {
  ...module.exports,
  notifyStart,
  getNewsArr: getNewsListSort,
  getNews: getNewsNotify,
  add: addNewsNotify,
  remove: removeNewsNotify,
  read: readNotify,
  getUser: getUserNotifies,
  getNoReadCount,
  readByIds,
  createChatNotify
}