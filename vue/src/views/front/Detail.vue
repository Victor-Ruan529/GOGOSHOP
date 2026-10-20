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
            <div style="font-size: 14px; color: rgb(102,102,102); margin-top: 5px">销量：{{goodsData.count}}　库存：{{goodsData.stock}}</div>
            <div style="font-size: 15px; margin-top: 15px; color: red">疯抢价：<span style="font-size: 20px">{{goodsData.price}} / {{goodsData.unit}}</span> </div>
            <div style="margin-top: 30px;">
              <img src="@/assets/imgs/right.png" alt="" style="border-radius: 15px; width: 70%;height: 130px">
            </div>
            <div style="color: rgb(102,102,102); font-size: 14px;margin-top: 20px">商家：{{goodsData.businessName}}</div>
            <div style="color: rgb(102,102,102); font-size: 14px;margin-top: 20px"><a href="#" @click="navTo('/front/type?id=' + goodsData.typeId)">分类：{{goodsData.typeName}}</a></div>
            <div style="margin-top: 20px; font-size: 14px; color: rgb(102,102,102)">
              数量：<el-input-number v-model="quantity" :min="1" :max="Math.max(1, Math.min(99, goodsData.stock || 1))" size="small"></el-input-number>
            </div>
            <div style="margin-top: 20px">
              <el-button type="warning" :disabled="!goodsData.stock" @click="pay">{{ goodsData.stock ? '立刻购买' : '已售罄' }}</el-button>
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
        <div style="font-size: 16px; margin: 0 70px; color: rgb(102,102,102); white-space: pre-wrap">{{goodsData.description}}</div>
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
      quantity: 1,
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
      // 价格、用户、商家、状态都由后端决定，前端只传商品和数量
      let data = {
        goodsId: this.goodsId,
        quantity: this.quantity
      }
      this.$request.post('/orders/add', data).then(res => {
        if(res.code === '200'){
          this.$message.success('购买成功，订单金额 ￥' + res.data.totalPrice)
          this.loadGoods()
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    collect(){
      let data = {
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