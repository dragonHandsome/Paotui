Component({
  data: {
    selected:0,
    newsCount:0,
    "custom": true,
    "borderStyle": "white",
    pathes:[
      "/pages/index/index",
      "/pages/attent/attent",
      "/pages/news/news",
      "/pages/my/my"
    ],
    issueHidden: true,
    category:[]
  },
  pageLifetimes:{
    show(){
      
      console.log("pageLifetimes")
      this.getIssueCategory(this);
    }
  },
  attached() {
    console.log("attached")
    this.setData({
      newsCount: getApp().getNoReadCount()
    })
    this.getIssueCategory(this);
  },
  methods: {
    getIssueCategory: getApp().getIssueCategory,
    switchTab(e) {
      let index = e.target.dataset.index;
      console.log(index)
      if (index === undefined || index === null) return; 
      index = +index;
      const url = this.data.pathes[index]
      console.log(url, index)
      wx.switchTab({ url })
      index != null && this.setData({
        selected: index
      })
      this.updateCount();
    },
    updateCount(){
      this.setData({
        newsCount: getApp().getNoReadCount()
      })
    },
    issue(){
      console.log("issue")
      this.setData({
        issueHidden: !this.data.issueHidden
      })
      if (this.data.issueHidden) {
        this.data.toUserId = null;
      }
    },
    loadToUserId(options) {
      const toUserId = this.data.toUserId;
      if (toUserId) options.toUserId = toUserId;
      this.data.toUserId = null;
    },
    toIssue(e){
      const categoryId = e.currentTarget.dataset.id;
      const toUserId = this.data.toUserId;
      const options = {
        id: categoryId
      }
      this.loadToUserId(options);
      wx.navigateTo({
        url: getApp().parseUrl("/pages/issue/issue", options),
      })
      this.setData({
        issueHidden: true
      })
    }
  }
})