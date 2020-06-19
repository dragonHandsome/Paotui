Component({
  options: {
    styleIsolation: 'apply-shared'
  },
  // externalClasses: ["padding", "radius", "text-center", "shadow-blur"],
  properties: {
    taskDetail: {
      type: Object,
      value: {}
    },
    animation:{
      type: String,
      value: ""
    },
    otherData:{
      type:Object,
      value:{}
    },
    fromUser:{
      type: Object,
      value:{}
    },
    toUser: {
      type: Object,
      value: {}
    },
    replyList: {
      type:Array,
      value:[]
    },
    setting: {
      type: Object,
      value: {
        steps: [
          {
            text: '步骤一',
            desc: '描述信息'
          }
        ],
        active: 0,
        buttonInfo:{
          type: "primary",// info warning danger
          text: "我要接单"
        }
      }
    }
  },

  /**
   * 组件的初始数据
   */
  data: {
    root:getApp().getRoot()||''
  },

  /**
   * 组件的方法列表
   */
  methods: {
    showImg(e){
      console.log(e)
      let imgs = e.currentTarget.dataset.imgs;
      let index = e.currentTarget.dataset.index;
      wx.previewImage({
        urls: imgs,
        current: imgs[index]
      })
    },
    resolveIssue(e){
      this.triggerEvent('click', e.currentTarget.dataset, null);
    },
    showReply(e){
      this.triggerEvent('reply', e.currentTarget.dataset, null);
    },
    caina(e){
      this.triggerEvent('caina', e.currentTarget.dataset, null);
    }
  }
})
