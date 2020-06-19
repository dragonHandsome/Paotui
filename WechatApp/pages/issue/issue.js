import api from "../../api/api.js";
const app = getApp()
let that;
Page({

  data: {
    StatusBar: app.globalData.StatusBar,
    CustomBar: app.globalData.CustomBar,
    addressInfo:{},
    root: app.getRoot(),
    fileList: [],
    rewardMoney: 0,
    images:[],
    loading:false,
    autosize: { maxHeight: 100, minHeight: 50 }
  },
  onLoad: function (options) {
    let categoryId = options.id;
    let toUserId = options.toUserId;
    this.data.toUserId = toUserId;
    that = app.proxy(this);
    that.getTaskForm(categoryId)
  },
  onShow(){
    app.initBack(this);
  },
  getTaskForm(categoryId) {
    return api.getTaskForm(categoryId)
      .then(res => {
        app.dealResponse(res, res => {
          let toUserId = this.data.toUserId;
          if (toUserId) res.toUserId = toUserId;
          that.data = { taskForm: res }
        })
      })
  },
  issueTask(){
    //验证表单
    if (!this.validateForm()) return;
    that.loading = true;
    let success = false;
    api.issueTask(this.issueTaskData())
    .then(res => {
      if(res.data.errCode== 0){
        wx.showToast({
          title: '发布成功！',
        })
        success = true;
        setTimeout(()=>{
          wx.switchTab({
            url: '/pages/index/index',
          })
          that.loading = false;
        },800)
      }else {
        wx.showToast({
          title: res.data.msg,
        })
        that.loading = false;
      }
    })
    .catch(error => {
      wx.showToast({
        title: '连接失败!',
      })
      that.loading = false;
    })
  },
  issueTaskData(){
    return this.data.taskForm;
  },
  validateForm(){
    return true;
  },
  //失焦执行
  bindblur(e){
    let dataset = e.currentTarget.dataset;
    let value = e.detail.value;
    //如果value为空则返回
    console.log(e)
    if(+value == 0) return;
    if (dataset.prop){
      if (dataset.prop == "rewardMoney") {
        value = isNaN(+value) ? 0 : (+value).toFixed(2);
        this.setData({ rewardMoney: value});
        //数据库存以分为单位的整型 所以乘以100
        value *= 100;
        
      }
      this.data.taskForm[dataset.prop] = value;
    }
    else if (dataset.name){
      let target = this.data.taskForm.wechatOptions.find(option => {
        return option.name == dataset.name;
      })
      target && (target.value = value);
    }
  },
  deleteImg(e){
    console.log(this.data.images);
    let index = e.detail.index;
    let { images, fileList} = this.data;
    images = images.filter((cur, i) => i != index );
    fileList = fileList.filter((cur, i) => i != index);
    this.updateImg(images);
    this.setData({
      fileList
    })
    console.log(this.data.taskForm);
  },
  updateImg(images){
    this.data.images = images;
    let {taskForm: { wechatOptions } } = this.data;
    let imgValue = images.join(";");
    let target = wechatOptions.find(option => {
      return option.type == "IMAGES"
    })
    target && (target.value = imgValue);
  },
  afterRead(e){
    let _this = this;
    const { file } = e.detail;
    // 当设置 mutiple 为 true 时, file 为数组格式，否则为对象格式
    api.uploadFile({
      filePath: file.path,
      name: 'file'
    })
      .then(res => {
        app.dealResponse(res, imgUrl => {
          let { fileList, images} = _this.data;
          //加入到组件列表中展示
          fileList.push({
            url: _this.data.root + imgUrl,
            isImage: true
          });
          console.log(fileList)
          images.push(imgUrl);
            _this.setData({
              fileList
            })
          //更新需上传的imgvalue ;分割
          _this.updateImg(images);
        })
      })
      .catch(res => {
        console.log("fail")
        wx.showToast({
          title: '上传失败',
        })
      })
  }
})