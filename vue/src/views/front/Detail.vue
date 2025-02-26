<template>
  <div class="main-content">
    <div style="width: 60%; background-color: white; min-height: 1000px; margin: 20px auto; border-radius: 20px">
      <div style="padding: 15px 20px">
        <el-row :gutter="20">
          <el-col :span="12">
            <img :src="goodsData.img" alt="" style="width: 100%; height: 400px; border-radius: 20px">
          </el-col>
          <el-col :span="12">
            <div style="font-size: 20px; font-weight: 900;overflow: hidden; text-overflow: ellipsis; display: -webkit-box; -webkit-line-clamp: 2;-webkit-box-orient: vertical;" >{{goodsData.name}}</div>
            <div style="font-size: 14px; color: rgb(102,102,102); margin-top: 5px">销量：{{goodsData.count}}</div>
            <div style="font-size: 15px; margin-top: 15px; color: red">疯抢价：<span style="font-size: 20px">{{goodsData.price}} / {{goodsData.unit}}</span> </div>
            <div style="margin-top: 30px;">
              <img src="@/assets/imgs/right.png" alt="" style="border-radius: 15px; width: 70%;height: 130px">
            </div>
            <div style="color: rgb(102,102,102); font-size: 14px;margin-top: 20px">商家：{{goodsData.businessName}}</div>
            <div style="color: rgb(102,102,102); font-size: 14px;margin-top: 20px"><a href="#" @click="navTo('/front/type?id=' + goodsData.typeId)">分类：{{goodsData.typeName}}</a></div>
            <div  style="margin-top: 30px">
              <el-button type="warning" @click="pay">立刻购买</el-button>
              <el-button type="warning" @click="collect">收藏</el-button>
            </div>
          </el-col>
        </el-row>
      </div>
      <div style="margin: 50px 0px 0px 20px">
        <span style="height: 40px; font-size: 20px; text-align: center; width: 130px; font-weight: 600">宝贝详情</span>
        <el-divider></el-divider>
      </div>
      <div>
        <span style="height: 40px; font-size: 20px; text-align: center; width: 130px; margin-left: 70px; color: rgb(102,102,102)">{{goodsData.description}}</span>
      </div>
      </div>
    </div>
</template>

<script>
export default {
  name: "Detail",
  data(){
    let goodsId = this.$route.query.id
    return{
      user:JSON.parse(localStorage.getItem('xm-user') || '{}'),
      goodsId: goodsId,
      goodsData:{},
    }
  },
  mounted(){
    this.loadGoods()
  },
  methods:{
    navTo(url){
      location.href = url
    },
    pay(){
      let data = {
        userId: this.user.id,
        businessId: this.goodsData.businessId,
        goodsId: this.goodsId,
        status: "待发货"
      }
      this.$request.post('/orders/add', data).then(res => {
        if(res.code === '200'){
          this.$message.success('购买成功')
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    collect(){
      let data = {
        userId: this.user.id,
        businessId: this.goodsData.businessId,
        goodsId: this.goodsId
      }
      this.$request.post('/collect/add', data).then(res => {
        if(res.code === '200'){
          this.$message.success('收藏成功')
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    loadGoods() {
      this.$request.get('/goods/selectById?id=' + this.goodsId).then(res => {
        if(res.code === '200'){
          this.goodsData = res.data
        }else{
          this.$message.error(res.msg)
        }
      })
    }
  }
}
</script>

<style scoped>
.main-content {
  width: 100%;
  margin: 0px auto;
}
</style>