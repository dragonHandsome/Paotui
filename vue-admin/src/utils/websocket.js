export let ws = null;
let isOpen = false;
let messageQueue = [];
let NOTIFY_KEY;
let handleOnMessage = null
let store = null
const wx = {
  setStorageSync,
  getStorageSync
}

function setStorageSync(key, value) {
  localStorage.setItem(key, value);
}

function getStorageSync(key) {
  return localStorage.getItem(key) || ''
}

function getObj(res) {
  if (typeof res === 'string') {
    return res = JSON.parse(res)
  } else return res
}

function handleMessage(res) {
  let message = getObj(res.data);
  let notify = getObj(message.data);
  notify.content = getObj(notify.content)
  if (message.errCode == 0) {
    addNewsNotify(notify);
    //123是自己的
  } else if (message.errCode == 123) {
    notify.isRead = true
    notify.isCurUser = true
    addNewsNotify(notify, true);
  }
  handleOnMessage(message, notify);
}

function notifySort(a, b) {
  let max = -(+b.isRead - +a.isRead) * 10;
  let diff = new Date(b.createdTime.replace(/-/g, "/")).getTime() - new Date(a.createdTime.replace(/-/g, "/")).getTime();
  let min = diff > 0 ? 1 : diff < 0 ? -1 : 0;
  console.log(b.createdTime + a.createdTime + diff)
  return (max + min);
}

export function getNewsListSort() {
  let notifies = getNewsNotify();
  const resultList = [];
  //遍历每个用户 系统为用户0
  Object.keys(notifies).forEach(key => {
    let noReadCount = 0;
    let notifyKeys = Object.keys(notifies[key]);
    //获取该用户最新的消息
    let mostNewId = notifyKeys.sort((a, b) => {
      return +new Date(notifies[key][b].createdTime) - +new Date(notifies[key][a].createdTime)
    })[0]
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
  const date = new Date(time);
  const now = new Date();
  let dateDiff = now.getDate() - date.getDate();
  if (date.getFullYear() !== now.getFullYear() || dateDiff > 3) {
    return time.split(" ")[0] + isNeadTime ? time.split(" ")[1] : '';
  }
  return timeMsg[dateDiff] + time.split(" ")[1];
}

function getNewsNotify() {
  let notifies = wx.getStorageSync(NOTIFY_KEY);
  if (notifies === "") return {};
  else return JSON.parse(notifies);
}

export function getUserNotifies(userId) {
  let notifies = getNewsNotify(NOTIFY_KEY);
  let userNotifies = notifies[userId];
  const result = []
  if (userNotifies) {
    Object.keys(userNotifies).forEach(key => {
      const opt = {
        ...userNotifies[key]
      }
      // opt.createdTime = formatTime(opt.createdTime, true)
      //
      if (opt.fromUserId === store.getters.id) {
        opt.isCurUser = true;
      }
      result.push(opt)
    })
    result.sort((a, b) => {
      return +new Date(a.createdTime) - +new Date(b.createdTime)
    })
  }
  return result;
}

function getNoReadCount() {
  const NoReadCount = getNewsListSort().reduce((beforeSum, b) => {
    return beforeSum + b.noReadCount
  }, 0)
  return NoReadCount;
}

export function addNewsNotify(notify, isSelf = false) {
  if (notify == null || notify.id + '' == 'null' || notify.id + '' === 'undefined') {
    console.log("插入失败notify")
    return;
  }
  const notifies = getNewsNotify();
  //存成对象形式
  const key = isSelf ? notify.toUserId : notify.fromUserId;
  if (!notifies[key]) {
    notifies[key] = {};
  }
  notifies[key][notify.id] = notify;
  wx.setStorageSync(NOTIFY_KEY, JSON.stringify(notifies));
}

export function removeNewsNotify(fromUserId, notifyId) {
  let notifies = getNewsNotify();
  delete notifies[fromUserId][notifyId];
  wx.setStorageSync(NOTIFY_KEY, JSON.stringify(notifies));
}

export function readNotify(fromUserId, notifyId) {
  let notifies = getNewsNotify();
  if (notifies[fromUserId] && notifies[fromUserId][notifyId]) {
    notifies[fromUserId][notifyId].isRead = true;
    console.log("set" + fromUserId + "-" + notifyId + " isRead true")
    wx.setStorageSync(NOTIFY_KEY, JSON.stringify(notifies));
  }
}

export function readByIds(ids = []) {
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

export async function connect(rstore, handle = () => {}) {
  store = store || rstore
  const {
    getters
  } = store
  if (!getters.token) {
    ws = null
    return
  }
  handleOnMessage = handle

  NOTIFY_KEY = getters.id + "notify_key";

  window.ws = ws = window.ws || new WebSocket(`ws://localhost/websocket/notify/-${getters.id}?token=${getters.token}&userType=admin`);
  
  console.log(ws)

  ws.onopen = handleOpen

  ws.onclose = handleCloseOrError

  ws.onerror = handleCloseOrError

  ws.onmessage = handleMessage
}

export function send(message) {
  if (!isOpen) {
    messageQueue.push(message)
    return
  }
  ws.send(message)
}

function handleOpen() {
  console.log("connect success")
  isOpen = true
  messageQueue.forEach(msg => {
    send(msg)
  })
}

function handleCloseOrError() {
  window.ws = ws = null
  isOpen = false
  console.log("re connect")
  setTimeout(() => connect(store, handleOnMessage), 2000);
}

window.test = module.exports
