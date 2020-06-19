const app = getApp();
let that;
import {status} from "../../utils/status.js"
const defaultReqOpt = {
  title: '',
  categoryId: '',
  status: status['新发布'],
  special: true,
  page: 1
}
Page({


  data: {
    root: app.getRoot(),
    reqOpt: {...defaultReqOpt},
    taskList:[],
    isCard: false,
    avatarUrl: app.globalData.avatarUrl
  },

  onLoad: function (options) {
    that = app.proxy(this);
    that.categoryId = options.id;
    this.updatePage();
    this.getNextPage();
  },
  onShow(){
  },
  onPullDownRefresh(){
    console.log("refresh")
    this.updatePage();
    this.getNextPage();
  },
  updatePage(){
    defaultReqOpt.categoryId = that.categoryId;
    this.data.reqOpt = {...defaultReqOpt}
    this.setData({
      "reqOpt.title": ''
    })
    this.clearList()
    app.initBack(this);
  },
  clearList(){
    that.data = {taskList:[]}
  },
  getNextPage(){
    this.getTask();
  },
  handleFocus(){
  },
  handleInput(e){
    console.log(e)
    let title = e.detail.value.slice(0, 10)
    that.reqOpt.title = e.detail.value
    return title
  },
  handleSearch() {
    that.reqOpt.page = 1;
    this.clearList()
    this.getNextPage();
  },
  getTask() {
    const reqOpt = { ...that.reqOpt };
    if( reqOpt.categoryId == 3 ) {
      delete reqOpt.status
    }
    if(reqOpt.title && !reqOpt.title.length) {
      delete reqOpt.title
    }
    app.api.getTask(reqOpt)
      .then(res => {
        that.reqOpt.page += 1;
        that.data = {
          taskList: that.taskList.concat(res.tasks),
          category: res.categories.find(item => item.id == reqOpt.categoryId)
        }
      })
      .catch(error => {

      })
  },
  toTa(e) {
    let id = e.currentTarget.dataset.id;
    if (id == null) return;
    wx.navigateTo({
      url: getApp().parseUrl("/pages/ta/ta", {id}),
    })
  },
  toTaskDetail(e){
    let index = e.currentTarget.dataset.index;
    if(index == null) return;
    let task = this.data.taskList[index];
    let category = this.data.category;
    let params = {
      taskId: task.task.id,
      ...task.fromUser
    }
    wx.navigateTo({
      url: getApp().parseUrl("/pages/issueDetail/issueDetail", params),
    })
  },
  onReachBottom()
  {
    this.getNextPage()
  }
})