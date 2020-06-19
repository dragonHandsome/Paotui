import api from "../../api/api.js"
import { formatTime} from "../../utils/util.js"
const app = getApp();
let that;
const checkbox = [{
  value: 10,
  name: '10元',
  checked: false,
  hot: false,
}, {
  value: 20,
  name: '20元',
  checked: true,
  hot: false,
}, {
  value: 30,
  name: '30元',
  checked: false,
  hot: false,
}, {
  value: 60,
  name: '60元',
  checked: false,
  hot: false,
}, {
  value: 80,
  name: '80元',
  checked: false,
  hot: false,
}, {
  value: 100,
  name: '100元',
  checked: false,
  hot: false,
}];
Page({

  /**
   * 页面的初始数据
   */
  data: {
    checkbox,
    root: app.getServerUrl(),
    userDetail: { phone:""},
    backgroundImage:[],
    account:{credit:0},
    avatar: [],
    currentDate: new Date(),
    formatTime,
    now:new Date().getTime(),
    loading: false,
    dateHidden: true,
    formatter(type, value) {
      if (type === 'year') {
        return `${value}年`;
      } else if (type === 'month') {
        return `${value}月`;
      }
      return `${value}日`;
    }
  },
  showModal(e) {
    this.setData({
      modalName: true
    })
  },
  hideModal(e) {
    this.setData({
      modalName: false
    })
    //如果是确认 则充值
    let check = e.currentTarget.dataset.check;
    if (check){
        let money = this.data.checkbox.reduce((beforeSum, cur) => {
          let value = cur.checked ? +cur.value : 0;
          return beforeSum += value;
        },0);
      this.addMoney(money);
    }
  },
  addMoney(money = 0){
    console.log(`充值${money}元`);
    //money 单位分
    app.api.addMoneyToNative(money * 100)
    .then(res => {
      if(res.data.errCode == 0){
        wx.showToast({
          title: `充值￥${ money }成功!`,
        })
        that.nativeAccount = res.data.data;
      }else throw new Error();
    })
    .catch(error => wx.showToast({
      title: '充值失败!',
    }))
  },
  ChooseCheckbox(e) {
    let items = this.data.checkbox;
    let values = e.currentTarget.dataset.value;
    for (let i = 0, lenI = items.length; i < lenI; ++i) {
      if (items[i].value == values) {
        items[i].checked = !items[i].checked;
        break
      }
    }
    this.setData({
      checkbox: items
    })
  },
  setBirthday(){
    that.dateHidden = false;
  },
  cancel() {
    that.dateHidden = true;
  },
  onShow(){
    this.getAccound();
  },
  getAccound(){
    app.api.getNativeAccount(this)
  },
  bindblur(e){
    console.log(e);
    let userDetail = this.data.userDetail;
    let prop = e.currentTarget.dataset.prop;
    console.log(prop, userDetail[prop], ["userDetail." + prop])
    if (that.validate(e, e.detail.value)) {
      userDetail[prop] = e.detail.value;
      console.log(this.data.userDetail);
    }else {
        this.setData({
          ["userDetail." + prop]: userDetail[prop] || ""
        })
    }
  },
  validate(e, value){
    if (value == null) return true;
    let prop = e.currentTarget.dataset.prop;
    if (prop == "phone"){
      //先简单处理
      if (!(/^\d+$/.test(value))) {
        return false;
      }
    }
    return true;
  },
  dataConfirm(e){
    let birthday = formatTime(new Date(e.detail)).split(" ")[0];
    this.setData({
      "userDetail.birthday": birthday
    })
    that.dateHidden = true;
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
          let fileList = [{url: imgUrl, isImage:true}];
          that.data = { [prop]:  fileList};
          _this.setData({
            ["userDetail." + prop]: serverRes.data
          })
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
    that.getUserDetail();
    app.initBack(this);
  },
  submit(){
    that.loading = true;
    //api.updateUserDetail(that.data.userDetail)
    api.updateUserDetail(that.data.userDetail)
    .then((res) => {
      getApp().dealResponse(res, res =>{
        wx.showToast({
          title: '更新成功'
        })
        app.globalData.state.userDetail = false;
        //暂时这样解决
        if (this.data.backgroundImage.length == 1)
        app.urlTobase64(this.data.root + that.data.userDetail.backgroundImage);
        if (this.data.avatar.length == 1)
          app.urlTobase64(this.data.root + that.data.userDetail.avatar, "avatar");
        setTimeout(wx.navigateBack,500)
        
      })
    })
    .catch(error => {
      wx.showToast({
        title: '保存失败',
      })
    })
    .then(()=>{
      that.loading = false;
    })
  },
  deleteImg(e) {
    let prop = e.currentTarget.dataset.prop;
    this.setData({
      [prop] : [],
      ["userDetail." + prop]: []
    })
  },
  getUserDetail() {
    app.api.getUserDetail()
      .then(res => {
        that.data = { userDetail: res.userDetail};
        let currentDate = res.userDetail.birthday;
        currentDate = currentDate ? new Date(currentDate) : new Date();
        this.setData({
          currentDate: currentDate.getTime()
        })
        //
      })
      .catch(error => {
        wx.showToast({
          title: '获取用户详情失败',
        })
      })
  }
})