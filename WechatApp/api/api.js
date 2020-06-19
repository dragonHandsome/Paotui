

import {
  fetch,
  to,
  SERVER_BASE_URL
} from "../utils/wxApi.js";
const URI = {
  USER: "/user",
  USER_DETAIL: "/user/detail",
  UPLOAD_FILE:"/uploadFile",
  TASK: "/task",
  TASK_STATUS: "/task/status",
  TASK_DETAIL: "/task/detail",
  TASK_CATEGORY: "/task/category",
  TASK_FORM: "/task/form",
  USERLIST: "/userList",
  USERLIST_LIKE: "/userList/like",
  ACCOUNT:"/account",
  REPLY:"/reply",
  TASK_CAINA:"/task/caina",
  RANK_LIST:'/rankList',
  NOTIFY_READ: "/notify/read",
  NOTIFY_TASK: "/notify/task",
  NOTIFY_CHAT: "/notify/chat",
  TASK_CANCEL_BAD: "/task/cancelBad",
  TASK_CANCEL: "/task/wantCancel",
  TASK_CERT: "/task/cert",
  TASK_REJECT_CANCEL: "/task/reject",
  TASK_ARGEE: "/task/agree"
}
/**
 * 上传code userinfo:{...}
 * 返回 token
 */

const signin = (code) => fetch.POST("/auth")({code})

const login = (form) => fetch.POST("/signin")(form);

const register = (signUpRequest) => fetch.POST("/signup")(signUpRequest);
/**
 * 
 */

export const rejectCancel = (opt) => {
  return fetch.GET(URI.TASK_REJECT_CANCEL)(opt);
}

export const argeeCancelTask = (opt) => {
  return fetch.GET(URI.TASK_ARGEE)(opt);
}

export const applyCancelTask = (opt) => {
  opt = Object.assign({}, opt)
  opt.images = JSON.stringify(opt.images);
  return fetch.POST(URI.TASK_CANCEL)(opt);
}

 export const submitCertification = (opt) => {
  opt = Object.assign({}, opt)
  opt.images = JSON.stringify(opt.images);
  return fetch.POST(URI.TASK_CERT)(opt);
}

export const cancelTaskByAcceptor = (taskId) => fetch.GET(URI.TASK_CANCEL_BAD)({taskId});

export const readAllNotSpecialNews = (opt) => fetch.POST(URI.NOTIFY_READ)(opt.ids)

export const chat = (opt) => fetch.PUT(URI.NOTIFY_CHAT)(opt);

const dealWasIssuedTask = (opt) => fetch.GET(URI.NOTIFY_TASK)(opt)

const getRankList = () => fetch.GET(URI.RANK_LIST)();
 
const caina = (caiNaOpt) => fetch.POST(URI.TASK_CAINA)(caiNaOpt);

const reply = (replyOpt) => fetch.PUT(URI.REPLY)(replyOpt)

const getReplyList = (requestOpt) => fetch.GET(URI.REPLY)(requestOpt)

const addMoneyToNative = (money) => fetch.POST(URI.ACCOUNT)({money: money})

const getNativeAccount = () => fetch.GET(URI.ACCOUNT)()

const getLikeUserList = () => fetch.GET(URI.USERLIST)()

const updateTogglelike = (likeWhoId) => fetch.POST(URI.USERLIST_LIKE)({ toUserId: likeWhoId})

const getIsLike = (likeWhoId) => fetch.GET(URI.USERLIST_LIKE)({ id: likeWhoId})

const updateUser = (userInfo) => fetch.POST(URI.USER)(userInfo)

const updateUserDetail = (userDetail) => fetch.POST(URI.USER_DETAIL)(userDetail)

const getUserDetail = (data) => fetch.GET(URI.USER_DETAIL)(data)

const uploadFile = (option) => fetch.UPLOAD(URI.UPLOAD_FILE)(null, option);

const getIssueCategory = () => fetch.GET(URI.TASK_CATEGORY)()

const getTaskForm = (categoryId) => fetch.GET(URI.TASK_FORM)({categoryId})

const issueTask = (taskForm) => fetch.PUT(URI.TASK)(taskForm)

const getTask = (taskCondition) => fetch.GET(URI.TASK)(taskCondition);

const getTaskDetail = (taskId) => fetch.GET(URI.TASK_DETAIL)({taskId})

const updateTask = (taskCondition) => fetch.POST(URI.TASK)(taskCondition)

const evaluateTask = (taskCondition) => fetch.POST(URI.TASK)({ id: taskCondition.id, star: taskCondition.star})

const updateTaskStatus = (task) => fetch.POST(URI.TASK_STATUS)({ 
  status: task.status,
  id: task.id
})

const getTaskTypes = () => {return {
  taskStatusList: [{ id: 0, name: "待接单" },
    { id: 1, name: "待确认" },
    { id: 2, name: "已完成" },
    { id: 3, name: "已过期" }
    ]
  }
}

module.exports = {
  ...module.exports,
  signin,
  updateUser,
  getTask,
  getTaskTypes,
  getTaskDetail,
  updateUserDetail,
  uploadFile,
  getUserDetail,
  getIssueCategory,
  getTaskForm,
  issueTask,
  updateTask,
  updateTaskStatus,
  register,
  login,
  updateTogglelike,
  getIsLike,
  evaluateTask,
  getLikeUserList,
  addMoneyToNative,
  getNativeAccount,
  reply,
  getReplyList,
  caina,
  getRankList,
  dealWasIssuedTask
}

