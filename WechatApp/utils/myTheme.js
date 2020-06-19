class Theme {
  points = [];
  capacity = 70;
  canvas;
  width;
  height;
  ctx;
  bound = 50;
  //是否开始动画
  start = true;
  rate = 1;
  radius = 2;
  constructor(canvas) {
    this.canvas = canvas;
    this.ctx = canvas.getContext('2d');
    this.init();
  }
  init() {
    const { canvas, ctx, initPoints } = this;
    //const dpr = wx.getSystemInfoSync().pixelRatio;
    this.width = canvas.width = canvas._width;
    this.height = canvas.height = canvas._height;
    //ctx.scale(dpr, dpr);
    this.initPoints();
    this.drawPoints();
    this.drawLines();
    return this;
  }
  initBackground() {
    const { ctx, height, width } = this;
    ctx.beginPath();
    const grd = ctx.createLinearGradient(0, 0, 0, height)
    //#a5673f,#1cbbb4
    grd.addColorStop(0, '#a5673f')
    grd.addColorStop(1, '#1cbbb4')
    ctx.fillStyle = grd;
    ctx.fillRect(0, 0, width, height)
    ctx.fill();
  }
  initPoints() {
    const { points, capacity, getPoint } = this;
    let size = capacity;
    while (size--) points.push(this.getPoint());
  }
  animate() {
    const { canvas, ctx, width, height } = this;
    this.start = true;
    (function draw() {
      if (this.start) {
        ctx.clearRect(0, 0, width, height);
        //this.initBackground();
        this.drawPoints();
        this.drawLines();
        canvas.requestAnimationFrame(() => draw.call(this));
      } else return;
    }.bind(this))();
    return this;
  }
  stop() {
    this.start = false;
    return this;
  }
  getPoint() {
    const x = Math.ceil(Math.random() * this.width), // 粒子的x坐标
      y = Math.ceil(Math.random() * this.height), // 粒子的y坐标
      r = (Math.random() * this.radius).toFixed(4), // 粒子的半径
      rateX = +(Math.random() * 2 - 1).toFixed(4), // 粒子在x方向运动的速率
      rateY = +(Math.random() * 2 - 1).toFixed(4); // 粒子在y方向运动的速率
    return { x, y, r, rateX, rateY };
  }
  drawPoints() {
    const { ctx, points, width, height, rate } = this;
    points.forEach((item, i) => {
      ctx.beginPath();
      ctx.arc(item.x, item.y, item.r, 0, Math.PI * 2, false);
      getApp().ctx = ctx
      ctx.fillStyle = '#ffffff';
      ctx.fill();
      // 移动
      if (item.x > 0 && item.x < width && item.y > 0 && item.y < height) {
        item.x += item.rateX * rate;
        item.y += item.rateY * rate;
      } else {
        // 如果粒子运动超出了边界，将这个粒子去除，同时重新生成一个新点。
        points.splice(i, 1);
        points.push(this.getPoint());
      }
    });
  }
  distance(x1, y1, x2, y2) {
    return ((x1 - x2) ** 2 + (y1 - y2) ** 2) ** 0.5
  }
  drawLines() {
    const { points, ctx, width, height, bound} = this;
    const distance = this.distance(0, 0, width, height);
    //对圆心坐标进行两两判断
    points.forEach(point => {
      points.forEach(reference => {
        let disPoint = this.distance(point.x, point.y, reference.x, reference.y);
        if (disPoint < bound) {
          ctx.beginPath();
          ctx.moveTo(point.x, point.y);
          ctx.lineTo(reference.x, reference.y);
          ctx.strokeStyle = '#fff';
          //距离大 线就细
          ctx.lineWidth = 0.4 - disPoint / bound;
          ctx.stroke();
        }
      })
    })
  }
}
class ThemeBlack {
  columns = [];
  fonts= "肥凯是傻吊普里西斯什么鬼ABCDEFGHIZKLMNOPQRSTUVWXYZ";
  canvas;
  width;
  height;
  ctx;
  //是否开始动画
  start = true;
  fontSize = 14;
  constructor(canvas) {
    this.canvas = canvas;
    this.ctx = canvas.getContext('2d');
    this.init();
  }
  init() {
    const { canvas, ctx } = this;
    this.width = canvas.width = canvas._width;
    this.height = canvas.height = canvas._height
    this.initColumns();
    return this;
  }
  initBackground(){
    const {ctx, height,width} = this;
    ctx.beginPath();
    //#a5673f,#1cbbb4
    ctx.fillStyle = "#000";
    ctx.fillRect(0, 0, width, height)
    ctx.fill();
  }
  initColumns(){
    const {width, fontSize} = this;
    let size = (width / fontSize) >> 0;
    while(size -- ){
      //决定生成多少列 value为每列的字有几个
      this.columns.push(this.getFontItem());
    }
    this.draw();
  }
  random(min, max){
    return min + (Math.random() * (max - min)) >> 0; 
  }
  getFontItem(){
    const { fonts, random} = this;
    const max = fonts.length;
    let len = random(1, max);
    const fontArr = [];
    while (len--) fontArr.push(fonts[random(0, max)]);
    return {
      fonts: fontArr,
      y: -this.random(0, fontArr.length),
      rate: (0.3 + Math.random())/2,
      fillStyle : "#00ff00" + this.random(3, 16).toString(16) + this.random(3, 16).toString(16)
    };
  }
  draw() {
    const { columns, width, height, font, ctx, fontSize } = this;
    this.columns.forEach((item, xi) => {
      ctx.beginPath();
      ctx.fillStyle = item.fillStyle;
      item.fonts.forEach((font, i) => {
        ctx.fillText(font, xi * fontSize, (i + item.y) * fontSize);
      })
      item.y += item.rate;
      //
      ctx.fill();
      if (item.y * fontSize > height) {
        this.columns[xi] = this.getFontItem();
      }
    })
  }
  animate() {
    const { canvas, ctx, width, height } = this;
    this.start = true;
    (function draw() {
      if (this.start) {
        ctx.clearRect(0, 0, width, height);
        this.draw();
        canvas.requestAnimationFrame(() => draw.call(this));
      } else return;
    }.bind(this))();
    return this;
  }
  stop() {
    this.start = false;
    return this;
  }
}
class ThemeFactory{
  canvas;
  constructor(canvas){
    this.canvas = canvas;
  }
  newInstance(){
    return Math.random() < 0 ? new ThemeBlack(this.canvas) : new Theme(this.canvas);
  }
}
module.exports = {
  Theme,
  ThemeBlack,
  ThemeFactory
}