// conpoments/back/back.js
const BACK_IMG = "backImg";
const TA_IMG = 'taBackImg';
Component({
  /**
   * 组件的属性列表
   */
  options:{
    styleIsolation:"apply-shared"
  },
  pageLifetimes:{
    show(){
      this.initData();
    }
  },
  attached: function () {
    //this.initData();
  },
  properties: {
    url:{
      type:String,
      value:"https://img.yzcdn.cn/vant/cat.jpeg"
    }
  },

  /**
   * 组件的初始数据
   */
  data: {
    backImg:''
  },

  /**
   * 组件的方法列表
   */
  methods: {
    setImg(img){
      console.log("setImg")
      this.setData({
        backImg: img
      })
      console.log(this.data.backImg)
    },
    initData(){
      console.log("initData")
      let page = getCurrentPages()[getCurrentPages().length - 1];
      const curPath = page.route;
      page.backCom = this;
      const isTa = curPath == "pages/ta/ta";
      let backImg;
      if (isTa) {
        if (page.data.userId) {
          page.getUserDetail(page.data.userId)
            .then(() => {
              this.setData({
                backImg: wx.getStorageSync(TA_IMG)
              })
            })
        }else {
          wx.showToast({
            title: '用户参数为空',
          })
          setTimeout(wx.navigateBack, 500)
        }
      }
      else {
        backImg = wx.getStorageSync(BACK_IMG)
        this.setData({
          backImg
        })
      }
    }
  }
})
