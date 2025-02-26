
<template>
  <div class="main-content">
    <div style="height: 60px; background-color: rgb(197,102,246)"></div>
    <div style="display: flex">
      <div class="left"></div>
      <div style="width: 66%; margin-bottom: 50px; background-color: white">
        <div style="color: #FE0137FF; margin: 15px 0px 15px 18px; front-size: 20px; font-weight: bold">主题市场</div>
        <div style="display: flex; margin: 0px 25px; height: 545px">

          <div style="flex: 2">
            <div style="display: flex; color: #666666FF; margin: 14px 0px" v-for="item in typeData">
              <img :src="item.img" alt="" style="height: 20px; width: 20px">
            <div style="margin-left: 10px; font-size: 14px"><a href="#" @click="navTo('/front/type?id=' + item.id)">{{item.name}}</a></div>
            </div>
          </div>

          <div style="flex: 5; margin-top: 15px">
            <div>
            <el-carousel height="530px" style="border-radius: 10px">
              <el-carousel-item v-for="item in carouselData">
                <img :src="item.img" alt="" style="width: 100%; height: 530px; border-radius: 10px">
              </el-carousel-item>
            </el-carousel>
            </div>
          </div>
          <div style="flex: 3; background-color: #F3F3F3FF; margin-top: 15px; margin-left: 15px; border-radius: 10px">
            <div style="text-align: center; margin-top: 30px">
              <img @click="navTo('/front/person')" :src="user.avatar" alt="" style="width: 80px; height: 80px; border-radius: 50%">
              <div style="margin-top: 10px">Hi,{{user.name}}</div>
            </div>
            <div style="margin-top: 20px; padding: 0px 15px">
              <img src='@/assets/imgs/right.png' alt="" style="height: 150px; width: 100%;border-radius: 20px">
            </div>
            <div style="margin: 20px 0px 10px 10px; width: 250px; white-space: nowrap; overflow: hidden;text-overflow: ellipsis">
              <i class="el-icon-bell"></i>
              <span style="font-weight: bold">公告</span>
              <span style="color: #666666">：{{ top }}</span>
            </div>
            <div style="display: flex; margin-top: 50px">
            <div style="flex: 1; text-align: center">
              <a href="#" @click="navTo('/front/collect')">
              <img src="@/assets/imgs/收藏.png" alt="" style="height: 25px; width: 25px">
              <div>我的收藏</div>
              </a>
            </div>
<!--            <div style="flex: 1; text-align: center">-->
<!--              <img src="@/assets/imgs/店铺.png" alt="" style="height: 25px; width: 25px">-->
<!--              <div>我的地址</div>-->
<!--            </div>-->
<!--            <div style="flex: 1; text-align: center">-->
<!--              <img src="@/assets/imgs/购物车.png" alt="" style="height: 25px; width: 25px">-->
<!--              <div>我的购物车</div>-->
<!--            </div>-->
            <div style="flex: 1; text-align: center">
              <a href="#" @click="navTo('/front/orders')">
              <img src="@/assets/imgs/订单.png" alt="" style="height: 25px; width: 25px">
              <div>我的订单</div>
              </a>
            </div>
            </div>
          </div>
        </div>
        <div class="list">热卖商品</div>
        <div style="margin: 10px 5px 0px 5px">
          <el-row >
            <el-col :span="5" v-for="item in goodsData">
              <img @click="navTo('/front/detail?id=' + item.id)" :src="item.img" alt="" style="height: 180px; width: 100%; border-radius: 10px;border: 1px solid rgb(204,204,204)">
              <div class="text" style="margin-top: 10px; font-size: 18px; font-weight: 1000; color: rgb(0,0,0)">{{item.name}}</div>
              <div style="margin-top: 5px; color: rgb(255,80,0);font-size: 20px; font-weight: 1000">￥：{{item.price}}/{{item.unit}}</div>
            </el-col >
          </el-row>
        </div>
      </div>
      <div class="right"></div>
    </div>
  </div>
</template>

<script>

  export default {

  data() {
    return {
      carouselData:[],
      goodsData:[],
      user:JSON.parse(localStorage.getItem('xm-user') || '{}' ),
      typeData:[],
      top:null,
      notice:[],
    }
  },
  mounted() {
    this.lodeType()
    this.lodeNotice()
    this.lodeGoods()
    this.loadCarousel()
  },
  // methods：本页面所有的点击事件或者其他函数定义区
  methods: {
    loadCarousel(){
      this.$request.get('/carousel/selectAll').then(res =>{
        if(res.code === '200'){
          this.carouselData = res.data
        }else {
          this.$message.error(res.msg)
        }
      })
    },
    lodeGoods(){
      this.$request.get('/goods/selectTop10').then(res => {
        if(res.code === '200'){
          this.goodsData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    lodeNotice() {
      this.$request.get('/notice/selectAll').then(res => {
        this.notice = res.data
        let i = 0
        if (this.notice && this.notice.length) {
          this.top = this.notice[0].content
          //定时器
          setInterval(() => {
            this.top = this.notice[i].content
            i++
            if (i === this.notice.length) {
              i = 0
            }
          }, 2500)
        }
      })
    },
    navTo(url) {
      location.href = url
    },
    lodeType(){
      this.$request.get('/type/selectAll').then(res =>{
        if(res.code === '200'){
          this.typeData = res.data
        }else {
          this.$message.error(res.msg)
        }
      })
    }
  }
}
</script>

<style scoped>

.left{
  width: 17%;
  background-repeat: no-repeat;
  background-image: url("@/assets/imgs/left-img.png");
}
.right{
  width:17%;
  background-repeat: no-repeat;
  background-image: url("@/assets/imgs/right-img.png");
}
.main-content{
  min-height: 100vh;
  background-size: 100%;
  background-image: url("@/assets/imgs/img.png");
}
.list{
  margin: 40px 0px 0px 15px;
  height: 40px;
  background-color: #5580d4;
  font-size: 20px;
  color: white;
  text-align: center;
  width: 130px;
  height: 40px;
  line-height: 40px;
  font-weight: bold;
  border-radius: 20px;
}
.el-col-5{
  width: 20%;
  max-width: 20%;
  padding: 10px 10px;
}
/*超出文本的字体显示'...'*/
.text{
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
  display: -webkit-box;
  -webkit-line-clamp: 2;/*能显示内容的行数*/
  -webkit-box-orient: vertical;
}
</style>
