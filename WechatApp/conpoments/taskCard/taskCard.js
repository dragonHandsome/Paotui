// compoments/taskCard/taskCard.js
Component({
  /**
   * 组件的属性列表
   */
  properties: {
    task:{
      type: Object,
      value: {}
    }
  },

  /**
   * 组件的初始数据
   */
  data: {
  },

  /**
   * 组件的方法列表
   */
  methods: {
    onTap(e){
      this.triggerEvent('click', e.currentTarget.dataset, null);
    }
  }
})
