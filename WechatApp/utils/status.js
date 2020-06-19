let status = {
  "新发布": "AWAIT_TAKEN",
  "等待提交": "AWAIT_COMMIT",
  "等待确认": "AWAIT_CONFIRM",
  "已完成": "COMPLETE",
  "请求取消": "REQ_CANCEL",
  "已取消": "CANCELED",
  "迫切的": "serious",
  "同意取消": "argee"
}
let options = [
  //非当前用户
  {
    [status["新发布"]]: [
      //可取消
      {
        name: "我要接单",
        afterStatus: status["等待提交"]
      }
    ],
    [status["等待提交"]]: [{
        name: "我已完成",
        afterStatus: status["等待确认"]
      },
      {
        name: "我要取消", //取消惩罚
        afterStatus: status["已取消"]
      }
    ],
    [status["等待确认"]]: [{
      hidden: true
    }
    ],
    [status["已完成"]]: [{
      hidden: true
    }],
    [status["已取消"]]: [{
      hidden: true,
      error: true
    }],
    [status["请求取消"]]: [
      {
        name: "同意取消",
        afterStatus: status["同意取消"],
        error: true
      },
      {
        name: "拒绝取消",
        afterStatus: status['迫切的'],
        error: true
      }
    ]
  },

  //当前用户
  {
    [status["新发布"]]: [
      //可取消
      {
        name: "我要取消",
        afterStatus: status["已取消"]
      }
    ],
    [status["等待提交"]]: [
      //可取消
      {
        name: "我要确认",
        afterStatus: status["已完成"]
      },
      {
        name: "申请退单",
        afterStatus: status["请求取消"]
      }
    ],
    [status["等待确认"]]: [{
        name: "我要确认",
        afterStatus: status["已完成"]
      },
      {
        name: "申请退单",
        afterStatus: status["请求取消"]
      }
    ],
    [status["请求取消"]]: [{
      hidden: true,
      error: true
    }],
    [status["已取消"]]: [{
      hidden: true,
      error: true
    }],
    [status["已完成"]]: [{
      hidden: true
    }]
  }
]

module.exports = {
  status,
  options
}