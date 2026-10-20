<template>
  <div class="main-content">
    <div style="width: 70%; background-color: white; margin: 30px auto; border-radius: 20px">
      <div style="padding-bottom: 10px">
        <div style="font-size: 18px; color: #000000FF; line-height: 80px; border-bottom: #cccccc 1px solid; display: flex">
          <div style="margin-left: 20px">全部订单（{{ total }}件）</div>
          <div style="margin: auto"></div>
          <div style="text-align: right; margin-right: 100px">
            <el-button type="primary" @click="navTo('/front/home')">返回主页</el-button>
          </div>
        </div>
        <div style="margin: 20px 0; padding: 0 50px">
          <div class="table">
            <el-table :data="ordersData" strip>
              <el-table-column label="商品图片" width="100px">
                <template v-slot="scope">
                  <el-image style="width: 80px; height: 60px; border-radius: 3px" v-if="scope.row.goodsImg"
                            :src="scope.row.goodsImg" :preview-src-list="[scope.row.goodsImg]"></el-image>
                </template>
              </el-table-column>
              <el-table-column prop="orderId" label="订单编号" width="180px"></el-table-column>
              <el-table-column prop="goodsName" label="商品名称" width="100px"></el-table-column>
              <el-table-column prop="businessName" label="店铺名称"></el-table-column>
              <el-table-column prop="goodsPrice" label="单价"></el-table-column>
              <el-table-column label="数量">
                <template v-slot="scope">{{ scope.row.quantity }} {{ scope.row.goodsUnit }}</template>
              </el-table-column>
              <el-table-column prop="totalPrice" label="总价"></el-table-column>
              <el-table-column prop="createTime" label="下单时间"></el-table-column>
              <el-table-column prop="status" label="订单状态" align="center"></el-table-column>
              <el-table-column label="操作" align="center" width="180">
                <template v-slot="scope">
                  <el-button size="mini" type="primary" v-if="scope.row.status === '已发货'" plain @click="updateStatus(scope.row, '已完成')">确认收货</el-button>
                  <el-button size="mini" type="warning" v-if="scope.row.status === '待发货'" plain @click="updateStatus(scope.row, '已取消')">取消订单</el-button>
                  <el-button size="mini" type="danger" v-if="scope.row.status === '已完成' || scope.row.status === '已取消'" plain @click="del(scope.row.id)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>

            <div class="pagination" style="margin-top: 20px">
              <el-pagination
                  background
                  @current-change="handleCurrentChange"
                  :current-page="pageNum"
                  :page-sizes="[5, 10, 20]"
                  :page-size="pageSize"
                  layout="total, prev, pager, next"
                  :total="total">
              </el-pagination>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

export default {

  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      ordersData: [],
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
    }
  },
  mounted() {
    this.loadOrders(1)

  },
  // methods：本页面所有的点击事件或者其他函数定义区
  methods: {

    loadOrders(pageNum) {
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/orders/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
        }
      }).then(res => {
        if (res.code === '200') {
          this.ordersData = res.data?.list
          this.total = res.data?.total
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    navTo(url) {
      location.href = url
    },
    del(id) {
      this.$request.delete('/orders/delete/' + id).then(res => {
        if (res.code === '200') {
          this.$message.success('删除成功')
          this.loadOrders(1)
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleCurrentChange(pageNum) {
      this.loadOrders(pageNum)
    },
    updateStatus(row, status) {
      this.$request.put('/orders/update', { id: row.id, status: status }).then(res => {
        if (res.code === '200') {
          this.$message.success(status === '已取消' ? '订单已取消' : '订单完成')
          this.loadOrders()
        } else {
          this.$message.error(res.msg)
        }
      })
    }
  }
}
</script>
