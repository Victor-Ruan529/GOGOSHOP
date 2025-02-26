<template>
  <div class="main-content">
    <div style="display: flex; width: 70%; background-color: white; margin: 30px auto; border-radius: 20px">
      <div style="flex: 1; padding: 0px 20px">
        <div style="font-size: 18px; color: rgb(0,0,0); line-height: 80px; border-bottom: #cccccc 1px solid">搜索"{{name}}"的结果</div>
        <div style="margin: 20px 0px;">
          <el-row>
            <el-col :span="4" v-for="item in goodsData" style="margin-bottom: 20px; margin-right: 20px">
              <img @click="navTo('/front/detail?id=' + item.id)" :src="item.img" alt="" style="height: 230px; width: 100%; border-radius: 10px;border: 1px solid rgb(204,204,204)">
              <div style="margin-top: 10px;font-size: 20px; font-weight: 1000; color: rgb(0,0,0)">{{item.name}}</div>
              <div style="margin-top: 5px;color: rgb(255,80,0);font-size: 20px; font-weight: 1000">￥：{{item.price}} / {{item.unit}}</div>
            </el-col >
          </el-row>
        </div>
      </div>
<!--      <div style="width: 250px; padding: 0 20px; border-left: #cccccc 1px solid">-->
<!--        <div style="font-size: 18px; color: rgb(0,0,0); line-height: 80px; border-bottom: #cccccc 1px solid;">猜你喜欢</div>-->
<!--        <div style="margin: 10px 0;padding: 10px 10px">-->
<!--        <div>-->
<!--          <el-row>-->
<!--            <el-col v-for="item in goodsRandomData" style="margin-bottom: 20px">-->
<!--              <img @click="navTo('/front/detail?id=' + item.id)" :src="item.img" alt="" style="height: 200px; width: 100%; border-radius: 10px;border: 1px solid rgb(204,204,204)">-->
<!--              <div style="margin-top: 10px; text-align: center; font-size: 25px; font-weight: 1000; color: rgb(0,0,0)">{{item.name}}</div>-->
<!--              <div style="margin-top: 5px; text-align: center; color: rgb(255,80,0);font-size: 20px; font-weight: 1000">￥：{{item.price}} / {{item.unit}}</div>-->
<!--            </el-col >-->
<!--          </el-row>-->
<!--        </div>-->
<!--        </div>-->
<!--      </div>-->
    </div>
  </div>
</template>

<script>
export default {
  name: "Search",
  data(){
    let name = this.$route.query.name
    return{
      user:JSON.parse(localStorage.getItem('xm-user') || '{}'),
      name: name,
      goodsRandomData:[],
      goodsData:[]
    }
  },
  mounted(){
    this.loadGoods()
  },
  methods: {
    navTo(url) {
      location.href = url
    },
    loadGoods(){
      this.$request.get('/goods/selectByName?name=' + this.name).then(res => {
        if (res.code === '200') {
          this.goodsData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    loadRandom(){
      this.$request.get('/goods/selectRandomGoodsId?id=' + this.typeId).then(res => {
        if(res.code === '200') {
          this.goodsRandomData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },
  }
}
</script>

<style scoped>

</style>