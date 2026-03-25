<template>
  <div class="order-container">
    <el-card>
      <template #header>
        <span>我的订单</span>
      </template>

      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部订单" name="all" />
        <el-tab-pane label="待付款" name="0" />
        <el-tab-pane label="待发货" name="1" />
        <el-tab-pane label="待收货" name="2" />
        <el-tab-pane label="已完成" name="3" />
      </el-tabs>

      <div class="order-list" v-loading="loading">
        <div v-for="order in filteredOrders" :key="order.id" class="order-item">
          <div class="order-header">
            <span class="order-no">订单编号：{{ order.orderNo }}</span>
            <span class="order-time">{{ formatTime(order.createTime) }}</span>
            <el-tag :type="getStatusType(order.status)">{{ getStatusText(order.status) }}</el-tag>
          </div>
          <div class="order-body">
            <div class="order-info">
              <div class="info-item">
                <span class="label">收货人：</span>
                <span>{{ order.receiverName }}</span>
              </div>
              <div class="info-item">
                <span class="label">联系电话：</span>
                <span>{{ order.receiverPhone }}</span>
              </div>
              <div class="info-item">
                <span class="label">收货地址：</span>
                <span>{{ order.receiverAddress }}</span>
              </div>
              <div class="info-item" v-if="order.remark">
                <span class="label">备注：</span>
                <span>{{ order.remark }}</span>
              </div>
            </div>
            <div class="order-amount">
              <div class="total">订单金额：<em>¥{{ order.totalAmount }}</em></div>
            </div>
          </div>
          <div class="order-footer">
            <el-button size="small" @click="viewDetail(order.id)">查看详情</el-button>
            <el-button v-if="order.status === 0" type="primary" size="small" @click="handlePay(order)">立即支付</el-button>
            <el-button v-if="order.status === 0" size="small" @click="handleCancel(order)">取消订单</el-button>
            <el-button v-if="order.status === 2" type="success" size="small" @click="handleConfirm(order)">确认收货</el-button>
          </div>
        </div>
        <el-empty v-if="filteredOrders.length === 0" description="暂无订单" />
      </div>
    </el-card>

    <el-dialog v-model="detailVisible" title="订单详情" width="700px">
      <div v-if="orderDetail" class="order-detail">
        <div class="detail-section">
          <h4>订单信息</h4>
          <div class="detail-row">
            <span class="label">订单编号：</span>
            <span>{{ orderDetail.order.orderNo }}</span>
          </div>
          <div class="detail-row">
            <span class="label">订单状态：</span>
            <el-tag :type="getStatusType(orderDetail.order.status)">{{ getStatusText(orderDetail.order.status) }}</el-tag>
          </div>
          <div class="detail-row">
            <span class="label">创建时间：</span>
            <span>{{ formatTime(orderDetail.order.createTime) }}</span>
          </div>
        </div>
        <div class="detail-section">
          <h4>收货信息</h4>
          <div class="detail-row">
            <span class="label">收货人：</span>
            <span>{{ orderDetail.order.receiverName }}</span>
          </div>
          <div class="detail-row">
            <span class="label">联系电话：</span>
            <span>{{ orderDetail.order.receiverPhone }}</span>
          </div>
          <div class="detail-row">
            <span class="label">收货地址：</span>
            <span>{{ orderDetail.order.receiverAddress }}</span>
          </div>
        </div>
        <div class="detail-section">
          <h4>商品信息</h4>
          <el-table :data="orderDetail.items" border>
            <el-table-column label="商品" width="300">
              <template #default="{ row }">
                <div class="product-cell">
                  <img :src="row.productImage || '/placeholder.jpg'" class="product-img" />
                  <span>{{ row.productName }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="单价" width="100">
              <template #default="{ row }">¥{{ row.price }}</template>
            </el-table-column>
            <el-table-column label="数量" width="80">
              <template #default="{ row }">{{ row.quantity }}</template>
            </el-table-column>
            <el-table-column label="小计" width="100">
              <template #default="{ row }">¥{{ row.subtotal }}</template>
            </el-table-column>
          </el-table>
        </div>
        <div class="detail-total">
          订单总额：<em>¥{{ orderDetail.order.totalAmount }}</em>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserOrders, getOrderDetail, payOrder, cancelOrder, confirmOrder } from '@/api/order'
import { formatTime } from '@/utils/time'

const loading = ref(false)
const orders = ref([])
const activeTab = ref('all')
const detailVisible = ref(false)
const orderDetail = ref(null)

const filteredOrders = computed(() => {
  if (activeTab.value === 'all') {
    return orders.value
  }
  return orders.value.filter(order => order.status === parseInt(activeTab.value))
})

const getStatusText = (status) => {
  const statusMap = {
    0: '待付款',
    1: '待发货',
    2: '待收货',
    3: '已完成',
    4: '已取消'
  }
  return statusMap[status] || '未知'
}

const getStatusType = (status) => {
  const typeMap = {
    0: 'warning',
    1: 'info',
    2: 'primary',
    3: 'success',
    4: 'danger'
  }
  return typeMap[status] || 'info'
}

const loadOrders = async () => {
  loading.value = true
  try {
    const res = await getUserOrders()
    orders.value = res.data || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
}

const viewDetail = async (id) => {
  try {
    const res = await getOrderDetail(id)
    orderDetail.value = res.data
    detailVisible.value = true
  } catch (error) {
    console.error(error)
  }
}

const handlePay = async (order) => {
  try {
    await ElMessageBox.confirm('确定要支付该订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    await payOrder(order.id)
    ElMessage.success('支付成功')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleCancel = async (order) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cancelOrder(order.id)
    ElMessage.success('订单已取消')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleConfirm = async (order) => {
  try {
    await ElMessageBox.confirm('确定已收到货物吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    await confirmOrder(order.id)
    ElMessage.success('确认收货成功')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.order-container {
  padding: 20px;
}

.order-list {
  min-height: 400px;
}

.order-item {
  border: 1px solid #eee;
  margin-bottom: 15px;
  border-radius: 4px;
}

.order-header {
  display: flex;
  align-items: center;
  padding: 10px 15px;
  background: #f5f5f5;
  border-bottom: 1px solid #eee;
}

.order-no {
  font-weight: bold;
  margin-right: 20px;
}

.order-time {
  color: #999;
  margin-right: 20px;
}

.order-body {
  display: flex;
  justify-content: space-between;
  padding: 15px;
}

.order-info {
  flex: 1;
}

.info-item {
  margin-bottom: 8px;
}

.info-item .label {
  color: #999;
  margin-right: 10px;
}

.order-amount {
  text-align: right;
}

.order-amount .total {
  font-size: 14px;
}

.order-amount em {
  font-size: 20px;
  color: #f56c6c;
  font-style: normal;
  font-weight: bold;
}

.order-footer {
  padding: 10px 15px;
  text-align: right;
  border-top: 1px solid #eee;
}

.order-detail {
  padding: 10px;
}

.detail-section {
  margin-bottom: 20px;
}

.detail-section h4 {
  margin-bottom: 10px;
  padding-bottom: 5px;
  border-bottom: 1px solid #eee;
}

.detail-row {
  margin-bottom: 8px;
}

.detail-row .label {
  color: #999;
  margin-right: 10px;
}

.product-cell {
  display: flex;
  align-items: center;
}

.product-img {
  width: 50px;
  height: 50px;
  object-fit: cover;
  margin-right: 10px;
}

.detail-total {
  text-align: right;
  font-size: 16px;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.detail-total em {
  font-size: 24px;
  color: #f56c6c;
  font-style: normal;
  font-weight: bold;
}
</style>
