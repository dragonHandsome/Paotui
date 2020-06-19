import api from "../../../api/api.js"
import { formatTime} from "../../../utils/util.js"
const app = getApp();
let that;
const certificationDefault = {
  taskId: '',
  content: '',
  images: [],//上传时json
}
Page({

  /**
   * 页面的初始数据
   */
  data: {
    root: app.getServerUrl(),
    fileList:[],
    certification:certificationDefault,
    loading: false
  },
  bindInput(e){
    console.log(e);
    let prop = e.currentTarget.dataset.prop;
    this.setData({
      ["certification." + prop]: e.detail.value
    })
  },
  dataConfirm(e){
    let birthday = formatTime(new Date(e.detail)).split(" ")[0];
    this.setData({
      "userDetail.birthday": birthday
    })
    that.dateHidden = true;
  },
  deleteImg(e) {
    const fileList = this.data.fileList;
    that.certification.images.splice(e.detail.index, 1);
    fileList.splice(e.detail.index, 1);
    that.data = {fileList}
  },
  afterRead(event) {
    let _this = this;
    let prop = event.currentTarget.dataset.prop;
    const { file } = event.detail;
    // 当设置 mutiple 为 true 时, file 为数组格式，否则为对象格式
    api.uploadFile({
      filePath: file.path,
      name: 'file'
    })
      .then(res => {
        let serverRes = JSON.parse(res.data);
        if (serverRes.errCode == 0){
          let imgUrl = app.getServerUrl() + serverRes.data;
          let fileList = that.fileList;
          fileList.push({url: imgUrl, isImage:true});
          that.certification.images.push(serverRes.data);
          that.data = { fileList:  fileList};
          wx.showToast({
            title: '上传成功',
          })
        }else throw new Error("上传失败");
    })
    .catch(res => {
      console.log("fail")
      wx.showToast({
        title: '上传失败',
      })
    })
  },
  onLoad: function (options) {
    that = getApp().proxy(this);
    this.data.taskId = options.id;
    that.certification.taskId = options.id;
    app.initBack(this);
  },
  submit(){
    that.loading = true;
    app.api.submitCertification(that.certification)
    .then((res) => {
      if(res.data.errCode == 0) 
      {
        wx.showToast({
          title: '提交成功',
        })
        setTimeout(() => {
          wx.switchTab({
            url: '/pages/my/my',
          })
        }, 500)
      }
    })
    .catch(error => {
      wx.showToast({
        title: '保存失败',
      })
    })
    .then(()=>{
      that.loading = false;
    })
  }
})